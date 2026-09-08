package com.nutrino.audiocutter.presentation.Screens.audiovolumebooster

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import com.nutrino.audiocutter.presentation.ViewModel.AudioVolumeBoosterViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import java.io.File

data class VolumePreset(
    val factor: Float,
    val label: String
)

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

    var volumeFactor by rememberSaveable { mutableFloatStateOf(1f) } // 1.0f = 100%

    val volumePresets = remember {
        listOf(
            VolumePreset(1.0f, "100% Normal"),
            VolumePreset(1.5f, "150% Boost"),
            VolumePreset(2.0f, "200% Loud"),
            VolumePreset(3.0f, "300% Max"),
            VolumePreset(5.0f, "500% Extreme")
        )
    }

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
            // Player View Studio Card Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Volume Preview",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

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
                            .height(240.dp)
                    )
                }
            }

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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = outputName.value,
                        onValueChange = { outputName.value = it },
                        label = { Text("Output Filename", color = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth(0.92f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        singleLine = true
                    )
                }

                // Volume Level Controls Studio Card Container
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Volume Level Controls",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Monospaced Jitter-Free Volume Readout Badge Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                ) {
                                    val currentPercentage = (volumeFactor * 100).toInt()
                                    Text(
                                        text = "Volume: $currentPercentage%",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // Quick Volume Preset Chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(volumePresets) { preset ->
                                    val isSelected = (volumeFactor - preset.factor).let { kotlin.math.abs(it) < 0.05f }
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { volumeFactor = preset.factor },
                                        label = {
                                            Text(
                                                text = preset.label,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.primary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            selectedBorderColor = MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }

                            // Gain Slider
                            Slider(
                                value = volumeFactor,
                                onValueChange = { volumeFactor = it },
                                valueRange = 0f..5f,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Scale Markers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("100%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("300%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("500%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Apply Volume Primary CTA Button
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
                            .fillMaxWidth(0.92f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !audioVolumeState.isLoading
                    ) {
                        if (audioVolumeState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply Volume",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}
