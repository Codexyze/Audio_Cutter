package com.nutrino.audiocutter.presentation.Navigation

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nutrino.audiocutter.presentation.Screens.AllAudioScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimErrorScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimSuccessScreen
import com.nutrino.audiocutter.presentation.Screens.AudioTrimmerScreen


@OptIn(UnstableApi::class)
@Composable
fun MainApp() {
    val navcontroller = rememberNavController()
    NavHost(navController = navcontroller, startDestination = HOMESCREEN) {
        composable<HOMESCREEN> {
            AllAudioScreen(navController = navcontroller)

        }
        composable<AUDIOTRIMMERSCREEN> {backstackEntry->
            val data: AUDIOTRIMMERSCREEN = backstackEntry.toRoute()
            AudioTrimmerScreen(navController = navcontroller, uri = data.uri,
                songDuration = data.songDuration, songName = data.songName
            )
        }
        composable<AUDIOTRIMMERSUCCESSSTATE> {
            AudioTrimSuccessScreen(navController = navcontroller)
        }
        composable<AUDIOTRIMMERERRORSTATE> {
            AudioTrimErrorScreen(navController = navcontroller)

        }


    }


}