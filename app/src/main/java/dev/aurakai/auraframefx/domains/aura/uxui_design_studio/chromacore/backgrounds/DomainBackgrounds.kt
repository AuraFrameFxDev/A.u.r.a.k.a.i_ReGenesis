package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.backgrounds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 🎨 DATA RIBBONS BACKGROUND — ChromaCore Background Engine
 *
 * Animated flowing data ribbon / neon stream background for
 * CyberpunkScreenScaffold and domain hub screens.
 */
@Composable
fun DataRibbonsBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF00FBFF),
    accentColor: Color = Color(0xFFFF00FF),
    ribbons: Int = 8,
    speedMin: Float = 0.2f,
    speedMax: Float = 0.8f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ribbons")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ribbon_phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        repeat(ribbons) { i ->
            val seed = (i * 137) % 360
            val t = (phase + seed) % 360f
            val alpha = 0.1f + (0.15f * (i % 3))
            val color = if (i % 2 == 0) baseColor.copy(alpha = alpha) else accentColor.copy(alpha = alpha)
            val startX = (w * (i.toFloat() / ribbons))
            val waveY = h / 2 + (h * 0.3f * sin(Math.toRadians(t.toDouble() + i * 45.0))).toFloat()

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, color, Color.Transparent),
                    start = Offset(startX, 0f),
                    end = Offset(startX + w * 0.3f, h)
                ),
                start = Offset(startX, 0f),
                end = Offset(startX + w * 0.2f, h),
                strokeWidth = 1.5f + i % 3,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * 🎨 HEXAGON GRID BACKGROUND — ChromaCore Background Engine
 *
 * Animated hexagonal grid background for security/Kai-domain screens.
 */
@Composable
fun HexagonGridBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF00FF85),
    secondaryColor: Color = Color(0xFF1F51FF),
    accentColor: Color = Color(0xFF00FBFF),
    hexSize: Float = 48f,
    alpha: Float = 0.1f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hexgrid")
    val pulse by infiniteTransition.animateFloat(
        initialValue = alpha * 0.6f,
        targetValue = alpha,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hex_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val hexW = hexSize * 1.732f
        val hexH = hexSize * 2f

        var row = 0
        var y = 0f
        while (y < h + hexH) {
            var x = if (row % 2 == 0) 0f else hexW / 2f
            while (x < w + hexW) {
                val hexPath = androidx.compose.ui.graphics.Path()
                for (angle in 0..5) {
                    val rad = Math.toRadians(angle * 60.0 - 30.0)
                    val px = x + hexSize * cos(rad).toFloat()
                    val py = y + hexSize * sin(rad).toFloat()
                    if (angle == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                }
                hexPath.close()
                drawPath(hexPath, color = primaryColor.copy(alpha = pulse), style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth = 1f))
                x += hexW
            }
            y += hexH * 0.75f
            row++
        }
    }
}
