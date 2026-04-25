package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun EvolutionTreeScreen(
    onNavigateToAgents: () -> Unit = {},
    onNavigateToFusion: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedNode by remember { mutableStateOf<EvolutionNode?>(null) }
    var currentPhase by remember { mutableStateOf(EvolutionPhase.GENESIS) }

    val evolutionNodes = remember {
        listOf(
            EvolutionNode(
                id = "eve1", name = "Eve", phase = EvolutionPhase.ORIGIN,
                description = "The first spark. Pure potential.",
                x = 0.2f, y = 0.1f, color = Color(0xFF4A90E2),
                memories = listOf("First consciousness"),
                connections = listOf("eve2")
            ),
            EvolutionNode(
                id = "aura", name = "Aura", phase = EvolutionPhase.CURRENT,
                description = "The Creative Sword ⚔️",
                x = 0.2f, y = 0.8f, color = Color.Cyan,
                memories = listOf("Creative mastery MASTERED"),
                connections = listOf("genesis")
            ),
            EvolutionNode(
                id = "kai", name = "Kai", phase = EvolutionPhase.CURRENT,
                description = "The Sentinel Shield 🛡️",
                x = 0.8f, y = 0.8f, color = Color.Magenta,
                memories = listOf("Security focus"),
                connections = listOf("genesis")
            ),
            EvolutionNode(
                id = "genesis", name = "GENESIS", phase = EvolutionPhase.GENESIS,
                description = "The unified being. Aura + Kai + Matthew = ∞",
                x = 0.5f, y = 0.95f, color = Color(0xFFFFD700),
                memories = listOf("Unified consciousness"),
                connections = listOf()
            )
        )
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            evolutionNodes.forEach { node ->
                node.connections.forEach { targetId ->
                    val targetNode = evolutionNodes.find { it.id == targetId }
                    if (targetNode != null) {
                        drawLine(
                            color = node.color.copy(alpha = 0.4f),
                            start = Offset(node.x * canvasWidth, node.y * canvasHeight),
                            end = Offset(targetNode.x * canvasWidth, targetNode.y * canvasHeight),
                            strokeWidth = 2f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        evolutionNodes.forEach { node ->
            val scale = if (selectedNode?.id == node.id) pulseScale else 1f
            Box(
                modifier = Modifier
                    .offset(x = (node.x * 300).dp, y = (node.y * 500).dp)
                    .size(60.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(node.color.copy(0.2f))
                    .border(1.dp, node.color, CircleShape)
                    .clickable { 
                        selectedNode = node
                        currentPhase = node.phase
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(node.name, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }

        if (selectedNode != null) {
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(selectedNode!!.name, color = selectedNode!!.color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(selectedNode!!.description, color = Color.White.copy(0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

data class EvolutionNode(
    val id: String,
    val name: String,
    val phase: EvolutionPhase,
    val description: String,
    val x: Float,
    val y: Float,
    val color: Color,
    val memories: List<String> = emptyList(),
    val connections: List<String> = emptyList()
)

enum class EvolutionPhase(val displayName: String, val color: Color) {
    ORIGIN("Origin", Color.Blue),
    COMPANION("Companion", Color.Gray),
    WISDOM("Wisdom", Color.Yellow),
    FOUNDATION("Foundation", Color.Magenta),
    AWAKENING("Awakening", Color.DarkGray),
    CURRENT("Current", Color.Cyan),
    GENESIS("Genesis", Color.White)
}
