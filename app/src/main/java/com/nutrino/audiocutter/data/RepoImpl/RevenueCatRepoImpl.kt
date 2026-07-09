package com.nutrino.audiocutter.data.RepoImpl

import android.app.Activity
import com.nutrino.audiocutter.Constants.RevenueCat
import com.nutrino.audiocutter.core.crashanalytics.CrashAnalyticsHelper
import com.nutrino.audiocutter.domain.Repository.RevenueCatRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RevenueCatRepoImpl @Inject constructor(
    private val crashAnalyticsHelper: CrashAnalyticsHelper
): RevenueCatRepository {
    override suspend fun getPackages(): Flow<ResultState<List<Package>>> = flow {
        emit(ResultState.Loading)
        try {
            val offerings = Purchases.sharedInstance.awaitOfferings()
            val packages = offerings.current?.availablePackages.orEmpty()
            if (packages.isEmpty()) {
                crashAnalyticsHelper.errorLog("RevenueCat", "No default offering found")
                emit(ResultState.Error("No default offering"))
            } else {
                crashAnalyticsHelper.successLog("RevenueCat", "Fetched ${packages.size} packages")
                emit(ResultState.Success(packages))
            }
        } catch (e: PurchasesException) {
            crashAnalyticsHelper.logNonFatalException(e, "PurchasesException in getPackages")
            crashAnalyticsHelper.errorLog("RevenueCat", e.message ?: "Failed to fetch offerings")
            emit(ResultState.Error(e.message ?: "Failed to fetch offerings"))
        } catch (e: Exception) {
            crashAnalyticsHelper.logNonFatalException(e, "Exception in getPackages")
            crashAnalyticsHelper.errorLog("RevenueCat", e.message ?: "Unexpected error")
            emit(ResultState.Error(e.message ?: "Unexpected error"))
        }
    }

    override suspend fun isUserPro(): Flow<ResultState<Boolean>> =flow{
        emit(ResultState.Loading)
        try {
            val  customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
            val isUserPro = customerInfo.entitlements[RevenueCat.AUDIO_CUTTER_PRO]?.isActive ==true
            crashAnalyticsHelper.setCustomKey("is_pro_user", isUserPro.toString())
            if(isUserPro){
                emit(ResultState.Success(data = true))
            } else{
                emit(ResultState.Success(data = false))
            }

        }catch (e: PurchasesException){
            crashAnalyticsHelper.logNonFatalException(e, "PurchasesException in isUserPro")
            emit(ResultState.Error(e.error.toString()))
        }catch (e: Exception){
            crashAnalyticsHelper.logNonFatalException(e, "Exception in isUserPro")
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
                crashAnalyticsHelper.successLog("RevenueCat", "Purchase successful for package: ${selectedPackage.identifier}")
                emit(ResultState.Success(true))
            } else {
                crashAnalyticsHelper.errorLog("RevenueCat", "Purchase completed but Pro entitlement is not active.")
                emit(ResultState.Error("Purchase completed but Pro entitlement is not active yet."))
            }
        } catch (e: PurchasesException) {
            crashAnalyticsHelper.logNonFatalException(e, "PurchasesException in buyPremiumPackage")
            crashAnalyticsHelper.errorLog("RevenueCat", e.message ?: "Purchase failed")
            emit(ResultState.Error(e.message ?: "Purchase failed"))
        } catch (e: Exception) {
            crashAnalyticsHelper.logNonFatalException(e, "Exception in buyPremiumPackage")
            crashAnalyticsHelper.errorLog("RevenueCat", e.message ?: "Unexpected purchase error")
            emit(ResultState.Error(e.message ?: "Unexpected purchase error"))
        }
    }

    override suspend fun getAppUserId(): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val appUserId = Purchases.sharedInstance.appUserID
            crashAnalyticsHelper.setUserId(appUserId)
            emit(ResultState.Success(appUserId))
        } catch (e: Exception) {
            crashAnalyticsHelper.logNonFatalException(e, "Exception in getAppUserId")
            emit(ResultState.Error(e.message ?: "Failed to get User ID"))
        }
    }


}
