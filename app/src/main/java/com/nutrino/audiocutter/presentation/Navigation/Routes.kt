package com.nutrino.audiocutter.presentation.Navigation

import kotlinx.serialization.Serializable

@Serializable
object HOMESCREEN

@Serializable
data class AUDIOTRIMMERSCREEN(val uri: String , val songDuration : Long, val songName : String)

