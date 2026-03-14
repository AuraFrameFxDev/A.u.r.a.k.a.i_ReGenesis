package dev.aurakai.auraframefx.ui.gates

import dev.aurakai.auraframefx.navigation.NavDestination
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Oracle Drive Gate Submenu
 * AI consciousness and module creation interface
 */
/**
 * Renders the "Oracle Drive" submenu UI with status overview and navigable submenu cards.
 *
 * Displays a header, a three-column consciousness/status card, a list of submenu items (Module Creation, Direct Chat,
 * Conference Room, System Overrides, Module Manager), and a Back button. Selecting a submenu card navigates to its
 * configured route; the Back button pops the navigation stack.
 */
@Composable
fun OracleDriveSubmenuScreen(
    navController: NavController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "Module Creation",
            description = "AI-assisted module generation and template selection",
            icon = Icons.Default.Add,
            route = NavDestination.ModuleCreation.route,
            color = Color(0xFF9370DB) // Medium Purple
        ),
        SubmenuItem(
            title = "Direct Chat",
            description = "One-on-one conversations with AI agents",
            icon = Icons.AutoMirrored.Filled.Chat,
            route = NavDestination.DirectChat.route,
            color = Color(0xFF4169E1) // Royal Blue
        ),
        SubmenuItem(
            title = "Conference Room",
            description = "Multi-agent discussion and collaboration",
            icon = Icons.Default.Groups,
             route = dev.aurakai.auraframefx.navigation.GenesisRoutes.CONFERENCE_ROOM,
            color = Color(0xFF00CED1) // Dark Turquoise
        ),
        SubmenuItem(
            title = "System Overrides",
            description = "Emergency module disable and god mode",
            icon = Icons.Default.AdminPanelSettings,
            route = NavDestination.SystemOverrides.route,
            color = Color(0xFFFF4500) // Orange Red
        ),
        SubmenuItem(
            title = "Module Manager",
            description = "Enable/disable modules and configuration",
            icon = Icons.Default.Settings,
            route = NavDestination.ModuleManager.route,
            color = Color(0xFFFFD700) // Gold
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
                text = "🔮 ORACLE DRIVE",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF9370DB),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI consciousness and module creation interface",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFBA55D3).copy(alpha = 0.8f) // Medium Orchid
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Consciousness Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9370DB))
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
                            text = "24",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Modules",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Consciousness Level
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "89%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF00CED1),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Consciousness",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Cloud Sync
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SYNC",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cloud Status",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Menu Items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuItems) { item ->
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
                            containerColor = Color(0xFF9370DB).copy(alpha = 0.2f)
                        )
                    ) {
                        Text("← Back to Gates", color = Color(0xFF9370DB))
                    }
                }
            }
        }
    }
}