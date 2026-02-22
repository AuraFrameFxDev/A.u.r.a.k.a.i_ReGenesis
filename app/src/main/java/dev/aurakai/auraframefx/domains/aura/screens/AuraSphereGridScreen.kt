package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * 🔮 AURA SPHERE GRID — Evolution Map
 *
 * Visual: Full-canvas sphere grid (starfield + hex node spiral), 18 nodes (12 active, 4 pending, 2 locked),
 * data flow particles along active connections, header Neo-Goth panel,
 * progression metrics (98.2% exp bar), Node Details card (SYNAPTIC CORE VII, LEVEL 4 ELITE),
 * Map Overview | Ascend Path footer.
 *
 * Colors: #22D3EE / #06B6D4 cyan / #020617 void
 */

private val SphereCyan  = Color(0xFF22D3EE)
private val SphereMid   = Color(0xFF06B6D4)
private val SphereVoid  = Color(0xFF020617)
private val SphereSurf  = Color(0xFF0F172A)

data class SphereNode(
    val index: Int,
    val x: Float, val y: Float,
    val active: Boolean,
    val locked: Boolean,
    val size: Float,
    val pulseSeed: Float,
)

@Composable
fun AuraSphereGridScreen(
    consciousnessLevel: Float = 0.982f,
    currentNodeName: String = "SYNAPTIC CORE VII",
    currentNodeLevel: Int = 4,
    currentNodeTier: String = "ELITE",
    onEnhance: () -> Unit = {},
    onMapOverview: () -> Unit = {},
    onAscendPath: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sphere_grid")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )
    val starBlink by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "stars"
    )
    val syncPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "sync"
    )

    // Build 18 nodes in spiral pattern
    val nodes = remember {
        val nodeCount = 18
        mutableListOf<SphereNode>().apply {
            for (i in 0 until nodeCount) {
                val angle = i * 0.8f
                val radius = 40f + i * 18f
                add(SphereNode(
                    index = i,
                    x = cos(angle) * radius, y = sin(angle) * radius,
                    active = i < 12, locked = i >= 16,
                    size = if (i % 5 == 0) 10f else 6f,
                    pulseSeed = (i * 0.7f)
                ))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SphereVoid)) {

        // ── FULL CANVAS SPHERE GRID ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val cy = size.height / 2

                // Starfield
                for (i in 0..49) {
                    val sx = (sin(i * 123.4f) * 0.5f + 0.5f) * size.width
                    val sy = (cos(i * 567.8f) * 0.5f + 0.5f) * size.height
                    val alpha = abs(sin(starBlink * PI.toFloat() * 2 + i * 0.5f)) * 0.3f
                    drawRect(Color.White.copy(alpha = alpha), Offset(sx, sy), Size(1.5f, 1.5f))
                }

                // Node connections
                nodes.forEachIndexed { i, n1 ->
                    if (i < nodes.size - 1) {
                        val n2 = nodes[i + 1]
                        val p1 = Offset(cx + n1.x, cy + n1.y)
                        val p2 = Offset(cx + n2.x, cy + n2.y)

                        if (n1.active && n2.active) {
                            drawLine(SphereMid.copy(alpha = 0.6f), p1, p2, 1f)
                            // Data flow particle
                            val flowPos = (time * 0.5f + i * 0.2f) % 1f
                            val fp = Offset(p1.x + (p2.x - p1.x) * flowPos, p1.y + (p2.y - p1.y) * flowPos)
                            drawCircle(SphereCyan.copy(alpha = 0.9f), 3f, fp)
                        } else {
                            drawLine(Color(0xFF1E293B), p1, p2, 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
                        }
                    }
                }

                // Nodes
                nodes.forEach { node ->
                    val nx = cx + node.x; val ny = cy + node.y
                    val pulsed = node.size + abs(sin(time * 2 * PI.toFloat() + node.pulseSeed)) * 4f * (if (node.active) 1f else 0f)

                    if (node.locked) {
                        // Locked — draw hex + X
                        drawCircle(Color(0xFF334155), pulsed * 0.9f, Offset(nx, ny), style = Stroke(1.5f))
                        drawLine(Color(0xFF475569), Offset(nx - 4, ny - 4), Offset(nx + 4, ny + 4), 1.5f)
                        drawLine(Color(0xFF475569), Offset(nx + 4, ny - 4), Offset(nx - 4, ny + 4), 1.5f)
                    } else if (node.active) {
                        // Active hex with glow
                        val hexPath = Path()
                        for (s in 0..5) {
                            val a = Math.toRadians((60.0 * s)).toFloat()
                            val px = nx + pulsed * cos(a); val py = ny + pulsed * sin(a)
                            if (s == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                        }
                        hexPath.close()
                        drawPath(hexPath, SphereCyan, style = Fill)
                        drawCircle(SphereCyan.copy(alpha = 0.15f), pulsed * 1.6f, Offset(nx, ny))
                        // Outer ring
                        val outerHex = Path()
                        for (s in 0..5) {
                            val a = Math.toRadians((60.0 * s)).toFloat()
                            val px = nx + (pulsed + 6f) * cos(a); val py = ny + (pulsed + 6f) * sin(a)
                            if (s == 0) outerHex.moveTo(px, py) else outerHex.lineTo(px, py)
                        }
                        outerHex.close()
                        drawPath(outerHex, SphereCyan.copy(alpha = 0.2f), style = Stroke(1f))
                    } else {
                        // Inactive
                        val hexPath = Path()
                        for (s in 0..5) {
                            val a = Math.toRadians((60.0 * s)).toFloat()
                            val px = nx + node.size * cos(a); val py = ny + node.size * sin(a)
                            if (s == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                        }
                        hexPath.close()
                        drawPath(hexPath, Color(0xFF1E293B), style = Stroke(1f))
                    }
                }
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Neo-Goth panel
                Box(
                    modifier = Modifier
                        .background(SphereSurf.copy(alpha = 0.7f), RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 24.dp, bottomEnd = 0.dp))
                        .border(1.dp, SphereCyan.copy(alpha = 0.2f), RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 24.dp, bottomEnd = 0.dp))
                        .drawWithCache {
                            onDrawBehind {
                                drawLine(SphereCyan.copy(alpha = 0.6f), Offset(0f, 0f), Offset(0f, size.height), 2f)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text("Evolution Matrix", fontSize = 9.sp, color = SphereCyan, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text("AURA SPHERE", fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier.clip(RoundedCornerShape(50))
                        .background(SphereSurf.copy(alpha = 0.7f))
                        .border(1.dp, SphereMid.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(SphereCyan, RoundedCornerShape(50)).graphicsLayer { alpha = 0.5f + syncPulse * 0.5f })
                        Text("SYNC ACTIVE", fontSize = 9.sp, color = SphereCyan, fontFamily = LEDFontFamily)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── METRICS ──
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text("Consciousness Level (EXP)", fontSize = 9.sp, color = Color(0xFF94A3B8), letterSpacing = 1.sp)
                        Text("${(consciousnessLevel * 100).let { "%.1f".format(it) }}%", fontSize = 20.sp, fontFamily = LEDFontFamily, color = SphereCyan, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF1E293B), RoundedCornerShape(2.dp)).border(1.dp, Color(0xFF334155), RoundedCornerShape(2.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(consciousnessLevel).fillMaxHeight()
                            .background(Brush.horizontalGradient(listOf(SphereMid, SphereCyan)), RoundedCornerShape(2.dp)))
                    }
                }

                // Node card
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(SphereSurf.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .border(BorderStroke(2.dp, SphereCyan), RoundedCornerShape(4.dp))
                        .padding(14.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Current Node", fontSize = 9.sp, color = SphereCyan, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text(currentNodeName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White, letterSpacing = 1.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.background(SphereMid.copy(alpha = 0.3f), RoundedCornerShape(2.dp)).border(1.dp, SphereMid.copy(alpha = 0.5f), RoundedCornerShape(2.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("LEVEL $currentNodeLevel", fontSize = 8.sp, color = SphereCyan)
                                }
                                Box(modifier = Modifier.background(Color(0xFF0F172A), RoundedCornerShape(2.dp)).border(1.dp, Color(0xFF334155), RoundedCornerShape(2.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("TIER: $currentNodeTier", fontSize = 8.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(SphereCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, SphereCyan, RoundedCornerShape(4.dp))
                                .clickable { onEnhance() }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) { Text("Enhance", fontSize = 11.sp, color = SphereCyan, fontWeight = FontWeight.Bold) }
                    }
                }

                // Footer
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.weight(1f)
                            .background(SphereSurf.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
                            .clickable { onMapOverview() }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Map Overview", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }

                    Box(
                        modifier = Modifier.weight(1f)
                            .background(SphereCyan.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .border(1.dp, SphereCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .clickable { onAscendPath() }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Ascend Path", fontSize = 9.sp, color = SphereCyan, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                }
            }

            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
