package com.nutrino.audiocutter.presentation.Utils


import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nutrino.audiocutter.BuildConfig

/**
 * Helper class to manage Interstitial Ads for Progress Tracker feature
 * Handles ad loading, showing, and lifecycle management
 */
object InterstitialAdHelper {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private const val TAG = "InterstitialAdHelper"
    private var pendingShow: ((Activity) -> Unit)? = null

    /**
     * Load an interstitial ad
     * Call this to preload ads before showing
     */
    fun loadAd(context: Context) {
        if (isLoading || interstitialAd != null) {
            Log.d(TAG, "Ad already loaded or loading")
            return
        }

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
                    // If there was a pending request to show, execute it now
                    val activity = (context as? Activity)
                    if (activity != null) {
                        pendingShow?.invoke(activity)
                    }
                    pendingShow = null
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, " Failed to load interstitial ad: ${error.message}")
                    Log.e(TAG, "Error code: ${error.code}")
                    Log.e(TAG, "Error domain: ${error.domain}")
                    interstitialAd = null
                    isLoading = false
                    pendingShow = null
                }
            }
        )
    }

    /**
     * Request to show an ad; if ready shows immediately, otherwise loads then shows once loaded.
     */
    fun requestAndShow(
        activity: Activity,
        onAdDismissed: () -> Unit = {},
        onAdFailed: () -> Unit = {}
    ) {
        if (isAdReady()) {
            showAd(activity, onAdDismissed, onAdFailed)
            return
        }
        // Queue up show attempt after load
        pendingShow = { act ->
            if (isAdReady()) {
                showAd(act, onAdDismissed, onAdFailed)
            } else {
                Log.w(TAG, "Ad still not ready after load callback; proceeding without ad")
                onAdFailed()
            }
        }
        loadAd(activity)
    }

    /**
     * Show the loaded interstitial ad
     * @param activity Activity context to show the ad
     * @param onAdDismissed Callback when ad is dismissed or closed
     * @param onAdFailed Callback when ad fails to show or not ready
     */
    fun showAd(
        activity: Activity,
        onAdDismissed: () -> Unit = {},
        onAdFailed: () -> Unit = {}
    ) {
        if (interstitialAd != null) {
            Log.d(TAG, "Showing interstitial ad")

            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed by user")
                    interstitialAd = null
                    onAdDismissed()
                    // Preload next ad for future use
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e(TAG, "Ad failed to show: ${error.message}")
                    interstitialAd = null
                    onAdFailed()
                    // Try to load for next time
                    loadAd(activity)
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
        } else {
            Log.w(TAG, "️ Interstitial ad not ready yet (showAd called directly)")
            onAdFailed()
            // Try to load for next time
            loadAd(activity)
        }
    }

    /**
     * Check if ad is ready to show
     * @return true if ad is loaded and ready
     */
    fun isAdReady(): Boolean {
        val ready = interstitialAd != null
        Log.d(TAG, "Ad ready status: $ready")
        return ready
    }

    /**
     * Clean up ad reference
     * Call this when you want to release the ad
     */
    fun destroy() {
        Log.d(TAG, "Destroying ad reference")
        interstitialAd = null
        isLoading = false
        pendingShow = null
    }
}
