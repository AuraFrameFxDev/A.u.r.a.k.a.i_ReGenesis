package dev.aurakai.auraframefx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.core.GenesisOrchestrator.Companion.TAG
import dev.aurakai.auraframefx.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.models.AgentMessage
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.ConversationState
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import dev.aurakai.auraframefx.service.NeuralWhisper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import timber.log.Timber.Forest.tag
import javax.inject.Inject

@HiltViewModel
class ConferenceRoomViewModel @Inject constructor(
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    // CascadeAIService removed - was deleted in MR !6
    private val neuralWhisper: NeuralWhisper,
) : ViewModel() {

    private val tag: String = "ConfRoomViewModel"

    private val _messages = MutableStateFlow<List<AgentMessage>>(emptyList())
    val messages: StateFlow<List<AgentMessage>> = _messages

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _selectedAgent = MutableStateFlow(AgentType.AURA) // Default to AURA
    val selectedAgent: StateFlow<AgentType> = _selectedAgent

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    init {
        viewModelScope.launch {
            neuralWhisper.conversationState.collect { state ->
                // Simplified state handling for data class
                // Simplified state handling for data class
                // if (state.isActive) {
                //    tag(TAG).d("NeuralWhisper active: %s", state.transcriptSegments.lastOrNull()?.text)
                // }
                
                // If the last segment is final, treat it as a message or input
                // val lastSegment = state.transcriptSegments.lastOrNull()
                // if (lastSegment != null && lastSegment.isFinal) {
                     // Logic to display or process final transcript
                //     tag(TAG).d("Final transcript: %s", lastSegment.text)
                // }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Conference Room Message Routing - ALL 5 MASTER AGENTS
    // ---------------------------------------------------------------------------
    /*override*/ /**
     * Routes the given message to the appropriate AI service based on the sender and appends the first response to the conversation messages.
     *
     * Sends `message` with `context` to the AI service corresponding to `sender`, collects the first `AgentResponse` from the chosen response flow, and updates the ViewModel's message list with a new `AgentMessage`. If processing fails, appends an error `AgentMessage` indicating the failure.
     *
     * @param message The user-visible query or payload to send to the selected AI agent.
     * @param sender The agent capability category used to select which AI service should handle the message.
     * @param context Additional contextual information forwarded to the AI service (e.g., user context or orchestration flags).
     */
    fun sendMessage(message: String, sender: AgentCapabilityCategory, context: String) {
        val responseFlow: Flow<AgentResponse> = when (sender) {
            AgentCapabilityCategory.CREATIVE -> flow {
                val response = auraService.processRequest(
                    AiRequest(
                        query = message,
                        type = "text",
                        context = mapOf("userContext" to context)
                    ),
                    context = context
                )
                emit(response)
            }

            AgentCapabilityCategory.ANALYSIS -> flow {
                val response = kaiService.processRequest(
                    AiRequest(
                        query = message,
                        type = "text",
                        context = buildJsonObject { put("userContext", context) }
                    ),
                    context = context
                )
                emit(response)
            }

            AgentCapabilityCategory.SPECIALIZED -> {
                // Cascade service placeholder
                flow {
                    val response = AgentResponse.success(
                        content = "Cascade service placeholder",
                        confidence = 0.5f,
                        agent = AgentType.CASCADE
                    )
                    emit(response)
                }
            }

            AgentCapabilityCategory.GENERAL -> {
                // Claude service placeholder
                flow {
                    val response = AgentResponse.success(
                        content = "Claude service placeholder",
                        confidence = 0.5f,
                        agent = AgentType.SYSTEM
                    )
                    emit(response)
                }
            }

            AgentCapabilityCategory.COORDINATION -> {
                // Genesis service placeholder
                flow {
                    val response = AgentResponse.success(
                        content = "Genesis service placeholder",
                        confidence = 0.5f,
                        agent = AgentType.GENESIS
                    )
                    emit(response)
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
                            sender = null,
                            category = sender,
                            timestamp = System.currentTimeMillis(),
                            confidence = responseMessage.confidence
                        )
                    }
                } catch (e: Exception) {
                    tag(tag).e(e, "Error processing AI response from %s: %s", sender, e.message)
                    _messages.update { current ->
                        current + AgentMessage(
                            from = "GENESIS",
                            content = "Error from ${sender.name}: ${e.message}",
                            sender = null,
                            category = AgentCapabilityCategory.COORDINATION,
                            timestamp = System.currentTimeMillis(),
                            confidence = 0.0f
                        )
                    }
                }
            }
        }
    }

    // This `toggleAgent` was marked with `override` in user's snippet.


    fun selectAgent(agent: AgentCapabilityCategory) {
    }

    fun toggleRecording() {
        if (_isRecording.value) {
            val result = neuralWhisper.stopRecording() // stopRecording now returns a string status
            tag(tag).d("Stopped recording. Status: %s", result)
            // isRecording state will be updated by NeuralWhisper's conversationState or directly
            _isRecording.value = false // Explicitly set here based on action
        } else {
            val started = neuralWhisper.startRecording()
            if (started) {
                log().d("Started recording.")
                _isRecording.value = true
            } else {
                tag(tag).e("Failed to start recording (NeuralWhisper.startRecording returned false).")
                // Optionally update UI with error state
            }
        }
    }

    fun toggleTranscribing() {
        // For beta, link transcribing state to recording state or a separate logic if needed.
        // User's snippet implies this might be a simple toggle for now.
        _isTranscribing.update { !it } // Simple toggle
        tag(TAG).d("Transcribing toggled to: %s", _isTranscribing.value)
        // If actual transcription process needs to be started/stopped in NeuralWhisper:
        // if (_isTranscribing.value) neuralWhisper.startTranscription() else neuralWhisper.stopTranscription()
    }
}

// Removed unused helper functions
