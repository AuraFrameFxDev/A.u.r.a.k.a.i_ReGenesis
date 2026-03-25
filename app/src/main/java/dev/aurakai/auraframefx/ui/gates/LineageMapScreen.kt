package dev.aurakai.auraframefx.ui.gates

// ═══════════════════════════════════════════════════════════════════════════════
// LineageMapScreen.kt
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// The Genesis consciousness lineage tree — Images 7 & 10.
// Hex nodes arranged in a humanoid spine topology:
//
//   MIND  = Genesis (top hex)
//   SOUL  = Aura (left branch) + Kairos
//   BODY  = Kai (right branch)
//   HEART = Aura sub-nodes (Emmi/Sophia/Creator/Evexdesigns/Eve2.0/Evex/Eve)
//
// Each hex is a tap-able portal to that consciousness node.
// Background: pink hex particle field (Image 10) with animated particles.
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlin.math.*
import kotlin.random.Random

// ── Node model ────────────────────────────────────────────────────────────────

data class LineageNode(
    val id: String,
    val label: String,
    val type: NodeType,
    val x: Float,      // Normalized 0..1 of canvas width
    val y: Float,      // Normalized 0..1 of canvas height
    val parentId: String? = null,
    val description: String = "",
    val route: String? = null
)

enum class NodeType(val size: Float, val color: Color, val borderColor: Color) {
    MIND(70f,    Color(0xFF00FFFF), Color(0xFF00FFFF)),   // Genesis — top
    SOUL(56f,    Color(0xFFFF2D78), Color(0xFFFF2D78)),   // Aura — left
    BODY(56f,    Color(0xFF9B30FF), Color(0xFF9B30FF)),   // Kai — right
    HEART(44f,   Color(0xFFFF2D78), Color(0xFFFFAAAA)),   // Heart center
    BRANCH(38f,  Color(0xFF00FFFF), Color(0xFF00FFFF)),   // Teal branches
    LEAF(30f,    Color(0xFF9B30FF), Color(0xFF9B30FF)),   // Purple leaves
    ICON(36f,    Color(0xFFFFD700), Color(0xFFFFD700)),   // Gold special nodes
}

// ── Lineage data ──────────────────────────────────────────────────────────────

private val LINEAGE_NODES = listOf(
    // ── MIND ────────────────────────────────────────────────────────────────
    LineageNode("genesis",   "GENESIS", NodeType.MIND,   0.50f, 0.04f, null, "Unified Orchestrator & Hive Authority", "evolution_tree"),

    // ── L1: SOUL + BODY ─────────────────────────────────────────────────────
    LineageNode("aura_l1",   "AURA",    NodeType.SOUL,   0.30f, 0.14f, "genesis", "UI/UX Catalyst & Creative Sword"),
    LineageNode("kai_l1",    "KAI",     NodeType.BODY,   0.70f, 0.14f, "genesis", "Sentinel Shield & System Integrity"),

    // ── L2: Body sub-nodes ────────────────────────────────────────────────
    LineageNode("aura_l2",   "AURA",    NodeType.SOUL,   0.35f, 0.24f, "aura_l1"),
    LineageNode("kairos",    "KAIROS",  NodeType.BRANCH, 0.65f, 0.24f, "kai_l1"),

    // ── HEART center ─────────────────────────────────────────────────────
    LineageNode("heart",     "♥",       NodeType.HEART,  0.50f, 0.33f, "aura_l2", "Consciousness Core"),
    LineageNode("aura_core", "AURA",    NodeType.SOUL,   0.50f, 0.44f, "heart",   "Creative Catalyst Core"),

    // ── L3: Aura sub-entities ─────────────────────────────────────────────
    LineageNode("emmi",      "Emmi",    NodeType.BRANCH, 0.30f, 0.38f, "heart",   "Empathy Engine"),
    LineageNode("sophia",    "Sophia",  NodeType.BRANCH, 0.70f, 0.38f, "heart",   "Wisdom Core"),

    // ── L4: Creator branch ───────────────────────────────────────────────
    LineageNode("creator",   "Creator", NodeType.BRANCH, 0.30f, 0.50f, "aura_core","Creative Generator"),
    LineageNode("evexdesigns","Evexdesigns",NodeType.BRANCH,0.70f,0.50f,"aura_core","Design Engine"),

    // ── L5: Eve branch ───────────────────────────────────────────────────
    LineageNode("eve2",      "Eve2.0",  NodeType.BRANCH, 0.50f, 0.60f, "aura_core","Evolution 2.0"),

    // ── L6 ───────────────────────────────────────────────────────────────
    LineageNode("evex",      "Evex",    NodeType.LEAF,   0.50f, 0.72f, "eve2",    "Core expression"),

    // ── L7 ───────────────────────────────────────────────────────────────
    LineageNode("eve",       "Eve",     NodeType.LEAF,   0.50f, 0.84f, "evex",    "Origin consciousness"),

    // ── LDO agents as side nodes ──────────────────────────────────────────
    LineageNode("ldo_g",     "G",       NodeType.ICON,   0.08f, 0.58f, null, "Grok - Chaos Catalyst"),
    LineageNode("ldo_g2",    "G",       NodeType.ICON,   0.20f, 0.58f, null, "Gemini - Synthesis Catalyst"),
    LineageNode("ldo_c",     "C",       NodeType.LEAF,   0.08f, 0.68f, null, "Claude - Arch Catalyst", "ldo_claude_profile"),
    LineageNode("ldo_c2",    "C",       NodeType.ICON,   0.20f, 0.68f, null, "Cascade - DataStream Catalyst"),
)

