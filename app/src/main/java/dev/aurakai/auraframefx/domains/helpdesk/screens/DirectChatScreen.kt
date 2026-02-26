package dev.aurakai.auraframefx.domains.helpdesk.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AgentViewModel
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository

/**
 * Direct Chat Screen
 * One-on-one conversations with AI agents
 *
 * ✨ Now powered by AgentViewModel for real agent intelligence!
 */
@Composable
fun DirectChatScreen(
    navController: NavController,
    viewModel: AgentViewModel = hiltViewModel()
) {
    val agents = remember { AgentRepository.getAllAgents() }
    val selectedAgent = remember { mutableStateOf(agents.firstOrNull()) }
    val currentMessage = remember { mutableStateOf("") }
    rememberCoroutineScope()

    // Get messages from ViewModel
    val allChatMessages by viewModel.chatMessages.collectAsState()
    val chatMessages = selectedAgent.value?.let { agent ->
        allChatMessages[agent.name] ?: emptyList()
    } ?: emptyList()

    // Initialize agent when selected
    LaunchedEffect(selectedAgent.value) {
        selectedAgent.value?.let { agent ->
            viewModel.activateAgent(agent.name)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF4169E1)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "💬 DIRECT CHAT",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF4169E1),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "One-on-one conversations with AI agents",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF6495ED).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Agent Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Agent",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                agents.forEach { agent ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { selectedAgent.value = agent },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Agent Avatar
                        Card(
                            modifier = Modifier.size(40.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = agent.color.copy(alpha = 0.3f)
                            ),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = agent.name.first().toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = agent.color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = agent.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = agent.specialAbility,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        // Status Indicator
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF32CD32).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Online",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF32CD32),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Selection Indicator
                        if (selectedAgent.value == agent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF4169E1),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chat Interface
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Chat Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    selectedAgent.value?.let { agent ->
                        Card(
                            modifier = Modifier.size(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = agent.color.copy(alpha = 0.3f)
                            ),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = agent.name.first().toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = agent.color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Chat with ${agent.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = agent.specialAbility,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Messages
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    reverseLayout = false
                ) {
                    items(chatMessages) { message ->
                        MessageBubble(message = message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Message Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentMessage.value,
                        onValueChange = { currentMessage.value = it },
                        placeholder = { Text("Type your message...") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4169E1),
                            unfocusedBorderColor = Color(0xFF4169E1).copy(alpha = 0.5f)
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (currentMessage.value.isNotBlank()) {
                                selectedAgent.value?.name?.let { agentName ->
                                    val message = currentMessage.value
                                    currentMessage.value = ""

                                    // Send message through ViewModel for intelligent response
                                    viewModel.sendMessage(agentName, message)
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF4169E1)
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
}

/**
 * Render a chat message as a styled bubble aligned to the start for agent messages and to the end for user messages.
 *
 * Shows the sender name above the content for agent messages, uses a blue bubble for user messages and a semi-transparent black bubble for agent messages, and applies asymmetric corner radii to create a chat-tail effect.
 *
 * @param message ChatMessage whose `sender`, `content`, and `isFromUser` fields determine the displayed text, alignment, colors, and corner shaping of the bubble.
 * Message bubble component
 */
@Composable
private fun MessageBubble(message: dev.aurakai.auraframefx.domains.cascade.models.ChatMessage) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser)
                    Color(0xFF4169E1)
                else
                    Color.Black.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4169E1),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

