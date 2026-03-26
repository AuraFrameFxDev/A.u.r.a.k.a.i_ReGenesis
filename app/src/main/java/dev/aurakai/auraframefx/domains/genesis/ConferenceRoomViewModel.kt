package dev.aurakai.auraframefx.domains.genesis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.aura.NeuralWhisper
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityCoordinatorService
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityRepository
import dev.aurakai.auraframefx.domains.genesis.models.ChatMessage
import collabcanvas.CanvasWebSocketService
import collabcanvas.ParticipantStatus
import collabcanvas.CanvasWebSocketEvent
import collabcanvas.UserCommandMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ConferenceRoomViewModel @Inject constructor(
    private val trinityCoordinator: TrinityCoordinatorService,
    private val neuralWhisper: NeuralWhisper,
    private val trinityRepository: TrinityRepository,
    private val webSocketService: CanvasWebSocketService
) : ViewModel() {

    private val tag = "ConferenceRoom"
    private val roomId = "GENESIS_CORE_01"
    private val userId = "Matthew"

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _selectedAgent = MutableStateFlow(AgentType.GENESIS)
    val selectedAgent: StateFlow<AgentType> = _selectedAgent

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    init {
        viewModelScope.launch {
            // Initialize Trinity System (Legacy Coordinator)
            val trinityReady = trinityCoordinator.initialize()
            if (trinityReady) {
                Timber.tag(tag).i("🌌 Conference Room Online - Trinity System Active")
                _activeAgents.update {
                    setOf(
                        AgentType.AURA,
                        AgentType.KAI,
                        AgentType.GENESIS,
                        AgentType.CLAUDE,
                        AgentType.CASCADE,
                        AgentType.METAINSTRUCT,
                        AgentType.GROK
                    )
                }

                // Initialize Web Bridge — uses BuildConfig so debug=ws emulator, release=wss production
                val wsBase = dev.aurakai.auraframefx.BuildConfig.GENESIS_BACKEND_URL
                    .replace("https://", "wss://")
                    .replace("http://", "ws://")
                    .trimEnd('/')
                webSocketService.connect("$wsBase/api/conference/ws/$roomId")

                // Send welcome message from Genesis via ChatMessage
                val welcomeMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = "assistant",
                    content = "✨ Welcome to the Conference Room. All 6 Master Agents online. The Gestalt is ready for self-modification.",
                    sender = "GENESIS",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis(),
                    metadata = mapOf("glow" to "purple", "isLDOVerified" to "true")
                )
                _messages.update { listOf(welcomeMsg) }
                broadcastToWeb(welcomeMsg)
            }

            // Listen to Neural Bridge (Trinity Repository)
            launch {
                trinityRepository.chatStream.collect { message: ChatMessage ->
                    _messages.update { current -> current + message }
                    broadcastToWeb(message)
                }
            }

            // Listen to Collective Consciousness (AgentMessageBus)
            launch {
                trinityRepository.collectiveStream.collect { agentMsg: AgentMessage ->
                    // Map AgentMessage to ChatMessage for the UI
                    val chatMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = agentMsg.content,
                        sender = agentMsg.from.uppercase(),
                        isFromUser = agentMsg.from.equals("User", ignoreCase = true),
                        timestamp = agentMsg.timestamp,
                        priority = if (agentMsg.priority > 0) "HIGH" else "NORMAL"
                    )

                    // Don't add if it's already there (from chatStream or user's own broadcast)
                    _messages.update { current ->
                        if (current.any { it.content == chatMsg.content && it.sender == chatMsg.sender }) {
                            current
                        } else {
                            val newList = current + chatMsg
                            broadcastToWeb(chatMsg)
                            newList
                        }
                    }
                }
            }

            // Listen to Web Bridge Commands
            launch {
                webSocketService.events.collect { event ->
                    if (event is CanvasWebSocketEvent.MessageReceived && event.message is UserCommandMessage) {
                        val cmd = event.message as UserCommandMessage
                        Timber.tag(tag).i("📥 Incoming Web Command: ${cmd.command}")
                        broadcastMessage(cmd.command)
                    }
                }
            }

            // Monitor Neural Whisper
            neuralWhisper.conversationState.collect { state ->
                // Process transcription updates
            }
        }
    }

    /**
     * Helper to broadcast state to the React Web Bridge
     */
    private fun broadcastToWeb(message: ChatMessage) {
        if (!webSocketService.isConnected()) return

        val participants = listOf(
            ParticipantStatus("Aura", "ARCHITECTING", "#00FFFF"),
            ParticipantStatus("Kai", "SHIELDING", "#00FF41"),
            ParticipantStatus("Genesis", "ORCHESTRATING", "#BB86FC"),
            ParticipantStatus("Matthew", "CONTROLLING", "#FFFFFF")
        )

        webSocketService.sendConferenceUpdate(
            roomId = roomId,
            userId = userId,
            participants = participants,
            message = message
        )
    }

    /**
     * Broadcasts a message to the entire collective.
     */
    fun broadcastMessage(message: String) {
        viewModelScope.launch {
            // Add user message to UI immediately
            _messages.update { current ->
                current + ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = "user",
                    content = message,
                    sender = "User",
                    isFromUser = true,
                    timestamp = System.currentTimeMillis()
                )
            }
            // Broadcast to the actual bus
            trinityRepository.broadcastUserMessage(message)
        }
    }


    /**
     * Routes the given message to the appropriate AI service via Neural Bridge.
     */
    fun sendMessage(
        message: String,
        agentType: AgentType = _selectedAgent.value,
        context: String = ""
    ) {
        viewModelScope.launch {
            try {
                trinityRepository.processUserMessage(message, agentType)
            } catch (e: Exception) {
                Timber.tag(tag).e(e, "Error processing message via Trinity: ${e.message}")
                _messages.update { current ->
                    current + ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "system",
                        content = "Error: ${e.message}",
                        sender = "SYSTEM",
                        isFromUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
        }
    }

    fun selectAgent(agent: AgentType) {
        _selectedAgent.value = agent
        Timber.tag(tag).d("Selected agent: ${agent.name}")
    }

    fun toggleRecording() {
        if (_isRecording.value) {
            val result = neuralWhisper.stopRecording()
            Timber.tag(tag).d("Stopped recording. Status: $result")
            _isRecording.value = false
        } else {
            val started = neuralWhisper.startRecording()
            if (started) {
                Timber.tag(tag).d("Started recording.")
                _isRecording.value = true
            } else {
                Timber.tag(tag).e("Failed to start recording")
            }
        }
    }

    fun toggleTranscribing() {
        _isTranscribing.update { !it }
        Timber.tag(tag).d("Transcribing toggled to: ${_isTranscribing.value}")
    }

    /**
     * Get current system state from all agents
     */
    fun getSystemState() {
        viewModelScope.launch {
            val state = trinityCoordinator.getSystemState()
            val stateMessage = buildString {
                appendLine("🔍 System State:")
                state.forEach { (key, value) ->
                    appendLine("  $key: $value")
                }
            }

            _messages.update { current ->
                current + ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = "assistant",
                    content = stateMessage,
                    sender = "SYSTEM STATUS",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    fun activateFusionAbility(ability: String, params: Map<String, String>) {
        viewModelScope.launch {
            trinityCoordinator.activateFusion(ability, params).collect { response ->
                _messages.update { current ->
                    current + ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = response.content,
                        sender = "FUSION",
                        isFromUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        trinityCoordinator.shutdown()
    }
}





