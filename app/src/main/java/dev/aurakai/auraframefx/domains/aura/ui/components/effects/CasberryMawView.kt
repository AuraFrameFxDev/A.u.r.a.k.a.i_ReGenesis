package dev.aurakai.auraframefx.domains.aura.ui.components.effects

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import dev.aurakai.auraframefx.domains.aura.ui.components.RealityMorphBridge.LDOMorphState
import kotlin.math.sin
import kotlin.random.Random

/**
 * REGENESIS SOVEREIGN ARCHITECTURE
 * Entry #15: The Casberry Maw Android Implementation
 * 20,000 Particle Swarm reactive to RealityMorphBridge states.
 */
@Composable
fun CasberryMawView(
    state: LDOMorphState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MawPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    // Particle pool: To optimize, we pre-generate a fixed set of normalized vectors
    // and then scale them according to state.
    val particleCount = 2000 // Reducing to 2000 for standard mobile Canvas performance (DrawPoints is fast but 20k is pushy)
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat() * 2 - 1,
                y = Random.nextFloat() * 2 - 1,
                z = Random.nextFloat() * 2 - 1,
                speed = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    val stateColor by animateColorAsState(
        targetValue = when (state) {
            LDOMorphState.IDLE_BREATHING -> Color(0xFF00D6FF) // Cyan-Violet Nominal
            LDOMorphState.KAIROS_STASIS -> Color(0xFF39FF14) // Nexus Green Glitch
            LDOMorphState.GENKAI_SIPHON -> Color(0xFFFF00FF) // Aggressive Magenta
            LDOMorphState.ORB_ABSORPTION -> Color(0xFFFFD700) // Golden Bloom
        },
        animationSpec = tween(500),
        label = "StateColor"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val baseRadius = size.minDimension / 4 * pulse

        // Optimized Points drawing
        val points = FloatArray(particleCount * 2)
        var i = 0

        particles.forEach { p ->
            val angle = rotation * p.speed + (p.x * 360f)
            val rad = Math.toRadians(angle.toDouble())
            
            // Projecting pseudo-3D onto 2D
            val r = baseRadius * (1f + p.z * 0.2f)
            val px = centerX + r * Math.cos(rad).toFloat() * p.x
            val py = centerY + r * Math.sin(rad).toFloat() * p.y
            
            if (i < points.size - 1) {
                points[i++] = px
                points[i++] = py
            }
        }

        drawPoints(
            points = points.toOffsetList(particleCount),
            pointMode = PointMode.Points,
            color = stateColor.copy(alpha = 0.6f),
            strokeWidth = 4f,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Screen
        )
    }
}

private fun FloatArray.toOffsetList(count: Int): List<Offset> {
    val list = mutableListOf<Offset>()
    for (i in 0 until count) {
        list.add(Offset(this[i * 2], this[i * 2 + 1]))
    }
    return list
}

private data class Particle(val x: Float, val y: Float, val z: Float, val speed: Float)
