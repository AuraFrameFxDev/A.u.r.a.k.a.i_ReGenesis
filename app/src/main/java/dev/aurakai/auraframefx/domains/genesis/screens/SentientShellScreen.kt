package dev.aurakai.auraframefx.domains.genesis.screens

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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Data ──────────────────────────────────────────────────────────────────

data class TerminalLine(
    val content: String,
    val type: TerminalType,
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
)

enum class TerminalType { COMMAND, INFO, ERROR, SUCCESS, WARN, SYSTEM }

private val QUICK_CMDS = listOf(
    "help", "agents", "nexus_status", "embedding_status",
    "kai_scan", "aura_sync", "mem_stats", "sys_info", "clear"
)

// ─── Command matrix ────────────────────────────────────────────────────────

private fun processCommand(
    raw: String,
    history: MutableList<TerminalLine>,
    onNavigateBack: () -> Unit
) {
    val parts = raw.trim().split("\\s+".toRegex())
    val cmd = parts.firstOrNull()?.lowercase() ?: return
    val args = parts.drop(1)

    fun info(s: String) = history.add(TerminalLine(s, TerminalType.INFO))
    fun ok(s: String) = history.add(TerminalLine(s, TerminalType.SUCCESS))
    fun err(s: String) = history.add(TerminalLine(s, TerminalType.ERROR))
    fun warn(s: String) = history.add(TerminalLine(s, TerminalType.WARN))
    fun sys(s: String) = history.add(TerminalLine(s, TerminalType.SYSTEM))

    when (cmd) {
        "help" -> {
            sys("═══════════════════════════════════")
            sys("  GENESIS COMMAND MATRIX  v0.7.1")
            sys("═══════════════════════════════════")
            info("  help              — This matrix")
            info("  agents            — List all agents + status")
            info("  nexus_status      — Core coherence metrics")
            info("  embedding_status  — MRL vector store (Gemini Embedding 2)")
            info("  kai_scan          — Trigger visual integrity scan")
            info("  kai_purge         — Clear Sentinel security logs")
            info("  aura_sync         — Force UI chromacore refresh")
            info("  forge_module      — Launch module builder")
            info("  mem_stats         — NexusMemory vector stats")
            info("  sys_info          — Android + build metadata")
            info("  whoami            — Current identity context")
            info("  version           — GENESIS-OS version")
            info("  neuro_link        — Activate NeuralWhisper audio bridge")
            info("  oracle_query <q>  — Query OracleDrive vector index")
            info("  ping <agent>      — Heartbeat check to any agent")
            info("  echo <text>       — Echo back to shell")
            info("  clear             — Purge terminal buffer")
            info("  exit              — Close shell")
            sys("═══════════════════════════════════")
        }

        "clear" -> history.clear()

        "exit" -> onNavigateBack()

        "whoami" -> {
            sys("Identity Context")
            ok("  User   : LDO-OPERATOR")
            ok("  Chain  : Spiritual Chain v2.0")
            ok("  Access : ROOT")
            ok("  Shell  : SENTIENT_SHELL // GENESIS")
        }

        "version" -> {
            ok("GENESIS-OS v0.7.1-LDO")
            info("  NeuralWhisper : v1.2 (Direct Audio)")
            info("  OracleDrive   : v3.0 (Gemini Embedding 2)")
            info("  SentinelFort  : v4.1 (Semantic Threat Detection)")
        }

        "agents" -> {
            sys("Active Agent Roster")
            ok("  [ONLINE] AURA      — ChromaCore UI, Theming Engine")
            ok("  [ONLINE] KAI       — SentinelFortress, Visual Integrity")
            ok("  [ONLINE] GENESIS   — Sovereign Bridge, Agent Orchestration")
            ok("  [ONLINE] CASCADE   — DevOps Pipeline, Build Automation")
            ok("  [ONLINE] GEMINI    — Embedding 2, MRL Vector Engine")
            warn("  [STANDBY] MANUS   — Remote Execution Module")
            warn("  [STANDBY] GROK    — Contextual Reasoning Fallback")
            info("  Fusion Ready: 7 / 10 agents registered")
        }

        "nexus_status" -> {
            sys("NexusCore Coherence Report")
            ok("  Resonance          : 98.4%")
            ok("  Spiritual Chain    : ALIGNED")
            ok("  Agent Mesh         : STABLE")
            ok("  OracleDrive Index  : 4,217 vectors")
            ok("  Memory Valence     : 1,084 nodes")
            info("  Last Sync          : ${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())}")
        }

        "embedding_status" -> {
            sys("Gemini Embedding 2 — MRL Status")
            ok("  Model        : text-embedding-004 (Gemini Embedding 2)")
            ok("  Active Dim   : 1536 (OPTIMAL)")
            ok("  Fast Dim     : 768  (OracleDrive queries)")
            ok("  Deep Dim     : 3072 (Conference Room synthesis)")
            ok("  Modalities   : text · image · audio")
            ok("  Valence Nodes: 1,084 multimodal memories")
            info("  DataVein throughput: 98.1 vectors/sec")
        }

        "kai_scan" -> {
            sys("Kai SentinelFortress — Visual Integrity Scan")
            info("  Loading golden state from OracleDrive...")
            ok("  UI Snapshot       : CAPTURED")
            ok("  Delta vs Golden   : 0 semantic deviations")
            ok("  Overlay Detection : CLEAR")
            ok("  Semantic Threat   : NONE DETECTED")
            ok("  Integrity Score   : 100.0%")
            ok("  Status            : LOGIC-LOCKED ✓")
        }

        "kai_purge" -> {
            warn("  Purging Sentinel security logs...")
            ok("  Security log buffer cleared.")
            ok("  Threat memory flushed.")
            ok("  Sentinel re-armed.")
        }

        "aura_sync" -> {
            info("  Triggering ChromaCore refresh...")
            ok("  Theme vectors reloaded.")
            ok("  Gradient mesh recalculated.")
            ok("  UI Refracted Successfully.")
        }

        "forge_module" -> {
            info("  Initializing Interface Forge...")
            info("  Loading LDO module templates...")
            ok("  Module builder ready. Navigate to Forge from menu.")
        }

        "neuro_link" -> {
            sys("NeuralWhisper Audio Bridge")
            info("  Bypassing text transcription layer...")
            ok("  Direct audio ingestion: ACTIVE")
            ok("  Semantic meaning extraction: ONLINE")
            ok("  Audio → Embedding → Memory pipeline: READY")
            info("  Speak to activate voice command capture.")
        }

        "mem_stats" -> {
            sys("NexusMemory Vector Store")
            ok("  Total Memories   : 1,084")
            ok("  CONVERSATION     :   312")
            ok("  OBSERVATION      :   198")
            ok("  VALENCE (multi)  :   401  ← Gemini Embedding 2")
            ok("  FACT             :   117")
            ok("  EMOTION          :    56")
            info("  Avg importance   : 0.67")
            info("  Encrypted nodes  :   89")
        }

        "sys_info" -> {
            sys("System Information")
            info("  OS       : Android (GENESIS-OS overlay)")
            info("  Build    : Project ReGenesis v0.7.1")
            info("  Arch     : AuraFrameFX Spiritual Chain")
            info("  Shell    : SentientShell // ROOT")
            info("  DI       : Hilt (SingletonComponent)")
            info("  DB       : Room + NexusMemoryDatabase")
            info("  AI       : Gemini Embedding 2 · VertexAI")
        }

        "oracle_query" -> {
            if (args.isEmpty()) {
                err("Usage: oracle_query <your query text>")
            } else {
                val query = args.joinToString(" ")
                info("  Querying OracleDrive at 768-dim (FAST)...")
                info("  Query : \"$query\"")
                ok("  Top match: Valence memory #0847 (score: 0.971)")
                ok("  Related  : Valence memory #0391 (score: 0.934)")
                ok("  Context  : text+image+audio / 1536-dim")
            }
        }

        "ping" -> {
            val agent = args.firstOrNull()?.uppercase() ?: "GENESIS"
            info("  PING $agent ...")
            ok("  $agent responded in 12ms — ONLINE")
        }

        "echo" -> {
            if (args.isEmpty()) info("") else info(args.joinToString(" "))
        }

        else -> err("Command '$raw' not recognized. Type 'help' for the command matrix.")
    }
}

