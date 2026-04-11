package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * ⬡ KAI SPHERE GRID SCREEN — Tactical Node Map
 *
 * Translated from Stitch export.
 * FFX-style sphere grid with HEAT MAP color scheme:
 * - Inactive nodes: deep cold purple
 * - Active nodes: orange-yellow thermal bloom
 * - Locked nodes: dark red + X redaction bar
 * - Connections: orange heat trail dashed lines
 * - Pannable/zoomable SVG-style canvas
 *
 * Design: #050508 bg, orange heat (#F97316), deep purple (#1A0B2E), stencil headers
 */

private val GridOrange   = Color(0xFFF97316)
private val GridAmber    = Color(0xFFFBBF24)
private val GridPurple   = Color(0xFF312E81)
private val GridDarkPurple = Color(0xFF1A0B2E)
private val GridRed      = Color(0xFF7F1D1D)
private val GridBg       = Color(0xFF050508)

data class SphereNode(
    val id: String,
    val label: String,
    val x: Float,
    val y: Float,
    val state: NodeState
)

data class SphereConnection(val from: Int, val to: Int)

enum class NodeState { ACTIVE, INACTIVE, LOCKED }

private val kaiNodes = listOf(
    SphereNode("SC", "SENTINEL_CORE",      200f, 300f, NodeState.ACTIVE),
    SphereNode("SW", "SHIELD_WALL",        140f, 200f, NodeState.ACTIVE),
    SphereNode("RG", "ROOT_GUARDIAN",      260f, 200f, NodeState.ACTIVE),
    SphereNode("VP", "VETO_PROTOCOL",      140f, 100f, NodeState.ACTIVE),
    SphereNode("DS", "DOMAIN_SEAL",        260f, 100f, NodeState.INACTIVE),
    SphereNode("BL", "BOOT_LOCK",           80f, 100f, NodeState.INACTIVE),
    SphereNode("LF", "LSP_FORGE",          200f, 420f, NodeState.INACTIVE),
    SphereNode("KS", "KAI_SURGE",          320f, 100f, NodeState.INACTIVE),
    SphereNode("OS", "OMEGA_SHIELD",       200f, 500f, NodeState.LOCKED),
    SphereNode("AN", "ABSOLUTE_NO",        320f, 300f, NodeState.LOCKED),
)

private val kaiConnections = listOf(
    SphereConnection(0, 1), SphereConnection(0, 2),
    SphereConnection(1, 3), SphereConnection(2, 4),
    SphereConnection(1, 5), SphereConnection(2, 7),
    SphereConnection(0, 6),
)

