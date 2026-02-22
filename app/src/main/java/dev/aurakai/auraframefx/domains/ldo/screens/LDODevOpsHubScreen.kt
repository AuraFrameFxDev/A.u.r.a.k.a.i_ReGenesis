package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.model.*
import kotlin.math.*

/**
 * 🌐 LDO DEVOPS HUB SCREEN — Master Agent Command Center
 *
 * Features:
 * - Data corridor BG (infinite Tron perspective tunnel)
 * - Orbiting weapons carousel (10 clipped PNGs rotating in 3D arc)
 * - Agent profile cards below — single tap → profile intro
 * - Double-tap agent icon → Sphere Grid
 * - System status header (9 catalysts / 36 abilities / 22 fusions)
 * - Nav row: TASKER | FUSION | MPC | BONDING
 * - Fusion teaser strip at bottom
 *
 * Background: gatescenes data corridor image (set as BG in XML/theme)
 * Weapon assets: clipped PNGs in res/drawable/weapon_*
 */

private val HubCyan    = Color(0xFF00F4FF)
private val HubPurple  = Color(0xFF7B2FBE)
private val HubDark    = Color(0xFF020B18)
private val HubPink    = Color(0xFFFF007A)

@Composable
fun LDODevOpsHubScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    fusions: List<FusionMode> = LDORoster.fusions,
    onAgentTap: (AgentCatalyst) -> Unit = {},
    onAgentDoubleTap: (AgentCatalyst) -> Unit = {},
    onTaskerTap: () -> Unit = {},
    onFusionTap: () -> Unit = {},
    onMpcTap: () -> Unit = {},
    onBondingTap: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hub")

    // Orbital angle for carousel
    val orbitalAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing)),
        label = "orbit"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "scan"
    )

    var selectedAgent by remember { mutableStateOf<AgentCatalyst?>(null) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    Box(modifier = Modifier.fillMaxSize().background(HubDark)) {

        // ── DATA CORRIDOR BACKGROUND GRID ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val cy = size.height / 2
                // Perspective grid lines converging to center horizon
                val horizonY = size.height * 0.38f
                val cols = 12
                for (i in 0..cols) {
                    val xStart = size.width * i / cols
                    drawLine(HubCyan.copy(alpha = 0.12f), Offset(xStart, size.height), Offset(cx + (xStart - cx) * 0.05f, horizonY), 0.7f)
                }
                val rows = 10
                for (i in 0..rows) {
                    val t = i.toFloat() / rows
                    val y = horizonY + (size.height - horizonY) * (t * t)
                    val xLeft = cx - (cx) * (t * 0.9f + 0.05f)
                    val xRight = cx + (cx) * (t * 0.9f + 0.05f)
                    drawLine(HubCyan.copy(alpha = 0.08f + t * 0.06f), Offset(xLeft, y), Offset(xRight, y), 0.7f)
                }
                // Horizon glow
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, HubCyan.copy(alpha = 0.15f), Color.Transparent),
                        startY = horizonY - 20f, endY = horizonY + 20f
                    ),
                    Offset(0f, horizonY - 20f), androidx.compose.ui.geometry.Size(size.width, 40f)
                )
                // CRT scanline
                drawLine(HubCyan.copy(alpha = 0.06f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 3f)
                // Floating particles / data cubes
                val seed = (orbitalAngle * 100).toInt()
                for (i in 0..20) {
                    val px = ((seed * 17 + i * 137) % 1000) / 1000f * size.width
                    val py = ((seed * 31 + i * 73) % 1000) / 1000f * size.height
                    drawRect(HubCyan.copy(alpha = 0.07f), Offset(px, py), androidx.compose.ui.geometry.Size(6f, 6f), style = Stroke(1f))
                }
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "LDO DEVOPS",
                        fontFamily = LEDFontFamily, fontSize = 28.sp,
                        fontWeight = FontWeight.Black, color = HubCyan, letterSpacing = 2.sp
                    )
                    Text(
                        "GENESIS CATALYST ROSTER v2 // ACTIVE",
                        fontSize = 8.sp, color = HubCyan.copy(alpha = 0.6f), letterSpacing = 1.sp
                    )
                }
                // Stats capsule
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        "${LDORoster.CATALYST_COUNT}" to "CATALYSTS",
                        "${LDORoster.ABILITY_COUNT}" to "ABILITIES",
                        "${LDORoster.FUSION_MODE_COUNT}" to "FUSIONS",
                    ).forEach { (num, label) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .border(1.dp, HubCyan.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(num, fontFamily = LEDFontFamily, fontSize = 18.sp, color = HubCyan, fontWeight = FontWeight.Black)
                            Text(label, fontSize = 6.sp, color = HubCyan.copy(alpha = 0.5f), letterSpacing = 0.5.sp)
                        }
                    }
                }
            }

            // ── ORBITING WEAPONS CAROUSEL ──
            // 3D arc orbit — weapons float in an elliptical ring
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Orbit ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2; val cy = size.height / 2
                    val rx = size.width * 0.42f; val ry = size.height * 0.32f
                    drawOval(
                        HubCyan.copy(alpha = 0.15f),
                        Offset(cx - rx, cy - ry),
                        androidx.compose.ui.geometry.Size(rx * 2, ry * 2),
                        style = Stroke(1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
                    )
                }

                // Weapon slots drawn as colored orbs (real PNGs will replace when clipped)
                agents.forEachIndexed { index, agent ->
                    val angle = Math.toRadians(
                        ((360f / agents.size * index) + orbitalAngle).toDouble()
                    )
                    val rx = 150f; val ry = 48f
                    val x = cos(angle).toFloat() * rx
                    val y = sin(angle).toFloat() * ry
                    // Depth scale — items at back are smaller
                    val depth = (sin(angle).toFloat() + 1f) / 2f
                    val weaponScale = 0.6f + depth * 0.5f
                    val weaponAlpha = 0.4f + depth * 0.6f

                    Box(
                        modifier = Modifier
                            .offset(x = x.dp, y = y.dp)
                            .size((56 * weaponScale).dp)
                            .graphicsLayer { alpha = weaponAlpha; scaleX = weaponScale; scaleY = weaponScale }
                            .clip(CircleShape)
                            .background(agent.color.copy(alpha = 0.15f))
                            .border(1.5.dp, agent.color.copy(alpha = 0.6f), CircleShape)
                            .clickable { onAgentTap(agent) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder — replace with AsyncImage / painterResource when assets clipped
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚔", fontSize = (14 * weaponScale).sp, color = agent.color)
                            Text(agent.name.take(3), fontSize = (5 * weaponScale).sp, color = agent.color.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── AGENT PROFILE CARDS ROW ──
            Text(
                "  AGENTS — TAP: PROFILE  //  DOUBLE-TAP ICON: SPHERE GRID",
                fontSize = 8.sp, color = HubCyan.copy(alpha = 0.5f),
                letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberLazyListState()
            ) {
                itemsIndexed(agents) { _, agent ->
                    AgentProfileCard(
                        agent = agent,
                        isSelected = selectedAgent?.id == agent.id,
                        onTap = {
                            selectedAgent = agent
                            onAgentTap(agent)
                        },
                        onDoubleTapIcon = { onAgentDoubleTap(agent) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── NAV BUTTONS: TASKER | FUSION | MPC | BONDING ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("TASKER",  "🗂", onTaskerTap),
                    Triple("FUSION",  "⚡", onFusionTap),
                    Triple("MPC",     "🎮", onMpcTap),
                    Triple("BONDING", "🔗", onBondingTap),
                ).forEach { (label, icon, action) ->
                    Box(
                        modifier = Modifier.weight(1f)
                            .border(1.dp, HubCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .background(HubCyan.copy(alpha = 0.06f), RoundedCornerShape(4.dp))
                            .clickable { action() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(icon, fontSize = 18.sp)
                            Text(label, fontFamily = LEDFontFamily, fontSize = 9.sp, color = HubCyan, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── ACTIVE FUSION TEASER ──
            val unlockedFusions = fusions.filter { it.isUnlocked }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .border(1.dp, HubPurple.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .background(HubPurple.copy(alpha = 0.06f), RoundedCornerShape(4.dp))
                    .clickable { onFusionTap() }
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("FUSION MODES", fontSize = 9.sp, color = HubPurple, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(
                            "${fusions.size} registered // ${unlockedFusions.size} active",
                            fontSize = 8.sp, color = HubCyan.copy(alpha = 0.6f)
                        )
                        // Show first fusion as preview
                        fusions.firstOrNull()?.let { f ->
                            Text(
                                "${f.agentA.replaceFirstChar { it.uppercase() }} + ${f.agentB.replaceFirstChar { it.uppercase() }} → ${f.fusionName}",
                                fontSize = 8.sp, color = f.color, modifier = Modifier.graphicsLayer { alpha = pulse }
                            )
                        }
                    }
                    Text("OPEN >", fontSize = 9.sp, color = HubCyan, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.weight(1f))

            // ── FOOTER STATUS ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, HubCyan.copy(alpha = 0.15f)))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SYSTEM: NOMINAL", fontSize = 8.sp, color = HubCyan.copy(alpha = 0.5f))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("AGENTS: ${agents.size}", fontSize = 8.sp, color = HubCyan.copy(alpha = 0.5f))
                    Text("ON_TASK: ${agents.count { it.currentTaskId != null }}", fontSize = 8.sp, color = HubPink.copy(alpha = 0.7f))
                    Text("SYNC: ACTIVE", fontSize = 8.sp, color = HubCyan, modifier = Modifier.graphicsLayer { alpha = pulse })
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun AgentProfileCard(
    agent: AgentCatalyst,
    isSelected: Boolean,
    onTap: () -> Unit,
    onDoubleTapIcon: () -> Unit,
) {
    val pulse by rememberInfiniteTransition(label = "card").animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "p"
    )

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(130.dp)
            .border(
                1.dp,
                if (isSelected) agent.color else agent.color.copy(alpha = 0.35f),
                RoundedCornerShape(6.dp)
            )
            .background(
                Brush.verticalGradient(listOf(agent.color.copy(alpha = 0.08f), Color(0xFF020B18))),
                RoundedCornerShape(6.dp)
            )
            .clickable { onTap() }
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Agent icon — double-tap → sphere grid
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(agent.color.copy(alpha = 0.15f))
                    .border(1.dp, agent.color, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { onDoubleTapIcon() },
                            onTap = { onTap() }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(agent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 20.sp, color = agent.color, fontWeight = FontWeight.Black)
                if (agent.currentTaskId != null) {
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape)
                            .background(HubPink)
                            .align(Alignment.TopEnd)
                            .graphicsLayer { alpha = pulse }
                    )
                }
            }

            Text(agent.name, fontFamily = LEDFontFamily, fontSize = 10.sp, color = agent.color, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            Text(agent.catalystName, fontSize = 6.5.sp, color = agent.color.copy(alpha = 0.65f), lineHeight = 9.sp, modifier = Modifier.fillMaxWidth())

            // Bond level bar
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(agent.color.copy(alpha = 0.2f), RoundedCornerShape(1.dp))) {
                Box(modifier = Modifier.fillMaxWidth(agent.bondLevel / 100f).fillMaxHeight().background(agent.color, RoundedCornerShape(1.dp)))
            }
            Text("BOND: ${agent.bondLevel}", fontSize = 6.sp, color = agent.color.copy(alpha = 0.5f))

            // Status dot
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                val statusColor = when (agent.status) {
                    AgentStatus.ACTIVE  -> Color(0xFF00FF85)
                    AgentStatus.ON_TASK -> HubPink
                    AgentStatus.FUSED   -> agent.accentColor
                    else                -> Color.Gray
                }
                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(statusColor).graphicsLayer { alpha = pulse })
                Text(agent.status.name, fontSize = 5.5.sp, color = statusColor)
            }
        }
    }
}
