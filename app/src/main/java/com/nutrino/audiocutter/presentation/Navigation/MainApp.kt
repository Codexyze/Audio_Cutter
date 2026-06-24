package com.nutrino.audiocutter.presentation.Navigation

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nutrino.audiocutter.presentation.Screens.AllAudioScreen
import com.nutrino.audiocutter.presentation.Screens.AudioExtractorErrorScreen
import com.nutrino.audiocutter.presentation.Screens.AudioExtractorScreen
import com.nutrino.audiocutter.presentation.Screens.AudioExtractorSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioMergeErrorScreen
import com.nutrino.audiocutter.presentation.Screens.AudioMergeScreen
import com.nutrino.audiocutter.presentation.Screens.AudioMergeSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimmerScreen
import com.nutrino.audiocutter.presentation.Screens.BuyProPackageScreen
import com.nutrino.audiocutter.presentation.Screens.ConvertAudioFormatErrorScreen
import com.nutrino.audiocutter.presentation.Screens.ConvertAudioFormatScreen
import com.nutrino.audiocutter.presentation.Screens.ConvertAudioFormatSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.AudioSpeedSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioSpeedErrorScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllAudioForMergeScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllAudioForSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllSongsForConvertAudioFormatScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllSongsForMultiCropScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideoForAudioExtractScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideoScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideoForSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideosForMultiCropScreen
import com.nutrino.audiocutter.presentation.Screens.MultiCropAudioErrorScreen
import com.nutrino.audiocutter.presentation.Screens.MultiCropAudioScreen
import com.nutrino.audiocutter.presentation.Screens.MultiCropAudioSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.MultiCropVideoErrorScreen
import com.nutrino.audiocutter.presentation.Screens.MultiCropVideoScreen
import com.nutrino.audiocutter.presentation.Screens.MultiCropVideoSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.ProPackageScreen
import com.nutrino.audiocutter.presentation.Screens.RecordAudioErrorScreen
import com.nutrino.audiocutter.presentation.Screens.RecordAudioScreen
import com.nutrino.audiocutter.presentation.Screens.RecordAudioSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.RecentAudioPlayerScreen
import com.nutrino.audiocutter.presentation.Screens.RecentScreen
import com.nutrino.audiocutter.presentation.Screens.RecentVideoPlayerScreen
import com.nutrino.audiocutter.presentation.Screens.SelectFeatureScreen
import com.nutrino.audiocutter.presentation.Screens.ThemeSelectionScreen
import com.nutrino.audiocutter.presentation.Screens.VideoTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.VideoTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.VideoTrimmerScreen
import com.nutrino.audiocutter.presentation.Screens.VideoSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.VideoSpeedSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.VideoSpeedErrorScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideosForMuteScreen
import com.nutrino.audiocutter.presentation.Screens.MuteVideoScreen
import com.nutrino.audiocutter.presentation.Screens.MuteVideoSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.MuteVideoErrorScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllAudioForVolumeBoosterScreen
import com.nutrino.audiocutter.presentation.Screens.AudioVolumeBoosterScreen
import com.nutrino.audiocutter.presentation.Screens.AudioVolumeBoosterSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioVolumeBoosterErrorScreen


