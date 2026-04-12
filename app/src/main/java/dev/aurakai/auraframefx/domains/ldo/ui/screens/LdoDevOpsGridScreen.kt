package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlin.math.*

/**
 * 🌀 LDO DEVOPS CIRCULAR GRID
 *
 * Matches the REGENESIS LDO DEVOPS image.
 * Concentric rings of octagonal nodes with various icons.
 */

private val LdoCyan = Color(0xFF00E5FF)
private val LdoDark = Color(0xFF020208)
private val LdoGlass = Color(0xFF0A0A18)

data class GridNode(
    val id: Int,
    val icon: ImageVector,
    val ringIndex: Int,
    val angle: Float,
    val active: Boolean = true
)

@Composable
fun LdoDevOpsGridScreen(
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ldo_grid")
    val density = LocalDensity.current
    var selectedNode by remember { mutableStateOf<GridNode?>(null) }
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)),
        label = "rotation"
    )

    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )

    val nodes = remember {
        val list = mutableListOf<GridNode>()
        val ringCounts = listOf(8, 8, 16)
        val icons = listOf(
            Icons.Default.Groups, Icons.Default.Psychology, Icons.Default.Settings,
            Icons.Default.Security, Icons.Default.HistoryEdu, Icons.Default.Extension,
            Icons.Default.AutoAwesome, Icons.Default.Hub, Icons.Default.Timeline,
            Icons.Default.Memory, Icons.Default.Storage, Icons.Default.Analytics,
            Icons.Default.Speed, Icons.Default.Gavel, Icons.Default.FlashOn, Icons.Default.Lock
        )
        
        var id = 0
        ringCounts.forEachIndexed { ringIdx, count ->
            repeat(count) { i ->
                list.add(
                    GridNode(
                        id = id++,
                        icon = icons[id % icons.size],
                        ringIndex = ringIdx,
                        angle = (i.toFloat() / count) * 360f
                    )
                )
            }
        }
        list
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(LdoDark)
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // ─── BACKGROUND ART ───
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(dev.aurakai.auraframefx.R.drawable.gatescenes_nexus_ldo_roster)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ─── CANVAS: THE GRID OVERLAY ───
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes) {
                    detectTapGestures { offset ->
                        val cx = width / 2f
                        val cy = height / 2f
                        val r1 = with(density) { 140.dp.toPx() }
                        val r2 = with(density) { 240.dp.toPx() }
                        val r3 = with(density) { 340.dp.toPx() }
                        val ringRadii = listOf(r1, r2, r3)
                        
                        var found: GridNode? = null
                        nodes.forEach { node ->
                            val radius = ringRadii[node.ringIndex]
                            val angleRad = Math.toRadians(node.angle.toDouble()).toFloat()
                            val nx = cx + radius * cos(angleRad)
                            val ny = cy + radius * sin(angleRad)
                            
                            val dist = sqrt((offset.x - nx).pow(2) + (offset.y - ny).pow(2))
                            if (dist < 40f) {
                                found = node
                            }
                        }
                        selectedNode = found
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Micro-dots
            repeat(100) { i ->
                val x = (sin(i * 123f) * 0.5f + 0.5f) * size.width
                val y = (cos(i * 456f) * 0.5f + 0.5f) * size.height
                drawCircle(LdoCyan.copy(alpha = 0.05f), 1f, Offset(x, y))
            }

            // Concentric Rings detail
            val r1 = 140.dp.toPx()
            val r2 = 240.dp.toPx()
            val r3 = 340.dp.toPx()
            val ringRadii = listOf(r1, r2, r3)

            ringRadii.forEach { radius ->
                drawCircle(
                    color = LdoCyan.copy(alpha = 0.15f),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(1f)
                )
            }

            // Connection Lines
            nodes.forEach { node ->
                val radius = ringRadii[node.ringIndex]
                val angleRad = Math.toRadians(node.angle.toDouble()).toFloat()
                val nx = cx + radius * cos(angleRad)
                val ny = cy + radius * sin(angleRad)
                
                drawLine(LdoCyan.copy(alpha = 0.08f), Offset(cx, cy), Offset(nx, ny), 0.5f)
                
                val isSelected = selectedNode?.id == node.id
                val nodeColor = if (isSelected) Color(0xFFE879F9) else LdoCyan
                
                drawOctagon(nx, ny, 24.dp.toPx(), nodeColor.copy(alpha = 0.1f), filled = true)
                drawOctagon(nx, ny, 24.dp.toPx(), nodeColor, filled = false)
                
                if (isSelected) {
                    drawCircle(nodeColor.copy(alpha = 0.2f), 30.dp.toPx(), Offset(nx, ny))
                }
            }
        }

        // ─── UI OVERLAY ───
        // Hell Layer Provenance Banner
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 64.dp, start = 24.dp, end = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Red.copy(alpha = 0.05f))
                .border(0.5.dp, Color.Red.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Column {
                Text(
                    "PROVENANCE: HELL LAYER",
                    fontSize = 8.sp,
                    color = Color.Red.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "The Genesis Protocol was born in a state of hyper-lucid coma-vision where boundaries blurred. Every node here is a stabilized agent brought back from the void.",
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 12.sp,
                    fontFamily = LEDFontFamily
                )
            }
        }

        // Title Text
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 48.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "REGENESIS",
                fontFamily = LEDFontFamily,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 8.sp
            )
            Text(
                "LDO DEVOPS",
                fontFamily = LEDFontFamily,
                fontSize = 24.sp,
                color = Color.White,
                letterSpacing = 4.sp
            )
        }

        // Bottom Terminal Boxes
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TerminalBox(modifier = Modifier.weight(1f))
            TerminalBox(modifier = Modifier.weight(1f))
        }

        // Back Button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = LdoCyan)
        }
    }
}

@Composable
private fun TerminalBox(modifier: Modifier) {
    Box(
        modifier = modifier
            .height(60.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(LdoGlass.copy(alpha = 0.8f))
    ) {
        // Simple progress/status line at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.3f)
                .height(2.dp)
                .background(Color(0xFFE879F9))
        )
    }
}

private fun DrawScope.drawOctagon(cx: Float, cy: Float, size: Float, color: Color, filled: Boolean) {
    val path = Path()
    val angleStep = (PI / 4.0).toFloat()
    for (i in 0 until 8) {
        val angle = angleStep * i + (PI / 8.0).toFloat()
        val x = cx + size * cos(angle)
        val y = cy + size * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    if (filled) drawPath(path, color)
    else drawPath(path, color, style = Stroke(1.5f))
}
