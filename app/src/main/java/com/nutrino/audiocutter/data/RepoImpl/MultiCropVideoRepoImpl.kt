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
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.data.DataClass.CropSegment
import com.nutrino.audiocutter.domain.Repository.MultiCropVideoRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@UnstableApi
class MultiCropVideoRepoImpl @Inject constructor(
    private val context: Context
) : MultiCropVideoRepository {

    override suspend fun multiCropVideo(
        context: Context,
        uri: String,
        segments: List<CropSegment>,
        filename: String
    ): Flow<ResultState<String>> = flow {
        // Emit loading state
        emit(ResultState.Loading)

        // Validate segments list is not empty
        if (segments.isEmpty()) {
            emit(ResultState.Error("No segments selected"))
            return@flow
        }

        // Create a result channel for async callback from Transformer
        val resultChannel = Channel<ResultState<String>>()

        try {
            //  Create EditedMediaItem for each segment with clipping configuration
            val editedItems = segments.mapNotNull { segment ->
                // Validate that both start and end times are present
                if (segment.start == null || segment.end == null) {
                    Log.e("MultiCropVideo", "Invalid segment: start or end is null")
                    return@mapNotNull null
                }

                // Create clipping configuration for this segment
                val clip = MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(segment.start)
                    .setEndPositionMs(segment.end)
                    .build()

                // Create MediaItem with the same URI but different clipping config
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setClippingConfiguration(clip)
                    .build()

                // Create EditedMediaItem from the MediaItem
                EditedMediaItem.Builder(mediaItem).build()
            }

            //  Validate that we have valid edited items
            if (editedItems.isEmpty()) {
                emit(ResultState.Error("No valid segments to process"))
                return@flow
            }

            //  Create sequence of edited media items (this will merge them sequentially)
            val sequence = EditedMediaItemSequence.Builder(editedItems).build()

            // Create composition from the sequence
            val composition = Composition.Builder(listOf(sequence)).build()

            //  Create output file in cache directory
            val outputFile = File(context.cacheDir, "$filename.mp4")

            //  Build Transformer with video configuration and listeners
            val transformer = Transformer.Builder(context)
                .setVideoMimeType(MimeTypes.VIDEO_H264) // Output format: H264 video
                .setRemoveAudio(false) // Keep audio track
                .setRemoveVideo(false) // Keep video track
                .addListener(object : Transformer.Listener {
                    //  On successful completion
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        Log.d("MultiCropVideo", "Processing completed successfully")
                        // Save the file to Downloads with legacy support
                        val savedUri = saveVideoFile(
                            outputFile,
                            "${filename}_${System.currentTimeMillis()}.mp4"
                        )
                        if (savedUri != null) {
                            resultChannel.trySend(ResultState.Success(savedUri.toString()))
                        } else {
                            resultChannel.trySend(ResultState.Error("Failed to save output file"))
                        }
                    }

                    // ❌ On error
                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        // Log detailed error information
                        Log.e("MultiCropVideo", "Transformation error: ${exportException.message}", exportException)
                        Log.e("MultiCropVideo", "Error code: ${exportException.errorCode}")

                        val errorMessage = when {
                            exportException.message?.contains("codec", ignoreCase = true) == true ->
                                "Video codec not supported. Try with a different video format (MP4 with H264 works best)"
                            exportException.message?.contains("format", ignoreCase = true) == true ->
                                "Video format not supported. Try converting the video to MP4 first"
                            exportException.message?.contains("resolution", ignoreCase = true) == true ->
                                "Video resolution too high. Try with a lower resolution video"
                            else -> exportException.message ?: "Multi-crop video failed. The video format may not be supported"
                        }

                        // Send error result with exception message
                        resultChannel.trySend(ResultState.Error(errorMessage))
                    }
                })
                .build()

            //  Start the transformation process with composition
            transformer.start(composition, outputFile.absolutePath)

            //Wait for result from channel and emit
            emit(resultChannel.receive())

        } catch (e: Exception) {
            // Handle any unexpected exceptions
            Log.e("MultiCropVideo", "Error during multi-crop: ${e.message}", e)
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        }
    }

    //  Save video file with version-specific logic
    private fun saveVideoFile(sourceFile: File, displayName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above - use MediaStore API
            saveToDownloads(sourceFile, displayName)
        } else {
            // Android 9 and below - use legacy file system access
            saveToLegacyDownloads(sourceFile, displayName)
        }
    }

    // Android 10+ (Q and above) - Save using MediaStore API
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(sourceFile: File, displayName: String): Uri? {
        // Prepare content values for MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/AudioCutter")
        }

        val resolver = context.contentResolver
        // Get the video collection URI for external storage
        val videoCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        // Insert a new entry in MediaStore
        val uri = resolver.insert(videoCollection, contentValues)

        uri?.let {
            try {
                // Open output stream and copy the file content
                resolver.openOutputStream(it)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                // Delete the cached file after successful copy
                sourceFile.delete()
                Log.d("MultiCropVideo", "Saved to MediaStore: $uri")
            } catch (e: Exception) {
                Log.e("MultiCropVideo", "Error writing to MediaStore", e)
            }
        }
        return uri
    }

    //  Android 9 and below - Save using legacy file system
    private fun saveToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        // Get the Movies directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        // Create AudioCutter subdirectory
        val audioCutterDir = File(downloadsDir, "AudioCutter")

        // Create directory if it doesn't exist
        if (!audioCutterDir.exists()) {
            audioCutterDir.mkdirs()
        }

        // Create destination file
        val destFile = File(audioCutterDir, displayName)

        return try {
            // Copy source file to destination
            sourceFile.copyTo(destFile, overwrite = true)
            // Delete the cached file
            sourceFile.delete()
            Log.d("MultiCropVideo", "Saved to legacy path: ${destFile.absolutePath}")
            // Return URI from file path
            destFile.toUri()
        } catch (e: Exception) {
            Log.e("MultiCropVideo", "Legacy save failed", e)
            null
        }
    }
}

