package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.core.crashanalytics.CrashAnalyticsHelper
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import com.nutrino.audiocutter.domain.Repository.AudioVolumeBoosterRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

@UnstableApi
class AudioVolumeBoosterRepoImpl @Inject constructor(
    private val context: Context,
    private val analyticsRepository: AnalyticsRepository,
    private val crashAnalyticsHelper: CrashAnalyticsHelper
) : AudioVolumeBoosterRepository {

    override suspend fun boostAudioVolume(
        uri: Uri,
        volumeFactor: Float,
        filename: String
    ): Flow<ResultState<String>> = flow {
        Log.d(TAG, "boostAudioVolume() called -> uri: $uri, volumeFactor: $volumeFactor, filename: '$filename'")
        emit(ResultState.Loading)
        Log.d(TAG, "Emitted ResultState.Loading")

        if (filename.isBlank()) {
            Log.e(TAG, "Filename validation failed: filename is blank")
            crashAnalyticsHelper.errorLog("AudioVolumeBooster", "Filename is empty")
            emit(ResultState.Error("File name cannot be empty"))
            return@flow
        }

        val resultChannel = Channel<ResultState<String>>()

        try {
            Log.d(TAG, "Building MediaItem from URI: $uri")
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .build()

            // Volume adjustment using ChannelMixingAudioProcessor
            // Support all common channel counts (1 = Mono, 2 = Stereo, 6 = 5.1 Surround, 8 = 7.1, etc.)
            Log.d(TAG, "Setting up ChannelMixingAudioProcessor with volumeFactor: $volumeFactor")
            val channelMixingProcessor = ChannelMixingAudioProcessor()

            // Register diagonal scaling matrix for channel counts 1 through 8
            for (channels in 1..8) {
                val coefficients = FloatArray(channels * channels)
                for (i in 0 until channels) {
                    coefficients[i * channels + i] = volumeFactor
                }
                val matrix = ChannelMixingMatrix(channels, channels, coefficients)
                channelMixingProcessor.putChannelMixingMatrix(matrix)
            }

            Log.d(TAG, "Building EditedMediaItem with audio effects")
            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setEffects(Effects(listOf(channelMixingProcessor), emptyList()))
                .build()

            val outputFile = File(context.cacheDir, "$filename.m4a")
            Log.d(TAG, "Output cache file path: ${outputFile.absolutePath}")

            Log.d(TAG, "Configuring Transformer instance")
            val transformer = Transformer.Builder(context)
                .setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        Log.d(TAG, "Transformer.onCompleted() -> Export finished successfully. Cache file exists: ${outputFile.exists()}, size: ${outputFile.length()} bytes")

                        val displayName = "${filename}_${System.currentTimeMillis()}"
                        Log.d(TAG, "Saving audio file to downloads with displayName: $displayName")
                        val savedUri = saveAudioFile(
                            sourceFile = outputFile,
                            displayName = displayName
                        )

                        analyticsRepository.logEventsNonSuspend("boost_volume_success", null)
                        if (savedUri != null) {
                            Log.d(TAG, "Audio file saved successfully -> Uri: $savedUri")
                            crashAnalyticsHelper.successLog("AudioVolumeBooster", "Successfully boosted volume for $filename")
                            resultChannel.trySend(ResultState.Success(savedUri.toString()))
                        } else {
                            Log.e(TAG, "Failed to save boosted audio file to downloads (savedUri is null)")
                            crashAnalyticsHelper.errorLog("AudioVolumeBooster", "Failed to save boosted audio file")
                            resultChannel.trySend(ResultState.Error("Failed to save output audio"))
                        }
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        Log.e(TAG, "Transformer.onError() -> Export failed with exception: ${exportException.message}", exportException)
                        analyticsRepository.logEventsNonSuspend("boost_volume_error", android.os.Bundle().apply {
                            putString("error", exportException.message)
                        })
                        crashAnalyticsHelper.logNonFatalException(exportException, "Error boosting audio volume for $filename")
                        crashAnalyticsHelper.errorLog("AudioVolumeBooster", exportException.message ?: "Failed to boost audio volume")
                        resultChannel.trySend(
                            ResultState.Error(exportException.message ?: "Failed to boost audio volume")
                        )
                    }
                })
                .build()

            Log.d(TAG, "Starting Transformer export...")
            transformer.start(editedMediaItem, outputFile.absolutePath)

            Log.d(TAG, "Awaiting result from resultChannel...")
            val result = resultChannel.receive()
            Log.d(TAG, "Received result from channel: $result")
            emit(result)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in boostAudioVolume flow for '$filename': ${e.message}", e)
            crashAnalyticsHelper.logNonFatalException(e, "Exception in boostAudioVolume for $filename")
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        } finally {
            Log.d(TAG, "Closing resultChannel")
            resultChannel.close()
        }
    }

    private fun saveAudioFile(sourceFile: File, displayName: String): Uri? {
        Log.d(TAG, "saveAudioFile() -> sourceFile: ${sourceFile.absolutePath}, exists: ${sourceFile.exists()}, size: ${sourceFile.length()} bytes, displayName: '$displayName'")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Android SDK >= Q (${Build.VERSION.SDK_INT}), saving via MediaStore Downloads")
            saveToDownloads(sourceFile, displayName)
        } else {
            Log.d(TAG, "Android SDK < Q (${Build.VERSION.SDK_INT}), saving via Legacy Public Downloads")
            saveToLegacyDownloads(sourceFile, displayName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(sourceFile: File, displayName: String): Uri? {
        Log.d(TAG, "saveToDownloads() started")
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName.m4a")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        Log.d(TAG, "Inserting entry into MediaStore collection: $collection")
        val itemUri = resolver.insert(collection, contentValues)

        if (itemUri == null) {
            Log.e(TAG, "saveToDownloads() -> MediaStore resolver.insert returned null")
            return null
        }
        Log.d(TAG, "MediaStore entry created -> itemUri: $itemUri")

        return try {
            Log.d(TAG, "Copying source bytes to MediaStore output stream...")
            val bytesCopied = resolver.openOutputStream(itemUri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d(TAG, "Bytes copied to MediaStore: $bytesCopied bytes")

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            val updatedRows = resolver.update(itemUri, contentValues, null, null)
            Log.d(TAG, "Updated IS_PENDING to 0 -> updatedRows: $updatedRows")
            itemUri
        } catch (e: Exception) {
            Log.e(TAG, "saveToDownloads() failed with exception: ${e.message}", e)
            null
        }
    }

    private fun saveToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        Log.d(TAG, "saveToLegacyDownloads() started")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            val created = downloadsDir.mkdirs()
            Log.d(TAG, "Created downloads directory (${downloadsDir.absolutePath}): $created")
        }

        val targetFile = File(downloadsDir, "$displayName.m4a")
        Log.d(TAG, "Target legacy file: ${targetFile.absolutePath}")

        return try {
            val bytesCopied = sourceFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d(TAG, "Legacy file saved successfully -> bytesCopied: $bytesCopied, targetFile exists: ${targetFile.exists()}")
            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "saveToLegacyDownloads() failed with exception: ${e.message}", e)
            null
        }
    }

    companion object {
        private const val TAG = "AudioVolumeRepoImpl"
    }
}
