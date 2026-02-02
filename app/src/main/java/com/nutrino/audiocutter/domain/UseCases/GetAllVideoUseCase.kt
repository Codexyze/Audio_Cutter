package com.nutrino.audiocutter.domain.UseCases

import com.nutrino.audiocutter.data.DataClass.Video
import com.nutrino.audiocutter.domain.Repository.VideoRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllVideoUseCase @Inject constructor(private val repository: VideoRepository){
    suspend operator  fun invoke(): Flow<ResultState<List<Video>>>{
        return repository.getAllVideos()
    }
}