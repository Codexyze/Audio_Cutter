package com.nutrino.audiocutter.domain.Repository

import android.net.Uri
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface MuteVideoRepository {
    suspend fun muteVideo(uri: Uri, filename: String): Flow<ResultState<String>>
}
