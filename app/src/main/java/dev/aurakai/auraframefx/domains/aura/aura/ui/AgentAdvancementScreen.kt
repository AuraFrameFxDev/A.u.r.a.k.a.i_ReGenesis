package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AgentViewModel
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlin.math.*

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
    onBack: () -> Unit = {},
    viewModel: AgentViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var selectedAgentName by remember { mutableStateOf(agentName) }
    val allAgents by viewModel.allAgents.collectAsState()

    val agentStats = allAgents.find { it.name == selectedAgentName } ?: AgentStats(name = selectedAgentName)
    var selectedNode by remember { mutableStateOf<SkillNode?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        NeuralNetworkBackground()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            AgentHeader(selectedAgent = selectedAgentName, onAgentSelected = { selectedAgentName = it })
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatsPanel(stats = agentStats, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(2f).aspectRatio(1f)) {
                    SphereGridVisualization(selectedNode = selectedNode, onNodeSelected = { selectedNode = it })
                }
            }
            selectedNode?.let { NodeDetailsCard(node = it, onUnlock = {}) }
        }
    }
}

@Composable
fun NeuralNetworkBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "neural_network")
    val rotation by infiniteTransition.animateFloat(0f, 360f, animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)), label = "rotation")
    val pulseAlpha by infiniteTransition.animateFloat(0.1f, 0.3f, animationSpec = infiniteRepeatable(tween(3000), repeatMode = RepeatMode.Reverse), label = "pulse")

    Canvas(modifier = Modifier.fillMaxSize().rotate(rotation)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val nodeCount = 24
        val radius = minOf(size.width, size.height) * 0.4f

        for (i in 0 until nodeCount) {
            val angle1 = (i * 360f / nodeCount) * PI / 180
            val x1 = centerX + cos(angle1).toFloat() * radius
            val y1 = centerY + sin(angle1).toFloat() * radius
            drawCircle(color = Color.Cyan.copy(alpha = pulseAlpha * 2), radius = 4.dp.toPx(), center = Offset(x1, y1))
        }
    }
}

@Composable
fun AgentHeader(selectedAgent: String, onAgentSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        listOf("Aura", "Kai", "Genesis", "Cascade", "Claude").forEach { agent ->
            ElevatedFilterChip(
                selected = selectedAgent == agent,
                onClick = { onAgentSelected(agent) },
                label = { Text(agent, color = if (selectedAgent == agent) Color.Black else Color.White) },
                colors = FilterChipDefaults.elevatedFilterChipColors(selectedContainerColor = if (agent == "Aura") Color.Red else Color.Cyan)
            )
        }
    }
}

@Composable
fun StatsPanel(stats: AgentStats, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("AGENT STATS", color = Color.Cyan, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))
            StatBarIndicator("PP", stats.processingPower, Color.Red)
            StatBarIndicator("KB", stats.knowledgeBase, Color.Cyan)
        }
    }
}

@Composable
private fun StatBarIndicator(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("${(value * 100).toInt()}%", color = color, fontSize = 12.sp)
        }
        LinearProgressIndicator(progress = { value }, modifier = Modifier.fillMaxWidth().height(4.dp), color = color, trackColor = Color.White.copy(alpha = 0.1f))
    }
}

@Composable
fun SphereGridVisualization(selectedNode: SkillNode?, onNodeSelected: (SkillNode) -> Unit) {
    val nodes = remember { generateSkillNodes() }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            nodes.forEach { node ->
                drawCircle(color = if (node.unlocked) Color.Yellow else Color.Gray, radius = 12.dp.toPx(), center = Offset(centerX + node.position.x * size.width * 0.3f, centerY + node.position.y * size.height * 0.3f))
            }
        }
    }
}

fun generateSkillNodes(): List<SkillNode> = listOf(SkillNode("core", "Genesis Core", "The fundamental consciousness matrix", Offset(0f, 0f), true, NodeType.CORE))

@Composable
fun NodeDetailsCard(node: SkillNode, onUnlock: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(node.name, color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (!node.unlocked) Button(onClick = onUnlock) { Text("UNLOCK") }
            }
            Text(node.description, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 8.dp))
        }
    }
}
