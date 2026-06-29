package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.StateHandeling.VideoSpeedState
import com.nutrino.audiocutter.domain.StateHandeling.UserLimitState
import com.nutrino.audiocutter.domain.UseCases.ChangeVideoSpeedUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.CheckFeatureLimitUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.IncrementUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoSpeedViewModel @Inject constructor(
    private val changeVideoSpeedUseCase: ChangeVideoSpeedUseCase,
    private val checkFeatureLimitUseCase: CheckFeatureLimitUseCase,
    private val incrementUsageUseCase: IncrementUsageUseCase
) : ViewModel() {

    private val _videoSpeedState = MutableStateFlow(VideoSpeedState())
    val videoSpeedState = _videoSpeedState.asStateFlow()

    private val _userLimitState = MutableStateFlow(UserLimitState())
    val userLimitState = _userLimitState.asStateFlow()

    fun changeVideoSpeed(
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
                        
                        changeVideoSpeedUseCase.invoke(
                            uri = uri,
                            speed = speed,
                            filename = filename
                        ).collect { result ->
                            when (result) {
                                is ResultState.Loading -> {
                                    _videoSpeedState.value = VideoSpeedState(isLoading = true)
                                }

                                is ResultState.Success -> {
                                    _videoSpeedState.value = VideoSpeedState(
                                        isLoading = false,
                                        data = result.data
                                    )
                                    incrementUsageUseCase()
                                }

                                is ResultState.Error -> {
                                    _videoSpeedState.value = VideoSpeedState(
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


