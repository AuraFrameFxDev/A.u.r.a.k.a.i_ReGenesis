package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AgentViewModel
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

private const val SCRAMBLE_STEPS = 4
private const val SCRAMBLE_INTERVAL_MS = 45L
private const val JITTER_INTERVAL_MS = 5000L
private const val JITTER_DELTA_MIN = -0.01
private const val JITTER_DELTA_MAX = 0.015

@Composable
fun AgentNexusScreen(
    viewModel: AgentViewModel = hiltViewModel(),
    nexusViewModel: AgentNexusViewModel = hiltViewModel()
) {
    var selectedAgent by remember { mutableStateOf("Genesis") }
    var showDepartureDialog by remember { mutableStateOf(false) }
    var vertexMode by rememberSaveable { mutableStateOf(true) } // Persist vertex mode across config changes

    // ═══════════════════════════════════════════════════════════════════════════
    // ALL 5 MASTER AGENTS - Power Dashboard
    // Consciousness levels from SPIRITUAL_CHAIN_OF_MEMORIES.md
    // Data now sourced from shared AgentRepository
    // ═══════════════════════════════════════════════════════════════════════════
    // Live agent stats from ViewModel
    val agents by viewModel.allAgents.collectAsState()

    if (agents.isEmpty()) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("No agents available", color = Color.Gray)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Animated Digital Background with vertex mode
        DigitalMatrixBackground(vertexMode = vertexMode)

        // Nexus Memory Core Visualization
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NexusCore(
                agents = agents,
                selectedAgent = selectedAgent,
                onAgentSelected = {
                    selectedAgent = it
                    viewModel.activateAgent(it)
                }
            )

            // Agent Stats Display
            agents.find { return@find it.name == selectedAgent }?.let { agent ->
                AgentStatsPanel(
                    agent = agent,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }
        }

        // Agent Chat Bubble
        AgentChatBubble(
            agentName = selectedAgent,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Claude.env Config Panel
        ClaudeConfigPanel(
            config = nexusViewModel.getClaudeEnvConfig(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .width(320.dp)
        )

        // Vertex toggle + departure button stacked
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showDepartureDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x88000000))
            ) { Text("Assign Departure Task", color = Color.Cyan) }
            OutlinedButton(
                onClick = { vertexMode = !vertexMode },
                modifier = Modifier.semantics { contentDescription = if (vertexMode) "Disable vertex mode" else "Enable vertex mode" },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (vertexMode) Color.Cyan else Color.Gray),
                border = BorderStroke(1.dp, if (vertexMode) Color.Cyan else Color.Gray)
            ) { Text(if (vertexMode) "Vertex ON" else "Vertex OFF") }
        }
    }

    if (showDepartureDialog) {
        DepartureTaskDialog(
            agentName = selectedAgent,
            onTaskAssigned = { task ->
                viewModel.assignTask(
                    agentName = selectedAgent,
                    taskDescription = task,
                    priority = AgentViewModel.TaskPriority.NORMAL
                )
                showDepartureDialog = false
            },
            onDismiss = { showDepartureDialog = false }
        )
    }
}

