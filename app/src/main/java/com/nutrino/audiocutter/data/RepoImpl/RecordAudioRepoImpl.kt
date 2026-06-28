package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import com.nutrino.audiocutter.domain.Repository.RecordAudioRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@UnstableApi
class RecordAudioRepoImpl @Inject constructor(
    private val context: Context,
    private val analyticsRepository: AnalyticsRepository
) : RecordAudioRepository {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var currentFilename: String = ""
    private var recording = false
    private var paused = false

    override suspend fun startRecording(
        context: Context,
        filename: String
    ): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            currentFilename = filename
            val outputFileName = "${filename}_${System.currentTimeMillis()}.m4a"
            outputFile = File(context.cacheDir, outputFileName)

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }

            recording = true
            paused = false
            emit(ResultState.Success("Recording started"))

        } catch (e: Exception) {
            Log.e("RecordAudio", "Error starting recording: ${e.message}", e)
            recording = false
            paused = false
            emit(ResultState.Error(e.message ?: "Failed to start recording"))
        }
    }

    override fun pauseRecording() {
        try {
            if (recording && !paused) {
                mediaRecorder?.pause()
                paused = true
            }
        } catch (e: Exception) {
            Log.e("RecordAudio", "Error pausing recording: ${e.message}", e)
        }
    }

    override fun resumeRecording() {
        try {
            if (recording && paused) {
                mediaRecorder?.resume()
                paused = false
            }
        } catch (e: Exception) {
            Log.e("RecordAudio", "Error resuming recording: ${e.message}", e)
        }
    }

    override suspend fun stopRecording(context: Context): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            recording = false
            paused = false

            val source = outputFile
            if (source != null && source.exists()) {
                val savedUri = saveAudioFile(
                    source,
                    "${currentFilename}_${System.currentTimeMillis()}.m4a",
                    "audio/mp4"
                )
                if (savedUri != null) {
                    analyticsRepository.logEventsNonSuspend("record_audio_success", null)
                    emit(ResultState.Success(savedUri.toString()))
                } else {
                    analyticsRepository.logEventsNonSuspend("record_audio_error", android.os.Bundle().apply {
                        putString("error", "Failed to save recording")
                    })
                    emit(ResultState.Error("Failed to save recording"))
                }
            } else {
                analyticsRepository.logEventsNonSuspend("record_audio_error", android.os.Bundle().apply {
                    putString("error", "Recording file not found")
                })
                emit(ResultState.Error("Recording file not found"))
            }

        } catch (e: Exception) {
            Log.e("RecordAudio", "Error stopping recording: ${e.message}", e)
            analyticsRepository.logEventsNonSuspend("record_audio_error", android.os.Bundle().apply {
                putString("error", e.message)
            })
            mediaRecorder?.release()
            mediaRecorder = null
            recording = false
            paused = false
            emit(ResultState.Error(e.message ?: "Failed to stop recording"))
        }
    }

    override fun isRecording(): Boolean = recording

    override fun isPaused(): Boolean = paused

    private fun saveAudioFile(sourceFile: File, displayName: String, mimeType: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStore(sourceFile, displayName, mimeType)
        } else {
            saveToLegacyStorage(sourceFile, displayName)
        }
    }

    @android.annotation.SuppressLint("NewApi")
    private fun saveToMediaStore(sourceFile: File, displayName: String, mimeType: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_MUSIC + "/AudioCutter"
            )
        }

        val resolver = context.contentResolver
        val audioCollection =
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(audioCollection, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                sourceFile.delete()
                Log.d("RecordAudio", "Saved to MediaStore: $uri")
            } catch (e: Exception) {
                Log.e("RecordAudio", "Error writing to MediaStore", e)
            }
        }
        return uri
    }

    private fun saveToLegacyStorage(sourceFile: File, displayName: String): Uri? {
        val musicDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val audioCutterDir = File(musicDir, "AudioCutter")

        if (!audioCutterDir.exists()) {
            audioCutterDir.mkdirs()
        }

        val destFile = File(audioCutterDir, displayName)

        return try {
            sourceFile.copyTo(destFile, overwrite = true)
            sourceFile.delete()
            Log.d("RecordAudio", "Saved to legacy path: ${destFile.absolutePath}")
            destFile.toUri()
        } catch (e: Exception) {
            Log.e("RecordAudio", "Legacy save failed", e)
            null
        }
    }
}

