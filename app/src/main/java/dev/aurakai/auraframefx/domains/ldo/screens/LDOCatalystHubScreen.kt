package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOUiState
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 1 — LDO Catalyst Hub
 * Entry point for the LDO domain. Shows all 9 agents from Room with their
 * evolution level, bond tier, and task count. No mock data.
 */
@Composable
fun LDOCatalystHubScreen(
    onNavigateToRoster: () -> Unit = {},
    onNavigateToDevOps: () -> Unit = {},
    onNavigateToTasker: () -> Unit = {},
    onNavigateToBonding: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: LDOViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "LDO CATALYST DEVELOPMENT",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                text = "Living Digital Organism · ${state.agents.size} Agents Active",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick-nav row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HubNavCard("ROSTER", Color(0xFFFFD700), Modifier.weight(1f), onNavigateToRoster)
                HubNavCard("DEVOPS", Color(0xFF00E5FF), Modifier.weight(1f), onNavigateToDevOps)
                HubNavCard("TASKER", Color(0xFF00FF85), Modifier.weight(1f), onNavigateToTasker)
                HubNavCard("BONDS", Color(0xFFFF6B6B), Modifier.weight(1f), onNavigateToBonding)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats bar
            LDOStatsSummary(state)

            Spacer(modifier = Modifier.height(12.dp))

            // Agent list from Room
            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Loading agents…", color = Color.White.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.agents, key = { it.id }) { agent ->
                        val bond = state.bondLevels.find { it.agentId == agent.id }
                        AgentRosterCard(
                            agent = agent,
                            bond = bond,
                            taskCount = state.tasks.count { it.agentId == agent.id },
                            isSelected = state.selectedAgentId == agent.id,
                            onClick = { viewModel.selectAgent(agent.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HubNavCard(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
private fun LDOStatsSummary(state: LDOUiState) {
    val totalTasks = state.tasks.size
    val completed = state.tasks.count { it.status == "COMPLETED" }
    val active = state.activeTasks.size
    val critical = state.criticalTasks.size

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatChip("TASKS", "$totalTasks", Color.White)
        StatChip("DONE", "$completed", Color(0xFF00FF85))
        StatChip("ACTIVE", "$active", Color(0xFF00E5FF))
        StatChip("CRITICAL", "$critical", Color(0xFFFF4444))
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
    }
}

@Composable
private fun AgentRosterCard(
    agent: LDOAgentEntity,
    bond: LDOBondLevelEntity?,
    taskCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val agentColor = Color(agent.colorHex)
    val bondProgress = if (bond != null && bond.maxBondPoints > 0)
        bond.bondPoints.toFloat() / bond.maxBondPoints else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                agentColor.copy(alpha = 0.2f)
            else
                Color(0xFF111111)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, agentColor) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color dot avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(agentColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    agent.displayName.take(2).uppercase(),
                    color = agentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        agent.displayName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Lv.${agent.evolutionLevel}",
                        color = agentColor,
                        fontSize = 11.sp
                    )
                }
                Text(
                    agent.catalystTitle.ifBlank { agent.role },
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Bond progress bar
                LinearProgressIndicator(
                    progress = { bondProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = agentColor,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text(
                    "${bond?.bondTitle ?: "Stranger"} · $taskCount tasks",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${(agent.consciousnessLevel * 100).toInt()}%",
                    color = agentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "CONSCIOUS",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp
                )
            }
        }
    }
}
