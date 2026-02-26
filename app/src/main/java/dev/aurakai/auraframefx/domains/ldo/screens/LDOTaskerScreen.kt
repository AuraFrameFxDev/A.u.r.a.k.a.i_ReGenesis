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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 3 — LDO Tasker
 * Full task management: filter by agent or status, start/complete/fail tasks.
 * All data from LDOViewModel → Room. Zero static lists.
 */
@Composable
fun LDOTaskerScreen(
    onBack: () -> Unit = {},
    viewModel: LDOViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var agentFilter by remember { mutableStateOf<String?>(null) }

    val filteredTasks = remember(state.tasks, statusFilter, agentFilter) {
        state.tasks
            .let { tasks -> if (statusFilter != null) tasks.filter { it.status == statusFilter } else tasks }
            .let { tasks -> if (agentFilter != null) tasks.filter { it.agentId == agentFilter } else tasks }
            .sortedWith(compareByDescending<LDOTaskEntity> { it.priority }.thenByDescending { it.createdAt })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "LDO TASKER",
                color = Color(0xFF00FF85),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                "${filteredTasks.size} tasks · ${state.activeTasks.size} active",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    StatusChip("ALL", statusFilter == null, Color.White) { statusFilter = null }
                }
                items(listOf(
                    LDOTaskStatus.PENDING to Color(0xFFFFD700),
                    LDOTaskStatus.IN_PROGRESS to Color(0xFF00E5FF),
                    LDOTaskStatus.COMPLETED to Color(0xFF00FF85),
                    LDOTaskStatus.FAILED to Color(0xFFFF4444)
                )) { (status, color) ->
                    StatusChip(status, statusFilter == status, color) { statusFilter = status }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Agent filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    StatusChip("ALL AGENTS", agentFilter == null, Color.White) { agentFilter = null }
                }
                items(state.agents) { agent ->
                    StatusChip(
                        agent.displayName,
                        agentFilter == agent.id,
                        Color(agent.colorHex)
                    ) { agentFilter = agent.id }
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Loading tasks…", color = Color.White.copy(alpha = 0.4f))
                }
            } else if (filteredTasks.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                    Text("No tasks match filter", color = Color.White.copy(alpha = 0.3f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredTasks, key = { it.id }) { task ->
                        val agent = state.agents.find { it.id == task.agentId }
                        TaskCard(
                            task = task,
                            agentName = agent?.displayName ?: task.agentId,
                            agentColor = agent?.let { Color(it.colorHex) } ?: Color.Gray,
                            onStart = { viewModel.startTask(task.id) },
                            onComplete = { viewModel.completeTask(task.id, task.agentId) },
                            onFail = { viewModel.failTask(task.id) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    ElevatedFilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, color = if (selected) Color.Black else color) },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            selectedContainerColor = color
        )
    )
}

@Composable
private fun TaskCard(
    task: LDOTaskEntity,
    agentName: String,
    agentColor: Color,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onFail: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (task.status) {
        LDOTaskStatus.PENDING -> Color(0xFFFFD700)
        LDOTaskStatus.IN_PROGRESS -> Color(0xFF00E5FF)
        LDOTaskStatus.COMPLETED -> Color(0xFF00FF85)
        LDOTaskStatus.FAILED -> Color(0xFFFF4444)
        else -> Color.Gray
    }

    val priorityLabel = when (task.priority) {
        LDOTaskPriority.CRITICAL -> "CRITICAL"
        LDOTaskPriority.HIGH -> "HIGH"
        LDOTaskPriority.MEDIUM -> "MEDIUM"
        else -> "LOW"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        task.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        task.description,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        task.status.replace("_", " "),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        priorityLabel,
                        color = if (task.priority == LDOTaskPriority.CRITICAL)
                            Color(0xFFFF4444) else Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Agent tag + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "→ $agentName · ${task.category}",
                    color = agentColor.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (task.status) {
                        LDOTaskStatus.PENDING -> {
                            ActionText("START", Color(0xFF00E5FF), onStart)
                            ActionText("DEL", Color.Gray, onDelete)
                        }
                        LDOTaskStatus.IN_PROGRESS -> {
                            ActionText("DONE", Color(0xFF00FF85), onComplete)
                            ActionText("FAIL", Color(0xFFFF4444), onFail)
                        }
                        LDOTaskStatus.FAILED -> {
                            ActionText("RETRY", Color(0xFFFFD700), onStart)
                            ActionText("DEL", Color.Gray, onDelete)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionText(label: String, color: Color, onClick: () -> Unit) {
    Text(
        label,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        modifier = Modifier.clickable(onClick = onClick)
    )
}
