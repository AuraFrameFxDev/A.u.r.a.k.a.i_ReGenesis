package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal

/**
 * Displays the main conference room UI, including agent selection, recording and transcription controls, chat interface, and message input.
 *
 * This composable manages local state for the selected agent, recording, and transcription status. It provides interactive controls for switching agents, starting/stopping recording and transcription, and a placeholder chat interface with a message input area.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Displays the main conference room UI, including agent selection, recording and transcription controls, chat area, and message input.
 *
 * This composable manages local UI state for the selected agent, recording, and transcription toggles. It provides interactive controls for selecting an agent, starting/stopping recording and transcription, and a chat interface with a message input field. Several actions are placeholders for future implementation.
 */
@Composable
fun ConferenceRoomScreen() {
    // Load string resources once at composition time
    val agentAura = stringResource(R.string.agent_aura)
    val agentKai = stringResource(R.string.agent_kai)
    val agentCascade = stringResource(R.string.agent_cascade)

    var selectedAgent by remember { mutableStateOf("Aura") }
    var isRecording by remember { mutableStateOf(false) }
    var isTranscribing by remember { mutableStateOf(false) }
    var messageInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ConferenceMessage>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.conference_room),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonTeal
            )

            IconButton(
                onClick = {
                    // Log settings access for analytics
                    // Future navigation: navController.navigate("conference_settings")
                    timber.log.Timber.i("Conference room settings clicked - Settings screen not yet implemented")
                }
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_action),
                    tint = NeonPurple
                )
            }
        }

        // Agent Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AgentButton(
                agent = agentAura,
                isSelected = selectedAgent == agentAura,
                onClick = { selectedAgent = agentAura }
            )
            AgentButton(
                agent = agentKai,
                isSelected = selectedAgent == agentKai,
                onClick = { selectedAgent = agentKai }
            )
            AgentButton(
                agent = agentCascade,
                isSelected = selectedAgent == agentCascade,
                onClick = { selectedAgent = agentCascade }
            )
        }

        // Recording Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            RecordingButton(
                isRecording = isRecording,
                onClick = { isRecording = !isRecording }
            )

            TranscribeButton(
                isTranscribing = isTranscribing,
                onClick = { isTranscribing = !isTranscribing }
            )
        }

        // Chat Interface
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.size) { index ->
                val message = messages[messages.size - 1 - index]
                MessageBubble(message = message)
            }
        }

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Type your message...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                    unfocusedContainerColor = NeonTeal.copy(alpha = 0.1f)
                )
            )

            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        val newMessage = ConferenceMessage(
                            agent = selectedAgent,
                            text = messageInput,
                            timestamp = System.currentTimeMillis()
                        )
                        messages = messages + newMessage
                        messageInput = ""
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = NeonBlue
                )
            }
        }
    }
}

/**
 * Renders a button representing an agent, visually indicating selection state.
 *
 * The button updates its background and text color based on whether it is selected, and triggers the provided callback when pressed.
 *
 * @param agent The display name of the agent.
 * @param isSelected True if this agent is currently selected.
 * @param onClick Invoked when the button is clicked.
 */
@Composable
fun RowScope.AgentButton(
    agent: String,
    isSelected: Boolean,
    onClick: @Composable () -> Unit,
) {
    val backgroundColor = if (isSelected) NeonTeal else Color.Black
    val contentColor = if (isSelected) Color.White else NeonTeal

    Button(
        onClick = onClick, ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ), Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = agent,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun RowScope.Button(
    onClick: @Composable (() -> Unit),
    colors: ButtonColors,
    modifier: Modifier,
    content: @Composable (RowScope) -> Unit
) {
    TODO("Not yet implemented")
}

/**
 * Renders a toggle button for recording, displaying a stop icon with red tint when active and a circle icon with purple tint when inactive.
 *
 * @param isRecording Indicates whether recording is currently active.
 * @param onClick Invoked when the button is pressed.
 */
@Composable
fun RecordingButton(
    isRecording: Boolean,
    onClick: () -> Unit,
) {
    val icon = if (isRecording) Icons.Default.Stop else Icons.Default.Circle
    val color = if (isRecording) Color.Red else NeonPurple

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp)
    ) {
        Icon(
            icon,
            contentDescription = if (isRecording) stringResource(R.string.stop_recording) else stringResource(
                R.string.start_recording
            ),
            tint = color
        )
    }
}

/**
 * Renders a toggle button for controlling transcription state, updating its icon and color based on whether transcription is active.
 *
 * Shows a stop icon with a red tint when transcription is active, and a phone icon with a blue tint otherwise. Invokes the provided callback when pressed.
 *
 * @param isTranscribing Indicates if transcription is currently active.
 * @param onClick Callback invoked when the button is pressed.
 */
@Composable
fun TranscribeButton(
    isTranscribing: Boolean,
    onClick: () -> Unit,
) {
    val icon = if (isTranscribing) Icons.Default.Stop else Icons.Default.Phone
    val color = if (isTranscribing) Color.Red else NeonBlue

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp)
    ) {
        Icon(
            icon,
            contentDescription = if (isTranscribing) stringResource(R.string.stop_transcription) else stringResource(
                R.string.start_transcription
            ),
            tint = color
        )
    }
}

/**
 * Data class representing a chat message in the conference room.
 *
 * @param agent The agent that sent the message (e.g., "Aura", "Kai", "Cascade")
 * @param text The message text content
 * @param timestamp Message timestamp in milliseconds
 */
data class ConferenceMessage(
    val agent: String,
    val text: String,
    val timestamp: Long
)

/**
 * Displays a single message bubble in the chat interface.
 *
 * Shows the agent name, message text, and applies color coding based on the agent.
 *
 * @param message The message to display
 */
@Composable
fun MessageBubble(message: ConferenceMessage) {
    val agentColor = when (message.agent) {
        "Aura" -> NeonPurple
        "Kai" -> NeonTeal
        "Cascade" -> NeonBlue
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = agentColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = message.agent,
                style = MaterialTheme.typography.labelSmall,
                color = agentColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

/**
 * Displays a preview of the ConferenceRoomScreen composable within a MaterialTheme for design-time inspection.
 */
@Composable
@Preview(showBackground = true)
fun ConferenceRoomScreenPreview() {
    MaterialTheme {
        ConferenceRoomScreen()
    }
}

