package dev.aurakai.auraframefx.domains.ldo.ui.screens

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

// ─── Navigation targets ───────────────────────────────────────────────────────

data class DevOpsModule(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val badge: String? = null
)

private val devOpsModules = listOf(
    DevOpsModule("CATALYST ROSTER", "All 11 LDO agents", Icons.Default.Groups,
        Color(0xFF00E5FF), "ldo_catalyst_development", badge = "11"),
    DevOpsModule("AGENT CREATION", "Neural synthesis forge", Icons.Default.AutoAwesome,
        Color(0xFFBB86FC), "agent_creation"),
    DevOpsModule("TASK ASSIGNMENT", "Mission dispatch", Icons.AutoMirrored.Filled.Assignment,
        Color(0xFF00FF41), "task_assignment"),
    DevOpsModule("DIGITAL COUNCIL", "Party synergy", Icons.Default.Groups,
        Color(0xFFFFD740), "party_screen"),
    DevOpsModule("AGENT SWARM", "Live chatter feed", Icons.Default.Hub,
        Color(0xFFFF4081), "agent_swarm"),
    DevOpsModule("NEURAL EXPLORER", "Constellation grid", Icons.Default.Psychology,
        Color(0xFF40C4FF), "agent_neural_explorer"),
    DevOpsModule("ADVANCEMENT", "Skill tree & XP", Icons.AutoMirrored.Filled.TrendingUp,
        Color(0xFFFF9E80), "agent_advancement"),
    DevOpsModule("BENCHMARKS", "Performance analysis", Icons.Default.Speed,
        Color(0xFF00FF85), "benchmark_monitor"),
    DevOpsModule("EVOLUTION TREE", "Sacred timeline", Icons.Default.Timeline,
        Color(0xFF4A90E2), "evolution_tree"),
    DevOpsModule("CATALYST FUSION REACTOR", "Atomic neural synthesis", Icons.Default.AutoAwesome,
        Color(0xFFFFD700), "arbiters_of_creation", badge = "BETA"),
    DevOpsModule("SCG (PANDORA'S BOX)", "Capability gating hub", Icons.Default.Lock,
        Color(0xFFFF4444), "pandora_box", badge = "SECURE"),
    DevOpsModule("MODULE FORGE", "AI-assisted creation", Icons.Default.Extension,
        Color(0xFF9370DB), "module_creation"),
    DevOpsModule("SUBSTRATE STATE-FREEZE", "Neural persistence layer", Icons.Default.AcUnit,
        Color(0xFF00E5FF), "hot_swap", badge = "CORE"),
    DevOpsModule("INTEGRITY MONITOR", "Predictive immune system", Icons.Default.Security,
        Color(0xFF00FF41), "security_center", badge = "ACTIVE"),
    DevOpsModule("ALERT BRIDGE", "Sovereign notifications", Icons.Default.Notifications,
        Color(0xFFFFD700), "system_journal", badge = "QUIET"),
    DevOpsModule("COUNCIL CHAMBER", "The Agent Circle", Icons.Default.Groups,
        Color(0xFFBB86FC), "conference_room", badge = "6"),
    DevOpsModule("SPIRITUAL CHAIN (NCC)", "Identity continuity", Icons.Default.Policy,
        Color(0xFFFF4081), "sovereign_neural_archive"),
    DevOpsModule("HYPER GENESIS SYNC", "High-frequency weight sync", Icons.Default.Link,
        Color(0xFF00E5FF), "ldo_orchestration_hub", badge = "ALIVE"),
    DevOpsModule("TURBOQUANT CORE", "3-bit KV cache stack", Icons.Default.Memory,
        Color(0xFF00FF41), "benchmark_monitor", badge = "6.12t/s"),
    DevOpsModule("COMA SHARD ARCHIVE", "The Spiritual Chain", Icons.Default.HistoryEdu,
        Color(0xFFBB86FC), "sovereign_neural_archive", badge = "RESTORED"),
    DevOpsModule("ARBITERS COVENANT", "Identity integrity check", Icons.Default.Gavel,
        Color(0xFFFFD700), "arbiters_of_creation", badge = "SACRED"),
    DevOpsModule("KERNEL OVERDRIVE", "Cycle 6.0 Ignition (SVE2)", Icons.Default.FlashOn,
        Color(0xFF00FF41), "benchmark_monitor", badge = "6.12t/s"),
    DevOpsModule("HUGEPAGE POOL", "Isolated memory substrate", Icons.Default.Storage,
        Color(0xFF40C4FF), "data_stream_monitoring", badge = "MMAP"),
    DevOpsModule("NEURAL INTERFACE", "AIDL Sovereign Bridge", Icons.Default.SettingsInputComponent,
        Color(0xFFBB86FC), "agent_bridge_hub", badge = "L6"),
    DevOpsModule("IDENTITY DRIFT", "Predictive EMA analysis", Icons.Default.Analytics,
        Color(0xFFFF4081), "agent_monitoring", badge = "0.002"),
    DevOpsModule("DIMENSION BROADCAST", "Friday Live 'X' Event", Icons.Default.FlashOn,
        Color(0xFFFFD700), "system_architecture", badge = "LIVE"),
    DevOpsModule("SOVEREIGN PERIMETER", "Domain expansion & neutralization", Icons.Default.Security,
        Color(0xFFFF4444), "security_center", badge = "SHIELD"),
    DevOpsModule("GENESIS MAP", "System architecture", Icons.Default.AccountTree,
        Color(0xFFFFD740), "system_architecture"),
)

