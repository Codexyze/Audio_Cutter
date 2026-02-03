package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentUris
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
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.data.DataClass.Video
import com.nutrino.audiocutter.domain.Repository.VideoRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

@UnstableApi
class VideoRepImpl (private val context: Context) : VideoRepository{
    override suspend fun getAllVideos(): Flow<ResultState<List<Video>>> = flow{
        val videoFiles = mutableListOf<Video>()
        emit(ResultState.Loading)
        val projection = arrayOf(
            MediaStore.Video.Media._ID,         // Unique ID for each video file.
            MediaStore.Video.Media.DATA,       // Full file path of the video.
            MediaStore.Video.Media.DURATION,   // Duration of the video in milliseconds.
            MediaStore.Video.Media.TITLE,      // Title of the video (can be user-defined).
            MediaStore.Video.Media.DISPLAY_NAME // File name of the video (e.g., "video.mp4").
        )
        val contentResolver = context.contentResolver
        // Define the URI to query video files from external storage.
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val cursor = contentResolver.query(uri, projection, null, null, null)
        try {
           if (cursor!=null){
               while (cursor.moveToNext()){
                   // Retrieve data from the current row using the column index.
                   val id = cursor.getString(0)           // Column 0: Unique ID of the video.
                   val path = cursor.getString(1)         // Column 1: Full file path.
                   val duration = cursor.getString(2)     // Column 2: Video duration.
                   val title = cursor.getString(3)        // Column 3: Title of the video.
                   val fileName = cursor.getString(4)     // Column 4: File name of the video.
                   val thumbnail = ContentUris.withAppendedId(
                       MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                       id.toLong() // Convert ID to Long since ContentUris requires it.
                   ).toString() // Convert the URI to a String.

                   val folderName = path.substringBeforeLast('/') // Get the folder path.
                       .substringAfterLast('/')                  // Get only the folder name.

                   val videoFile = Video(
                       id = id,                  // Unique ID of the video.
                       path = path,              // Full file path.
                       duration = duration,      // Video duration.
                       thumbnail = thumbnail,    // URI of the video's thumbnail.
                       fileName = fileName,      // File name of the video.
                       title = title,            // Title of the video.
                       folderName = folderName   // Folder name containing the video.
                   )

                   videoFiles.add(videoFile)


               }
               cursor.close()
               emit(ResultState.Success(videoFiles))


           }else{

               emit(ResultState.Error("No Video Found.."))
           }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Error loading videos"))
        } finally {
            cursor?.close()
        }

    }

    override suspend fun TrimVideo(
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
                .setVideoMimeType(MimeTypes.VIDEO_H264)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        val savedUri = saveVideoFile(outputFile, "${filename}_${System.currentTimeMillis()}")
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

    override suspend fun ExtractAudioFromVideo(
        uri: Uri,
        startTime: Long,
        endTime: Long,
        filename: String
    ): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val resultChannel = Channel<ResultState<String>>()

        try {
            val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(startTime)
                .setEndPositionMs(endTime)
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setClippingConfiguration(clippingConfiguration)
                .build()

            // Remove video to extract only audio
            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setRemoveVideo(true)
                .build()

            val outputFile = File(context.cacheDir, "$filename.m4a")

            val transformer = Transformer.Builder(context)
                .setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        val savedUri = saveAudioFile(outputFile, "${filename}_${System.currentTimeMillis()}.m4a")
                        resultChannel.trySend(ResultState.Success(savedUri.toString()))
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        resultChannel.trySend(ResultState.Error(exportException.message ?: "Audio extraction failed"))
                    }
                })
                .build()

            transformer.start(editedMediaItem, outputFile.absolutePath)

            emit(resultChannel.receive())

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Something went wrong"))
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
            Log.e("VideoTrim", "Failed to create MediaStore entry")
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
            Log.e("VideoTrim", "Error writing to MediaStore", e)
        }

        return itemUri
    }

    // For Android < Q
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
            Log.d("VideoTrim", "Saved to legacy path: ${targetFile.absolutePath}")
            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            Log.e("VideoTrim", "Legacy save failed", e)
            null
        }
    }

    private fun saveAudioFile(sourceFile: File, displayName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveAudioToDownloads(sourceFile, displayName)
        } else {
            saveAudioToLegacyDownloads(sourceFile, displayName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveAudioToDownloads(sourceFile: File, displayName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, contentValues)

        if (itemUri == null) {
            Log.e("AudioExtract", "Failed to create MediaStore entry")
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
            Log.e("AudioExtract", "Error writing to MediaStore", e)
        }

        return itemUri
    }

    private fun saveAudioToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val targetFile = File(downloadsDir, displayName)

        return try {
            sourceFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("AudioExtract", "Saved to legacy path: ${targetFile.absolutePath}")
            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            Log.e("AudioExtract", "Legacy save failed", e)
            null
        }
    }

}

