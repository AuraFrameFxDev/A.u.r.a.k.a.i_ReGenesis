package dev.aurakai.auraframefx.domains.aura.ui.components.backgrounds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DataRibbonsBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF00FFFF),   // cyan default
    accentColor: Color = Color(0xFFFF00FF), // magenta secondary
    ribbons: Int = 7,
    amplitudePx: Float = 48f,
    thicknessPx: Float = 3.5f,
    parallaxLayers: Int = 3,
    speedMin: Float = 0.25f,
    speedMax: Float = 0.85f,
) {
    val inf = rememberInfiniteTransition(label = "ribbons")
    val t by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing)), label = "t"
    )

    data class Ribbon(
        val yBase: Float, val phase: Float, val speed: Float,
        val amp: Float, val thick: Float, val layer: Int, val col: Color
    )

    val ribbonSet = remember(ribbons, amplitudePx, thicknessPx) {
        List(ribbons) {
            val layer = it % parallaxLayers
            Ribbon(
                yBase = Random.nextFloat(),                // relative 0..1
                phase = Random.nextFloat() * 6.28318f,     // 0..2π
                speed = Random.nextFloat() * (speedMax - speedMin) + speedMin,
                amp = amplitudePx * (0.7f + 0.6f * Random.nextFloat()),
                thick = thicknessPx * (0.7f + 0.6f * Random.nextFloat()),
                layer = layer,
                col = lerp(baseColor, accentColor, Random.nextFloat()).copy(alpha = 0.85f)
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        ribbonSet.forEach { r ->
            val path = Path()
            val points = 80
            for (i in 0..points) {
                val x = (i / points.toFloat()) * w
                val ω = (x / w) * 6.28318f
                // subtle parallax via layer depth and time
                val y = r.yBase * h +
                        sin(ω + r.phase + t * r.speed) * r.amp * (1f - 0.15f * r.layer)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            // glow passes
            drawPath(
                path, r.col.copy(alpha = 0.20f),
                style = Stroke(width = r.thick * 2.8f, cap = StrokeCap.Round)
            )
            drawPath(
                path, r.col.copy(alpha = 0.40f),
                style = Stroke(width = r.thick * 1.8f, cap = StrokeCap.Round)
            )
            drawPath(path, r.col, style = Stroke(width = r.thick, cap = StrokeCap.Round))
        }
    }
}

