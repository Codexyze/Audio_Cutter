package com.nutrino.audiocutter.presentation.ViewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.StateHandeling.AdState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.LoadAdUseCase
import com.nutrino.audiocutter.domain.UseCases.LoadBannerAdUseCase
import com.nutrino.audiocutter.domain.UseCases.ShowAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(
    private val loadAdUseCase: LoadAdUseCase,
    private val showAdUseCase: ShowAdUseCase,
    private val loadBannerAdUseCase: LoadBannerAdUseCase,
    private val adsRepository: AdsRepository
) : ViewModel() {

    private val _adState = MutableStateFlow(AdState())
    val adState = _adState.asStateFlow()

    private val _bannerAdState = MutableStateFlow<BannerAdState>(BannerAdState.Idle)
    val bannerAdState = _bannerAdState.asStateFlow()

    private val TAG = "AdsViewModel"

    init {
        // Preload ad when ViewModel is created
        loadAd()
    }

    fun loadAd() {
        viewModelScope.launch(Dispatchers.Main) {
            loadAdUseCase().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _adState.value = _adState.value.copy(
                            isLoading = true,
                            error = null
                        )
                        Log.d(TAG, "Loading ad...")
                    }

                    is ResultState.Success -> {
                        _adState.value = _adState.value.copy(
                            isLoading = false,
                            isAdReady = result.data,
                            error = null
                        )
                        Log.d(TAG, "Ad loaded successfully")
                    }

                    is ResultState.Error -> {
                        _adState.value = _adState.value.copy(
                            isLoading = false,
                            isAdReady = false,
                            error = result.message
                        )
                        Log.e(TAG, "Failed to load ad: ${result.message}")
                    }
                }
            }
        }
    }

    fun showAd(activity: Activity, onAdDismissed: () -> Unit = {}, onAdFailed: () -> Unit = {}) {
        if (!adsRepository.isAdReady()) {
            Log.w(TAG, "Ad not ready, calling onAdFailed")
            onAdFailed()
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            showAdUseCase(activity).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _adState.value = _adState.value.copy(
                            isAdShowing = true,
                            error = null
                        )
                        Log.d(TAG, "Showing ad...")
                    }

                    is ResultState.Success -> {
                        _adState.value = _adState.value.copy(
                            isAdShowing = false,
                            isAdReady = false,
                            adDismissed = true
                        )
                        Log.d(TAG, "Ad dismissed successfully")
                        onAdDismissed()
                        // Preload next ad
                        loadAd()
                    }

                    is ResultState.Error -> {
                        _adState.value = _adState.value.copy(
                            isAdShowing = false,
                            isAdReady = false,
                            error = result.message
                        )
                        Log.e(TAG, "Failed to show ad: ${result.message}")
                        onAdFailed()
                        // Try to load again for next time
                        loadAd()
                    }
                }
            }
        }
    }

    fun requestAndShowAd(activity: Activity, onAdDismissed: () -> Unit = {}, onAdFailed: () -> Unit = {}) {
        if (adsRepository.isAdReady()) {
            showAd(activity, onAdDismissed, onAdFailed)
        } else {
            Log.w(TAG, "Ad not ready, loading first then showing...")
            viewModelScope.launch(Dispatchers.Main) {
                loadAdUseCase().collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            if (result.data) {
                                showAd(activity, onAdDismissed, onAdFailed)
                            } else {
                                onAdFailed()
                            }
                        }
                        is ResultState.Error -> {
                            onAdFailed()
                        }
                        else -> { /* Loading state, do nothing */ }
                    }
                }
            }
        }
    }

    fun resetAdDismissedState() {
        _adState.value = _adState.value.copy(adDismissed = false)
    }

    fun loadBannerAd(adView: AdView) {
        viewModelScope.launch(Dispatchers.Main) {
            loadBannerAdUseCase(adView).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _bannerAdState.value = BannerAdState.Loading
                        Log.d(TAG, "Loading banner ad...")
                    }

                    is ResultState.Success -> {
                        _bannerAdState.value = BannerAdState.Loaded
                        Log.d(TAG, "Banner ad loaded successfully")
                    }

                    is ResultState.Error -> {
                        _bannerAdState.value = BannerAdState.Failed(result.message)
                        Log.e(TAG, "Banner ad failed to load: ${result.message}")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        adsRepository.destroy()
    }
}

sealed class BannerAdState {
    object Idle : BannerAdState()
    object Loading : BannerAdState()
    object Loaded : BannerAdState()
    data class Failed(val message: String) : BannerAdState()
}

