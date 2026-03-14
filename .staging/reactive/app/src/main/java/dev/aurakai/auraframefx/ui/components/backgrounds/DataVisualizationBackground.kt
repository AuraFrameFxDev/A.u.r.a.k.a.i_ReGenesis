package dev.aurakai.auraframefx.ui.components.backgrounds

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

/**
 * An animated data visualization background with flowing lines and nodes
 * @param primaryColor Primary color for the data lines (default: Cyan)
 * @param secondaryColor Secondary color for the data lines (default: Magenta)
 * @param backgroundColor Background color (default: Transparent)
 * @param lineCount Number of data lines (default: 8)
 * @param nodeCount Number of nodes per line (default: 12)
 * @param animationDuration Duration of one animation cycle in milliseconds (default: 10000)
 */
/**
 * Displays an animated background with flowing radial data lines and glowing nodes.
 *
 * Renders a customizable number of animated radial lines, each composed of nodes with dynamic positions and glowing effects, over a configurable background color. The animation creates a flowing, data-inspired visual effect suitable for dashboards or decorative backgrounds.
 *
 * @param modifier Modifier for layout and drawing constraints.
 * @param primaryColor Color used for alternating primary data lines and grid.
 * @param secondaryColor Color used for alternating secondary data lines.
 * @param backgroundColor Background fill color.
 * @param lineCount Number of radial data lines to draw.
 * @param nodeCount Number of nodes per data line.
 * @param animationDuration Duration of one animation cycle in milliseconds.
 */
/**
 * Displays an animated background with flowing radial lines and glowing nodes for data visualization effects.
 *
 * Renders a customizable number of animated radial lines, each with dynamically moving nodes and glowing highlights, over a configurable background. The animation creates a flowing, data-inspired visual suitable for dashboards or decorative backgrounds.
 *
 * @param modifier Modifier for layout and drawing constraints.
 * @param primaryColor Color used for primary data lines and grid circles.
 * @param secondaryColor Color used for alternating secondary data lines.
 * @param backgroundColor Fill color for the background area.
 * @param lineCount Number of radial data lines to display.
 * @param nodeCount Number of nodes per data line.
 * @param animationDuration Duration of one animation cycle in milliseconds.
 */
@Composable
fun DataVisualizationBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Magenta,
    backgroundColor: Color = Color.Transparent,
    lineCount: Int = 8,
    nodeCount: Int = 12,
    animationDuration: Int = 10000,
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.5.dp.toPx() }
    val nodeRadius = with(density) { 2.dp.toPx() }

    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "dataVizBackground")
    val phase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            )
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        // Draw background
        drawRect(color = backgroundColor, size = size)

        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = minOf(size.width, size.height) * 0.8f / 2

        // Draw grid lines
        val gridColor = primaryColor.copy(alpha = 0.1f)
        val gridSteps = 5
        repeat(gridSteps) { i ->
            val radius = maxRadius * (i + 1) / gridSteps
            drawCircle(
                color = gridColor,
                radius = radius,
                center = center,
                style = Stroke(width = 0.5f)
            )
        }

        // Draw data lines and nodes
        repeat(lineCount) { lineIndex ->
            val angle = 2f * PI.toFloat() * lineIndex / lineCount
            val isPrimary = lineIndex % 2 == 0
            val lineColor = if (isPrimary) primaryColor else secondaryColor

            // Calculate points along the line with some noise
            val points = List(nodeCount) { nodeIndex ->
                val progress = nodeIndex.toFloat() / (nodeCount - 1)
                val noise = sin(phase.value * 2 + lineIndex * 0.5f + nodeIndex * 0.3f) * 0.1f
                val radius = (0.3f + 0.7f * progress) * maxRadius * (1 + noise * 0.2f)

                Offset(
                    x = center.x + radius * cos(angle + noise * 0.2f).toFloat(),
                    y = center.y + radius * sin(angle + noise * 0.2f).toFloat()
                )
            }

            // Draw connecting lines
            for (i in 0 until points.size - 1) {
                val alpha = 0.3f + 0.7f * (i.toFloat() / (points.size - 1))
                drawLine(
                    color = lineColor.copy(alpha = alpha),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = strokeWidth * (0.5f + 0.5f * (i.toFloat() / (points.size - 1)))
                )
            }

            // Draw nodes
            points.forEachIndexed { index, point ->
                val alpha = 0.5f + 0.5f * (index.toFloat() / (points.size - 1))
                val radius = nodeRadius * (0.5f + 1.5f * (index.toFloat() / (points.size - 1)))

                // Outer glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            lineColor.copy(alpha = alpha * 0.3f),
                            lineColor.copy(alpha = 0f)
                        ),
                        radius = radius * 3f,
                        center = point
                    ),
                    radius = radius * 3f,
                    center = point
                )

                // Node
                drawCircle(
                    color = lineColor.copy(alpha = alpha),
                    radius = radius,
                    center = point
                )
            }
        }
    }
}

/**
 * Displays a preview of the DataVisualizationBackground composable with preset colors and a dark background.
 */
@Composable
@Preview
fun DataVisualizationBackgroundPreview() {
    DataVisualizationBackground(
        primaryColor = Color.Cyan,
        secondaryColor = Color.Magenta,
        backgroundColor = Color(0xFF0A0A1A)
    )
}
