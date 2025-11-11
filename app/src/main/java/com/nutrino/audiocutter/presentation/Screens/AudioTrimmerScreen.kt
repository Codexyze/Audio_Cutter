package com.nutrino.audiocutter.presentation.Screens


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
import com.nutrino.audiocutter.presentation.Navigation.AUDIOTRIMMERERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.AUDIOTRIMMERSUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AudioTrimViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun AudioTrimmerScreen(
    navController: NavController,
    audioTrimViewModel: AudioTrimViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    uri: String = "",
    songDuration: Long = 0,
) {
    val context = LocalContext.current

    // 🎚️ Range slider for start and end time selection
    val startValue = rememberSaveable { mutableStateOf(0f) }
    val endValue = rememberSaveable { mutableStateOf(songDuration.toFloat()) }

    val filename = rememberSaveable { mutableStateOf("") }

    // ✅ Convert slider values to seconds
    val startTime = startValue.value.toLong()
    val endTime = endValue.value.toLong()

    val audioTrimState = audioTrimViewModel.audioTrimmerState.collectAsState()

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

    LaunchedEffect(uri) {
        mediaPlayerViewModel.initializePlayer(uri.toUri())
    }



    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerViewModel.getPlayer().pause()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
            audioTrimState.value.data.isNotBlank() -> {
              navController.navigate(AUDIOTRIMMERSUCCESSSTATE)
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
                    },
                    valueRange = 0f..songDuration.toFloat(),
                    steps = 0, // Always allow slider to work, even for songDuration = 0
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
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
                            audioTrimViewModel.audioTrimmerState(
                                context = context,
                                uri = uri.toUri(),
                                startTime = startTime * 1000,
                                endTime = endTime * 1000,
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
}
