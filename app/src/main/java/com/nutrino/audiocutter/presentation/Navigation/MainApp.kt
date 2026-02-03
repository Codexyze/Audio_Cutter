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
import com.nutrino.audiocutter.presentation.Screens.AudioTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimmerScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideoForAudioExtractScreen
import com.nutrino.audiocutter.presentation.Screens.GetAllVideoScreen
import com.nutrino.audiocutter.presentation.Screens.SelectFeatureScreen
import com.nutrino.audiocutter.presentation.Screens.VideoTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.VideoTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.VideoTrimmerScreen


@OptIn(UnstableApi::class)
@Composable
fun MainApp() {
    val navcontroller = rememberNavController()
    NavHost(navController = navcontroller, startDestination = SELECTFEATURESCREEN) {

        composable <SELECTFEATURESCREEN>{
            SelectFeatureScreen(navController = navcontroller)
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
                songDuration = data.songDuration
            )
        }
        composable<VIDEOTRIMMERSCREEN> { backstackEntry ->
            val data: VIDEOTRIMMERSCREEN = backstackEntry.toRoute()
            VideoTrimmerScreen(
                navController = navcontroller,
                uri = data.uri,
                videoDuration = data.videoDuration
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
                videoDuration = data.videoDuration
            )
        }
        composable<AUDIOEXTRACTORSUCCESSSTATE> {
            AudioExtractorSuccessScreen(navController = navcontroller)
        }
        composable<AUDIOEXTRACTORERRORSTATE> {
            AudioExtractorErrorScreen(navController = navcontroller)

        }


    }


}

