package com.nutrino.audiocutter.domain.StateHandeling

sealed class ResultState<out T>{
    object Loading: ResultState<Nothing>()
    data class Success<T>(val data: T): ResultState<T>()
    data class Error(val message: String): ResultState<Nothing>()
}