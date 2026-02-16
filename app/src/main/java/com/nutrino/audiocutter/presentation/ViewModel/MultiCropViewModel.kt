package com.nutrino.audiocutter.presentation.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.data.DataClass.CropSegment
import com.nutrino.audiocutter.domain.StateHandeling.MultiCropAudioState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.MultiCropAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiCropViewModel @Inject constructor(
    private val multiCropAudioUseCase: MultiCropAudioUseCase
) : ViewModel() {

    // State for selected crop segments
    private val _segments = MutableStateFlow<List<CropSegment>>(emptyList())
    val segments = _segments.asStateFlow()

    // State for multi-crop audio operation
    private val _multiCropAudioState = MutableStateFlow(MultiCropAudioState())
    val multiCropAudioState = _multiCropAudioState.asStateFlow()

    // Function to add a segment
    fun addSegment(segment: CropSegment) {
        _segments.value = _segments.value + segment
    }

    // Function to remove a segment
    fun removeSegment(index: Int) {
        _segments.value = _segments.value.filterIndexed { i, _ -> i != index }
    }

    // Function to clear all segments
    fun clearSegments() {
        _segments.value = emptyList()
    }

    // Function to perform multi-crop audio operation
    fun multiCropAudio(
        context: Context,
        uri: String,
        segments: List<CropSegment>,
        filename: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            multiCropAudioUseCase.invoke(
                context = context,
                uri = uri,
                segments = segments,
                filename = filename
            ).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _multiCropAudioState.value = MultiCropAudioState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _multiCropAudioState.value = MultiCropAudioState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _multiCropAudioState.value = MultiCropAudioState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}