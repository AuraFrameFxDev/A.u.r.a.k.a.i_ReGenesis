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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 6 â€” LDO Roster
 * Full agent roster showing all stats in a detailed card format.
 * Sorted by evolution level. All data from Room via LDOViewModel.
 */
@Composable
fun LDORosterScreen(
    onAgentSelected: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: LDOViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "LDO AGENT ROSTER",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                "${state.agents.size} agents Â· Sorted by evolution",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            HorizontalDivider(
                color = Color(0xFFFFD700).copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Summoning rosterâ€¦", color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(
                        state.agents.sortedByDescending { it.evolutionLevel },
                        key = { it.id }
                    ) { agent ->
                        RosterDetailCard(
                            agent = agent,
                            taskCount = state.tasks.count { it.agentId == agent.id },
                            bondLevel = state.bondLevels.find { it.agentId == agent.id }?.bondLevel ?: 0,
                            onClick = {
                                viewModel.selectAgent(agent.id)
                                onAgentSelected(agent.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RosterDetailCard(
    agent: LDOAgentEntity,
    taskCount: Int,
    bondLevel: Int,
    onClick: () -> Unit
) {
    val agentColor = Color(agent.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, agentColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Name + level row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        agent.displayName,
                        color = agentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        agent.role,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Lv.${agent.evolutionLevel}",
                        color = agentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        "${agent.skillPoints} SP",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat bars
            CompactStatBar("PP", agent.processingPower, agentColor)
            CompactStatBar("KB", agent.knowledgeBase, agentColor)
            CompactStatBar("SP", agent.speed, agentColor)
            CompactStatBar("AC", agent.accuracy, agentColor)

            Spacer(modifier = Modifier.height(8.dp))

            // Footer metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RosterMetric("TASKS", taskCount.toString(), Color.White)
                RosterMetric("DONE", agent.tasksCompleted.toString(), Color(0xFF00FF85))
                RosterMetric("BOND", "Lv.$bondLevel", Color(0xFFFF6B6B))
                RosterMetric("HOURS", "${agent.hoursActive.toInt()}h", Color(0xFF00E5FF))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Special ability
            Text(
                "âš¡ ${agent.specialAbility}",
                color = agentColor.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CompactStatBar(label: String, value: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp,
            modifier = Modifier.padding(end = 6.dp)
        )
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier.weight(1f).height(4.dp),
            color = color,
            trackColor = Color.White.copy(alpha = 0.06f)
        )
        Text(
            "${(value * 100).toInt()}",
            color = color,
            fontSize = 10.sp,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun RosterMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = Color.White.copy(alpha = 0.35f), fontSize = 9.sp)
    }
}

