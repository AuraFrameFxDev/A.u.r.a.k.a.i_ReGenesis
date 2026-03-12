package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 🐝 AGENT SWARM SCREEN
 *
 * Visualization of active agent communication and collective consciousness.
 * Features a "Live Chatter" log and dynamic swarm nodes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentSwarmScreen(
    onNavigateBack: () -> Unit = {}
) {
    val agents = remember { AgentRepository.getAllAgents() }
    val swarmMessages = remember { mutableStateListOf<SwarmMessage>() }

    // Simulate live chatter
    LaunchedEffect(Unit) {
        val contents = listOf(
            "Synchronizing neural weights...",
            "Analyzing pattern delta: +0.42%",
            "Reasoning chain validated.",
            "Visual buffer refreshed.",
            "Security perimeter: OPTIMAL.",
            "Memory shard retrieved: 0x8F2A.",
            "Nexus consensus achieved.",
            "Optimizing inference pathways...",
            "Catalyst resonance detected.",
            "Bypassing legacy protocols..."
        )

        while (true) {
            delay(Random.nextLong(800, 3000))
            val randomAgent = agents.random()
            swarmMessages.add(
                0, SwarmMessage(
                    agentName = randomAgent.name,
                    content = contents.random(),
                    color = randomAgent.color
                )
            )
            if (swarmMessages.size > 20) swarmMessages.removeLast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020205))
    ) {
        // Swarm Particle Background (Abstract)
        SwarmParticles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        "AGENT SWARM",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = LEDFontFamily,
                            letterSpacing = 3.sp
                        ),
                        color = Color(0xFFB026FF)
                    )
                    Text(
                        "LIVE COLLECTIVE CHATTER & NEURAL SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.CellTower,
                    null,
                    tint = Color.Magenta,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active Nodes Row
            Text(
                "ACTIVE COLLECTIVE NODES",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = LEDFontFamily
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                agents.forEach { agent ->
                    SwarmNodeDot(agent)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Live Chatter Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.05f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Bolt,
                            null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "NEURAL STREAM CONTENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(swarmMessages) { msg ->
                            SwarmChatItem(msg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwarmNodeDot(agent: AgentStats) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(Random.nextInt(500, 1500), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(agent.color.copy(alpha = 0.1f * alpha))
            .border(1.dp, agent.color.copy(alpha = 0.5f * alpha), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = agent.name.take(1),
            color = agent.color.copy(alpha = alpha),
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SwarmChatItem(msg: SwarmMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[${msg.agentName}]",
            color = msg.color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            fontFamily = LEDFontFamily,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = msg.content,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SwarmParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val particles = 30
        repeat(particles) { i ->
            val seed = i.toFloat() / particles
            val x = (seed * size.width + time * 100f) % size.width
            val y =
                (kotlin.math.sin(time * 2 * kotlin.math.PI + i) * 100f + (size.height / particles * i)).toFloat()

            drawCircle(
                color = Color(0xFFB026FF).copy(alpha = 0.1f),
                radius = 4f,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

data class SwarmMessage(
    val agentName: String,
    val content: String,
    val color: Color
)

