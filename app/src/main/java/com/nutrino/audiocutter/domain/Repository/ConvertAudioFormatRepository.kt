package com.nutrino.audiocutter.domain.Repository

import android.content.Context
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface ConvertAudioFormatRepository {
    suspend fun convertAudioFormat(
        context: Context,
        uri: String,
        outputMimeType: String,
        outputExtension: String,
        filename: String
    ): Flow<ResultState<String>>
}

