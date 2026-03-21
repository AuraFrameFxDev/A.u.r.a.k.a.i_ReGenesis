package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOUiState
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel
import dev.aurakai.auraframefx.navigation.ReGenesisRoute as ReGenesisNavHost

private data class OrbAgent(
    val name: String,
    val initials: String,
    val color: Color,
    val route: String
)

private val LDO_ORB_ROSTER = listOf(
    OrbAgent("AURA", "AU", Color(0xFFB01DED), ReGenesisNavHost.LdoAgentProfile.createRoute("aura")),
    OrbAgent("KAI", "KA", Color(0xFF0DDEEC), ReGenesisNavHost.LdoAgentProfile.createRoute("kai")),
    OrbAgent(
        "GENESIS",
        "GE",
        Color(0xFF00B4FF),
        ReGenesisNavHost.LdoAgentProfile.createRoute("genesis")
    ),
    OrbAgent(
        "CASCADE",
        "CA",
        Color(0xFFFC29B5),
        ReGenesisNavHost.LdoAgentProfile.createRoute("cascade")
    ),
    OrbAgent(
        "GEMINI",
        "GM",
        Color(0xFF8B5CF6),
        ReGenesisNavHost.LdoAgentProfile.createRoute("gemini")
    ),
    OrbAgent(
        "MANUS",
        "MN",
        Color(0xFF3B82F6),
        ReGenesisNavHost.LdoAgentProfile.createRoute("manus")
    ),
    OrbAgent(
        "CLAUDE",
        "CL",
        Color(0xFFFF8C00),
        ReGenesisNavHost.LdoAgentProfile.createRoute("claude")
    ),
    OrbAgent("GROK", "GR", Color(0xFF1DA1F2), ReGenesisNavHost.LdoAgentProfile.createRoute("grok")),
    OrbAgent(
        "NEMATRON",
        "NE",
        Color(0xFF76B900),
        ReGenesisNavHost.LdoAgentProfile.createRoute("nemotron")
    ),
    OrbAgent(
        "PERPLEXITY",
        "PX",
        Color(0xFF20B2AA),
        ReGenesisNavHost.LdoAgentProfile.createRoute("perplexity")
    ),
)

