package com.nutrino.audiocutter.domain

import android.content.Context
import android.net.Uri
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface AudioTrimmerRepository {
    suspend fun TrimAudio(context: Context, uri: Uri, startTime: Long, endTime: Long, filename: String): Flow<ResultState<String>>
}