package com.nutrino.audiocutter.domain.Repository

import android.app.Activity
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface AdsRepository {
    suspend fun loadInterstitialAd(): Flow<ResultState<Boolean>>
    suspend fun showInterstitialAd(activity: Activity): Flow<ResultState<Boolean>>
    fun isAdReady(): Boolean
    fun destroy()
}
