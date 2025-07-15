package com.nutrino.audiocutter.presentation.Utils

fun formatDuration(duration: String?): String {
    val millis = duration?.toLongOrNull() ?: return "0:00"
    val minutes = millis / 1000 / 60
    val seconds = (millis / 1000 % 60)
    return String.format("%d:%02d", minutes, seconds)
}