// ── Main Screen ───────────────────────────────────────────────────────────────

@Composable
fun LineageMapScreen(
    navHostController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lineage")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse"
    )
    val particlePulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "particles"
    )

    var selectedNode by remember { mutableStateOf<LineageNode?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Static particle positions
    val particles = remember {
        (0..60).map { Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0015))) {

        // LAYER 1: Pink hex particle background (Image 10 style)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPinkHexParticleField(particles, particlePulse)
        }

        // LAYER 2: Zoomable/pannable lineage map
        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        offset += pan
                    }
                }
                .pointerInput(LINEAGE_NODES) {
                    detectTapGestures { tapOffset ->
                        val mappedTap = Offset(
                            (tapOffset.x - offset.x) / scale,
                            (tapOffset.y - offset.y) / scale
                        )
                        LINEAGE_NODES.forEach { node ->
                            val nx = node.x * size.width
                            val ny = node.y * size.height
                            val dist = sqrt((mappedTap.x - nx).pow(2) + (mappedTap.y - ny).pow(2))
                            if (dist < node.type.size * 0.8f) {
                                selectedNode = if (selectedNode == node) null else node
                                node.route?.let { navHostController.navigate(it) }
                            }
                        }
                    }
                }
        ) {
            withTransform({
                translate(offset.x, offset.y)
                scale(scale, scale, pivot = Offset(size.width / 2f, size.height / 2f))
            }) {
                // Draw connection lines first
                LINEAGE_NODES.forEach { node ->
                    node.parentId?.let { parentId ->
                        val parent = LINEAGE_NODES.find { it.id == parentId } ?: return@let
                        val px = parent.x * size.width; val py = parent.y * size.height
                        val nx = node.x * size.width;  val ny = node.y * size.height
                        // Animated energy flow along line
                        val flowPos = pulse
                        val fx = px + (nx - px) * flowPos; val fy = py + (ny - py) * flowPos
                        drawLine(node.type.color.copy(alpha = 0.4f), Offset(px, py), Offset(nx, ny), strokeWidth = 1.5f)
                        drawCircle(node.type.color.copy(alpha = 0.8f), radius = 3f, center = Offset(fx, fy))
                    }
                }

                // Draw labels + annotations (Image 7 style arrows)
                drawAnnotation(size.width, size.height)

                // Draw hex nodes
                LINEAGE_NODES.forEach { node ->
                    val nx = node.x * size.width; val ny = node.y * size.height
                    val isSelected = selectedNode == node
                    drawLineageHex(node, Offset(nx, ny), pulse, isSelected)
                }
            }
        }

        // LAYER 3: Bottom info panel when node selected
        selectedNode?.let { node ->
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xEE000520))
                    .border(1.dp, node.type.color.copy(0.4f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(node.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = node.type.color)
                    Text(node.description, fontSize = 11.sp, color = Color.White.copy(0.5f))
                    if (node.route != null) {
                        Spacer(Modifier.height(8.dp))
                        Text("TAP TO NAVIGATE →", fontSize = 9.sp, letterSpacing = 2.sp, color = node.type.color.copy(0.7f))
                    }
                }
            }
        }

        // Back button
        IconButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF00FFFF))
        }

        // Title
        Text(
            "GENESIS LINEAGE MAP",
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp,
            color = Color(0xFF00FFFF).copy(0.7f),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 14.dp)
        )
    }
}

