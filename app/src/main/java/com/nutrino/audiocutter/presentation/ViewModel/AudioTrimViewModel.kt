package com.nutrino.audiocutter.presentation.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.AudioTrimmerState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.StateHandeling.UserLimitState
import com.nutrino.audiocutter.domain.UseCases.TrimAudioUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.CheckFeatureLimitUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.IncrementUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class AudioTrimViewModel @Inject constructor(
    private val audioTrimmerUseCase: TrimAudioUseCase,
    private val checkFeatureLimitUseCase: CheckFeatureLimitUseCase,
    private val incrementUsageUseCase: IncrementUsageUseCase
) : ViewModel() {
    private val _audioTrimmerState = MutableStateFlow(AudioTrimmerState())
    val audioTrimmerState = _audioTrimmerState.asStateFlow()

    private val _userLimitState = MutableStateFlow(UserLimitState())
    val userLimitState = _userLimitState.asStateFlow()

    fun audioTrimmerState( context: Context,
                           uri: Uri,
                           startTime: Long,
                           endTime: Long,
                           filename: String){

        viewModelScope.launch(Dispatchers.Main) {
            // Check daily limit first
            checkFeatureLimitUseCase().collect { limitResult ->
                when (limitResult) {
                    is ResultState.Loading -> {
                        _userLimitState.value = UserLimitState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _userLimitState.value = UserLimitState(isLoading = false, isLimitReached = false)
                        
                        // Proceed with trim if limit not reached
                        audioTrimmerUseCase.invoke(context = context, uri = uri, startTime = startTime, endTime = endTime,
                            filename = filename).collect { result ->
                            when (result) {
                                is ResultState.Loading -> {
                                    _audioTrimmerState.value = AudioTrimmerState(isLoading = true)
                                }
                                is ResultState.Success -> {
                                    _audioTrimmerState.value = AudioTrimmerState(isLoading = false, data = result.data)
                                    // Increment usage count after successful trim
                                    incrementUsageUseCase()
                                }
                                is ResultState.Error -> {
                                    _audioTrimmerState.value = AudioTrimmerState(isLoading = false, error = result.message)
                                }
                            }
                        }
                    }
                    is ResultState.Error -> {
                        _userLimitState.value = UserLimitState(
                            isLoading = false, 
                            isLimitReached = true, 
                            error = limitResult.message
                        )
                    }
                }
            }
        }
    }

    fun resetUserLimitError() {
        _userLimitState.value = UserLimitState(isLimitReached = false, error = null)
    }
}
