package dev.aurakai.auraframefx.domains.aura.ui.components.graph

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
    var translation by remember { mutableStateOf(Offset.Zero) }
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

        // Calculate content bounds for centering
        val contentWidth = 1000f * scale
        val contentHeight = 800f * scale

        val offsetX = (canvasWidth - contentWidth) / 2 + translation.x
        val offsetY = (canvasHeight - contentHeight) / 2 + translation.y

        val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        val nodeTextColor = Color.White // Or MaterialTheme.colorScheme.onPrimary if appropriate

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            translation = translation + (pan / scale)
                        }
                    )
                }
        ) {
            // Draw grid
            drawGrid(scale, translation, gridColor)

            // Draw connections first (behind nodes)
            nodes.forEach { node ->
                node.connections.forEach { connection ->
                    val targetNode = nodes.find { it.id == connection.targetId }
                    targetNode?.let { drawConnection(node, it, connection) }
                }
            }

            // Draw nodes
            nodes.forEach { node ->
                val isSelected = node.id == selectedNodeId
                val nodeScale = if (isSelected) pulse else 1f
                val currentOffset = Offset(offsetX, offsetY) + node.position.toCompose() * scale

                withTransform({
                    translate(
                        left = currentOffset.x - node.position.toCompose().x * scale * nodeScale,
                        top = currentOffset.y - node.position.toCompose().y * scale * nodeScale
                    )
                    scale(scale * nodeScale, scale * nodeScale, pivot = node.position.toCompose())
                }) {
                    drawNode(node, isSelected, nodeTextColor, this)
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
 * @param gridColor The color of the grid lines.
 */
private fun DrawScope.drawGrid(scale: Float, translation: Offset, gridColor: Color) {
    val gridSize = 40f * scale // Adjust grid size with scale
    val strokeWidth = (1f / scale).coerceAtLeast(0.5f) // Ensure minimum stroke width

    var x = translation.x % gridSize
    while (x < size.width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = strokeWidth
        )
        x += gridSize
    }

    var y = translation.y % gridSize
    while (y < size.height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidth
        )
        y += gridSize
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
 * @param textColor The color for the node's label.
 * @param drawScope The DrawScope to draw on.
 */
private fun drawNode(node: GraphNode, isSelected: Boolean, textColor: Color, drawScope: DrawScope) {
    with(drawScope) {
        val nodeSize = node.type.defaultSize.toPx()
        val center = node.position.toCompose() // Use the toCompose() extension

        // Draw glow/selection ring
        if (isSelected) {
            val ringWidth = 4.dp.toPx()
            drawCircle(
                color = node.type.color.copy(alpha = 0.5f),
                radius = nodeSize * 0.7f,
                center = center,
                style = Stroke(width = ringWidth * 2)
            )
        }

        // Draw node background
        drawCircle(
            color = node.type.color.copy(alpha = 0.2f),
            radius = nodeSize * 0.6f,
            center = center
        )

        // Draw node border
        drawCircle(
            color = node.type.color,
            radius = nodeSize * 0.6f,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw node icon background
        val iconSize = nodeSize * 0.5f
        val iconBgRadius = iconSize * 0.8f

        // Draw icon background circle
        drawCircle(
            color = node.type.color,
            radius = iconBgRadius,
            center = center
        )

        // Draw the icon (simplified - actual icon rendering would require more complex handling)
        // For now, we'll just draw a smaller circle as a placeholder
        drawCircle(
            color = Color.White,
            radius = iconBgRadius * 0.5f,
            center = center
        )

        // Draw node label
        // Note: For more complex text rendering, consider using TextMeasurer
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                node.name,
                center.x,
                center.y + nodeSize * 0.8f + 12.dp.toPx(), // Adjusted Y for better placement
                android.graphics.Paint().apply {
                    color = textColor.toArgb() // Use passed textColor
                    textSize = 12.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
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
 */
private fun DrawScope.drawConnection(
    from: GraphNode,
    to: GraphNode,
    connection: VisualConnection,
) {
    val fromCenter = from.position.toCompose()
    val toCenter = to.position.toCompose()
    val direction = toCenter - fromCenter
    val distance = sqrt(direction.x.pow(2) + direction.y.pow(2)) // Use Float.pow
    if (distance == 0f) return // Avoid division by zero if nodes are at the same position
    val directionNormalized = direction / distance

    val fromRadius = from.type.defaultSize.toPx(this) * 0.6f
    val toRadius = to.type.defaultSize.toPx(this) * 0.6f

    val start = fromCenter + directionNormalized * fromRadius
    val end = toCenter - directionNormalized * toRadius

    // Draw connection line
    val strokeWidth = 2.dp.toPx(this)
    val color = when (connection.type) {
        ConnectionType.DIRECT -> Color.White.copy(alpha = 0.7f)
        ConnectionType.BIDIRECTIONAL -> Color.Green.copy(alpha = 0.7f)
        ConnectionType.DASHED -> Color.Yellow.copy(alpha = 0.7f)
        // It's good practice to have an else branch for when, even if you expect all cases to be covered
        else -> Color.Gray
    }

    if (connection.type == ConnectionType.DASHED) {
        // Draw dashed line
        val dashLength = 10.dp.toPx(this)
        val gapLength = 5.dp.toPx(this)
        val lineLength = distance - fromRadius - toRadius
        if (lineLength <= 0) return // No space to draw the line

        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        drawLine(
            color = color,
            start = start, // Use the calculated start and end for the dashed line
            end = end,
            strokeWidth = strokeWidth,
            pathEffect = pathEffect
        )
    } else {
        // Draw solid line
        drawLine(
            color = color,
            start = start,
            end = end,
            strokeWidth = strokeWidth
        )
    }

    // Draw arrow head
    if (connection.type == ConnectionType.DIRECT || connection.type == ConnectionType.BIDIRECTIONAL) {
        val arrowSize = 10.dp.toPx(this)
        val arrowAngle = (PI / 6).toFloat() // 30 degrees for a narrower arrow

        // Arrowhead for 'to' node
        drawArrowHead(end, directionNormalized, arrowSize, arrowAngle, color)

        // Arrowhead for 'from' node if bidirectional
        if (connection.type == ConnectionType.BIDIRECTIONAL) {
            drawArrowHead(start, -directionNormalized, arrowSize, arrowAngle, color)
        }
    }
}

private fun DrawScope.drawArrowHead(
    tip: Offset,
    direction: Offset,
    size: Float,
    angle: Float,
    color: Color,
) {
    val arrowPath = Path().apply {
        val p1 = tip - (direction.rotate(angle) * size)
        val p2 = tip - (direction.rotate(-angle) * size)
        moveTo(tip.x, tip.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        close()
    }
    drawPath(path = arrowPath, color = color)
}


// Helper extension for Dp to Px conversion within DrawScope
fun Dp.toPx(drawScope: DrawScope): Float = with(drawScope) { this@toPx.toPx() }

// Helper extension for GraphOffset to Compose Offset - already defined in Composable
// fun Offset.toCompose(): Offset = Offset(this.x.toFloat(), this.y.toFloat())


/**
 * Rotates this offset by the given angle in radians.
 * This assumes rotation around the origin (0,0).
 * If rotation around a pivot is needed, translate to origin, rotate, then translate back.
 *
 * @param angle The rotation angle in radians.
 * @return The rotated offset.
 */
fun Offset.rotate(angle: Float): Offset {
    val cosAngle = cos(angle)
    val sinAngle = sin(angle)
    return Offset(x * cosAngle - y * sinAngle, x * sinAngle + y * cosAngle)
}

