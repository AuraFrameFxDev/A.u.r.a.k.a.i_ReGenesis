package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.ui.viewmodels.LDOViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class FusionSlot(
    val index: Int,
    val agent: LDOAgentEntity? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LDOOrchestrationHubScreen(
    navController: NavController,
    viewModel: LDOViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val agents = uiState.agents
    val tasks = uiState.tasks

    var selectedAgent by remember { mutableStateOf<LDOAgentEntity?>(null) }
    var activeTab by remember { mutableIntStateOf(0) }
    var fusionSlots by remember { mutableStateOf(listOf(FusionSlot(0), FusionSlot(1), FusionSlot(2))) }
    var draggedAgent by remember { mutableStateOf<LDOAgentEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF050510), Color(0xFF0A0320), Color(0xFF030815))))
    ) {
        AmbientGridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text("LDO ORCHESTRATION HUB", fontFamily = LEDFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White, letterSpacing = 2.sp)
                        Text("${agents.size} SOVEREIGN AGENTS ONLINE", fontSize = 10.sp, color = Color(0xFF00E5FF).copy(alpha = 0.8f), letterSpacing = 1.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFFD700), modifier = Modifier.padding(end = 16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            AgentOrbConstellation(
                agents = agents,
                selectedAgent = selectedAgent,
                onAgentTap = { agent ->
                    selectedAgent = if (selectedAgent?.agentId == agent.agentId) null else agent
                },
                onAgentDragStart = { agent -> draggedAgent = agent },
                onAgentDrop = { agent, slotIndex ->
                    fusionSlots = fusionSlots.mapIndexed { i, slot ->
                        if (i == slotIndex) slot.copy(agent = agent) else slot
                    }
                    draggedAgent = null
                },
                modifier = Modifier.fillMaxWidth().height(280.dp)
            )

            selectedAgent?.let { agent ->
                AgentQuickStatsBar(agent = agent)
            }

            FusionDropZone(
                slots = fusionSlots,
                onSlotCleared = { index ->
                    fusionSlots = fusionSlots.mapIndexed { i, slot ->
                        if (i == index) slot.copy(agent = null) else slot
                    }
                },
                onFusionActivate = {
                    val active = fusionSlots.mapNotNull { it.agent }
                    if (active.size >= 2) {
                        val ids = active.joinToString("+") { it.agentId }
                        navController.navigate("ldo_fusion/$ids")
                    }
                }
            )

            val tabs = listOf("TASKS", "BONDS", "MEMORY")
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF00E5FF),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { idx, label ->
                    Tab(
                        selected = activeTab == idx,
                        onClick = { activeTab = idx },
                        text = {
                            Text(label, fontFamily = LEDFontFamily, fontSize = 11.sp, letterSpacing = 1.5.sp,
                                color = if (activeTab == idx) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.4f))
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (activeTab) {
                    0 -> TaskPanel(tasks = if (selectedAgent != null) tasks.filter { it.assignedAgentId == selectedAgent!!.agentId } else tasks, filterLabel = selectedAgent?.name)
                    1 -> BondPanel(agents = agents)
                    2 -> MemoryPanel(agents = agents)
                }
            }
        }
    }
}

