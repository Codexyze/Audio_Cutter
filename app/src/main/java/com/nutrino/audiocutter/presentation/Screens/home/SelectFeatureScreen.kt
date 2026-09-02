package com.nutrino.audiocutter.presentation.Screens.home

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nutrino.audiocutter.BuildConfig
import com.nutrino.audiocutter.domain.StateHandeling.IsUserProState
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORMERGESCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORSPEEDSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORVOLUMEBOOSTERSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLSONGSFORCONVERTAUDIOFORMATSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLSONGSFORMULTICROPSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOFORAUDIOEXTRACTSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSFORMULTICROPSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSFORMUTESCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSFORSPEEDSCREEN
import com.nutrino.audiocutter.presentation.Navigation.HOMESCREEN
import com.nutrino.audiocutter.presentation.Navigation.PROPACKAGESCREEN
import com.nutrino.audiocutter.presentation.Navigation.RECENTSCREEN
import com.nutrino.audiocutter.presentation.Navigation.RECORDAUDIOSCREEN
import com.nutrino.audiocutter.presentation.Navigation.THEMESELECTIONSCREEN
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.ViewModel.UserPrefViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class FeatureItem(
    val title: String,
    val description: String = "",
    val icon: ImageVector,
    val isComingSoon: Boolean = false,
    val isFeatureRequest: Boolean = false,
    val isFeedbackAds: Boolean = false,
    val isPrivacyPolicy: Boolean = false
)

enum class FeatureTab(
    val title: String,
    val icon: ImageVector
) {
    AUDIO("Audio", Icons.Default.MusicNote),
    VIDEO("Video", Icons.Default.VideoLibrary),
    RECENT("Recent", Icons.Outlined.Schedule),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun SelectFeatureScreen(
    navController: NavController,
    adsViewModel: AdsViewModel = hiltViewModel(),
    userPrefViewModel: UserPrefViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showLimitInfoDialog by remember { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(FeatureTab.AUDIO) }

    val isUserProState by adsViewModel.isUserProState.collectAsStateWithLifecycle()
    val usageCount by userPrefViewModel.usageCount.collectAsStateWithLifecycle()
    val lastUsageDate by userPrefViewModel.lastUsageDate.collectAsStateWithLifecycle()

    val trialsLeft = remember(usageCount, lastUsageDate) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val actualCount = if (lastUsageDate == today) usageCount else 0
        (5 - actualCount).coerceAtLeast(0)
    }

    val refreshDateText = remember(lastUsageDate) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val calendar = Calendar.getInstance()
        if (lastUsageDate == today) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    LaunchedEffect(Unit) {
        adsViewModel.refreshIsUserProStatusForAds()
    }

    LaunchedEffect(showFeedbackDialog) {
        if (showFeedbackDialog) {
            adsViewModel.refreshIsUserProStatusForAds()
        }
    }

    val onFeatureClick: (FeatureItem) -> Unit = remember(navController) {
        { item ->
            when (item.title) {
                "Audio Trimmer" -> navController.navigate(HOMESCREEN)
                "Audio Extractor" -> navController.navigate(ALLVIDEOFORAUDIOEXTRACTSCREEN)
                "Record Audio" -> navController.navigate(RECORDAUDIOSCREEN)
                "Audio Speed" -> navController.navigate(ALLAUDIOFORSPEEDSCREEN)
                "Audio Volume" -> navController.navigate(ALLAUDIOFORVOLUMEBOOSTERSCREEN)
                "Audio Merge" -> navController.navigate(ALLAUDIOFORMERGESCREEN)
                "Multi Crop Audio" -> navController.navigate(ALLSONGSFORMULTICROPSCREEN)
                "Convert Audio" -> navController.navigate(ALLSONGSFORCONVERTAUDIOFORMATSCREEN)
                "Video Trimmer" -> navController.navigate(ALLVIDEOSCREEN)
                "Mute Video" -> navController.navigate(ALLVIDEOSFORMUTESCREEN)
                "Video Speed" -> navController.navigate(ALLVIDEOSFORSPEEDSCREEN)
                "Multi Crop Video" -> navController.navigate(ALLVIDEOSFORMULTICROPSCREEN)
            }
        }
    }

    Scaffold(
        topBar = {
            TopHeaderBar(
                isUserProState = isUserProState,
                trialsLeft = trialsLeft,
                refreshDateText = refreshDateText,
                onProClick = { navController.navigate(PROPACKAGESCREEN) },
                onTrialInfoClick = { showLimitInfoDialog = true }
            )
        },
        bottomBar = {
            Column {
                BannerAdView(
                    modifier = Modifier.fillMaxWidth(),
                    adsViewModel = adsViewModel
                )
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        if (tab == FeatureTab.RECENT) {
                            navController.navigate(RECENTSCREEN)
                        } else {
                            selectedTab = tab
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                FeatureTab.AUDIO -> AudioFeaturesContent(onFeatureClick = onFeatureClick)
                FeatureTab.VIDEO -> VideoFeaturesContent(onFeatureClick = onFeatureClick)
                FeatureTab.RECENT -> AudioFeaturesContent(onFeatureClick = onFeatureClick)
                FeatureTab.SETTINGS -> SettingsFeaturesContent(
                    navController = navController,
                    onShowFeedbackDialog = { showFeedbackDialog = true },
                    onOpenPrivacyPolicy = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = "https://codexyze.github.io/audio_cutter.html".toUri()
                        }
                        context.startActivity(intent)
                    },
                    onOpenFeatureRequest = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:${BuildConfig.FEEDBACK_EMAIL}".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Feature Request")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    }
                )
            }
        }
    }

    if (showFeedbackDialog) {
        FeedbackAdsDialog(
            isUserProState = isUserProState,
            adsViewModel = adsViewModel,
            navController = navController,
            onDismiss = { showFeedbackDialog = false }
        )
    }

    if (showLimitInfoDialog) {
        LimitInfoDialog(
            isPro = isUserProState.data,
            navController = navController,
            onDismiss = { showLimitInfoDialog = false }
        )
    }
}

