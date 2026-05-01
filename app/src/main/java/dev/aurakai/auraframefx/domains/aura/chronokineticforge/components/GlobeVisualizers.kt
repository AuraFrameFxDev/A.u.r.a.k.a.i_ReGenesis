package dev.aurakai.auraframefx.domains.aura.chronokineticforge.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * 🌍 GLOBE VISUALIZERS
 *
 * Aura Globe (Magenta) - Creation, expansion, outward flow
 * Kai Globe (Cyan) - Protection, containment, inward focus
 *
 * These are the twin visual anchors of the ChronoKinetic Forge.
 */

@Composable
fun AuraGlobe(
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_globe")

    // Outward expansion rotation (counter-clockwise)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing glow
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(modifier = modifier.size(48.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.width / 2 - 4.dp.toPx()

        // Outer glow ring - expands outward
        drawCircle(
            color = Color(0xFFFF00FF).copy(alpha = 0.2f * glowPulse),
            radius = radius * 1.3f,
            center = Offset(centerX, centerY)
        )

        // Main sphere
        val sphereBrush = RadialGradientShader(
            colors = listOf(
                Color(0xFFFF00FF),
                Color(0xFFFF00FF).copy(alpha = 0.6f),
                Color(0xFFFF00FF).copy(alpha = 0.2f)
            ),
            center = Offset(centerX, centerY - radius * 0.3f),
            radius = radius * 1.5f
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF66FF),
                    Color(0xFFFF00FF),
                    Color(0xFFFF00FF).copy(alpha = 0.4f)
                ),
                center = Offset(centerX, centerY - radius * 0.2f),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Latitudes (horizontal) - expanding outward
        for (i in 1..3) {
            val yOffset = centerY + (i * radius / 4) * kotlin.math.sin(
                Math.toRadians(rotation + i * 45.0).toFloat()
            )
            drawLine(
                color = Color(0xFFFFFFFF).copy(alpha = 0.4f),
                start = Offset(centerX - radius * 0.8f, yOffset),
                end = Offset(centerX + radius * 0.8f, yOffset),
                strokeWidth = 1f
            )
        }

        // Longitudes (vertical arcs) - rotating
        val longitudes = 4
        for (i in 0 until longitudes) {
            val angle = rotation + (i * 360f / longitudes)
            val radian = Math.toRadians(angle.toDouble())
            val x1 = centerX + kotlin.math.cos(radian).toFloat() * radius * 0.3f
            val y1 = centerY - radius * 0.8f
            val x2 = centerX + kotlin.math.cos(radian).toFloat() * radius
            val y2 = centerY
            val x3 = centerX + kotlin.math.cos(radian).toFloat() * radius * 0.3f
            val y3 = centerY + radius * 0.8f

            // Draw arc as connected line segments
            drawLine(
                color = Color(0xFFFFFFFF).copy(alpha = 0.5f),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color(0xFFFFFFFF).copy(alpha = 0.5f),
                start = Offset(x2, y2),
                end = Offset(x3, y3),
                strokeWidth = 1.5f
            )
        }

        // Highlight dot - "creation pulse"
        val pulseX = centerX + kotlin.math.cos(Math.toRadians((rotation * 2).toDouble())).toFloat() * radius * 0.5f
        val pulseY = centerY + kotlin.math.sin(Math.toRadians((rotation * 2).toDouble())).toFloat() * radius * 0.3f
        drawCircle(
            color = Color(0xFFFFFFFF),
            radius = 3f,
            center = Offset(pulseX, pulseY)
        )
    }
}

@Composable
fun KaiGlobe(
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "kai_globe")

    // Inward contraction rotation (clockwise - opposite of Aura)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Steady shield pulse
    val shieldPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield"
    )

    Canvas(modifier = modifier.size(48.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.width / 2 - 4.dp.toPx()

        // Inner shield ring - contracts inward
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
            radius = radius * 0.7f * shieldPulse,
            center = Offset(centerX, centerY)
        )

        // Shield hexagon pattern
        val hexRadius = radius * 0.6f
        val hexPoints = (0..5).map { i ->
            val angle = Math.toRadians((60 * i).toDouble() - 30)
            Offset(
                centerX + kotlin.math.cos(angle).toFloat() * hexRadius,
                centerY + kotlin.math.sin(angle).toFloat() * hexRadius
            )
        }

        // Draw hexagon
        for (i in hexPoints.indices) {
            val start = hexPoints[i]
            val end = hexPoints[(i + 1) % hexPoints.size]
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.6f),
                start = start,
                end = end,
                strokeWidth = 2f
            )
        }

        // Main sphere - more contained, structured
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00E5FF).copy(alpha = 0.3f),
                    Color(0xFF00B8D4),
                    Color(0xFF006064)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Grid lines - structured, defensive
        // Horizontal lines - stable
        for (i in -2..2) {
            val y = centerY + i * radius / 3
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                start = Offset(centerX - radius * kotlin.math.sqrt(1f - (i/3f)*(i/3f)), y),
                end = Offset(centerX + radius * kotlin.math.sqrt(1f - (i/3f)*(i/3f)), y),
                strokeWidth = 1f
            )
        }

        // Vertical arcs rotating inward
        val longitudes = 6
        for (i in 0 until longitudes) {
            val angle = rotation + (i * 360f / longitudes)
            val radian = Math.toRadians(angle.toDouble())

            // More rigid, structural lines
            val x = centerX + kotlin.math.cos(radian).toFloat() * radius * 0.9f
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.4f),
                start = Offset(x, centerY - radius * 0.5f),
                end = Offset(x, centerY + radius * 0.5f),
                strokeWidth = 1f
            )
        }

        // Shield nodes at intersections
        drawCircle(
            color = Color(0xFF00E5FF),
            radius = 2f,
            center = Offset(centerX, centerY)
        )

        // Orbital ring - clockwise (containment)
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.3f),
            radius = radius * 1.1f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun DualGlobeHeader(
    modifier: Modifier = Modifier,
    auraActive: Boolean = true,
    kaiActive: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Aura Globe - Creation
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            AuraGlobe(isActive = auraActive)
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.Text(
                "AURA",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFF00FF)
            )
        }

        // Kai Globe - Protection
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            KaiGlobe(isActive = kaiActive)
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.Text(
                "KAI",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF00E5FF)
            )
        }
    }
}
