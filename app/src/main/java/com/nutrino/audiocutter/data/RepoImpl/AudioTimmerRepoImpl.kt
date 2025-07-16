package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.domain.Repository.AudioTrimmerRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File


@UnstableApi
class AudioTimmerRepoImpl : AudioTrimmerRepository {

    override suspend fun TrimAudio(
        context: Context,
        uri: Uri,
        startTime: Long,
        endTime: Long,
        filename: String
    ): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val resultChannel = Channel<ResultState<String>>() // ✅ Create channel for result

        try {
            val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(startTime)
                .setEndPositionMs(endTime)
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setClippingConfiguration(clippingConfiguration)
                .build()

            val editedMediaItem = EditedMediaItem.Builder(mediaItem).build()

            val outputFile = File(context.cacheDir, filename)

            val transformer = Transformer.Builder(context)
                .setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        val savedUri = saveAudioFile(context, outputFile, "${filename}_${System.currentTimeMillis()}")
                        resultChannel.trySend(ResultState.Success(savedUri.toString())) // ✅ Send success
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        resultChannel.trySend(ResultState.Error(exportException.message ?: "Unknown error")) // ✅ Send error
                    }
                })
                .build()

            transformer.start(editedMediaItem, outputFile.absolutePath)

            emit(resultChannel.receive()) // ✅ Await result and emit

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        }
    }


    private fun saveAudioFile(context: Context, sourceFile: File, displayName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDownloads(context, sourceFile, displayName)
        } else {
            saveToLegacyDownloads(context, sourceFile, displayName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(context: Context, sourceFile: File, displayName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName.m4a")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, contentValues)

        if (itemUri == null) {
            Log.e("AudioTrim", "Failed to create MediaStore entry")
            return null
        }

        try {
            resolver.openOutputStream(itemUri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(itemUri, contentValues, null, null)

        } catch (e: Exception) {
            Log.e("AudioTrim", "Error writing to MediaStore", e)
        }

        return itemUri
    }

    // For Android < Q
    private fun saveToLegacyDownloads(context: Context, sourceFile: File, displayName: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val targetFile = File(downloadsDir, "$displayName.m4a")

        return try {
            sourceFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("AudioTrim", "Saved to legacy path: ${targetFile.absolutePath}")
            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            Log.e("AudioTrim", "Legacy save failed", e)
            null
        }
    }

}
