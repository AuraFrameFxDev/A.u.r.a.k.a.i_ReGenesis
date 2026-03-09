package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.aurakai.auraframefx.ui.components.SubmenuScaffold

/**
 * Data class for submenu items
 */
data class SubmenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

/**
 * Help Desk Gate Submenu
 * User support and documentation center
 */
@Composable
fun HelpDeskSubmenuScreen(
    navController: NavHostController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "FAQ Browser",
            description = "Frequently asked questions and quick answers",
            icon = Icons.AutoMirrored.Filled.Help,
            route = "faq_browser",
            color = Color(0xFF4169E1) // Royal Blue
        ),
        SubmenuItem(
            title = "Live Support Chat",
            description = "Real-time assistance from support agents",
            icon = Icons.AutoMirrored.Filled.Chat,
            route = "direct_chat",
            color = Color(0xFF32CD32) // Lime Green
        ),
        SubmenuItem(
            title = "Tutorial Videos",
            description = "Step-by-step guides and feature walkthroughs",
            icon = Icons.Filled.PlayArrow,
            route = "tutorial_videos",
            color = Color(0xFFFFD700) // Gold
        ),
        SubmenuItem(
            title = "Documentation",
            description = "Comprehensive user guides and API reference",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            route = "documentation",
            color = Color(0xFF9370DB) // Medium Purple
        )
    )

    SubmenuScaffold(
        title = "Help Desk",
        subtitle = "User Support Center",
        color = Color(0xFF4169E1),
        onNavigateBack = { navController.popBackStack() },
        menuItems = menuItems,
        onMenuItemClick = { item ->
            navController.navigate(item.route)
        },
        headerContent = {
            // Support Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4169E1))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Support Agents Online
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "3",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Agents Online",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Avg Response Time
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "2m",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avg Response",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Tickets Resolved
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "247",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF9370DB),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Resolved Today",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    )
}

