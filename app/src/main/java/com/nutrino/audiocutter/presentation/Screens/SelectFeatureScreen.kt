package com.nutrino.audiocutter.presentation.Screens

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import com.nutrino.audiocutter.BuildConfig
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORSPEEDSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORVOLUMEBOOSTERSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORMERGESCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLSONGSFORCONVERTAUDIOFORMATSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLSONGSFORMULTICROPSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSFORMULTICROPSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOFORAUDIOEXTRACTSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSFORSPEEDSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSFORMUTESCREEN
import com.nutrino.audiocutter.presentation.Navigation.HOMESCREEN
import com.nutrino.audiocutter.presentation.Navigation.PROPACKAGESCREEN
import com.nutrino.audiocutter.presentation.Navigation.RECORDAUDIOSCREEN
import com.nutrino.audiocutter.presentation.Navigation.RECENTSCREEN
import com.nutrino.audiocutter.presentation.Navigation.THEMESELECTIONSCREEN
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutrino.audiocutter.presentation.ViewModel.AdsViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView
import androidx.core.net.toUri

data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val isComingSoon: Boolean = false,
    val isFeatureRequest: Boolean = false,
    val isFeedbackAds: Boolean = false,
    val isPrivacyPolicy: Boolean = false
)


@Composable
fun SelectFeatureScreen(
    navController: NavController,
    adsViewModel: AdsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showFeedbackDialog by remember { mutableStateOf(false) }
    val isUserProState by adsViewModel.isUserProState.collectAsStateWithLifecycle()

    LaunchedEffect(showFeedbackDialog) {
        if (showFeedbackDialog) {
            adsViewModel.refreshIsUserProStatusForAds()
        }
    }

    val features = listOf(
        FeatureItem("Audio Trimmer", Icons.Default.ContentCut),
        FeatureItem("Video Trimmer", Icons.Default.VideoLibrary),
        FeatureItem("Video Speed", Icons.Default.VideoLibrary),
        FeatureItem("Audio Speed", Icons.Default.MusicNote),
        FeatureItem("Mute Video", Icons.Default.VideoLibrary),
        FeatureItem("Audio Volume Booster", Icons.Default.GraphicEq),
        FeatureItem("Audio Extractor", Icons.Default.GraphicEq),
        FeatureItem("Audio Merge", Icons.Default.MusicNote),
        FeatureItem("Multi Crop Audio", Icons.Default.ContentCut),
        FeatureItem("Multi Crop Video", Icons.Default.VideoLibrary),
        FeatureItem("Convert Audio", Icons.Default.SwapHoriz),
        FeatureItem("Record Audio", Icons.Default.Mic),
        FeatureItem("Recent", Icons.Outlined.Schedule),
        FeatureItem("Pro", Icons.Default.Favorite),
        FeatureItem("Theme", Icons.Default.ColorLens),
        FeatureItem("FeedBack Ads", Icons.Default.CardGiftcard, isFeedbackAds = true),
        FeatureItem("Privacy Policy", Icons.Default.PrivacyTip, isPrivacyPolicy = true),
        FeatureItem("Feature Request", Icons.Default.Email, isFeatureRequest = true),
        FeatureItem("Coming Soon", Icons.Outlined.Schedule, isComingSoon = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Feature",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(features) { feature ->
                    FeatureCard(
                        feature = feature,
                        onClick = {
                            when{
                                feature == features[0] -> {
                                    navController.navigate(HOMESCREEN)
                                }
                                feature == features[1] -> {
                                    // Video Trimmer
                                    navController.navigate(ALLVIDEOSCREEN)
                                }
                                feature == features[2] -> {
                                    // Video Speed
                                    navController.navigate(ALLVIDEOSFORSPEEDSCREEN)
                                }
                                feature == features[3] -> {
                                    // Audio Speed
                                    navController.navigate(ALLAUDIOFORSPEEDSCREEN)
                                }
                                feature == features[4] -> {
                                    // Mute Video
                                    navController.navigate(ALLVIDEOSFORMUTESCREEN)
                                }
                                feature == features[5] -> {
                                    // Audio Volume Booster
                                    navController.navigate(ALLAUDIOFORVOLUMEBOOSTERSCREEN)
                                }
                                feature == features[6] -> {
                                    // Audio Extractor
                                    navController.navigate(ALLVIDEOFORAUDIOEXTRACTSCREEN)
                                }
                                feature == features[7] -> {
                                    // Audio Track Merge
                                    navController.navigate(ALLAUDIOFORMERGESCREEN)
                                }
                                feature == features[8] -> {
                                    // Multi Crop Audio
                                    navController.navigate(ALLSONGSFORMULTICROPSCREEN)
                                }
                                feature == features[9] -> {
                                    // Multi Crop Video
                                    navController.navigate(ALLVIDEOSFORMULTICROPSCREEN)
                                }
                                feature == features[10] -> {
                                    // Convert Audio Format
                                    navController.navigate(ALLSONGSFORCONVERTAUDIOFORMATSCREEN)
                                }
                                feature == features[11] -> {
                                    // Record Audio
                                    navController.navigate(RECORDAUDIOSCREEN)
                                }
                                feature == features[12] -> {
                                    // Recent
                                    navController.navigate(RECENTSCREEN)
                                }
                                feature == features[13] -> {
                                    // Pro
                                   navController.navigate(PROPACKAGESCREEN)
                                }
                                feature == features[14] -> {
                                    // Theme
                                    navController.navigate(THEMESELECTIONSCREEN)
                                }
                                feature == features[15] -> {
                                    // FeedBack Ads
                                    showFeedbackDialog = true
                                }
                                feature == features[16] -> {
                                    // Privacy Policy
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data =
                                            "https://codexyze.github.io/audio_cutter.html".toUri()
                                    }
                                    context.startActivity(intent)
                                }
                                feature == features[17] -> {
                                    // Feature Request
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:${BuildConfig.FEEDBACK_EMAIL}".toUri()
                                        putExtra(Intent.EXTRA_SUBJECT, "Feature Request")
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                                }
                            }
                        }
                    )
                }
            }
        }

        // Banner Ad at the bottom - won't disturb UI, will hide if fails to load
        BannerAdView(
            modifier = Modifier.fillMaxWidth(),
            adsViewModel = adsViewModel
        )
    }

    // FeedBack Ads Dialog - Pro aware behavior
    if (showFeedbackDialog) {
        Dialog(
            onDismissRequest = { showFeedbackDialog = false }
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
                        // Animated Heart Icon
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
                            tint = Color(0xFFFF6B9D) // Soft pink
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
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
                                    onClick = { showFeedbackDialog = false },
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
                                            showFeedbackDialog = false
                                            val activity = context as? Activity
                                            if (activity != null) {
                                                adsViewModel.requestAndShowAd(
                                                    activity = activity,
                                                    onAdDismissed = {},
                                                    onAdFailed = {}
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
                                            showFeedbackDialog = false
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
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = !feature.isComingSoon) { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (feature.isComingSoon)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (feature.isComingSoon) 0.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with background circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = if (feature.isComingSoon)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
                            else
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(36.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = feature.title,
                        modifier = Modifier.size(36.dp),
                        tint = if (feature.isComingSoon)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = if (feature.isComingSoon) Color.White.copy(alpha = 0.65f) else Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                if (feature.isComingSoon) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Soon",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

}