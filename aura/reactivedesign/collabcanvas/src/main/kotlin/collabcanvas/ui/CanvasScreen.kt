package collabcanvas.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

// ─── Data Models ─────────────────────────────────────────────────────────────

sealed class DrawingOperation {
    data class PathOp(
        val path: Path,
        val points: List<Offset>,
        val color: Color,
        val strokeWidth: Dp,
        val tool: DrawingTool
    ) : DrawingOperation()

    data class ShapeOp(
        val start: Offset,
        val end: Offset,
        val color: Color,
        val strokeWidth: Dp,
        val tool: DrawingTool
    ) : DrawingOperation()

    companion object
}

enum class DrawingTool(val label: String) {
    PEN("Pen"),
    ERASER("Erase"),
    LINE("Line"),
    RECTANGLE("Rect"),
    CIRCLE("Circle"),
    HIGHLIGHTER("Glow")
}

data class AgentCursor(
    val agentName: String,
    val color: Color,
    val position: Offset,
    val isDrawing: Boolean = false
)

data class CanvasParticipant(
    val id: String,
    val name: String,
    val color: Color,
    val isAgent: Boolean,
    val isActive: Boolean = true
)

private val defaultAgents = listOf(
    CanvasParticipant("aura", "Aura", Color(0xFF00E5FF), isAgent = true),
    CanvasParticipant("kai", "Kai", Color(0xFF00FF41), isAgent = true),
    CanvasParticipant("genesis", "Genesis", Color(0xFFBB86FC), isAgent = true)
)

