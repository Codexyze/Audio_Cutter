package com.nutrino.audiocutter.presentation.Screens

import android.app.Activity
import android.util.Log

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.nutrino.audiocutter.data.DataClass.CropSegment
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MultiCropViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import com.nutrino.audiocutter.presentation.Navigation.MULTICROPAUDIOERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.MULTICROPAUDIOSUCCESSSTATE
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MultiCropAudioScreen(
    navController: NavController,
    multiCropViewModel: MultiCropViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    songDuration: Long = 0,
    songName: String = ""
) {
    val context = LocalContext.current

    // Segment selection states
    val startValue = rememberSaveable { mutableStateOf(0f) }
    val endValue = rememberSaveable { mutableStateOf(songDuration.toFloat()) }

    // Filename state
    val filename = rememberSaveable { mutableStateOf("") }

    // Collected segments from ViewModel
    val segments by multiCropViewModel.segments.collectAsState()

    // Multi-crop state
    val multiCropState by multiCropViewModel.multiCropAudioState.collectAsState()

    // Flag to ensure ad only attempts once
    val adShown = rememberSaveable { mutableStateOf(false) }

    // Playing segment state
    var playingSegmentIndex by rememberSaveable { mutableStateOf(-1) }
    var isPlayingSegment by rememberSaveable { mutableStateOf(false) }
    var previewEndTime by rememberSaveable { mutableStateOf(0L) }

    // Editing segment state
    var editingSegmentIndex by rememberSaveable { mutableStateOf(-1) }
    var editStartValue by rememberSaveable { mutableStateOf(0f) }
    var editEndValue by rememberSaveable { mutableStateOf(0f) }

    // Helper function to format time
    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, secs)
        }
    }

    // Initialize player
    LaunchedEffect(uri) {
        mediaPlayerViewModel.initializePlayer(uri.toUri())
    }

    // Monitor player position and pause at segment end
    LaunchedEffect(isPlayingSegment, previewEndTime) {
        if (isPlayingSegment && previewEndTime > 0) {
            while (isPlayingSegment) {
                val player = mediaPlayerViewModel.getPlayer()
                val currentPosition = player.currentPosition

                if (currentPosition >= previewEndTime) {
                    player.pause()
                    isPlayingSegment = false
                    playingSegmentIndex = -1
                    previewEndTime = 0L
                    break
                }

                kotlinx.coroutines.delay(100) // Check every 100ms
            }
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerViewModel.getPlayer().pause()
            multiCropViewModel.clearSegments()
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
            // ExoPlayer View
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
                    .height(250.dp)
            )

            // Handle states
            when {
                multiCropState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                    return@Column
                }
                multiCropState.error != null -> {
                    navController.navigate(MULTICROPAUDIOERRORSTATE)
                }
                multiCropState.data.isNotBlank() && !adShown.value -> {
                    adShown.value = true
                    val activity = context as? Activity
                    if (activity == null) {
                        Log.w("MultiCropAudioScreen", "Context is not an Activity")
                        navController.navigate(MULTICROPAUDIOSUCCESSSTATE)
                    } else {
                        adsViewModel.requestAndShowAd(
                            activity = activity,
                            onAdDismissed = { navController.navigate(MULTICROPAUDIOSUCCESSSTATE) },
                            onAdFailed = { navController.navigate(MULTICROPAUDIOSUCCESSSTATE) }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Song name display
                item {
                    Text(
                        text = songName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }


                // Filename input
                item {
                    OutlinedTextField(
                        value = filename.value,
                        onValueChange = { filename.value = it },
                        label = { Text("Save As", color = MaterialTheme.colorScheme.primary) },
                        placeholder = { Text("Enter filename for merged audio", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(0.9f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                    )
                }

                // Segment selector section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Select Segment Range",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Range Slider
                            RangeSlider(
                                value = startValue.value..endValue.value,
                                onValueChange = {
                                    startValue.value = it.start
                                    endValue.value = it.endInclusive
                                },
                                valueRange = 0f..songDuration.toFloat(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Time display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Start: ${formatTime(startValue.value.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "End: ${formatTime(endValue.value.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Preview and Add buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Preview segment button
                                OutlinedButton(
                                    onClick = {
                                        val player = mediaPlayerViewModel.getPlayer()
                                        player.seekTo(startValue.value.toLong())
                                        player.play()
                                        isPlayingSegment = true
                                        playingSegmentIndex = -1
                                        previewEndTime = endValue.value.toLong()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.PlayArrow, "Preview", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview")
                                }

                                // Add segment button
                                Button(
                                    onClick = {
                                        val start = startValue.value.toLong()
                                        val end = endValue.value.toLong()
                                        if (start < end) {
                                            multiCropViewModel.addSegment(
                                                CropSegment(start = start, end = end)
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.Add, "Add", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Segment")
                                }
                            }
                        }
                    }
                }

                // Selected segments list
                if (segments.isNotEmpty()) {
                    item {
                        Text(
                            text = "Selected Segments (${segments.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // List of selected segments
                itemsIndexed(segments) { index, segment ->
                    SegmentCard(
                        index = index,
                        segment = segment,
                        isPlaying = playingSegmentIndex == index && isPlayingSegment,
                        onPlayClick = {
                            val player = mediaPlayerViewModel.getPlayer()
                            if (playingSegmentIndex == index && isPlayingSegment) {
                                player.pause()
                                isPlayingSegment = false
                                previewEndTime = 0L
                            } else {
                                player.seekTo(segment.start ?: 0L)
                                player.play()
                                isPlayingSegment = true
                                playingSegmentIndex = index
                                previewEndTime = segment.end ?: 0L
                            }
                        },
                        onEditClick = {
                            editingSegmentIndex = index
                            editStartValue = (segment.start ?: 0L).toFloat()
                            editEndValue = (segment.end ?: songDuration).toFloat()
                        },
                        onDeleteClick = {
                            multiCropViewModel.removeSegment(index)
                            if (playingSegmentIndex == index) {
                                isPlayingSegment = false
                                playingSegmentIndex = -1
                                previewEndTime = 0L
                            }
                        },
                        formatTime = ::formatTime
                    )
                }

                // Basic introduction at bottom
                if (segments.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Select multiple segments from the audio and merge them into one file. Use the slider to choose start and end points, preview, then add to your list.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                // Process button
                if (segments.isNotEmpty()) {
                    item {
                        Button(
                            onClick = {
                                if (filename.value.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Please enter a filename",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    multiCropViewModel.multiCropAudio(
                                        context = context,
                                        uri = uri,
                                        segments = segments,
                                        filename = filename.value
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(56.dp),
                            enabled = true,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Crop & Merge Audio",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Edit Segment Dialog
        if (editingSegmentIndex >= 0 && editingSegmentIndex < segments.size) {
            AlertDialog(
                onDismissRequest = { editingSegmentIndex = -1 },
                title = {
                    Text(
                        text = "Edit Segment ${editingSegmentIndex + 1}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Range Slider for precise editing
                        Text(
                            "Adjust segment range",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        RangeSlider(
                            value = editStartValue..editEndValue,
                            onValueChange = {
                                editStartValue = it.start
                                editEndValue = it.endInclusive
                            },
                            valueRange = 0f..songDuration.toFloat(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time display with precise values
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Start: ${formatTime(editStartValue.toLong())}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "End: ${formatTime(editEndValue.toLong())}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Duration: ${formatTime(editEndValue.toLong() - editStartValue.toLong())}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Preview button for edited segment
                        OutlinedButton(
                            onClick = {
                                val player = mediaPlayerViewModel.getPlayer()
                                player.seekTo(editStartValue.toLong())
                                player.play()
                                isPlayingSegment = true
                                playingSegmentIndex = -1
                                previewEndTime = editEndValue.toLong()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, "Preview", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Preview Changes")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Update the segment
                            val updatedSegments = segments.toMutableList()
                            updatedSegments[editingSegmentIndex] = CropSegment(
                                start = editStartValue.toLong(),
                                end = editEndValue.toLong()
                            )
                            // Clear and re-add all segments
                            multiCropViewModel.clearSegments()
                            updatedSegments.forEach { segment ->
                                multiCropViewModel.addSegment(segment)
                            }
                            editingSegmentIndex = -1
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingSegmentIndex = -1 }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Banner Ad at bottom
        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun SegmentCard(
    index: Int,
    segment: CropSegment,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    formatTime: (Long) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Segment number badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Time info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Segment ${index + 1}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatTime(segment.start ?: 0L)} - ${formatTime(segment.end ?: 0L)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Duration: ${formatTime((segment.end ?: 0L) - (segment.start ?: 0L))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Play/Pause button
            IconButton(
                onClick = onPlayClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Edit button
            IconButton(
                onClick = onEditClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Delete button
            IconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

