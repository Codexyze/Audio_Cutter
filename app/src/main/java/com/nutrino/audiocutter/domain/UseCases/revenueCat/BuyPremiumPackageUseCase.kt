package com.nutrino.audiocutter.domain.UseCases.revenueCat

import android.app.Activity
import com.nutrino.audiocutter.domain.Repository.RevenueCatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.revenuecat.purchases.Package
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuyPremiumPackageUseCase @Inject constructor(
    private val revenueCatRepository: RevenueCatRepository
) {
    suspend operator fun invoke(
        activity: Activity,
        selectedPackage: Package
    ): Flow<ResultState<Boolean>> {
        return revenueCatRepository.buyPremiumPackage(
            activity = activity,
            selectedPackage = selectedPackage
        )
    }
}

