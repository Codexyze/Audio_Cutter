package com.nutrino.audiocutter.presentation.Screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nutrino.audiocutter.presentation.Navigation.RECORDAUDIOERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.RECORDAUDIOSUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecordAudioViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun RecordAudioScreen(
    navController: NavController,
    recordAudioViewModel: RecordAudioViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val recordState by recordAudioViewModel.recordAudioState.collectAsState()

    val filename = rememberSaveable { mutableStateOf("") }
    val adShown = rememberSaveable { mutableStateOf(false) }
    var hasPermission by rememberSaveable { mutableStateOf(false) }

    // Timer state
    var elapsedSeconds by rememberSaveable { mutableLongStateOf(0L) }
    var timerRunning by rememberSaveable { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            Toast.makeText(context, "Microphone permission is required to record audio", Toast.LENGTH_LONG).show()
        }
    }

    // Check permission on launch
    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Timer effect
    LaunchedEffect(timerRunning, recordState.isPaused) {
        if (timerRunning && !recordState.isPaused) {
            while (true) {
                delay(1000L)
                elapsedSeconds++
            }
        }
    }

    fun formatTimer(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
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
            // Handle states
            when {
                recordState.isLoading && !recordState.isRecording -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                    return@Column
                }
                recordState.error != null -> {
                    navController.navigate(RECORDAUDIOERRORSTATE)
                }
                recordState.data.isNotBlank() && !adShown.value -> {
                    adShown.value = true
                    val activity = context as? Activity
                    if (activity == null) {
                        Log.w("RecordAudioScreen", "Context is not an Activity")
                        navController.navigate(RECORDAUDIOSUCCESSSTATE)
                    } else {
                        adsViewModel.requestAndShowAd(
                            activity = activity,
                            onAdDismissed = { navController.navigate(RECORDAUDIOSUCCESSSTATE) },
                            onAdFailed = { navController.navigate(RECORDAUDIOSUCCESSSTATE) }
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
                // Title
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Record Audio",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Mic icon with animation
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (recordState.isRecording && !recordState.isPaused) {
                            val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
                            val micScale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "micScale"
                            )
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(micScale)
                                    .background(
                                        color = Color(0xFFFF5252).copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Recording",
                                    modifier = Modifier.size(60.dp),
                                    tint = Color(0xFFFF5252)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mic",
                                    modifier = Modifier.size(60.dp),
                                    tint = if (recordState.isPaused) Color(0xFFFFA726) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Timer display
                item {
                    Text(
                        text = formatTimer(elapsedSeconds),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (recordState.isRecording && !recordState.isPaused)
                            Color(0xFFFF5252)
                        else if (recordState.isPaused)
                            Color(0xFFFFA726)
                        else
                            MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Status text
                    Text(
                        text = when {
                            recordState.isRecording && !recordState.isPaused -> "Recording..."
                            recordState.isPaused -> "Paused"
                            else -> "Ready to Record"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Filename input
                item {
                    OutlinedTextField(
                        value = filename.value,
                        onValueChange = { filename.value = it },
                        label = { Text("File Name", color = MaterialTheme.colorScheme.primary) },
                        placeholder = { Text("Enter filename for recording", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(0.85f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        enabled = !recordState.isRecording,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                    )
                }

                // Controls
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!recordState.isRecording) {
                                // Start button
                                IconButton(
                                    onClick = {
                                        if (!hasPermission) {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            return@IconButton
                                        }
                                        if (filename.value.isBlank()) {
                                            Toast.makeText(context, "Please enter a filename", Toast.LENGTH_SHORT).show()
                                            return@IconButton
                                        }
                                        elapsedSeconds = 0L
                                        timerRunning = true
                                        recordAudioViewModel.startRecording(
                                            context = context,
                                            filename = filename.value.trim()
                                        )
                                    },
                                    modifier = Modifier.size(72.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color(0xFFFF5252)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FiberManualRecord,
                                        contentDescription = "Start Recording",
                                        modifier = Modifier.size(36.dp),
                                        tint = Color.White
                                    )
                                }
                            } else {
                                // Pause / Resume button
                                IconButton(
                                    onClick = {
                                        if (recordState.isPaused) {
                                            recordAudioViewModel.resumeRecording()
                                        } else {
                                            recordAudioViewModel.pauseRecording()
                                        }
                                    },
                                    modifier = Modifier.size(72.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color(0xFFFFA726)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (recordState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = if (recordState.isPaused) "Resume" else "Pause",
                                        modifier = Modifier.size(36.dp),
                                        tint = Color.White
                                    )
                                }

                                // Stop button
                                IconButton(
                                    onClick = {
                                        timerRunning = false
                                        recordAudioViewModel.stopRecording(context = context)
                                    },
                                    modifier = Modifier.size(72.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop Recording",
                                        modifier = Modifier.size(36.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Info card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Record audio from your device microphone. Enter a filename, tap the record button to start. Use pause/resume to control recording. The file will be saved to Music/AudioCutter folder.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Banner Ad at bottom
        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}

