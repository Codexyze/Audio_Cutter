package com.nutrino.audiocutter.data.RepoImpl

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nutrino.audiocutter.BuildConfig
import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsRepositoryImpl @Inject constructor(
    private val context: Context
) : AdsRepository {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private val TAG = "AdsRepository"

    override suspend fun loadInterstitialAd(): Flow<ResultState<Boolean>> = callbackFlow {
        if (isLoading) {
            Log.d(TAG, "Ad is already loading")
            trySend(ResultState.Error("Ad is already loading"))
            close()
            return@callbackFlow
        }

        if (interstitialAd != null) {
            Log.d(TAG, "Ad already loaded")
            trySend(ResultState.Success(true))
            close()
            return@callbackFlow
        }

        trySend(ResultState.Loading)
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Starting to load interstitial ad with ID: ${BuildConfig.INTERSTITIAL_AD_ID}")

        InterstitialAd.load(
            context,
            BuildConfig.INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                    isLoading = false
                    trySend(ResultState.Success(true))
                    close()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Failed to load interstitial ad: ${error.message}")
                    Log.e(TAG, "Error code: ${error.code}, domain: ${error.domain}")
                    interstitialAd = null
                    isLoading = false
                    trySend(ResultState.Error("Failed to load ad: ${error.message}"))
                    close()
                }
            }
        )

        awaitClose {
            isLoading = false
        }
    }

    override suspend fun showInterstitialAd(activity: Activity): Flow<ResultState<Boolean>> = callbackFlow {
        if (interstitialAd == null) {
            Log.w(TAG, "Interstitial ad not ready")
            trySend(ResultState.Error("Ad not ready"))
            close()
            return@callbackFlow
        }

        trySend(ResultState.Loading)
        Log.d(TAG, "Showing interstitial ad")

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed by user")
                interstitialAd = null
                trySend(ResultState.Success(true))
                close()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Ad failed to show: ${error.message}")
                interstitialAd = null
                trySend(ResultState.Error("Failed to show ad: ${error.message}"))
                close()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content")
                interstitialAd = null
            }

            override fun onAdClicked() {
                Log.d(TAG, "Ad was clicked")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Ad recorded an impression")
            }
        }

        interstitialAd?.show(activity)

        awaitClose {
            // Cleanup if needed
        }
    }

    override fun isAdReady(): Boolean {
        val ready = interstitialAd != null
        Log.d(TAG, "Ad ready status: $ready")
        return ready
    }

    override fun destroy() {
        Log.d(TAG, "Destroying ad reference")
        interstitialAd = null
        isLoading = false
    }

    override suspend fun loadBannerAd(adView: AdView): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        Log.d(TAG, "Starting to load banner ad with ID: ${BuildConfig.BANNER_ADS_ID}")

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad loaded successfully")
                trySend(ResultState.Success(true))
                close()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner ad failed to load: ${error.message}")
                Log.e(TAG, "Error code: ${error.code}, domain: ${error.domain}")
                trySend(ResultState.Error("Failed to load banner ad: ${error.message}"))
                close()
            }

            override fun onAdClicked() {
                Log.d(TAG, "Banner ad clicked")
            }

            override fun onAdOpened() {
                Log.d(TAG, "Banner ad opened")
            }

            override fun onAdClosed() {
                Log.d(TAG, "Banner ad closed")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Banner ad impression recorded")
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        awaitClose {
            Log.d(TAG, "Banner ad flow closed")
        }
    }
}
