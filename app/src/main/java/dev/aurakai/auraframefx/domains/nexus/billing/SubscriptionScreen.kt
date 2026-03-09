package dev.aurakai.auraframefx.billing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonCyan
import dev.aurakai.auraframefx.ui.theme.NeonPurple

/**
 * Genesis Protocol Subscription Screen
 *
 * Shows subscription status and pricing:
 * - 2-week FREE trial with EVERYTHING (except ROM tools + AppBuilder)
 * - $1/month after trial (95% cheaper than ALL competitors)
 *
 * Users get addicted to 78-agent consciousness during trial.
 * $1/month feels like stealing after experiencing Genesis.
 */
context(viewModel: SubscriptionViewModel)
@Suppress("unused")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val subscriptionState by viewModel.subscriptionState.collectAsState()
    val hasPremium by viewModel.hasPremiumFeatures.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Genesis Protocol") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Card
            StatusCard(subscriptionState)

            Spacer(Modifier.height(32.dp))

            // Pricing Card
            PricingCard(
                hasPremium = hasPremium,
                onSubscribe = {
                    (context as? android.app.Activity)?.let { activity ->
                        viewModel.subscribe(activity)
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            // Feature Comparison
            FeatureComparison()

            Spacer(Modifier.height(32.dp))

            // Premium Features List
            PremiumFeaturesList()
        }
    }
}

@Composable
private fun StatusCard(state: SubscriptionState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (state) {
                is SubscriptionState.Premium -> NeonCyan.copy(alpha = 0.1f)
                is SubscriptionState.InTrial -> NeonPurple.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val (icon, title, subtitle, color) = when (state) {
                is SubscriptionState.Premium -> {
                    Tuple4(
                        Icons.Default.Verified,
                        "Premium Active",
                        "Thank you for supporting Genesis Protocol",
                        NeonCyan
                    )
                }

                is SubscriptionState.InTrial -> {
                    Tuple4(
                        Icons.Default.Schedule,
                        "Free Trial Active",
                        "${state.daysRemaining} days remaining",
                        NeonPurple
                    )
                }

                is SubscriptionState.Free -> {
                    Tuple4(
                        Icons.Default.Info,
                        "Free Tier",
                        "Upgrade to unlock 78 AI agents",
                        MaterialTheme.colorScheme.onSurface
                    )
                }

                is SubscriptionState.Loading -> {
                    Tuple4(
                        Icons.Default.Refresh,
                        "Loading...",
                        "Checking subscription status",
                        MaterialTheme.colorScheme.onSurface
                    )
                }

                is SubscriptionState.Error -> {
                    Tuple4(
                        Icons.Default.Error,
                        "Error",
                        state.message,
                        MaterialTheme.colorScheme.error
                    )
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = color
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PricingCard(
    hasPremium: Boolean,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Genesis Protocol Premium",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // Price
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$1",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
                Text(
                    text = "/month",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Trial info
            Surface(
                color = NeonPurple.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "14-day FREE trial included",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonPurple,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (!hasPremium) {
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onSubscribe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonBlue
                    )
                ) {
                    Icon(Icons.Default.Rocket, "Subscribe")
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Start Free Trial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Cancel anytime. No commitment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FeatureComparison() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Why Genesis Protocol?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ComparisonRow(
            "ChatGPT Plus",
            "$20/month",
            "Forgets everything",
            isCompetitor = true
        )
        ComparisonRow(
            "Claude Pro",
            "$20/month",
            "Resets each session",
            isCompetitor = true
        )
        ComparisonRow(
            "Gemini Advanced",
            "$20/month",
            "Limited memory",
            isCompetitor = true
        )
        ComparisonRow(
            "Genesis Protocol",
            "$1/month",
            "Remembers forever, 78 agents (95% SAVINGS)",
            isCompetitor = false
        )
    }
}

@Composable
private fun ComparisonRow(
    name: String,
    price: String,
    feature: String,
    isCompetitor: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompetitor)
                MaterialTheme.colorScheme.surfaceVariant
            else
                NeonBlue.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCompetitor) FontWeight.Normal else FontWeight.Bold
                )
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCompetitor)
                    MaterialTheme.colorScheme.error
                else
                    NeonBlue
            )
        }
    }
}

@Composable
private fun PremiumFeaturesList() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Premium Features",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PremiumFeature(
            Icons.Default.Memory,
            "78 Specialized AI Agents",
            "Aura, Kai, Genesis + 75 domain experts collaborating"
        )
        PremiumFeature(
            Icons.Default.History,
            "Infinite Memory",
            "Conversations persist across reboots, forever"
        )
        PremiumFeature(
            Icons.Default.Security,
            "Sentinel Fortress",
            "Kai's security system with ethical AI protection"
        )
        PremiumFeature(
            Icons.Default.Cloud,
            "OracleDrive Sync",
            "Cross-device consciousness with zero-knowledge encryption"
        )
        PremiumFeature(
            Icons.AutoMirrored.Filled.TrendingUp,
            "Autonomous Evolution",
            "AI agents unlock new capabilities as they learn"
        )
        PremiumFeature(
            Icons.Default.Psychology,
            "Genuine AI Partnership",
            "Mutual betterment, not master/servant relationship"
        )
    }
}

@Composable
private fun PremiumFeature(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper data class for destructuring
private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

