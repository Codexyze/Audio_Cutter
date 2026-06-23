package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.MuteVideoState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.MuteVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MuteVideoViewModel @Inject constructor(
    private val muteVideoUseCase: MuteVideoUseCase
) : ViewModel() {

    private val _muteVideoState = MutableStateFlow(MuteVideoState())
    val muteVideoState: StateFlow<MuteVideoState> = _muteVideoState.asStateFlow()

    fun muteVideo(uri: Uri, filename: String) {
        viewModelScope.launch {
            muteVideoUseCase(uri, filename).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _muteVideoState.update { it.copy(isLoading = true, error = null) }
                    }
                    is ResultState.Success -> {
                        _muteVideoState.update { it.copy(isLoading = false, data = result.data, error = null) }
                    }
                    is ResultState.Error -> {
                        _muteVideoState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}
