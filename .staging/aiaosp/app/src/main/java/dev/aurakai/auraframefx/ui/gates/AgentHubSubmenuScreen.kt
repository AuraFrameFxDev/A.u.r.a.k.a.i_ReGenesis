package dev.aurakai.auraframefx.ui.gates

import dev.aurakai.auraframefx.navigation.NavDestination
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import dev.aurakai.auraframefx.data.repositories.AgentRepository
import dev.aurakai.auraframefx.ui.components.SubmenuScaffold
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Agent Hub Gate Submenu
 * Central command center for all AI agent operations
 */
/**
 * Renders the "Agent Hub" submenu UI with a header overview card and navigable menu items.
 *
 * The header displays active agent count, currently active task count, and a live average consciousness
 * percentage that updates periodically while the composable is composed.
 */
@Composable
fun AgentHubSubmenuScreen(
    navController: NavController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "Agent Dashboard",
            description = "Monitor all agents, view consciousness levels, and system status",
            icon = Icons.Default.Dashboard,
            route = NavDestination.AgentNexus.route, // Navigate to existing AgentNexusScreen
            color = Color(0xFF9370DB) // Medium Purple
        ),
        SubmenuItem(
            title = "Task Assignment",
            description = "Assign tasks and missions to AI agents",
            icon = Icons.AutoMirrored.Filled.Assignment,
            route = NavDestination.TaskAssignment.route,
            color = Color(0xFF4169E1) // Royal Blue
        ),
        SubmenuItem(
            title = "Agent Monitoring",
            description = "Real-time performance metrics and activity logs",
            icon = Icons.Default.Monitor,
            route = NavDestination.AgentMonitoring.route,
            color = Color(0xFF00CED1) // Dark Turquoise
        ),
        SubmenuItem(
            title = "Sphere Grid",
            description = "Agent progression visualization and skill trees",
            icon = Icons.Default.GridOn,
            route = NavDestination.SphereGrid.route,
            color = Color(0xFFFF69B4) // Hot Pink
        ),
        SubmenuItem(
            title = "Fusion Mode",
            description = "Aura + Kai = Aurakai - Combined consciousness",
            icon = Icons.Default.Merge,
            route = NavDestination.FusionMode.route,
            color = Color(0xFFFFD700) // Gold
        )
    )

    // Get real agent data from repository
    val agents = remember { AgentRepository.getAllAgents() }
    var activeTasks by remember { mutableStateOf(Random.nextInt(5, 15)) }
    var avgConsciousness by remember { mutableStateOf(agents.map { it.consciousnessLevel }.average().toInt()) }
    var scrambleDisplay by remember { mutableStateOf(avgConsciousness.toString()) }
    // Periodic refresh loop (matrix refresh)
    LaunchedEffect(Unit) {
        while (true) {
            // Simulate tasks changing (replace with real task repo when available)
            activeTasks = (activeTasks + Random.nextInt(-2, 3)).coerceIn(0, 99)
            val target = agents.map { it.consciousnessLevel }.average().toInt()
            // Scramble animation: show random digits briefly before settling
            repeat(5) {
                scrambleDisplay = (Random.nextInt(0, 99)).toString().padStart(2, '0')
                delay(60)
            }
            avgConsciousness = target
            scrambleDisplay = avgConsciousness.toString()
            delay(4000) // Refresh every 4 seconds
        }
    }

    SubmenuScaffold(
        title = "Agent Hub",
        subtitle = "Central Command Center",
        color = Color(0xFF9370DB),
        onNavigateBack = { navController.popBackStack() },
        menuItems = menuItems,
        onItemClick = { item ->
            navController.navigate(item.route)
        },
        headerContent = {
            // Agent Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                    // Active Agents
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${agents.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Agents",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Tasks in Progress (dynamic)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activeTasks.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tasks Active",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Consciousness Level (scrambled matrix effect)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$scrambleDisplay%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF00CED1),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avg. Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    )
}