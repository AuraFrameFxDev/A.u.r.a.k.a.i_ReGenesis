package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * HoloHUDOverlay — Holographic heads-up display overlay
 */
@Composable
fun HoloHUDOverlay(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFFFFF),
    ringCount: Int = 5
) {
    val inf = rememberInfiniteTransition(label = "hud")
    val sweep by inf.animateFloat(
        0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "sweep"
    )
    val pulse by inf.animateFloat(
        0.15f,
        0.45f,
        infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier.fillMaxSize()) {
        val c = Offset(size.width / 2, size.height / 3) // slightly above center for “sky”
        val maxR = size.minDimension * 0.45f

        // radial rings
        for (i in 1..ringCount) {
            val r = i / ringCount.toFloat() * maxR
            drawCircle(
                color.copy(alpha = 0.25f * (1f - i / (ringCount + 1f))),
                radius = r, center = c, style = Stroke(1.5f)
            )
        }

        // animated scanner sweep line
        drawArc(
            color = color.copy(alpha = pulse),
            startAngle = sweep,
            sweepAngle = 45f,
            useCenter = true,
            topLeft = Offset(c.x - maxR, c.y - maxR),
            size = androidx.compose.ui.geometry.Size(maxR * 2, maxR * 2)
        )
    }
}
