package com.nutrino.audiocutter.presentation.ViewModel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.UseCases.LogEventUseCase
import com.nutrino.audiocutter.domain.UseCases.ScreenViewLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val logEventUseCase: LogEventUseCase,
    private val screenViewLogUseCase: ScreenViewLogUseCase
) : ViewModel() {

    fun logEvent(eventName: String, params: Bundle? = null) {
        viewModelScope.launch {
            logEventUseCase(eventName, params)
        }
    }

    fun screenViewLog(screenName: String, bundle: Bundle = Bundle()) {
        viewModelScope.launch {
            screenViewLogUseCase(screenName, bundle)
        }
    }
}
