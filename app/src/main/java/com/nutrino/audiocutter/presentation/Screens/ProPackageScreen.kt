package com.nutrino.audiocutter.presentation.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nutrino.audiocutter.presentation.Navigation.BUYPROPACKAGESCREEN
import com.nutrino.audiocutter.presentation.ViewModel.RevenueCatViewmodel

@Composable
fun ProPackageScreen(
    navController: NavController,
    revenueCatViewmodel: RevenueCatViewmodel = hiltViewModel()
) {
    val getAllPackageState = revenueCatViewmodel.getAllPackageState.collectAsStateWithLifecycle()
    val isUserProState = revenueCatViewmodel.isUserProState.collectAsStateWithLifecycle()
    val isProUser = isUserProState.value.data

    LaunchedEffect(Unit) {
        revenueCatViewmodel.getAllPackageRevenueCat()
        revenueCatViewmodel.checkIsUserPro()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            getAllPackageState.value.isLoading || isUserProState.value.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            getAllPackageState.value.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = getAllPackageState.value.error ?: "Failed to load packages",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { revenueCatViewmodel.getAllPackageRevenueCat() }) {
                        Text(text = "Retry")
                    }
                }
            }

            getAllPackageState.value.data.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No packages available right now",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(onClick = { revenueCatViewmodel.getAllPackageRevenueCat() }) {
                        Text(text = "Refresh")
                    }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Go Pro",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Choose a plan to unlock premium features",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isProUser) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Pro user",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Status: Pro",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "Status: Not Pro",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(getAllPackageState.value.data, key = { it.identifier }) { pkg ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        if (!isProUser) {
                                            navController.navigate(
                                                BUYPROPACKAGESCREEN(
                                                    packageIdentifier = pkg.identifier,
                                                    productId = pkg.product.id,
                                                    title = pkg.product.title,
                                                    description = pkg.product.description,
                                                    priceFormatted = pkg.product.price.formatted,
                                                    packageType = pkg.packageType.name
                                                )
                                            )
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = pkg.product.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (pkg.product.description.isNotBlank()) {
                                        Text(
                                            text = pkg.product.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                        )
                                    }
                                    Text(
                                        text = pkg.product.price.formatted,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                BUYPROPACKAGESCREEN(
                                                    packageIdentifier = pkg.identifier,
                                                    productId = pkg.product.id,
                                                    title = pkg.product.title,
                                                    description = pkg.product.description,
                                                    priceFormatted = pkg.product.price.formatted,
                                                    packageType = pkg.packageType.name
                                                )
                                            )
                                        },
                                        enabled = !isProUser,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (isProUser) "Already Pro" else "Buy")
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