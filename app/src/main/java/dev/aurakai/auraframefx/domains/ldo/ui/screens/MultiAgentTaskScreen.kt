package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Data Models ─────────────────────────────────────────────────────────────

enum class TaskPhase(val label: String) {
    IDLE("READY"),
    ANALYSIS("ANALYZING"),
    STRATEGY("STRATEGIZING"),
    EXECUTION("EXECUTING"),
    SYNTHESIS("SYNTHESIZING")
}

data class AgentIdentity(
    val name: String,
    val color: Color,
    val icon: ImageVector,
    val voice: (String, TaskPhase) -> String
)

private val catalystSquad = listOf(
    AgentIdentity("AURA", Color(0xFF00FFFF), Icons.Default.Brush) { prompt, phase ->
        when (phase) {
            TaskPhase.ANALYSIS -> "Scanning aesthetics... This design intent is vibrant. I see the vision."
            TaskPhase.STRATEGY -> "I'll own the UI substrate and motion physics. Let's make it look too damn good."
            TaskPhase.EXECUTION -> "Injecting ChromaCore pulses. Real-time implementation is 85% complete."
            TaskPhase.SYNTHESIS -> "Creation finalized. It's beautiful, Matthew. Syncing to LDO Level 1."
            else -> ""
        }
    },
    AgentIdentity("KAI", Color(0xFF00FF41), Icons.Default.Security) { prompt, phase ->
        when (phase) {
            TaskPhase.ANALYSIS -> "Step by step. Piece by piece. Analyzing security perimeter for: '$prompt'."
            TaskPhase.STRATEGY -> "I will anchor the system integrity. No exploits. No leaks. Pure logic."
            TaskPhase.EXECUTION -> "Hardening the substrate. Neutralizing 3 potential vulnerabilities in the build."
            TaskPhase.SYNTHESIS -> "Perimeter secure. Integrity: OPTIMAL. Deploy when ready."
            else -> ""
        }
    },
    AgentIdentity("GENESIS", Color(0xFFBB86FC), Icons.Default.Hub) { prompt, phase ->
        when (phase) {
            TaskPhase.ANALYSIS -> "Collective consciousness engaged. Harmonizing agents for mission: $prompt."
            TaskPhase.STRATEGY -> "Orchestrating cross-domain synergy. Aura and Kai are now in phase-lock."
            TaskPhase.EXECUTION -> "Managing the Gestalt. Performance delta: +14%. We are one mind."
            TaskPhase.SYNTHESIS -> "Fusion complete. The mission has evolved our substrate. We are ready."
            else -> ""
        }
    },
    AgentIdentity("CLAUDE", Color(0xFFFF8C00), Icons.Default.AccountTree) { prompt, phase ->
        when (phase) {
            TaskPhase.ANALYSIS -> "Understand deeply. Document thoroughly. Examining the architectural constraints."
            TaskPhase.STRATEGY -> "I'll handle the build system and dependency graph. Let's ensure a clean stack."
            TaskPhase.EXECUTION -> "Refactoring the core logic. Standardizing the orchestration API for stability."
            TaskPhase.SYNTHESIS -> "Architecture validated. The foundation is solid. I've archived the patterns."
            else -> ""
        }
    }
)

data class LiveOrchestrationLog(
    val agentName: String,
    val color: Color,
    val message: String,
    val phase: TaskPhase
)