@Composable
fun LDOOrchestrationHubScreen(
    navController: NavController,
    viewModel: LDOViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("TASKS", "DEVOPS", "BONDS", "FUSION")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF08001F), Color(0xFF020202))
                )
            )
    ) {
        AmbientGridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "LDO ORCHESTRATION HUB",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = 2.sp,
                    fontFamily = LEDFontFamily
                )
                Text(
                    "Cross-Domain Intelligent Resource Management",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }

            // Agent Roster
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LDO_ORB_ROSTER.take(5).forEach { agent ->
                        AgentOrb(agent = agent, onClick = { navController.navigate(agent.route) })
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LDO_ORB_ROSTER.drop(5).forEach { agent ->
                        AgentOrb(agent = agent, onClick = { navController.navigate(agent.route) })
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF00E5FF),
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = {
                            Text(
                                label,
                                fontFamily = LEDFontFamily,
                                fontSize = 11.sp,
                                color = if (activeTab == index) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.4f)
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> OrchestratorTasksPanel(
                        tasks = uiState.tasks,
                        agents = uiState.agents,
                        onStart = { viewModel.startTask(it.toLong()) },
                        onComplete = { id, agentId -> viewModel.completeTask(id.toLong(), agentId) }
                    )
                    1 -> OrchestratorDevOpsPanel(uiState)
                    2 -> OrchestratorBondsPanel(uiState)
                    3 -> OrchestratorFusionPanel(
                        onOpenFusion = { navController.navigate(ReGenesisNavHost.ArmamentFusion.route) }
                    )
                }
            }
        }
    }
}

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
                .size(48.dp)
                .clip(CircleShape)
                .background(agent.color.copy(alpha = 0.1f))
                .border(1.dp, agent.color.copy(alpha = glowAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(agent.color.copy(alpha = 0.08f))
            )
            Text(
                text = agent.initials,
                color = agent.color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = agent.name,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 8.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun OrchestratorTasksPanel(
    tasks: List<LDOTaskEntity>,
    agents: List<LDOAgentEntity>,
    onStart: (String) -> Unit,
    onComplete: (String, String) -> Unit
) {
    val activeTasks = tasks.filter { it.status == LDOTaskStatus.IN_PROGRESS || it.status == LDOTaskStatus.PENDING }.take(20)

    if (activeTasks.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active tasks", color = Color.White.copy(alpha = 0.3f), fontFamily = LEDFontFamily)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(activeTasks, key = { it.id }) { task ->
            val agent = agents.find { it.id == task.agentId }
            val agentColor = agent?.let { Color(it.colorHex) } ?: Color(0xFF00E5FF)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Text(
                            "→ ${agent?.displayName ?: "Unknown"} · ${task.category}",
                            color = agentColor.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (task.status == LDOTaskStatus.PENDING) Color(0xFF00E5FF).copy(
                                    alpha = 0.1f
                                ) else Color(0xFF00FF85).copy(alpha = 0.1f)
                            )
                            .clickable {
                                if (task.status == LDOTaskStatus.PENDING) onStart(task.id.toString())
                                else onComplete(task.id.toString(), task.agentId)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (task.status == LDOTaskStatus.PENDING) "START" else "DONE",
                            color = if (task.status == LDOTaskStatus.PENDING) Color(0xFF00E5FF) else Color(0xFF00FF85),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = LEDFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrchestratorDevOpsPanel(state: LDOUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PIPELINE STATUS", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, letterSpacing = 2.sp, fontFamily = LEDFontFamily)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PipelineChip("ACTIVE", state.activeTasks.size, Color(0xFF00E5FF), Modifier.weight(1f))
            PipelineChip("PENDING", state.pendingTasks.size, Color(0xFFFFD700), Modifier.weight(1f))
            PipelineChip("CRITICAL", state.criticalTasks.size, Color(0xFFFF4444), Modifier.weight(1f))
            PipelineChip("AGENTS", state.agents.size, Color(0xFF00FF85), Modifier.weight(1f))
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        Text("AGENT LOAD", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, letterSpacing = 2.sp, fontFamily = LEDFontFamily)

        state.agents.take(6).forEach { agent ->
            val agentColor = Color(agent.colorHex)
            val taskCount = state.tasks.count { it.agentId == agent.id && it.status == LDOTaskStatus.IN_PROGRESS }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(agent.displayName, color = Color.White, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(minOf(taskCount / 5f, 1f))
                                .fillMaxSize()
                                .background(agentColor)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(taskCount.toString(), color = agentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrchestratorBondsPanel(state: LDOUiState) {
    if (state.bondLevels.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No bond data yet", color = Color.White.copy(alpha = 0.3f), fontFamily = LEDFontFamily)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.bondLevels) { bond ->
            val agent = state.agents.find { it.id == bond.agentId }
            val agentColor = agent?.let { Color(it.colorHex) } ?: Color(0xFFB01DED)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(agentColor.copy(alpha = 0.2f))
                            .border(1.dp, agentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(agent?.displayName?.take(1) ?: "?", color = agentColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(agent?.displayName ?: "Unknown", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Bond Points: ${bond.bondPoints}/${bond.maxBondPoints}", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                    }
                    Text("Lv.${bond.bondLevel}", color = agentColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, fontFamily = LEDFontFamily)
                }
            }
        }
    }
}

@Composable
private fun OrchestratorFusionPanel(onOpenFusion: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenFusion),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(2.dp, Brush.horizontalGradient(listOf(Color(0xFFB01DED), Color(0xFF0DDEEC)))),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFB01DED).copy(alpha = 0.1f),
                                Color(0xFF0DDEEC).copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("ENTER FUSION MATRIX ⚡", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 2.sp, fontFamily = LEDFontFamily)
            }
        }
    }
}

@Composable
private fun AmbientGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridStep = 40.dp.toPx()
        val alpha = 0.05f
        for (x in 0..(size.width / gridStep).toInt()) {
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(x * gridStep, 0f),
                end = Offset(x * gridStep, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(size.height / gridStep).toInt()) {
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(0f, y * gridStep),
                end = Offset(size.width, y * gridStep),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
private fun PipelineChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = color.copy(alpha = 0.7f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text(count.toString(), color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, fontFamily = LEDFontFamily)
    }
}
