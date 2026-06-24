package com.nutrino.audiocutter.presentation.Screens

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.FileTypes
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.presentation.Navigation.AUDIOVOLUMEBOOSTERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.AUDIOVOLUMEBOOSTERSUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.ViewModel.AudioVolumeBoosterViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun AudioVolumeBoosterScreen(
    navController: NavController,
    audioVolumeBoosterViewModel: AudioVolumeBoosterViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    songDuration: Long = 0,
    songName: String = ""
) {
    val context = LocalContext.current

    val outputName = rememberSaveable { mutableStateOf("Boosted $songName") }
    val adShown = rememberSaveable { mutableStateOf(false) }

    var volumeFactor by rememberSaveable { mutableStateOf(1f) } // 1.0f = 100%

    val audioVolumeState by audioVolumeBoosterViewModel.audioVolumeBoosterState.collectAsState()
    val upsertRecentState = recentViewModel.upsertRecentEntryState.collectAsState()

    LaunchedEffect(uri) {
        runCatching {
            val fileUri = Uri.fromFile(File(uri))
            mediaPlayerViewModel.initializePlayer(fileUri)
        }.onFailure {
            navController.navigate(AUDIOVOLUMEBOOSTERRORSTATE)
        }
    }

    LaunchedEffect(audioVolumeState.data) {
        if (audioVolumeState.data.isNotBlank()) {
            recentViewModel.resetUpsertRecentEntryState()
            recentViewModel.upsertRecentEntry(
                recentTable = RecentTable(
                    featureType = "Audio Volume Booster",
                    inputUri = uri,
                    outputUri = audioVolumeState.data,
                    date_modified = System.currentTimeMillis().toString(),
                    input_duration = songDuration.toString(),
                    output_duration = songDuration.toString(),
                    input_name = songName,
                    output_name = outputName.value.trim(),
                    input_size = "",
                    output_size = "",
                    fileType = FileTypes.AUDIO_FILE
                )
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerViewModel.getPlayer().pause()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = mediaPlayerViewModel.getPlayer()
                        useController = true
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            when {
                audioVolumeState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    return@Column
                }

                audioVolumeState.error != null -> {
                    navController.navigate(AUDIOVOLUMEBOOSTERRORSTATE)
                }

                audioVolumeState.data.isNotBlank() && !adShown.value -> {
                    if (upsertRecentState.value.isLoading ||
                        (upsertRecentState.value.data.isBlank() && upsertRecentState.value.error == null)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        return@Column
                    }

                    adShown.value = true
                    val activity = context as? Activity
                    if (activity == null) {
                        navController.navigate(AUDIOVOLUMEBOOSTERSUCCESSSTATE)
                    } else {
                        adsViewModel.requestAndShowAd(
                            activity = activity,
                            onAdDismissed = { navController.navigate(AUDIOVOLUMEBOOSTERSUCCESSSTATE) },
                            onAdFailed = { navController.navigate(AUDIOVOLUMEBOOSTERSUCCESSSTATE) }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = outputName.value,
                        onValueChange = { outputName.value = it },
                        label = { Text("Audio File Name", color = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth(0.88f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        singleLine = true
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Volume: ${(volumeFactor * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = volumeFactor,
                            onValueChange = { volumeFactor = it },
                            valueRange = 0f..2f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0%", style = MaterialTheme.typography.bodySmall)
                            Text("100%", style = MaterialTheme.typography.bodySmall)
                            Text("200%", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (outputName.value.isBlank()) {
                                Toast.makeText(context, "Please enter output filename", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val fileUri = Uri.fromFile(File(uri))
                            audioVolumeBoosterViewModel.boostAudioVolume(
                                uri = fileUri,
                                volumeFactor = volumeFactor,
                                filename = outputName.value.trim()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.76f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Apply Volume", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}
