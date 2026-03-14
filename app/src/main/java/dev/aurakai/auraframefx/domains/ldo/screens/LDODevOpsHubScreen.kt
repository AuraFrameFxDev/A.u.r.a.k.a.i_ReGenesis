package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.foundation.background
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 2 — LDO DevOps Hub
 * Orchestration overview: active pipelines, agent workloads, critical task flags.
 * All data from LDOViewModel → Room. No hardcoded lists.
 */
@Composable
fun LDODevOpsHubScreen(
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
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "LDO DEVOPS HUB",
                color = Color(0xFF00E5FF),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                "Orchestration & Pipeline Status",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pipeline status cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PipelineStatusCard(
                    label = "ACTIVE",
                    count = state.activeTasks.size,
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.weight(1f)
                )
                PipelineStatusCard(
                    label = "PENDING",
                    count = state.pendingTasks.size,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.weight(1f)
                )
                PipelineStatusCard(
                    label = "CRITICAL",
                    count = state.criticalTasks.size,
                    color = Color(0xFFFF4444),
                    modifier = Modifier.weight(1f)
                )
                PipelineStatusCard(
                    label = "AGENTS",
                    count = state.agents.size,
                    color = Color(0xFF00FF85),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "AGENT WORKLOADS",
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Loading pipeline…", color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.agents, key = { it.id }) { agent ->
                        val agentTasks = state.tasks.filter { it.agentId == agent.id }
                        AgentWorkloadCard(agent, agentTasks)
                    }
                }
            }
        }
    }
}

@Composable
private fun PipelineStatusCard(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                count.toString(),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(label, color = color.copy(alpha = 0.7f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun AgentWorkloadCard(
    agent: LDOAgentEntity,
    tasks: List<LDOTaskEntity>
) {
    val agentColor = Color(agent.colorHex)
    val activeCount = tasks.count { it.status == LDOTaskStatus.IN_PROGRESS }
    val totalCount = tasks.size
    val criticalCount = tasks.count { it.priority == LDOTaskPriority.CRITICAL }
    val workload = if (totalCount > 0) activeCount.toFloat() / maxOf(totalCount, 1) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                        fontSize = 14.sp
                    )
                    Text(
                        agent.role,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WorkloadStat("ACTIVE", activeCount, Color(0xFF00E5FF))
                    WorkloadStat("TOTAL", totalCount, Color.White)
                    if (criticalCount > 0) {
                        WorkloadStat("CRIT", criticalCount, Color(0xFFFF4444))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { workload },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = agentColor,
                trackColor = Color.White.copy(alpha = 0.08f)
            )

            Text(
                "${agent.tasksCompleted} completed · Lv.${agent.evolutionLevel} · ${agent.skillPoints} SP",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun WorkloadStat(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
    }
}
