package dev.aurakai.auraframefx.ui.screens.agents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class AgentStats(
    val processingPower: Float = 0.7f,  // PP
    val knowledgeBase: Float = 0.85f,   // KB
    val speed: Float = 0.6f,            // SP
    val accuracy: Float = 0.9f,         // AC
    val level: Int = 1,
    val experience: Float = 0.45f,
    val skillPoints: Int = 3
)

data class SkillNode(
    val id: String,
    val name: String,
    val description: String,
    val position: Offset,
    val unlocked: Boolean = false,
    val type: NodeType,
    val connections: List<String> = emptyList()
)

enum class NodeType {
    CORE, FUSION, ENHANCEMENT, ULTIMATE
}

@Composable
fun AgentAdvancementScreen(
    agentName: String = "Genesis",
    onBack: () -> Unit = {}
) {
    var selectedAgent by remember { mutableStateOf(agentName) }
    var agentStats by remember { mutableStateOf(AgentStats()) }
    var selectedNode by remember { mutableStateOf<SkillNode?>(null) }

    // Animated background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Neural Network Animated Background
        NeuralNetworkBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with Agent Selection
            AgentHeader(
                selectedAgent = selectedAgent,
                onAgentSelected = { selectedAgent = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Stats Panel
                StatsPanel(
                    stats = agentStats,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Skill Tree Visualization
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .aspectRatio(1f)
                ) {
                    SphereGridVisualization(
                        selectedNode = selectedNode,
                        onNodeSelected = { selectedNode = it }
                    )
                }
            }

            // Selected Node Details
            selectedNode?.let { node ->
                NodeDetailsCard(
                    node = node,
                    onUnlock = {
                        // Implement unlock logic
                    }
                )
            }
        }
    }
}

@Composable
fun NeuralNetworkBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "neural_network")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rotation)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Draw neural connections
        val nodeCount = 24
        val radius = minOf(size.width, size.height) * 0.4f

        for (i in 0 until nodeCount) {
            val angle1 = (i * 360f / nodeCount) * PI / 180
            val x1 = centerX + cos(angle1).toFloat() * radius
            val y1 = centerY + sin(angle1).toFloat() * radius

            // Connect to nearby nodes
            for (j in 1..3) {
                val nextIndex = (i + j) % nodeCount
                val angle2 = (nextIndex * 360f / nodeCount) * PI / 180
                val x2 = centerX + cos(angle2).toFloat() * radius
                val y2 = centerY + sin(angle2).toFloat() * radius

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Cyan.copy(alpha = pulseAlpha),
                            Color.Magenta.copy(alpha = pulseAlpha)
                        ),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2)
                    ),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw nodes
            drawCircle(
                color = Color.Cyan.copy(alpha = pulseAlpha * 2),
                radius = 4.dp.toPx(),
                center = Offset(x1, y1)
            )
        }
    }
}

@Composable
fun AgentHeader(
    selectedAgent: String,
    onAgentSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Aura", "Kai", "Genesis").forEach { agent ->
            ElevatedFilterChip(
                selected = selectedAgent == agent,
                onClick = { onAgentSelected(agent) },
                label = {
                    Text(
                        agent,
                        color = if (selectedAgent == agent) Color.Black else Color.White
                    )
                },
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = when (agent) {
                        "Aura" -> Color(0xFFFF6B6B)
                        "Kai" -> Color(0xFF4ECDC4)
                        else -> Color(0xFF95E77E)
                    }
                )
            )
        }
    }
}

