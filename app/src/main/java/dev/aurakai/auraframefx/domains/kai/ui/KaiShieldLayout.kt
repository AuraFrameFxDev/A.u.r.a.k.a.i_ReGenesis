package dev.aurakai.auraframefx.domains.kai.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom layout for the Kai Shield Node Map.
 * Implements 60-degree radial symmetry for node placement.
 *
 * Node Distribution:
 * - 0: The Core (Center)
 * - 1-6: Inner Ring
 * - 7-12: Perimeter Ring
 */
@Composable
fun KaiShieldLayout(
    modifier: Modifier = Modifier,
    innerRadius: Dp = 80.dp,
    outerRadius: Dp = 140.dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // 1. MEASUREMENT PHASE
        val placeables = measurables.map { it.measure(constraints) }

        // Ensure we have the correct center for the layout
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val centerX = width / 2f
        val centerY = height / 2f

        layout(width, height) {
            // 2. PLACEMENT PHASE
            placeables.forEachIndexed { index, placeable ->
                val nodeX: Float
                val nodeY: Float

                when {
                    // The Core (Node 0)
                    index == 0 -> {
                        nodeX = centerX
                        nodeY = centerY
                    }
                    // The Inner Ring (Nodes 1-6)
                    index <= 6 -> {
                        val angle =
                            Math.toRadians(((index - 1) * 60.0) - 90.0) // Start from top (-90)
                        nodeX = (centerX + innerRadius.toPx() * cos(angle)).toFloat()
                        nodeY = (centerY + innerRadius.toPx() * sin(angle)).toFloat()
                    }
                    // The Perimeter Ring (Nodes 7-12)
                    else -> {
                        val angle = Math.toRadians(((index - 7) * 60.0) - 90.0)
                        nodeX = (centerX + outerRadius.toPx() * cos(angle)).toFloat()
                        nodeY = (centerY + outerRadius.toPx() * sin(angle)).toFloat()
                    }
                }

                // Center the item at the calculated coordinates
                placeable.placeRelative(
                    x = (nodeX - placeable.width / 2).toInt(),
                    y = (nodeY - placeable.height / 2).toInt()
                )
            }
        }
    }
}
