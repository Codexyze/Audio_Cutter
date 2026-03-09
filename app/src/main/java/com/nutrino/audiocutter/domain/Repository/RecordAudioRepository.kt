package com.nutrino.audiocutter.domain.Repository

import android.content.Context
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface RecordAudioRepository {
    suspend fun startRecording(context: Context, filename: String): Flow<ResultState<String>>
    fun pauseRecording()
    fun resumeRecording()
    suspend fun stopRecording(context: Context): Flow<ResultState<String>>
    fun isRecording(): Boolean
    fun isPaused(): Boolean
}