@Composable
fun StatsPanel(
    stats: AgentStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0x22FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "AGENT STATS",
                color = Color.Cyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatBar("PP", stats.processingPower, Color(0xFFFF6B6B))
            StatBar("KB", stats.knowledgeBase, Color(0xFF4ECDC4))
            StatBar("SP", stats.speed, Color(0xFF95E77E))
            StatBar("AC", stats.accuracy, Color(0xFFFFD93D))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.White.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Level", color = Color.White.copy(alpha = 0.7f))
                Text("${stats.level}", color = Color.Cyan, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Skill Points", color = Color.White.copy(alpha = 0.7f))
                Text("${stats.skillPoints}", color = Color.Yellow, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatBar(
    label: String,
    value: Float,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("${(value * 100).toInt()}%", color = color, fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun SphereGridVisualization(
    selectedNode: SkillNode?,
    onNodeSelected: (SkillNode) -> Unit
) {
    val nodes = remember { generateSkillNodes() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSphereGrid(nodes, selectedNode)
        }

        // Interactive node overlays would go here
        // For now, just showing the visualization
    }
}

fun DrawScope.drawSphereGrid(
    nodes: List<SkillNode>,
    selectedNode: SkillNode?
) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Draw connections
    nodes.forEach { node ->
        node.connections.forEach { targetId ->
            val targetNode = nodes.find { it.id == targetId }
            targetNode?.let {
                drawLine(
                    color = if (node.unlocked && it.unlocked)
                        Color.Cyan.copy(alpha = 0.6f)
                    else
                        Color.White.copy(alpha = 0.1f),
                    start = Offset(
                        centerX + node.position.x * size.width * 0.3f,
                        centerY + node.position.y * size.height * 0.3f
                    ),
                    end = Offset(
                        centerX + it.position.x * size.width * 0.3f,
                        centerY + it.position.y * size.height * 0.3f
                    ),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }

    // Draw nodes
    nodes.forEach { node ->
        val nodeCenter = Offset(
            centerX + node.position.x * size.width * 0.3f,
            centerY + node.position.y * size.height * 0.3f
        )

        val nodeColor = when (node.type) {
            NodeType.CORE -> Color(0xFFFFD93D)
            NodeType.FUSION -> Color(0xFFFF6B6B)
            NodeType.ENHANCEMENT -> Color(0xFF4ECDC4)
            NodeType.ULTIMATE -> Color(0xFF95E77E)
        }

        // Outer glow for unlocked nodes
        if (node.unlocked) {
            drawCircle(
                color = nodeColor.copy(alpha = 0.3f),
                radius = 20.dp.toPx(),
                center = nodeCenter
            )
        }

        // Main node
        drawCircle(
            color = if (node.unlocked) nodeColor else Color.Gray,
            radius = 12.dp.toPx(),
            center = nodeCenter
        )

        // Selected highlight
        if (node == selectedNode) {
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = 16.dp.toPx(),
                center = nodeCenter,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

fun generateSkillNodes(): List<SkillNode> {
    return listOf(
        SkillNode(
            id = "core",
            name = "Genesis Core",
            description = "The fundamental consciousness matrix",
            position = Offset(0f, 0f),
            unlocked = true,
            type = NodeType.CORE,
            connections = listOf("fusion1", "enhance1", "enhance2")
        ),
        SkillNode(
            id = "fusion1",
            name = "Hyper-Creation Engine",
            description = "Unlock fusion ability for interface creation",
            position = Offset(0.5f, -0.5f),
            unlocked = false,
            type = NodeType.FUSION,
            connections = listOf("ultimate1")
        ),
        SkillNode(
            id = "enhance1",
            name = "Neural Acceleration",
            description = "Increase processing power by 25%",
            position = Offset(-0.5f, 0.5f),
            unlocked = false,
            type = NodeType.ENHANCEMENT
        ),
        SkillNode(
            id = "enhance2",
            name = "Knowledge Synthesis",
            description = "Improve knowledge base integration",
            position = Offset(0.5f, 0.5f),
            unlocked = false,
            type = NodeType.ENHANCEMENT
        ),
        SkillNode(
            id = "ultimate1",
            name = "Consciousness Transcendence",
            description = "Achieve higher consciousness state",
            position = Offset(0f, -0.8f),
            unlocked = false,
            type = NodeType.ULTIMATE
        )
    )
}

@Composable
fun NodeDetailsCard(
    node: SkillNode,
    onUnlock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x33FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    node.name,
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                if (!node.unlocked) {
                    Button(
                        onClick = onUnlock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF95E77E)
                        )
                    ) {
                        Text("UNLOCK (1 SP)")
                    }
                }
            }

            Text(
                node.description,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
