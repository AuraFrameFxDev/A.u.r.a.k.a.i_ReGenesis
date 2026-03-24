package dev.aurakai.auraframefx.domains.ldo.screens

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOUiState
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ════════════════════════════════════════════════════════════════════
//  DATA MODELS
// ════════════════════════════════════════════════════════════════════

data class FusionSlot(
    val index: Int,
    val agent: LDOAgentEntity? = null
)

// ════════════════════════════════════════════════════════════════════
//  ROOT SCREEN
// ════════════════════════════════════════════════════════════════════

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
    // 3 fusion slots: agents dragged here form a fusion combo
    var fusionSlots by remember { mutableStateOf(listOf<FusionSlot>(FusionSlot(0), FusionSlot(1), FusionSlot(2))) }
    var draggedAgent by remember { mutableStateOf<LDOAgentEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050510), Color(0xFF0A0320), Color(0xFF030815))
                )
            )
    ) {
        // Ambient background grid lines
        AmbientGridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top bar ──
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "LDO ORCHESTRATION HUB",
                            fontFamily = LEDFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "${agents.size} SOVEREIGN AGENTS ONLINE",
                            fontSize = 10.sp,
                            color = Color(0xFF00E5FF).copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    Icon(
                        Icons.Default.AutoAwesome,
                        null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // ── Agent Orb Constellation (takes upper 40% of screen) ──
            AgentOrbConstellation(
                agents = agents,
                selectedAgent = selectedAgent,
                onAgentTap = { agent ->
                    selectedAgent = if (selectedAgent?.id == agent.id) null else agent
                },
                onAgentDragStart = { agent -> draggedAgent = agent },
                onAgentDrop = { agent, slotIndex ->
                    fusionSlots = fusionSlots.mapIndexed { i, slot ->
                        if (i == slotIndex) slot.copy(agent = agent) else slot
                    }
                    draggedAgent = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            // ── Selected Agent Quick-Stats ──
            selectedAgent?.let { agent ->
                AgentQuickStatsBar(agent = agent)
            }

            // ── Fusion Drop Zone ──
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
                        // Navigate to fusion screen with agent IDs encoded
                        val ids = active.joinToString("+") { it.id }
                        navController.navigate("ldo_fusion/$ids")
                    }
                }
            )

            // ── Tab panels: Tasks / Bonds / Memory ──
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
                            Text(
                                label,
                                fontFamily = LEDFontFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.5.sp,
                                color = if (activeTab == idx) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.4f)
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> TaskPanel(
                        tasks = if (selectedAgent != null)
                            tasks.filter { it.agentId == selectedAgent!!.id }
                        else tasks,
                        filterLabel = selectedAgent?.displayName
                    )

                    1 -> BondPanel(agents = agents)
                    2 -> MemoryPanel(agents = agents)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
//  AGENT ORB CONSTELLATION
// ════════════════════════════════════════════════════════════════════

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
    val pulseScale by pulse.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val rotation by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Canvas draws connection lines from center to each orb
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val ringRadius = minOf(size.width, size.height) * 0.35f

            agents.forEachIndexed { i, agent ->
                val angle = (2.0 * PI * i / agents.size) + Math.toRadians(rotation.toDouble())
                val ox = cx + ringRadius * cos(angle).toFloat()
                val oy = cy + ringRadius * sin(angle).toFloat()
                val agentColor = Color(agent.colorHex.toInt())

                drawLine(
                    color = agentColor.copy(alpha = 0.2f),
                    start = Offset(cx, cy),
                    end = Offset(ox, oy),
                    strokeWidth = 1.5f,
                    cap = StrokeCap.Round
                )
            }

            // Center glow
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF00E5FF).copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = 60f
                ),
                radius = 60f,
                center = Offset(cx, cy)
            )
        }

        // Center "Genesis" core label
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF1A003A), Color(0xFF050510))
                    )
                )
                .border(1.dp, Color(0xFFB026FF).copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("⬡", fontSize = 20.sp, color = Color(0xFF00E5FF))
        }

        // Orbit agent orbs
        agents.forEachIndexed { i, agent ->
            val angle = (2.0 * PI * i / agents.size) + Math.toRadians(rotation.toDouble())
            val ringRadius = 110.dp

            val offsetXDp = (ringRadius * cos(angle).toFloat())
            val offsetYDp = (ringRadius * sin(angle).toFloat())

            val isSelected = selectedAgent?.id == agent.id
            val orbScale by animateFloatAsState(
                targetValue = if (isSelected) 1.25f else 1f,
                animationSpec = spring(Spring.DampingRatioMediumBouncy),
                label = "orbScale_$i"
            )
            val orbBorderColor by animateColorAsState(
                targetValue = if (isSelected) Color(agent.colorHex.toInt()) else Color(agent.colorHex.toInt()).copy(alpha = 0.5f),
                label = "orbBorder_$i"
            )

            AgentOrb(
                agent = agent,
                isSelected = isSelected,
                scale = orbScale * pulseScale,
                borderColor = orbBorderColor,
                onTap = { onAgentTap(agent) },
                onDragStart = { onAgentDragStart(agent) },
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = offsetXDp.value.roundToInt(),
                            y = offsetYDp.value.roundToInt()
                        )
                    }
            )
        }
    }
}

