package com.nutrino.audiocutter.domain.Repository

import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.revenuecat.purchases.Package
import kotlinx.coroutines.flow.Flow

interface RevenueCatRepository {
    suspend fun getPackages(): Flow<ResultState<List<Package>>>

    suspend fun isUserPro(): Flow<ResultState<Boolean>>
}