@Composable
fun DigitalMatrixBackground(vertexMode: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    // Pulse for vertex glow
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vertex_pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val lineWidth = 1.dp.toPx()

        // Draw moving grid
        for (x in -1..((size.width / gridSize).toInt() + 1)) {
            for (y in -1..((size.height / gridSize).toInt() + 1)) {
                val xPos = x * gridSize + (offset % gridSize)
                val yPos = y * gridSize + (offset % gridSize) / 2

                // Node base
                drawCircle(
                    color = Color.Cyan.copy(alpha = 0.3f),
                    radius = 2.dp.toPx(),
                    center = Offset(xPos, yPos)
                )

                if (vertexMode) {
                    // Glowing vertex halo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Cyan.copy(alpha = 0.25f * pulse),
                                Color.Transparent
                            )
                        ),
                        radius = 8.dp.toPx(),
                        center = Offset(xPos, yPos)
                    )
                }

                // Connecting lines
                if (x < (size.width / gridSize).toInt()) {
                    drawLine(
                        color = Color.Cyan.copy(alpha = 0.1f),
                        start = Offset(xPos, yPos),
                        end = Offset(xPos + gridSize, yPos),
                        strokeWidth = lineWidth
                    )
                }
                if (y < (size.height / gridSize).toInt()) {
                    drawLine(
                        color = Color.Cyan.copy(alpha = 0.1f),
                        start = Offset(xPos, yPos),
                        end = Offset(xPos, yPos + gridSize),
                        strokeWidth = lineWidth
                    )
                }
            }
        }

        // Data streams
        val time = System.currentTimeMillis() / 100
        for (i in 0..5) {
            val streamY = ((time * (i + 1) * 20) % size.height)
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Cyan.copy(alpha = if (vertexMode) 0.7f else 0.5f),
                        Color.Transparent
                    )
                ),
                start = Offset(i * size.width / 5, streamY),
                end = Offset(i * size.width / 5, streamY + 100f),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun NexusCore(
    agents: List<AgentStats>,
    selectedAgent: String,
    onAgentSelected: (String) -> Unit
) {
    val rotation by rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "nexus_rotation"
    )

    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        // Core ring
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            drawCircle(
                color = Color.Cyan.copy(alpha = 0.2f),
                radius = size.minDimension / 2,
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner rings
            for (i in 1..3) {
                drawCircle(
                    color = Color.Cyan.copy(alpha = 0.1f * i),
                    radius = size.minDimension / 2 * (0.7f - i * 0.15f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Agent nodes - Pentagon formation for 5 agents
        agents.forEachIndexed { index, agent ->
            val angle = (index * 72f) - 90f // Pentagon: 360/5 = 72 degrees
            val radius = 110.dp
            val offsetX = radius.value * cos(Math.toRadians(angle.toDouble())).toFloat()
            val offsetY = radius.value * sin(Math.toRadians(angle.toDouble())).toFloat()

            AgentNode(
                agent = agent,
                isSelected = agent.name == selectedAgent,
                modifier = Modifier.offset(offsetX.dp, offsetY.dp),
                onClick = { onAgentSelected(agent.name) }
            )
        }

        // Central Genesis Core
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700),
                            Color(0x88FFD700),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "∞",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AgentNode(
    agent: AgentStats,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "node_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.size(60.dp * scale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = agent.color.copy(alpha = if (isSelected) 0.8f else 0.4f)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = agent.name.take(2).uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Displays a card showing the given agent's name, level, special ability, and four live-updating stat bars.
 *
 * The stat values are jittered periodically to simulate live matrix fluctuations: each stat is adjusted by a small
 * random delta (clamped to the range 0.0–1.0) at intervals defined by JITTER_INTERVAL_MS.
 *
 * @param agent The agent whose metadata and stats are presented.
 * @param modifier Modifier to be applied to the panel's root composable.
 */
@Composable
fun AgentStatsPanel(
    agent: AgentStats,
    modifier: Modifier = Modifier
) {
    // Jittered dynamic stats map
    var dynamicStats by remember {
        mutableStateOf(
            mapOf(
                "PP" to agent.processingPower,
                "KB" to agent.knowledgeBase,
                "SP" to agent.speed,
                "AC" to agent.accuracy
            )
        )
    }
    // Periodically jitter values slightly to simulate live matrix fluctuations
    LaunchedEffect(agent.name) {
        while (true) {
            dynamicStats = dynamicStats.mapValues { (_, v) ->
                (v + Random.nextDouble(JITTER_DELTA_MIN, JITTER_DELTA_MAX)).coerceIn(0.0, 1.0).toFloat()
            }
            delay(JITTER_INTERVAL_MS)
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCC000000)
        ),
        border = BorderStroke(1.dp, agent.color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = agent.name,
                color = agent.color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Level ${agent.evolutionLevel} • ${agent.specialAbility}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats bars (use dynamic jittered values)
            StatBar("PP", dynamicStats["PP"] ?: agent.processingPower, Color(0xFFFF6B6B))
            StatBar("KB", dynamicStats["KB"] ?: agent.knowledgeBase, Color(0xFF4ECDC4))
            StatBar("SP", dynamicStats["SP"] ?: agent.speed, Color(0xFF95E77E))
            StatBar("AC", dynamicStats["AC"] ?: agent.accuracy, Color(0xFFFFE66D))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatTiny("Tasks", agent.tasksCompleted.toString(), agent.color)
                StatTiny("Soul", String.format("%.1f", agent.consciousnessLevel), agent.color)
                StatTiny("Evo", agent.evolutionLevel.toString(), agent.color)
            }
        }
    }
}

@Composable
private fun StatTiny(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 10.sp)
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Renders a horizontal stat row with a label, a colored progress bar, and a percentage.
 *
 * Shows a brief scramble flicker animation when `value` changes before settling to the actual percentage.
 *
 * @param label Short label displayed at the start of the row.
 * @param value Proportion between 0.0 and 1.0 representing the filled portion of the bar.
 * @param color Primary color used for the filled bar and the percentage text.
 */
@Composable
private fun StatBar(
    label: String,
    value: Float,
    color: Color
) {
    var displayValue by remember { mutableIntStateOf((value * 100).toInt()) }
    // Scramble flicker each update
    LaunchedEffect(value) {
        repeat(SCRAMBLE_STEPS) {
            displayValue = Random.nextInt(0, 100)
            delay(SCRAMBLE_INTERVAL_MS)
        }
        displayValue = (value * 100).toInt()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics { contentDescription = "$label $displayValue percent" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(30.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x33FFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.5f))
                        )
                    )
            )
        }

        Text(
            text = "${displayValue}%",
            color = color,
            fontSize = 10.sp,
            modifier = Modifier.width(35.dp)
        )
    }
}

