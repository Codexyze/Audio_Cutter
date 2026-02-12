package com.nutrino.audiocutter.domain.UseCases

import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadAdUseCase @Inject constructor(
    private val repository: AdsRepository
) {
    suspend operator fun invoke(): Flow<ResultState<Boolean>> {
        return repository.loadInterstitialAd()
    }
}
