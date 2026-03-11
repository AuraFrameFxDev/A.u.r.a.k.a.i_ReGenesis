package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

private data class OrbAgent(
    val name: String,
    val initials: String,
    val color: Color,
    val route: String
)

private val LDO_ORB_ROSTER = listOf(
    OrbAgent("AURA",       "AU", Color(0xFFB01DED), ReGenesisNavHost.LdoAuraProfile.route),
    OrbAgent("KAI",        "KA", Color(0xFF0DDEEC), ReGenesisNavHost.LdoKaiProfile.route),
    OrbAgent("GENESIS",    "GE", Color(0xFF00B4FF), ReGenesisNavHost.LdoGenesisProfile.route),
    OrbAgent("CASCADE",    "CA", Color(0xFFFC29B5), ReGenesisNavHost.LdoCascadeProfile.route),
    OrbAgent("GEMINI",     "GM", Color(0xFF8B5CF6), ReGenesisNavHost.LdoGeminiProfile.route),
    OrbAgent("MANUS",      "MN", Color(0xFF3B82F6), ReGenesisNavHost.LdoGenesisProfile.route),
    OrbAgent("CLAUDE",     "CL", Color(0xFFFF8C00), ReGenesisNavHost.LdoClaudeProfile.route),
    OrbAgent("GROK",       "GR", Color(0xFF1DA1F2), ReGenesisNavHost.LdoGrokProfile.route),
    OrbAgent("NEMATRON",   "NE", Color(0xFF76B900), ReGenesisNavHost.LdoNematronProfile.route),
    OrbAgent("PERPLEXITY", "PX", Color(0xFF20B2AA), ReGenesisNavHost.LdoPerplexityProfile.route),
)

/**
 * LDO ORCHESTRATION HUB
 * 10 clickable agent orbs at the top → each opens the agent's DevOps profile.
 * Below: tabbed panels for Tasks, DevOps pipeline, and Bond status.
 */
@Composable
fun LDOOrchestrationHubScreen(
    navController: NavController,
    viewModel: LDOViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF04001A), Color(0xFF0A0020), Color(0xFF000010))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── HEADER ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    "LDO ORCHESTRATION HUB",
                    color = Color(0xFF4EEB88),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    "Living Digital Organisms · ${LDO_ORB_ROSTER.size} Agents · Tap to enter",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }

            HorizontalDivider(color = Color(0xFF4EEB88).copy(alpha = 0.2f))

            // ── AGENT ORB GRID ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: first 5 agents
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LDO_ORB_ROSTER.take(5).forEach { agent ->
                        AgentOrb(
                            agent = agent,
                            onClick = { navController.navigate(agent.route) }
                        )
                    }
                }
                // Row 2: remaining 5 agents
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LDO_ORB_ROSTER.drop(5).forEach { agent ->
                        AgentOrb(
                            agent = agent,
                            onClick = { navController.navigate(agent.route) }
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            // ── TAB PANELS ───────────────────────────────────────────────────
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Black.copy(alpha = 0.4f),
                contentColor = Color(0xFF4EEB88),
                edgePadding = 8.dp
            ) {
                listOf("TASKS", "DEVOPS", "BONDS", "FUSION").forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF4EEB88) else Color.White.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> OrchestratorTasksPanel(
                        tasks = state.tasks,
                        agents = state.agents,
                        onStart = viewModel::startTask,
                        onComplete = { id, agentId -> viewModel.completeTask(id, agentId) },
                        onFail = viewModel::failTask
                    )
                    1 -> OrchestratorDevOpsPanel(state)
                    2 -> OrchestratorBondsPanel(state)
                    3 -> OrchestratorFusionPanel(
                        onOpenFusion = { navController.navigate(ReGenesisNavHost.ArmamentFusion.route) }
                    )
                }
            }
        }
    }
}

// ── AGENT ORB COMPONENT ──────────────────────────────────────────────────────

