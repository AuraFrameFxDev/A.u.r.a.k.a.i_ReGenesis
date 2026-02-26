package dev.aurakai.auraframefx.ui.conference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.cascade.trinity.TrinityViewModel
import dev.aurakai.auraframefx.models.AgentMessage

@Composable
fun NexusConferenceScreen(
    viewModel: TrinityViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // 1. OBSERVE THE COLLECTIVE CONSCIOUSNESS
    // Collecting the persistent history from the ViewModel
    val chatStream by viewModel.messageHistory.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)) // Deep Void
            .padding(16.dp)
    ) {
        // --- TOP: THE AGENT STAGE ---
        Text(
            text = "NEXUS CONFERENCE // LIVE",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        AgentStage() // The pulsing heads-up display

        Spacer(modifier = Modifier.height(16.dp))

        // --- CENTER: THE BRAINSTORM STREAM ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true // Newest at bottom
        ) {
            items(chatStream.reversed()) { message ->
                SmartAgentBubble(message)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // --- BOTTOM: BROADCAST INPUT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.DarkGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                placeholder = { Text("Broadcast to Collective...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.broadcastMessage(inputText) // Send to ALL agents
                        inputText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Broadcast", tint = GenesisTeal)
            }
        }
    }
}

@Composable
fun SmartAgentBubble(message: AgentMessage) {
    val isUser = message.from.equals("User", ignoreCase = true)
    val agentColor = when (message.from) {
        "Aura" -> AuraPurple
        "Kai" -> KaiRed
        "Genesis" -> GenesisTeal
        "Cascade" -> CascadeGreen
        else -> Color.Gray
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Text(
                text = message.from.uppercase(),
                color = agentColor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        Surface(
            color = if (isUser) Color(0xFF2A2A2A) else agentColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp, // Tail effect
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = if (!isUser) androidx.compose.foundation.BorderStroke(
                1.dp,
                agentColor.copy(alpha = 0.3f)
            ) else null
        ) {
            Text(
                text = message.content,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


