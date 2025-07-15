package com.nutrino.audiocutter.domain.UseCases

import com.nutrino.audiocutter.data.DataClass.Song
import com.nutrino.audiocutter.domain.Repository.GetAllSongRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class GetAllSongUseCase @Inject constructor(private val getAllSongRepository: GetAllSongRepository) {
    suspend operator fun invoke(): Flow<ResultState<List<Song>>>{
        return getAllSongRepository.getAllSongs()
    }
}