package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.AudioVolumeBoosterState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.BoostAudioVolumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioVolumeBoosterViewModel @Inject constructor(
    private val boostAudioVolumeUseCase: BoostAudioVolumeUseCase
) : ViewModel() {

    private val _audioVolumeBoosterState = MutableStateFlow(AudioVolumeBoosterState())
    val audioVolumeBoosterState = _audioVolumeBoosterState.asStateFlow()

    fun boostAudioVolume(
        uri: Uri,
        volumeFactor: Float,
        filename: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            boostAudioVolumeUseCase(
                uri = uri,
                volumeFactor = volumeFactor,
                filename = filename
            ).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _audioVolumeBoosterState.value = AudioVolumeBoosterState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _audioVolumeBoosterState.value = AudioVolumeBoosterState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _audioVolumeBoosterState.value = AudioVolumeBoosterState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _audioVolumeBoosterState.value = AudioVolumeBoosterState()
    }
}
