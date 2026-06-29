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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.FileTypes
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.presentation.Navigation.MUTEVIDEOERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.MUTEVIDEOSUCCESSSTATE
import com.nutrino.audiocutter.presentation.Navigation.PROPACKAGESCREEN
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MuteVideoViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MuteVideoScreen(
    navController: NavController,
    muteVideoViewModel: MuteVideoViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    videoDuration: Long = 0,
    videoName: String = ""
) {
    val context = LocalContext.current

    val outputName = rememberSaveable { mutableStateOf("Muted $videoName") }
    val adShown = rememberSaveable { mutableStateOf(false) }

    val muteVideoState by muteVideoViewModel.muteVideoState.collectAsState()
    val upsertRecentState = recentViewModel.upsertRecentEntryState.collectAsState()
    val userLimitState by muteVideoViewModel.userLimitState.collectAsState()

    // Daily Limit Dialog
    if (userLimitState.isLimitReached) {
        val nextRefreshDate = remember {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(calendar.time)
        }

        AlertDialog(
            onDismissRequest = { muteVideoViewModel.resetUserLimitError() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Pro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Daily Limit Reached",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your free trial has expired for today.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your daily limit will refresh tomorrow on $nextRefreshDate.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Upgrade to Studio Pro now to get unlimited access and remove all restrictions instantly!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        muteVideoViewModel.resetUserLimitError()
                        navController.navigate(PROPACKAGESCREEN)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Buy Studio Pro", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { muteVideoViewModel.resetUserLimitError() }
                ) {
                    Text("Maybe Later", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(24.dp)
        )
    }

    LaunchedEffect(uri) {
        runCatching {
            val fileUri = Uri.fromFile(File(uri))
            mediaPlayerViewModel.initializePlayer(fileUri)
        }.onFailure {
            navController.navigate(MUTEVIDEOERRORSTATE)
        }
    }

    LaunchedEffect(muteVideoState.data) {
        if (muteVideoState.data.isNotBlank()) {
            recentViewModel.resetUpsertRecentEntryState()
            recentViewModel.upsertRecentEntry(
                recentTable = RecentTable(
                    featureType = "Mute Video",
                    inputUri = uri,
                    outputUri = muteVideoState.data,
                    date_modified = System.currentTimeMillis().toString(),
                    input_duration = videoDuration.toString(),
                    output_duration = videoDuration.toString(),
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
                muteVideoState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    return@Column
                }

                muteVideoState.error != null -> {
                    navController.navigate(MUTEVIDEOERRORSTATE)
                }

                muteVideoState.data.isNotBlank() && !adShown.value -> {
                    if (upsertRecentState.value.isLoading ||
                        (upsertRecentState.value.data.isBlank() && upsertRecentState.value.error == null)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        return@Column
                    }

                    adShown.value = true
                    val activity = context as? Activity
                    if (activity == null) {
                        Log.w("MuteVideoScreen", "Context is not an Activity; navigating without ad")
                        navController.navigate(MUTEVIDEOSUCCESSSTATE)
                    } else {
                        adsViewModel.requestAndShowAd(
                            activity = activity,
                            onAdDismissed = { navController.navigate(MUTEVIDEOSUCCESSSTATE) },
                            onAdFailed = { navController.navigate(MUTEVIDEOSUCCESSSTATE) }
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
                    val isProcessing = muteVideoState.isLoading || userLimitState.isLoading

                    Button(
                        onClick = {
                            if (outputName.value.isBlank()) {
                                Toast.makeText(context, "Please enter output filename", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (uri.isBlank() || videoDuration <= 0L) {
                                navController.navigate(MUTEVIDEOERRORSTATE)
                                return@Button
                            }

                            val fileUri = Uri.fromFile(File(uri))
                            recentViewModel.resetUpsertRecentEntryState()
                            muteVideoViewModel.muteVideo(
                                uri = fileUri,
                                filename = outputName.value.trim()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.76f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Mute Video", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}
