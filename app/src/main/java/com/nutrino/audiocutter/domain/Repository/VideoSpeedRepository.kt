package com.nutrino.audiocutter.domain.Repository

import android.net.Uri
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface VideoSpeedRepository {
    suspend fun ChangeVideoSpeed(
        uri: Uri,
        speed: Float,
        filename: String
    ): Flow<ResultState<String>>
}

