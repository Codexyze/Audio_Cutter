package com.nutrino.audiocutter.data.DataClass

import android.graphics.Bitmap

data class Video(
    val id: String,
    val path: String,
    val duration: String,
    val thumbnail: String,
    val fileName: String,
    val title: String,
    val folderName: String
)