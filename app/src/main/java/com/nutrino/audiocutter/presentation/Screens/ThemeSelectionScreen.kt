package com.nutrino.audiocutter.presentation.Screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
        ThemeOption("Red", Colors.REDTHEME, Color(0xFFFF0B55)),
        ThemeOption("Green", Colors.GREENTHEME, Color(0xFF8BC34A)),
        ThemeOption("Blue", Colors.BLUETHEME, Color(0xFF03A9F4)),
        ThemeOption("Yellow", Colors.YELLOWTHEME, Color(0xFFFFEB3B)),
        ThemeOption("Purple", Colors.PURPLETHEME, Color(0xFFDF77EE)),
        ThemeOption("Pink", Colors.PINKTHEME, Color(0xFFF35389)),
        ThemeOption("Orange", Colors.ORANGETHEME, Color(0xFFF54E1B))
    )

    when {
        isUserProState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        isUserProState.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Internet",
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
                        Text(
                            text = "Theme Selection",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pick a theme color",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(themeOptions) { option ->
                            val isSelected = selectedTheme == option.value

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clickable {
                                            userPrefViewModel.updateThemeSelection(option.value)
                                        },
                                    shape = CircleShape,
                                    border = BorderStroke(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = option.color
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize())
                                }

                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "Theme updates instantly when you tap a color.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
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