@OptIn(UnstableApi::class)
@Composable
fun MainApp() {
    val navcontroller = rememberNavController()
    NavHost(navController = navcontroller, startDestination = SELECTFEATURESCREEN) {

        composable <SELECTFEATURESCREEN>{
            SelectFeatureScreen(navController = navcontroller)
        }
        composable<RECENTSCREEN> {
            RecentScreen(navController = navcontroller)
        }
        composable<PROPACKAGESCREEN> {
            ProPackageScreen(navController = navcontroller)
        }
        composable<THEMESELECTIONSCREEN> {
            ThemeSelectionScreen(navController = navcontroller)
        }
        composable<BUYPROPACKAGESCREEN> { backstackEntry ->
            val data: BUYPROPACKAGESCREEN = backstackEntry.toRoute()
            BuyProPackageScreen(
                packageIdentifier = data.packageIdentifier,
                productId = data.productId,
                title = data.title,
                description = data.description,
                priceFormatted = data.priceFormatted,
                packageType = data.packageType
            )
        }
        composable<RECENTAUDIOPLAYERSCREEN> { backstackEntry ->
            val data: RECENTAUDIOPLAYERSCREEN = backstackEntry.toRoute()
            RecentAudioPlayerScreen(
                navController = navcontroller,
                outputUri = data.outputUri,
                outputName = data.outputName,
                inputName = data.inputName
            )
        }
        composable<RECENTVIDEOPLAYERSCREEN> { backstackEntry ->
            val data: RECENTVIDEOPLAYERSCREEN = backstackEntry.toRoute()
            RecentVideoPlayerScreen(
                navController = navcontroller,
                outputUri = data.outputUri,
                outputName = data.outputName,
                inputName = data.inputName
            )
        }
        composable<HOMESCREEN> {
            AllAudioScreen(navController = navcontroller)

        }
        composable<ALLVIDEOSCREEN> {
            GetAllVideoScreen(navController = navcontroller)

        }
        composable<AUDIOTRIMMERSCREEN> { backstackEntry ->
            val data: AUDIOTRIMMERSCREEN = backstackEntry.toRoute()
            AudioTrimmerScreen(
                navController = navcontroller,
                uri = data.uri,
                songDuration = data.songDuration,
                songName = data.songName
            )
        }
        composable<VIDEOTRIMMERSCREEN> { backstackEntry ->
            val data: VIDEOTRIMMERSCREEN = backstackEntry.toRoute()
            VideoTrimmerScreen(
                navController = navcontroller,
                uri = data.uri,
                videoDuration = data.videoDuration,
                videoName = data.videoName
            )
        }
        composable<AUDIOTRIMMERSUCCESSSTATE> {
            AudioTrimSuccessScreen(navController = navcontroller)
        }
        composable<AUDIOTRIMMERERRORSTATE> {
            AudioTrimErrorScreen(navController = navcontroller)

        }
        composable<VIDEOTRIMMERSUCCESSSTATE> {
            VideoTrimSuccessScreen(navController = navcontroller)
        }
        composable<VIDEOTRIMMERERRORSTATE> {
            VideoTrimErrorScreen(navController = navcontroller)

        }
        composable<ALLVIDEOFORAUDIOEXTRACTSCREEN> {
            GetAllVideoForAudioExtractScreen(navController = navcontroller)

        }
        composable<AUDIOEXTRACTORSCREEN> { backstackEntry ->
            val data: AUDIOEXTRACTORSCREEN = backstackEntry.toRoute()
            AudioExtractorScreen(
                navController = navcontroller,
                uri = data.uri,
                videoDuration = data.videoDuration,
                videoName = data.videoName
            )
        }
        composable<AUDIOEXTRACTORSUCCESSSTATE> {
            AudioExtractorSuccessScreen(navController = navcontroller)
        }
        composable<AUDIOEXTRACTORERRORSTATE> {
            AudioExtractorErrorScreen(navController = navcontroller)

        }
        composable<ALLAUDIOFORMERGESCREEN> {
            GetAllAudioForMergeScreen(navController = navcontroller)

        }
        composable<AUDIOMERGESCREEN> { backstackEntry ->
            val data: AUDIOMERGESCREEN = backstackEntry.toRoute()
            AudioMergeScreen(
                navController = navcontroller,
                uriList = data.uriList,
                songNames = data.songNames
            )
        }
        composable<AUDIOMERGESUCCESSSTATE> {
            AudioMergeSuccessScreen(navController = navcontroller)
        }
        composable<AUDIOMERGEERRORSTATE> {
            AudioMergeErrorScreen(navController = navcontroller)

        }

        composable<ALLSONGSFORMULTICROPSCREEN> {
            GetAllSongsForMultiCropScreen(navController = navcontroller)
        }

        composable<MULTICROPAUDIOSCREEN> { backstackEntry ->
            val data: MULTICROPAUDIOSCREEN = backstackEntry.toRoute()
            MultiCropAudioScreen(
                navController = navcontroller,
                uri = data.uri,
                songDuration = data.songDuration,
                songName = data.songName
            )
        }

        composable<MULTICROPAUDIOSUCCESSSTATE> {
            MultiCropAudioSuccessScreen(navController = navcontroller)
        }

        composable<MULTICROPAUDIOERRORSTATE> {
            MultiCropAudioErrorScreen(navController = navcontroller)
        }

        composable<ALLVIDEOSFORMULTICROPSCREEN> {
            GetAllVideosForMultiCropScreen(navController = navcontroller)
        }

        composable<MULTICROPVIDEOSCREEN> { backstackEntry ->
            val data: MULTICROPVIDEOSCREEN = backstackEntry.toRoute()
            MultiCropVideoScreen(
                navController = navcontroller,
                uri = data.uri,
                videoDuration = data.videoDuration,
                videoName = data.videoName
            )
        }

        composable<MULTICROPVIDEOSUCCESSSTATE> {
            MultiCropVideoSuccessScreen(navController = navcontroller)
        }

        composable<MULTICROPVIDEOERRORSTATE> {
            MultiCropVideoErrorScreen(navController = navcontroller)
        }

        composable<ALLSONGSFORCONVERTAUDIOFORMATSCREEN> {
            GetAllSongsForConvertAudioFormatScreen(navController = navcontroller)
        }

        composable<CONVERTAUDIOFORMATSCREEN> { backstackEntry ->
            val data: CONVERTAUDIOFORMATSCREEN = backstackEntry.toRoute()
            ConvertAudioFormatScreen(
                navController = navcontroller,
                uri = data.uri,
                songDuration = data.songDuration,
                songName = data.songName
            )
        }

        composable<CONVERTAUDIOFORMATSUCCESSSTATE> {
            ConvertAudioFormatSuccessScreen(navController = navcontroller)
        }

        composable<CONVERTAUDIOFORMATERRORSTATE> {
            ConvertAudioFormatErrorScreen(navController = navcontroller)
        }

        composable<RECORDAUDIOSCREEN> {
            RecordAudioScreen(navController = navcontroller)
        }

        composable<RECORDAUDIOSUCCESSSTATE> {
            RecordAudioSuccessScreen(navController = navcontroller)
        }

        composable<RECORDAUDIOERRORSTATE> {
            RecordAudioErrorScreen(navController = navcontroller)
        }

        composable<ALLVIDEOSFORSPEEDSCREEN> {
            GetAllVideoForSpeedScreen(navController = navcontroller)
        }

        composable<VIDEOSPEEDSCREEN> { backstackEntry ->
            val data: VIDEOSPEEDSCREEN = backstackEntry.toRoute()
            VideoSpeedScreen(
                navController = navcontroller,
                uri = data.uri,
                videoDuration = data.videoDuration,
                videoName = data.videoName
            )
        }

        composable<VIDEOSPEEDSUCCESSSTATE> {
            VideoSpeedSuccessScreen(navController = navcontroller)
        }

        composable<VIDEOSPEEDERRORSTATE> {
            VideoSpeedErrorScreen(navController = navcontroller)
        }

        composable<ALLAUDIOFORSPEEDSCREEN> {
            GetAllAudioForSpeedScreen(navController = navcontroller)
        }

        composable<AUDIOSPEEDSCREEN> { backstackEntry ->
            val data: AUDIOSPEEDSCREEN = backstackEntry.toRoute()
            AudioSpeedScreen(
                navController = navcontroller,
                uri = data.uri,
                songDuration = data.songDuration,
                songName = data.songName
            )
        }

        composable<AUDIOSPEEDSUCCESSSTATE> {
            AudioSpeedSuccessScreen(navController = navcontroller)
        }

        composable<AUDIOSPEEDERRORSTATE> {
            AudioSpeedErrorScreen(navController = navcontroller)
        }

        composable<ALLVIDEOSFORMUTESCREEN> {
            GetAllVideosForMuteScreen(navController = navcontroller)
        }

        composable<MUTEVIDEOSCREEN> { backstackEntry ->
            val data: MUTEVIDEOSCREEN = backstackEntry.toRoute()
            MuteVideoScreen(
                navController = navcontroller,
                uri = data.uri,
                videoDuration = data.videoDuration,
                videoName = data.videoName
            )
        }

        composable<MUTEVIDEOSUCCESSSTATE> {
            MuteVideoSuccessScreen(navController = navcontroller)
        }

        composable<MUTEVIDEOERRORSTATE> {
            MuteVideoErrorScreen(navController = navcontroller)
        }

        composable<ALLAUDIOFORVOLUMEBOOSTERSCREEN> {
            GetAllAudioForVolumeBoosterScreen(navController = navcontroller)
        }

        composable<AUDIOVOLUMEBOOSTERSCREEN> { backstackEntry ->
            val data: AUDIOVOLUMEBOOSTERSCREEN = backstackEntry.toRoute()
            AudioVolumeBoosterScreen(
                navController = navcontroller,
                uri = data.uri,
                songDuration = data.songDuration,
                songName = data.songName
            )
        }

        composable<AUDIOVOLUMEBOOSTERSUCCESSSTATE> {
            AudioVolumeBoosterSuccessScreen(navController = navcontroller)
        }

        composable<AUDIOVOLUMEBOOSTERRORSTATE> {
            AudioVolumeBoosterErrorScreen(navController = navcontroller)
        }

    }


}
