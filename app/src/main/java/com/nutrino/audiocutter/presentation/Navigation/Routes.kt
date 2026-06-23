package com.nutrino.audiocutter.presentation.Navigation

import kotlinx.serialization.Serializable

@Serializable
object HOMESCREEN

@Serializable
object ALLVIDEOSCREEN

@Serializable
data class AUDIOTRIMMERSCREEN(val uri: String , val songDuration : Long, val songName : String)

@Serializable
data class VIDEOTRIMMERSCREEN(val uri: String , val videoDuration : Long, val videoName : String)

@Serializable
object AUDIOTRIMMERSUCCESSSTATE

@Serializable
object AUDIOTRIMMERERRORSTATE

@Serializable
object VIDEOTRIMMERSUCCESSSTATE

@Serializable
object VIDEOTRIMMERERRORSTATE

@Serializable
object SELECTFEATURESCREEN

@Serializable
object RECENTSCREEN

@Serializable
object PROPACKAGESCREEN

@Serializable
object THEMESELECTIONSCREEN

@Serializable
data class BUYPROPACKAGESCREEN(
	val packageIdentifier: String,
	val productId: String,
	val title: String,
	val description: String,
	val priceFormatted: String,
	val packageType: String
)

@Serializable
data class RECENTAUDIOPLAYERSCREEN(
	val outputUri: String,
	val outputName: String,
	val inputName: String
)

@Serializable
data class RECENTVIDEOPLAYERSCREEN(
	val outputUri: String,
	val outputName: String,
	val inputName: String
)

@Serializable
object ALLVIDEOFORAUDIOEXTRACTSCREEN

@Serializable
data class AUDIOEXTRACTORSCREEN(val uri: String , val videoDuration : Long, val videoName : String)

@Serializable
object AUDIOEXTRACTORSUCCESSSTATE

@Serializable
object AUDIOEXTRACTORERRORSTATE

@Serializable
object ALLAUDIOFORMERGESCREEN

@Serializable
data class AUDIOMERGESCREEN(val uriList: List<String>, val songNames: List<String>)

@Serializable
object AUDIOMERGESUCCESSSTATE

@Serializable
object AUDIOMERGEERRORSTATE

@Serializable
object ALLSONGSFORMULTICROPSCREEN

@Serializable
data class MULTICROPAUDIOSCREEN(val uri: String, val songDuration: Long, val songName: String)

@Serializable
object MULTICROPAUDIOSUCCESSSTATE

@Serializable
object MULTICROPAUDIOERRORSTATE

@Serializable
object ALLVIDEOSFORMULTICROPSCREEN

@Serializable
data class MULTICROPVIDEOSCREEN(val uri: String, val videoDuration: Long, val videoName: String)

@Serializable
object MULTICROPVIDEOSUCCESSSTATE

@Serializable
object MULTICROPVIDEOERRORSTATE

@Serializable
object ALLSONGSFORCONVERTAUDIOFORMATSCREEN

@Serializable
data class CONVERTAUDIOFORMATSCREEN(val uri: String, val songDuration: Long, val songName: String)

@Serializable
object CONVERTAUDIOFORMATSUCCESSSTATE

@Serializable
object CONVERTAUDIOFORMATERRORSTATE

@Serializable
object RECORDAUDIOSCREEN

@Serializable
object RECORDAUDIOSUCCESSSTATE

@Serializable
object RECORDAUDIOERRORSTATE

@Serializable
object ALLVIDEOSFORSPEEDSCREEN

@Serializable
data class VIDEOSPEEDSCREEN(
    val uri: String,
    val videoDuration: Long,
    val videoName: String
)

@Serializable
object VIDEOSPEEDSUCCESSSTATE

@Serializable
object VIDEOSPEEDERRORSTATE

@Serializable
object ALLAUDIOFORSPEEDSCREEN

@Serializable
data class AUDIOSPEEDSCREEN(
    val uri: String,
    val songDuration: Long,
    val songName: String
)

@Serializable
object AUDIOSPEEDSUCCESSSTATE

@Serializable
object AUDIOSPEEDERRORSTATE
