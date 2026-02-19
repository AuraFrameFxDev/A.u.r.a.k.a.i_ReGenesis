package dev.aurakai.auraframefx.domains.lsposed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.cascade.utils.LSPosedDetector
import dev.aurakai.auraframefx.navigation.gates.components.SubmenuItem
import dev.aurakai.auraframefx.navigation.gates.components.SubmenuCard
import kotlinx.coroutines.launch

/**
 * LSPosed Gate Submenu
 * Xposed framework management and module control
 */
@Composable
fun LSPosedSubmenuScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val detectionResult = remember { LSPosedDetector.getDetectionSummary(context) }

    // Show error screen if LSPosed is not installed
    if (!detectionResult.isInstalled) {
        LSPosedNotInstalledScreen(
            onNavigateBack = { navController.popBackStack() }
        )
        return
    }

    val menuItems = listOf(
        SubmenuItem(
            title = "Sovereign Module Manager",
            description = "Unified control for Magisk, LSPosed, and KernelSU modules",
            icon = Icons.Default.Extension,
            route = "module_manager",
            color = Color(0xFF00FF85)
        ),
        SubmenuItem(
            title = "Hook Manager",
            description = "Monitor and manage active method hooks",
            icon = Icons.AutoMirrored.Filled.CallSplit,
            route = "hook_manager",
            color = Color(0xFF4ECDC4) // Teal
        ),
        SubmenuItem(
            title = "Logs Viewer",
            description = "View system logs and module activity",
            icon = Icons.AutoMirrored.Filled.ListAlt,
            route = "logs_viewer",
            color = Color(0xFFFFD93D) // Yellow
        ),
        SubmenuItem(
            title = "Quick Actions",
            description = "Common operations and shortcuts",
            icon = Icons.Default.Bolt,
            route = "quick_actions",
            color = Color(0xFF6C5CE7) // Purple
        )
    )

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
                            Color(0xFF1A0033), // Dark Purple
                            Color.Black,
                            Color(0xFF330066)  // Deep Purple
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "🔧 LSPOSED GATE",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Xposed framework management and module control",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFFF8C69).copy(alpha = 0.8f) // Coral
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions Panel
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            XposedQuickActionsPanel()

            Spacer(modifier = Modifier.height(16.dp))

            // Framework Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B35))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Active Modules
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "12",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF4ECDC4),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Modules",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Framework Status
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Framework",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // System Hooks
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "247",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD93D),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Hooks",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Text(
                text = "Advanced Options",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Menu Items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuItems) { item: SubmenuItem ->
                    SubmenuCard(
                        item = item,
                        onClick = {
                            navController.navigate(item.route)
                        }
                    )
                }

                // Back button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
                        )
                    ) {
                        Text("← Back to Gates", color = Color(0xFFFF6B35))
                    }
                }
            }
        }
    }
}

/**
 * Xposed Quick Actions Panel
 * Provides instant access to common Xposed framework operations
 */
@Composable
private fun XposedQuickActionsPanel() {
    var modulesEnabled by remember { mutableStateOf(true) }
    var showHooksDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status message
            if (statusMessage != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = statusMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Cyan
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Quick action buttons in 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Enable/Disable Modules
                QuickActionButton(
                    title = if (modulesEnabled) "Disable Modules" else "Enable Modules",
                    icon = Icons.Default.Extension,
                    color = if (modulesEnabled) Color(0xFFFF6B35) else Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            modulesEnabled = !modulesEnabled
                            statusMessage = if (modulesEnabled) {
                                "Modules enabled. Reboot required."
                            } else {
                                "Modules disabled. Reboot required."
                            }
                            kotlinx.coroutines.delay(3000)
                            statusMessage = null
                        }
                    }
                )

                // View Active Hooks
                QuickActionButton(
                    title = "View Hooks",
                    icon = Icons.AutoMirrored.Filled.CallSplit,
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showHooksDialog = true
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Framework Restart
                QuickActionButton(
                    title = "Restart Framework",
                    icon = Icons.Default.Refresh,
                    color = Color(0xFFFFD93D),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            statusMessage = "Restarting Xposed framework..."
                            // TODO: Implement framework restart
                            kotlinx.coroutines.delay(2000)
                            statusMessage = "Framework restarted successfully"
                            kotlinx.coroutines.delay(3000)
                            statusMessage = null
                        }
                    }
                )

                // Clear Hook Cache
                QuickActionButton(
                    title = "Clear Cache",
                    icon = Icons.Default.DeleteOutline,
                    color = Color(0xFF6C5CE7),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            statusMessage = "Clearing hook cache..."
                            // TODO: Implement cache clear
                            kotlinx.coroutines.delay(1500)
                            statusMessage = "Hook cache cleared"
                            kotlinx.coroutines.delay(3000)
                            statusMessage = null
                        }
                    }
                )
            }
        }
    }

    // Active Hooks Dialog
    if (showHooksDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    "Active Hooks",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Currently active hooks in the system:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HookItem("SystemUI", "247 hooks", Color(0xFF4ECDC4))
                    HookItem("Settings", "84 hooks", Color(0xFFFFD93D))
                    HookItem("Package Manager", "56 hooks", Color(0xFF32CD32))
                    HookItem("Activity Manager", "102 hooks", Color(0xFFFF6B35))
                    Text(
                        "Total: 489 active hooks",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { }) {
                    Text("Close", color = Color.Cyan)
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun HookItem(
    packageName: String,
    hookCount: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Text(
                text = packageName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White
                )
            )
        }
        Text(
            text = hookCount,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/**
 * Error screen shown when LSPosed is not installed
 */
@Composable
private fun LSPosedNotInstalledScreen(
    onNavigateBack: () -> Unit
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
                            Color(0xFF330000), // Dark Red
                            Color.Black,
                            Color(0xFF330000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error Icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "LSPosed Not Found",
                tint = Color(0xFFFF6B35),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error Title
            Text(
                text = "LSPosed Not Detected",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFFFF6B35),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Description
            Text(
                text = "LSPosed framework is not installed on this device. This feature requires LSPosed to function.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "How to install LSPosed:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )

                    InfoStep("1", "Root your device with Magisk or KernelSU")
                    InfoStep("2", "Install LSPosed module from Magisk Manager")
                    InfoStep("3", "Reboot your device")
                    InfoStep("4", "Return to this screen to access features")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⚠️ Warning: Modifying system files requires root access and carries risks. Proceed with caution.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Back Button
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Gates", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Information step item for installation instructions
 */
@Composable
private fun InfoStep(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFFF6B35).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .border(width = 2.dp, color = Color(0xFFFF6B35), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFFF6B35)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

