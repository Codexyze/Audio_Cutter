package com.nutrino.audiocutter.domain.UseCases.recent

import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.domain.Repository.RecentRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentEntriesByInputNameAscUseCase @Inject constructor(
    private val repository: RecentRepository
) {
    suspend operator fun invoke(): Flow<ResultState<List<RecentTable>>> {
        return repository.getRecentEntriesByInputNameAsc()
    }
}

