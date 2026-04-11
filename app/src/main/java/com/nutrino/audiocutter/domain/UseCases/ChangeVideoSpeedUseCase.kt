package com.nutrino.audiocutter.domain.UseCases

import android.net.Uri
import com.nutrino.audiocutter.domain.Repository.VideoSpeedRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangeVideoSpeedUseCase @Inject constructor(
    private val repository: VideoSpeedRepository
) {

    suspend operator fun invoke(
        uri: Uri,
        speed: Float,
        filename: String
    ): Flow<ResultState<String>> {
        return repository.ChangeVideoSpeed(
            uri = uri,
            speed = speed,
            filename = filename
        )
    }
}

