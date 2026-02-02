package com.nutrino.audiocutter.presentation.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.GetAllVideoState
import com.nutrino.audiocutter.domain.StateHandeling.VideoTrimmerState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.GetAllVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.TrimVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class VideoViewModel @Inject constructor (
    private val getAllVideoUseCase: GetAllVideoUseCase,
    private val trimVideoUseCase: TrimVideoUseCase
): ViewModel() {
    private val _getAllVideosState= MutableStateFlow(GetAllVideoState())
    val getAllVideosState = _getAllVideosState.asStateFlow()

    private val _videoTrimmerState = MutableStateFlow(VideoTrimmerState())
    val videoTrimmerState = _videoTrimmerState.asStateFlow()

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

    fun trimVideo(context: Context,
                  uri: Uri,
                  startTime: Long,
                  endTime: Long,
                  filename: String){

        viewModelScope.launch(Dispatchers.Main) {
            trimVideoUseCase.invoke(context = context, uri = uri, startTime = startTime, endTime = endTime,
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
}