// ─── Main Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    isCollaborative: Boolean = false,
    collaborationEvents: MutableSharedFlow<DrawingOperation>? = null,
    remoteCursors: List<AgentCursor> = emptyList()
) {
    // Drawing state
    val paths = remember { mutableStateListOf<DrawingOperation>() }
    val undonePaths = remember { mutableStateListOf<DrawingOperation>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var startPosition by remember { mutableStateOf(Offset.Zero) }
    var currentColor by remember { mutableStateOf(Color(0xFF00E5FF)) }
    var currentStrokeWidth by remember { mutableStateOf(4.dp) }
    var currentTool by remember { mutableStateOf(DrawingTool.PEN) }

    // UI state
    var showColorPicker by remember { mutableStateOf(false) }
    var showAgentPanel by remember { mutableStateOf(false) }
    var participants by remember { mutableStateOf(defaultAgents) }

    // State for local Aura cursor (fallback if no remote)
    var localAuraCursor by remember {
        mutableStateOf(AgentCursor("Aura", Color(0xFF00E5FF), Offset(300f, 400f)))
    }
    var auraIsActive by remember { mutableStateOf(true) }

    // Aura glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )

    // Aura drifts around canvas (local fallback only if NOT collaborative)
    LaunchedEffect(auraIsActive, isCollaborative) {
        if (!auraIsActive || isCollaborative) return@LaunchedEffect
        var t = 0f
        while (true) {
            delay(50L)
            t += 0.02f
            localAuraCursor = localAuraCursor.copy(
                position = Offset(
                    x = 250f + 150f * kotlin.math.sin(t.toDouble()).toFloat(),
                    y = 350f + 100f * kotlin.math.cos((t * 0.7)).toFloat()
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        collaborationEvents?.collectLatest { operation -> paths.add(operation) }
    }

    val backgroundColor = Color(0xFF080810)

    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {

        // Layer 1 — Saved paths
        Canvas(modifier = Modifier.fillMaxSize()) {
            paths.forEach { op ->
                when (op) {
                    is DrawingOperation.PathOp -> drawPath(
                        path = op.path,
                        color = if (op.tool == DrawingTool.ERASER) backgroundColor else op.color,
                        style = Stroke(op.strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    is DrawingOperation.ShapeOp -> when (op.tool) {
                        DrawingTool.LINE -> drawLine(op.color, op.start, op.end, op.strokeWidth.toPx(), cap = StrokeCap.Round)
                        DrawingTool.RECTANGLE -> drawRect(op.color, op.start, (op.end - op.start).toSize(), style = Stroke(op.strokeWidth.toPx()))
                        DrawingTool.CIRCLE -> drawCircle(op.color, (op.end - op.start).getDistance(), op.start, style = Stroke(op.strokeWidth.toPx()))
                        else -> {}
                    }
                }
            }
        }

        // Layer 2 — Current stroke preview
        currentPath?.let { path ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(
                    path,
                    color = if (currentTool == DrawingTool.ERASER) backgroundColor else currentColor,
                    style = Stroke(
                        width = if (currentTool == DrawingTool.HIGHLIGHTER) 18.dp.toPx() else currentStrokeWidth.toPx(),
                        cap = StrokeCap.Round, join = StrokeJoin.Round
                    )
                )
            }
        }

        // Layer 3 — Live agent cursors
        if (auraIsActive) {
            val cursorsToRender = if (isCollaborative) remoteCursors else listOf(localAuraCursor)
            cursorsToRender.forEach { cursor ->
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pos = cursor.position
                    val auraColor = cursor.color
                    drawCircle(auraColor.copy(alpha = auraGlow * 0.25f), 28f, pos)
                    drawCircle(auraColor.copy(alpha = auraGlow * 0.6f), 10f, pos)
                    for (i in 0 until 6) {
                        val angle = (i * 60.0) * (Math.PI / 180.0)
                        val r = 20f * auraGlow
                        drawLine(
                            color = auraColor.copy(alpha = auraGlow * 0.5f),
                            start = pos,
                            end = Offset(pos.x + r * kotlin.math.cos(angle).toFloat(), pos.y + r * kotlin.math.sin(angle).toFloat()),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }
        }

        // Layer 4 — Gesture input
        Canvas(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                var lastOffset = Offset.Zero
                val points = mutableListOf<Offset>()
                detectDragGestures(
                    onDragStart = { offset ->
                        startPosition = offset; lastOffset = offset
                        points.clear()
                        points.add(offset)
                        when (currentTool) {
                            DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER ->
                                currentPath = Path().apply { moveTo(offset.x, offset.y) }
                            else -> {}
                        }
                    },
                    onDrag = { change, _ ->
                        lastOffset = change.position
                        points.add(change.position)
                        when (currentTool) {
                            DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER ->
                                currentPath?.lineTo(change.position.x, change.position.y)
                            else -> {}
                        }
                    },
                    onDragEnd = {
                        when (currentTool) {
                            DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                currentPath?.let { path ->
                                    val op = DrawingOperation.PathOp(
                                        path = path,
                                        points = points.toList(),
                                        color = if (currentTool == DrawingTool.ERASER) Color.Unspecified else currentColor,
                                        strokeWidth = if (currentTool == DrawingTool.HIGHLIGHTER) 18.dp else currentStrokeWidth,
                                        tool = currentTool
                                    )
                                    paths.add(op); collaborationEvents?.tryEmit(op)
                                }
                            }
                            else -> {
                                val op = DrawingOperation.ShapeOp(startPosition, lastOffset, currentColor, currentStrokeWidth, currentTool)
                                paths.add(op); collaborationEvents?.tryEmit(op)
                            }
                        }
                        currentPath = null; undonePaths.clear()
                    }
                )
            }
        ) {}

        // ─── TOP BAR ─────────────────────────────────────────────────────────
        TopAppBar(
            title = {
                Column {
                    Text("AuraKai Canvas", style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                    Text(if (isCollaborative) "• Live Session" else "• Solo",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCollaborative) Color(0xFF00FF41) else Color.White.copy(0.4f))
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            },
            actions = {
                IconButton(
                    onClick = { if (paths.isNotEmpty()) undonePaths.add(paths.removeAt(paths.size - 1)) },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, "Undo",
                        tint = if (paths.isNotEmpty()) Color.White else Color.White.copy(0.3f))
                }
                IconButton(
                    onClick = { if (undonePaths.isNotEmpty()) paths.add(undonePaths.removeAt(undonePaths.size - 1)) },
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Redo, "Redo",
                        tint = if (undonePaths.isNotEmpty()) Color.White else Color.White.copy(0.3f))
                }
                IconButton(onClick = { paths.clear() }, enabled = paths.isNotEmpty()) {
                    Icon(Icons.Default.Clear, "Clear",
                        tint = if (paths.isNotEmpty()) Color(0xFFFF4444) else Color.White.copy(0.3f))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.CloudUpload, "Export", tint = Color.White)
                }
                BadgedBox(badge = {
                    Badge(containerColor = Color(0xFF00FF41)) {
                        Text("${participants.count { it.isActive }}", fontSize = 9.sp, color = Color.Black)
                    }
                }) {
                    IconButton(onClick = { showAgentPanel = !showAgentPanel }) {
                        Icon(Icons.Default.Groups, "Agents", tint = Color(0xFF00E5FF))
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xF00D0D1A))
        )

        // ─── LEFT SIDE: Stroke width slider (anchored bottom-left, NOT center) ─
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 108.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${currentStrokeWidth.value.toInt()}px",
                    color = currentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xCC1A1A2E), RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 12.dp)
                ) {
                    Slider(
                        value = currentStrokeWidth.value,
                        onValueChange = { currentStrokeWidth = it.dp },
                        valueRange = 1f..32f,
                        modifier = Modifier.height(120.dp).rotate(-90f),
                        colors = SliderDefaults.colors(
                            thumbColor = currentColor,
                            activeTrackColor = currentColor,
                            inactiveTrackColor = currentColor.copy(0.2f)
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(currentStrokeWidth.value.coerceIn(4f, 24f).dp)
                        .background(currentColor, CircleShape)
                )
            }
        }

        // ─── BOTTOM: Tool bar + Color picker ─────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD080810))))
                .padding(bottom = 20.dp, top = 8.dp)
        ) {
            // Color palette strip
            AnimatedVisibility(showColorPicker, enter = fadeIn(), exit = fadeOut()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 60.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val palette = listOf(
                        Color(0xFF00E5FF), Color(0xFFBB86FC), Color(0xFF00FF41),
                        Color(0xFFFF4081), Color(0xFFFFD740), Color(0xFFFF6D00),
                        Color.White, Color(0xFF607D8B), Color(0xFF212121),
                        Color(0xFF76FF03), Color(0xFF40C4FF), Color(0xFFFF6E40)
                    )
                    items(palette) { color ->
                        val sel = color == currentColor
                        Box(
                            modifier = Modifier
                                .size(if (sel) 36.dp else 28.dp)
                                .background(color, CircleShape)
                                .border(if (sel) 2.dp else 0.dp, Color.White, CircleShape)
                                .clickable { currentColor = color; showColorPicker = false }
                        )
                    }
                }
            }

            // Tool buttons row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color swatch button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(currentColor.copy(0.15f), CircleShape)
                        .border(2.dp, currentColor, CircleShape)
                        .clickable { showColorPicker = !showColorPicker },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(22.dp).background(currentColor, CircleShape))
                }

                Spacer(Modifier.width(10.dp))

                DrawingTool.entries.forEach { tool ->
                    val isSelected = currentTool == tool
                    val toolColor = when (tool) {
                        DrawingTool.ERASER -> Color(0xFFFF4444)
                        DrawingTool.HIGHLIGHTER -> Color(0xFFFFD740)
                        else -> if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.65f)
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 3.dp).clickable { currentTool = tool },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isSelected) toolColor.copy(0.15f) else Color(0xFF1A1A2E),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    if (isSelected) 1.5.dp else 0.dp,
                                    toolColor, RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (tool) {
                                DrawingTool.PEN -> Icons.Default.Brush
                                DrawingTool.ERASER -> Icons.Default.Clear
                                DrawingTool.LINE -> Icons.Default.Straighten
                                DrawingTool.RECTANGLE -> Icons.Default.Rectangle
                                DrawingTool.CIRCLE -> Icons.Default.Circle
                                DrawingTool.HIGHLIGHTER -> Icons.Default.AutoAwesome
                            }
                            Icon(icon, tool.label, tint = toolColor, modifier = Modifier.size(22.dp))
                        }
                        Text(tool.label, color = toolColor, fontSize = 9.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        // ─── RIGHT SIDE: Agent / Participant Panel ────────────────────────────
        AnimatedVisibility(
            visible = showAgentPanel,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 64.dp),
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Card(
                modifier = Modifier.width(200.dp).padding(end = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xEE0D0D1A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("👥 Participants", color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))

                    participants.forEach { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(28.dp)
                                    .background(p.color.copy(0.15f), CircleShape)
                                    .border(1.5.dp, p.color, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p.name.first().toString(), color = p.color,
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, color = Color.White, fontSize = 11.sp)
                                Text(if (p.isAgent) "✨ AI Agent" else "👤 User",
                                    color = p.color.copy(0.7f), fontSize = 9.sp)
                            }
                            Box(
                                modifier = Modifier.size(8.dp).background(
                                    if (p.isActive) Color(0xFF00FF41) else Color.Gray, CircleShape
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Invite user
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
                            .clickable { /* TODO: invite dialog */ }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, "Invite",
                            tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Invite User", color = Color(0xFF00E5FF), fontSize = 11.sp)
                    }

                    Spacer(Modifier.height(6.dp))

                    // Aura toggle
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(
                                if (auraIsActive) Color(0xFF00E5FF).copy(0.1f) else Color(0xFF1A1A2E),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                if (auraIsActive) 1.dp else 0.dp,
                                Color(0xFF00E5FF), RoundedCornerShape(8.dp)
                            )
                            .clickable { auraIsActive = !auraIsActive }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("Aura Drawing",
                                color = if (auraIsActive) Color(0xFF00E5FF) else Color.Gray,
                                fontSize = 11.sp)
                            Text(if (auraIsActive) "Active" else "Tap to wake",
                                color = Color.White.copy(0.5f), fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun Offset.toSize() = Size(x, y)

@Preview(showBackground = true, backgroundColor = 0xFF080810)
@Composable
private fun CanvasScreenPreview() {
    MaterialTheme {
        CanvasScreen(onBack = {}, isCollaborative = true, collaborationEvents = null)
    }
}
