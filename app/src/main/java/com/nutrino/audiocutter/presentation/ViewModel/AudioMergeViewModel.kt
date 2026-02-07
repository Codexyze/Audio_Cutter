package com.nutrino.audiocutter.presentation.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.AudioMergeState
import com.nutrino.audiocutter.domain.StateHandeling.GetAllSongsForMergeState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.GetAllSongsForMergeUseCase
import com.nutrino.audiocutter.domain.UseCases.MergeSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class AudioMergeViewModel @Inject constructor(
    private val getAllSongsForMergeUseCase: GetAllSongsForMergeUseCase,
    private val mergeSongsUseCase: MergeSongsUseCase
): ViewModel() {

    private val _getAllSongsForMergeState= MutableStateFlow(GetAllSongsForMergeState())
    val getAllSongsForMergeState = _getAllSongsForMergeState.asStateFlow()

    private val _audioMergeState = MutableStateFlow(AudioMergeState())
    val audioMergeState = _audioMergeState.asStateFlow()

    init {
        getAllSongsForMerge()
        Log.d("AudioMerge",getAllSongsForMergeState.value.data.toString())
    }

    fun getAllSongsForMerge(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllSongsForMergeUseCase.invoke().collect {result->
                when(result){
                    is ResultState.Loading->{
                        _getAllSongsForMergeState.value = GetAllSongsForMergeState(isLoading = true)
                    }
                    is ResultState.Success ->{
                        _getAllSongsForMergeState.value = GetAllSongsForMergeState(isLoading = false, data = result.data)
                    }
                    is ResultState.Error->{
                        _getAllSongsForMergeState.value = GetAllSongsForMergeState(isLoading = false, error = result.message)

                    }
                }

            }
        }
    }

    fun mergeSongs(uriList: List<Uri>, filename: String){
        viewModelScope.launch(Dispatchers.Main) {
            mergeSongsUseCase.invoke(uriList = uriList, filename = filename).collect {result->
                when(result){
                    is ResultState.Loading -> {
                        _audioMergeState.value = AudioMergeState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _audioMergeState.value = AudioMergeState(isLoading = false, data = result.data)
                    }
                    is ResultState.Error->{
                        _audioMergeState.value = AudioMergeState(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}
