package dev.aurakai.auraframefx.domains.aura.ui.components.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🕸️ SYNAPTIC WEB BACKGROUND (Level 1)
 * A GPU-accelerated canvas that draws a hexagonal neural grid.
 * It connects the 11 nodes of the ReGenesis system.
 */
@Composable
fun SynapticWebBackground(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Cyan
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val hexSize = 60.dp.toPx()
        val strokeWidth = 1.dp.toPx()

        // Grid Calculation
        val cols = (width / (hexSize * 1.5)).toInt() + 2
        val rows = (height / (hexSize * 1.732)).toInt() + 2

        for (i in 0 until cols) {
            for (j in 0 until rows) {
                val x = i * hexSize * 1.5f
                val y = j * hexSize * 1.732f + (if (i % 2 == 1) hexSize * 0.866f else 0f)

                drawHexagon(
                    center = Offset(x, y),
                    size = hexSize,
                    color = glowColor.copy(alpha = 0.1f),
                    strokeWidth = strokeWidth
                )

                // Draw connection nodes (synapses)
                drawCircle(
                    color = glowColor.copy(alpha = 0.2f),
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexagon(
    center: Offset,
    size: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    for (i in 0..6) {
        val angle = Math.toRadians(60.0 * i - 30.0)
        val x = center.x + size * cos(angle).toFloat()
        val y = center.y + size * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = strokeWidth))
}

