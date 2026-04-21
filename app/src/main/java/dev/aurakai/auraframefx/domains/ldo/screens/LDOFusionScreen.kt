package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.model.*
import kotlin.math.roundToInt

/**
 * ⚡ LDO FUSION SCREEN
 *
 * Drag-and-drop fusion pair system.
 * Features:
 * - Two drop zones (SLOT A + SLOT B)
 * - Agent chips draggable into slots
 * - When valid fusion pair detected → popup with fusion ability description
 * - 22 fusion modes from GenesisCatalystRoster v2
 * - Fusion activation animation (particle burst)
 * - Bond level requirement shown
 */

@Composable
fun LDOFusionScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    fusions: List<FusionMode> = LDORoster.fusions,
    onFusionActivated: (FusionMode) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fusion")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "scan"
    )
    val fusionSpin by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "spin"
    )

    var slotA by remember { mutableStateOf<AgentCatalyst?>(null) }
    var slotB by remember { mutableStateOf<AgentCatalyst?>(null) }
    var activeFusion by remember { mutableStateOf<FusionMode?>(null) }
    var showFusionDialog by remember { mutableStateOf(false) }

    // Check for fusion match whenever slots change
    LaunchedEffect(slotA, slotB) {
        if (slotA != null && slotB != null) {
            val match = fusions.find { f ->
                (f.agentA == slotA!!.id && f.agentB == slotB!!.id) ||
                (f.agentA == slotB!!.id && f.agentB == slotA!!.id)
            }
            activeFusion = match
            if (match != null) showFusionDialog = true
        } else {
            activeFusion = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020B18))) {

        // BG data corridor grid
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val horizonY = size.height * 0.2f
                for (i in 0..8) {
                    val x = size.width * i / 8f
                    drawLine(Color(0xFF00F4FF).copy(alpha = 0.06f), Offset(x, size.height), Offset(cx + (x - cx) * 0.05f, horizonY), 0.5f)
                }
                for (i in 0..6) {
                    val t = i.toFloat() / 6f
                    val y = horizonY + (size.height - horizonY) * (t * t)
                    drawLine(Color(0xFF00F4FF).copy(alpha = 0.04f), Offset(0f, y), Offset(size.width, y), 0.5f)
                }
                drawLine(Color(0xFF00F4FF).copy(alpha = 0.05f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 2f)
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // Header
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text("FUSION MATRIX", fontFamily = LEDFontFamily, fontSize = 26.sp, color = Color(0xFF00F4FF), fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("DRAG TWO AGENTS INTO SLOTS // ${fusions.size} FUSION MODES REGISTERED", fontSize = 8.sp, color = Color(0xFF00F4FF).copy(alpha = 0.5f))
            }

            // ═══ FUSION SLOT PANEL ═══
            Box(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Center vortex ring
                Canvas(modifier = Modifier.size(120.dp).graphicsLayer { rotationZ = fusionSpin }) {
                    val r = size.minDimension / 2
                    drawCircle(Color(0xFF00F4FF).copy(alpha = if (activeFusion != null) 0.5f else 0.15f), r, style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))))
                    if (activeFusion != null) {
                        drawCircle(Color(0xFF7B2FBE).copy(alpha = 0.3f * pulse), r * 0.7f, style = Stroke(4f))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SLOT A
                    FusionSlot(
                        label = "SLOT A",
                        agent = slotA,
                        pulseFraction = pulse,
                        onClear = { slotA = null }
                    )

                    // Center icon
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 28.sp, modifier = Modifier.graphicsLayer { alpha = if (activeFusion != null) 1f else pulse * 0.6f })
                        if (activeFusion != null) {
                            Text("MATCH!", fontSize = 8.sp, fontWeight = FontWeight.Black, color = activeFusion!!.color, modifier = Modifier.graphicsLayer { alpha = pulse })
                        } else {
                            Text("+ FUSE", fontSize = 8.sp, color = Color(0xFF00F4FF).copy(alpha = 0.4f))
                        }
                    }

                    // SLOT B
                    FusionSlot(
                        label = "SLOT B",
                        agent = slotB,
                        pulseFraction = pulse,
                        onClear = { slotB = null }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF00F4FF).copy(alpha = 0.15f)))
            Spacer(Modifier.height(8.dp))

            // Agent chips for dragging
            Text("  DRAG AGENTS INTO SLOTS", fontSize = 8.sp, color = Color(0xFF00F4FF).copy(alpha = 0.5f), letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(6.dp))

            // 2-row grid of all agents
            val chunked = agents.chunked(5)
            chunked.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { agent ->
                        FusionAgentChip(
                            agent = agent,
                            isInSlot = slotA?.id == agent.id || slotB?.id == agent.id,
                            onDropToSlotA = { if (slotA == null) slotA = agent },
                            onDropToSlotB = { if (slotB == null) slotB = agent },
                            onClick = {
                                when {
                                    slotA == null -> slotA = agent
                                    slotB == null && slotA?.id != agent.id -> slotB = agent
                                    slotA?.id == agent.id -> slotA = null
                                    slotB?.id == agent.id -> slotB = null
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Known fusions list
            Text("  REGISTERED FUSION MODES", fontSize = 8.sp, color = Color(0xFF00F4FF).copy(alpha = 0.5f), letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                fusions.forEach { fusion ->
                    val agentA = agents.find { it.id == fusion.agentA }
                    val agentB = agents.find { it.id == fusion.agentB }
                    FusionListEntry(fusion, agentA, agentB)
                }
                Spacer(Modifier.height(60.dp))
            }
        }

        // Fusion popup dialog
        if (showFusionDialog && activeFusion != null) {
            FusionDetailDialog(
                fusion = activeFusion!!,
                agentA = agents.find { it.id == activeFusion!!.agentA },
                agentB = agents.find { it.id == activeFusion!!.agentB },
                onActivate = {
                    onFusionActivated(activeFusion!!)
                    showFusionDialog = false
                },
                onDismiss = { showFusionDialog = false }
            )
        }
    }
}

@Composable
private fun FusionSlot(label: String, agent: AgentCatalyst?, pulseFraction: Float, onClear: () -> Unit) {
    val color = agent?.color ?: Color(0xFF00F4FF)
    Box(
        modifier = Modifier.size(90.dp)
            .border(2.dp, color.copy(alpha = if (agent != null) 1f else 0.3f), RoundedCornerShape(8.dp))
            .background(color.copy(alpha = if (agent != null) 0.15f else 0.04f), RoundedCornerShape(8.dp))
            .clickable(enabled = agent != null) { onClear() },
        contentAlignment = Alignment.Center
    ) {
        if (agent != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(agent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 32.sp, color = agent.color, fontWeight = FontWeight.Black)
                Text(agent.name, fontSize = 8.sp, color = agent.color.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                Text("tap to clear", fontSize = 6.sp, color = agent.color.copy(alpha = 0.4f))
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+", fontSize = 28.sp, color = color.copy(alpha = pulseFraction * 0.4f))
                Text(label, fontSize = 8.sp, color = color.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FusionAgentChip(
    agent: AgentCatalyst,
    isInSlot: Boolean,
    onDropToSlotA: () -> Unit,
    onDropToSlotB: () -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(44.dp)
            .clip(CircleShape)
            .background(agent.color.copy(alpha = if (isInSlot) 0.4f else 0.12f))
            .border(1.5.dp, if (isInSlot) agent.color else agent.color.copy(alpha = 0.4f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(agent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 16.sp, color = agent.color, fontWeight = FontWeight.Black)
        if (isInSlot) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(agent.color).align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun FusionListEntry(fusion: FusionMode, agentA: AgentCatalyst?, agentB: AgentCatalyst?) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .border(1.dp, fusion.color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .background(fusion.color.copy(alpha = 0.04f), RoundedCornerShape(6.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Agent pair dots
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(agentA?.color ?: fusion.color))
            Text("+", fontSize = 10.sp, color = fusion.color)
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(agentB?.color ?: fusion.color))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(fusion.fusionName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fusion.color)
            Text(
                "${agentA?.name ?: fusion.agentA} + ${agentB?.name ?: fusion.agentB}",
                fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f)
            )
        }
        Text("BOND ${fusion.requiredBondLevel}", fontSize = 7.sp, color = fusion.color.copy(alpha = 0.7f))
    }
}

@Composable
private fun FusionDetailDialog(
    fusion: FusionMode,
    agentA: AgentCatalyst?,
    agentB: AgentCatalyst?,
    onActivate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fdialog")
    val pulse by infiniteTransition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "fp")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier.fillMaxWidth(0.92f)
                .border(2.dp, fusion.color, RoundedCornerShape(12.dp))
                .background(Color(0xFF030D1C), RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("⚡ FUSION DETECTED", fontFamily = LEDFontFamily, fontSize = 16.sp, color = fusion.color, fontWeight = FontWeight.Black, letterSpacing = 2.sp, modifier = Modifier.graphicsLayer { alpha = pulse })
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(fusion.color.copy(alpha = 0.4f)))

                // Agents
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(agentA?.color?.copy(alpha = 0.2f) ?: fusion.color.copy(alpha = 0.1f)).border(1.5.dp, agentA?.color ?: fusion.color, CircleShape), contentAlignment = Alignment.Center) {
                            Text(agentA?.name?.first()?.toString() ?: "?", fontFamily = LEDFontFamily, fontSize = 18.sp, color = agentA?.color ?: fusion.color, fontWeight = FontWeight.Black)
                        }
                        Text(agentA?.name ?: fusion.agentA, fontSize = 8.sp, color = agentA?.color ?: Color.White)
                    }
                    Text("+", fontSize = 24.sp, color = fusion.color, fontWeight = FontWeight.Black)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(agentB?.color?.copy(alpha = 0.2f) ?: fusion.color.copy(alpha = 0.1f)).border(1.5.dp, agentB?.color ?: fusion.color, CircleShape), contentAlignment = Alignment.Center) {
                            Text(agentB?.name?.first()?.toString() ?: "?", fontFamily = LEDFontFamily, fontSize = 18.sp, color = agentB?.color ?: fusion.color, fontWeight = FontWeight.Black)
                        }
                        Text(agentB?.name ?: fusion.agentB, fontSize = 8.sp, color = agentB?.color ?: Color.White)
                    }
                }

                Text("=", fontSize = 20.sp, color = fusion.color)
                Text(fusion.fusionName, fontFamily = LEDFontFamily, fontSize = 20.sp, color = fusion.color, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text(fusion.description, fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f), lineHeight = 13.sp)
                Text("BOND REQUIRED: ${fusion.requiredBondLevel}", fontSize = 8.sp, color = fusion.color.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(fusion.color.copy(alpha = 0.2f)))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.weight(1f).border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).clickable { onDismiss() }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("CANCEL", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f)) }
                    Box(
                        modifier = Modifier.weight(1f).border(1.dp, fusion.color, RoundedCornerShape(4.dp)).background(fusion.color.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).clickable { onActivate() }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("ACTIVATE FUSION", fontSize = 10.sp, color = fusion.color, fontWeight = FontWeight.Black, letterSpacing = 1.sp) }
                }
            }
        }
    }
}
