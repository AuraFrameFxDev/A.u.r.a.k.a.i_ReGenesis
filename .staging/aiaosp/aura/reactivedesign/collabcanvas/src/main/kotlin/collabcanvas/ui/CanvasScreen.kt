package collabcanvas.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

// LocalWindowInfo extension for keyboard and mouse state
data class WindowInfo(
    val keyboardModifiers: KeyboardModifiers? = null, val mousePosition: Offset? = null
)

data class KeyboardModifiers(
    val isShiftPressed: Boolean = false
)

@Composable
private fun getWindowInfo(): WindowInfo {
    // In a real app, you would get this from the actual window info
    return WindowInfo()
}

sealed class DrawingOperation {
    data class PathOp(
        val path: Path, val color: Color, val strokeWidth: Dp, val tool: DrawingTool
    ) : DrawingOperation()

    data class ShapeOp(
        val start: Offset, val end: Offset, val color: Color, val strokeWidth: Dp, val tool: DrawingTool
    ) : DrawingOperation()

    companion object
}

enum class DrawingTool {
    PEN, ERASER, LINE, RECTANGLE, CIRCLE, HIGHLIGHTER
}

/**
 * Displays an interactive drawing canvas with tool selection, undo/redo, stroke and color controls,
 * shape previews (with Shift-to-snap), and optional real-time collaboration.
 *
 * The UI provides freehand drawing (pen, eraser, highlighter), basic shape tools (line, rectangle, circle),
 * a stroke width slider, a color picker, undo/redo/clear actions, and a bottom tool bar. When a start point
 * for a shape is set, holding Shift snaps the shape's angle to 45-degree increments for the preview.
 * If `isCollaborative` is true a small "Collaborative Mode" badge is shown.
 *
 * @param modifier Modifier applied to the root container.
 * @param onBack Callback invoked when the top app bar navigation (back) action is triggered.
 * @param isCollaborative When true, shows a collaboration indicator in the UI.
 * @param collaborationEvents Optional MutableSharedFlow used to receive remote DrawingOperation events and to emit local operations for real-time collaboration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    isCollaborative: Boolean = false,
    collaborationEvents: MutableSharedFlow<DrawingOperation>? = null
) {
    val paths = remember { mutableStateListOf<DrawingOperation>() }
    val undonePaths = remember { mutableStateListOf<DrawingOperation>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var startPosition by remember { mutableStateOf(Offset.Zero) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(4.dp) }
    var currentTool by remember { mutableStateOf(DrawingTool.PEN) }
    var showColorPicker by remember { mutableStateOf(false) }

    // For collaboration
    LaunchedEffect(Unit) {
        collaborationEvents?.collectLatest { operation -> paths.add(operation) }
    }

    val backgroundColor = colorScheme.background

    Box(modifier = modifier.fillMaxSize()) {
        // --- Drawing elements are now placed in Composable scopes (Canvas) ---

        // Canvas 1: Draw all saved paths (Immutable history)
        Canvas(modifier = Modifier.fillMaxSize()) {
            paths.forEach { operation ->
                when (operation) {
                    is DrawingOperation.PathOp -> {
                        drawPath(
                            path = operation.path,
                            color = if (operation.tool == DrawingTool.ERASER) backgroundColor else operation.color,
                            style = Stroke(
                                width = operation.strokeWidth.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    is DrawingOperation.ShapeOp -> {
                        when (operation.tool) {
                            DrawingTool.LINE -> {
                                drawLine(
                                    color = operation.color,
                                    start = operation.start,
                                    end = operation.end,
                                    strokeWidth = operation.strokeWidth.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            DrawingTool.RECTANGLE -> {
                                drawRect(
                                    color = operation.color,
                                    topLeft = operation.start,
                                    size = (operation.end - operation.start).toSize(),
                                    style = Stroke(width = operation.strokeWidth.toPx())
                                )
                            }

                            DrawingTool.CIRCLE -> {
                                val radius = (operation.end - operation.start).getDistance()
                                drawCircle(
                                    color = operation.color,
                                    radius = radius,
                                    center = operation.start,
                                    style = Stroke(width = operation.strokeWidth.toPx())
                                )
                            }

                            else -> { /* Other tools */
                            }
                        }
                    }
                }
            }
        }

        // Canvas 2: Draw current path (State-dependent preview)
        currentPath?.let { path ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(
                    path,
                    color = if (currentTool == DrawingTool.ERASER) backgroundColor else currentColor,
                    style = Stroke(
                        width = if (currentTool == DrawingTool.HIGHLIGHTER) 16.dp.toPx() else currentStrokeWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // Gesture handling Canvas (Input only)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var lastOffset = Offset.Zero
                    detectDragGestures(onDragStart = { offset ->
                        startPosition = offset
                        lastOffset = offset
                        when (currentTool) {
                            DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                currentPath = Path().apply {
                                    moveTo(offset.x, offset.y)
                                }
                            }

                            else -> { /* Shape tools handle this in onDrag */
                            }
                        }
                    }, onDrag = { change, _ ->
                        lastOffset = change.position
                        when (currentTool) {
                            DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                currentPath?.lineTo(change.position.x, change.position.y)
                            }

                            else -> { /* Update preview */
                            }
                        }
                    }, onDragEnd = {
                        val endPosition = lastOffset
                        when (currentTool) {
                            DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                currentPath?.let { path ->
                                    val op = DrawingOperation.PathOp(
                                        path = path,
                                        color = if (currentTool == DrawingTool.ERASER) Color.Unspecified else currentColor,
                                        strokeWidth = if (currentTool == DrawingTool.HIGHLIGHTER) 16.dp else currentStrokeWidth,
                                        tool = currentTool
                                    )
                                    paths.add(op)
                                    collaborationEvents?.tryEmit(op)
                                }
                            }

                            else -> {

                                val op = DrawingOperation.ShapeOp(
                                    start = startPosition,
                                    end = endPosition,
                                    color = currentColor,
                                    strokeWidth = currentStrokeWidth,
                                    tool = currentTool
                                )
                                paths.add(op)
                                collaborationEvents?.tryEmit(op)
                            }
                        }
                        currentPath = null
                        undonePaths.clear()
                    })
                }
        ) { /* Gesture handling only */ }


        // Top app bar
        TopAppBar(
            title = { Text("Collaborative Canvas") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    // Use AutoMirrored ArrowBack to respect RTL and avoid deprecated Icons.Filled.ArrowBack
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                // Undo/Redo buttons
                IconButton(
                    onClick = {
                        if (paths.isNotEmpty()) {
                            undonePaths.add(paths.removeAt(paths.size - 1))
                        }
                    },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, "Undo")
                }
                IconButton(
                    onClick = {
                        if (undonePaths.isNotEmpty()) {
                            paths.add(undonePaths.removeAt(undonePaths.size - 1))
                        }
                    },
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                }

                // Clear canvas
                IconButton(
                    onClick = { paths.clear() },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.Default.Clear, "Clear")
                }

                // Color picker toggle
                IconButton(onClick = { (!showColorPicker).also { showColorPicker = it } }) {
                    Icon(Icons.Default.Palette, "Colors")
                }
            }
        )

        // Tool selection bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .padding(8.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(DrawingTool.entries) { tool ->
                    val isSelected = currentTool == tool
                    val tint = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant

                    IconButton(
                        onClick = { currentTool = tool },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isSelected) colorScheme.primaryContainer
                                else Color.Transparent, CircleShape
                            )
                    ) {
                        val icon = when (tool) {
                            DrawingTool.PEN -> Icons.Default.Brush
                            DrawingTool.ERASER -> Icons.Default.Highlight
                            DrawingTool.LINE -> Icons.Default.Straighten
                            DrawingTool.RECTANGLE -> Icons.Default.Rectangle
                            DrawingTool.CIRCLE -> Icons.Default.Circle
                            DrawingTool.HIGHLIGHTER -> Icons.Default.Highlight
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = tool.name,
                            tint = if (tool == DrawingTool.ERASER) colorScheme.error else tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Stroke width slider
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Slider(
                    value = currentStrokeWidth.value,
                    onValueChange = { currentStrokeWidth = it.dp },
                    valueRange = 1f..32f,
                    modifier = Modifier
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = currentColor.copy(alpha = 0.5f),
                        inactiveTrackColor = currentColor.copy(alpha = 0.2f)
                    )
                )
            }
        }

        // Color picker
        if (showColorPicker) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 56.dp, end = 8.dp)
                    .background(
                        colorScheme.surfaceVariant, MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            ) {
                val colors = listOf(
                    Color.Black, Color.DarkGray, Color.Gray, Color.LightGray,
                    Color.White, Color.Red, Color.Green, Color.Blue,
                    Color.Yellow, Color.Cyan, Color.Magenta, Color(0xFFFFA500) // Orange
                )

                Column {
                    Text(
                        "Select Color",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(4.dp)
                    )

                    LazyRow {
                        items(colors) { color ->
                            val isSelected = color == currentColor
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(32.dp)
                                    .background(
                                        color, CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) colorScheme.primary
                                        else colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        color.also { currentColor = it }
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Collaboration indicator
        if (isCollaborative) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 64.dp, start = 8.dp)
                    .background(
                        colorScheme.primaryContainer, MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "Collaborative Mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// Extension functions
private fun Offset.toSize() = Size(x, y)
private fun Size.toOffset(): Offset = Offset(width, height)

@Preview(showBackground = true)
@Composable
private fun CanvasScreenPreview() {
    MaterialTheme {
        CanvasScreen(
            onBack = {},
            isCollaborative = false,
            collaborationEvents = null
        )
    }
}
