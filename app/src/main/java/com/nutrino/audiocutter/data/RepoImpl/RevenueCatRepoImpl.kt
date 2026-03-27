package com.nutrino.audiocutter.data.RepoImpl

import android.app.Activity
import android.util.Log
import com.nutrino.audiocutter.Constants.RevenueCat
import com.nutrino.audiocutter.domain.Repository.RevenueCatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.text.get

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

    override suspend fun isUserPro(): Flow<ResultState<Boolean>> =flow{
        emit(ResultState.Loading)
        try {
            val  customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
            val isUserPro = customerInfo.entitlements[RevenueCat.AUDIO_CUTTER_PRO]?.isActive ==true
            if(isUserPro){
                emit(ResultState.Success(data = true))
            } else{
                emit(ResultState.Success(data = false))
            }

        }catch (e: PurchasesException){
            emit(ResultState.Error(e.error.toString()))
        }catch (e: Exception){
            emit(ResultState.Error(e.message.toString()))
        }
    }

    override suspend fun buyPremiumPackage(
        activity: Activity,
        selectedPackage: Package
    ): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        try {
            // RevenueCat purchase
            val purchaseResult = Purchases.sharedInstance.awaitPurchase(
                PurchaseParams.Builder(activity, selectedPackage).build()
            )

            // Security check: unlock only if entitlement is active
            val isPro = purchaseResult.customerInfo
                .entitlements[RevenueCat.AUDIO_CUTTER_PRO]
                ?.isActive == true

            if (isPro) {
                emit(ResultState.Success(true))
            } else {
                emit(ResultState.Error("Purchase completed but Pro entitlement is not active yet."))
            }
        } catch (e: PurchasesException) {
            // Optional: check e.code for purchase cancelled and show friendly message
            emit(ResultState.Error(e.message ?: "Purchase failed"))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unexpected purchase error"))
        }
    }


}