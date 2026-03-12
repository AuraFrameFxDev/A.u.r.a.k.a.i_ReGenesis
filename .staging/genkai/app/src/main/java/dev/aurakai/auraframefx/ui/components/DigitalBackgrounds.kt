package dev.aurakai.auraframefx.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Digital landscape background component
 */
/**
 * Displays a digital landscape background as a grid pattern using a Canvas.
 *
 * @param modifier Modifier to be applied to the Canvas.
 * @param color The color used for the grid lines, defaulting to semi-transparent cyan.
 */
/**
 * Displays a digital landscape-style grid background using a Canvas.
 *
 * Renders evenly spaced vertical and horizontal lines to create a grid pattern, with customizable color and modifier.
 *
 * @param modifier Modifier to apply to the Canvas.
 * @param color The color of the grid lines, defaulting to semi-transparent cyan.
 */
/**
 * Displays a digital landscape background as a grid pattern using a Canvas.
 *
 * Renders evenly spaced vertical and horizontal lines to create a digital grid effect.
 *
 * @param modifier Modifier to be applied to the Canvas.
 * @param color The color of the grid lines, defaulting to semi-transparent cyan.
 */
@Composable
fun DigitalLandscapeBackground(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00FFFF).copy(alpha = 0.3f),
) {
    Canvas(modifier = modifier) {
        drawDigitalLandscape(color)
    }
}

/**
 * Displays a hexagon grid background pattern using a Canvas.
 *
 * Renders a continuous tiling of hexagon outlines in a staggered arrangement, creating a geometric grid effect. The color and opacity of the hexagons can be customized.
 *
 * @param modifier Modifier to apply to the Canvas.
 * @param alpha Opacity of the hexagon grid, from 0.0 (transparent) to 1.0 (opaque).
 * @param color Color of the hexagon outlines, with the specified alpha applied.
 */
@Composable
fun HexagonGridBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.2f,
    color: Color = Color(0xFF00FFFF).copy(alpha = alpha),
) {
    Canvas(modifier = modifier) {
        drawHexagonGrid(color)
    }
}

/**
 * Draws a rectangular grid of evenly spaced vertical and horizontal lines across the canvas.
 *
 * Creates a digital landscape effect by rendering lines at fixed intervals using the specified color.
 *
 * @param color The color to use for the grid lines.
 */
private fun DrawScope.drawDigitalLandscape(color: Color) {
    // Simple grid pattern for digital landscape
    val spacing = 50f
    for (i in 0 until (size.width / spacing).toInt()) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(i * spacing, 0f),
            end = androidx.compose.ui.geometry.Offset(i * spacing, size.height),
            strokeWidth = 1f
        )
    }
    for (i in 0 until (size.height / spacing).toInt()) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, i * spacing),
            end = androidx.compose.ui.geometry.Offset(size.width, i * spacing),
            strokeWidth = 1f
        )
    }
}

/**
 * Renders a staggered grid of hexagon outlines across the canvas to create a seamless hexagonal tiling effect.
 *
 * Each row is horizontally offset to ensure the hexagons interlock, forming a continuous pattern. The size and spacing of the hexagons are fixed, and only hexagons fully within the canvas bounds are drawn.
 *
 * @param color The color used for the hexagon outlines.
 */
private fun DrawScope.drawHexagonGrid(color: Color) {
    // Simple hexagon grid pattern
    val radius = 30f
    val spacing = radius * 1.5f

    for (row in 0 until (size.height / spacing).toInt()) {
        for (col in 0 until (size.width / spacing).toInt()) {
            val x = col * spacing + if (row % 2 == 1) spacing / 2 else 0f
            val y = row * spacing

            if (x < size.width && y < size.height) {
                drawHexagon(
                    center = androidx.compose.ui.geometry.Offset(x, y),
                    radius = radius * 0.8f,
                    color = color
                )
            }
        }
    }
}

/**
 * Draws a hexagon outline centered at the specified position with the given radius and color.
 *
 * The hexagon is constructed by connecting six vertices spaced at 60-degree intervals around the center.
 *
 * @param center The center point of the hexagon.
 * @param radius The distance from the center to each vertex.
 * @param color The color used for the hexagon outline.
 */
private fun DrawScope.drawHexagon(
    center: androidx.compose.ui.geometry.Offset,
    radius: Float,
    color: Color,
) {
    val path = androidx.compose.ui.graphics.Path()
    for (i in 0..5) {
        val angle = i * 60.0 * Math.PI / 180.0
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 1f)
    )
}