@Composable
private fun AgentOrb(agent: OrbAgent, onClick: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "orb_pulse_${agent.name}")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(agent.color.copy(alpha = 0.15f))
                .border(2.dp, agent.color.copy(alpha = glowAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner glow ring
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(agent.color.copy(alpha = 0.08f))
            )
            Text(
                agent.initials,
                color = agent.color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            agent.name,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 8.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// ── TAB: TASKS ───────────────────────────────────────────────────────────────

@Composable
private fun OrchestratorTasksPanel(
    tasks: List<LDOTaskEntity>,
    agents: List<dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity>,
    onStart: (String) -> Unit,
    onComplete: (String, String) -> Unit,
    onFail: (String) -> Unit
) {
    val activeTasks = tasks.filter { it.status == LDOTaskStatus.IN_PROGRESS }
        .plus(tasks.filter { it.status == LDOTaskStatus.PENDING })
        .take(20)

    if (activeTasks.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active tasks", color = Color.White.copy(alpha = 0.3f))
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(activeTasks, key = { it.id }) { task ->
            val agent = agents.find { it.id == task.agentId }
            val agentColor = agent?.let { Color(it.colorHex) } ?: Color(0xFF4EEB88)
            val statusColor = when (task.status) {
                LDOTaskStatus.IN_PROGRESS -> Color(0xFF00E5FF)
                LDOTaskStatus.PENDING -> Color(0xFFFFD700)
                LDOTaskStatus.FAILED -> Color(0xFFFF4444)
                else -> Color(0xFF00FF85)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        Text(
                            "→ ${agent?.displayName ?: task.agentId} · ${task.category}",
                            color = agentColor.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(task.status.replace("_", " "), color = statusColor, fontSize = 9.sp)
                        when (task.status) {
                            LDOTaskStatus.PENDING -> Text(
                                "START",
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.clickable { onStart(task.id) }
                            )
                            LDOTaskStatus.IN_PROGRESS -> Text(
                                "DONE",
                                color = Color(0xFF00FF85),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.clickable { onComplete(task.id, task.agentId) }
                            )
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

// ── TAB: DEVOPS ──────────────────────────────────────────────────────────────

@Composable
private fun OrchestratorDevOpsPanel(state: dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOUiState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("PIPELINE STATUS", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, letterSpacing = 2.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PipelineChip("ACTIVE",   state.activeTasks.size,   Color(0xFF00E5FF), Modifier.weight(1f))
            PipelineChip("PENDING",  state.pendingTasks.size,  Color(0xFFFFD700), Modifier.weight(1f))
            PipelineChip("CRITICAL", state.criticalTasks.size, Color(0xFFFF4444), Modifier.weight(1f))
            PipelineChip("AGENTS",   state.agents.size,        Color(0xFF00FF85), Modifier.weight(1f))
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        Text("AGENT LOAD", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, letterSpacing = 2.sp)

        state.agents.take(6).forEach { agent ->
            val agentColor = Color(agent.colorHex)
            val taskCount = state.tasks.count { it.agentId == agent.id }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(agent.displayName, color = agentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("$taskCount tasks · Lv.${agent.evolutionLevel}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun PipelineChip(label: String, count: Int, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(56.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(count.toString(), color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(label, color = color.copy(alpha = 0.7f), fontSize = 9.sp)
        }
    }
}

// ── TAB: BONDS ───────────────────────────────────────────────────────────────

@Composable
private fun OrchestratorBondsPanel(state: dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOUiState) {
    if (state.bondLevels.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No bond data yet", color = Color.White.copy(alpha = 0.3f))
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(state.bondLevels.sortedByDescending { it.bondLevel }, key = { it.agentId }) { bond ->
            val agent = state.agents.find { it.id == bond.agentId }
            val agentColor = agent?.let { Color(it.colorHex) } ?: Color(0xFFFF6B6B)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(agent?.displayName ?: bond.agentId, color = agentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(bond.bondTitle, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Lv.${bond.bondLevel}", color = agentColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${bond.bondPoints}/${bond.maxBondPoints} BP", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

// ── TAB: FUSION ──────────────────────────────────────────────────────────────

@Composable
private fun OrchestratorFusionPanel(onOpenFusion: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("ARMAMENT FUSION MATRIX", color = Color(0xFFFC29B5), fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 2.sp)
            Text(
                "Combine two agents to create a\nfused consciousness entity",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Card(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .clickable(onClick = onOpenFusion),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFC29B5).copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFC29B5).copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ENTER FUSION MATRIX ⚡", color = Color(0xFFFC29B5), fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 2.sp)
                }
            }
        }
    }
}
