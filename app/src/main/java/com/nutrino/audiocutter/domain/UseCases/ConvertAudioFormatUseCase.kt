package com.nutrino.audiocutter.domain.UseCases

import android.content.Context
import com.nutrino.audiocutter.domain.Repository.ConvertAudioFormatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConvertAudioFormatUseCase @Inject constructor(
    private val repository: ConvertAudioFormatRepository
) {
    suspend operator fun invoke(
        context: Context,
        uri: String,
        outputMimeType: String,
        outputExtension: String,
        filename: String
    ): Flow<ResultState<String>> {
        return repository.convertAudioFormat(
            context = context,
            uri = uri,
            outputMimeType = outputMimeType,
            outputExtension = outputExtension,
            filename = filename
        )
    }
}

