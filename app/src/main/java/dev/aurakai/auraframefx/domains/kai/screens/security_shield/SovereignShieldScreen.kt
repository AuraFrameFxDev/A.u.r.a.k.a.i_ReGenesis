package dev.aurakai.auraframefx.domains.kai.screens.security_shield

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.viewmodels.SovereignShieldViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import androidx.compose.foundation.shape.CircleShape

/**
 * 🛡️ SOVEREIGN SHIELD (The Anti-Big-Tech Standard)
 * Kai's core hardening hub. Disables trackers, telemetry, and non-sovereign data flows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SovereignShieldScreen(
    onNavigateBack: () -> Unit,
    viewModel: SovereignShieldViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF030305))) {
        AnimeHUDContainer(
            title = "SOVEREIGN SHIELD",
            description = "SETTING THE STANDARD: ZERO TELEMETRY. ZERO ADVERTISING. TOTAL SYSTEM SOVEREIGNTY.",
            glowColor = Color(0xFFFF1111)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header - Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                
                // Privacy Score readout
                PrivacyScoreDisplay(state.privacyScore)

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        ShieldToggleItem(
                            title = "Neural AdBlock",
                            description = "Redirects ad-server requests to null via /etc/hosts logic.",
                            icon = Icons.Default.PublicOff,
                            isActive = state.isAdBlockActive,
                            onToggle = { viewModel.toggleAdBlock() },
                            accentColor = Color(0xFFFF3366)
                        )
                    }

                    item {
                        ShieldToggleItem(
                            title = "Telemetry Cloak",
                            description = "Suspends background analytical services and OEM meta-data streams.",
                            icon = Icons.Default.GppBad,
                            isActive = state.isTelemetryBlocked,
                            onToggle = { viewModel.toggleTelemetry() },
                            accentColor = Color(0xFF00FF85)
                        )
                    }

                    item {
                        ShieldToggleItem(
                            title = "Sensor Isolation",
                            description = "Intercepts app requests for Device IDs, Sensors, and GPS while inactive.",
                            icon = Icons.Default.Security,
                            isActive = state.isSensorCloakActive,
                            onToggle = { viewModel.toggleSensorCloak() },
                            accentColor = Color(0xFF00E5FF)
                        )
                    }

                    item {
                        ShieldToggleItem(
                            title = "Encrypted Handshake",
                            description = "Enforces DNS over TLS (DoT) via non-corporate providers.",
                            icon = Icons.Default.Shield,
                            isActive = state.isPrivateDnsEnabled,
                            onToggle = { /* Lock State */ },
                            accentColor = Color(0xFFB026FF)
                        )
                    }

                    item {
                        ShieldToggleItem(
                            title = "Sovereign Bridge",
                            description = "Utilizes Rikka/Shizuku for system operations without OEM tracking.",
                            icon = Icons.Default.PrivacyTip,
                            isActive = state.isShizukuBridgeActive,
                            onToggle = { viewModel.toggleShizukuBridge() },
                            accentColor = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun PrivacyScoreDisplay(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "SOVEREIGNTY RATING",
                fontFamily = LEDFontFamily,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Text(
                "$score%",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    brush = Brush.verticalGradient(listOf(Color(0xFFFF1111), Color(0xFFFF5555)))
                ),
                fontFamily = LEDFontFamily
            )
            LinearProgressIndicator(
                progress = score / 100f,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = Color(0xFFFF1111),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun ShieldToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    isActive: Boolean,
    onToggle: () -> Unit,
    accentColor: Color
) {
    val backgroundColor = if (isActive) accentColor.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.02f)
    val borderColor = if (isActive) accentColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor)
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(description, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, lineHeight = 14.sp)
            }

            Switch(
                checked = isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accentColor,
                    checkedTrackColor = accentColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}


