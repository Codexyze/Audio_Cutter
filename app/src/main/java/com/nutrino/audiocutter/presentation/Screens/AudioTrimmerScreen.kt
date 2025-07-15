package com.nutrino.audiocutter.presentation.Screens

import android.os.Build
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.nutrino.audiocutter.presentation.ViewModel.AudioTrimViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel


@UnstableApi
@Composable
fun AudioTrimmerScreen(
    navController: NavController,
    audioTrimViewModel: AudioTrimViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    uri: String = "",
    songDuration: Long = 0,
    songName: String = ""
) {
    val context = LocalContext.current

    // 🔁 Inputs split into H:M:S for start and end
    val startHours = rememberSaveable { mutableStateOf("") }
    val startMinutes = rememberSaveable { mutableStateOf("") }
    val startSeconds = rememberSaveable { mutableStateOf("") }

    val endHours = rememberSaveable { mutableStateOf("") }
    val endMinutes = rememberSaveable { mutableStateOf("") }
    val endSeconds = rememberSaveable { mutableStateOf("") }

    val filename = rememberSaveable { mutableStateOf("") }

    // ✅ Combine HMS into total seconds
    val startTime = (
            (startHours.value.toLongOrNull() ?: 0L) * 3600 +
                    (startMinutes.value.toLongOrNull() ?: 0L) * 60 +
                    (startSeconds.value.toLongOrNull() ?: 0L)
            )

    val endTime = (
            (endHours.value.toLongOrNull() ?: 0L) * 3600 +
                    (endMinutes.value.toLongOrNull() ?: 0L) * 60 +
                    (endSeconds.value.toLongOrNull() ?: 0L)
            )

    val audioTrimState = audioTrimViewModel.audioTrimmerState.collectAsState()

    LaunchedEffect(uri) {
        mediaPlayerViewModel.initializePlayer(uri.toUri())
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerViewModel.releasePlayer()
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
                Text("Error: ${audioTrimState.value.error}")
                return@Column
            }
            audioTrimState.value.data != null -> {
                // success UI (optional)
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

            // 🕓 START TIME FIELDS
            item {
                Text("Start Time (H:M:S)", color = MaterialTheme.colorScheme.primary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    OutlinedTextField(
                        value = startHours.value,
                        onValueChange = { startHours.value = it },
                        label = { Text("HH") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startMinutes.value,
                        onValueChange = { startMinutes.value = it },
                        label = { Text("MM") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startSeconds.value,
                        onValueChange = { startSeconds.value = it },
                        label = { Text("SS") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ⏳ END TIME FIELDS
            item {
                Text("End Time (H:M:S)", color = MaterialTheme.colorScheme.primary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    OutlinedTextField(
                        value = endHours.value,
                        onValueChange = { endHours.value = it },
                        label = { Text("HH") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endMinutes.value,
                        onValueChange = { endMinutes.value = it },
                        label = { Text("MM") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endSeconds.value,
                        onValueChange = { endSeconds.value = it },
                        label = { Text("SS") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val isStartValid = startTime > 0
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
                                "Please enter valid start/end time",
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
