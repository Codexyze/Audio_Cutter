package com.nutrino.audiocutter.domain.UseCases.revenueCat

import com.nutrino.audiocutter.domain.Repository.RevenueCatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppUserIdUseCase @Inject constructor(private val revenueCatRepository: RevenueCatRepository) {
    suspend operator fun invoke(): Flow<ResultState<String>> {
        return revenueCatRepository.getAppUserId()
    }
}
