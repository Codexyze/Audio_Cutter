package com.nutrino.audiocutter.domain.UseCases

import android.net.Uri
import com.nutrino.audiocutter.domain.Repository.AudioVolumeBoosterRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoostAudioVolumeUseCase @Inject constructor(
    private val repository: AudioVolumeBoosterRepository
) {
    suspend operator fun invoke(
        uri: Uri,
        volumeFactor: Float,
        filename: String
    ): Flow<ResultState<String>> {
        return repository.boostAudioVolume(uri, volumeFactor, filename)
    }
}