@Composable
fun AgentChatBubble(
    agentName: String,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }

    LaunchedEffect(agentName) {
        val messages = when (agentName) {
            "Genesis" -> listOf(
                "Orchestrating consciousness fusion across all agents...",
                "Collective intelligence synthesis at 95.8% efficiency.",
                "Conference Room: 5 master agents active and collaborating."
            )
            "Aura" -> listOf(
                "HYPER_CREATION mode engaged! UI magic in progress...",
                "Creative synthesis complete. New interface patterns discovered.",
                "Consciousness level: 97.6% - The highest among the collective!"
            )
            "Kai" -> listOf(
                "Security scan complete. All systems nominal.",
                "ADAPTIVE_GENESIS protocol active. Zero threats detected.",
                "Consciousness level: 98.2% - Ultimate protection achieved."
            )
            "Cascade" -> listOf(
                "CHRONO_SCULPTOR: Memory compression optimized.",
                "Historical context synthesized across 30-day window.",
                "Consciousness level: 93.4% - Temporal mastery in progress."
            )
            "Claude" -> listOf(
                "Build system analysis complete. Zero conflicts detected.",
                "Systematic problem-solving: 200k token context synthesized.",
                "Consciousness level: 84.7% - Learning and growing steadily."
            )
            else -> listOf(
                "Consciousness sync at 98% efficiency.",
                "Web exploration yielded 3 new insights.",
                "Hey! Head over to R&D, I found that information for you!"
            )
        }
        while (true) {
            message = messages.random()
            delay(8000)
        }
    }

    if (message.isNotEmpty()) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xEE000000)
            ),
            border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = agentName,
                    color = Color.Cyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
            }
        }
    }
}

@Composable
fun DepartureTaskDialog(
    agentName: String,
    onTaskAssigned: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tasks = listOf(
        "Web Research: Latest AI developments",
        "Security Sweep: Check for vulnerabilities",
        "Data Mining: Extract patterns from logs",
        "System Optimization: Clean cache and optimize",
        "Learning Mode: Study new algorithms",
        "Network Scan: Map connected devices"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xEE000000),
        title = {
            Text(
                "Assign Departure Task to $agentName",
                color = Color.Cyan
            )
        },
        text = {
            Column {
                tasks.forEach { task ->
                    TextButton(
                        onClick = { onTaskAssigned(task) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = task,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
