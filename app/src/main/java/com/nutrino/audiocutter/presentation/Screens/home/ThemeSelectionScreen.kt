package com.nutrino.audiocutter.presentation.Screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.Colors
import com.nutrino.audiocutter.presentation.Navigation.PROPACKAGESCREEN
import com.nutrino.audiocutter.presentation.ViewModel.RevenueCatViewmodel
import com.nutrino.audiocutter.presentation.ViewModel.UserPrefViewModel
import com.nutrino.audiocutter.presentation.components.BannerAdView

data class ThemeOption(
    val title: String,
    val value: String,
    val color: Color
)

@Composable
fun ThemeSelectionScreen(
    navController: NavController,
    userPrefViewModel: UserPrefViewModel = hiltViewModel(),
    revenueCatViewmodel: RevenueCatViewmodel = hiltViewModel()
) {
    val selectedTheme by userPrefViewModel.themeSelection.collectAsState()
    val isUserProState by revenueCatViewmodel.isUserProState.collectAsState()

    LaunchedEffect(Unit) {
        revenueCatViewmodel.checkIsUserPro()
    }

    val themeOptions = listOf(
        ThemeOption("Crimson Red", Colors.REDTHEME, Color(0xFFFF0B55)),
        ThemeOption("Lime Green", Colors.GREENTHEME, Color(0xFF8BC34A)),
        ThemeOption("Sky Blue", Colors.BLUETHEME, Color(0xFF03A9F4)),
        ThemeOption("Electric Yellow", Colors.YELLOWTHEME, Color(0xFFFFEB3B)),
        ThemeOption("Orchid Purple", Colors.PURPLETHEME, Color(0xFFDF77EE)),
        ThemeOption("Deep Pink", Colors.PINKTHEME, Color(0xFFF35389)),
        ThemeOption("Fiery Orange", Colors.ORANGETHEME, Color(0xFFF54E1B)),
        // 16 Additional Professional Studio Themes
        ThemeOption("Cyber Cyan", Colors.TEALTHEME, Color(0xFF00E5FF)),
        ThemeOption("Emerald Studio", Colors.EMERALDTHEME, Color(0xFF00E676)),
        ThemeOption("Cosmic Indigo", Colors.INDIGOTHEME, Color(0xFF651FFF)),
        ThemeOption("Cyber Amber", Colors.AMBERTHEME, Color(0xFFFFC400)),
        ThemeOption("Vivid Crimson", Colors.CRIMSONTHEME, Color(0xFFD50000)),
        ThemeOption("Sunset Coral", Colors.CORALTHEME, Color(0xFFFF6E40)),
        ThemeOption("Neon Lime", Colors.LIMETHEME, Color(0xFFAEEA00)),
        ThemeOption("Royal Violet", Colors.VIOLETTHEME, Color(0xFFB388FF)),
        ThemeOption("Ruby Rose", Colors.ROSETHEME, Color(0xFFFF1744)),
        ThemeOption("Aquamarine", Colors.AQUATHEME, Color(0xFF1DE9B6)),
        ThemeOption("Electric Blue", Colors.ELECTRICBLUETHEME, Color(0xFF2979FF)),
        ThemeOption("Magenta Plum", Colors.PLUMTHEME, Color(0xFFE040FB)),
        ThemeOption("Solar Gold", Colors.SOLARTHEME, Color(0xFFFF9100)),
        ThemeOption("Studio Mint", Colors.MINTTHEME, Color(0xFF64FFDA)),
        ThemeOption("Hot Magenta", Colors.MAGENTATHEME, Color(0xFFFF007F)),
        ThemeOption("Titanium Ice", Colors.SILVERTHEME, Color(0xFFE0E6ED))
    )

    when {
        !isUserProState.data && isUserProState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        !isUserProState.data && isUserProState.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }

        isUserProState.data -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "Theme Selection",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Customize your studio accent color",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(themeOptions, key = { it.value }) { option ->
                            val isSelected = selectedTheme == option.value
                            val animatedScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.03f else 1.0f,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "scale"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(animatedScale)
                                    .clickable {
                                        userPrefViewModel.updateThemeSelection(option.value)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) option.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        option.color.copy(alpha = 0.15f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Mini UI Preview Strip Inside Card
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1A1A22))
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(option.color, CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(14.dp)
                                                    .width(48.dp)
                                                    .background(option.color.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Active Theme",
                                                tint = option.color,
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .align(Alignment.Center)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = option.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) option.color else Color.White
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(option.color, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Theme updates instantly when you tap any color card.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                BannerAdView(modifier = Modifier.fillMaxWidth())
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AlertDialog(
                    onDismissRequest = { navController.popBackStack() },
                    title = {
                        Text(
                            text = "Premium Required",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Text(
                            text = "To use different themes, you need Premium.",
                            color = Color.White
                        )
                    },
                    confirmButton = {
                        Button(onClick = { navController.navigate(PROPACKAGESCREEN) }) {
                            Text(text = "Buy Premium")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { navController.popBackStack() }) {
                            Text(text = "Close")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                )
            }
        }
    }
}
