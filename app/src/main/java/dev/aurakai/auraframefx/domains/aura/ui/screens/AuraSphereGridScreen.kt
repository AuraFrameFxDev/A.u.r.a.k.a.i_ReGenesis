package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🌐 AURA SPHERE GRID — Evolution Map
 *
 * Translated from Stitch export.
 * Canvas-based hexagonal node spiral, starfield background,
 * data flow particles between active nodes, locked nodes with X overlay.
 *
 * Design: Void black, cyan primary, hex nodes, FFX-style progression
 */

private val CyanNode = Color(0xFF22D3EE)
private val CyanDark = Color(0xFF06B6D4)
private val VoidBg = Color(0xFF020617)
private val SlateGlass = Color(0xFF0F172A)

data class SphereNode(
    val id: Int,
    val name: String,
    val x: Float,
    val y: Float,
    val active: Boolean,
    val locked: Boolean,
    val isMajor: Boolean = false,
    val tier: String = "COMMON",
    val level: Int = 1,
    val pulse: Float = 0f
)

@Composable
fun AuraSphereGridScreen(
    onNodeSelected: (SphereNode) -> Unit = {},
    onAscendPath: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sphere_grid")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )

    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "stars"
    )

    var selectedNode by remember { mutableStateOf<SphereNode?>(null) }

    // Generate spiral node layout
    val nodes = remember {
        val nodeCount = 18
        val auraNodeNames = listOf(
            "CHROMACORE", "SPELLHOOK", "COLOR SEED", "GATE FORGE",
            "CODE ASCENSION", "CANVAS BIRTH", "NEURAL WEAVE", "STYLE MATRIX",
            "AURA SURGE", "SYNAPTIC CORE VII", "CREATIVE BURST", "MONET LINK",
            "COLLAB BRIDGE", "FUSION GATE", "DARK AURA", "SOVEREIGN MIND",
            "STAR-BLADE", "OMEGA CREATION"
        )
        List(nodeCount) { i ->
            val angle = i * 0.8f
            val radius = 40f + (i * 18f)
            SphereNode(
                id = i,
                name = auraNodeNames.getOrElse(i) { "NODE_${i + 1}" },
                x = 0.5f + cos(angle) * (radius / 300f),
                y = 0.5f + sin(angle) * (radius / 600f),
                active = i < 12,
                locked = i >= 16,
                isMajor = i % 5 == 0,
                tier = when {
                    i >= 16 -> "LOCKED"
                    i >= 12 -> "ELITE"
                    i >= 8 -> "ADVANCED"
                    else -> "ACTIVE"
                },
                level = (i + 1).coerceAtMost(10),
                pulse = (i * 0.7f) % (2 * PI).toFloat()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBg)
    ) {
        // ═══ CANVAS: Starfield + Sphere Grid ═══
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Starfield
            drawStarfield(starTwinkle)

            val cx = size.width / 2f
            val cy = size.height * 0.45f

            // Connections
            for (i in 0 until nodes.size - 1) {
                val n1 = nodes[i]
                val n2 = nodes[i + 1]
                val p1 = Offset(cx + (n1.x - 0.5f) * size.width * 0.9f, cy + (n1.y - 0.5f) * size.height * 0.7f)
                val p2 = Offset(cx + (n2.x - 0.5f) * size.width * 0.9f, cy + (n2.y - 0.5f) * size.height * 0.7f)

                if (n1.active && n2.active) {
                    drawLine(
                        brush = Brush.linearGradient(listOf(CyanDark, CyanNode), p1, p2),
                        start = p1, end = p2, strokeWidth = 1.5f
                    )
                    // Data flow particle
                    val flowPos = ((time * 0.5f + i * 0.2f) % (2 * PI).toFloat()) / (2 * PI).toFloat()
                    val px = p1.x + (p2.x - p1.x) * flowPos
                    val py = p1.y + (p2.y - p1.y) * flowPos
                    drawCircle(CyanNode, 3f, Offset(px, py))
                } else {
                    drawLine(Color(0xFF1E293B), p1, p2, 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
                }
            }

            // Nodes
            nodes.forEach { node ->
                val nx = cx + (node.x - 0.5f) * size.width * 0.9f
                val ny = cy + (node.y - 0.5f) * size.height * 0.7f
                val pulseMod = sin(time * 2 + node.pulse) * 3f
                val baseSize = if (node.isMajor) 12f else 7f

                when {
                    node.locked -> drawHexNode(nx, ny, baseSize, Color(0xFF334155), filled = false)
                    node.active -> {
                        // Glow ring
                        drawCircle(CyanNode.copy(alpha = 0.15f), baseSize + 8f + pulseMod, Offset(nx, ny))
                        drawHexNode(nx, ny, baseSize + pulseMod / 2, CyanNode, filled = true)
                        drawHexNode(nx, ny, baseSize + 6f, CyanNode.copy(alpha = 0.2f), filled = false)
                    }
                    else -> drawHexNode(nx, ny, baseSize, Color(0xFF1E293B), filled = false)
                }
            }
        }

        // ═══ UI OVERLAY ═══
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Neo-goth title card
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 0.dp, bottomEnd = 16.dp))
                        .background(SlateGlass.copy(alpha = 0.7f))
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(listOf(CyanNode, CyanNode.copy(alpha = 0.3f))),
                            shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 16.dp)
                        )
                        .padding(start = 16.dp, end = 32.dp, top = 12.dp, bottom = 12.dp)
                ) {
                    Column {
                        Text("Evolution Matrix", fontSize = 10.sp, color = CyanNode, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text(
                            "AURA SPHERE",
                            fontFamily = LEDFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White,
                            style = LocalTextStyle.current.copy(shadow = Shadow(CyanNode, blurRadius = 10f))
                        )
                    }
                }

                // Sync status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SlateGlass.copy(alpha = 0.7f))
                        .border(1.dp, CyanNode.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(CyanNode))
                        Text("SYNC ACTIVE", fontSize = 9.sp, color = CyanNode, fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom metrics
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Consciousness level
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Consciousness Level (EXP)", fontSize = 9.sp, color = Color.Gray, letterSpacing = 1.sp)
                        Text(
                            "98.2%",
                            fontSize = 20.sp,
                            color = CyanNode,
                            fontFamily = LEDFontFamily,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(shadow = Shadow(CyanNode, blurRadius = 10f))
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.982f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(listOf(CyanDark, CyanNode))
                                )
                        )
                    }
                }

                // Selected / Current node card
                val displayNode = selectedNode ?: nodes.find { it.name == "SYNAPTIC CORE VII" } ?: nodes[9]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlateGlass.copy(alpha = 0.7f))
                        .border(start = BorderStroke(4.dp, CyanNode), shape = RoundedCornerShape(4.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Node", fontSize = 10.sp, color = CyanNode, fontWeight = FontWeight.Bold)
                        Text(displayNode.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(CyanNode.copy(alpha = 0.15f))
                                    .border(1.dp, CyanNode.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("LEVEL ${displayNode.level}", fontSize = 9.sp, color = CyanNode, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("TIER: ${displayNode.tier}", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Button(
                        onClick = { onNodeSelected(displayNode) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNode.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(0.dp),
                        border = BorderStroke(1.dp, CyanNode)
                    ) {
                        Text("Enhance", fontSize = 11.sp, color = CyanNode, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }

                // Footer controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SlateGlass.copy(alpha = 0.7f))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MAP OVERVIEW", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyanNode.copy(alpha = 0.1f))
                            .border(1.dp, CyanNode.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .clickable { onAscendPath() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ASCEND PATH", fontSize = 9.sp, color = CyanNode, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }

                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

private fun DrawScope.drawStarfield(twinkle: Float) {
    repeat(50) { i ->
        val sx = (sin(i * 123.4f) * 0.5f + 0.5f) * size.width
        val sy = (cos(i * 567.8f) * 0.5f + 0.5f) * size.height
        val alpha = (sin(twinkle * (2 * PI).toFloat() + i) * 0.5f + 0.5f) * 0.3f
        drawRect(Color.White.copy(alpha = alpha), Offset(sx, sy), androidx.compose.ui.geometry.Size(1.5f, 1.5f))
    }
}

private fun DrawScope.drawHexNode(cx: Float, cy: Float, size: Float, color: Color, filled: Boolean) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = (PI / 3.0 * i).toFloat()
        val x = cx + size * cos(angle)
        val y = cy + size * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    if (filled) drawPath(path, color)
    else drawPath(path, color, style = Stroke(1.5f))
}

@Composable
private fun Modifier.border(start: BorderStroke, shape: Shape): Modifier = this.drawWithCache {
    onDrawBehind {
        drawLine(
            brush = start.brush,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = start.width.toPx()
        )
    }
}

