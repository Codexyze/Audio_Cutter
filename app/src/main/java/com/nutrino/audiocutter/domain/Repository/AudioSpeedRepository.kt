package com.nutrino.audiocutter.domain.Repository

import android.net.Uri
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface AudioSpeedRepository {
    suspend fun ChangeAudioSpeed(
        uri: Uri,
        speed: Float,
        filename: String
    ): Flow<ResultState<String>>
}
