package dev.aurakai.auraframefx.ui.debug.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity // Added import
import androidx.compose.ui.unit.Density // Added import
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.debug.model.Connection
import dev.aurakai.auraframefx.ui.debug.model.ConnectionType
import dev.aurakai.auraframefx.ui.debug.model.GraphNode
import kotlin.math.*
import androidx.compose.ui.geometry.Offset as ComposeOffset
import dev.aurakai.auraframefx.ui.debug.model.Offset as GraphOffset

/**
 * Displays an interactive, zoomable, and pannable graph visualization with selectable nodes.
 *
 * Renders a graph of nodes and their connections on a canvas, supporting pinch-to-zoom and pan gestures.
 * Nodes can be selected, triggering a pulsing animation effect. Connections are drawn with visual styles
 * based on their type, and node labels are displayed below each node. The graph content is centered within
 * the available space, and a grid background is rendered behind the graph.
 *
 * @param nodes The list of graph nodes to display, each with position and connection data.
 * @param selectedNodeId The ID of the currently selected node, if any.
 * @param onNodeSelected Callback invoked when a node is selected, receiving the node's ID.
 * @param modifier Modifier to be applied to the graph container.
 * @param contentPadding Padding to apply around the graph content.
 */
@Composable
fun InteractiveGraph(
    nodes: List<GraphNode>,
    selectedNodeId: String? = null,
    onNodeSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    var scale by remember { mutableStateOf(1f) }
    var translation by remember { mutableStateOf(ComposeOffset.Zero) }
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
            .clip(MaterialTheme.shapes.medium)
    ) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        val density = LocalDensity.current
        val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

        // Calculate content bounds for centering
        val contentWidth = 1000f * scale
        val contentHeight = 800f * scale

        val offsetX = (canvasWidth - contentWidth) / 2 + translation.x
        val offsetY = (canvasHeight - contentHeight) / 2 + translation.y

        // Convert GraphOffset to ComposeOffset for rendering
        fun GraphOffset.toCompose() = ComposeOffset(x.toFloat(), y.toFloat())


        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            translation += pan / scale
                        }
                    )
                }
        ) {
            // Draw grid
            drawGrid(scale, translation, gridLineColor, density)

            // Draw connections first (behind nodes)
            nodes.forEach { node ->
                node.connections.forEach { connection ->
                    val targetNode = nodes.find { it.id == connection.targetId }
                    targetNode?.let { drawConnection(node, it, connection, density) }
                }
            }

            // Draw nodes
            nodes.forEach { node ->
                val isSelected = node.id == selectedNodeId
                val nodePulseScale = if (isSelected) pulse else 1f

                withTransform({ // Assuming this is DrawScope.withTransform, receiver is DrawTransform
                    val globalTransformPivot =
                        ComposeOffset(offsetX, offsetY) + node.position.toCompose() * scale
                    this.scale(
                        scaleX = scale,
                        scaleY = scale,
                        pivotX = globalTransformPivot.x,
                        pivotY = globalTransformPivot.y
                    )

                    val pulseTransformPivot = node.position.toCompose()
                    this.scale(
                        scaleX = nodePulseScale,
                        scaleY = nodePulseScale,
                        pivotX = pulseTransformPivot.x,
                        pivotY = pulseTransformPivot.y
                    )
                }) { // Receiver here is DrawScope
                    drawNode(node, isSelected, density)
                }
            }
        }
    }
}

/**
 * Draws a scalable grid background on the canvas, offset by the current translation.
 *
 * The grid lines are spaced proportionally to the zoom level and panning offset, providing visual reference for graph navigation.
 *
 * @param scale The current zoom level, affecting grid spacing and line thickness.
 * @param translation The current pan offset, shifting the grid accordingly.
 * @param gridLineColor The color to use for the grid lines.
 * @param density The current screen density.
 */
private fun DrawScope.drawGrid(
    scale: Float,
    translation: ComposeOffset,
    gridLineColor: Color,
    density: Density,
) {
    val gridSize = 40f / scale // This is in Px, no Dp conversion needed for logic

    // Draw vertical lines
    for (x in 0 until size.width.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridLineColor,
            start = ComposeOffset(
                x.toFloat(),
                0f
            ) - translation, // Ensure using ComposeOffset if operations defined on it
            end = ComposeOffset(x.toFloat(), size.height) - translation,
            strokeWidth = 1f / scale
        )
    }

    // Draw horizontal lines
    for (y in 0 until size.height.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridLineColor,
            start = ComposeOffset(0f, y.toFloat()) - translation,
            end = ComposeOffset(size.width, y.toFloat()) - translation,
            strokeWidth = 1f / scale
        )
    }
}

/**
 * Draws a single graph node with visual styling and label.
 *
 * Renders the node at its position with a colored background, border, and icon placeholder.
 * If the node is selected, a glowing ring is drawn around it. The node's name is displayed below the node.
 *
 * @param node The graph node to draw.
 * @param isSelected Whether the node is currently selected, affecting its visual appearance.
 * @param density The current screen density.
 */
