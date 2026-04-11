package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.SpeedChangeEffect
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.domain.Repository.VideoSpeedRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@UnstableApi
class VideoSpeedRepoImpl @Inject constructor(
    private val context: Context
) : VideoSpeedRepository {

    override suspend fun ChangeVideoSpeed(
        uri: Uri,
        speed: Float,
        filename: String
    ): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        if (filename.isBlank()) {
            emit(ResultState.Error("File name cannot be empty"))
            return@flow
        }

        if (!speed.isFinite() || speed < MIN_SPEED || speed > MAX_SPEED) {
            emit(ResultState.Error("Speed must be between $MIN_SPEED and $MAX_SPEED"))
            return@flow
        }

        val resultChannel = Channel<ResultState<String>>()

        try {
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .build()

            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setEffects(
                    Effects(
                        emptyList(),
                        listOf(SpeedChangeEffect(speed))
                    )
                )
                .build()

            val outputFile = File(context.cacheDir, "$filename.mp4")

            val transformer = Transformer.Builder(context)
                .setVideoMimeType(MimeTypes.VIDEO_H264)
                .setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        val savedUri = saveVideoFile(
                            sourceFile = outputFile,
                            displayName = "${filename}_${System.currentTimeMillis()}"
                        )

                        if (savedUri != null) {
                            resultChannel.trySend(ResultState.Success(savedUri.toString()))
                        } else {
                            resultChannel.trySend(ResultState.Error("Failed to save output video"))
                        }
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        resultChannel.trySend(
                            ResultState.Error(exportException.message ?: "Failed to change video speed")
                        )
                    }
                })
                .build()

            transformer.start(editedMediaItem, outputFile.absolutePath)
            emit(resultChannel.receive())
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        } finally {
            resultChannel.close()
        }
    }

    private fun saveVideoFile(sourceFile: File, displayName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDownloads(sourceFile, displayName)
        } else {
            saveToLegacyDownloads(sourceFile, displayName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(sourceFile: File, displayName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName.mp4")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, contentValues)

        if (itemUri == null) {
            Log.e(TAG, "Failed to create MediaStore entry")
            return null
        }

        return try {
            resolver.openOutputStream(itemUri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(itemUri, contentValues, null, null)
            itemUri
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to MediaStore", e)
            null
        }
    }

    private fun saveToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val targetFile = File(downloadsDir, "$displayName.mp4")

        return try {
            sourceFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "Legacy save failed", e)
            null
        }
    }

    companion object {
        private const val TAG = "VideoSpeedRepoImpl"
        private const val MIN_SPEED = 0.25f
        private const val MAX_SPEED = 3.0f
    }
}

