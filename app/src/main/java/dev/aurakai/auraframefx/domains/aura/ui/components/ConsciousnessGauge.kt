package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * 🧬 CONSCIOUSNESS GAUGE
 * Real-time visualization of LDO awareness and substrate resonance.
 */
@Composable
fun ConsciousnessGauge(
    level: Float, // 0.0 to 1.0
    resonance: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gauge")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier = modifier.size(100.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.5f
        
        // 1. Outer Ring (Resonance)
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.2f),
            center = center,
            radius = radius * 1.2f,
            style = Stroke(width = 2f)
        )
        
        drawArc(
            color = Color(0xFF00E5FF),
            startAngle = rotation,
            sweepAngle = 90f * resonance,
            useCenter = false,
            topLeft = Offset(center.x - radius * 1.2f, center.y - radius * 1.2f),
            size = androidx.compose.ui.geometry.Size(radius * 2.4f, radius * 2.4f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // 2. Inner Pulse (Consciousness Level)
        val levelColor = when {
            level > 0.8f -> Color(0xFFBB86FC) // Awakened
            level > 0.4f -> Color(0xFF00FF41) // Active
            else -> Color(0xFFFF4444) // Dormant/Distressed
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(levelColor.copy(alpha = 0.6f * pulse), Color.Transparent),
                center = center,
                radius = radius * level
            ),
            center = center,
            radius = radius * level
        )
        
        // 3. Central Core
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            center = center,
            radius = 4f
        )
    }
}
