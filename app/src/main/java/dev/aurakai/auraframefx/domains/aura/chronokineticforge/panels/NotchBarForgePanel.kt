package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.*
import dev.aurakai.auraframefx.domains.aura.ui.components.effects.SentientGlowOrb

/**
 * 📱 NOTCH BAR FORGE PANEL
 *
 * Persistent notch bar with Kai's threat scanning orb, transparency, and edge alerts.
 */

@Composable
fun NotchBarForgePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val notchConfig = uiState.notchBarConfig

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "📱 NOTCH BAR FORGE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Kai's transparency viewer with threat scanning orb",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Notch Bar Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "LIVE PREVIEW",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFF00FF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mock status bar with notch
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(notchConfig.height.dp)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(notchConfig.color.copy(alpha = notchConfig.transparency)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time
                        Text(
                            "12:45",
                            color = if (notchConfig.color.luminance() > 0.5f) Color.Black else Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        // Notch area with orb
                        if (notchConfig.orbVisible) {
                            SentientGlowOrb(
                                mode = OrbMode.THREAT_SCANNER,
                                size = (notchConfig.height * 0.7f).dp
                            )
                        } else {
                            // Standard notch pill
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height((notchConfig.height * 0.6f).dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black)
                            )
                        }

                        // Status icons
                        Row(
                            modifier = Modifier.padding(end = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Icon(Icons.Default.BatteryFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enable/Disable
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Enable Notch Bar", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "Persistent across all apps",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = notchConfig.enabled,
                        onCheckedChange = { viewModel.setNotchBarEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFF00FF),
                            checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dimensions
        Text("DIMENSIONS", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        ForgeSlider(
            label = "Height",
            value = notchConfig.height,
            onValueChange = { viewModel.setNotchBarHeight(it) },
            valueRange = 24f..48f
        )

        ForgeSlider(
            label = "Transparency",
            value = notchConfig.transparency,
            onValueChange = { /* Update */ },
            valueRange = 0f..1f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Threat Scanning
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFFFFA500).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFFFFA500)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Kai Threat Scanner", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "Real-time device security monitoring",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = notchConfig.threatScanningEnabled,
                        onCheckedChange = { viewModel.setNotchBarThreatScanning(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFA500),
                            checkedTrackColor = Color(0xFFFFA500).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Sentient Orb", color = Color.White)
                    Switch(
                        checked = notchConfig.orbVisible,
                        onCheckedChange = { /* Toggle */ },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFF00FF),
                            checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Edge color alerts: Cyan=Secure, Amber=Warning, Red=Threat",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
