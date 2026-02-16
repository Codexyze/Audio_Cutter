package com.nutrino.audiocutter.domain.Repository

import android.content.Context
import com.nutrino.audiocutter.data.DataClass.CropSegment
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow


interface MultiCropAudioRepository {

    suspend fun multiCropAudio(context: Context, uri: String, segments: List<CropSegment>, filename: String): Flow<ResultState<String>>

}