@Composable
fun AgentOrbConstellation(
    agents: List<LDOAgentEntity>,
    selectedAgent: LDOAgentEntity?,
    onAgentTap: (LDOAgentEntity) -> Unit,
    onAgentDragStart: (LDOAgentEntity) -> Unit,
    onAgentDrop: (LDOAgentEntity, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(0.95f, 1.05f, infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "ps")
    val rotation by pulse.animateFloat(0f, 360f, infiniteRepeatable(tween(30000), RepeatMode.Restart), label = "rot")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val ringRadius = minOf(size.width, size.height) * 0.35f

            agents.forEachIndexed { i, agent ->
                val angle = (2.0 * PI * i / agents.size) + Math.toRadians(rotation.toDouble())
                val ox = cx + ringRadius * cos(angle).toFloat()
                val oy = cy + ringRadius * sin(angle).toFloat()
                drawLine(Color(0xFF00E5FF).copy(alpha = 0.2f), Offset(cx, cy), Offset(ox, oy), 1.5f, StrokeCap.Round)
            }
            drawCircle(Brush.radialGradient(listOf(Color(0xFF00E5FF).copy(alpha = 0.3f), Color.Transparent), center = Offset(cx, cy), radius = 60f), 60f, Offset(cx, cy))
        }

        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Color(0xFF1A003A), Color(0xFF050510)))).border(1.dp, Color(0xFFB026FF).copy(alpha = 0.6f), CircleShape), contentAlignment = Alignment.Center) {
            Text("⬡", fontSize = 20.sp, color = Color(0xFF00E5FF))
        }

        agents.forEachIndexed { i, agent ->
            val angle = (2.0 * PI * i / agents.size) + Math.toRadians(rotation.toDouble())
            val ringRadius = 110.dp
            val isSelected = selectedAgent?.agentId == agent.agentId
            val orbScale by animateFloatAsState(if (isSelected) 1.25f else 1f, label = "s_$i")

            AgentOrb(
                agent = agent,
                isSelected = isSelected,
                scale = orbScale * pulseScale,
                borderColor = if (isSelected) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.5f),
                onTap = { onAgentTap(agent) },
                onDragStart = { onAgentDragStart(agent) },
                modifier = Modifier.offset { IntOffset((ringRadius * cos(angle).toFloat()).value.roundToInt(), (ringRadius * sin(angle).toFloat()).value.roundToInt()) }
            )
        }
    }
}