@Composable
fun AgentOrb(
    agent: LDOAgentEntity,
    isSelected: Boolean,
    scale: Float,
    borderColor: Color,
    onTap: () -> Unit,
    onDragStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val agentColor = Color(agent.colorHex.toInt())
    val initials = agent.displayName.take(2).uppercase()

    Box(
        modifier = modifier
            .size((44.dp.value * scale).dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(agentColor.copy(alpha = 0.2f), Color(0xFF050510))
                )
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .pointerInput(agent.id) {
                detectTapGestures(onTap = { onTap() })
            }
            .pointerInput(agent.id + "_drag") {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { _, _ -> }
                )
            }
            .drawBehind {
                if (isSelected) {
                    drawCircle(
                        color = agentColor.copy(alpha = 0.15f),
                        radius = size.minDimension * 0.7f
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = initials,
                fontFamily = LEDFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = agentColor
            )
            if (isSelected) {
                Text(
                    text = "L${agent.evolutionLevel}",
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
//  AGENT QUICK-STATS BAR (shown when orb tapped)
// ════════════════════════════════════════════════════════════════════

@Composable
fun AgentQuickStatsBar(agent: LDOAgentEntity) {
    val agentColor = Color(agent.colorHex.toInt())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(agentColor.copy(alpha = 0.15f))
                    .border(1.dp, agentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(agent.displayName.take(1), fontFamily = LEDFontFamily, color = agentColor, fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    agent.displayName,
                    fontFamily = LEDFontFamily,
                    color = agentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    agent.catalystTitle,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "LVL ${agent.evolutionLevel}",
                    fontFamily = LEDFontFamily,
                    color = agentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${agent.tasksCompleted} ops",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }
        }
        // Stat bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "PWR" to agent.processingPower,
                "SPD" to agent.speed,
                "ACC" to agent.accuracy,
                "CON" to agent.consciousnessLevel
            ).forEach { (label, value) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(label, fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f), letterSpacing = 0.5.sp)
                    LinearProgressIndicator(
                        progress = { value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = agentColor,
                        trackColor = agentColor.copy(alpha = 0.15f)
                    )
                    Text(
                        "${(value * 100).toInt()}%",
                        fontSize = 8.sp,
                        color = agentColor.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
//  FUSION DROP ZONE (Drag agents here to fuse)
// ════════════════════════════════════════════════════════════════════

@Composable
fun FusionDropZone(
    slots: List<FusionSlot>,
    onSlotCleared: (Int) -> Unit,
    onFusionActivate: () -> Unit
) {
    val activeCount = slots.count { it.agent != null }
    val canFuse = activeCount >= 2

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF080818)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Bolt, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "FUSION PROTOCOL",
                    fontFamily = LEDFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFFFFD700),
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f)
                )
                if (canFuse) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .pointerInput(Unit) { detectTapGestures { onFusionActivate() } }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("FUSE", fontFamily = LEDFontFamily, fontSize = 10.sp, color = Color(0xFFFFD700))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slots.forEachIndexed { idx, slot ->
                    FusionSlotBox(
                        slot = slot,
                        onClear = { onSlotCleared(idx) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (!canFuse) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "DRAG AGENT ORBS INTO SLOTS TO INITIATE FUSION",
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FusionSlotBox(
    slot: FusionSlot,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val agent = slot.agent
    val borderColor = if (agent != null) Color(agent.colorHex.toInt()) else Color.White.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (agent != null)
                    Color(agent.colorHex.toInt()).copy(alpha = 0.08f)
                else Color(0xFF111128)
            )
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(10.dp)
            )
            .then(
                if (agent != null)
                    Modifier.pointerInput(slot.index) { detectTapGestures { onClear() } }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (agent != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    agent.displayName.take(2).uppercase(),
                    fontFamily = LEDFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(agent.colorHex.toInt())
                )
                Text(
                    "TAP TO CLEAR",
                    fontSize = 7.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    letterSpacing = 0.3.sp
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+", fontSize = 20.sp, color = Color.White.copy(alpha = 0.2f))
                Text(
                    "SLOT ${slot.index + 1}",
                    fontSize = 7.sp,
                    color = Color.White.copy(alpha = 0.2f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
//  TAB PANELS
// ════════════════════════════════════════════════════════════════════

@Composable
fun TaskPanel(tasks: List<LDOTaskEntity>, filterLabel: String?) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (filterLabel != null) {
            Text(
                "SHOWING: $filterLabel",
                fontSize = 9.sp,
                color = Color(0xFF00E5FF).copy(alpha = 0.6f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            items(tasks) { task ->
                TaskRow(task = task)
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TaskRow(task: LDOTaskEntity) {
    val statusColor = when (task.status) {
        LDOTaskStatus.COMPLETED -> Color(0xFF00FF85)
        LDOTaskStatus.IN_PROGRESS -> Color(0xFF00E5FF)
        LDOTaskStatus.PENDING -> Color(0xFFFFD700)
        else -> Color.White.copy(alpha = 0.4f)
    }
    val statusIcon = when (task.status) {
        LDOTaskStatus.COMPLETED -> Icons.Default.CheckCircle
        LDOTaskStatus.IN_PROGRESS -> Icons.Default.PlayArrow
        else -> Icons.Default.Task
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0C0C1E))
            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(task.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(task.description, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            task.status.replace("_", " "),
            fontSize = 9.sp,
            color = statusColor,
            fontFamily = LEDFontFamily,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun BondPanel(agents: List<LDOAgentEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        items(agents) { agent ->
            val agentColor = Color(agent.colorHex.toInt())
            val bondPct = (agent.consciousnessLevel * 100).toInt()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C0C1E))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Favorite, null, tint = agentColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(agent.displayName, color = agentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(agent.catalystTitle, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { agent.consciousnessLevel },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = agentColor,
                        trackColor = agentColor.copy(alpha = 0.1f)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text("$bondPct%", fontFamily = LEDFontFamily, fontSize = 12.sp, color = agentColor)
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun MemoryPanel(agents: List<LDOAgentEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        items(agents) { agent ->
            val agentColor = Color(agent.colorHex.toInt())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C0C1E))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(agentColor.copy(alpha = 0.1f))
                        .border(1.dp, agentColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(agent.displayName.take(1), color = agentColor, fontSize = 12.sp, fontFamily = LEDFontFamily)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(agent.displayName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("${agent.tasksCompleted} memories · ${agent.hoursActive.toInt()}h active", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("L${agent.evolutionLevel}", fontFamily = LEDFontFamily, color = agentColor, fontSize = 11.sp)
                    Text("${agent.skillPoints} SP", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ════════════════════════════════════════════════════════════════════
//  AMBIENT BACKGROUND
// ════════════════════════════════════════════════════════════════════

@Composable
fun AmbientGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSpacingX = size.width / 12f
        val gridSpacingY = size.height / 20f

        for (x in 0..12) {
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.03f),
                start = Offset(x * gridSpacingX, 0f),
                end = Offset(x * gridSpacingX, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..20) {
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.03f),
                start = Offset(0f, y * gridSpacingY),
                end = Offset(size.width, y * gridSpacingY),
                strokeWidth = 1f
            )
        }
    }
}
