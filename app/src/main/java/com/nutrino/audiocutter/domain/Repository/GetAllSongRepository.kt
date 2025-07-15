package com.nutrino.audiocutter.domain.Repository

import com.nutrino.audiocutter.data.DataClass.Song
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow


interface GetAllSongRepository {
    suspend fun getAllSongs(): Flow<ResultState<List<Song>>>
}