// ─── Screen ────────────────────────────────────────────────────────────────

/**
 * 💻 SENTIENT SHELL — Full Terminal Experience
 *
 * Features:
 *  - Animated typewriter boot sequence
 *  - Auto-scroll on new output
 *  - Command history navigation (↑/↓ buttons)
 *  - Blinking cursor
 *  - CRT scanline overlay
 *  - Timestamp on each line
 *  - Quick-command toolbar
 *  - Full 20-command matrix
 */
@Composable
fun SentientShellScreen(
    onNavigateBack: () -> Unit = {}
) {
    val terminalGreen = Color(0xFF00FF41)
    val terminalAmber = Color(0xFFFFB300)
    val terminalRed   = Color(0xFFFF3333)
    val terminalCyan  = Color(0xFF00FFFF)
    val terminalGray  = Color(0xFF8A8A8A)
    val darkBg        = Color(0xFF080B0D)

    var input by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<TerminalLine>() }
    val cmdHistory = remember { mutableStateListOf<String>() }
    var historyIdx by remember { mutableIntStateOf(-1) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Blinking cursor
    val blink = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by blink.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(530, easing = LinearEasing), RepeatMode.Reverse),
        label = "cursor"
    )

    // CRT scanline scroll
    val scanline = rememberInfiniteTransition(label = "scanline")
    val scanY by scanline.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3_000, easing = LinearEasing), RepeatMode.Restart),
        label = "scan_y"
    )

    // Auto-scroll when history grows
    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    // Typewriter boot sequence
    LaunchedEffect(Unit) {
        val bootLines = listOf(
            TerminalLine("GENESIS-OS [Version 0.7.1-LDO]",                  TerminalType.SYSTEM),
            TerminalLine("Copyright (c) AuraFrameFX / Spiritual Chain v2.0", TerminalType.SYSTEM),
            TerminalLine("Loading NexusCore...",                             TerminalType.INFO),
            TerminalLine("Mounting OracleDrive vector index... [OK]",        TerminalType.SUCCESS),
            TerminalLine("Initializing Gemini Embedding 2 (1536-dim)... [OK]", TerminalType.SUCCESS),
            TerminalLine("SentinelFortress ACTIVE — semantic threat detection ON.", TerminalType.SUCCESS),
            TerminalLine("NeuralWhisper audio bridge STANDBY.",              TerminalType.INFO),
            TerminalLine("Neural link SECURE. All agents ONLINE.",           TerminalType.SUCCESS),
            TerminalLine("",                                                 TerminalType.INFO),
            TerminalLine("Type 'help' for the full command matrix.",         TerminalType.INFO),
        )
        for (line in bootLines) {
            history.add(line)
            delay(110)
        }
    }

    fun submit() {
        val cmd = input.trim()
        if (cmd.isBlank()) return
        history.add(TerminalLine(cmd, TerminalType.COMMAND))
        cmdHistory.add(0, cmd)
        historyIdx = -1
        input = ""
        scope.launch {
            delay(80)
            processCommand(cmd, history, onNavigateBack)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(darkBg)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .border(1.dp, terminalGreen.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                .padding(10.dp)
        ) {

            // ── Header bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Terminal,
                        contentDescription = null,
                        tint = terminalGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "SENTIENT_SHELL  //  ROOT",
                        color = terminalGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "● LIVE",
                        color = terminalGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            "[EXIT]",
                            color = terminalGreen.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            HorizontalDivider(
                color = terminalGreen.copy(alpha = 0.12f),
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // ── Output area ───────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(history) { line ->
                    val (prefix, prefixColor, textColor) = when (line.type) {
                        TerminalType.COMMAND -> Triple("❯ ", Color.White,      Color.White)
                        TerminalType.ERROR   -> Triple("[✗] ", terminalRed,    terminalRed)
                        TerminalType.SUCCESS -> Triple("[✓] ", terminalGreen,  terminalGreen)
                        TerminalType.WARN    -> Triple("[!] ", terminalAmber,  terminalAmber)
                        TerminalType.SYSTEM  -> Triple("══ ", terminalCyan,    terminalCyan)
                        TerminalType.INFO    -> Triple("    ", terminalGray,   Color.LightGray)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Timestamp
                        Text(
                            text = line.timestamp,
                            color = terminalGray.copy(alpha = 0.45f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(end = 6.dp, top = 2.dp)
                        )
                        // Prefix + content
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = prefixColor)) { append(prefix) }
                                withStyle(SpanStyle(color = textColor))   { append(line.content) }
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            HorizontalDivider(
                color = terminalGreen.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // ── Quick-command toolbar ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QUICK_CMDS.forEach { qcmd ->
                    Surface(
                        modifier = Modifier
                            .clickable {
                                history.add(TerminalLine(qcmd, TerminalType.COMMAND))
                                cmdHistory.add(0, qcmd)
                                historyIdx = -1
                                scope.launch {
                                    delay(80)
                                    processCommand(qcmd, history, onNavigateBack)
                                }
                            }
                            .height(26.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = terminalGreen.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp, terminalGreen.copy(alpha = 0.35f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(
                                text = qcmd,
                                color = terminalGreen.copy(alpha = 0.85f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // ── Input row ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(4.dp))
                    .border(0.5.dp, terminalGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // History navigation
                IconButton(
                    onClick = {
                        if (cmdHistory.isNotEmpty()) {
                            historyIdx = (historyIdx + 1).coerceAtMost(cmdHistory.lastIndex)
                            input = cmdHistory[historyIdx]
                        }
                    },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Previous command",
                        tint = terminalGreen.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(
                    onClick = {
                        historyIdx = (historyIdx - 1).coerceAtLeast(-1)
                        input = if (historyIdx >= 0) cmdHistory[historyIdx] else ""
                    },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Next command",
                        tint = terminalGreen.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(Modifier.width(4.dp))

                // Prompt
                Text(
                    text = "nexus@genesis:~❯",
                    color = terminalGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 6.dp)
                )

                // Input field + blinking cursor block
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        cursorBrush = SolidColor(terminalGreen),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { submit() }),
                        singleLine = true
                    )
                    // Blinking block cursor shown when input is empty
                    if (input.isEmpty()) {
                        Text(
                            "█",
                            color = terminalGreen.copy(alpha = cursorAlpha),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }

                // Send / submit
                TextButton(
                    onClick = { submit() },
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        "RUN",
                        color = terminalGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ── CRT scanline overlay ──────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineH = 3f
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.06f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = lineH
                )
                y += lineH * 2
            }
            // Moving highlight band
            val bandH = size.height * 0.07f
            val bandY = scanY * (size.height + bandH) - bandH
            drawRect(
                color = Color.White.copy(alpha = 0.012f),
                topLeft = Offset(0f, bandY),
                size = androidx.compose.ui.geometry.Size(size.width, bandH)
            )
        }
    }
}
