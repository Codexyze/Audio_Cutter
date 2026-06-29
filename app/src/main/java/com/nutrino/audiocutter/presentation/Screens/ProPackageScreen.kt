package com.nutrino.audiocutter.presentation.Screens

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.Colors
import com.nutrino.audiocutter.presentation.ViewModel.RevenueCatViewmodel
import com.nutrino.audiocutter.presentation.ViewModel.UserPrefViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue


@SuppressLint("ContextCastToActivity")
@Composable
fun ProPackageScreen(
    navController: NavController,
    revenueCatViewmodel: RevenueCatViewmodel = hiltViewModel(),
    userPrefViewModel: UserPrefViewModel = hiltViewModel()

) {
    val getAllPackageState = revenueCatViewmodel.getAllPackageState.collectAsStateWithLifecycle()
    val isUserProState = revenueCatViewmodel.isUserProState.collectAsStateWithLifecycle()
    val buyPremiumPackageState = revenueCatViewmodel.buyPremiumPackageState.collectAsStateWithLifecycle()
    val getAppUserIdState = revenueCatViewmodel.getAppUserIdState.collectAsStateWithLifecycle()
    
    val usageCount by userPrefViewModel.usageCount.collectAsStateWithLifecycle()
    val lastUsageDate by userPrefViewModel.lastUsageDate.collectAsStateWithLifecycle()

    val trialsLeft = remember(usageCount, lastUsageDate) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val actualCount = if (lastUsageDate == today) usageCount else 0
        (3 - actualCount).coerceAtLeast(0)
    }

    val refreshDateText = remember(lastUsageDate) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val calendar = Calendar.getInstance()
        if (lastUsageDate == today) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    val isProUser = isUserProState.value.data
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activity = context as? Activity
    val isBuyingPackage = buyPremiumPackageState.value.isLoading

    LaunchedEffect(Unit) {
        revenueCatViewmodel.getAllPackageRevenueCat()
        revenueCatViewmodel.checkIsUserPro()
        revenueCatViewmodel.getAppUserId()
    }

    LaunchedEffect(
        buyPremiumPackageState.value.data,
        buyPremiumPackageState.value.error,
        buyPremiumPackageState.value.isLoading
    ) {
        if (buyPremiumPackageState.value.isLoading) return@LaunchedEffect

        if (buyPremiumPackageState.value.error != null) {
            Toast.makeText(context, buyPremiumPackageState.value.error, Toast.LENGTH_SHORT).show()
        } else if (buyPremiumPackageState.value.data) {
            userPrefViewModel.updateThemeSelection(theme = Colors.REDTHEME )
            Toast.makeText(context, "Purchase successful", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            getAllPackageState.value.isLoading || isUserProState.value.isLoading || isBuyingPackage -> {
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
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

                        // Usage info badge
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isProUser) "Unlimited Trials" else "$trialsLeft Free Left",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!isProUser) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Next refresh on $refreshDateText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (getAppUserIdState.value.data.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "User ID",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = getAppUserIdState.value.data,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(getAppUserIdState.value.data))
                                Toast.makeText(context, "User ID copied", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy User ID",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

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
                                    .clickable(enabled = !isProUser) {
                                        if (!isProUser) {
                                            if (activity == null) {
                                                Toast.makeText(context, "Issue with payment system", Toast.LENGTH_SHORT).show()
                                            }else{
                                                revenueCatViewmodel.buyPremiumPackage(
                                                    activity = activity,
                                                    selectedPackage = pkg
                                                )

                                            }

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
                                            if (activity == null) {
                                                Toast.makeText(context, "Issue with payment system", Toast.LENGTH_SHORT).show()
                                            }else{
                                                revenueCatViewmodel.buyPremiumPackage(
                                                    activity = activity,
                                                    selectedPackage = pkg
                                                )

                                            }
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