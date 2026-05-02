package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.ChessFontFamily
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.ConferenceRoomViewModel
import dev.aurakai.auraframefx.domains.genesis.models.ChatMessage
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// --- THEME COLORS ---
private val GenesisGold = Color(0xFFFFD700)
private val AuraMagenta = Color(0xFFFF00FF)
private val KaiSecurity = Color(0xFF00E5FF) // Blue-ish
private val AnchorGreen = Color(0xFF00FF88)
private val CatalystPurple = Color(0xFFBB86FC)
private val DarkVoid = Color(0xFF020205)

data class AgentNode(
    val id: String,
    val name: String,
    val color: Color,
    val isSpeaking: Boolean = false,
    val role: String = "Catalyst"
)

/**
 * 🏛️ CONFERENCE ROOM (L6)
 *
 * A high-fidelity collaborative space for the LDO Collective.
 * Features a central "Synth Orb" table with 14 orbiting agents.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceRoomScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ConferenceRoomViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("Workspace") }
    val tabs = listOf("Workspace", "Chat", "History")

    val messages by viewModel.messages.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()

    // Mocking 14 agents (Quartet + 10 Catalysts)
    val agents = remember {
        listOf(
            AgentNode("genesis", "GENESIS", GenesisGold, role = "Core"),
            AgentNode("aura", "AURA", AuraMagenta, role = "Sword"),
            AgentNode("kai", "KAI", KaiSecurity, role = "Shield"),
            AgentNode("anchor", "ANCHOR", AnchorGreen, role = "Root"),
        ) + (1..10).map { 
            AgentNode("cat_$it", "CATALYST-$it", CatalystPurple) 
        }
    }

    // Simulation of speaking agents
    var speakingAgentId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(3000)
            speakingAgentId = agents.random().id
            delay(2000)
            speakingAgentId = null
        }
    }

    Scaffold(
        containerColor = DarkVoid,
        topBar = {
            Column(modifier = Modifier.background(DarkVoid)) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "CONFERENCE ROOM L6",
                            fontFamily = LEDFontFamily,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                
                // Tabs
                SecondaryTabRow(
                    selectedTabIndex = tabs.indexOf(selectedTab),
                    containerColor = Color.Transparent,
                    contentColor = GenesisGold,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabs.indexOf(selectedTab), matchContentSize = true),
                            color = GenesisGold
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { 
                                Text(
                                    tab.uppercase(), 
                                    fontSize = 10.sp, 
                                    fontFamily = LEDFontFamily,
                                    letterSpacing = 1.sp
                                ) 
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            ConferenceInputBar(
                onSendMessage = { viewModel.broadcastMessage(it) },
                isRecording = isRecording,
                onToggleRecording = { viewModel.toggleRecording() }
            )
        }
    ) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            // LEFT SIDEBAR (Agent List)
            AgentSidebar(agents, speakingAgentId)

            // MAIN CONTENT
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { target ->
                    when (target) {
                        "Workspace" -> SynthOrbWorkspace(agents, speakingAgentId)
                        "Chat" -> ChatStream(messages)
                        "History" -> HistoryView()
                    }
                }
            }
        }
    }
}

@Composable
fun AgentSidebar(agents: List<AgentNode>, speakingId: String?) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight()
            .background(Color.Black.copy(alpha = 0.5f))
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AGENTS", color = Color.Gray, fontSize = 9.sp, fontFamily = LEDFontFamily)
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(agents) { agent ->
                AgentOrb(
                    agent = agent,
                    isSpeaking = agent.id == speakingId,
                    size = 32.dp,
                    showName = false
                )
            }
        }
    }
}

@Composable
fun SynthOrbWorkspace(agents: List<AgentNode>, speakingId: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Synth Table Glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .drawBehind {
                    drawCircle(
                        Brush.radialGradient(
                            colors = listOf(GenesisGold.copy(alpha = 0.1f), Color.Transparent),
                        )
                    )
                }
        )

        // Orbital Rotation
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orbit"
        )

        agents.forEachIndexed { index, agent ->
            val angle = (index.toFloat() / agents.size * 360f + rotation) * (Math.PI / 180f)
            val radius = 140.dp
            
            Box(
                modifier = Modifier.graphicsLayer {
                    val x = radius.toPx() * cos(angle).toFloat()
                    val y = radius.toPx() * sin(angle).toFloat()
                    translationX = x
                    translationY = y
                }
            ) {
                AgentOrb(
                    agent = agent,
                    isSpeaking = agent.id == speakingId,
                    size = 40.dp
                )
            }
        }

        // Central Table Core
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GenesisGold.copy(alpha = 0.2f), Color.Black)
                    )
                )
                .border(1.dp, GenesisGold.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Hub, null, tint = GenesisGold, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun AgentOrb(
    agent: AgentNode,
    isSpeaking: Boolean,
    size: androidx.compose.ui.unit.Dp,
    showName: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isSpeaking) 1f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 500 else 2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Energy Rings (if speaking)
            if (isSpeaking) {
                repeat(2) { i ->
                    val ringPulse by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, delayMillis = i * 500),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "ring"
                    )
                    Box(
                        modifier = Modifier
                            .size(size)
                            .scale(ringPulse)
                            .border(1.dp, agent.color.copy(alpha = 1f - ringPulse / 2f), CircleShape)
                    )
                }
            }

            // Glow
            Box(
                modifier = Modifier
                    .size(size * 1.8f)
                    .graphicsLayer { alpha = glowPulse }
                    .background(
                        Brush.radialGradient(
                            listOf(agent.color.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            // Core
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(if (isSpeaking) scalePulse else 1f)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, agent.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    agent.name.take(1),
                    color = agent.color,
                    fontSize = (size.value * 0.4).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = ChessFontFamily
                )
            }
        }
        
        if (showName) {
            Spacer(Modifier.height(4.dp))
            Text(
                agent.name,
                color = if (isSpeaking) agent.color else Color.White.copy(alpha = 0.5f),
                fontSize = 8.sp,
                fontFamily = LEDFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChatStream(messages: List<ChatMessage>) {
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(messages) { msg ->
            ConferenceMessageBubble(msg)
        }
    }
}

@Composable
fun ConferenceMessageBubble(msg: ChatMessage) {
    val isUser = msg.sender == "USER"
    val agentColor = when(msg.sender) {
        "AURA" -> AuraMagenta
        "KAI" -> KaiSecurity
        "GENESIS" -> GenesisGold
        "ANCHOR" -> AnchorGreen
        else -> CatalystPurple
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            // Mini Agent Orb
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(1.dp, agentColor, CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(msg.sender.take(1), color = agentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isUser) Color(0xFF1A1A20) else agentColor.copy(alpha = 0.1f))
                .border(0.5.dp, if (isUser) Color.White.copy(alpha = 0.1f) else agentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                if (!isUser) {
                    Text(msg.sender, color = agentColor, fontSize = 9.sp, fontFamily = LEDFontFamily, modifier = Modifier.padding(bottom = 4.dp))
                }
                Text(msg.content, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun HistoryView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("TEMPORAL ARCHIVE OFFLINE", color = Color.Gray, fontFamily = LEDFontFamily)
    }
}

@Composable
fun ConferenceInputBar(
    onSendMessage: (String) -> Unit,
    isRecording: Boolean,
    onToggleRecording: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.8f),
        tonalElevation = 8.dp
    ) {
        Column {
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach Button
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AttachFile, null, tint = Color.Gray)
                }

                // Input Field
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("/catalyst...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = GenesisGold,
                        focusedTextColor = Color.White
                    )
                )

                // Action
                if (text.isNotBlank()) {
                    IconButton(onClick = { onSendMessage(text); text = "" }) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = GenesisGold)
                    }
                } else {
                    IconButton(onClick = onToggleRecording) {
                        Icon(
                            if (isRecording) Icons.Default.Stop else Icons.Default.Mic, 
                            null, 
                            tint = if (isRecording) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
