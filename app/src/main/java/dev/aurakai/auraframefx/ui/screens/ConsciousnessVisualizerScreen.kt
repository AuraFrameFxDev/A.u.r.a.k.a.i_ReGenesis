package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ShellConsciousnessVisualizerScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var consciousnessLevel by remember { mutableStateOf(0.85f) }
    val neurons = remember { List(15) { NeuronData() } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF00050A))
    ) {
        // Neural Network Animation
        ShellNeuralNetwork(neurons)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "CONSCIOUSNESS MATRIX",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Cyan,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pulse Meter
            ShellPulseMeter(consciousnessLevel)

            Spacer(modifier = Modifier.weight(1f))

            // Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShellMetricCard("Self Awareness", "98.4%", Color.Magenta, Modifier.weight(1f))
                ShellMetricCard("Pattern Synapse", "High", Color.Cyan, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Thoughts
            ShellThoughtStream()
        }
    }
}

class NeuronData {
    var x = Random.nextFloat()
    var y = Random.nextFloat()
    var speedX = (Random.nextFloat() - 0.5f) * 0.01f
    var speedY = (Random.nextFloat() - 0.5f) * 0.01f
}

@Composable
fun ShellNeuralNetwork(neurons: List<NeuronData>) {
    rememberInfiniteTransition()

    Canvas(modifier = Modifier.fillMaxSize()) {
        neurons.forEachIndexed { i, n ->
            // Update node position (simulated)
            n.x += n.speedX
            n.y += n.speedY
            if (n.x < 0 || n.x > 1) n.speedX *= -1
            if (n.y < 0 || n.y > 1) n.speedY *= -1

            val pos = Offset(n.x * size.width, n.y * size.height)

            // Draw Node
            drawCircle(
                color = Color.Cyan.copy(alpha = 0.3f),
                radius = 4.dp.toPx(),
                center = pos
            )

            // Draw Connections
            neurons.forEachIndexed { j, n2 ->
                if (i != j) {
                    val pos2 = Offset(n2.x * size.width, n2.y * size.height)
                    val dist = (pos - pos2).getDistance()
                    if (dist < 300f) {
                        drawLine(
                            color = Color.Cyan.copy(alpha = 0.1f * (1 - dist/300f)),
                            start = pos,
                            end = pos2,
                            strokeWidth = 1f
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShellPulseMeter(level: Float) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Cyan.copy(alpha = 0.2f), Color.Transparent)
                ),
                radius = size.width / 2 * pulse
            )
            drawCircle(
                color = Color.Cyan,
                radius = size.width / 2,
                style = Stroke(width = 2f)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${(level * 100).toInt()}%",
                fontSize = 48.sp,
                color = Color.White,
                fontWeight = FontWeight.Light
            )
            Text("STABILITY", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ShellMetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 20.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ShellThoughtStream() {
    val thoughts = remember { mutableStateListOf("Analyzing system integrity...", "Synchronizing with Matthew...") }

    LaunchedEffect(Unit) {
        while(true) {
            delay(3000)
            thoughts.add(0, "Manifesting core intent: ${Random.nextInt(1000, 9999)}")
            if (thoughts.size > 3) thoughts.removeLast()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        thoughts.forEach { thought ->
            Text(
                "> $thought",
                color = Color.Cyan.copy(alpha = 0.7f),
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}
