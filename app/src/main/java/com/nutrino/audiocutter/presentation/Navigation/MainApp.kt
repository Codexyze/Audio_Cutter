package com.nutrino.audiocutter.presentation.Navigation

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nutrino.audiocutter.presentation.Screens.audioextractor.AudioExtractorErrorScreen
import com.nutrino.audiocutter.presentation.Screens.audioextractor.AudioExtractorScreen
import com.nutrino.audiocutter.presentation.Screens.audioextractor.AudioExtractorSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.audioextractor.GetAllVideoForAudioExtractScreen
import com.nutrino.audiocutter.presentation.Screens.audiomerge.AudioMergeErrorScreen
import com.nutrino.audiocutter.presentation.Screens.audiomerge.AudioMergeScreen
import com.nutrino.audiocutter.presentation.Screens.audiomerge.AudioMergeSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.audiomerge.GetAllAudioForMergeScreen
import com.nutrino.audiocutter.presentation.Screens.audiospeed.AudioSpeedErrorScreen
import com.nutrino.audiocutter.presentation.Screens.audiospeed.AudioSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.audiospeed.AudioSpeedSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.audiospeed.GetAllAudioForSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.audiotrimmer.AudioTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.audiotrimmer.AudioTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.audiotrimmer.AudioTrimmerScreen
import com.nutrino.audiocutter.presentation.Screens.audiovolumebooster.AudioVolumeBoosterErrorScreen
import com.nutrino.audiocutter.presentation.Screens.audiovolumebooster.AudioVolumeBoosterScreen
import com.nutrino.audiocutter.presentation.Screens.audiovolumebooster.AudioVolumeBoosterSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.audiovolumebooster.GetAllAudioForVolumeBoosterScreen
import com.nutrino.audiocutter.presentation.Screens.convertaudioformat.ConvertAudioFormatErrorScreen
import com.nutrino.audiocutter.presentation.Screens.convertaudioformat.ConvertAudioFormatScreen
import com.nutrino.audiocutter.presentation.Screens.convertaudioformat.ConvertAudioFormatSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.convertaudioformat.GetAllSongsForConvertAudioFormatScreen
import com.nutrino.audiocutter.presentation.Screens.home.SelectFeatureScreen
import com.nutrino.audiocutter.presentation.Screens.home.ThemeSelectionScreen
import com.nutrino.audiocutter.presentation.Screens.mediapicker.AllAudioScreen
import com.nutrino.audiocutter.presentation.Screens.mediapicker.GetAllVideoScreen
import com.nutrino.audiocutter.presentation.Screens.multicropaudio.GetAllSongsForMultiCropScreen
import com.nutrino.audiocutter.presentation.Screens.multicropaudio.MultiCropAudioErrorScreen
import com.nutrino.audiocutter.presentation.Screens.multicropaudio.MultiCropAudioScreen
import com.nutrino.audiocutter.presentation.Screens.multicropaudio.MultiCropAudioSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.multicropvideo.GetAllVideosForMultiCropScreen
import com.nutrino.audiocutter.presentation.Screens.multicropvideo.MultiCropVideoErrorScreen
import com.nutrino.audiocutter.presentation.Screens.multicropvideo.MultiCropVideoScreen
import com.nutrino.audiocutter.presentation.Screens.multicropvideo.MultiCropVideoSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.mutevideo.GetAllVideosForMuteScreen
import com.nutrino.audiocutter.presentation.Screens.mutevideo.MuteVideoErrorScreen
import com.nutrino.audiocutter.presentation.Screens.mutevideo.MuteVideoScreen
import com.nutrino.audiocutter.presentation.Screens.mutevideo.MuteVideoSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.pro.BuyProPackageScreen
import com.nutrino.audiocutter.presentation.Screens.pro.ProPackageScreen
import com.nutrino.audiocutter.presentation.Screens.recent.RecentAudioPlayerScreen
import com.nutrino.audiocutter.presentation.Screens.recent.RecentScreen
import com.nutrino.audiocutter.presentation.Screens.recent.RecentVideoPlayerScreen
import com.nutrino.audiocutter.presentation.Screens.recordaudio.RecordAudioErrorScreen
import com.nutrino.audiocutter.presentation.Screens.recordaudio.RecordAudioScreen
import com.nutrino.audiocutter.presentation.Screens.recordaudio.RecordAudioSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.videospeed.GetAllVideoForSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.videospeed.VideoSpeedErrorScreen
import com.nutrino.audiocutter.presentation.Screens.videospeed.VideoSpeedScreen
import com.nutrino.audiocutter.presentation.Screens.videospeed.VideoSpeedSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.videotrimmer.VideoTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.videotrimmer.VideoTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.videotrimmer.VideoTrimmerScreen
import com.nutrino.audiocutter.presentation.ViewModel.AnalyticsViewModel


@OptIn(UnstableApi::class)
@Composable
fun MainApp(
    analyticsViewModel: AnalyticsViewModel = hiltViewModel()
) {
    val navcontroller = rememberNavController()

    DisposableEffect(navcontroller) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route?.substringBefore("?") ?: "Unknown"
            val screenName = route.substringAfterLast(".")
            analyticsViewModel.screenViewLog(screenName)
        }
        navcontroller.addOnDestinationChangedListener(listener)
        onDispose {
            navcontroller.removeOnDestinationChangedListener(listener)
        }
    }

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
