package com.nutrino.audiocutter.presentation.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.GetAllSongState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.GetAllSongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class GetAllSongViewModel @Inject constructor (private val getAllSongUseCase: GetAllSongUseCase): ViewModel() {
    private val _getAllSongsState= MutableStateFlow(GetAllSongState())
    val getAllSongsState = _getAllSongsState.asStateFlow()

    init {
        getAllSong()
        Log.d("Songs",getAllSongsState.value.data.toString())
    }
    fun getAllSong(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllSongUseCase.invoke().collect {result->
                when(result){
                    is ResultState.Loading->{
                        _getAllSongsState.value = GetAllSongState(isLoading = true)
                    }
                    is ResultState.Success ->{
                        _getAllSongsState.value = GetAllSongState(isLoading = false, data = result.data)
                    }
                    is ResultState.Error->{
                        _getAllSongsState.value = GetAllSongState(isLoading = false, error = result.message)

                    }
                }

            }
        }
    }
}