package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.AudioSpeedState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.StateHandeling.UserLimitState
import com.nutrino.audiocutter.domain.UseCases.ChangeAudioSpeedUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.CheckFeatureLimitUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.IncrementUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioSpeedViewModel @Inject constructor(
    private val changeAudioSpeedUseCase: ChangeAudioSpeedUseCase,
    private val checkFeatureLimitUseCase: CheckFeatureLimitUseCase,
    private val incrementUsageUseCase: IncrementUsageUseCase
) : ViewModel() {

    private val _audioSpeedState = MutableStateFlow(AudioSpeedState())
    val audioSpeedState = _audioSpeedState.asStateFlow()

    private val _userLimitState = MutableStateFlow(UserLimitState())
    val userLimitState = _userLimitState.asStateFlow()

    fun changeAudioSpeed(
        uri: Uri,
        speed: Float,
        filename: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            checkFeatureLimitUseCase().collect { limitResult ->
                when (limitResult) {
                    is ResultState.Loading -> {
                        _userLimitState.value = UserLimitState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _userLimitState.value = UserLimitState(isLoading = false, isLimitReached = false)
                        
                        changeAudioSpeedUseCase.invoke(
                            uri = uri,
                            speed = speed,
                            filename = filename
                        ).collect { result ->
                            when (result) {
                                is ResultState.Loading -> {
                                    _audioSpeedState.value = AudioSpeedState(isLoading = true)
                                }

                                is ResultState.Success -> {
                                    _audioSpeedState.value = AudioSpeedState(
                                        isLoading = false,
                                        data = result.data
                                    )
                                    incrementUsageUseCase()
                                }

                                is ResultState.Error -> {
                                    _audioSpeedState.value = AudioSpeedState(
                                        isLoading = false,
                                        error = result.message
                                    )
                                }
                            }
                        }
                    }
                    is ResultState.Error -> {
                        _userLimitState.value = UserLimitState(isLoading = false, isLimitReached = true, error = limitResult.message)
                    }
                }
            }
        }
    }

    fun resetUserLimitError() {
        _userLimitState.value = UserLimitState(isLimitReached = false, error = null)
    }
}
