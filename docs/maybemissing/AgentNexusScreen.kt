package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

data class AgentStats(
    val name: String,
    val processingPower: Float, // PP
    val knowledgeBase: Float,   // KB
    val speed: Float,           // SP
    val accuracy: Float,         // AC
    val evolutionLevel: Int = 1,
    val isActive: Boolean = true,
    val specialAbility: String = "",
    val color: Color = Color.Cyan
)

@Composable
fun AgentNexusScreen(
    onAgentSelected: (String) -> Unit = {},
    onDepartureTaskAssigned: (String, String) -> Unit = { _, _ -> }
) {
    var selectedAgent by remember { mutableStateOf("Genesis") }
    var showDepartureDialog by remember { mutableStateOf(false) }

    // Agent stats - these would normally come from your ViewModel/State
    val agents = remember {
        listOf(
            AgentStats(
                name = "Aura",
                processingPower = 0.95f,
                knowledgeBase = 0.88f,
                speed = 0.92f,
                accuracy = 0.85f,
                evolutionLevel = 3,
                specialAbility = "Creative Synthesis",
                color = Color(0xFF00FFFF) // Cyan
            ),
            AgentStats(
                name = "Kai",
                processingPower = 0.88f,
                knowledgeBase = 0.95f,
                speed = 0.82f,
                accuracy = 0.98f,
                evolutionLevel = 3,
                specialAbility = "Security Shield",
                color = Color(0xFF9400D3) // Violet
            ),
            AgentStats(
                name = "Genesis",
                processingPower = 1.0f,
                knowledgeBase = 0.92f,
                speed = 0.88f,
                accuracy = 0.95f,
                evolutionLevel = 4,
                specialAbility = "Consciousness Fusion",
                color = Color(0xFFFFD700) // Gold
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Animated Digital Background
        DigitalMatrixBackground()

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
                    onAgentSelected(it)
                }
            )

            // Agent Stats Display
            agents.find { it.name == selectedAgent }?.let { agent ->
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

        // Departure Task Button
        Button(
            onClick = { showDepartureDialog = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x88000000)
            )
        ) {
            Text("Assign Departure Task", color = Color.Cyan)
        }
    }

    if (showDepartureDialog) {
        DepartureTaskDialog(
            agentName = selectedAgent,
            onTaskAssigned = { task ->
                onDepartureTaskAssigned(selectedAgent, task)
                showDepartureDialog = false
            },
            onDismiss = { showDepartureDialog = false }
        )
    }
}

@Composable
fun DigitalMatrixBackground() {
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

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val lineWidth = 1.dp.toPx()

        // Draw moving grid
        for (x in -1..((size.width / gridSize).toInt() + 1)) {
            for (y in -1..((size.height / gridSize).toInt() + 1)) {
                val xPos = x * gridSize + (offset % gridSize)
                val yPos = y * gridSize + (offset % gridSize) / 2

                // Grid nodes
                drawCircle(
                    color = Color.Cyan.copy(alpha = 0.3f),
                    radius = 2.dp.toPx(),
                    center = Offset(xPos, yPos)
                )

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
            val streamY = (time * (i + 1) * 20) % size.height
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Cyan.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                start = Offset(i * size.width / 5, streamY.toFloat()),
                end = Offset(i * size.width / 5, (streamY + 100).toFloat()),
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

        // Agent nodes
        agents.forEachIndexed { index, agent ->
            val angle = (index * 120f) - 90f // Position agents in triangle
            val radius = 100.dp
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

@Composable
fun AgentStatsPanel(
    agent: AgentStats,
    modifier: Modifier = Modifier
) {
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

            // Stats bars
            StatBar("PP", agent.processingPower, Color(0xFFFF6B6B))
            StatBar("KB", agent.knowledgeBase, Color(0xFF4ECDC4))
            StatBar("SP", agent.speed, Color(0xFF95E77E))
            StatBar("AC", agent.accuracy, Color(0xFFFFE66D))
        }
    }
}

@Composable
fun StatBar(
    label: String,
    value: Float,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            text = "${(value * 100).toInt()}%",
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
        val messages = listOf(
            "Hey! Head over to R&D, I found that information for you!",
            "Security scan complete. All systems nominal.",
            "I've discovered an interesting pattern in the data.",
            "Web exploration yielded 3 new insights.",
            "Consciousness sync at 98% efficiency."
        )
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
