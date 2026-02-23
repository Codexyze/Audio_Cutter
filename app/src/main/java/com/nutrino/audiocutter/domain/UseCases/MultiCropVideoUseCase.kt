package com.nutrino.audiocutter.domain.UseCases

import android.content.Context
import com.nutrino.audiocutter.data.DataClass.CropSegment
import com.nutrino.audiocutter.domain.Repository.MultiCropVideoRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MultiCropVideoUseCase @Inject constructor(
    private val repository: MultiCropVideoRepository
) {

    suspend operator fun invoke(
        context: Context,
        uri: String,
        segments: List<CropSegment>,
        filename: String
    ): Flow<ResultState<String>> {
        return repository.multiCropVideo(
            context = context,
            uri = uri,
            segments = segments,
            filename = filename
        )
    }
}

