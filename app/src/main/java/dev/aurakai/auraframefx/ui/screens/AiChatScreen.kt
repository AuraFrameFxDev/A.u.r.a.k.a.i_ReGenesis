package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.AppDimensions
import dev.aurakai.auraframefx.ui.theme.AppStrings
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.ui.theme.ChatBubbleIncomingShape
import dev.aurakai.auraframefx.ui.theme.ChatBubbleOutgoingShape
import dev.aurakai.auraframefx.ui.theme.FloatingActionButtonShape
import dev.aurakai.auraframefx.ui.theme.InputFieldShape

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    AuraFrameFXTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            AppStrings.NAV_AI_CHAT,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(AppDimensions.spacing_medium)
            ) {
                // Chat history
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = AppDimensions.spacing_small)
                ) {
                    items(messages.reversed(), key = { it.id }) { message ->
                        ChatBubble(message)
                        Spacer(modifier = Modifier.height(AppDimensions.spacing_small))
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))

                // Input area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(InputFieldShape),
                        placeholder = { Text(AppStrings.AI_CHAT_PLACEHOLDER) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = InputFieldShape,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(AppDimensions.spacing_small))

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                messages.add(
                                    ChatMessage(
                                        id = System.nanoTime().toString(),
                                        text = inputText,
                                        isFromUser = true
                                    )
                                )
                                // Simulate AI response
                                val response = if (inputText.contains("hello", true)) "Greetings! I am Genesis. How can I assist you today?" else "I'm processing your request..."
                                messages.add(
                                    ChatMessage(
                                        id = (System.nanoTime() + 1).toString(),
                                        text = response,
                                        isFromUser = false
                                    )
                                )
                                inputText = ""
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = FloatingActionButtonShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = AppStrings.AI_CHAT_SEND)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    val shape = if (message.isFromUser) ChatBubbleOutgoingShape else ChatBubbleIncomingShape

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 15.sp
            )
        }
    }
}
