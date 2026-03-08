package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.domain.Repository.ConvertAudioFormatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@UnstableApi
class ConvertAudioFormatRepoImpl @Inject constructor(
    private val context: Context
) : ConvertAudioFormatRepository {

    override suspend fun convertAudioFormat(
        context: Context,
        uri: String,
        outputMimeType: String,
        outputExtension: String,
        filename: String
    ): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val resultChannel = Channel<ResultState<String>>()

        try {
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .build()

            val editedMediaItem = EditedMediaItem.Builder(mediaItem).build()

            val outputFileName = "$filename.$outputExtension"
            val outputFile = File(context.cacheDir, outputFileName)

            // Map codec MIME type to container MIME type for MediaStore saving
            val containerMimeType = when (outputMimeType) {
                "audio/mp4a-latm" -> "audio/mp4"   // AAC codec → MP4 container
                else -> "audio/mp4"                  // Fallback to MP4
            }

            val transformer = Transformer.Builder(context)
                .setAudioMimeType(outputMimeType)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        val savedUri = saveAudioFile(
                            outputFile,
                            "${filename}_${System.currentTimeMillis()}.$outputExtension",
                            containerMimeType
                        )
                        resultChannel.trySend(ResultState.Success(savedUri.toString()))
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        resultChannel.trySend(
                            ResultState.Error(
                                exportException.message ?: "Audio format conversion failed"
                            )
                        )
                    }
                })
                .build()

            transformer.start(editedMediaItem, outputFile.absolutePath)

            emit(resultChannel.receive())

        } catch (e: Exception) {
            Log.e("ConvertAudioFormat", "Error during conversion: ${e.message}", e)
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        }
    }

    private fun saveAudioFile(sourceFile: File, displayName: String, mimeType: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDownloads(sourceFile, displayName, mimeType)
        } else {
            saveToLegacyDownloads(sourceFile, displayName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(sourceFile: File, displayName: String, mimeType: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/AudioCutter")
        }

        val resolver = context.contentResolver
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(audioCollection, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                sourceFile.delete()
                Log.d("ConvertAudioFormat", "Saved to MediaStore: $uri")
            } catch (e: Exception) {
                Log.e("ConvertAudioFormat", "Error writing to MediaStore", e)
            }
        }
        return uri
    }

    private fun saveToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val audioCutterDir = File(downloadsDir, "AudioCutter")

        if (!audioCutterDir.exists()) {
            audioCutterDir.mkdirs()
        }

        val destFile = File(audioCutterDir, displayName)

        return try {
            sourceFile.copyTo(destFile, overwrite = true)
            sourceFile.delete()
            Log.d("ConvertAudioFormat", "Saved to legacy path: ${destFile.absolutePath}")
            destFile.toUri()
        } catch (e: Exception) {
            Log.e("ConvertAudioFormat", "Legacy save failed", e)
            null
        }
    }
}