private fun DrawScope.drawNode(node: GraphNode, isSelected: Boolean, density: Density) {
    with(density) {
        val nodeSizePx = node.type.defaultSize.toPx()
        val centerCompose = node.position.toCompose() // Use ComposeOffset for drawing

        // Draw glow/selection ring
        if (isSelected) {
            val ringWidthPx = 4.dp.toPx()
            drawCircle(
                color = node.type.color.copy(alpha = 0.5f),
                radius = nodeSizePx * 0.7f,
                center = centerCompose,
                style = Stroke(width = ringWidthPx * 2)
            )
        }

        // Draw node background
        drawCircle(
            color = node.type.color.copy(alpha = 0.2f),
            radius = nodeSizePx * 0.6f,
            center = centerCompose
        )

        // Draw node border
        drawCircle(
            color = node.type.color,
            radius = nodeSizePx * 0.6f,
            center = centerCompose,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw node icon background
        val iconSizePx = nodeSizePx * 0.5f
        val iconBgRadiusPx = iconSizePx * 0.8f

        // Draw icon background circle
        drawCircle(
            color = node.type.color,
            radius = iconBgRadiusPx,
            center = centerCompose
        )

        // Draw the icon (simplified - actual icon rendering would require more complex handling)
        // For now, we'll just draw a smaller circle as a placeholder
        drawCircle(
            color = Color.White,
            radius = iconBgRadiusPx * 0.5f,
            center = centerCompose
        )

        // Draw node label
        drawContext.canvas.nativeCanvas.apply {
            val labelTextSizePx = 12.dp.toPx()
            drawText(
                node.name,
                centerCompose.x,
                centerCompose.y + nodeSizePx * 0.8f,
                android.graphics.Paint().apply {
                    color =
                        android.graphics.Color.WHITE // Consider MaterialTheme.colorScheme.onSurface
                    textSize = labelTextSizePx
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

/**
 * Draws a connection line with an arrowhead between two graph nodes, styled according to the connection type.
 *
 * The connection line is rendered as solid or dashed, with color and arrow direction determined by the connection type.
 * The line starts and ends offset from the node centers by their radii to avoid overlapping node visuals.
 * An arrowhead is drawn at the end of the connection to indicate directionality.
 *
 * @param from The source node of the connection.
 * @param to The target node of the connection.
 * @param connection The connection data specifying type and style.
 * @param density The current screen density.
 */
private fun DrawScope.drawConnection(
    from: GraphNode,
    to: GraphNode,
    connection: Connection,
    density: Density,
) {
    with(density) {
        val fromCenterCompose = from.position.toCompose()
        val toCenterCompose = to.position.toCompose()
        // It's important to use ComposeOffset for +/- operations if they are defined for it
        val directionGraphOffset = to.position - from.position // GraphOffset
        val distance =
            sqrt(directionGraphOffset.x * directionGraphOffset.x + directionGraphOffset.y * directionGraphOffset.y)

        if (distance == 0f) return // Avoid division by zero if nodes are at the same position

        val directionNormalizedGraphOffset =
            directionGraphOffset * (1f / distance) // GraphOffset, uses times operator

        // Convert normalized direction to ComposeOffset for use with Compose drawing operations
        val directionNormalizedCompose = directionNormalizedGraphOffset.toCompose()


        val fromRadiusPx = from.type.defaultSize.toPx() * 0.6f
        val toRadiusPx = to.type.defaultSize.toPx() * 0.6f

        val startCompose = fromCenterCompose + directionNormalizedCompose * fromRadiusPx
        val endCompose = toCenterCompose - directionNormalizedCompose * toRadiusPx

        // Draw connection line
        val strokeWidthPx = 2.dp.toPx()
        val color = when (connection.type) {
            ConnectionType.DIRECT -> Color.White.copy(alpha = 0.7f)
            ConnectionType.BIDIRECTIONAL -> Color.Green.copy(alpha = 0.7f)
            ConnectionType.DASHED -> Color.Yellow.copy(alpha = 0.7f)
            // else -> Color.Gray // Make 'when' exhaustive if not already
        }

        if (connection.type == ConnectionType.DASHED) {
            // Draw dashed line
            val dashLength = 10f // px
            val gapLength = 5f   // px
            // Recalculate totalLength based on ComposeOffsets if startCompose/endCompose are used for it
            val lineVector = endCompose - startCompose
            val totalLength = sqrt(lineVector.x * lineVector.x + lineVector.y * lineVector.y)

            if (totalLength <= 0f) return // Nothing to draw if length is zero or negative

            val dashCount = (totalLength / (dashLength + gapLength)).toInt()
            val actualDashVector = directionNormalizedCompose * dashLength

            for (i in 0 until dashCount) {
                val dashStartOffset = directionNormalizedCompose * (i * (dashLength + gapLength))
                val dashStart = startCompose + dashStartOffset
                val dashEnd = dashStart + actualDashVector
                drawLine(
                    color = color,
                    start = dashStart,
                    end = dashEnd,
                    strokeWidth = strokeWidthPx
                )
            }
        } else {
            // Draw solid line
            drawLine(
                color = color,
                start = startCompose,
                end = endCompose,
                strokeWidth = strokeWidthPx
            )
        }

        // Draw arrow head
        // The condition `connection.type != ConnectionType.BIDIRECTIONAL || true` always evaluates to true.
        // It should likely be just `connection.type != ConnectionType.BIDIRECTIONAL` if arrows aren't for bidirectional
        // or always draw if all types have arrows. Assuming arrow for DIRECT and DASHED, and maybe both ends for BIDIRECTIONAL.
        // For now, let's assume it means "draw an arrow pointing towards 'to' node unless it's special".
        // The original logic implies an arrow is always drawn at 'endCompose'.

        val arrowSizePx = 10.dp.toPx()
        val arrowAngleRad =
            Math.PI.toFloat() / 6f // Adjusted for a narrower arrow head, common is PI/6

        // Arrow for 'to' node
        val arrowTip = endCompose
        val arrowBaseOffset = directionNormalizedCompose * arrowSizePx

        val p1 = arrowTip - arrowBaseOffset.rotate(arrowAngleRad, ComposeOffset.Zero)
        val p2 = arrowTip - arrowBaseOffset.rotate(-arrowAngleRad, ComposeOffset.Zero)

        drawPath(
            path = Path().apply {
                moveTo(arrowTip.x, arrowTip.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                close()
            },
            color = color
        )

        // If bidirectional, draw arrow at the other end too
        if (connection.type == ConnectionType.BIDIRECTIONAL) {
            val arrowTipStart = startCompose
            val arrowBaseOffsetStart =
                (directionNormalizedCompose * -1f) * arrowSizePx // Reversed direction

            val p1Start =
                arrowTipStart - arrowBaseOffsetStart.rotate(arrowAngleRad, ComposeOffset.Zero)
            val p2Start =
                arrowTipStart - arrowBaseOffsetStart.rotate(-arrowAngleRad, ComposeOffset.Zero)

            drawPath(
                path = Path().apply {
                    moveTo(arrowTipStart.x, arrowTipStart.y)
                    lineTo(p1Start.x, p1Start.y)
                    lineTo(p2Start.x, p2Start.y)
                    close()
                },
                color = color
            )
        }
    }
}

/**
 * Returns the sum of this [Offset] and another [Offset] as a new [Offset].
 *
 * The resulting [Offset] has its x and y components added element-wise.
 *
 * @return The element-wise sum of the two offsets.
 */
internal operator fun ComposeOffset.plus(other: ComposeOffset): ComposeOffset {
    return ComposeOffset(x + other.x, y + other.y)
}

/**
 * Returns the vector difference between this [Offset] and another [Offset].
 *
 * @return A new [Offset] representing the component-wise subtraction.
 */
internal operator fun ComposeOffset.minus(other: ComposeOffset): ComposeOffset {
    return ComposeOffset(x - other.x, y - other.y)
}

/**
 * Divides the components of this [Offset] by the given scalar value.
 *
 * @param scalar The value to divide both x and y components by.
 * @return A new [Offset] with each component divided by [scalar].
 */
internal operator fun ComposeOffset.div(scalar: Float): ComposeOffset {
    if (scalar == 0f) {
        // Handle division by zero, e.g., return zero offset or throw exception
        // For now, returning zero offset to avoid crash, but this should be reviewed.
        return ComposeOffset(0f, 0f)
    }
    return ComposeOffset(x / scalar, y / scalar)
}

/**
 * Multiplies the components of this [Offset] by the given scalar value.
 *
 * @param scalar The value to multiply both x and y components by.
 * @return A new [Offset] with each component multiplied by [scalar].
 */
internal operator fun ComposeOffset.times(scalar: Float): ComposeOffset {
    return ComposeOffset(x * scalar, y * scalar)
}

/**
 * Rotates this offset around a specified pivot point by the given angle in radians.
 *
 * Optionally applies an additional offset to the pivot after rotation.
 *
 * @param angle The rotation angle in radians.
 * @param pivot The point around which to rotate.
 * @param pivotOffset An optional offset to apply to the pivot after rotation. Defaults to [ComposeOffset.Zero].
 * @return The rotated offset.
 */
private fun ComposeOffset.rotate(
    angle: Float,
    pivot: ComposeOffset,
    pivotOffset: ComposeOffset = ComposeOffset.Zero,
): ComposeOffset {
    val cos = cos(angle)
    val sin = sin(angle)

    val translatedX = x - pivot.x
    val translatedY = y - pivot.y

    val rotatedX = translatedX * cos - translatedY * sin
    val rotatedY = translatedX * sin + translatedY * cos

    return Offset(rotatedX + pivot.x + pivotOffset.x, rotatedY + pivot.y + pivotOffset.y)

}

// Convert GraphOffset to ComposeOffset for rendering
private fun GraphOffset.toCompose(): ComposeOffset = ComposeOffset(this.x, this.y)
