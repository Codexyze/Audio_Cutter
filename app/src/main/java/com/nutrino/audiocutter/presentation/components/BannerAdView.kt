package com.nutrino.audiocutter.presentation.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nutrino.audiocutter.BuildConfig
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.BannerAdState

private const val TAG = "BannerAdView"

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adsViewModel: AdsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val bannerAdState: BannerAdState by adsViewModel.bannerAdState.collectAsState()

    val adView = remember {
        AdView(context).apply {
            adUnitId = BuildConfig.BANNER_ADS_ID
            setAdSize(AdSize.BANNER)
            Log.d(TAG, "AdView created - Banner Ad ID configured from gradle.properties")
        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "Loading banner ad...")
        adsViewModel.loadBannerAd(adView)
    }

    // Only show the banner when it's successfully loaded
    AnimatedVisibility(
        visible = bannerAdState is BannerAdState.Loaded,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { adView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            )
        }
    }

    // Log state changes
    LaunchedEffect(bannerAdState) {
        when (bannerAdState) {
            is BannerAdState.Loading -> Log.d(TAG, "Banner ad state: Loading")
            is BannerAdState.Loaded -> Log.d(TAG, "Banner ad state: Loaded")
            is BannerAdState.Failed -> Log.e(TAG, "Banner ad state: Failed - ${(bannerAdState as BannerAdState.Failed).message}")
            is BannerAdState.Idle -> Log.d(TAG, "Banner ad state: Idle")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Destroying banner ad view")
            adView.destroy()
        }
    }
}
