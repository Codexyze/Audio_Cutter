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
import com.nutrino.audiocutter.domain.Repository.MultiCropAudioRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@UnstableApi
class MultiCropAudioRepoImpl @Inject constructor(
    private val context: Context
) : MultiCropAudioRepository {

    override suspend fun multiCropAudio(
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
                    Log.e("MultiCropAudio", "Invalid segment: start or end is null")
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
            val outputFile = File(context.cacheDir, "$filename.m4a")

            //  Build Transformer with audio configuration and listeners
            val transformer = Transformer.Builder(context)
                .setAudioMimeType(MimeTypes.AUDIO_AAC) // Output format: AAC audio
                .addListener(object : Transformer.Listener {
                    //  On successful completion
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        // Save the file to Downloads with legacy support
                        val savedUri = saveAudioFile(
                            outputFile,
                            "${filename}_${System.currentTimeMillis()}.m4a"
                        )
                        // Send success result with saved URI
                        resultChannel.trySend(ResultState.Success(savedUri.toString()))
                    }

                    // ❌ On error
                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        // Send error result with exception message
                        resultChannel.trySend(
                            ResultState.Error(
                                exportException.message ?: "Multi-crop audio failed"
                            )
                        )
                    }
                })
                .build()

            //  Start the transformation process with composition
            transformer.start(composition, outputFile.absolutePath)

            //Wait for result from channel and emit
            emit(resultChannel.receive())

        } catch (e: Exception) {
            // Handle any unexpected exceptions
            Log.e("MultiCropAudio", "Error during multi-crop: ${e.message}", e)
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        }
    }

    //  Save audio file with version-specific logic
    private fun saveAudioFile(sourceFile: File, displayName: String): Uri? {
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
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/AudioCutter")
        }

        val resolver = context.contentResolver
        // Get the audio collection URI for external storage
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        // Insert a new entry in MediaStore
        val uri = resolver.insert(audioCollection, contentValues)

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
                Log.d("MultiCropAudio", "Saved to MediaStore: $uri")
            } catch (e: Exception) {
                Log.e("MultiCropAudio", "Error writing to MediaStore", e)
            }
        }
        return uri
    }

    //  Android 9 and below - Save using legacy file system
    private fun saveToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        // Get the Music directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
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
            Log.d("MultiCropAudio", "Saved to legacy path: ${destFile.absolutePath}")
            // Return URI from file path
            destFile.toUri()
        } catch (e: Exception) {
            Log.e("MultiCropAudio", "Legacy save failed", e)
            null
        }
    }
}