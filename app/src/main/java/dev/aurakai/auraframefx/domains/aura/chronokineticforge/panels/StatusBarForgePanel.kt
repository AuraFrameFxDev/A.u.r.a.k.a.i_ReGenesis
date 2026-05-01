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

/**
 * 📶 STATUS BAR FORGE PANEL
 *
 * Height, icon colors, battery style, clock visibility.
 */

@Composable
fun StatusBarForgePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val statusConfig = uiState.statusBarConfig

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "📶 STATUS BAR FORGE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Height, icon colors, and battery customization",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Status Bar Preview
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

                // Mock status bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(statusConfig.height.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (statusConfig.clockVisible) {
                                Text(
                                    "12:45",
                                    color = if (statusConfig.darkIcons) Color.Black else Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Right side
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.SignalCellularAlt,
                                contentDescription = null,
                                tint = if (statusConfig.darkIcons) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                Icons.Default.Wifi,
                                contentDescription = null,
                                tint = if (statusConfig.darkIcons) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            // Battery
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(10.dp)
                                    .border(1.dp, if (statusConfig.darkIcons) Color.Black else Color.White, RoundedCornerShape(2.dp))
                                    .padding(1.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.7f)
                                        .background(if (statusConfig.darkIcons) Color.Black else Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Height Control
        ForgeSlider(
            label = "Status Bar Height",
            value = statusConfig.height,
            onValueChange = { viewModel.setStatusBarHeight(it) },
            valueRange = 16f..40f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Icon Colors
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
                Text(
                    "ICON COLORS",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconColorChip(
                        label = "Light",
                        isSelected = !statusConfig.darkIcons,
                        iconColor = Color.White,
                        onClick = { viewModel.setStatusBarIconsColor(false) }
                    )

                    IconColorChip(
                        label = "Dark",
                        isSelected = statusConfig.darkIcons,
                        iconColor = Color.Black,
                        onClick = { viewModel.setStatusBarIconsColor(true) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Show Clock", color = Color.White)
            }
            Switch(
                checked = statusConfig.clockVisible,
                onCheckedChange = { /* Toggle */ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFF00FF),
                    checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BatteryFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Battery Style", color = Color.White)
            }
            // Battery style selector
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2A2A2A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    statusConfig.batteryStyle.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transparency
        ForgeSlider(
            label = "Background Transparency",
            value = statusConfig.transparency,
            onValueChange = { /* Update */ },
            valueRange = 0f..1f
        )
    }
}

@Composable
private fun IconColorChip(
    label: String,
    isSelected: Boolean,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.3f) else Color(0xFF2A2A2A),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
        modifier = Modifier.weight(1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Circle,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
