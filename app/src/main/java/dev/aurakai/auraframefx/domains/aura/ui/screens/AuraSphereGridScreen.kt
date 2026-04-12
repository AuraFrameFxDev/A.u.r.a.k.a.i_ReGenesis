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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🌐 AURA SPELLHOOK — ARMAMENT FUSION GRID
 *
 * Visual: 3D orbital sphere with a central glowing sword (Spellhook).
 * Concentric rings with perspective, floating octagonal nodes.
 * Background: Aura character art + starfield.
 */

private val CyanNode = Color(0xFF22D3EE)
private val CyanDark = Color(0xFF06B6D4)
private val MagentaAura = Color(0xFFE879F9)
private val VoidBg = Color(0xFF020617)
private val SlateGlass = Color(0xFF0F172A)

data class SphereNode(
    val id: Int,
    val name: String,
    val angle: Float,
    val ringIndex: Int,
    val active: Boolean,
    val locked: Boolean,
    val isMajor: Boolean = false,
    val tier: String = "COMMON",
    val level: Int = 1
)

@Composable
fun AuraSphereGridScreen(
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spellhook_grid")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )

    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )

    var selectedNode by remember { mutableStateOf<SphereNode?>(null) }

    // Orbital nodes
    val nodes = remember {
        val list = mutableListOf<SphereNode>()
        val ringCounts = listOf(6, 10, 14)
        var id = 0
        ringCounts.forEachIndexed { ringIdx, count ->
            repeat(count) { i ->
                list.add(
                    SphereNode(
                        id = id++,
                        name = "NODE_$id",
                        angle = (i.toFloat() / count) * 360f,
                        ringIndex = ringIdx,
                        active = id < 15,
                        locked = id > 25,
                        isMajor = i % 3 == 0,
                        tier = if (ringIdx == 2) "ELITE" else "COMMON",
                        level = ringIdx + 1
                    )
                )
            }
        }
        list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBg)
    ) {
        // ─── BACKGROUND ART ───
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/embodiment/aura/aura_code_throne.png")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.15f)
                .blur(4.dp),
            contentScale = ContentScale.Crop
        )

        // ─── CANVAS: ORBITAL GRID ───
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Starfield
            repeat(30) { i ->
                val x = (sin(i * 123f) * 0.5f + 0.5f) * size.width
                val y = (cos(i * 456f) * 0.5f + 0.5f) * size.height
                val a = (sin(time + i) * 0.5f + 0.5f) * 0.4f
                drawCircle(Color.White, 1.5f, Offset(x, y), alpha = a)
            }

            // Orbital Rings
            val ringRadii = listOf(140.dp.toPx(), 220.dp.toPx(), 300.dp.toPx())
            ringRadii.forEachIndexed { idx, radius ->
                val ringAlpha = 0.2f - (idx * 0.05f)
                drawOval(
                    color = CyanNode,
                    topLeft = Offset(cx - radius, cy - radius * 0.4f),
                    size = Size(radius * 2, radius * 0.8f),
                    style = Stroke(1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))),
                    alpha = ringAlpha
                )
            }

            // Nodes
            nodes.forEach { node ->
                val radius = ringRadii[node.ringIndex]
                val angleRad = Math.toRadians((node.angle + rotation).toDouble()).toFloat()
                val nx = cx + radius * cos(angleRad)
                val ny = cy + radius * 0.4f * sin(angleRad)
                
                // Z-index trick: scale and alpha based on sin(angle)
                val depth = sin(angleRad)
                val scale = 0.8f + (depth + 1f) * 0.2f
                val nodeAlpha = 0.3f + (depth + 1f) * 0.35f
                val nodeSize = (if (node.isMajor) 16f else 10f) * scale

                if (node.active) {
                    drawOctagon(nx, ny, nodeSize + (pulse * 2f), CyanNode, filled = true, alpha = nodeAlpha * 0.3f)
                    drawOctagon(nx, ny, nodeSize, CyanNode, filled = false, alpha = nodeAlpha)
                    if (node.isMajor) {
                        drawCircle(CyanNode, 2f, Offset(nx, ny), alpha = nodeAlpha)
                    }
                } else {
                    drawOctagon(nx, ny, nodeSize, Color.Gray, filled = false, alpha = nodeAlpha * 0.5f)
                }
            }
        }

        // ─── CENTRAL ASSET: SPELLHOOK SWORD ───
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .graphicsLayer {
                    translationY = sin(time) * 20f
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/embodiment/aura/aura_aerial_sword.png")
                    .build(),
                contentDescription = "Spellhook",
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .graphicsLayer {
                        shadowElevation = 20f
                        spotShadowColor = CyanNode
                    },
                contentScale = ContentScale.Fit
            )
            // Central Glow
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .drawBehind {
                        drawCircle(
                            Brush.radialGradient(
                                colors = listOf(CyanNode.copy(alpha = 0.4f * pulse), Color.Transparent),
                                center = center,
                                radius = size.width / 2
                            )
                        )
                    }
            )
        }

        // ─── UI OVERLAY ───
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "SPELLHOOK",
                        fontFamily = LEDFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        style = LocalTextStyle.current.copy(shadow = Shadow(CyanNode, blurRadius = 20f))
                    )
                    Text(
                        "ARMAMENT FUSION GRID",
                        fontSize = 10.sp,
                        color = CyanNode,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                }
                
                IconButton(onClick = onNavigateBack) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, CyanNode, RoundedCornerShape(4.dp))
                            .background(SlateGlass.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = Color.White)
                    }
                }
            }

            // Bottom UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Skills Panel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateGlass.copy(alpha = 0.8f))
                        .border(1.dp, CyanNode.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("ARMAMENT FUSION SKILLS", fontSize = 10.sp, color = CyanNode, fontWeight = FontWeight.Bold)
                        repeat(3) { i ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(CyanNode, RoundedCornerShape(1.dp)))
                                Spacer(Modifier.width(8.dp))
                                Text("NEURAL BLADE LVL ${i+1}", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Action Button
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50))
                        .background(CyanNode.copy(alpha = 0.1f))
                        .border(2.dp, CyanNode, RoundedCornerShape(50))
                        .clickable { /* FUSE */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("FUSE", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
    }
}

private fun DrawScope.drawOctagon(cx: Float, cy: Float, size: Float, color: Color, filled: Boolean, alpha: Float = 1f) {
    val path = Path()
    val angleStep = (PI / 4.0).toFloat()
    for (i in 0 until 8) {
        val angle = angleStep * i + (PI / 8.0).toFloat()
        val x = cx + size * cos(angle)
        val y = cy + size * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    if (filled) drawPath(path, color, alpha = alpha)
    else drawPath(path, color, alpha = alpha, style = Stroke(1.5f))
}