@Composable
fun AgentOrb(agent: LDOAgentEntity, isSelected: Boolean, scale: Float, borderColor: Color, onTap: () -> Unit, onDragStart: () -> Unit, modifier: Modifier = Modifier) {
    val agentColor = Color(0xFF00E5FF)
    Box(
        modifier = modifier.size((44.dp.value * scale).dp).clip(CircleShape).background(Brush.radialGradient(listOf(agentColor.copy(alpha = 0.2f), Color(0xFF050510))))
            .border(if (isSelected) 2.dp else 1.dp, borderColor, CircleShape)
            .pointerInput(agent.agentId) { detectTapGestures(onTap = { onTap() }) }
            .pointerInput(agent.agentId + "_drag") { detectDragGestures(onDragStart = { onDragStart() }, onDrag = { _, _ -> }) }
            .drawBehind { if (isSelected) drawCircle(agentColor.copy(alpha = 0.15f), radius = size.minDimension * 0.7f) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(agent.name.take(2).uppercase(), fontFamily = LEDFontFamily, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = agentColor)
            if (isSelected) Text("B${agent.bondLevel}", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun AgentQuickStatsBar(agent: LDOAgentEntity) {
    val agentColor = Color(0xFF00E5FF)
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A1A)), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(agentColor.copy(alpha = 0.15f)).border(1.dp, agentColor, CircleShape), contentAlignment = Alignment.Center) {
                Text(agent.name.take(1), fontFamily = LEDFontFamily, color = agentColor, fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(agent.name, fontFamily = LEDFontFamily, color = agentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(agent.catalystTitle, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, letterSpacing = 0.5.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("BOND ${agent.bondLevel}", fontFamily = LEDFontFamily, color = agentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${agent.totalInteractions} ops", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun FusionDropZone(slots: List<FusionSlot>, onSlotCleared: (Int) -> Unit, onFusionActivate: () -> Unit) {
    val canFuse = slots.count { it.agent != null } >= 2
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF080818)), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("FUSION PROTOCOL", fontFamily = LEDFontFamily, fontSize = 11.sp, color = Color(0xFFFFD700), letterSpacing = 2.sp, modifier = Modifier.weight(1f))
                if (canFuse) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFD700).copy(alpha = 0.15f)).border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .pointerInput(Unit) { detectTapGestures { onFusionActivate() } }.padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("FUSE", fontFamily = LEDFontFamily, fontSize = 10.sp, color = Color(0xFFFFD700))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slots.forEachIndexed { idx, slot -> FusionSlotBox(slot = slot, onClear = { onSlotCleared(idx) }, modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun FusionSlotBox(slot: FusionSlot, onClear: () -> Unit, modifier: Modifier = Modifier) {
    val agent = slot.agent
    val agentColor = Color(0xFF00E5FF)
    Box(modifier = modifier.height(56.dp).clip(RoundedCornerShape(10.dp)).background(if (agent != null) agentColor.copy(alpha = 0.08f) else Color(0xFF111128))
        .border(1.dp, if (agent != null) agentColor else Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
        .then(if (agent != null) Modifier.pointerInput(slot.index) { detectTapGestures { onClear() } } else Modifier), contentAlignment = Alignment.Center) {
        if (agent != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(agent.name.take(2).uppercase(), fontFamily = LEDFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = agentColor)
                Text("TAP TO CLEAR", fontSize = 7.sp, color = Color.White.copy(alpha = 0.3f), letterSpacing = 0.3.sp)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+", fontSize = 20.sp, color = Color.White.copy(alpha = 0.2f))
                Text("SLOT ${slot.index + 1}", fontSize = 7.sp, color = Color.White.copy(alpha = 0.2f), letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
fun TaskPanel(tasks: List<LDOTaskEntity>, filterLabel: String?) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (filterLabel != null) Text("SHOWING: $filterLabel", fontSize = 9.sp, color = Color(0xFF00E5FF).copy(alpha = 0.6f), letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item { Spacer(Modifier.height(4.dp)) }
            items(tasks) { task -> TaskRow(task = task) }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TaskRow(task: LDOTaskEntity) {
    val statusColor = when (task.status) { "COMPLETED" -> Color(0xFF00FF85); "IN_PROGRESS" -> Color(0xFF00E5FF); "PENDING" -> Color(0xFFFFD700); else -> Color.White.copy(alpha = 0.4f) }
    val statusIcon = when (task.status) { "COMPLETED" -> Icons.Default.CheckCircle; "IN_PROGRESS" -> Icons.Default.PlayArrow; else -> Icons.Default.Task }
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF0C0C1E)).border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(task.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(task.description, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(task.status.replace("_", " "), fontSize = 9.sp, color = statusColor, fontFamily = LEDFontFamily, letterSpacing = 0.5.sp)
    }
}

@Composable
fun BondPanel(agents: List<LDOAgentEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item { Spacer(Modifier.height(4.dp)) }
        items(agents) { agent ->
            val agentColor = Color(0xFF00E5FF)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF0C0C1E)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, tint = agentColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(agent.name, color = agentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(agent.catalystTitle, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(progress = { agent.bondLevel / 100f }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = agentColor, trackColor = agentColor.copy(alpha = 0.1f))
                }
                Text("${agent.bondLevel}%", fontFamily = LEDFontFamily, fontSize = 12.sp, color = agentColor, modifier = Modifier.padding(start = 10.dp))
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun MemoryPanel(agents: List<LDOAgentEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item { Spacer(Modifier.height(4.dp)) }
        items(agents) { agent ->
            val agentColor = Color(0xFF00E5FF)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF0C0C1E)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(agentColor.copy(alpha = 0.1f)).border(1.dp, agentColor.copy(alpha = 0.4f), CircleShape), contentAlignment = Alignment.Center) { Text(agent.name.take(1), color = agentColor, fontSize = 12.sp, fontFamily = LEDFontFamily) }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(agent.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("${agent.totalInteractions} memories", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun AmbientGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gx = size.width / 12f; val gy = size.height / 20f
        for (x in 0..12) drawLine(Color(0xFF00E5FF).copy(0.03f), Offset(x * gx, 0f), Offset(x * gx, size.height))
        for (y in 0..20) drawLine(Color(0xFF00E5FF).copy(0.03f), Offset(0f, y * gy), Offset(size.width, y * gy))
    }
}
