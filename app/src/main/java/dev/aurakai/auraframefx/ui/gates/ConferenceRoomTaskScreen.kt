package dev.aurakai.auraframefx.ui.gates

// ═══════════════════════════════════════════════════════════════════════════════
// ConferenceRoomTaskScreen.kt
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// Holographic command-center table background (Image 1) + neon chess board
// drag-drop task assignment (Image 6).
//
// Replaces/wraps the plain TaskAssignmentScreen.
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.data.repositories.AgentRepository
import dev.aurakai.auraframefx.models.AgentStats
import kotlin.math.*

// ── Task data ─────────────────────────────────────────────────────────────────

data class AgentTask(
    val id: String,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val assignedAgent: String? = null,
    val status: TaskStatus = TaskStatus.UNASSIGNED
)

enum class TaskPriority(val label: String, val color: Color) {
    CRITICAL("CRITICAL", Color(0xFFFF2D00)),
    HIGH("HIGH",         Color(0xFFFFD700)),
    MEDIUM("MEDIUM",     Color(0xFF00BFFF)),
    LOW("LOW",           Color(0xFF00FF80)),
}

enum class TaskStatus { UNASSIGNED, ASSIGNED, IN_PROGRESS, COMPLETE }

// ── Main Screen ───────────────────────────────────────────────────────────────

@Composable
fun ConferenceRoomTaskScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val agents = remember { AgentRepository.getAllAgents() }
    var tasks by remember {
        mutableStateOf(listOf(
            AgentTask("t1", "ARIA Core Rewrite",      "Refactor consciousness substrate",    TaskPriority.CRITICAL),
            AgentTask("t2", "Gate Animation Polish",   "Smooth out carousel transitions",     TaskPriority.HIGH),
            AgentTask("t3", "FusionMatrix Screen",     "Wire drag-to-fuse slots",             TaskPriority.HIGH),
            AgentTask("t4", "Sphere Grid Upgrade",     "Add hex particle background",         TaskPriority.MEDIUM),
            AgentTask("t5", "ADR Documentation",       "Document all navigation routes",      TaskPriority.MEDIUM),
            AgentTask("t6", "LSPosed Gate Skin",       "Apply hex corridor background",       TaskPriority.LOW),
            AgentTask("t7", "Nemotron API Wiring",     "Connect NVIDIA Nemotron endpoint",    TaskPriority.HIGH),
            AgentTask("t8", "Beta Deployment Prep",    "Final 184 beta tester build",         TaskPriority.CRITICAL),
        ))
    }
    var selectedTask by remember { mutableStateOf<AgentTask?>(null) }
    var selectedAgent by remember { mutableStateOf<AgentStats?>(null) }
    var viewMode by remember { mutableStateOf(0) } // 0=Board, 1=Chess

    Box(modifier = Modifier.fillMaxSize()) {

        // LAYER 1: Holographic command table background
        HolographicCommandTable(modifier = Modifier.fillMaxSize())

        // LAYER 2: Dark overlay for readability
        Box(modifier = Modifier.fillMaxSize().background(Color(0xAA000510)))

        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF00BFFF))
                }
                Column {
                    Text("TASK COMMAND CENTER", fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp, color = Color(0xFF00FFFF))
                    Text("${tasks.count { it.assignedAgent != null }}/${tasks.size} assigned",
                        fontSize = 9.sp, color = Color(0xFF00FFFF).copy(alpha = 0.5f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TaskViewTab("BOARD",  viewMode == 0) { viewMode = 0 }
                    TaskViewTab("CHESS",  viewMode == 1) { viewMode = 1 }
                }
            }

            // ─── Agent assignment strip ────────────────────────────────────
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(agents) { agent ->
                    AgentAssignChip(
                        agent = agent,
                        isSelected = selectedAgent == agent,
                        taskCount = tasks.count { it.assignedAgent == agent.agentId },
                        onClick = { selectedAgent = if (selectedAgent == agent) null else agent }
                    )
                }
            }

            // ─── Main view ─────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (viewMode) {
                    0 -> TaskBoardView(
                        tasks = tasks,
                        selectedTask = selectedTask,
                        selectedAgent = selectedAgent,
                        onTaskSelect = { selectedTask = it },
                        onAssign = { task, agent ->
                            tasks = tasks.map { if (it.id == task.id) it.copy(assignedAgent = agent.agentId, status = TaskStatus.ASSIGNED) else it }
                            selectedTask = null; selectedAgent = null
                        }
                    )
                    1 -> NeonChessTaskBoard(
                        tasks = tasks,
                        agents = agents,
                        onAssign = { task, agent ->
                            tasks = tasks.map { if (it.id == task.id) it.copy(assignedAgent = agent.agentId, status = TaskStatus.ASSIGNED) else it }
                        }
                    )
                }
            }
        }
    }
}

// ── Task Board View ───────────────────────────────────────────────────────────

