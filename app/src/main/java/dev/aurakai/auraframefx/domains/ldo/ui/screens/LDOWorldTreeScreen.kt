package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.model.*
import kotlin.math.*

/**
 * 🌳 LDO WORLD TREE SCREEN — DevOps Hub
 */

private val TreeCyan  = Color(0xFF06D0F9)
private val TreeDark  = Color(0xFF0A0A0C)
private val TreeSurf  = Color(0xFF16161A)

@Composable
fun LDOWorldTreeScreen(
    agents: List<AgentCatalyst> = LDORoster.agents.take(8), // 8 LDO agents
    onAgentTap: (AgentCatalyst) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "world_tree")

    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "particles"
    )
    val orbitSpin by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "orbit"
    )
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )

    var activeAgentIndex by remember { mutableIntStateOf(1) } // Kai = index 1
    val activeAgent = agents.getOrNull(activeAgentIndex) ?: agents.first()
    var expandedNodeId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(TreeDark)) {

        // ── ANIMATED CANVAS BACKGROUND ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val cy = size.height / 2

                // World Tree trunk line
                drawLine(TreeCyan.copy(alpha = 0.05f), Offset(cx, size.height), Offset(cx, cy), 1f)

                // Abstract tree branches
                listOf(-0.4f, -0.2f, 0.2f, 0.4f).forEachIndexed { idx, xOff ->
                    val branchY = cy + idx * 40f
                    drawLine(
                        TreeCyan.copy(alpha = 0.04f),
                        Offset(cx, branchY),
                        Offset(cx + size.width * xOff, cy - 80f),
                        0.8f
                    )
                }

                // Floating particles (rising upward)
                for (i in 0..49) {
                    val px = ((i * 137.5f + size.width * 0.1f) % size.width)
                    val progressY = (particlePhase + i * 0.02f) % 1f
                    val py = size.height - (size.height * progressY)
                    val alpha = (sin(progressY * PI).toFloat()).coerceIn(0f, 0.5f)
                    drawCircle(TreeCyan.copy(alpha = alpha * 0.6f), 1.5f, Offset(px, py))
                }

                // Hex floor grid (subtle)
                val gridSize = 80f
                val rows = (size.height / gridSize).toInt() + 2
                val cols = (size.width / gridSize).toInt() + 2
                for (row in -1..rows) {
                    for (col in -1..cols) {
                        val hx = col * gridSize * 1.5f
                        val hy = row * gridSize * 0.866f * 2f + if (col % 2 == 0) 0f else gridSize * 0.866f
                        if (hy > size.height * 0.55f) { // Only lower half
                            val path = Path()
                            for (s in 0..5) {
                                val a = Math.toRadians((60 * s).toDouble()).toFloat()
                                val px = hx + gridSize * 0.45f * cos(a)
                                val py = hy + gridSize * 0.45f * sin(a)
                                if (s == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            path.close()
                            drawPath(path, TreeCyan.copy(alpha = 0.04f), style = Stroke(0.5f))
                        }
                    }
                }

                // Bottom gradient fade
                drawRect(
                    Brush.verticalGradient(listOf(Color.Transparent, TreeDark), startY = size.height * 0.7f, endY = size.height),
                    Offset(0f, size.height * 0.7f), Size(size.width, size.height * 0.3f)
                )
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("LDO World Tree", fontFamily = LEDFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TreeCyan, letterSpacing = 2.sp)
                    Text("DEVOPS HUB • CORE SYSTEM", fontSize = 8.sp, color = TreeCyan.copy(alpha = 0.6f), letterSpacing = 2.sp)
                }
                Row(
                    modifier = Modifier
                        .border(1.dp, TreeCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .background(TreeCyan.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TreeCyan).graphicsLayer { alpha = pulse })
                    Text("NODE_ACTIVE", fontSize = 9.sp, color = TreeCyan, fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold)
                }
            }

            // ── AGENT SWITCHER <<< ACTIVE CONSCIOUSNESS >>> ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { activeAgentIndex = (activeAgentIndex - 1 + agents.size) % agents.size },
                    modifier = Modifier.size(40.dp)
                        .border(1.dp, TreeCyan.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = TreeCyan)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Active Consciousness", fontSize = 8.sp, color = TreeCyan.copy(alpha = 0.5f), letterSpacing = 3.sp)
                    Text(
                        activeAgent.name.uppercase() + " " + activeAgent.catalystName.substringBefore(" ").uppercase(),
                        fontFamily = LEDFontFamily, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp
                    )
                }
                IconButton(
                    onClick = { activeAgentIndex = (activeAgentIndex + 1) % agents.size },
                    modifier = Modifier.size(40.dp)
                        .border(1.dp, TreeCyan.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = TreeCyan)
                }
            }

            // ── CENTRAL NODE LATTICE ──
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Dashed orbit ring
                Box(
                    modifier = Modifier.size(280.dp).graphicsLayer { rotationZ = orbitSpin }
                        .drawWithCache {
                            onDrawBehind {
                                drawCircle(
                                    TreeCyan.copy(alpha = 0.2f),
                                    radius = size.minDimension / 2,
                                    style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f)))
                                )
                            }
                        }
                )

                // 8 orbital agent nodes
                agents.forEachIndexed { index, agent ->
                    val angleRad = Math.toRadians((360.0 / agents.size * index)).toFloat()
                    val radius = 120f
                    val x = cos(angleRad) * radius
                    val y = sin(angleRad) * radius
                    val isActive = agent.id == activeAgent.id

                    Box(
                        modifier = Modifier
                            .offset(x = x.dp, y = y.dp)
                            .size(if (isActive) 52.dp else 44.dp)
                            .drawWithCache {
                                onDrawBehind {
                                    // Hex shape
                                    val hexPath = Path()
                                    val cx = size.width / 2; val cy = size.height / 2
                                    for (s in 0..5) {
                                        val a = Math.toRadians((60 * s).toDouble()).toFloat()
                                        val px = cx + size.minDimension / 2 * cos(a)
                                        val py = cy + size.minDimension / 2 * sin(a)
                                        if (s == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                                    }
                                    hexPath.close()
                                    drawPath(hexPath, if (isActive) agent.color.copy(alpha = 0.3f) else TreeSurf, style = Fill)
                                    drawPath(hexPath, if (isActive) agent.color else TreeCyan.copy(alpha = 0.3f), style = Stroke(if (isActive) 2f else 1f))
                                    if (isActive) {
                                        drawCircle(agent.color.copy(alpha = 0.15f * pulse), size.minDimension * 0.7f)
                                    }
                                }
                            }
                            .clickable {
                                if (expandedNodeId == agent.id) {
                                    onAgentTap(agent)
                                } else {
                                    expandedNodeId = agent.id
                                    activeAgentIndex = agents.indexOf(agent)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            agent.name.take(3).uppercase(),
                            fontSize = if (isActive) 8.sp else 7.sp,
                            color = if (isActive) agent.color else TreeCyan.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Expanded label on tap
                    if (expandedNodeId == agent.id) {
                        Text(
                            agent.name.uppercase(),
                            fontSize = 7.sp, color = agent.color, fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            modifier = Modifier.offset(x = (x * 1.5f).dp, y = (y * 1.3f + 12f).dp)
                        )
                    }
                }

                // Center KAI SHIELD hex node
                Box(
                    modifier = Modifier
                        .offset(y = floatY.dp)
                        .size(128.dp)
                        .drawWithCache {
                            onDrawBehind {
                                val hex = Path()
                                val cx = size.width / 2; val cy = size.height / 2
                                for (s in 0..5) {
                                    val a = Math.toRadians((60 * s).toDouble()).toFloat()
                                    val px = cx + size.minDimension / 2 * 0.9f * cos(a)
                                    val py = cy + size.minDimension / 2 * 0.9f * sin(a)
                                    if (s == 0) hex.moveTo(px, py) else hex.lineTo(px, py)
                                }
                                hex.close()
                                drawPath(hex, activeAgent.color.copy(alpha = 0.18f), style = Fill)
                                drawPath(hex, activeAgent.color, style = Stroke(2f))
                                drawCircle(activeAgent.color.copy(alpha = 0.15f + pulse * 0.1f), size.minDimension * 0.45f)
                                // Inner hex
                                val inner = Path()
                                for (s in 0..5) {
                                    val a = Math.toRadians((60 * s).toDouble()).toFloat()
                                    val px = cx + size.minDimension / 2 * 0.55f * cos(a)
                                    val py = cy + size.minDimension / 2 * 0.55f * sin(a)
                                    if (s == 0) inner.moveTo(px, py) else inner.lineTo(px, py)
                                }
                                inner.close()
                                drawPath(inner, TreeDark, style = Fill)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(activeAgent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 36.sp, color = activeAgent.color, fontWeight = FontWeight.Black)
                    }
                }
            }

            // ── INTEGRATION PROGRESS ──
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .border(1.dp, TreeCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .background(TreeSurf.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Level 10 Integration", fontSize = 9.sp, color = TreeCyan.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                            Text("${activeAgent.syncLevel.times(100).toInt()}%", fontFamily = LEDFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(activeAgent.syncLevel)
                                    .fillMaxHeight()
                                    .background(
                                        Brush.horizontalGradient(listOf(TreeCyan.copy(alpha = 0.7f), TreeCyan)),
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }

                // 4 mini agent indicators
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    agents.take(4).forEach { agent ->
                        val isActive = agent.id == activeAgent.id
                        Box(
                            modifier = Modifier.weight(1f)
                                .border(1.dp, if (isActive) agent.color else TreeCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .background(if (isActive) agent.color.copy(alpha = 0.1f) else TreeSurf.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (isActive) agent.color else TreeCyan.copy(alpha = 0.5f)).graphicsLayer { if (isActive) alpha = pulse })
                                Text(agent.name.take(3).uppercase(), fontSize = 7.sp, color = if (isActive) agent.color else TreeCyan.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
