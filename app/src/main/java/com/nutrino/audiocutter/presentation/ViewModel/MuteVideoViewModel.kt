package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.MuteVideoState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.StateHandeling.UserLimitState
import com.nutrino.audiocutter.domain.UseCases.MuteVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.CheckFeatureLimitUseCase
import com.nutrino.audiocutter.domain.UseCases.userPref.IncrementUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MuteVideoViewModel @Inject constructor(
    private val muteVideoUseCase: MuteVideoUseCase,
    private val checkFeatureLimitUseCase: CheckFeatureLimitUseCase,
    private val incrementUsageUseCase: IncrementUsageUseCase
) : ViewModel() {

    private val _muteVideoState = MutableStateFlow(MuteVideoState())
    val muteVideoState: StateFlow<MuteVideoState> = _muteVideoState.asStateFlow()

    private val _userLimitState = MutableStateFlow(UserLimitState())
    val userLimitState = _userLimitState.asStateFlow()

    fun muteVideo(uri: Uri, filename: String) {
        viewModelScope.launch {
            checkFeatureLimitUseCase().collect { limitResult ->
                when (limitResult) {
                    is ResultState.Loading -> {
                        _userLimitState.value = UserLimitState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _userLimitState.value = UserLimitState(isLoading = false, isLimitReached = false)
                        
                        muteVideoUseCase(uri, filename).collect { result ->
                            when (result) {
                                is ResultState.Loading -> {
                                    _muteVideoState.update { it.copy(isLoading = true, error = null) }
                                }
                                is ResultState.Success -> {
                                    _muteVideoState.update { it.copy(isLoading = false, data = result.data, error = null) }
                                    incrementUsageUseCase()
                                }
                                is ResultState.Error -> {
                                    _muteVideoState.update { it.copy(isLoading = false, error = result.message) }
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
