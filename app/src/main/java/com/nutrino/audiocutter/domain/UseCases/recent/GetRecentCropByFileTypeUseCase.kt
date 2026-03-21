package com.nutrino.audiocutter.domain.UseCases.recent

import com.nutrino.audiocutter.data.room.entity.CropSegmentTable
import com.nutrino.audiocutter.domain.Repository.RecentRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentCropByFileTypeUseCase @Inject constructor(
    private val repository: RecentRepository
) {
    suspend operator fun invoke(fileType: String): Flow<ResultState<List<CropSegmentTable>>> {
        return repository.getRecentCropByFileType(fileType = fileType)
    }
}