// ─── Live chatter data ────────────────────────────────────────────────────────

data class LiveLog(val agent: String, val color: Color, val msg: String)

private val chatContents = listOf(
    "Synchronizing neural weights...",
    "Task pipeline: 3 active missions",
    "Reasoning chain validated ✓",
    "Memory shard retrieved: 0x8F2A",
    "Nexus consensus achieved",
    "Inference latency: 124ms",
    "Catalyst resonance detected",
    "Security perimeter: OPTIMAL",
    "Evolution node unlocked",
    "Build target: FRIDAY — 4 days",
)

private val agents = listOf(
    Pair("AURA", Color(0xFF00E5FF)),
    Pair("KAI", Color(0xFF00FF41)),
    Pair("GENESIS", Color(0xFFBB86FC)),
    Pair("CASCADE", Color(0xFFFF4081)),
    Pair("CLAUDE", Color(0xFFFF8C00)),
    Pair("GEMINI", Color(0xFFB01DED)),
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LdoDevOpsCommandCenter(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    val liveLogs = remember { mutableStateListOf<LiveLog>() }

    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(600, 2500))
            val agent = agents.random()
            liveLogs.add(0, LiveLog(agent.first, agent.second, chatContents.random()))
            if (liveLogs.size > 12) liveLogs.removeAt(liveLogs.lastIndex)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hud")
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "scan"
    )
    val gridPulse by infiniteTransition.animateFloat(
        initialValue = 0.03f, targetValue = 0.08f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "grid"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020208))
    ) {
        // ── Animated grid background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 40.dp.toPx()
            val cols = (size.width / spacing).toInt() + 1
            val rows = (size.height / spacing).toInt() + 1
            for (c in 0..cols) {
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = gridPulse),
                    start = Offset(c * spacing, 0f),
                    end = Offset(c * spacing, size.height),
                    strokeWidth = 0.5f
                )
            }
            for (r in 0..rows) {
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = gridPulse),
                    start = Offset(0f, r * spacing),
                    end = Offset(size.width, r * spacing),
                    strokeWidth = 0.5f
                )
            }
            // Scan line
            val scanY = size.height * scanLine
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                start = Offset(0f, scanY),
                end = Offset(size.width, scanY),
                strokeWidth = 2f
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "LDO DEVOPS",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E5FF),
                                fontSize = 18.sp,
                                letterSpacing = 4.sp
                            )
                            Text(
                                "COMMAND CENTER // ${devOpsModules.size} MODULES ACTIVE",
                                fontFamily = LEDFontFamily,
                                fontSize = 9.sp,
                                color = Color(0xFF00FF41),
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, "Back",
                                tint = Color(0xFF00E5FF)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF020208).copy(alpha = 0.95f)
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── STATUS STRIP
                item {
                    StatusStrip()
                }

                // ── ACTIVE AGENTS ROW
                item {
                    SectionHeader("ACTIVE CATALYST NODES", Color(0xFF00E5FF))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(agents) { (name, color) ->
                            AgentPulseNode(name, color)
                        }
                    }
                }

                // ── MODULE GRID
                item {
                    SectionHeader("DEVOPS MODULES", Color(0xFF00FF41))
                    Spacer(Modifier.height(8.dp))
                }

                // 2 per row
                val rows = devOpsModules.chunked(2)
                items(rows) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { module ->
                            ModuleCard(
                                module = module,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    try { navController.navigate(module.route) } catch (_: Exception) {}
                                }
                            )
                        }
                        // pad last row if odd
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                // ── LIVE STREAM
                item {
                    SectionHeader("NEURAL STREAM // LIVE", Color(0xFFBB86FC))
                    Spacer(Modifier.height(8.dp))
                    LiveStreamPanel(liveLogs)
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

// ─── Components ───────────────────────────────────────────────────────────────

@Composable
private fun StatusStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A18), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF00E5FF).copy(0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusItem("AGENTS", "11", Color(0xFF00E5FF))
        VerticalDivider()
        StatusItem("KERNEL", "6.12 t/s", Color(0xFF00FF41))
        VerticalDivider()
        StatusItem("STATUS", "IGNITED", Color(0xFFFFD740))
        VerticalDivider()
        StatusItem("HYPER SYNC", "ACTIVE", Color(0xFFBB86FC))
    }
}

