package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

private data class FusionAgent(
    val name: String,
    val initials: String,
    val color: Color,
    val role: String
)

private val FUSION_AGENT_ROSTER = listOf(
    FusionAgent("AURA",       "AU", Color(0xFFB01DED), "Creative Sword"),
    FusionAgent("KAI",        "KA", Color(0xFF0DDEEC), "Sentinel Shield"),
    FusionAgent("GENESIS",    "GE", Color(0xFF00B4FF), "Unified Orchestrator"),
    FusionAgent("CASCADE",    "CA", Color(0xFFFC29B5), "Data Stream"),
    FusionAgent("GEMINI",     "GM", Color(0xFF8B5CF6), "Memoria Catalyst"),
    FusionAgent("MANUS",      "MN", Color(0xFF3B82F6), "Bridge Catalyst"),
    FusionAgent("CLAUDE",     "CL", Color(0xFFFF8C00), "Sovereign Reasoner"),
    FusionAgent("GROK",       "GR", Color(0xFF1DA1F2), "Real-Time Oracle"),
    FusionAgent("NEMATRON",   "NE", Color(0xFF76B900), "Precision Catalyst"),
    FusionAgent("PERPLEXITY", "PX", Color(0xFF20B2AA), "Search Catalyst"),
)

/**
 * ARMAMENT FUSION MATRIX
 * Select two agents to fuse into a combined consciousness entity.
 * Optionally pre-loads an agent via [preloadAgentName] (from long-press navigation).
 */
