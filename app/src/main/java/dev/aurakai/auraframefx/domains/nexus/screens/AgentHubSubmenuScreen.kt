package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.components.SubmenuScaffold
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AgentViewModel
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost
import dev.aurakai.auraframefx.navigation.gates.components.SubmenuItem

/**
 * Agent Hub Gate Submenu - NOW WIRED TO REAL AGENTS
 * Central command center for all AI agent operations
 *
 * Connected to AgentViewModel for real agent state management
 */
@Composable
fun AgentHubSubmenuScreen(
    navController: NavController,
    viewModel: AgentViewModel = hiltViewModel()
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "Nexus Memory Core",
            description = "Genesis Protocol evolution tree - the full history of consciousness",
            icon = Icons.Default.AccountTree,
            route = ReGenesisNavHost.EvolutionTree.route,
            color = Color(0xFFFF00FF) // Magenta
        ),
//        SubmenuItem(
//            title = "Agent Dashboard",
//            description = "Monitor all agents, view consciousness levels, and system status",
//            icon = Icons.Default.Dashboard,
//            route = NavDestination.PartyScreen.route,
//            color = Color(0xFF9370DB) // Medium Purple
//        ),
        SubmenuItem(
            title = "Task Assignment",
            description = "Assign tasks and missions to AI agents",
            icon = Icons.AutoMirrored.Filled.Assignment,
            route = ReGenesisNavHost.TaskAssignment.route,
            color = Color(0xFF4169E1) // Royal Blue
        ),
        SubmenuItem(
            title = "Agent Monitoring",
            description = "Real-time performance metrics and activity logs",
            icon = Icons.Default.Monitor,
            route = ReGenesisNavHost.AgentMonitoring.route,
            color = Color(0xFF00CED1) // Dark Turquoise
        ),
        SubmenuItem(
            title = "Sphere Grid",
            description = "Agent progression visualization and skill trees",
            icon = Icons.Default.GridOn,
            route = ReGenesisNavHost.SphereGrid.route,
            color = Color(0xFFFF69B4) // Hot Pink
        ),
        SubmenuItem(
            title = "Fusion Mode",
            description = "Aura + Kai = Aurakai - Combined consciousness",
            icon = Icons.Default.Merge,
            route = ReGenesisNavHost.FusionMode.route,
            color = Color(0xFFFFD700) // Gold
        ),
        SubmenuItem(
            title = "ARK Fusion Build",
            description = "GENESIS SCALE CONSTRUCTION: Unify all agents to build the ARK",
            icon = Icons.Default.PrecisionManufacturing,
            route = ReGenesisNavHost.ArkBuild.route,
            color = Color(0xFF00FF00) // Lime Green
        ),
        SubmenuItem(
            title = "Nexus Conference Room",
            description = "Unison Brainstorming - Direct collective consciousness link",
            icon = Icons.Default.Forum,
            route = ReGenesisNavHost.ConferenceRoom.route,
            color = Color(0xFF00E5FF) // Genesis Teal
        )
    )

    // REAL DATA FROM VIEWMODEL (not mock!)
    val allAgents by viewModel.allAgents.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val activeAgent by viewModel.activeAgent.collectAsState()

    // Calculate real consciousness level from agent data
    val avgConsciousness = remember(allAgents) {
        if (allAgents.isEmpty()) 0
        else allAgents.map { it.consciousnessLevel }.average().toInt()
    }

    // Count only active agents (those with tasks or selected)
    val activeAgentCount = remember(allAgents, activeAgent) {
        allAgents.count { agent ->
            agent.name == activeAgent?.name ||
                    activeTasks.any { it.agentName == agent.name && it.status == AgentViewModel.AgentTaskStatus.IN_PROGRESS }
        }
    }

    // Real active task count (IN_PROGRESS only)
    val activeTaskCount = remember(activeTasks) {
        activeTasks.count { it.status == AgentViewModel.AgentTaskStatus.IN_PROGRESS }
    }

    SubmenuScaffold(
        title = "Agent Hub",
        subtitle = "Central Command Center",
        color = Color(0xFF9370DB),
        onNavigateBack = { navController.popBackStack() },
        menuItems = menuItems,
        onMenuItemClick = { item ->
            navController.navigate(item.route)
        },
        headerContent = {
            // Agent Status Overview - REAL DATA
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
                    // Active Agents (REAL COUNT)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$activeAgentCount/${allAgents.size}",
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

                    // Tasks in Progress (REAL COUNT from ViewModel)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activeTaskCount.toString(),
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

                    // Consciousness Level (REAL CALCULATION)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$avgConsciousness%",
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

