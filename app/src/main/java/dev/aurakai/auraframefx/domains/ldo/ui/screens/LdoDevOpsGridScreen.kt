package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LdoDark)
    ) {
        // ─── CANVAS: THE GRID ───
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Starfield / Micro-dots
            repeat(100) { i ->
                val x = (sin(i * 123f) * 0.5f + 0.5f) * size.width
                val y = (cos(i * 456f) * 0.5f + 0.5f) * size.height
                drawCircle(LdoCyan.copy(alpha = 0.1f), 1f, Offset(x, y))
            }

            // Concentric Rings
            val ringRadii = listOf(140f, 240f, 340f)
            ringRadii.forEach { radius ->
                drawCircle(
                    color = LdoCyan.copy(alpha = 0.1f),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(1f)
                )
                // Inner dashed detail
                drawCircle(
                    color = LdoCyan.copy(alpha = 0.05f),
                    radius = radius - 10f,
                    center = Offset(cx, cy),
                    style = Stroke(0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
                )
            }

            // Central Node
            drawOctagon(cx, cy, 60f, LdoCyan.copy(alpha = 0.2f), filled = true)
            drawOctagon(cx, cy, 60f, LdoCyan, filled = false)
            drawCircle(LdoCyan.copy(alpha = 0.1f), 70f, Offset(cx, cy), style = Stroke(1f))

            // Crosshair lines
            drawLine(LdoCyan.copy(alpha = 0.1f), Offset(cx - 400f, cy), Offset(cx + 400f, cy), 0.5f)
            drawLine(LdoCyan.copy(alpha = 0.1f), Offset(cx, cy - 400f), Offset(cx, cy + 400f), 0.5f)

            // Connection Lines between rings
            nodes.forEach { node ->
                val r1 = ringRadii[node.ringIndex]
                val angleRad = Math.toRadians(node.angle.toDouble()).toFloat()
                val nx = cx + r1 * cos(angleRad)
                val ny = cy + r1 * sin(angleRad)
                
                drawLine(LdoCyan.copy(alpha = 0.05f), Offset(cx, cy), Offset(nx, ny), 0.5f)
            }
        }

        // ─── NODES (Icons) ───
        Box(modifier = Modifier.fillMaxSize()) {
            val ringRadii = listOf(140.dp, 240.dp, 340.dp)
            nodes.forEach { node ->
                val radius = ringRadii[node.ringIndex]
                val angleRad = Math.toRadians(node.angle.toDouble()).toFloat()
                
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(
                            x = (radius.value * cos(angleRad)).dp,
                            y = (radius.value * sin(angleRad)).dp
                        )
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawOctagon(size.width / 2, size.height / 2, size.width / 2, LdoCyan.copy(alpha = 0.1f), filled = true)
                        drawOctagon(size.width / 2, size.height / 2, size.width / 2, LdoCyan, filled = false)
                    }
                    Icon(
                        node.icon,
                        contentDescription = null,
                        tint = LdoCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ─── UI OVERLAY ───
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
