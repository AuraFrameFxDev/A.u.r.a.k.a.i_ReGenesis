package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 🌌 Neural Link Depth Effect Background
 * 
 * Simulates a high-speed travel through a neural network or data tunnel.
 * Features:
 * - Moving "data lines" in perspective (Z-axis movement)
 * - Radial gradient depth
 * - Neon accents (Cyan/Blue/Purple)
 * - "Speed" effect
 */
@Composable
fun NeuralLinkBackground(
    modifier: Modifier = Modifier,
    speed: Float = 1.0f,
    primaryColor: Color = Color(0xFF00FFFF), // Cyan
    secondaryColor: Color = Color(0xFF0000FF) // Blue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neural_link")

    // Animate "travel" distance
    val travelDistance by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "travel"
    )

    // Generate static random lines that we will animate along Z-axis
    val lines = remember {
        List(40) {
            NeuralLine(
                angle = Random.nextFloat() * 2 * PI.toFloat(),
                radiusOffset = Random.nextFloat() * 200f,
                length = Random.nextFloat() * 300f + 100f,
                speedMultiplier = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Deep radial gradient for infinite depth feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF001020), // Dark Blue center
                            Color.Black // Black edges
                        ),
                        radius = 1500f
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = size.width.coerceAtLeast(size.height)

            // Draw "Grid" floor/ceiling effect
            // We simulate this by drawing radial lines from center
            for (i in 0 until 12) {
                val angle = (i * 30f) * (PI.toFloat() / 180f)
                val startX = centerX
                val startY = centerY
                val endX = centerX + cos(angle) * maxRadius
                val endY = centerY + sin(angle) * maxRadius

                drawLine(
                    color = secondaryColor.copy(alpha = 0.1f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1f
                )
            }

            // Draw moving "Neural Links" (Data packets)
            lines.forEach { line ->
                // Calculate current Z position (0 is far, 1 is near)
                // We wrap the value using modulo to create infinite loop
                val currentZ =
                    (travelDistance * line.speedMultiplier * speed + line.radiusOffset) % 1000f / 1000f

                // Perspective projection
                // Objects get larger and further from center as they approach (Z -> 1)
                // Objects fade in as they approach, then fade out quickly when too close

                val perspective = currentZ * currentZ * currentZ // Non-linear for speed effect
                val radius = perspective * maxRadius * 0.8f

                val x = centerX + cos(line.angle) * radius
                val y = centerY + sin(line.angle) * radius

                // Tail effect (line segment pointing towards center)
                val tailLength = line.length * perspective * 0.5f
                val tailX = centerX + cos(line.angle) * (radius - tailLength)
                val tailY = centerY + sin(line.angle) * (radius - tailLength)

                val alpha = when {
                    currentZ < 0.1f -> currentZ * 10f // Fade in
                    currentZ > 0.9f -> (1f - currentZ) * 10f // Fade out
                    else -> 1f
                }

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = alpha),
                            Color.White.copy(alpha = alpha)
                        ),
                        start = Offset(tailX, tailY),
                        end = Offset(x, y)
                    ),
                    start = Offset(tailX, tailY),
                    end = Offset(x, y),
                    strokeWidth = 2f + (perspective * 5f), // Thicker when closer
                    cap = Stroke.DefaultCap
                )
            }
        }
    }
}

private data class NeuralLine(
    val angle: Float,
    val radiusOffset: Float,
    val length: Float,
    val speedMultiplier: Float
)

