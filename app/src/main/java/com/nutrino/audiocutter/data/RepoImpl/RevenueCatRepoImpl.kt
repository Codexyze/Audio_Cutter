package com.nutrino.audiocutter.data.RepoImpl

import com.nutrino.audiocutter.domain.Repository.RevenueCatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.getOfferingsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine

class RevenueCatRepoImpl: RevenueCatRepository {
    override suspend fun getPackages(): Flow<ResultState<List<Package>>> = flow {
        emit(ResultState.Loading)
        try {
            val offerings = Purchases.sharedInstance.awaitOfferings()
            val packages = offerings.current?.availablePackages.orEmpty()
            if (packages.isEmpty()) {
                emit(ResultState.Error("No default offering"))
            } else {
                emit(ResultState.Success(packages))
            }
        } catch (e: PurchasesException) {
            emit(ResultState.Error(e.message ?: "Failed to fetch offerings"))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unexpected error"))
        }
    }

}