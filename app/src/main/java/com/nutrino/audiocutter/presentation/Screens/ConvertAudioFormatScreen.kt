package com.nutrino.audiocutter.presentation.Screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.nutrino.audiocutter.presentation.Navigation.CONVERTAUDIOFORMATERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.CONVERTAUDIOFORMATSUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.ConvertAudioFormatViewModel
import com.nutrino.audiocutter.presentation.ViewModel.MediaPlayerViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView

// Data class representing an output audio format supported by Transformer
data class AudioFormatOption(
    val displayName: String,
    val mimeType: String,
    val extension: String,
    val comingSoon: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun ConvertAudioFormatScreen(
    navController: NavController,
    convertAudioFormatViewModel: ConvertAudioFormatViewModel = hiltViewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uri: String = "",
    songDuration: Long = 0,
    songName: String = ""
) {
    val context = LocalContext.current

    // Output formats actually supported by Media3 Transformer's setAudioMimeType()
    // Only AAC is fully supported — MP3, Vorbis, AMR, OGG all throw IllegalStateException
    val supportedFormats = listOf(
        AudioFormatOption("AAC (.m4a)", MimeTypes.AUDIO_AAC, "m4a"),
        AudioFormatOption("MP3 (.mp3) — Coming Soon", "", "mp3", comingSoon = true),
        AudioFormatOption("WAV (.wav) — Coming Soon", "", "wav", comingSoon = true),
        AudioFormatOption("OGG (.ogg) — Coming Soon", "", "ogg", comingSoon = true),
        AudioFormatOption("FLAC (.flac) — Coming Soon", "", "flac", comingSoon = true)
    )

    // State
    val filename = rememberSaveable { mutableStateOf("") }
    var selectedFormatIndex by rememberSaveable { mutableStateOf(0) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val adShown = rememberSaveable { mutableStateOf(false) }

    val convertState by convertAudioFormatViewModel.convertAudioFormatState.collectAsState()

    // Initialize player
    LaunchedEffect(uri) {
        mediaPlayerViewModel.initializePlayer(uri.toUri())
    }

    // Cleanup on dispose
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
                convertState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                    return@Column
                }
                convertState.error != null -> {
                    navController.navigate(CONVERTAUDIOFORMATERRORSTATE)
                }
                convertState.data.isNotBlank() && !adShown.value -> {
                    adShown.value = true
                    val activity = context as? Activity
                    if (activity == null) {
                        Log.w("ConvertAudioFormatScreen", "Context is not an Activity")
                        navController.navigate(CONVERTAUDIOFORMATSUCCESSSTATE)
                    } else {
                        adsViewModel.requestAndShowAd(
                            activity = activity,
                            onAdDismissed = { navController.navigate(CONVERTAUDIOFORMATSUCCESSSTATE) },
                            onAdFailed = { navController.navigate(CONVERTAUDIOFORMATSUCCESSSTATE) }
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
                        placeholder = { Text("Enter filename for converted audio", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
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

                // Format selector section
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
                                "Select Output Format",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dropdown for format selection
                            ExposedDropdownMenuBox(
                                expanded = dropdownExpanded,
                                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = supportedFormats[selectedFormatIndex].displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Output Format", color = MaterialTheme.colorScheme.primary) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                                    ),
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.primary)
                                )

                                ExposedDropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    supportedFormats.forEachIndexed { index, format ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = format.displayName,
                                                    color = if (format.comingSoon)
                                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                    else if (index == selectedFormatIndex)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            onClick = {
                                                if (!format.comingSoon) {
                                                    selectedFormatIndex = index
                                                    dropdownExpanded = false
                                                }
                                            },
                                            enabled = !format.comingSoon
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Format info
                            Text(
                                text = "Output: ${supportedFormats[selectedFormatIndex].extension.uppercase()} format",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                            text = "Convert your audio to different formats. Select the desired output format from the dropdown and click convert. The converted file will be saved to Music/AudioCutter folder.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Convert button
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
                                val selectedFormat = supportedFormats[selectedFormatIndex]
                                convertAudioFormatViewModel.convertAudioFormat(
                                    context = context,
                                    uri = uri,
                                    outputMimeType = selectedFormat.mimeType,
                                    outputExtension = selectedFormat.extension,
                                    filename = filename.value.trim()
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
                            text = "Convert Audio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Banner Ad at bottom
        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}

