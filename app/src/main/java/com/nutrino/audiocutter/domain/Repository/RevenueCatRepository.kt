package com.nutrino.audiocutter.domain.Repository

import android.app.Activity
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.revenuecat.purchases.Package
import kotlinx.coroutines.flow.Flow

interface RevenueCatRepository {
    suspend fun getPackages(): Flow<ResultState<List<Package>>>

    suspend fun isUserPro(): Flow<ResultState<Boolean>>

    suspend fun buyPremiumPackage(activity: Activity,
                                  selectedPackage: Package): Flow<ResultState<Boolean>>

    suspend fun getAppUserId(): Flow<ResultState<String>>

}