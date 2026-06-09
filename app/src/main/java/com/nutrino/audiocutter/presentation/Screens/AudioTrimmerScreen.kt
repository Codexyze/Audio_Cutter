package com.nutrino.audiocutter.presentation.Screens


import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.presentation.Navigation.AUDIOTRIMMERERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.AUDIOTRIMMERSUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.AudioTrimViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import android.util.Log
import com.nutrino.audiocutter.Constants.FileTypes
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun AudioTrimmerScreen(
    navController: NavController,
    audioTrimViewModel: AudioTrimViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    songDuration: Long = 0,
    songName: String = "",
) {
    val context = LocalContext.current

    // Helper function to format time from seconds to MM:SS or HH:MM:SS
    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, secs)
        }
    }

    // Helper function to parse time string (HH:MM:SS or MM:SS or SS) to milliseconds
    fun parseTimeToMillis(timeStr: String): Long? {
        return try {
            val parts = timeStr.split(":").map { it.trim().toLong() }
            when (parts.size) {
                1 -> parts[0] * 1000L // SS
                2 -> (parts[0] * 60 + parts[1]) * 1000L // MM:SS
                3 -> (parts[0] * 3600 + parts[1] * 60 + parts[2]) * 1000L // HH:MM:SS
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    // 🎚️ Range slider for start and end time selection
    val startValue = rememberSaveable { mutableStateOf(0f) }
    val endValue = rememberSaveable { mutableStateOf(songDuration.toFloat()) }

    // Manual time entry states (HH:MM:SS or MM:SS)
    val startText = rememberSaveable { mutableStateOf(formatTime(0)) }
    val endText = rememberSaveable { mutableStateOf(formatTime(songDuration / 1000)) }

    val filename = rememberSaveable { mutableStateOf("file-name") }

    // Flag to ensure ad only attempts once per successful trim
    val adShown = rememberSaveable { mutableStateOf(false) }

    // ✅ Convert slider values to seconds
    val startTime = startValue.value.toLong()
    val endTime = endValue.value.toLong()

    val audioTrimState = audioTrimViewModel.audioTrimmerState.collectAsState()
    val upsertRecentState = recentViewModel.upsertRecentEntryState.collectAsState()

    LaunchedEffect(uri) {
        mediaPlayerViewModel.initializePlayer(uri.toUri())
    }

    LaunchedEffect(audioTrimState.value.data) {
        if (audioTrimState.value.data.isNotBlank()) {
            recentViewModel.resetUpsertRecentEntryState()
            recentViewModel.upsertRecentEntry(
                recentTable = RecentTable(
                    featureType = "Audio Trimmer",
                    inputUri = uri,
                    outputUri = audioTrimState.value.data,
                    date_modified = System.currentTimeMillis().toString(),
                    input_duration = songDuration.toString(),
                    output_duration = (endTime - startTime).toString(),
                    input_name = songName,
                    output_name = filename.value.trim(),
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
                    .height(300.dp)
            )

        when {
            audioTrimState.value.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(24.dp)
                )
                return@Column
            }
            audioTrimState.value.error != null -> {
               navController.navigate(AUDIOTRIMMERERRORSTATE)
            }
            audioTrimState.value.data.isNotBlank() && !adShown.value -> {
                if (upsertRecentState.value.isLoading ||
                    (upsertRecentState.value.data.isBlank() && upsertRecentState.value.error == null)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                    return@Column
                }

                // Successful trim; attempt to show interstitial ad once
                adShown.value = true // prevent re-entry
                val activity = context as? Activity
                if (activity == null) {
                    Log.w("AudioTrimmerScreen", "Context is not an Activity; navigating without ad")
                    navController.navigate(AUDIOTRIMMERSUCCESSSTATE)
                } else {
                    // Unified ad request (show if ready, otherwise load then show) and always navigate after
                    adsViewModel.requestAndShowAd(
                        activity = activity,
                        onAdDismissed = { navController.navigate(AUDIOTRIMMERSUCCESSSTATE) },
                        onAdFailed = { navController.navigate(AUDIOTRIMMERSUCCESSSTATE) }
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
                    value = filename.value,
                    onValueChange = { filename.value = it },
                    label = { Text("Audio File Name", color = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(0.85f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
            }

            // 🎚️ RANGE SLIDER FOR TRIMMING
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Select Trim Range",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = startValue.value..endValue.value,
                    onValueChange = {
                        startValue.value = it.start
                        endValue.value = it.endInclusive
                        // Update text fields when slider moves
                        startText.value = formatTime(it.start.toLong() / 1000)
                        endText.value = formatTime(it.endInclusive.toLong() / 1000)
                    },
                    valueRange = 0f..songDuration.toFloat(),
                    steps = 0, // Always allow slider to work, even for songDuration = 0
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    OutlinedTextField(
                        value = startText.value,
                        onValueChange = { newValue ->
                            startText.value = newValue
                            val parsed = parseTimeToMillis(newValue)
                            if (parsed != null) {
                                val milli = parsed.toFloat()
                                if (milli >= 0 && milli < endValue.value) {
                                    startValue.value = milli
                                }
                            }
                        },
                        label = { Text("Start (HH:MM:SS)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = endText.value,
                        onValueChange = { newValue ->
                            endText.value = newValue
                            val parsed = parseTimeToMillis(newValue)
                            if (parsed != null) {
                                val milli = parsed.toFloat()
                                if (milli > startValue.value && milli <= songDuration) {
                                    endValue.value = milli
                                }
                            }
                        },
                        label = { Text("End (HH:MM:SS)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Text(
                        text = "Start: ${formatTime(startTime / 1000)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "End: ${formatTime(endTime / 1000)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val isStartValid = startTime >= 0 // Allow 0
                        val isEndValid = endTime > 0
                        val isRangeValid = startTime < endTime && endTime <= songDuration

                        if (isStartValid && isEndValid && isRangeValid && filename.value.isNotBlank()) {
                            recentViewModel.resetUpsertRecentEntryState()

                            // IMPORTANT: RangeSlider values already represent milliseconds; do NOT multiply by 1000
                            audioTrimViewModel.audioTrimmerState(
                                context = context,
                                uri = uri.toUri(),
                                startTime = startTime,
                                endTime = endTime,
                                filename = filename.value.trim()
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter valid start/end time or file name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Trim Audio", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        }

        // Banner Ad at bottom
        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}
