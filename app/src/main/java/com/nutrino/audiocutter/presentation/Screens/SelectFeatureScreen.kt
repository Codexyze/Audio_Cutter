package com.nutrino.audiocutter.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary

import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nutrino.audiocutter.presentation.Navigation.ALLAUDIOFORMERGESCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOFORAUDIOEXTRACTSCREEN
import com.nutrino.audiocutter.presentation.Navigation.ALLVIDEOSCREEN
import com.nutrino.audiocutter.presentation.Navigation.HOMESCREEN

data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val isComingSoon: Boolean = false
)


@Composable
fun SelectFeatureScreen(navController: NavController) {
    val features = listOf(
        FeatureItem("Audio Trimmer", Icons.Default.ContentCut),
        FeatureItem("Video Trimmer", Icons.Default.VideoLibrary),
        FeatureItem("Audio Extractor", Icons.Default.GraphicEq),
        FeatureItem("Audio Merge", Icons.Default.MusicNote),
        FeatureItem("Coming Soon", Icons.Outlined.Schedule, isComingSoon = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Feature",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
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
                                // Audio Trimmer
                                navController.navigate(HOMESCREEN)
                            }
                            feature == features[1] -> {
                                // Video Trimmer
                                navController.navigate(ALLVIDEOSCREEN)
                            }
                            feature == features[2] -> {
                                // Audio Extractor
                                navController.navigate(ALLVIDEOFORAUDIOEXTRACTSCREEN)
                            }
                            feature == features[3] -> {
                                // Audio Track Merge
                                navController.navigate(ALLAUDIOFORMERGESCREEN)
                            }
                        }
                    }
                )
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
                    color = if (feature.isComingSoon)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.onSurface,
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

}