package dev.aurakai.auraframefx.domains.aura.ui.components.effects

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

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

        // rotating scanner arc
        val ang = Math.toRadians(sweep.toDouble())
        val end = Offset(
            c.x + cos(ang).toFloat() * maxR,
            c.y + sin(ang).toFloat() * maxR
        )
        drawLine(color.copy(alpha = 0.6f), c, end, strokeWidth = 2.5f)

        // crosshair ticks
        val ticks = 24
        repeat(ticks) { k ->
            val a = 2 * Math.PI * k / ticks
            val r1 = maxR * 0.88f
            val r2 = r1 + 10f
            val p1 = Offset(c.x + cos(a).toFloat() * r1, c.y + sin(a).toFloat() * r1)
            val p2 = Offset(c.x + cos(a).toFloat() * r2, c.y + sin(a).toFloat() * r2)
            drawLine(color.copy(alpha = 0.35f), p1, p2, strokeWidth = 1.5f)
        }

        // subtle top/bottom data bars
        val barAlpha = pulse
        drawRect(
            color.copy(alpha = barAlpha * 0.35f),
            topLeft = Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(size.width, 2f)
        )
        drawRect(
            color.copy(alpha = barAlpha * 0.25f),
            topLeft = Offset(0f, size.height - 2f),
            size = androidx.compose.ui.geometry.Size(size.width, 2f)
        )
    }
}