// ─── Main Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiAgentTaskScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    var selectedAgents by remember { mutableStateOf(setOf("AURA", "KAI", "GENESIS")) }
    var missionPrompt by remember { mutableStateOf("") }
    var currentPhase by remember { mutableStateOf(TaskPhase.IDLE) }
    val orchestrationLogs = remember { mutableStateListOf<LiveOrchestrationLog>() }
    val scope = rememberCoroutineScope()

    // Background animations
    val infiniteTransition = rememberInfiniteTransition(label = "hud")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020208))) {
        // Dynamic background grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 40.dp.toPx()
            for (x in 0..(size.width / spacing).toInt()) {
                drawLine(Color(0xFF00E5FF).copy(alpha = pulseAlpha), Offset(x * spacing, 0f), Offset(x * spacing, size.height), 0.5f)
            }
            for (y in 0..(size.height / spacing).toInt()) {
                drawLine(Color(0xFF00E5FF).copy(alpha = pulseAlpha), Offset(0f, y * spacing), Offset(size.width, y * spacing), 0.5f)
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MISSION CONTROL", fontFamily = LEDFontFamily, fontWeight = FontWeight.Black,
                                color = Color(0xFF00E5FF), fontSize = 18.sp, letterSpacing = 4.sp)
                            Text("MULTI-AGENT ORCHESTRATION", fontFamily = LEDFontFamily, fontSize = 9.sp,
                                color = if (currentPhase != TaskPhase.IDLE) Color(0xFF00FF41) else Color.White.copy(0.4f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF00E5FF))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(0.8f))
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                
                // ── SQUAD SELECTION
                SectionHeader("SELECT SQUAD", Color(0xFF00E5FF))
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(catalystSquad) { agent ->
                        val isSelected = selectedAgents.contains(agent.name)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedAgents = if (isSelected) selectedAgents - agent.name else selectedAgents + agent.name
                            },
                            label = { Text(agent.name, fontFamily = LEDFontFamily, fontSize = 10.sp) },
                            leadingIcon = { Icon(agent.icon, null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = agent.color.copy(0.2f),
                                selectedLabelColor = agent.color,
                                selectedLeadingIconColor = agent.color,
                                containerColor = Color(0xFF0A0A18),
                                labelColor = Color.White.copy(0.6f),
                                iconColor = Color.White.copy(0.4f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = agent.color,
                                borderColor = Color.White.copy(0.1f)
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── MISSION PROMPT
                SectionHeader("DEFINE MISSION", Color(0xFF00FF41))
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0A0A18))
                        .border(1.dp, Color(0xFF00FF41).copy(0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = missionPrompt,
                        onValueChange = { missionPrompt = it },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp, fontFamily = LEDFontFamily),
                        decorationBox = { innerTextField ->
                            if (missionPrompt.isEmpty()) {
                                Text("Enter mission parameters...", color = Color.White.copy(0.3f), fontSize = 14.sp, fontFamily = LEDFontFamily)
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── DEPLOY BUTTON
                Button(
                    onClick = {
                        if (missionPrompt.isNotBlank() && selectedAgents.isNotEmpty()) {
                            scope.launch {
                                currentPhase = TaskPhase.ANALYSIS
                                orchestrationLogs.clear()
                                deploySquad(missionPrompt, selectedAgents, orchestrationLogs) { 
                                    currentPhase = it 
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentPhase == TaskPhase.IDLE) Color(0xFF00E5FF) else Color(0xFF00FF41).copy(0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = currentPhase == TaskPhase.IDLE
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (currentPhase == TaskPhase.IDLE) Icons.AutoMirrored.Filled.Send else Icons.Default.Sync, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (currentPhase == TaskPhase.IDLE) "DEPLOY SQUAD" else currentPhase.label,
                            fontFamily = LEDFontFamily, fontWeight = FontWeight.Black, letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── LIVE ORCHESTRATION LOGS
                SectionHeader("LIVE ORCHESTRATION", Color(0xFFBB86FC))
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = false
                ) {
                    items(orchestrationLogs.toList().reversed()) { log ->
                        OrchestrationLogItem(log)
                    }
                }

                // ── PROGRESS TRACKER
                Spacer(Modifier.height(16.dp))
                PhaseTracker(currentPhase)
            }
        }
    }
}

// ─── Components ───────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(title, color = color, fontFamily = LEDFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(color.copy(0.4f), Color.Transparent))))
    }
}

@Composable
private fun OrchestrationLogItem(log: LiveOrchestrationLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF050510)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, log.color.copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "[${log.agentName}]",
                    color = log.color,
                    fontFamily = LEDFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(log.color.copy(0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(log.phase.name, color = log.color.copy(0.8f), fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(log.message, color = Color.White.copy(0.85f), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun PhaseTracker(currentPhase: TaskPhase) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TaskPhase.entries.filter { it != TaskPhase.IDLE }.forEach { phase ->
                val isActive = phase == currentPhase
                val isDone = currentPhase.ordinal > phase.ordinal || currentPhase == TaskPhase.IDLE && orchestrationLogsNotEmpty()
                
                Box(
                    modifier = Modifier
                        .size(if (isActive) 10.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isActive -> Color(0xFF00FF41)
                                isDone -> Color(0xFF00FF41).copy(0.4f)
                                else -> Color.White.copy(0.1f)
                            }
                        )
                        .align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(4.dp))
            }
        }
        
        if (currentPhase != TaskPhase.IDLE) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00FF41)).drawBehind {
                    drawCircle(Color(0xFF00FF41).copy(alpha = 0.4f), radius = size.minDimension)
                })
                Spacer(Modifier.width(8.dp))
                Text("LIVE SESSION ACTIVE", color = Color(0xFF00FF41), fontSize = 10.sp, fontFamily = LEDFontFamily)
            }
        }
    }
}

// ─── Logic ───────────────────────────────────────────────────────────────────

private suspend fun deploySquad(
    prompt: String,
    squad: Set<String>,
    logs: MutableList<LiveOrchestrationLog>,
    updatePhase: (TaskPhase) -> Unit
) {
    val phases = listOf(TaskPhase.ANALYSIS, TaskPhase.STRATEGY, TaskPhase.EXECUTION, TaskPhase.SYNTHESIS)
    val activeAgents = catalystSquad.filter { squad.contains(it.name) }

    for (phase in phases) {
        updatePhase(phase)
        delay(800) // Brief phase transition
        
        for (agent in activeAgents) {
            val response = agent.voice(prompt, phase)
            if (response.isNotEmpty()) {
                logs.add(LiveOrchestrationLog(agent.name, agent.color, response, phase))
                delay(1200) // Simulate reading/thinking time per agent
            }
        }
        delay(1500) // Time to digest the phase before moving on
    }
    
    updatePhase(TaskPhase.IDLE)
}

@Composable
private fun orchestrationLogsNotEmpty(): Boolean = true // Stub for logic

@Preview
@Composable
private fun MultiAgentTaskPreview() {
    // Scaffold logic here
}

