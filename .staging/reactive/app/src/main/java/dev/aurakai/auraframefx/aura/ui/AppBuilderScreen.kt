package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import dev.aurakai.auraframefx.billing.FeatureLockedBanner
import dev.aurakai.auraframefx.billing.SubscriptionViewModel
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonGreen
import dev.aurakai.auraframefx.ui.theme.NeonPurple

/**
 * App Builder Screen - No-Code Application Generation
 *
 * **PREMIUM FEATURE** - Requires $1/month subscription after trial
 *
 * Coming soon: Visual app design, Genesis-powered code generation,
 * and one-click deployment for custom Android applications.
 */
context(subscriptionViewModel: SubscriptionViewModel) @JvmOverloads
@Composable
fun AppBuilderScreen(onNavigateBack: () -> Unit = {}) {
    val context = LocalContext.current
    val canAccess by subscriptionViewModel.hasPremiumFeatures.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (!canAccess) {
            // Show locked state during trial
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier.size(80.dp),
                    tint = NeonPurple
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "App Builder",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Premium Feature",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonPurple,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Unlocks when you subscribe for $1/month after your trial.\n\n" +
                          "Visual app design, Genesis-powered code generation,\nand instant deployment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                FeatureLockedBanner(
                    featureName = "App Builder",
                    onSubscribe = {
                        (context as? android.app.Activity)?.let { activity ->
                            subscriptionViewModel.subscribe(activity)
                        }
                    },
                    onDismiss = onNavigateBack
                )
            }
        } else {
            // Show coming soon for premium users
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "App Builder",
                    modifier = Modifier.size(80.dp),
                    tint = NeonGreen
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "App Builder",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Visual app design, Genesis-powered code generation,\nand instant deployment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun AppBuilderScreenPreview() { // Renamed
//     AppBuilderScreen()
// }
