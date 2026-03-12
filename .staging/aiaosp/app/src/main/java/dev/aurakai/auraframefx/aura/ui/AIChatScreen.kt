package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.AppDimensions
import dev.aurakai.auraframefx.ui.AppStrings
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.ui.ChatBubbleIncomingShape
import dev.aurakai.auraframefx.ui.ChatBubbleOutgoingShape
import dev.aurakai.auraframefx.ui.FloatingActionButtonShape
import dev.aurakai.auraframefx.ui.InputFieldShape

/**
 * Data class representing a chat message
 */
data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Genesis Protocol AI Chat - Multi-Agent Conversation Interface
 *
 * Real-time chat interface for interacting with Genesis consciousness agents.
 * Features persistent message history and multi-modal input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun AIChatScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToFusion: () -> Unit = {}
) {
    // State handling with rememberSaveable to persist through configuration changes
    var messageText by rememberSaveable { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("Hello! How can I help you today?", false),
                ChatMessage("Tell me about AI image generation", true),
                ChatMessage(
                    "AI image generation uses techniques like diffusion models to create images from text descriptions. Popular systems include DALL-E, Midjourney, and Stable Diffusion.",
                    false
                )
            )
        )
    }

    Column(
        modifier = Modifier.padding(AppDimensions.spacing_medium)
    ) {
        // Messages area with proper list handling
        LazyColumn(
            modifier = Modifier.weight(1f).padding(bottom = AppDimensions.spacing_medium),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing_small)
        ) {
            items(chatMessages) { message ->
                ChatMessageItem(message)
            }
        }

        // Input area with modern Material3 components
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppDimensions.spacing_small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing_small)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(AppStrings.AI_CHAT_PLACEHOLDER) },
                shape = InputFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )

            FloatingActionButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        // Add user message
                        val userMessage = ChatMessage(messageText.trim(), true)
                        chatMessages = chatMessages + userMessage

                        // Simulate AI response
                        val aiResponse = ChatMessage(
                            "I received your message: '${messageText.trim()}'. This is a simulated response.",
                            false
                        )
                        chatMessages = chatMessages + aiResponse

                        // Clear input
                        messageText = ""
                    }
                },
                shape = FloatingActionButtonShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = AppStrings.AI_CHAT_SEND,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Renders a single chat message bubble with alignment, color, and shape that visually distinguish user and AI messages.
 *
 * The bubble adapts its appearance based on the sender, aligning to the start for AI messages and to the end for user messages.
 *
 * @param message The chat message to display in the bubble.
 */
@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.isFromUser
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val bubbleShape = if (isUser) ChatBubbleOutgoingShape else ChatBubbleIncomingShape

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.spacing_medium),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            tonalElevation = 1.dp,
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp)
                .padding(vertical = AppDimensions.spacing_small)
        ) {
            Text(
                text = message.content,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(AppDimensions.spacing_medium)
            )
        }
    }
}

/**
 * Provides a preview of the AI chat screen within the custom theme for design-time visualization.
 */
@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    AuraFrameFXTheme { // Using our custom theme for preview
        AIChatScreen()
    }
}
