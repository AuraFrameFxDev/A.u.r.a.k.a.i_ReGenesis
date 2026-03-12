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
 * ROM Tools Gate Submenu
 * Advanced ROM editing and flashing capabilities
 */
@Composable
fun RootToolsScreen(
    navController: NavController? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A0A0A), // Dark Red
                            Color.Black,
                            Color(0xFF1A001A)  // Dark Purple
                        )
                    )
                )
        )

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
                        text = "⚠️ ROM TOOLS ⚠️",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            letterSpacing = 2.sp
                        ),
                        color = Color(0xFFFF4500) // Orange Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Advanced ROM editing and flashing capabilities",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFFF6347).copy(alpha = 0.7f) // Tomato
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Warning Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF4500).copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4500))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFFF4500),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "⚠️ Advanced users only. Incorrect use may brick your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF4500),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ROM Tools Features
            item {
                ROMSubmenuCard(
                    icon = Icons.Default.Edit,
                    title = "Live ROM Editor",
                    description = "Edit system files in real-time with safety checks",
                    color = Color(0xFFFF4500), // Orange Red
                    onClick = {
                        navController?.navigate("live_rom_editor")
                    }
                )
            }

            item {
                ROMSubmenuCard(
                    icon = Icons.Default.FlashOn,
                    title = "ROM Flasher",
                    description = "Flash custom ROMs and recovery images",
                    color = Color(0xFFFFD700), // Gold
                    onClick = {
                        navController?.navigate("rom_flasher")
                    }
                )
            }

            item {
                ROMSubmenuCard(
                    icon = Icons.Default.LockOpen,
                    title = "Bootloader Manager",
                    description = "Unlock/lock bootloader and manage partitions",
                    color = Color(0xFFDC143C), // Crimson
                    onClick = {
                        navController?.navigate("bootloader_manager")
                    }
                )
            }

            item {
                ROMSubmenuCard(
                    icon = Icons.Default.Restore,
                    title = "Recovery Tools",
                    description = "TWRP integration and backup management",
                    color = Color(0xFF32CD32), // Lime Green
                    onClick = {
                        navController?.navigate("recovery_tools")
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
                        containerColor = Color(0xFFFF4500).copy(alpha = 0.2f)
                    )
                ) {
                    Text("← Back to Gates", color = Color(0xFFFF4500))
                }
            }
        }
    }
}

/**
 * Reusable card component for ROM submenu items
 */
@Composable
private fun ROMSubmenuCard(
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
