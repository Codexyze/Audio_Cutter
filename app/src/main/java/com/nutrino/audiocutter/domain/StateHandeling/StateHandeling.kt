package com.nutrino.audiocutter.domain.StateHandeling

import com.nutrino.audiocutter.data.DataClass.Song
import com.nutrino.audiocutter.data.DataClass.Video

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

data class VideoTrimmerState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class AudioExtractorState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class GetAllSongState(
    val isLoading: Boolean = false,
    val data: List<Song> = emptyList(),
    val error: String ? = null
)

data class GetAllVideoState(
    val isLoading: Boolean = false,
    val data: List<Video> = emptyList(),
    val error: String ? = null
)

data class GetAllSongsForMergeState(
    val isLoading: Boolean = false,
    val data: List<Song> = emptyList(),
    val error: String ? = null
)

data class AudioMergeState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

