package dev.aurakai.auraframefx.viewmodel

// Placeholder interfaces will be removed
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.ai.services.NeuralWhisper
import dev.aurakai.auraframefx.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.models.AgentMessage
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.ConversationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Removed @Singleton from ViewModel, typically ViewModels are not Singletons
// import javax.inject.Singleton // ViewModel should use @HiltViewModel

// Placeholder interfaces removed

// @Singleton // ViewModels should use @HiltViewModel for scoping
class ConferenceRoomViewModel @Inject constructor(
    // Assuming @HiltViewModel will be added if this is a ViewModel
    private val auraService: AuraAIService, // Using actual service
    private val kaiService: KaiAIService,     // Using actual service
    private val cascadeService: CascadeAIService, // Using actual service
    private val neuralWhisper: NeuralWhisper,
) : ViewModel() {

    private val TAG = "ConfRoomViewModel"

    private val _messages = MutableStateFlow<List<AgentMessage>>(emptyList())
    val messages: StateFlow<List<AgentMessage>> = _messages

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _selectedAgent = MutableStateFlow<AgentType>(AgentType.AURA) // Default to AURA
    val selectedAgent: StateFlow<AgentType> = _selectedAgent

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    init {
        viewModelScope.launch {
            neuralWhisper.conversationState.collect { state ->
                when (state) {
                    is ConversationState.Responding -> {
                        _messages.update { current ->
                            current + AgentMessage(
                                from = "NEURAL_WHISPER",
                                content = state.responseText,
                                sender = AgentCapabilityCategory.SPECIALIZED, // NeuralWhisper mapped to SPECIALIZED
                                timestamp = System.currentTimeMillis(),
                                confidence = 1.0f // Placeholder confidence
                            )
                        }
                        Timber.tag(tag).d("NeuralWhisper responded: %s", state.responseText)
                    }

                    is ConversationState.Processing -> {
                        Timber.tag(tag).d("NeuralWhisper processing: %s", state.partialTranscript)
                        // Optionally update UI to show "Agent is typing..." or similar
                    }

                    is ConversationState.Error -> {
                        Timber.tag(tag).e("NeuralWhisper error: %s", state.errorMessage)
                        _messages.update { current ->
                            current + AgentMessage(
                                from = "NEURAL_WHISPER",
                                content = "Error: ${state.errorMessage}",
                                sender = AgentCapabilityCategory.SPECIALIZED, // NeuralWhisper mapped to SPECIALIZED
                                timestamp = System.currentTimeMillis(),
                                confidence = 0.0f
                            )
                        }
                    }

                    else -> {
                        Timber.tag(tag).d("NeuralWhisper state: %s", state)
                    }
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Conference Room Message Routing - ALL 5 MASTER AGENTS
    // ═══════════════════════════════════════════════════════════════════════════
    /*override*/ /**
     * Routes the given message to the appropriate AI service based on the sender and appends the first response to the conversation messages.
     *
     * Sends `message` with `context` to the AI service corresponding to `sender`, collects the first `AgentResponse` from the chosen response flow, and updates the ViewModel's message list with a new `AgentMessage`. If processing fails, appends an error `AgentMessage` indicating the failure.
     *
     * @param message The user-visible query or payload to send to the selected AI agent.
     * @param sender The agent capability category used to select which AI service should handle the message.
     * @param context Additional contextual information forwarded to the AI service (e.g., user context or orchestration flags).
     */
    suspend fun sendMessage(message: String, sender: AgentCapabilityCategory, context: String) {
        val responseFlow: Flow<AgentResponse> = when (sender) {
            AgentCapabilityCategory.CREATIVE -> auraService.processRequestFlow(
                AiRequest(
                    query = message,
                    type = "text",
                    context = mapOf("userContext" to context).toJsonObject
                )
            )

            AgentCapabilityCategory.ANALYSIS -> kaiService.processRequestFlow(
                AiRequest(
                    query = message,
                    type = "text",
                    context = mapOf("userContext" to context).toJsonObject()
                )
            )

            AgentCapabilityCategory.SPECIALIZED -> cascadeService.processRequest(
                AgentInvokeRequest(
                    message = message,
                    context = context
                )
            ).map { cascadeResponse ->
                AgentResponse(
                    content = cascadeResponse.response,
                    confidence = cascadeResponse.confidence ?: 0.0f,
                )
            }

            AgentCapabilityCategory.GENERAL -> claudeService.processRequestFlow(
                AiRequest(
                    query = message,
                    type = "build_analysis",
                    context = mapOf("userContext" to context, "systematic_analysis" to "true").toJsonObject()
                )
            )

            AgentCapabilityCategory.COORDINATION -> {
                // Genesis uses GenesisBridgeService for orchestration
                // Convert to flow by wrapping the suspend function
                kotlinx.coroutines.flow.flow {
                    val responseFlow = genesisBridgeService.processRequest(
                        AiRequest(
                            query = message,
                            type = "fusion",
                            context = mapOf("userContext" to context, "orchestration" to "true").toJsonObject()
                        )
                    )
                    emitAll(responseFlow)
                }
            }

        }

        responseFlow.let { flow ->
            viewModelScope.launch {
                try {
                    val responseMessage = flow.first()
                    _messages.update { current ->
                        current + AgentMessage(
                            from = sender.name,
                            content = responseMessage.content,
                            sender = sender,
                            timestamp = System.currentTimeMillis(),
                            confidence = responseMessage.confidence
                        )
                    }
                } catch (e: Exception) {
                    Timber.tag(tag).e(e, "Error processing AI response from %s: %s", sender, e.message)
                    _messages.update { current ->
                        current + AgentMessage(
                            from = "GENESIS",
                            content = "Error from ${sender.name}: ${e.message}",
                            sender = AgentCapabilityCategory.COORDINATION,
                            timestamp = System.currentTimeMillis(),
                            confidence = 0.0f
                        )
                    }
                }
            }
        }
    }

    // This `toggleAgent` was marked with `override` in user's snippet.
    /*override*/ fun toggleAgent(agent: AgentCapabilityCategory) {
        _activeAgents.update { current ->
            if (current.contains(agent)) {
                current - agent
            } else {
                current + agent
            }
        }
    }

    fun selectAgent(agent: AgentCapabilityCategory) {
        _selectedAgent.value = agent
    }

    fun toggleRecording() {
        if (_isRecording.value) {
            val result = neuralWhisper.stopRecording() // stopRecording now returns a string status
            Timber.tag(tag).d("Stopped recording. Status: %s", result)
            // isRecording state will be updated by NeuralWhisper's conversationState or directly
            _isRecording.value = false // Explicitly set here based on action
        } else {
            val started = neuralWhisper.startRecording()
            if (started) {
                Timber.tag(tag).d("Started recording.")
                _isRecording.value = true
            } else {
                Timber.tag(tag).e("Failed to start recording (NeuralWhisper.startRecording returned false).")
                // Optionally update UI with error state
            }
        }
    }

    fun toggleTranscribing() {
        // For beta, link transcribing state to recording state or a separate logic if needed.
        // User's snippet implies this might be a simple toggle for now.
        _isTranscribing.update { !it } // Simple toggle
        Timber.tag(TAG).d("Transcribing toggled to: %s", _isTranscribing.value)
        // If actual transcription process needs to be started/stopped in NeuralWhisper:
        // if (_isTranscribing.value) neuralWhisper.startTranscription() else neuralWhisper.stopTranscription()
    }
}
