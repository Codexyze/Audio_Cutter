package com.nutrino.audiocutter.data.DataClass

import android.graphics.Bitmap


data class Song(
    val id: String ,
    val path: String,
    val size: String?="",
    val album: String?="Unknown",
    val title: String ?="Unknown",
    val artist: String?="Unknown",
    val duration: String?="0",
    val year: String?="0",
    val composer: String?="Unknown",
    val albumId: String?="",
    val albumArt: Bitmap? = null
)

