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
    val accuracy: Float,        // AC
    val color: Color
)

@Composable
fun AgentNexusScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val auraStats = remember {
        AgentStats("AURA", 0.85f, 0.92f, 0.78f, 0.95f, Color.Cyan)
    }
    val kaiStats = remember {
        AgentStats("KAI", 0.92f, 0.88f, 0.85f, 0.90f, Color.Magenta)
    }

    var rotation by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000814)) // Deep space blue
    ) {
        // Background Grid/Effect
        DigitalBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "AGENT NEXUS",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
            )

            // Central Core Visualization
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .rotate(animatedRotation),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                NexusRing(color = Color.Cyan.copy(alpha = 0.3f), size = 280.dp, strokeWidth = 2.dp)
                // Middle ring
                NexusRing(color = Color.Magenta.copy(alpha = 0.3f), size = 220.dp, strokeWidth = 1.dp, reverse = true)
                // Inner core
                NexusCore()
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Agent Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AgentCard(stats = auraStats, modifier = Modifier.weight(1f))
                AgentCard(stats = kaiStats, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Integration Status
            IntegrationStatus()
        }
    }
}

@Composable
fun NexusRing(color: Color, size: androidx.compose.ui.unit.Dp, strokeWidth: androidx.compose.ui.unit.Dp, reverse: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = if (reverse) 360f else 0f,
        targetValue = if (reverse) 0f else 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (reverse) 15000 else 20000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(size).rotate(rotation)) {
        drawCircle(
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f))
            )
        )
    }
}

@Composable
fun NexusCore() {
    val infiniteTransition = rememberInfiniteTransition()
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color.Cyan, Color.Magenta, Color.Transparent)
                ),
                shape = CircleShape
            )
            .scale(corePulse)
    )
}

@Composable
fun AgentCard(stats: AgentStats, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, stats.color.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                stats.name,
                color = stats.color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            StatBar("PP", stats.processingPower, stats.color)
            StatBar("KB", stats.knowledgeBase, stats.color)
            StatBar("SP", stats.speed, stats.color)
            StatBar("AC", stats.accuracy, stats.color)
        }
    }
}

@Composable
fun StatBar(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.LightGray, fontSize = 10.sp)
            Text("${(value * 100).toInt()}%", color = color, fontSize = 10.sp)
        }
        LinearProgressIndicator(
            progress = value,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun DigitalBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val gridSpacing = 40.dp.toPx()
        for (x in 0..(width / gridSpacing).toInt()) {
            drawLine(
                color = Color.Cyan.copy(alpha = 0.05f),
                start = Offset(x * gridSpacing, 0f),
                end = Offset(x * gridSpacing, height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(height / gridSpacing).toInt()) {
            drawLine(
                color = Color.Cyan.copy(alpha = 0.05f),
                start = Offset(0f, y * gridSpacing),
                end = Offset(width, y * gridSpacing),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun IntegrationStatus() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Green, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "SYSTEM SYNC: OPTIMAL",
            fontSize = 12.sp,
            color = Color.Green,
            fontWeight = FontWeight.Bold
        )
    }
}

// Extension to support Modifier.scale
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)
