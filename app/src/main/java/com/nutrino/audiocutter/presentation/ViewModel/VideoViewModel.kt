package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.GetAllVideoState
import com.nutrino.audiocutter.domain.StateHandeling.VideoTrimmerState
import com.nutrino.audiocutter.domain.StateHandeling.AudioExtractorState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.GetAllVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.TrimVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.ExtractAudioFromVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class VideoViewModel @Inject constructor (
    private val getAllVideoUseCase: GetAllVideoUseCase,
    private val trimVideoUseCase: TrimVideoUseCase,
    private val extractAudioFromVideoUseCase: ExtractAudioFromVideoUseCase
): ViewModel() {
    private val _getAllVideosState= MutableStateFlow(GetAllVideoState())
    val getAllVideosState = _getAllVideosState.asStateFlow()

    private val _videoTrimmerState = MutableStateFlow(VideoTrimmerState())
    val videoTrimmerState = _videoTrimmerState.asStateFlow()

    private val _audioExtractorState = MutableStateFlow(AudioExtractorState())
    val audioExtractorState = _audioExtractorState.asStateFlow()

    fun getAllVideo(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllVideoUseCase.invoke().collect {result->
                when(result){
                    is ResultState.Loading->{
                        _getAllVideosState.value = GetAllVideoState(isLoading = true)
                    }
                    is ResultState.Success ->{
                        _getAllVideosState.value = GetAllVideoState(isLoading = false, data = result.data)
                    }
                    is ResultState.Error->{
                        _getAllVideosState.value = GetAllVideoState(isLoading = false, error = result.message)

                    }
                }

            }
        }
    }

    fun trimVideo(uri: Uri,
                  startTime: Long,
                  endTime: Long,
                  filename: String){

        viewModelScope.launch(Dispatchers.Main) {
            trimVideoUseCase.invoke(uri = uri, startTime = startTime, endTime = endTime,
                filename = filename).collect {result->
                when(result){
                    is ResultState.Loading -> {
                        _videoTrimmerState.value = VideoTrimmerState(isLoading = true)

                    }
                    is ResultState.Success -> {
                        _videoTrimmerState.value = VideoTrimmerState(isLoading = false, data = result.data)

                    }
                    is ResultState.Error->{
                        _videoTrimmerState.value = VideoTrimmerState(isLoading = false, error = result.message)

                    }

                }

            }
        }

    }

    fun extractAudioFromVideo(uri: Uri,
                              startTime: Long,
                              endTime: Long,
                              filename: String){

        viewModelScope.launch(Dispatchers.Main) {
            extractAudioFromVideoUseCase.invoke(uri = uri, startTime = startTime, endTime = endTime,
                filename = filename).collect {result->
                when(result){
                    is ResultState.Loading -> {
                        _audioExtractorState.value = AudioExtractorState(isLoading = true)

                    }
                    is ResultState.Success -> {
                        _audioExtractorState.value = AudioExtractorState(isLoading = false, data = result.data)

                    }
                    is ResultState.Error->{
                        _audioExtractorState.value = AudioExtractorState(isLoading = false, error = result.message)

                    }

                }

            }
        }

    }
}
