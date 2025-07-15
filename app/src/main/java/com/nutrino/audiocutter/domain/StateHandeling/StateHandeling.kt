package com.nutrino.audiocutter.domain.StateHandeling

import com.nutrino.audiocutter.data.DataClass.Song

sealed class ResultState<out T>{
    object Loading: ResultState<Nothing>()
    data class Success<T>(val data: T): ResultState<T>()
    data class Error(val message: String): ResultState<Nothing>()
}

data class AudioTrimmerState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class GetAllSongState(
    val isLoading: Boolean = false,
    val data: List<Song> = emptyList(),
    val error: String ? = null
)