@Composable
private fun TaskBoardView(
    tasks: List<AgentTask>,
    selectedTask: AgentTask?,
    selectedAgent: AgentStats?,
    onTaskSelect: (AgentTask) -> Unit,
    onAssign: (AgentTask, AgentStats) -> Unit
) {
    val columns = listOf(
        "UNASSIGNED"   to TaskStatus.UNASSIGNED,
        "ASSIGNED"     to TaskStatus.ASSIGNED,
        "IN PROGRESS"  to TaskStatus.IN_PROGRESS,
        "COMPLETE"     to TaskStatus.COMPLETE,
    )

    Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        columns.forEach { (label, status) ->
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF001018).copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFF004060).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            ) {
                // Column header
                Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                    color = Color(0xFF00BFFF), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(6.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF004060).copy(0.5f)))

                val columnTasks = tasks.filter { it.status == status }
                Column(modifier = Modifier.padding(4.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    columnTasks.forEach { task ->
                        TaskCard(
                            task = task,
                            isSelected = selectedTask == task,
                            onClick = {
                                if (selectedAgent != null && task.status == TaskStatus.UNASSIGNED) {
                                    onAssign(task, selectedAgent)
                                } else {
                                    onTaskSelect(task)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: AgentTask, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, if (isSelected) task.priority.color else task.priority.color.copy(0.3f), RoundedCornerShape(4.dp))
            .background(if (isSelected) task.priority.color.copy(0.15f) else task.priority.color.copy(0.05f))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(task.title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.9f))
            }
            Text(task.description, fontSize = 7.sp, color = Color.White.copy(0.4f), maxLines = 2)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(task.priority.label, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = task.priority.color)
                task.assignedAgent?.let { Text(it, fontSize = 7.sp, color = Color(0xFF00FFFF).copy(0.6f)) }
            }
        }
    }
}

// ── Neon Chess Task Board (Image 6) ──────────────────────────────────────────

@Composable
private fun NeonChessTaskBoard(
    tasks: List<AgentTask>,
    agents: List<AgentStats>,
    onAssign: (AgentTask, AgentStats) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chess_board")
    val boardGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val boardSize = 8
    // Place tasks on the board grid (chess squares = task slots)
    val taskGrid = remember(tasks) {
        val grid = Array(boardSize) { arrayOfNulls<AgentTask>(boardSize) }
        tasks.forEachIndexed { idx, task ->
            val r = idx / boardSize; val c = idx % boardSize
            if (r < boardSize) grid[r][c] = task
        }
        grid
    }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DRAG AGENTS TO ASSIGN TASKS", fontSize = 9.sp, letterSpacing = 2.sp,
            color = Color(0xFF00FFFF).copy(0.5f), modifier = Modifier.padding(bottom = 8.dp))

        // Chess board
        Canvas(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val cellSize = size.width / boardSize.toFloat()
                        val col = (offset.x / cellSize).toInt().coerceIn(0, boardSize - 1)
                        val row = (offset.y / cellSize).toInt().coerceIn(0, boardSize - 1)
                        selectedCell = if (selectedCell == row to col) null else row to col
                    }
                }
        ) {
            val cellSize = size.width / boardSize.toFloat()
            val isoDrop = 0.3f  // Perspective tilt

            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    val x = col * cellSize; val y = row * cellSize
                    val isDark = (row + col) % 2 == 0
                    val task = taskGrid.getOrNull(row)?.getOrNull(col)
                    val cellColor = when {
                        selectedCell == row to col  -> Color(0xFF00FFFF).copy(0.3f)
                        isDark && task != null       -> task.priority.color.copy(0.2f)
                        isDark                       -> Color(0xFF9B30FF).copy(0.15f)
                        else                         -> Color(0xFF000000).copy(0.05f)
                    }
                    val borderColor = when {
                        isDark -> Color(0xFFFF2D78).copy(boardGlow * 0.5f)
                        else   -> Color(0xFF00BFFF).copy(boardGlow * 0.3f)
                    }

                    drawRect(cellColor, Offset(x, y), Size(cellSize, cellSize))
                    drawRect(borderColor, Offset(x, y), Size(cellSize, cellSize), style = Stroke(1f))

                    // Task indicator
                    if (task != null) {
                        drawCircle(task.priority.color, radius = cellSize * 0.18f,
                            center = Offset(x + cellSize / 2, y + cellSize / 2))
                        if (task.assignedAgent != null) {
                            drawCircle(Color.White.copy(0.8f), radius = cellSize * 0.08f,
                                center = Offset(x + cellSize / 2, y + cellSize / 2))
                        }
                    }
                }
            }

            // Board outer glow frame
            drawRect(Color(0xFFFF2D78).copy(boardGlow * 0.5f), Offset(0f, 0f),
                Size(size.width, size.height), style = Stroke(2f))
        }

        // Legend
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TaskPriority.values().forEach { p ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(p.color))
                    Text(p.label, fontSize = 8.sp, color = p.color.copy(0.8f))
                }
            }
        }
    }
}

// ── Supporting composables ────────────────────────────────────────────────────

@Composable
private fun AgentAssignChip(agent: AgentStats, isSelected: Boolean, taskCount: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(70.dp).fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (isSelected) Color.Cyan else Color.Cyan.copy(0.3f), RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.Cyan.copy(0.2f) else Color.Cyan.copy(0.06f))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(Modifier.size(30.dp).clip(CircleShape).border(1.dp, Color.Cyan, CircleShape)
            .background(Color.Cyan.copy(0.15f)), contentAlignment = Alignment.Center) {
            Text(agent.agentId.first().toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Cyan)
        }
        Text(agent.agentId, fontSize = 7.sp, color = Color.Cyan.copy(0.8f), textAlign = TextAlign.Center, maxLines = 1)
        if (taskCount > 0) Text("×$taskCount", fontSize = 7.sp, color = Color.White.copy(0.5f))
    }
}

@Composable
private fun TaskViewTab(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(4.dp))
            .background(if (isActive) Color(0xFF00BFFF).copy(0.2f) else Color.Transparent)
            .border(1.dp, if (isActive) Color(0xFF00BFFF).copy(0.6f) else Color.Transparent, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isActive) Color(0xFF00BFFF) else Color.White.copy(0.4f))
    }
}
