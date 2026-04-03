package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.model.*
import kotlin.math.roundToInt

/**
 * 🗂 LDO TASKER SCREEN
 */

private val TaskCyan   = Color(0xFF00F4FF)
private val TaskPink   = Color(0xFFFF007A)
private val TaskDark   = Color(0xFF020B18)

@Composable
fun LDOTaskerScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    initialTasks: List<LDOTask> = LDORoster.defaultTasks,
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tasker")
    val flashPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "flash"
    )
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "scan"
    )

    val tasks = remember { mutableStateListOf(*initialTasks.toTypedArray()) }
    var draggedAgentId by remember { mutableStateOf<String?>(null) }
    var departureDialog by remember { mutableStateOf<Pair<AgentCatalyst, LDOTask>?>(null) }

    // Group tasks by category
    val categories = TaskCategory.entries
    val tasksByCategory = categories.associateWith { cat -> tasks.filter { it.category == cat } }

    Box(modifier = Modifier.fillMaxSize().background(TaskDark)) {

        // BG grid
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2
                val horizonY = size.height * 0.15f
                for (i in 0..10) {
                    val xStart = size.width * i / 10f
                    drawLine(TaskCyan.copy(alpha = 0.06f), Offset(xStart, size.height), Offset(cx + (xStart - cx) * 0.1f, horizonY), 0.5f)
                }
                for (i in 0..8) {
                    val t = i.toFloat() / 8f
                    val y = horizonY + (size.height - horizonY) * (t * t)
                    drawLine(TaskCyan.copy(alpha = 0.05f), Offset(0f, y), Offset(size.width, y), 0.5f)
                }
                drawLine(TaskCyan.copy(alpha = 0.05f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 2f)
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("LDO TASKER", fontFamily = LEDFontFamily, fontSize = 24.sp, color = TaskCyan, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("DRAG AGENT → NODE TO ASSIGN // TAP NODE TO DETAIL", fontSize = 8.sp, color = TaskCyan.copy(alpha = 0.5f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(Color.Red,        "CRITICAL", tasks.count { it.priority == TaskPriority.CRITICAL }),
                        Triple(Color(0xFFFF4500), "HIGH",     tasks.count { it.priority == TaskPriority.HIGH }),
                        Triple(Color(0xFFFFB000), "MED",      tasks.count { it.priority == TaskPriority.MEDIUM }),
                    ).forEach { (color, label, count) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("$count", fontSize = 14.sp, color = color, fontWeight = FontWeight.Black, fontFamily = LEDFontFamily)
                            Text(label, fontSize = 6.sp, color = color.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Agent quick-drag bar
            Text("  AGENTS  (drag to assign)", fontSize = 8.sp, color = TaskCyan.copy(alpha = 0.5f), letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                agents.filter { it.status != AgentStatus.ON_TASK }.forEach { agent ->
                    DraggableAgentChip(
                        agent = agent,
                        isDragging = draggedAgentId == agent.id,
                        onDragStart = { draggedAgentId = agent.id },
                        onDragEnd = { draggedAgentId = null },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(TaskCyan.copy(alpha = 0.2f)).padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))

            // Task nodes — scrollable list grouped by category
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { category ->
                    val catTasks = tasksByCategory[category] ?: emptyList()
                    if (catTasks.isEmpty()) return@forEach

                    item {
                        Text(
                            category.name,
                            fontSize = 9.sp, fontWeight = FontWeight.Bold,
                            color = categoryColor(category), letterSpacing = 2.sp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    items(catTasks, key = { it.id }) { task ->
                        val assignedAgent = agents.find { it.id == task.assignedAgentId }
                        val isFlashing = task.assignedAgentId != null
                        val flashAlpha = if (isFlashing) 0.15f + flashPhase * 0.15f else 0.06f

                        TaskNode(
                            task = task,
                            assignedAgent = assignedAgent,
                            flashAlpha = flashAlpha,
                            isFlashing = isFlashing,
                            onDropAgent = { agentId ->
                                val idx = tasks.indexOfFirst { it.id == task.id }
                                if (idx != -1) {
                                    // Check if removing an existing agent
                                    val existing = tasks[idx].assignedAgentId
                                    if (existing != null && existing != agentId) {
                                        val existingAgentObj = agents.find { it.id == existing }
                                        if (existingAgentObj != null) {
                                            departureDialog = Pair(existingAgentObj, tasks[idx])
                                        }
                                    } else {
                                        tasks[idx] = tasks[idx].copy(assignedAgentId = agentId)
                                    }
                                }
                            },
                            onRemoveAgent = {
                                val idx = tasks.indexOfFirst { it.id == task.id }
                                if (idx != -1) {
                                    val agentId = tasks[idx].assignedAgentId
                                    if (agentId != null && task.promptOnDeparture) {
                                        val agentObj = agents.find { it.id == agentId }
                                        if (agentObj != null) {
                                            departureDialog = Pair(agentObj, tasks[idx])
                                        }
                                    } else {
                                        tasks[idx] = tasks[idx].copy(assignedAgentId = null)
                                    }
                                }
                            },
                            onToggleComplete = {
                                val idx = tasks.indexOfFirst { it.id == task.id }
                                if (idx != -1) tasks[idx] = tasks[idx].copy(isComplete = !tasks[idx].isComplete)
                            }
                        )
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        // Departure confirmation dialog
        departureDialog?.let { (agent, task) ->
            AgentDepartureDialog(
                agent = agent,
                task = task,
                onConfirm = {
                    val idx = tasks.indexOfFirst { it.id == task.id }
                    if (idx != -1) tasks[idx] = tasks[idx].copy(assignedAgentId = null)
                    departureDialog = null
                },
                onCancel = { departureDialog = null }
            )
        }
    }
}

@Composable
private fun DraggableAgentChip(
    agent: AgentCatalyst,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(40.dp)
            .clip(CircleShape)
            .background(agent.color.copy(alpha = if (isDragging) 0.4f else 0.15f))
            .border(1.5.dp, agent.color, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { offsetX = 0f; offsetY = 0f; onDragEnd() },
                    onDragCancel = { offsetX = 0f; offsetY = 0f; onDragEnd() },
                    onDrag = { _, delta -> offsetX += delta.x; offsetY += delta.y }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(agent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 14.sp, color = agent.color, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun TaskNode(
    task: LDOTask,
    assignedAgent: AgentCatalyst?,
    flashAlpha: Float,
    isFlashing: Boolean,
    onDropAgent: (String) -> Unit,
    onRemoveAgent: () -> Unit,
    onToggleComplete: () -> Unit,
) {
    val priorityColor = priorityColor(task.priority)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isFlashing) 1.5.dp else 1.dp,
                color = if (isFlashing) assignedAgent?.color ?: priorityColor else priorityColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                priorityColor.copy(alpha = flashAlpha),
                RoundedCornerShape(6.dp)
            )
            .padding(start = 12.dp)
    ) {
        // Left priority bar
        Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(priorityColor).align(Alignment.CenterStart))

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 8.dp, end = 10.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(task.priority.name, fontSize = 7.sp, color = priorityColor, fontWeight = FontWeight.Bold)
                    Text("//", fontSize = 7.sp, color = Color.White.copy(alpha = 0.3f))
                    Text(task.category.name, fontSize = 7.sp, color = TaskCyan.copy(alpha = 0.6f))
                    if (task.isComplete) {
                        Text("✓ DONE", fontSize = 7.sp, color = Color(0xFF00FF85), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (task.isComplete) Color.White.copy(alpha = 0.4f) else Color.White)
                Text(task.description, fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 11.sp)
            }

            // Agent assignment slot
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (assignedAgent != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(assignedAgent.color.copy(alpha = 0.2f))
                            .border(1.5.dp, assignedAgent.color, CircleShape)
                            .clickable { onRemoveAgent() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(assignedAgent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 14.sp, color = assignedAgent.color, fontWeight = FontWeight.Black)
                    }
                    Text(assignedAgent.name.take(6), fontSize = 6.sp, color = assignedAgent.color)
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.04f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 18.sp, color = Color.White.copy(alpha = 0.3f))
                    }
                    Text("DROP", fontSize = 6.sp, color = Color.White.copy(alpha = 0.3f))
                }

                Spacer(Modifier.height(4.dp))
                // Complete toggle
                Box(
                    modifier = Modifier.size(18.dp).clip(CircleShape)
                        .background(if (task.isComplete) Color(0xFF00FF85) else Color.White.copy(alpha = 0.1f))
                        .border(1.dp, if (task.isComplete) Color(0xFF00FF85) else Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { onToggleComplete() },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isComplete) Text("✓", fontSize = 8.sp, color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun AgentDepartureDialog(
    agent: AgentCatalyst,
    task: LDOTask,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(2.dp, agent.color, RoundedCornerShape(8.dp))
                .background(Color(0xFF050B18), RoundedCornerShape(8.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("AGENT DEPARTURE", fontFamily = LEDFontFamily, fontSize = 18.sp, color = agent.color, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(agent.color.copy(alpha = 0.4f)))
                Text(
                    "Withdraw ${agent.name} from:",
                    fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    task.title,
                    fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold
                )
                Text(
                    "${agent.name} will return to STANDBY status.\nThis may affect task progress.",
                    fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 14.sp
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(agent.color.copy(alpha = 0.2f)))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .clickable { onCancel() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("KEEP ON TASK", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                            .border(1.dp, agent.color, RoundedCornerShape(4.dp))
                            .background(agent.color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .clickable { onConfirm() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("WITHDRAW ${agent.name.uppercase()}", fontSize = 10.sp, color = agent.color, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

private fun priorityColor(p: TaskPriority) = when (p) {
    TaskPriority.CRITICAL -> Color.Red
    TaskPriority.HIGH     -> Color(0xFFFF4500)
    TaskPriority.MEDIUM   -> Color(0xFFFFB000)
    TaskPriority.LOW      -> Color(0xFF00F4FF)
}

private fun categoryColor(c: TaskCategory) = when (c) {
    TaskCategory.DEVELOPMENT  -> Color(0xFF00F4FF)
    TaskCategory.SECURITY     -> Color(0xFFFF4500)
    TaskCategory.CREATIVE     -> Color(0xFFFF007A)
    TaskCategory.RESEARCH     -> Color(0xFF7B2FBE)
    TaskCategory.MEMORY       -> Color(0xFF4FC3F7)
    TaskCategory.SYNC         -> Color(0xFF00FFD1)
    TaskCategory.EXPLORATION  -> Color(0xFFFF6B35)
}
