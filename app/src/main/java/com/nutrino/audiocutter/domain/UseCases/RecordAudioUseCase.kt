package com.nutrino.audiocutter.domain.UseCases

import android.content.Context
import com.nutrino.audiocutter.domain.Repository.RecordAudioRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecordAudioUseCase @Inject constructor(
    private val repository: RecordAudioRepository
) {
    suspend fun startRecording(context: Context, filename: String): Flow<ResultState<String>> {
        return repository.startRecording(context = context, filename = filename)
    }

    fun pauseRecording() {
        repository.pauseRecording()
    }

    fun resumeRecording() {
        repository.resumeRecording()
    }

    suspend fun stopRecording(context: Context): Flow<ResultState<String>> {
        return repository.stopRecording(context = context)
    }

    fun isRecording(): Boolean = repository.isRecording()

    fun isPaused(): Boolean = repository.isPaused()
}

