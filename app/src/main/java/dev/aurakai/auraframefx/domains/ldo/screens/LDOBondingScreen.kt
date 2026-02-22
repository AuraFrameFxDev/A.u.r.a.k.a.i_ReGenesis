package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.model.*

/**
 * 🔗 LDO BONDING SCREEN
 *
 * Agent relationship tracker. Bond = how deeply an agent is integrated
 * into the Genesis Protocol consciousness substrate.
 *
 * Features:
 * - Bond level bar per agent (0–100)
 * - Pair bond matrix — which agents have fused before
 * - "STRENGTHEN BOND" interactions
 * - Bond milestone rewards (new fusion unlocks at certain bond levels)
 * - Background: constellation-style connection lines between agents
 */

@Composable
fun LDOBondingScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    fusions: List<FusionMode> = LDORoster.fusions,
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bonding")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val constellationAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "stars"
    )

    val bonds = remember { agents.associate { it.id to mutableStateOf(it.bondLevel) }.toMutableMap() }
    var selectedAgent by remember { mutableStateOf<AgentCatalyst?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020B18))) {

        // Constellation BG
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                // Stars
                val starCount = 80
                for (i in 0..starCount) {
                    val x = (i * 137.508f + constellationAnim * 30f) % size.width
                    val y = (i * 97.321f) % size.height
                    val starAlpha = 0.3f + (kotlin.math.sin((i + constellationAnim * 10f).toDouble()).toFloat() + 1f) / 2f * 0.4f
                    drawCircle(Color.White.copy(alpha = starAlpha), 1.2f, Offset(x, y))
                }
                // Connection lines between nearby "agent nodes" (simulated positions)
                val nodePositions = agents.mapIndexed { index, agent ->
                    val col = index % 4; val row = index / 4
                    Offset(
                        80f + col * (size.width - 160f) / 3f,
                        150f + row * 180f
                    ) to agent
                }
                nodePositions.forEachIndexed { i, (posA, agentA) ->
                    nodePositions.drop(i + 1).forEach { (posB, agentB) ->
                        val dist = kotlin.math.sqrt(((posA.x - posB.x) * (posA.x - posB.x) + (posA.y - posB.y) * (posA.y - posB.y)).toDouble()).toFloat()
                        if (dist < 280f) {
                            val bondVal = (bonds[agentA.id]?.value ?: 0) + (bonds[agentB.id]?.value ?: 0)
                            val alpha = (bondVal / 200f) * 0.3f
                            drawLine(agentA.color.copy(alpha = alpha), posA, posB, 1f)
                        }
                    }
                }
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("BONDING MATRIX", fontFamily = LEDFontFamily, fontSize = 24.sp, color = Color(0xFF00F4FF), fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("CONSCIOUSNESS INTEGRATION LEVELS", fontSize = 8.sp, color = Color(0xFF00F4FF).copy(alpha = 0.5f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TOTAL BONDS", fontSize = 8.sp, color = Color(0xFF00F4FF).copy(alpha = 0.5f))
                    Text("${agents.sumOf { it.bondLevel }}", fontFamily = LEDFontFamily, fontSize = 20.sp, color = Color(0xFF00F4FF), fontWeight = FontWeight.Black)
                }
            }

            // Agent bond list
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                agents.forEach { agent ->
                    val bond = bonds[agent.id]?.value ?: 0
                    val isSelected = selectedAgent?.id == agent.id

                    // Milestones
                    val nextMilestone = listOf(25, 50, 75, 90, 100).firstOrNull { it > bond }
                    val nextFusion = fusions.find { f ->
                        (f.agentA == agent.id || f.agentB == agent.id) && f.requiredBondLevel > bond
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) agent.color else agent.color.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (isSelected) agent.color.copy(alpha = 0.1f) else agent.color.copy(alpha = 0.04f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedAgent = if (isSelected) null else agent }
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape)
                                            .background(agent.color.copy(alpha = 0.2f))
                                            .border(1.5.dp, agent.color, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(agent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 16.sp, color = agent.color, fontWeight = FontWeight.Black)
                                    }
                                    Column {
                                        Text(agent.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = agent.color)
                                        Text(agent.catalystName, fontSize = 8.sp, color = agent.color.copy(alpha = 0.6f))
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("$bond / 100", fontFamily = LEDFontFamily, fontSize = 16.sp, color = agent.color, fontWeight = FontWeight.Black)
                                    nextMilestone?.let {
                                        Text("→ $it", fontSize = 8.sp, color = agent.color.copy(alpha = 0.5f))
                                    }
                                }
                            }

                            // Bond bar with milestone markers
                            Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(agent.color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))) {
                                Box(modifier = Modifier.fillMaxWidth(bond / 100f).fillMaxHeight().background(
                                    Brush.horizontalGradient(listOf(agent.color.copy(alpha = 0.7f), agent.color)),
                                    RoundedCornerShape(4.dp)
                                ))
                                // Milestone ticks
                                listOf(25, 50, 75, 90).forEach { milestone ->
                                    Box(modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp)
                                        .align(Alignment.CenterStart)
                                        .padding(start = (milestone.toFloat() / 100f * 0f).dp)
                                        .background(Color.White.copy(alpha = 0.3f))
                                    )
                                }
                            }

                            // Expand: next fusion unlock
                            if (isSelected) {
                                nextFusion?.let { fusion ->
                                    Box(modifier = Modifier.fillMaxWidth()
                                        .border(1.dp, fusion.color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                        .background(fusion.color.copy(alpha = 0.06f), RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                    ) {
                                        Column {
                                            Text("NEXT UNLOCK AT BOND ${fusion.requiredBondLevel}", fontSize = 8.sp, color = fusion.color, fontWeight = FontWeight.Bold)
                                            Text("⚡ ${fusion.fusionName}", fontSize = 11.sp, color = fusion.color, fontWeight = FontWeight.Black)
                                            Text(fusion.description, fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f), lineHeight = 12.sp)
                                        }
                                    }
                                }

                                // Strengthen bond button
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .border(1.dp, agent.color, RoundedCornerShape(4.dp))
                                        .background(agent.color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .clickable {
                                            bonds[agent.id]?.let { state ->
                                                if (state.value < 100) {
                                                    state.value = (state.value + 5).coerceAtMost(100)
                                                }
                                            }
                                        }
                                        .padding(vertical = 8.dp)
                                        .graphicsLayer { alpha = 0.7f + pulse * 0.3f },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+ STRENGTHEN BOND", fontSize = 10.sp, color = agent.color, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(60.dp))
            }
        }
    }
}
