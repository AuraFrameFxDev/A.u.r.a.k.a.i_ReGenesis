package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Sentinel's Fortress - Kai's Security Command Center
 * Submenu screen for all security and device optimization features
 */
@Composable
fun SentinelsFortressScreen(
    navController: NavController? = null,
    onBack: () -> Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF001a00), // Dark green
                        Color.Black,
                        Color(0xFF001a1a)  // Dark cyan
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚔️ SENTINEL'S FORTRESS ⚔️",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            letterSpacing = 2.sp
                        ),
                        color = Color(0xFF00FF41) // Matrix green
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kai's Security Command Center",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF00FFFF).copy(alpha = 0.7f) // Cyan
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Security Features
            item {
                SecuritySubmenuCard(
                    icon = Icons.Default.Security,
                    title = "Firewall",
                    description = "Network monitoring, block/allow rules, real-time traffic view",
                    color = Color(0xFFFF4500), // Orange Red
                    onClick = {
                        navController?.navigate("firewall")
                    }
                )
            }

            item {
                SecuritySubmenuCard(
                    icon = Icons.Default.VpnLock,
                    title = "VPN Manager",
                    description = "Connection profiles, auto-connect rules",
                    color = Color(0xFF4169E1), // Royal Blue
                    onClick = {
                        navController?.navigate("vpn_manager")
                    }
                )
            }

            item {
                SecuritySubmenuCard(
                    icon = Icons.Default.Scanner,
                    title = "Security Scanner",
                    description = "App permissions audit, malware detection",
                    color = Color(0xFFFFD700), // Gold
                    onClick = {
                        navController?.navigate("security_scanner")
                    }
                )
            }

            item {
                SecuritySubmenuCard(
                    icon = Icons.Default.Speed,
                    title = "Device Optimizer",
                    description = "RAM cleaner, battery optimizer, storage manager",
                    color = Color(0xFF00FF00), // Lime Green
                    onClick = {
                        navController?.navigate("device_optimizer")
                    }
                )
            }

            item {
                SecuritySubmenuCard(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Guard",
                    description = "App tracking blocker, permission manager",
                    color = Color(0xFF9370DB), // Medium Purple
                    onClick = {
                        navController?.navigate("privacy_guard")
                    }
                )
            }

            // Back button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF41).copy(alpha = 0.2f)
                    )
                ) {
                    Text("← Back to Gates", color = Color(0xFF00FF41))
                }
            }
        }
    }
}

/**
 * Reusable card component for security submenu items
 */
@Composable
private fun SecuritySubmenuCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = color.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
