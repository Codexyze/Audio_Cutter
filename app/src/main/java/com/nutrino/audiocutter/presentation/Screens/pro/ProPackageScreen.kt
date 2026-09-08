package com.nutrino.audiocutter.presentation.Screens.pro

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nutrino.audiocutter.Constants.Colors
import com.nutrino.audiocutter.presentation.ViewModel.RevenueCatViewmodel
import com.nutrino.audiocutter.presentation.ViewModel.UserPrefViewModel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.models.Period
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    val isProUser = isUserProState.value.data
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activity = context as? Activity
    val isBuyingPackage = buyPremiumPackageState.value.isLoading

    val packages = getAllPackageState.value.data
    var selectedPackage by remember(packages) {
        mutableStateOf(packages.firstOrNull { it.packageType == PackageType.ANNUAL } ?: packages.firstOrNull())
    }

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
            userPrefViewModel.updateThemeSelection(theme = Colors.REDTHEME)
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose a plan to unlock premium features and unlimited access",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Status & Credits Summary Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Status: Pro Active",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "Status: Free Tier",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isProUser) "Unlimited Trials" else "$trialsLeft Free Left",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (!isProUser) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Next free trial refresh on $refreshDateText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Package Selection List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(packages, key = { it.identifier }) { pkg ->
                            val isSelected = selectedPackage?.identifier == pkg.identifier
                            val isBestValue = pkg.packageType == PackageType.ANNUAL

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isProUser) {
                                        selectedPackage = pkg
                                    },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isSelected && !isProUser) 2.dp else 1.dp,
                                    color = if (isSelected && !isProUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected && !isProUser)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isBestValue) 4.dp else 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Top Row: Title, Best Value Tag, Radio Selector
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (!isProUser) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { selectedPackage = pkg },
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                            }

                                            Text(
                                                text = pkg.product.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isBestValue) {
                                                Surface(
                                                    color = Color(0xFFFF9800),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Text(
                                                            text = "BEST VALUE",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 10.sp
                                                            ),
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }

                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = pkg.getDurationText(),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

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
                                }
                            }
                        }

                        // Subdued Footer for RevenueCat App User ID
                        item {
                            if (getAppUserIdState.value.data.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, bottom = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Account ID: ${getAppUserIdState.value.data}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(getAppUserIdState.value.data))
                                                Toast.makeText(context, "Account ID copied", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy Account ID",
                                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Single Floating Bottom CTA Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = Color.Transparent
                    ) {
                        val activePkg = selectedPackage
                        Button(
                            onClick = {
                                if (!isProUser && activePkg != null) {
                                    if (activity == null) {
                                        Toast.makeText(context, "Issue with payment system", Toast.LENGTH_SHORT).show()
                                    } else {
                                        revenueCatViewmodel.buyPremiumPackage(
                                            activity = activity,
                                            selectedPackage = activePkg
                                        )
                                    }
                                }
                            },
                            enabled = !isProUser && activePkg != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isProUser) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Pro Subscription Active",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                val priceText = activePkg?.product?.price?.formatted ?: ""
                                Text(
                                    text = if (priceText.isNotBlank()) "Subscribe Now — $priceText" else "Buy Pro",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Package.getDurationText(): String {
    return when (packageType) {
        PackageType.MONTHLY -> "1 Month"
        PackageType.TWO_MONTH -> "2 Months"
        PackageType.THREE_MONTH -> "3 Months"
        PackageType.SIX_MONTH -> "6 Months"
        PackageType.ANNUAL -> "12 Months (1 Year)"
        PackageType.WEEKLY -> "1 Week"
        PackageType.LIFETIME -> "Lifetime Access"
        else -> {
            val period = product.period
            if (period != null) {
                val value = period.value
                when (period.unit) {
                    Period.Unit.MONTH -> if (value == 1) "1 Month" else "$value Months"
                    Period.Unit.YEAR -> if (value == 1) "12 Months (1 Year)" else "${value * 12} Months"
                    Period.Unit.WEEK -> if (value == 1) "1 Week" else "$value Weeks"
                    Period.Unit.DAY -> if (value == 1) "1 Day" else "$value Days"
                    else -> "Subscription Plan"
                }
            } else {
                "Subscription Plan"
            }
        }
    }
}
