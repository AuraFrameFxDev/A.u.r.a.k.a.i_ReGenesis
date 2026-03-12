package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.ChessFontFamily
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.cascade.models.ChatMessage
import dev.aurakai.auraframefx.domains.genesis.ConferenceRoomViewModel

// --- COLORS & THEME LOCALS ---
private val AuraPurple = Color(0xFFD500F9)
private val KaiRed = Color(0xFFFF1744)
private val GenesisTeal = Color(0xFF00E5FF)
private val CascadeGreen = Color(0xFF00E676)
private val UserBlue = Color(0xFF2979FF)
private val DarkBackground = Color(0xFF050508)
private val SurfaceGlass = Color(0xFF121216)

/**
 * 🧠 NEXUS CONFERENCE ROOM
 * Multi-Agent collaboration space with Gemini-style visuals.
 */
@Composable
fun ConferenceRoomScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToAgents: () -> Unit = {},
    viewModel: ConferenceRoomViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isTranscribing by viewModel.isTranscribing.collectAsState()

    // Auto-scroll to bottom
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            UnisonInputBar(
                isRecording = isRecording,
                onToggleRecording = { viewModel.toggleRecording() },
                onSendMessage = { text ->
                    // Broadcast to ALL agents in the collective
                    viewModel.broadcastMessage(text)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, Color(0xFF0A0A10), Color.Black)
                    )
                )
        ) {
            // 1. TOP: AGENT STAGE (Visualizers)
            AgentStageRow(
                isSomeoneSpeaking = isTranscribing || isRecording // Mock "activity"
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // 2. CENTER: UNIFIED CHAT STREAM
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { msg ->
                    ConferenceMessageBubble(message = msg)
                }
            }
        }
    }
}

@Composable
fun AgentStageRow(isSomeoneSpeaking: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(100.dp), // Height for the visuals
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AURA
        AgentAvatarNode(
            name = "AURA",
            color = AuraPurple,
            isActive = true
        )
        // KAI
        AgentAvatarNode(
            name = "KAI",
            color = KaiRed,
            isActive = true
        )
        // GENESIS (Center/Big)
        AgentAvatarNode(
            name = "GENESIS",
            color = GenesisTeal,
            isActive = true,
            isPrimary = true,
            isSpeaking = isSomeoneSpeaking // Center simulates the "Brain" processing
        )
        // CASCADE
        AgentAvatarNode(
            name = "CASCADE",
            color = CascadeGreen,
            isActive = true
        )
    }
}

@Composable
fun AgentAvatarNode(
    name: String,
    color: Color,
    isActive: Boolean,
    isPrimary: Boolean = false,
    isSpeaking: Boolean = false
) {
    val size = if (isPrimary) 64.dp else 48.dp
    val fontSize = if (isPrimary) 10.sp else 8.sp

    // Breathing pulse - Slowed down for better performance
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 1000 else 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isSpeaking) 0.8f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 1000 else 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Derived states to skip reading animated values when not necessary
    val finalScale by remember(isSpeaking) {
        derivedStateOf { if (isSpeaking) scalePulse else 1.05f }
    }
    val finalGlow by remember(isSpeaking) {
        derivedStateOf { if (isSpeaking) glowPulse else 0.4f }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Glow Halo
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(size * 1.5f)
                        .graphicsLayer {
                            scaleX = finalScale
                            scaleY = finalScale
                            alpha = finalGlow
                        }
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    color.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
            }

            // Core
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Icon or Initial
                Text(
                    text = name.take(1),
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = ChessFontFamily
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            color = if (isActive) color else Color.Gray,
            fontSize = fontSize,
            fontFamily = LEDFontFamily,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ConferenceMessageBubble(message: ChatMessage) {
    val isUser = message.sender.equals("User", ignoreCase = true) || message.sender.equals(
        "You",
        ignoreCase = true
    )

    val bubbleColor = when (message.sender.uppercase()) {
        "AURA" -> AuraPurple
        "KAI" -> KaiRed
        "GENESIS" -> GenesisTeal
        "CASCADE" -> CascadeGreen
        else -> UserBlue
    }.copy(alpha = 0.15f)

    val borderColor = when (message.sender.uppercase()) {
        "AURA" -> AuraPurple
        "KAI" -> KaiRed
        "GENESIS" -> GenesisTeal
        "CASCADE" -> CascadeGreen
        else -> UserBlue
    }.copy(alpha = 0.5f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Agent Avatar Mini
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, borderColor, CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    message.sender.take(1),
                    color = borderColor.copy(alpha = 1f),
                    fontSize = 12.sp,
                    fontFamily = ChessFontFamily
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Message Content
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp, // Tail effect
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .border(
                    1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            if (!isUser) {
                Text(
                    text = message.sender.uppercase(),
                    color = borderColor.copy(alpha = 1f),
                    fontSize = 10.sp,
                    fontFamily = LEDFontFamily,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = message.content,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun UnisonInputBar(
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceGlass)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input Field
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Speak with the Nexus...", color = Color.Gray) },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = GenesisTeal,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Mic / Send Actions
        val haptic = LocalHapticFeedback.current
        val micScale by animateFloatAsState(if (isRecording) 1.15f else 1f, label = "mic_scale")

        if (text.isBlank()) {
            // Mic Button
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggleRecording()
                },
                modifier = Modifier
                    .size(48.dp)
                    .scale(micScale)
                    .background(
                        if (isRecording) KaiRed else GenesisTeal.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                    contentDescription = "Voice",
                    tint = if (isRecording) Color.White else GenesisTeal
                )
            }
        } else {
            // Send Button
            IconButton(
                onClick = {
                    onSendMessage(text)
                    text = ""
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(UserBlue, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

