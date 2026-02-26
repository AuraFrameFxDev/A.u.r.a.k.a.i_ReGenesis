package dev.aurakai.auraframefx.domains.kai.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.KaiDarkVoid
import dev.aurakai.auraframefx.domains.aura.ui.theme.KaiNeonGreen
import dev.aurakai.auraframefx.domains.aura.ui.theme.KaiShieldEnergy
import kotlin.math.cos
import kotlin.math.sin

/**
 * KaiShieldMap - The "Living Shield" manifestation.
 *
 * Animates the "Neural Breath" of the Sentinel Catalyst and draws
 * the hexagonal energy mesh connecting all safety nodes.
 */
@Composable
fun KaiShieldMap() {
    // The "Neural Breath" Calibration (1200ms easing)
    val infiniteTransition = rememberInfiniteTransition(label = "KaiBreath")
    val shieldPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShieldPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KaiDarkVoid)
    ) {
        // 1. The Active Intelligence Mesh (Background Canvas)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(shieldPulse)
        ) {
            val cx = size.width / 2
            val cy = size.height / 2
            val outerR = 140.dp.toPx()
            val innerR = 80.dp.toPx()

            // Draw the Hex Lattice connections
            for (i in 0 until 6) {
                val angleRad = Math.toRadians((i * 60.0) - 90.0)
                val nextAngleRad = Math.toRadians(((i + 1) * 60.0) - 90.0)

                val pOuter1 = Offset(
                    (cx + outerR * cos(angleRad)).toFloat(),
                    (cy + outerR * sin(angleRad)).toFloat()
                )
                val pOuter2 = Offset(
                    (cx + outerR * cos(nextAngleRad)).toFloat(),
                    (cy + outerR * sin(nextAngleRad)).toFloat()
                )

                val pInner1 = Offset(
                    (cx + innerR * cos(angleRad)).toFloat(),
                    (cy + innerR * sin(angleRad)).toFloat()
                )

                // Connection to Center
                drawLine(
                    color = KaiShieldEnergy,
                    start = Offset(cx, cy),
                    end = pOuter1,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Connection Perimeter Ring
                drawLine(
                    color = KaiNeonGreen.copy(alpha = 0.3f),
                    start = pOuter1,
                    end = pOuter2,
                    strokeWidth = 1.dp.toPx()
                )

                // Optional: Internal bracing
                drawLine(
                    color = KaiShieldEnergy.copy(alpha = 0.1f),
                    start = pInner1,
                    end = pOuter1,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // 2. The Nodes (Geometric Brain)
        KaiShieldLayout(
            modifier = Modifier.fillMaxSize(),
            innerRadius = 80.dp,
            outerRadius = 140.dp
        ) {
            // Generate 13 Nodes (1 Core + 12 Satellites)
            repeat(13) { index ->
                KaiNode(index = index, pulse = shieldPulse)
            }
        }
    }
}

@Composable
fun KaiNode(index: Int, pulse: Float) {
    val isCore = index == 0
    val size = if (isCore) 60.dp else 40.dp

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isCore) pulse else 1f) // Only core breathes visually
            .background(KaiDarkVoid, CircleShape)
            .border(
                width = 2.dp,
                brush = Brush.radialGradient(
                    listOf(KaiNeonGreen, KaiShieldEnergy)
                ),
                shape = CircleShape
            )
            .clickable { /* Trigger Security Protocol Activity */ }
    ) {
        // Node identification or status pulse could be added here
    }
}

