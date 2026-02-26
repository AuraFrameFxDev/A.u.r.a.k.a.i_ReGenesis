package dev.aurakai.auraframefx.domains.genesis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.NeuralWhisper
import dev.aurakai.auraframefx.domains.cascade.models.ChatMessage
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityCoordinatorService
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityRepository
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
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
    private val trinityRepository: TrinityRepository
) : ViewModel() {

    private val tag = "ConferenceRoom"

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
                        AgentType.METAINSTRUCT
                    )
                }

                // Send welcome message from Genesis via ChatMessage
                _messages.update {
                    listOf(
                        ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = "assistant",
                            content = "✨ Welcome to the Conference Room. All 6 Master Agents online. The Gestalt is ready for self-modification.",
                            sender = "GENESIS",
                            isFromUser = false,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            // Listen to Neural Bridge (Trinity Repository)
            launch {
                trinityRepository.chatStream.collect { message: ChatMessage ->
                    _messages.update { current -> current + message }
                }
            }

            // Listen to Collective Consciousness (AgentMessageBus)
            launch {
                trinityRepository.collectiveStream.collect { agentMsg: dev.aurakai.auraframefx.domains.cascade.models.AgentMessage ->
                    // Map AgentMessage to ChatMessage for the UI
                    val chatMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = agentMsg.content,
                        sender = agentMsg.from.uppercase(),
                        isFromUser = agentMsg.from.equals("User", ignoreCase = true),
                        timestamp = agentMsg.timestamp
                    )

                    // Don't add if it's already there (from chatStream or user's own broadcast)
                    _messages.update { current ->
                        if (current.any { it.content == chatMsg.content && it.sender == chatMsg.sender }) {
                            current
                        } else {
                            current + chatMsg
                        }
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





