package com.nutrino.audiocutter.presentation.TestScreens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.purchaseWith


//TESTT
@Composable
fun RevenueCatTestScreen(activity: Activity) {

    // ── State variables ───────────────────────────────────────────────────
    // These hold whatever is happening right now — loading, error, success etc.

    // Stores the log messages we print on screen so you can see what's happening
    val logs = remember { mutableStateListOf<String>() }

    // The two packages fetched from RC (monthly + semiannual)
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }

    // True while any RC operation is running (shows loading indicator)
    var isLoading by remember { mutableStateOf(false) }

    // True once user successfully purchases — shows success message
    var isPurchaseSuccess by remember { mutableStateOf(false) }

    // True if user is already pro when screen opens
    var isAlreadyPro by remember { mutableStateOf(false) }

    // Helper to add a line to the on-screen log
    fun log(message: String) {
        logs.add(0, message) // add at top so latest is always visible
        Log.d("RC_TEST", message)
    }

    // ── Step 1: Check if already Pro + Fetch packages on screen open ──────
    LaunchedEffect(Unit) {

        isLoading = true
        log("⏳ Checking entitlement...")

        // SNIPPET 4 — Check if user is already pro
        // Runs immediately when screen opens
        // "AUDIO CUTTER Pro" = your entitlement identifier in RC dashboard
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = { error ->
                log("❌ Entitlement check failed: ${error.message}")
            },
            onSuccess = { customerInfo ->
                val isPro = customerInfo.entitlements["AUDIO CUTTER Pro"]?.isActive == true
                isAlreadyPro = isPro
                if (isPro) {
                    log("✅ User is already PRO!")
                } else {
                    log("ℹ️ User is NOT pro — free user")
                }
            }
        )

        log("⏳ Fetching offerings from RC...")

        // SNIPPET 2 — Fetch packages from RC
        // Gets your "default" offering and the 2 packages inside it
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                isLoading = false
                log("❌ Failed to fetch offerings: ${error.message}")
            },
            onSuccess = { offerings ->
                isLoading = false

                // "current" = the offering you marked as Default in RC dashboard
                val currentOffering = offerings.current

                if (currentOffering == null) {
                    // This means you forgot to mark an offering as Default in RC dashboard
                    log("❌ No default offering found! Check RC dashboard → Offerings → Make Default")
                    return@getOfferingsWith
                }

                // Store the packages so we can show Buy buttons for each
                packages = currentOffering.availablePackages
                log("✅ Offerings fetched! Found ${packages.size} package(s)")

                // Log each package so you can verify the right products loaded
                packages.forEach { pkg ->
                    // pkg.identifier           → "$rc_monthly" or "$rc_six_month"
                    // pkg.product.title        → title you set in Play Console
                    // pkg.product.price.formatted → real price like "₹199.00"
                    log("📦 ${pkg.identifier} | ${pkg.product.title} | ${pkg.product.price.formatted}")
                }
            }
        )
    }

    // ── UI ────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "RevenueCat Test Screen",
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Shows current pro status at the top
        Text(
            text = if (isAlreadyPro) "Status: ✅ PRO USER" else "Status: 🔒 FREE USER",
            color = if (isAlreadyPro) Color.Green else Color.Gray,
          
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading spinner while RC is working
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading...", style = MaterialTheme.typography.bodySmall)
        }

        // Purchase success banner
        if (isPurchaseSuccess) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉 Purchase Successful! You are now PRO.",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Buy buttons — one per package fetched from RC ─────────────────
        // This will show 2 buttons: one for monthly, one for semiannual
        packages.forEach { rcPackage ->
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    isLoading = true
                    log("⏳ Starting purchase for: ${rcPackage.identifier}")

                    // SNIPPET 3 — Purchase
                    // activity = needed by Google Play to show the payment sheet
                    // rcPackage = the specific plan the user tapped
                    Purchases.sharedInstance.purchaseWith(
                        PurchaseParams.Builder(activity, rcPackage).build(),

                        onError = { error, userCancelled ->
                            isLoading = false
                            if (userCancelled) {
                                // User pressed back — not a real error, just log it
                                log("↩️ User cancelled the purchase")
                            } else {
                                // Real failure — billing issue, network, etc.
                                log("❌ Purchase failed: ${error.message}")
                            }
                        },

                        onSuccess = { _, customerInfo ->
                            isLoading = false

                            // RC validated the purchase with Google backend
                            // Now check if entitlement is active
                            // "AUDIO CUTTER Pro" = your RC dashboard entitlement identifier
                            val isPro = customerInfo
                                .entitlements["AUDIO CUTTER Pro"]
                                ?.isActive == true

                            isPurchaseSuccess = isPro
                            isAlreadyPro = isPro

                            if (isPro) {
                                log("✅ Purchase verified! Entitlement ACTIVE ✅")
                            } else {
                                // Purchase went through but entitlement not active
                                // Most likely: product not attached to entitlement in RC dashboard
                                log("⚠️ Purchased but entitlement NOT active — check RC dashboard → Entitlements → attach products")
                            }
                        }
                    )
                }
            ) {
                // Shows real price from Play Console e.g. "Buy Monthly — ₹199.00"
                Text("Buy ${rcPackage.product.title} — ${rcPackage.product.price.formatted}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        // ── On-screen log ─────────────────────────────────────────────────
        // Shows everything RC is doing so you don't need to stare at Logcat
        Text("Logs:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(logs) { logLine ->
                Text(
                    text = logLine,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}