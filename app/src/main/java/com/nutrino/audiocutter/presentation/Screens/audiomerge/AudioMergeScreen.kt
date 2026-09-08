package com.nutrino.audiocutter.presentation.Screens.audiomerge

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.FileTypes
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.presentation.Navigation.AUDIOMERGEERRORSTATE
import com.nutrino.audiocutter.presentation.Navigation.AUDIOMERGESUCCESSSTATE
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.AudioMergeViewModel
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView

@Composable
fun AudioMergeScreen(
    navController: NavController,
    audioMergeViewModel: AudioMergeViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    adsViewModel: AdsViewModel = hiltViewModel(),
    uriList: List<String>,
    songNames: List<String>
) {
    val context = LocalContext.current

    val filename = rememberSaveable { mutableStateOf("Merged ${songNames.getOrNull(0) ?: ""}") }
    val adShown = rememberSaveable { mutableStateOf(false) }

    val audioMergeState = audioMergeViewModel.audioMergeState.collectAsState()
    val upsertRecentState = recentViewModel.upsertRecentEntryState.collectAsState()

    // Handle successful merge - upsert recent entry with all details
    LaunchedEffect(audioMergeState.value.data) {
        if (audioMergeState.value.data.isNotBlank()) {
            recentViewModel.resetUpsertRecentEntryState()
            recentViewModel.upsertRecentEntry(
                recentTable = RecentTable(
                    featureType = "Audio Merge",
                    inputUri = uriList.joinToString(","),
                    outputUri = audioMergeState.value.data,
                    date_modified = System.currentTimeMillis().toString(),
                    input_duration = "",
                    output_duration = "",
                    input_name = songNames.joinToString(", "),
                    output_name = filename.value.trim(),
                    input_size = "",
                    output_size = "",
                    fileType = FileTypes.AUDIO_FILE
                )
            )
        }
    }

    // Handle navigation with ad display after recent entry is saved
    LaunchedEffect(audioMergeState.value, upsertRecentState.value) {
        val mergeState = audioMergeState.value
        val recentState = upsertRecentState.value

        if (!mergeState.isLoading && mergeState.data.isNotEmpty() && !adShown.value) {
            // Wait for recent entry to be saved in Room DB
            if (recentState.isLoading || (recentState.data.isBlank() && recentState.error == null)) {
                return@LaunchedEffect
            }

            // Successful merge; attempt to show ad once and navigate to success screen
            adShown.value = true // prevent re-entry
            val activity = context as? Activity
            if (activity == null) {
                navController.navigate(AUDIOMERGESUCCESSSTATE)
            } else {
                adsViewModel.requestAndShowAd(
                    activity = activity,
                    onAdDismissed = {
                        navController.navigate(AUDIOMERGESUCCESSSTATE)
                    },
                    onAdFailed = {
                        Toast.makeText(context, "Ad not available, proceeding...", Toast.LENGTH_SHORT).show()
                        navController.navigate(AUDIOMERGESUCCESSSTATE)
                    }
                )
            }
        }

        if (!mergeState.isLoading && mergeState.error != null) {
            navController.navigate(AUDIOMERGEERRORSTATE)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "Merge Audio Tracks",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Selected Songs Sequence Studio Card Container
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected Songs (${songNames.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        songNames.forEachIndexed { index, name ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (index < songNames.size - 1) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Output Settings Studio Card Container
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Output Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = filename.value,
                            onValueChange = { filename.value = it },
                            label = { Text("Output Filename", color = MaterialTheme.colorScheme.primary) },
                            placeholder = { Text("Enter filename for merged audio") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (filename.value.isNotEmpty()) {
                            recentViewModel.resetUpsertRecentEntryState()
                            val uris = uriList.map { it.toUri() }
                            audioMergeViewModel.mergeSongs(
                                uriList = uris,
                                filename = filename.value
                            )
                        } else {
                            Toast.makeText(context, "Please enter a filename", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !audioMergeState.value.isLoading && filename.value.isNotEmpty()
                ) {
                    if (audioMergeState.value.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Merge",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Merge Tracks",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            if (audioMergeState.value.isLoading) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Merging audio tracks...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Processing with high quality, please wait",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }
        }

        // Banner Ad at bottom
        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}
