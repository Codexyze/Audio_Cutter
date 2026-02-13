package com.nutrino.audiocutter.domain.UseCases

import com.google.android.gms.ads.AdView
import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadBannerAdUseCase @Inject constructor(
    private val adsRepository: AdsRepository
) {
    suspend operator fun invoke(adView: AdView): Flow<ResultState<Boolean>> {
        return adsRepository.loadBannerAd(adView)
    }
}
