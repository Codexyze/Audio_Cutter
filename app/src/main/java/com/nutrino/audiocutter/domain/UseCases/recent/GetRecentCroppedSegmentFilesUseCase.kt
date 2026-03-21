package com.nutrino.audiocutter.domain.UseCases.recent

import com.nutrino.audiocutter.domain.Repository.RecentRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentCroppedSegmentFilesUseCase @Inject constructor(
    private val repository: RecentRepository
) {
    suspend operator fun invoke(): Flow<ResultState<List<String>>> {
        return repository.getRecentCroppedSegmentFiles()
    }
}

