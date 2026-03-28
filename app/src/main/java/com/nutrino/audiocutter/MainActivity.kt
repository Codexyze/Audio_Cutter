package com.nutrino.audiocutter

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nutrino.audiocutter.notification.createMusicNotificationChannel
import com.nutrino.audiocutter.presentation.Navigation.MainApp
import com.nutrino.audiocutter.ui.theme.AudioCutterTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Intentionally no-op: if declined, we leave the user alone.
            markNotificationPromptShown()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()

        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createMusicNotificationChannel(this)
        requestNotificationPermissionIfNeeded()

        setContent {
            AudioCutterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                         MainApp()
                    }

                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val alreadyGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted || isNotificationPromptShown()) return

        AlertDialog.Builder(this)
            .setTitle("Stay updated")
            .setMessage("Allow notifications so we can send updates, discounts, and upcoming changes.")
            .setCancelable(true)
            .setPositiveButton("Allow") { _, _ ->
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Not now") { _, _ ->
                markNotificationPromptShown()
            }
            .setOnCancelListener {
                markNotificationPromptShown()
            }
            .show()
    }

    private fun isNotificationPromptShown(): Boolean {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(KEY_NOTIFICATION_PROMPT_SHOWN, false)
    }

    private fun markNotificationPromptShown() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit {
                putBoolean(KEY_NOTIFICATION_PROMPT_SHOWN, true)
            }
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_NOTIFICATION_PROMPT_SHOWN = "notification_prompt_shown"
    }
}
