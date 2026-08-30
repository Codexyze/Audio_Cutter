package com.nutrino.audiocutter.data.DataClass

data class Video(
    val id: String,
    val path: String,
    val duration: String,
    val thumbnail: String,
    val fileName: String,
    val title: String,
    val folderName: String
)