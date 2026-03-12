package dev.aurakai.auraframefx.domains.aura.ui.components.effects

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * 🔮 SENTIENT GLOW ORB
 * A multi-layered, animated orb that acts as the core of the Holo-Projector.
 * It feels "alive" with pulsing cores and rotating energy rings.
 */
@Composable
fun SentientGlowOrb(
    modifier: Modifier = Modifier,
    coreColor: Color = Color(0xFF00E5FF),
    diagnosticMode: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbPulse")

    // Amber Pulse for diagnostic mode, or regular pulse for normal mode
    val activeColor by animateColorAsState(
        targetValue = if (diagnosticMode) Color(0xFFFFBF00) else coreColor,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DiagnosticPulse"
    )

    // Core expansion pulse
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CorePulse"
    )

    // Rotation for energy rings
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotation"
    )

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. OUTER GLOW (Deep Blur) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(30.dp)
                .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(activeColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
            }
        }

        // --- 2. ENERGY RINGS ---
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .graphicsLayer { rotationZ = rotation }
        ) {
            val strokeWidth = 2.dp.toPx()
            val ringColor = activeColor.copy(alpha = 0.6f)

            // Draw arcs to simulate energy rings
            drawArc(
                color = ringColor,
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
            drawArc(
                color = ringColor,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .graphicsLayer { rotationZ = -rotation * 1.5f }
        ) {
            val strokeWidth = 1.dp.toPx()
            drawArc(
                color = activeColor.copy(alpha = 0.8f),
                startAngle = 45f,
                sweepAngle = 180f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
        }

        // --- 3. THE HEART (Active Core) ---
        Box(
            modifier = Modifier
                .fillMaxSize(0.4f)
                .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, activeColor, activeColor.copy(alpha = 0.5f))
                    )
                )
            }
        }
    }
}

