package com.nutrino.audiocutter.presentation.Screens.recent

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.FileTypes
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.presentation.Navigation.RECENTAUDIOPLAYERSCREEN
import com.nutrino.audiocutter.presentation.Navigation.RECENTVIDEOPLAYERSCREEN
import com.nutrino.audiocutter.presentation.Navigation.SELECTFEATURESCREEN
import com.nutrino.audiocutter.presentation.ViewModel.RecentViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RecentScreen(
    navController: NavController,
    recentViewModel: RecentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by recentViewModel.getAllRecentEntriesState.collectAsState()
    val deleteState by recentViewModel.deleteRecentEntryState.collectAsState()
    val searchQuery by recentViewModel.searchQueryState.collectAsState()
    val filteredEntries by recentViewModel.filteredEntriesState.collectAsState()
    var deletingEntryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var deleteErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var ringtoneMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var settingRingtoneEntryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingRingtoneUri by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingRingtoneEntryId by rememberSaveable { mutableStateOf<Int?>(null) }

    fun setRingtoneForUri(targetUri: String, entryId: Int?) {
        settingRingtoneEntryId = entryId
        scope.launch {
            val setResult = withContext(Dispatchers.IO) {
                try {
                    RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_RINGTONE,
                        targetUri.toUri()
                    )
                    null
                } catch (e: Exception) {
                    e
                }
            }

            ringtoneMessage = if (setResult == null) {
                "Ringtone updated successfully"
            } else {
                setResult.message ?: "Failed to set ringtone"
            }
            settingRingtoneEntryId = null
        }
    }

    val writeSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val targetUri = pendingRingtoneUri
        val targetEntryId = pendingRingtoneEntryId
        pendingRingtoneUri = null
        pendingRingtoneEntryId = null

        if (targetUri.isNullOrBlank()) return@rememberLauncherForActivityResult

        if (Settings.System.canWrite(context)) {
            setRingtoneForUri(targetUri = targetUri, entryId = targetEntryId)
        } else {
            ringtoneMessage = "Permission denied. Cannot set ringtone"
        }
    }

    LaunchedEffect(Unit) {
        recentViewModel.getAllRecentEntries()
    }

    LaunchedEffect(state.data) {
        state.data
            .filter { it.outputUri.isNotBlank() && !isOutputUriValid(context, it.outputUri) }
            .forEach { invalidEntry ->
                recentViewModel.deleteRecentEntry(invalidEntry)
            }
    }

    LaunchedEffect(deleteState.isLoading, deleteState.data, deleteState.error) {
        when {
            deleteState.isLoading -> Unit
            deleteState.error != null -> {
                deleteErrorMessage = deleteState.error
                deletingEntryId = null
            }
            deleteState.data.isNotBlank() -> {
                deleteErrorMessage = null
                deletingEntryId = null
            }
        }
    }

    // Handle multicrop audio editing - navigate to edit screen with loaded segments
    val getCropSegmentsState by recentViewModel.getCropSegmentsByFileNameState.collectAsState()
    LaunchedEffect(getCropSegmentsState.data) {
        if (getCropSegmentsState.data.isNotEmpty() && getCropSegmentsState.error == null) {
            val segments = getCropSegmentsState.data
            if (segments.isNotEmpty()) {
                // Get first segment for input URI (all segments have same input URI)
                val inputUri = segments.first().input_uri
                val fileName = segments.first().fileName

                // Convert CropSegmentTable to CropSegment for navigation
                val cropSegments = segments.map { table ->
                    com.nutrino.audiocutter.data.DataClass.CropSegment(
                        start = table.start.toLongOrNull() ?: 0L,
                        end = table.end.toLongOrNull() ?: 0L
                    )
                }

                // Navigate to MultiCropAudioScreen with pre-loaded segments
                navController.navigate(
                    com.nutrino.audiocutter.presentation.Navigation.MULTICROPAUDIOSCREEN(
                        uri = inputUri,
                        songDuration = 0L, // Will be calculated by player
                        songName = fileName
                    )
                )

                // Optionally clear the state after navigation
                recentViewModel.getCropSegmentsByFileName(fileName)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.data.isEmpty() -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Your Studio Workspace",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Your trimmed clips, merged audio, and processed files will appear here once you start creating.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    navController.navigate(SELECTFEATURESCREEN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCut,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Explore Studio Tools",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Recent",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your latest processed files",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Search Bar
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { recentViewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("Search by name or feature type...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { recentViewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (deleteErrorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = deleteErrorMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        if (ringtoneMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ringtoneMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        // Show search results count
                        if (searchQuery.isNotEmpty()) {
                            Text(
                                text = "${filteredEntries.size} result(s) found",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Display filtered entries or all entries
                        val displayEntries = if (searchQuery.isEmpty()) state.data else filteredEntries
                        if (displayEntries.isEmpty() && searchQuery.isNotEmpty()) {
                            Text(
                                text = "No matching entries found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 32.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(4.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(displayEntries, key = { it.id }) { recent ->
                                    RecentItem(
                                        recent = recent,
                                        isDeleting = deletingEntryId == recent.id && deleteState.isLoading,
                                        isSettingRingtone = settingRingtoneEntryId == recent.id,
                                        showRingtoneAction = recent.fileType == FileTypes.AUDIO_FILE,
                                        showEditAction = recent.fileType == FileTypes.MULTICROP_AUDIO,
                                        onClick = {
                                            if (isOutputUriValid(context, recent.outputUri)) {
                                                when (recent.fileType) {
                                                    FileTypes.VIDEO_FILE -> navController.navigate(
                                                        RECENTVIDEOPLAYERSCREEN(
                                                            outputUri = recent.outputUri,
                                                            outputName = recent.output_name,
                                                            inputName = recent.input_name
                                                        )
                                                    )

                                                    else -> navController.navigate(
                                                        RECENTAUDIOPLAYERSCREEN(
                                                            outputUri = recent.outputUri,
                                                            outputName = recent.output_name,
                                                            inputName = recent.input_name
                                                        )
                                                    )
                                                }
                                            }
                                        },
                                        onDeleteClick = {
                                            deleteErrorMessage = null
                                            deletingEntryId = recent.id
                                            recentViewModel.deleteRecentEntry(recent)
                                        },
                                        onSetRingtoneClick = {
                                            ringtoneMessage = null

                                            if (!isOutputUriValid(context, recent.outputUri)) {
                                                ringtoneMessage = "Output file is missing"
                                                return@RecentItem
                                            }

                                            if (Settings.System.canWrite(context)) {
                                                setRingtoneForUri(
                                                    targetUri = recent.outputUri,
                                                    entryId = recent.id
                                                )
                                            } else {
                                                pendingRingtoneUri = recent.outputUri
                                                pendingRingtoneEntryId = recent.id
                                                ringtoneMessage = "Grant Modify system settings permission"
                                                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                                                    data = "package:${context.packageName}".toUri()
                                                }
                                                writeSettingsLauncher.launch(intent)
                                            }
                                        },
                                        onEditClick = {
                                            // Fetch crop segments for editing
                                            recentViewModel.getCropSegmentsByFileName(recent.output_name)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        BannerAdView(modifier = Modifier.fillMaxWidth())
    }
}

private fun isOutputUriValid(context: Context, outputUri: String): Boolean {
    if (outputUri.isBlank()) return false

    return runCatching {
        val parsedUri = outputUri.toUri()
        if (parsedUri.scheme == "content") {
            context.contentResolver.openAssetFileDescriptor(parsedUri, "r")?.use { true } ?: false
        } else {
            val path = parsedUri.path ?: outputUri
            java.io.File(path).exists()
        }
    }.getOrDefault(false)
}

@Composable
private fun RecentItem(
    recent: RecentTable,
    isDeleting: Boolean,
    isSettingRingtone: Boolean,
    showRingtoneAction: Boolean,
    showEditAction: Boolean = false,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSetRingtoneClick: () -> Unit,
    onEditClick: () -> Unit = {}
) {
    val isPlayable = recent.outputUri.isNotBlank()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(
            0.5.dp,
            if (isPlayable) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isPlayable) { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Feature Type Badge & Playable Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recent.featureType.ifBlank { "Recent" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPlayable) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                ) {
                    Text(
                        text = if (isPlayable) "Playable" else "Not Playable",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isPlayable) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Middle Row: Leading Play Icon Box + File Names Stack
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Leading Play Button Container
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (isPlayable)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (recent.fileType == FileTypes.VIDEO_FILE) Icons.Default.VideoLibrary else Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = if (isPlayable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = recent.output_name.ifBlank { "Untitled Output" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = recent.input_name.ifBlank { "Input name not available" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        maxLines = 1
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 0.5.dp
            )

            // Trailing Footer Row: Action Icons & Tap to Play hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPlayable) {
                    Text(
                        text = "Tap card to play",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit button for MULTICROP_AUDIO entries
                    if (showEditAction) {
                        IconButton(
                            onClick = onEditClick,
                            enabled = isPlayable,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit segments",
                                tint = if (isPlayable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Ringtone button for regular AUDIO_FILE entries
                    if (showRingtoneAction && !showEditAction) {
                        if (isSettingRingtone) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(
                                onClick = onSetRingtoneClick,
                                enabled = isPlayable,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Set as ringtone",
                                    tint = if (isPlayable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete entry",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
