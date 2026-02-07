package com.nutrino.audiocutter.domain.Repository

import android.net.Uri
import com.nutrino.audiocutter.data.DataClass.Song
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow


interface GetAllSongRepository {
    suspend fun getAllSongs(): Flow<ResultState<List<Song>>>
    suspend fun getAllSongsForMerge(): Flow<ResultState<List<Song>>>
    suspend fun mergeSongs(uriList: List<Uri>, filename: String): Flow<ResultState<String>>
}