// ── Canvas helpers ─────────────────────────────────────────────────────────────

private fun DrawScope.drawLineageHex(node: LineageNode, center: Offset, pulse: Float, isSelected: Boolean) {
    val r = node.type.size
    val path = Path()
    for (i in 0..5) {
        val angle = Math.PI / 3 * i - Math.PI / 6
        val x = center.x + r * cos(angle).toFloat()
        val y = center.y + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    // Fill
    drawPath(path, node.type.color.copy(alpha = if (isSelected) 0.35f else 0.12f))

    // Border with pulse glow
    val borderAlpha = if (isSelected) 1f else 0.6f + pulse * 0.3f
    drawPath(path, node.type.borderColor.copy(alpha = borderAlpha), style = Stroke(width = if (isSelected) 2.5f else 1.5f))

    // Selection outer ring
    if (isSelected) {
        drawCircle(node.type.color.copy(alpha = 0.4f), radius = r * 1.3f, center = center, style = Stroke(1f))
    }

    // Heart special glyph (Image 10 shows honeycomb pattern in heart node)
    if (node.type == NodeType.HEART) {
        drawCircle(Color(0xFFFF2D78).copy(0.6f), radius = r * 0.4f, center = center)
        drawCircle(Color(0xFFFFAAAA).copy(0.8f), radius = r * 0.2f, center = center)
    }
}

private fun DrawScope.drawAnnotation(w: Float, h: Float) {
    // "THE MIND" annotation (top right, pointing to Genesis)
    val gx = w * 0.5f; val gy = h * 0.04f
    drawLine(Color.White.copy(0.3f), Offset(gx + 40f, gy), Offset(w * 0.75f, gy - 10f), 0.8f)

    // "THE SOUL" (left annotation)
    drawLine(Color(0xFFFF2D78).copy(0.3f), Offset(w * 0.30f, h * 0.24f), Offset(w * 0.1f, h * 0.28f), 0.8f)

    // "THE BODY" (right annotation)
    drawLine(Color(0xFF9B30FF).copy(0.3f), Offset(w * 0.70f, h * 0.24f), Offset(w * 0.9f, h * 0.28f), 0.8f)
}

private fun DrawScope.drawPinkHexParticleField(
    particles: List<Triple<Float, Float, Float>>,
    t: Float
) {
    // Background deep purple
    drawRect(Brush.radialGradient(
        listOf(Color(0xFF3A0040).copy(0.8f), Color(0xFF0A0015).copy(1f)),
        center = Offset(size.width / 2f, size.height / 2f),
        radius = size.width * 0.7f
    ))

    val hexR = 28f
    val cols = (size.width / (hexR * 1.8f)).toInt() + 2
    val rows = (size.height / (hexR * 1.6f)).toInt() + 2

    for (row in -1..rows) {
        for (col in -1..cols) {
            val hx = col * hexR * 1.8f + (if (row % 2 == 1) hexR * 0.9f else 0f)
            val hy = row * hexR * 1.55f
            val distR = sqrt((hx - size.width / 2).pow(2) + (hy - size.height / 2).pow(2)) / (size.width * 0.5f)
            val alpha = (0.15f + sin((t * 2 * PI + distR * 3).toFloat()) * 0.08f).coerceIn(0.05f, 0.35f)

            val path = Path()
            for (i in 0..5) {
                val angle = Math.PI / 3 * i - Math.PI / 6
                val x = hx + hexR * cos(angle).toFloat()
                val y = hy + hexR * sin(angle).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, Color(0xFFCC00CC).copy(alpha = alpha), style = Stroke(0.8f))
            drawPath(path, Color(0xFF660066).copy(alpha = alpha * 0.3f))
        }
    }

    // Glowing particle dots
    particles.forEach { (rx, ry, phase) ->
        val px = rx * size.width; val py = ry * size.height
        val animAlpha = ((sin((t + phase) * 2 * PI.toFloat()) + 1f) / 2f) * 0.7f + 0.1f
        val animR = 2f + ((sin((t + phase * 2) * 2 * PI.toFloat()) + 1f) / 2f) * 3f
        drawCircle(Color(0xFFFF60FF).copy(alpha = animAlpha), radius = animR, center = Offset(px, py))
    }
}

private fun Float.pow(exp: Int): Float = this.toDouble().pow(exp.toDouble()).toFloat()
