package com.nutrino.audiocutter.presentation.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.ConvertAudioFormatState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.ConvertAudioFormatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConvertAudioFormatViewModel @Inject constructor(
    private val convertAudioFormatUseCase: ConvertAudioFormatUseCase
) : ViewModel() {

    private val _convertAudioFormatState = MutableStateFlow(ConvertAudioFormatState())
    val convertAudioFormatState = _convertAudioFormatState.asStateFlow()

    fun convertAudioFormat(
        context: Context,
        uri: String,
        outputMimeType: String,
        outputExtension: String,
        filename: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            convertAudioFormatUseCase.invoke(
                context = context,
                uri = uri,
                outputMimeType = outputMimeType,
                outputExtension = outputExtension,
                filename = filename
            ).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _convertAudioFormatState.value = ConvertAudioFormatState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _convertAudioFormatState.value = ConvertAudioFormatState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _convertAudioFormatState.value = ConvertAudioFormatState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}

