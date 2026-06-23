package com.nutrino.audiocutter.domain.UseCases

import android.net.Uri
import com.nutrino.audiocutter.domain.Repository.MuteVideoRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MuteVideoUseCase @Inject constructor(
    private val repository: MuteVideoRepository
) {
    suspend operator fun invoke(uri: Uri, filename: String): Flow<ResultState<String>> {
        return repository.muteVideo(uri = uri, filename = filename)
    }
}
