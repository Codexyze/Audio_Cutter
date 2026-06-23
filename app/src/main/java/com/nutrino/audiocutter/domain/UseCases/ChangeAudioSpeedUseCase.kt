package com.nutrino.audiocutter.domain.UseCases

import android.net.Uri
import com.nutrino.audiocutter.domain.Repository.AudioSpeedRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangeAudioSpeedUseCase @Inject constructor(
    private val repository: AudioSpeedRepository
) {

    suspend operator fun invoke(
        uri: Uri,
        speed: Float,
        filename: String
    ): Flow<ResultState<String>> {
        return repository.ChangeAudioSpeed(
            uri = uri,
            speed = speed,
            filename = filename
        )
    }
}
