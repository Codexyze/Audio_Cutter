package com.nutrino.audiocutter.presentation.ViewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.StateHandeling.AdState
import com.nutrino.audiocutter.domain.StateHandeling.IsUserProState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.StateHandeling.RewardedAdState
import com.nutrino.audiocutter.domain.UseCases.LoadAdUseCase
import com.nutrino.audiocutter.domain.UseCases.LoadBannerAdUseCase
import com.nutrino.audiocutter.domain.UseCases.LoadRewardedAdUseCase
import com.nutrino.audiocutter.domain.UseCases.ShowAdUseCase
import com.nutrino.audiocutter.domain.UseCases.ShowRewardedAdUseCase
import com.nutrino.audiocutter.domain.UseCases.revenueCat.IsUserProUseCase
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
    private val loadRewardedAdUseCase: LoadRewardedAdUseCase,
    private val showRewardedAdUseCase: ShowRewardedAdUseCase,
    private val adsRepository: AdsRepository,
    private val isUserProUseCase: IsUserProUseCase
) : ViewModel() {

    private val _adState = MutableStateFlow(AdState())
    val adState = _adState.asStateFlow()

    private val _rewardedAdState = MutableStateFlow(RewardedAdState())
    val rewardedAdState = _rewardedAdState.asStateFlow()

    private val _bannerAdState = MutableStateFlow<BannerAdState>(BannerAdState.Idle)
    val bannerAdState = _bannerAdState.asStateFlow()

    private val _isUserProState = MutableStateFlow(IsUserProState())
    val isUserProState = _isUserProState.asStateFlow()

    private val TAG = "AdsViewModel"

    init {
        refreshIsUserProStatusForAds()
    }

    fun refreshIsUserProStatusForAds() {
        viewModelScope.launch(Dispatchers.Main) {
            isUserProUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = true
                        )
                    }

                    is ResultState.Error -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            error = result.message
                        )
                        // Fallback to ad loading when pro status check fails.
                        loadAd()
                    }

                    is ResultState.Success -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            data = result.data
                        )

                        if (!result.data) {
                            loadAd()
                            loadRewardedAd()
                        } else {
                            Log.d(TAG, "User is Pro, skipping interstitial preload")
                        }
                    }
                }
            }
        }
    }

    fun loadAd() {
        if (_isUserProState.value.isLoading || _isUserProState.value.data) {
            Log.d(TAG, "Skipping interstitial load because user is Pro or pro check is loading")
            return
        }

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

    fun loadRewardedAd() {
        if (_isUserProState.value.isLoading || _isUserProState.value.data) {
            Log.d(TAG, "Skipping rewarded load because user is Pro or pro check is loading")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            loadRewardedAdUseCase().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _rewardedAdState.value = _rewardedAdState.value.copy(
                            isLoading = true,
                            error = null
                        )
                        Log.d(TAG, "Loading rewarded ad...")
                    }

                    is ResultState.Success -> {
                        _rewardedAdState.value = _rewardedAdState.value.copy(
                            isLoading = false,
                            isAdReady = result.data,
                            error = null
                        )
                        Log.d(TAG, "Rewarded ad loaded successfully")
                    }

                    is ResultState.Error -> {
                        _rewardedAdState.value = _rewardedAdState.value.copy(
                            isLoading = false,
                            isAdReady = false,
                            error = result.message
                        )
                        Log.e(TAG, "Failed to load rewarded ad: ${result.message}")
                    }
                }
            }
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onAdDismissed: () -> Unit = {},
        onAdFailed: () -> Unit = {},
        onUserEarnedReward: () -> Unit = {}
    ) {
        if (_isUserProState.value.data) {
            Log.d(TAG, "User is Pro, skipping rewarded show")
            onAdDismissed()
            return
        }

        if (!adsRepository.isRewardedAdReady()) {
            Log.w(TAG, "Rewarded ad not ready, calling onAdFailed")
            onAdFailed()
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            showRewardedAdUseCase(activity).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _rewardedAdState.value = _rewardedAdState.value.copy(
                            isAdShowing = true,
                            error = null
                        )
                        Log.d(TAG, "Showing rewarded ad...")
                    }

                    is ResultState.Success -> {
                        _rewardedAdState.value = _rewardedAdState.value.copy(
                            isAdShowing = false,
                            isAdReady = false,
                            adDismissed = true,
                            userEarnedReward = true
                        )
                        Log.d(TAG, "Rewarded ad dismissed successfully")
                        onUserEarnedReward()
                        onAdDismissed()
                        // Preload next ad
                        loadRewardedAd()
                    }

                    is ResultState.Error -> {
                        _rewardedAdState.value = _rewardedAdState.value.copy(
                            isAdShowing = false,
                            isAdReady = false,
                            error = result.message
                        )
                        Log.e(TAG, "Failed to show rewarded ad: ${result.message}")
                        onAdFailed()
                        // Try to load again for next time
                        loadRewardedAd()
                    }
                }
            }
        }
    }

    fun showAd(activity: Activity, onAdDismissed: () -> Unit = {}, onAdFailed: () -> Unit = {}) {
        if (_isUserProState.value.data) {
            Log.d(TAG, "User is Pro, skipping interstitial show")
            onAdDismissed()
            return
        }

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
        viewModelScope.launch(Dispatchers.Main) {
            isUserProUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = true
                        )
                    }

                    is ResultState.Error -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            error = result.message
                        )
                        requestAndShowAdForNonPro(activity, onAdDismissed, onAdFailed)
                    }

                    is ResultState.Success -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            data = result.data
                        )

                        if (result.data) {
                            Log.d(TAG, "User is Pro, bypassing interstitial in requestAndShowAd")
                            onAdDismissed()
                        } else {
                            requestAndShowAdForNonPro(activity, onAdDismissed, onAdFailed)
                        }
                    }
                }
            }
        }
    }

    fun requestAndShowRewardedAd(
        activity: Activity,
        onAdDismissed: () -> Unit = {},
        onAdFailed: () -> Unit = {},
        onUserEarnedReward: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            isUserProUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = true
                        )
                    }

                    is ResultState.Error -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            error = result.message
                        )
                        requestAndShowRewardedAdForNonPro(activity, onAdDismissed, onAdFailed, onUserEarnedReward)
                    }

                    is ResultState.Success -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            data = result.data
                        )

                        if (result.data) {
                            Log.d(TAG, "User is Pro, bypassing rewarded in requestAndShowRewardedAd")
                            onAdDismissed()
                        } else {
                            requestAndShowRewardedAdForNonPro(activity, onAdDismissed, onAdFailed, onUserEarnedReward)
                        }
                    }
                }
            }
        }
    }

    private fun requestAndShowRewardedAdForNonPro(
        activity: Activity,
        onAdDismissed: () -> Unit,
        onAdFailed: () -> Unit,
        onUserEarnedReward: () -> Unit
    ) {
        if (adsRepository.isRewardedAdReady()) {
            showRewardedAd(activity, onAdDismissed, onAdFailed, onUserEarnedReward)
        } else {
            Log.w(TAG, "Rewarded ad not ready, loading first then showing...")
            viewModelScope.launch(Dispatchers.Main) {
                loadRewardedAdUseCase().collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            if (result.data) {
                                showRewardedAd(activity, onAdDismissed, onAdFailed, onUserEarnedReward)
                            } else {
                                onAdFailed()
                            }
                        }

                        is ResultState.Error -> {
                            onAdFailed()
                        }

                        else -> {
                            // Loading state, do nothing
                        }
                    }
                }
            }
        }
    }

    private fun requestAndShowAdForNonPro(
        activity: Activity,
        onAdDismissed: () -> Unit,
        onAdFailed: () -> Unit
    ) {
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

                        else -> {
                            // Loading state, do nothing
                        }
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