@Composable
fun TopHeaderBar(
    isUserProState: IsUserProState,
    trialsLeft: Int,
    refreshDateText: String,
    onProClick: () -> Unit,
    onTrialInfoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title & Profile Row
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tools & Studio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterEnd)
                    .clickable { onProClick() },
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Capsule Pill Badge for Credits / Pro status
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    isUserProState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Checking status...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    isUserProState.data -> {
                        Row(
                            modifier = Modifier.clickable { onTrialInfoClick() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Unlimited Trials",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Trial Info",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(14.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Surface(
                            onClick = onProClick,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFF9800)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier.clickable { onTrialInfoClick() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "$trialsLeft Free Credits Left",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Trial Info",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(14.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Surface(
                            onClick = onProClick,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFF6F00)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (!isUserProState.data && !isUserProState.isLoading) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Refreshes on $refreshDateText",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: FeatureTab,
    onTabSelected: (FeatureTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0.dp)
    ) {
        FeatureTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun AudioFeaturesContent(
    onFeatureClick: (FeatureItem) -> Unit
) {
    val quickActions = remember {
        listOf(
            FeatureItem("Audio Trimmer", "Cut & trim audio clips", Icons.Default.ContentCut),
            FeatureItem("Audio Extractor", "Extract audio from video", Icons.Default.GraphicEq),
            FeatureItem("Record Audio", "Record voice or audio", Icons.Default.Mic)
        )
    }

    val audioUtilities = remember {
        listOf(
            FeatureItem("Audio Speed", "Adjust playback rate", Icons.Default.MusicNote),
            FeatureItem("Audio Volume", "Boost audio levels", Icons.Default.GraphicEq),
            FeatureItem("Audio Merge", "Combine clips", Icons.Default.MusicNote),
            FeatureItem("Multi Crop Audio", "Precise cuts", Icons.Default.ContentCut),
            FeatureItem("Convert Audio", "Change format", Icons.Default.SwapHoriz)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("Quick Actions")
        FeatureGridSection(items = quickActions, onClick = onFeatureClick)

        SectionTitle("Audio Utilities")
        FeatureGridSection(items = audioUtilities, onClick = onFeatureClick)
    }
}

@Composable
fun VideoFeaturesContent(
    onFeatureClick: (FeatureItem) -> Unit
) {
    val quickActions = remember {
        listOf(
            FeatureItem("Video Trimmer", "Trim video clips", Icons.Default.VideoLibrary),
            FeatureItem("Mute Video", "Remove video sound", Icons.Default.VideoLibrary)
        )
    }

    val videoUtilities = remember {
        listOf(
            FeatureItem("Video Speed", "Adjust playback rate", Icons.Default.VideoLibrary),
            FeatureItem("Multi Crop Video", "Crop video clips", Icons.Default.VideoLibrary)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("Quick Actions")
        FeatureGridSection(items = quickActions, onClick = onFeatureClick)

        SectionTitle("Video Utilities")
        FeatureGridSection(items = videoUtilities, onClick = onFeatureClick)
    }
}

@Composable
fun SettingsFeaturesContent(
    navController: NavController,
    onShowFeedbackDialog: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenFeatureRequest: () -> Unit
) {
    val settingsFeatures = remember {
        listOf(
            FeatureItem("Pro Package", "Unlock unlimited features & remove limits", Icons.Default.WorkspacePremium),
            FeatureItem("Support Us (Watch Ad)", "Watch a short ad to support development", Icons.Default.CardGiftcard, isFeedbackAds = true),
            FeatureItem("Theme", "Select your preferred app theme", Icons.Default.ColorLens),
            FeatureItem("Feature Request", "Request new features or report issues", Icons.Default.Email, isFeatureRequest = true),
            FeatureItem("Privacy Policy", "Read our privacy policy", Icons.Default.PrivacyTip, isPrivacyPolicy = true),
            FeatureItem("Coming Soon", "More exciting features coming soon", Icons.Outlined.Schedule, isComingSoon = true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle("Settings & Account")

        settingsFeatures.forEach { item ->
            SettingsCardItem(
                feature = item,
                onClick = {
                    when {
                        item.title == "Pro Package" -> navController.navigate(PROPACKAGESCREEN)
                        item.title == "Theme" -> navController.navigate(THEMESELECTIONSCREEN)
                        item.isFeedbackAds -> onShowFeedbackDialog()
                        item.isPrivacyPolicy -> onOpenPrivacyPolicy()
                        item.isFeatureRequest -> onOpenFeatureRequest()
                    }
                }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 2.dp)
    )
}

@Composable
fun FeatureGridSection(
    items: List<FeatureItem>,
    onClick: (FeatureItem) -> Unit
) {
    val chunkedItems = remember(items) { items.chunked(2) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        chunkedItems.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val item1 = rowItems[0]
                Box(modifier = Modifier.weight(1f)) {
                    FeatureCard(
                        feature = item1,
                        onClick = { onClick(item1) }
                    )
                }

                if (rowItems.size > 1) {
                    val item2 = rowItems[1]
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureCard(
                            feature = item2,
                            onClick = { onClick(item2) }
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !feature.isComingSoon, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (feature.isComingSoon)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (feature.isComingSoon)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    modifier = Modifier.size(22.dp),
                    tint = if (feature.isComingSoon)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = if (feature.isComingSoon) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                if (feature.description.isNotEmpty()) {
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                if (feature.isComingSoon) {
                    Text(
                        text = "Soon",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCardItem(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !feature.isComingSoon, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (feature.isComingSoon)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (feature.isComingSoon)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    modifier = Modifier.size(20.dp),
                    tint = if (feature.isComingSoon)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = if (feature.isComingSoon) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
                if (feature.description.isNotEmpty()) {
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (feature.isComingSoon) {
                Text(
                    text = "Soon",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun FeedbackAdsDialog(
    isUserProState: IsUserProState,
    adsViewModel: AdsViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "heartPulse")
                    val heartScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "heartScale"
                    )

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        modifier = Modifier
                            .size(48.dp)
                            .scale(heartScale),
                        tint = Color(0xFFFF6B9D)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "A Small Help Goes a Long Way 🤍",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    when {
                        isUserProState.isLoading -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }

                        isUserProState.data -> {
                            Text(
                                text = "you already did a lot by taking pro .",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            TextButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Close",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = if (isUserProState.error != null) {
                                    "No internet. You can still support by watching an ad."
                                } else {
                                    "Watching a short ad helps support this independent app and keeps future updates coming."
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                ),
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "No pressure at all — thanks for using the app!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onDismiss()
                                        val activity = context as? Activity
                                        if (activity != null) {
                                            adsViewModel.requestAndShowRewardedAd(
                                                activity = activity,
                                                onAdDismissed = {
                                                    Toast.makeText(context, "Thank you!", Toast.LENGTH_SHORT).show()
                                                },
                                                onAdFailed = {
                                                    Toast.makeText(context, "Failed Loading Add..", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF6B9D)
                                    )
                                ) {
                                    Text(
                                        text = "Watch Ads",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        onDismiss()
                                        navController.navigate(PROPACKAGESCREEN)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "Buy Pro",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LimitInfoDialog(
    isPro: Boolean,
    navController: NavController,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Feature Usage Limits",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isPro) {
                    Text(
                        text = "All features are free and unlimited for you! ✨",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Trial Limited Features (5 Daily Trials)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FeatureNavRow("Audio Trimmer", Icons.Default.ContentCut) {
                            onDismiss()
                            navController.navigate(HOMESCREEN)
                        }
                        FeatureNavRow("Video Trimmer", Icons.Default.VideoLibrary) {
                            onDismiss()
                            navController.navigate(ALLVIDEOSCREEN)
                        }
                        FeatureNavRow("Video Speed", Icons.Default.VideoLibrary) {
                            onDismiss()
                            navController.navigate(ALLVIDEOSFORSPEEDSCREEN)
                        }
                        FeatureNavRow("Audio Speed", Icons.Default.MusicNote) {
                            onDismiss()
                            navController.navigate(ALLAUDIOFORSPEEDSCREEN)
                        }
                        FeatureNavRow("Mute Video", Icons.Default.VideoLibrary) {
                            onDismiss()
                            navController.navigate(ALLVIDEOSFORMUTESCREEN)
                        }
                        FeatureNavRow("Audio Extractor", Icons.Default.GraphicEq) {
                            onDismiss()
                            navController.navigate(ALLVIDEOFORAUDIOEXTRACTSCREEN)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "Unlimited Features",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FeatureNavRow("Audio Volume Booster", Icons.Default.GraphicEq) {
                            onDismiss()
                            navController.navigate(ALLAUDIOFORVOLUMEBOOSTERSCREEN)
                        }
                        FeatureNavRow("Audio Merge", Icons.Default.MusicNote) {
                            onDismiss()
                            navController.navigate(ALLAUDIOFORMERGESCREEN)
                        }
                        FeatureNavRow("Multi Crop Audio", Icons.Default.ContentCut) {
                            onDismiss()
                            navController.navigate(ALLSONGSFORMULTICROPSCREEN)
                        }
                        FeatureNavRow("Multi Crop Video", Icons.Default.VideoLibrary) {
                            onDismiss()
                            navController.navigate(ALLVIDEOSFORMULTICROPSCREEN)
                        }
                        FeatureNavRow("Convert Audio", Icons.Default.SwapHoriz) {
                            onDismiss()
                            navController.navigate(ALLSONGSFORCONVERTAUDIOFORMATSCREEN)
                        }
                        FeatureNavRow("Record Audio", Icons.Default.Mic) {
                            onDismiss()
                            navController.navigate(RECORDAUDIOSCREEN)
                        }
                        FeatureNavRow("Recent", Icons.Outlined.Schedule) {
                            onDismiss()
                            navController.navigate(RECENTSCREEN)
                        }
                        FeatureNavRow("Theme", Icons.Default.ColorLens) {
                            onDismiss()
                            navController.navigate(THEMESELECTIONSCREEN)
                        }
                    }

                    if (!isPro) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Premium Access",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Upgrade to Pro to remove all daily limits and enjoy unlimited access to every feature.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Button(
                                onClick = {
                                    onDismiss()
                                    navController.navigate(PROPACKAGESCREEN)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Go Pro")
                            }
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun FeatureNavRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