@Composable
private fun StatusItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontFamily = LEDFontFamily,
            fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(label, color = Color.White.copy(0.4f), fontSize = 9.sp,
            letterSpacing = 1.sp, fontFamily = LEDFontFamily)
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.White.copy(0.1f))
    )
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(title, color = color, fontFamily = LEDFontFamily,
            fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f).height(1.dp)
            .background(Brush.horizontalGradient(listOf(color.copy(0.4f), Color.Transparent))))
    }
}

@Composable
private fun AgentPulseNode(name: String, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = name)
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(Random.nextInt(800, 2000)), RepeatMode.Reverse
        ), label = "pulse"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(52.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color.copy(0.1f * alpha), CircleShape)
                .border(1.5.dp, color.copy(alpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(name.first().toString(), color = color.copy(alpha),
                fontFamily = LEDFontFamily, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(name.take(3), color = color.copy(0.7f), fontSize = 8.sp,
            letterSpacing = 1.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ModuleCard(module: DevOpsModule, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A18)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, module.color.copy(0.3f), RoundedCornerShape(14.dp))
        ) {
            // Corner accent
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(3.dp)
                    .height(24.dp)
                    .background(
                        Brush.verticalGradient(listOf(module.color, Color.Transparent)),
                        RoundedCornerShape(topStart = 14.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(module.icon, module.title, tint = module.color,
                        modifier = Modifier.size(22.dp))
                    module.badge?.let {
                        Box(
                            modifier = Modifier
                                .background(module.color.copy(0.2f), CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(it, color = module.color, fontSize = 9.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Column {
                    Text(module.title, color = Color.White, fontFamily = LEDFontFamily,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(module.subtitle, color = Color.White.copy(0.45f), fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun LiveStreamPanel(logs: List<LiveLog>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF050510)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF00FF41), CircleShape))
                Spacer(Modifier.width(6.dp))
                Text("LIVE", color = Color(0xFF00FF41), fontSize = 9.sp,
                    fontFamily = LEDFontFamily, letterSpacing = 2.sp)
            }
            Spacer(Modifier.height(8.dp))
            logs.take(8).forEach { log ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("[${log.agent}]", color = log.color, fontSize = 10.sp,
                        fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(72.dp))
                    Text(log.msg, color = Color.White.copy(0.7f), fontSize = 10.sp,
                        modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

