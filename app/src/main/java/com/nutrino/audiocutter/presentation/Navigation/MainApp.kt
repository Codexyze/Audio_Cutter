package com.nutrino.audiocutter.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutrino.audiocutter.presentation.Screens.AllAudioScreen

@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val navcontroller = rememberNavController()
    NavHost(navController = navcontroller, startDestination = HOMESCREEN) {
        composable<HOMESCREEN> {
            AllAudioScreen()

        }

    }


}