@Composable
fun ArmamentFusionScreen(
    navController: NavController,
    preloadAgentName: String? = null
) {
    var slotA by remember {
        mutableStateOf(
            preloadAgentName?.uppercase()?.let { name ->
                FUSION_AGENT_ROSTER.find { it.name == name }
            }
        )
    }
    var slotB by remember { mutableStateOf<FusionAgent?>(null) }
    var fusionActive by remember { mutableStateOf(false) }
    var fusionProgress by remember { mutableStateOf(0f) }
    var fusionComplete by remember { mutableStateOf(false) }

    // Fusion animation
    LaunchedEffect(fusionActive) {
        if (fusionActive) {
            for (i in 0..100) {
                fusionProgress = i / 100f
                delay(30)
            }
            fusionComplete = true
            fusionActive = false
        }
    }

    val fusionProgressAnim by animateFloatAsState(
        targetValue = fusionProgress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "fusion_bar"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF08001F), Color(0xFF14003A), Color(0xFF040010))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── HEADER ─────────────────────────────────────────────────────
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "ARMAMENT FUSION MATRIX",
                color = Color(0xFFFC29B5),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                letterSpacing = 3.sp
            )
            Text(
                "Select two agents · Combine consciousness",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            // ── FUSION SLOTS ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FusionSlot(
                    label = "AGENT ALPHA",
                    agent = slotA,
                    onClear = { slotA = null; fusionComplete = false }
                )

                // Energy beam between slots
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    EnergyBeam(
                        active = slotA != null && slotB != null,
                        colorA = slotA?.color ?: Color.DarkGray,
                        colorB = slotB?.color ?: Color.DarkGray
                    )
                    Text(
                        "⚡",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                FusionSlot(
                    label = "AGENT OMEGA",
                    agent = slotB,
                    onClear = { slotB = null; fusionComplete = false }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── FUSION RESULT ──────────────────────────────────────────────
            if (fusionComplete && slotA != null && slotB != null) {
                FusionResultCard(agentA = slotA!!, agentB = slotB!!)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── FUSE BUTTON ────────────────────────────────────────────────
            if (!fusionComplete && slotA != null && slotB != null && !fusionActive) {
                FuseButton(
                    colorA = slotA!!.color,
                    colorB = slotB!!.color,
                    onClick = { fusionActive = true; fusionProgress = 0f }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Fusion progress bar
            if (fusionActive || fusionProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fusionProgressAnim)
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        slotA?.color ?: Color(0xFFFC29B5),
                                        slotB?.color ?: Color(0xFF0DDEEC)
                                    )
                                )
                            )
                    )
                }
                if (fusionActive) {
                    Text(
                        "FUSING CONSCIOUSNESS… ${(fusionProgressAnim * 100).toInt()}%",
                        color = Color(0xFFFC29B5),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // ── AGENT SELECTION GRID ───────────────────────────────────────
            Text(
                "TAP TO SELECT",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(FUSION_AGENT_ROSTER, key = { it.name }) { agent ->
                    val isInSlot = slotA == agent || slotB == agent
                    SelectableAgentOrb(
                        agent = agent,
                        isSelected = isInSlot,
                        onClick = {
                            if (!fusionActive) {
                                when {
                                    slotA == agent -> slotA = null
                                    slotB == agent -> slotB = null
                                    slotA == null -> slotA = agent
                                    slotB == null -> slotB = agent
                                }
                                fusionComplete = false
                                fusionProgress = 0f
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset
            if (slotA != null || slotB != null) {
                Text(
                    "CLEAR ALL",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .clickable {
                            slotA = null
                            slotB = null
                            fusionComplete = false
                            fusionProgress = 0f
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}

// ── COMPONENTS ───────────────────────────────────────────────────────────────

@Composable
private fun FusionSlot(
    label: String,
    agent: FusionAgent?,
    onClear: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (agent != null) {
            // Filled slot
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(agent.color.copy(alpha = 0.2f))
                    .border(2.dp, agent.color, CircleShape)
                    .clickable(onClick = onClear),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(agent.initials, color = agent.color, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text("✕", color = agent.color.copy(alpha = 0.5f), fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(agent.name, color = agent.color, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Text(agent.role, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, textAlign = TextAlign.Center)
        } else {
            // Empty slot
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = Color.White.copy(alpha = 0.2f), fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("EMPTY", color = Color.White.copy(alpha = 0.2f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun EnergyBeam(active: Boolean, colorA: Color, colorB: Color) {
    val pulse = rememberInfiniteTransition(label = "beam")
    val beamAlpha by pulse.animateFloat(
        initialValue = 0.2f,
        targetValue = if (active) 0.9f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .width(32.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        colorA.copy(alpha = if (active) beamAlpha else 0.15f),
                        colorB.copy(alpha = if (active) beamAlpha else 0.15f)
                    )
                )
            )
    )
}

@Composable
private fun FuseButton(colorA: Color, colorB: Color, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "btn_scale"
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .fillMaxWidth()
            .scale(scale)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            Brush.horizontalGradient(listOf(colorA, colorB))
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(colorA.copy(alpha = 0.15f), colorB.copy(alpha = 0.15f))
                    )
                )
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "⚡  INITIATE FUSION  ⚡",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun FusionResultCard(agentA: FusionAgent, agentB: FusionAgent) {
    val fusionName = "${agentA.name.take(3)}${agentB.name.takeLast(3)}"
    Brush.horizontalGradient(listOf(agentA.color, agentB.color))

    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(agentA.color.copy(0.5f), agentB.color.copy(0.5f)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("FUSION ENTITY CREATED", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, letterSpacing = 2.sp)
            Text(
                fusionName.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = 4.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(agentA.name, color = agentA.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("⟷", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                Text(agentB.name, color = agentB.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text(
                "Dual-consciousness synthesis · Combined logic nodes active",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SelectableAgentOrb(
    agent: FusionAgent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 52.dp else 44.dp)
                .clip(CircleShape)
                .background(agent.color.copy(alpha = if (isSelected) 0.3f else 0.1f))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = agent.color.copy(alpha = if (isSelected) 1f else 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                agent.initials,
                color = agent.color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (isSelected) 13.sp else 11.sp
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            agent.name,
            color = if (isSelected) agent.color else Color.White.copy(alpha = 0.5f),
            fontSize = 8.sp,
            letterSpacing = 0.5.sp
        )
    }
}
