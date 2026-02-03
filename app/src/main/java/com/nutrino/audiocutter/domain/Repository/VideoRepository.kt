package com.nutrino.audiocutter.domain.Repository

import android.net.Uri
import com.nutrino.audiocutter.data.DataClass.Video
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    suspend fun getAllVideos(): Flow<ResultState<List<Video>>>
    suspend fun TrimVideo(uri: Uri, startTime: Long, endTime: Long, filename: String): Flow<ResultState<String>>
    suspend fun ExtractAudioFromVideo(uri: Uri, startTime: Long, endTime: Long, filename: String): Flow<ResultState<String>>
}

