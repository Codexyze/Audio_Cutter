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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.nutrino.audiocutter.presentation.Navigation.VIDEOSPEEDERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.VIDEOSPEEDSUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.ViewModel.VideoSpeedViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import java.io.File
import java.util.Locale

private const val MIN_SPEED = 0.25f
private const val MAX_SPEED = 3.0f

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun VideoSpeedScreen(
    navController: NavController,
    videoSpeedViewModel: VideoSpeedViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    videoDuration: Long = 0,
    videoName: String = ""
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val outputName = rememberSaveable { mutableStateOf("Speed $videoName") }
    val adShown = rememberSaveable { mutableStateOf(false) }

    var speedValue by rememberSaveable { mutableStateOf(1f) }
    var speedText by rememberSaveable { mutableStateOf("1.00") }
    var speedDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    val speedOptions = rememberSaveable {
        (1..12).map { step -> step * 0.25f }
    }

    val videoSpeedState by videoSpeedViewModel.videoSpeedState.collectAsState()
    val upsertRecentState = recentViewModel.upsertRecentEntryState.collectAsState()

    LaunchedEffect(uri) {
        runCatching {
            val fileUri = Uri.fromFile(File(uri))
            mediaPlayerViewModel.initializePlayer(fileUri)
        }.onFailure {
            navController.navigate(VIDEOSPEEDERRORSTATE)
        }
    }

    LaunchedEffect(videoSpeedState.data) {
        if (videoSpeedState.data.isNotBlank()) {
            val outputDuration = if (speedValue > 0f) {
                (videoDuration / speedValue).toLong().coerceAtLeast(1L)
            } else {
                videoDuration
            }

            recentViewModel.resetUpsertRecentEntryState()
            recentViewModel.upsertRecentEntry(
                recentTable = RecentTable(
                    featureType = "Video Speed",
                    inputUri = uri,
                    outputUri = videoSpeedState.data,
                    date_modified = System.currentTimeMillis().toString(),
                    input_duration = videoDuration.toString(),
                    output_duration = outputDuration.toString(),
                    input_name = videoName,
                    output_name = outputName.value.trim(),
                    input_size = "",
                    output_size = "",
                    fileType = FileTypes.VIDEO_FILE
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
                videoSpeedState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    return@Column
                }

                videoSpeedState.error != null -> {
                    navController.navigate(VIDEOSPEEDERRORSTATE)
                }

                videoSpeedState.data.isNotBlank() && !adShown.value -> {
                    if (upsertRecentState.value.isLoading ||
                        (upsertRecentState.value.data.isBlank() && upsertRecentState.value.error == null)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        return@Column
                    }

                    adShown.value = true
                    val activity = context as? Activity
                    if (activity == null) {
                        Log.w("VideoSpeedScreen", "Context is not an Activity; navigating without ad")
                        navController.navigate(VIDEOSPEEDSUCCESSSTATE)
                    } else {
                        adsViewModel.requestAndShowAd(
                            activity = activity,
                            onAdDismissed = { navController.navigate(VIDEOSPEEDSUCCESSSTATE) },
                            onAdFailed = { navController.navigate(VIDEOSPEEDSUCCESSSTATE) }
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
                        label = { Text("Video File Name", color = MaterialTheme.colorScheme.primary) },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = speedText,
                            onValueChange = { newValue ->
                                speedText = newValue
                                val parsed = newValue.toFloatOrNull()
                                if (parsed != null && parsed in MIN_SPEED..MAX_SPEED) {
                                    speedValue = parsed
                                }
                            },
                            label = { Text("Custom Speed") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )

                        ExposedDropdownMenuBox(
                            expanded = speedDropdownExpanded,
                            onExpandedChange = { speedDropdownExpanded = !speedDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = String.format(Locale.getDefault(), "%.2fx", speedValue),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Preset") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = speedDropdownExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.primary)
                            )

                            ExposedDropdownMenu(
                                expanded = speedDropdownExpanded,
                                onDismissRequest = { speedDropdownExpanded = false }
                            ) {
                                speedOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = String.format(Locale.getDefault(), "%.2fx", option),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            speedValue = option
                                            speedText = String.format(Locale.getDefault(), "%.2f", option)
                                            speedDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Speed: ${String.format(Locale.getDefault(), "%.2f", speedValue)}x",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = speedValue,
                        onValueChange = {
                            speedValue = it
                            speedText = String.format(Locale.getDefault(), "%.2f", it)
                        },
                        valueRange = MIN_SPEED..MAX_SPEED,
                        modifier = Modifier.fillMaxWidth(0.88f)
                    )
                }


                item {
                    Button(
                        onClick = {
                            val parsedSpeed = speedText.toFloatOrNull()
                            if (outputName.value.isBlank()) {
                                Toast.makeText(context, "Please enter output filename", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (uri.isBlank() || videoDuration <= 0L) {
                                navController.navigate(VIDEOSPEEDERRORSTATE)
                                return@Button
                            }

                            if (parsedSpeed == null || parsedSpeed < MIN_SPEED || parsedSpeed > MAX_SPEED) {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid speed between 0.25 and 3.00",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val fileUri = Uri.fromFile(File(uri))
                            recentViewModel.resetUpsertRecentEntryState()
                            videoSpeedViewModel.changeVideoSpeed(
                                uri = fileUri,
                                speed = parsedSpeed,
                                filename = outputName.value.trim()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.76f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Apply Speed", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}
