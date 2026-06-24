package com.nutrino.audiocutter.domain.Repository

import android.net.Uri
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface AudioVolumeBoosterRepository {
    suspend fun boostAudioVolume(
        uri: Uri,
        volumeFactor: Float,
        filename: String
    ): Flow<ResultState<String>>
}