@Composable
fun KaiSphereGridScreen(
    nodes: List<SphereNode> = kaiNodes,
    connections: List<SphereConnection> = kaiConnections,
    onNodeSelected: (SphereNode) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")

    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "scan"
    )
    val nodePulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "nPulse"
    )
    val dashOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)),
        label = "dash"
    )

    var selectedNode by remember { mutableStateOf<SphereNode?>(null) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }

    Box(modifier = Modifier.fillMaxSize().background(GridBg)) {

        // CRT scanline
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                drawLine(GridOrange.copy(alpha = 0.07f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 2f)
                var gx = 0f; while (gx < size.width) {
                    drawLine(GridOrange.copy(alpha = 0.03f), Offset(gx, 0f), Offset(gx, size.height), 0.5f)
                    gx += 20f
                }
            }
        })

        // HEADER OVERLAY
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.8f))
                .border(BorderStroke(1.dp, GridOrange.copy(alpha = 0.5f)))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text("KAI_SPHERE_GRID_V4.2", fontFamily = LEDFontFamily, fontSize = 18.sp, color = GridOrange, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text("SECTOR: 09-OMEGA // THERMAL_SYNC: ACTIVE", fontSize = 8.sp, color = GridOrange.copy(alpha = 0.6f))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(GridOrange.copy(alpha = 0.15f))
                .border(BorderStroke(1.dp, GridOrange.copy(alpha = 0.4f)))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Row { Text("CORE_TEMP: ", fontSize = 9.sp, color = GridOrange); Text("88.4°C", fontSize = 9.sp, color = GridAmber, fontWeight = FontWeight.Bold) }
                Text("COORD: 34.22.109.X", fontSize = 7.sp, color = GridOrange.copy(alpha = 0.4f))
            }
        }

        // MAIN GRID CANVAS
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        panOffset += pan
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                    }
                }
        ) {
            val cx = size.width / 2 + panOffset.x
            val cy = size.height / 2 + panOffset.y - 40f

            withTransform({ scale(scale, scale, Offset(cx, cy)) }) {
                // Draw connections
                connections.forEach { conn ->
                    if (conn.from < nodes.size && conn.to < nodes.size) {
                        val fromNode = nodes[conn.from]
                        val toNode   = nodes[conn.to]
                        val fx = cx + (fromNode.x - 200f)
                        val fy = cy + (fromNode.y - 300f)
                        val tx = cx + (toNode.x - 200f)
                        val ty = cy + (toNode.y - 300f)

                        val isActive = fromNode.state == NodeState.ACTIVE && toNode.state == NodeState.ACTIVE
                        drawLine(
                            if (isActive) GridOrange.copy(alpha = 0.6f) else GridPurple.copy(alpha = 0.4f),
                            Offset(fx, fy), Offset(tx, ty),
                            strokeWidth = 3f,
                            pathEffect = if (isActive) PathEffect.dashPathEffect(floatArrayOf(8f, 6f), dashOffset) else PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)
                        )
                    }
                }

                // Draw nodes
                nodes.forEach { node ->
                    val nx = cx + (node.x - 200f)
                    val ny = cy + (node.y - 300f)

                    when (node.state) {
                        NodeState.ACTIVE -> {
                            // Thermal glow
                            drawCircle(GridAmber.copy(alpha = 0.2f * nodePulse), 28f * nodePulse, Offset(nx, ny))
                            drawHexPath(this, nx, ny, 20f, GridAmber, GridOrange.copy(alpha = 0.9f))
                        }
                        NodeState.INACTIVE -> {
                            drawHexPath(this, nx, ny, 20f, GridDarkPurple, GridPurple.copy(alpha = 0.6f))
                        }
                        NodeState.LOCKED -> {
                            drawHexPath(this, nx, ny, 20f, Color(0xFF111111), GridRed.copy(alpha = 0.6f))
                            // Redaction bar
                            drawRect(GridRed.copy(alpha = 0.8f), Offset(nx - 15f, ny - 4f), Size(30f, 8f))
                            // X mark
                            drawLine(Color(0xFFEF4444), Offset(nx - 8f, ny - 8f), Offset(nx + 8f, ny + 8f), 2.5f)
                            drawLine(Color(0xFFEF4444), Offset(nx + 8f, ny - 8f), Offset(nx - 8f, ny + 8f), 2.5f)
                        }
                    }
                }
            }
        }

        // BOTTOM OVERLAY
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .background(GridRed.copy(alpha = 0.15f))
                .border(BorderStroke(1.dp, GridRed.copy(alpha = 0.5f)))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("SYSTEM_ALERT: NODES_LOCKED_BY_REDACTION", fontFamily = LEDFontFamily, fontSize = 9.sp, color = Color(0xFFEF4444), letterSpacing = 1.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.width(128.dp).height(4.dp).background(GridOrange.copy(alpha = 0.2f), RoundedCornerShape(2.dp))) {
                    Box(modifier = Modifier.fillMaxWidth(0.75f).fillMaxHeight().background(GridOrange, RoundedCornerShape(2.dp)))
                }
                Text("UPGRADE_POINTS: 1,420", fontFamily = LEDFontFamily, fontSize = 10.sp, color = GridOrange)
            }
        }

        // NAVIGATION
        Row(
            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.9f))
                .border(BorderStroke(1.dp, GridOrange.copy(alpha = 0.3f)))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                "ZOOM+" to { scale = (scale * 1.3f).coerceAtMost(3f) },
                "ZOOM-" to { scale = (scale * 0.7f).coerceAtLeast(0.5f) },
                "CENTER" to { panOffset = Offset.Zero; scale = 1f },
                "EXIT"  to { onNavigateBack() }
            ).forEach { (label, action) ->
                Box(
                    modifier = Modifier
                        .border(1.dp, if (label == "EXIT") Color(0xFFEF4444).copy(alpha = 0.6f) else GridOrange.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        .background(if (label == "EXIT") Color(0xFF1A0000) else GridOrange.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
                        .clickable { action() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 9.sp, color = if (label == "EXIT") Color(0xFFEF4444) else GridOrange, fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

private fun drawHexPath(scope: DrawScope, cx: Float, cy: Float, r: Float, fillColor: Color, strokeColor: Color) {
    val path = Path().apply {
        val angles = (0..5).map { Math.toRadians((60.0 * it - 30.0)) }
        moveTo(cx + (r * cos(angles[0])).toFloat(), cy + (r * sin(angles[0])).toFloat())
        for (i in 1..5) {
            lineTo(cx + (r * cos(angles[i])).toFloat(), cy + (r * sin(angles[i])).toFloat())
        }
        close()
    }
    scope.drawPath(path, fillColor)
    scope.drawPath(path, strokeColor, style = Stroke(2f))
}
