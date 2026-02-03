package com.nutrino.audiocutter.domain.UseCases

import android.net.Uri
import com.nutrino.audiocutter.domain.Repository.VideoRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExtractAudioFromVideoUseCase @Inject constructor(private val repository: VideoRepository) {

    suspend operator fun invoke(uri: Uri, startTime: Long, endTime: Long, filename: String):
            Flow<ResultState<String>>
    {
        return repository.ExtractAudioFromVideo(uri = uri,
            startTime = startTime, endTime = endTime, filename = filename
        )

    }
}
