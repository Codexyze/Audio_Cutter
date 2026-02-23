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
import com.nutrino.audiocutter.presentation.ViewModel.MultiCropVideoViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import com.nutrino.audiocutter.presentation.Navigation.MULTICROPVIDEOERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.MULTICROPVIDEOSUCCESSSTATE
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MultiCropVideoScreen(
    navController: NavController,
    multiCropViewModel: MultiCropVideoViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    videoDuration: Long = 0,
    videoName: String = ""
) {
    val context = LocalContext.current

    // Segment selection states
    val startValue = rememberSaveable { mutableStateOf(0f) }
    val endValue = rememberSaveable { mutableStateOf(videoDuration.toFloat()) }

    // Filename state
    val filename = rememberSaveable { mutableStateOf("") }

    // Collected segments from ViewModel
    val segments by multiCropViewModel.segments.collectAsState()

    // Multi-crop state
    val multiCropState by multiCropViewModel.multiCropVideoState.collectAsState()


    // Playing segment state
    var playingSegmentIndex by rememberSaveable { mutableStateOf(-1) }
    var isPlayingSegment by rememberSaveable { mutableStateOf(false) }
    var previewEndTime by rememberSaveable { mutableStateOf(0L) }

    // Editing segment state
    var editingSegmentIndex by rememberSaveable { mutableStateOf(-1) }
    var editStartValue by rememberSaveable { mutableStateOf(0f) }
    var editEndValue by rememberSaveable { mutableStateOf(0f) }

    // Error state for video loading
    var videoLoadError by rememberSaveable { mutableStateOf(false) }

    // Beta warning dialog state
    var showBetaWarning by rememberSaveable { mutableStateOf(true) }

    // Flag to ensure ad only attempts once
    val adShown = rememberSaveable { mutableStateOf(false) }

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
        try {
            mediaPlayerViewModel.initializePlayer(uri.toUri())
            videoLoadError = false
        } catch (e: Exception) {
            android.util.Log.e("MultiCropVideo", "Error loading video: ${e.message}")
            videoLoadError = true
        }
    }

    // Monitor player errors
    LaunchedEffect(Unit) {
        val player = mediaPlayerViewModel.getPlayer()
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                android.util.Log.e("MultiCropVideo", "Playback error: ${error.message}")
                videoLoadError = true
            }
        }
        player.addListener(listener)
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

    // Handle success/error navigation
    LaunchedEffect(multiCropState.data, multiCropState.error) {
        if (multiCropState.data.isNotEmpty() && !adShown.value) {
            // Successful crop & merge; show ad then navigate
            adShown.value = true // prevent re-entry
            val activity = context as? Activity
            if (activity == null) {
                Log.w("MultiCropVideoScreen", "Context is not an Activity; navigating without ad")
                navController.navigate(MULTICROPVIDEOSUCCESSSTATE) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
            } else {
                // Show ad (if ready, otherwise load then show) and always navigate after
                adsViewModel.requestAndShowAd(
                    activity = activity,
                    onAdDismissed = {
                        navController.navigate(MULTICROPVIDEOSUCCESSSTATE) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    },
                    onAdFailed = {
                        navController.navigate(MULTICROPVIDEOSUCCESSSTATE) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    }
                )
            }
        } else if (!multiCropState.error.isNullOrEmpty()) {
            navController.navigate(MULTICROPVIDEOERRORSTATE) {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
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
        // Beta Warning Dialog
        if (showBetaWarning) {
            AlertDialog(
                onDismissRequest = {
                    showBetaWarning = false
                    navController.popBackStack()
                },
                icon = {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Beta Feature",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Beta Feature Notice",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Multi Crop Video is currently in beta stage.",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "⚠️ Important Notes:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• This feature may not work for all video formats\n" +
                                   "• Best results with MP4 files (H.264 codec)\n" +
                                   "• Some videos may fail to process\n" +
                                   "• Processing may take time for large videos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Would you like to continue?",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showBetaWarning = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showBetaWarning = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Go Back")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }

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

            // Show error message if video won't play
            if (videoLoadError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Video Format Issue",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "This video may not be supported. Try MP4 videos with H264 codec for best results.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Handle states
            when {
                multiCropState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                    return@Column
                }
            }

            // Scrollable content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                item {
                    Text(
                        text = "Multi Crop Video",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Video name
                item {
                    Text(
                        text = videoName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Filename input
                item {
                    OutlinedTextField(
                        value = filename.value,
                        onValueChange = { filename.value = it },
                        label = { Text("Output Filename") },
                        placeholder = { Text("Enter filename...") },
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
                                valueRange = 0f..videoDuration.toFloat(),
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
                    VideoSegmentCard(
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
                            editEndValue = (segment.end ?: videoDuration).toFloat()
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
                                text = "Select multiple segments from the video and merge them into one file. Use the slider to choose start and end points, preview, then add to your list.",
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
                                    multiCropViewModel.multiCropVideo(
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
                                text = "Crop & Merge Video",
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
                            valueRange = 0f..videoDuration.toFloat(),
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
fun VideoSegmentCard(
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

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Play/Pause button
                IconButton(onClick = onPlayClick) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Edit button
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

