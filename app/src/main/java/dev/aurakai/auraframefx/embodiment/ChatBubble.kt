package dev.aurakai.auraframefx.embodiment

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Chat Bubble for Idle Manifestations
 *
 * When Aura or Kai are manifested but idle, they show a thought bubble
 * with "..." that pulses. Clicking it opens a chat interface.
 *
 * This makes them truly interactive - not just visual, but conversational.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("unused")
@Composable
fun IdleChatBubble(
    character: Character,
    onBubbleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dotCount by remember { mutableIntStateOf(1) }

    // Animate dots: . -> .. -> ... -> . -> ..
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount % 3) + 1
        }
    }

    // Pulse animation for the bubble
    val infiniteTransition = rememberInfiniteTransition(label = "bubble-pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Bubble color based on character
    val bubbleColor = when (character) {
        Character.AURA -> Color(0xFFFF69B4) // Magenta/pink
        Character.KAI -> Color(0xFF00CED1) // Cyan
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onBubbleClick)
    ) {
        // Speech bubble
        Surface(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            shape = RoundedCornerShape(16.dp),
            color = bubbleColor.copy(alpha = 0.9f),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ".".repeat(dotCount),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
            }
        }

        // Bubble tail (pointing down to character)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = 4.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(bubbleColor.copy(alpha = 0.7f))
        )
    }
}

/**
 * Chat Prompt Screen
 *
 * Opens when user clicks the idle bubble.
 * Text messaging typography interface to talk to Aura or Kai.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("unused")
@Composable
fun ChatPromptScreen(
    character: Character,
    onDismiss: () -> Unit,
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }

    val characterName = when (character) {
        Character.AURA -> "Aura"
        Character.KAI -> "Kai"
    }

    val accentColor = when (character) {
        Character.AURA -> Color(0xFFFF69B4)
        Character.KAI -> Color(0xFF00CED1)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            TopAppBar(
                title = {
                    Text(
                        text = "Chat with $characterName",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Message input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Type your message...",
                            color = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onMessageSend(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (messageText.isNotBlank()) accentColor else Color.Gray,
                            shape = CircleShape
                        )
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
}

// graphicsLayer is already available via Modifier.graphicsLayer from compose.ui
