package com.nutrino.audiocutter.domain.UseCases

import android.app.Activity
import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShowAdUseCase @Inject constructor(
    private val repository: AdsRepository
) {
    suspend operator fun invoke(activity: Activity): Flow<ResultState<Boolean>> {
        return repository.showInterstitialAd(activity)
    }
}
