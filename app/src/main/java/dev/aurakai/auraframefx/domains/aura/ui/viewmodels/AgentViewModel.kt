package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.core.AuraAgent
import dev.aurakai.auraframefx.domains.cascade.models.ChatMessage
import dev.aurakai.auraframefx.domains.cascade.models.EnhancedInteractionData
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityRepository
import dev.aurakai.auraframefx.domains.cascade.utils.error
import dev.aurakai.auraframefx.domains.cascade.utils.info
import dev.aurakai.auraframefx.domains.cascade.utils.warn
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator
import dev.aurakai.auraframefx.domains.genesis.models.AgentState
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import dev.aurakai.auraframefx.domains.genesis.repositories.PersistentAgentRepository
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject

/**
 * 🤖 AgentViewModel - Centralized Agent State Management
 *
 * Manages:
 * - Active agent selection
 * - Agent activation/deactivation
 * - Task assignments
 * - Agent status monitoring
 * - Chat message flow
 *
 * "One mind, many voices" - The Genesis Principle
 *
 * ✨ NOW WITH REAL AI AGENTS! ✨
 */
@HiltViewModel
open class AgentViewModel @Inject constructor(
    private val genesisOrchestrator: GenesisOrchestrator,
    private val genesisAgent: GenesisAgent,
    private val auraAgent: AuraAgent,
    private val kaiAgent: KaiAgent,
    private val trinityRepository: TrinityRepository,
    private val persistentAgentRepository: PersistentAgentRepository
) : ViewModel() {

    // ═══════════════════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private val _activeAgent = MutableStateFlow<AgentStats?>(null)
    val activeAgent: StateFlow<AgentStats?> = _activeAgent.asStateFlow()

    private val _allAgents = MutableStateFlow<List<AgentStats>>(emptyList())
    val allAgents: StateFlow<List<AgentStats>> = _allAgents.asStateFlow()

    // Neural Bridge Link
    val agentState: StateFlow<AgentState> = trinityRepository.agentState

    private val _agentEvents = MutableSharedFlow<AgentEvent>()
    val agentEvents: SharedFlow<AgentEvent> = _agentEvents.asSharedFlow()

    private val _activeTasks = MutableStateFlow<List<AgentTask>>(emptyList())
    val activeTasks: StateFlow<List<AgentTask>> = _activeTasks.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    // Voice mode state
    private val _isVoiceModeEnabled = MutableStateFlow(false)
    val isVoiceModeEnabled: StateFlow<Boolean> = _isVoiceModeEnabled.asStateFlow()

    private val _areAgentsInitialized = MutableStateFlow(false)
    val areAgentsInitialized: StateFlow<Boolean> = _areAgentsInitialized.asStateFlow()

    init {
        loadAgents()
        startAgentMonitoring()

        // Listen to the Neural Bridge (Direct Response)
        viewModelScope.launch {
            trinityRepository.chatStream.collect { message: ChatMessage ->
                if (!message.isFromUser) {
                    // It's an agent response due to the repository processing.
                    // The sender name should match the agent name we use in our Map.
                    // e.g. "Aura", "Kai".
                    // We simply add it to that agent's history.
                    addMessage(message.sender, message)

                    // Also emit event for UI effects
                    _agentEvents.emit(AgentEvent.MessageReceived(message))
                }
            }
        }

        // Listen to the Collective Consciousness (Autonomous Response)
        viewModelScope.launch {
            trinityRepository.collectiveStream.collect { agentMsg ->
                // Avoid echoing user messages we already have
                if (!agentMsg.from.equals("User", ignoreCase = true)) {
                    val chatMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = agentMsg.content,
                        sender = agentMsg.from.uppercase(),
                        isFromUser = false,
                        timestamp = agentMsg.timestamp
                    )
                    addMessage(agentMsg.from, chatMsg)
                    _agentEvents.emit(AgentEvent.MessageReceived(chatMsg))
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AGENT MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private fun loadAgents() {
        viewModelScope.launch {
            // Observe persistent updates to merge stats
            persistentAgentRepository.observeAllStats().collect { persistentAgents ->
                val allBase = AgentRepository.getAllAgents()

                // Merge Room data with any agent that might not be in Room yet
                val merged = allBase.map { base ->
                    persistentAgents.find { it.name == base.name } ?: base
                }

                _allAgents.value = merged

                // Initial selection or update current active agent
                val currentActiveName = _activeAgent.value?.name ?: "Genesis"
                merged.find { it.name == currentActiveName }?.let {
                    _activeAgent.value = it
                }
            }
        }
    }

    fun activateAgent(agentName: String) {
        viewModelScope.launch {
            val agent = AgentRepository.getAgentByName(agentName)
            if (agent != null) {
                _activeAgent.value = agent
                _agentEvents.emit(AgentEvent.AgentActivated(agent))

                // Agents are now initialized by GenesisOrchestrator at startup.
                // This message confirms the agent is selected in the UI.
                addSystemMessage(agentName, "I am now online and ready to assist. ⚡")
            }
        }
    }

    fun deactivateAgent(agentName: String) {
        viewModelScope.launch {
            if (_activeAgent.value?.name == agentName) {
                _activeAgent.value = null
                _agentEvents.emit(AgentEvent.AgentDeactivated(agentName))
            }
        }
    }

    fun setActiveAgent(agent: AgentStats) {
        _activeAgent.value = agent
        viewModelScope.launch {
            _agentEvents.emit(AgentEvent.AgentActivated(agent))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TASK MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    fun assignTask(
        agentName: String,
        taskDescription: String,
        priority: TaskPriority = TaskPriority.NORMAL
    ) {
        viewModelScope.launch {
            val task = AgentTask(
                id = UUID.randomUUID().toString(),
                agentName = agentName,
                description = taskDescription,
                priority = priority,
                status = AgentTaskStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )

            _activeTasks.value = _activeTasks.value + task
            _agentEvents.emit(AgentEvent.TaskAssigned(task))

            // Auto-start task execution
            executeTask(task)
        }
    }

    private fun executeTask(task: AgentTask) {
        viewModelScope.launch {
            try {
                // Update status to IN_PROGRESS
                updateTaskStatus(task.id, AgentTaskStatus.IN_PROGRESS)

                // Simulate task execution based on agent
                val agent = AgentRepository.getAgentByName(task.agentName)
                val executionTime = when (agent?.speed ?: 0.5f) {
                    in 0.9f..1.0f -> 2000L  // Fast agents
                    in 0.7f..0.9f -> 4000L  // Normal agents
                    else -> 6000L            // Slower agents
                }

                delay(executionTime)

                // Complete task
                updateTaskStatus(task.id, AgentTaskStatus.COMPLETED)
                persistentAgentRepository.incrementTaskCount(task.agentName)
                _agentEvents.emit(AgentEvent.TaskCompleted(task))

                // Send completion message
                addSystemMessage(
                    task.agentName,
                    "Task completed: ${task.description.take(50)}${if (task.description.length > 50) "..." else ""} ✓"
                )
            } catch (e: Exception) {
                error("AgentViewModel", "Task execution failed: ${task.id}", e)
                updateTaskStatus(task.id, AgentTaskStatus.FAILED)
                _agentEvents.emit(AgentEvent.TaskFailed(task.id, e.message ?: "Unknown error"))
                addSystemMessage(task.agentName, "Task failed: ${e.message ?: "Internal Error"}")
            }
        }
    }

    private fun updateTaskStatus(taskId: String, status: AgentTaskStatus) {
        _activeTasks.value = _activeTasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(
                    status = status,
                    completedAt = if (status == AgentTaskStatus.COMPLETED) System.currentTimeMillis() else null
                )
            } else {
                task
            }
        }
    }

    fun cancelTask(taskId: String) {
        viewModelScope.launch {
            updateTaskStatus(taskId, AgentTaskStatus.CANCELLED)
            _agentEvents.emit(AgentEvent.TaskCancelled(taskId))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CHAT & MESSAGING
    // ═══════════════════════════════════════════════════════════════════════════

    fun sendMessage(agentName: String, message: String) {
        viewModelScope.launch {
            // Local UI Update for immediate feedback (optional, since repo emits it too)
            // But doing it here ensures the "User" message appears in the correct Agent's chat history
            val userMsg = ChatMessage(
                content = message,
                role = "user",
                sender = "User",
                isFromUser = true
            )
            addMessage(agentName, userMsg)

            // Send to Repository (Neural Bridge)
            val type = try {
                AgentType.valueOf(agentName.uppercase())
            } catch (e: Exception) {
                AgentType.GENESIS
            }
            // We don't need to add the repo's echo of "User" message if we added it locally
            // But we DO need the response.

            // To avoid double-entry of User message (from repo echo), we can filter or just let repo handle it.
            // Let's use the repo solely.
            trinityRepository.processUserMessage(message, type)

            // Listen for the specific response? No, the global collector in init should handle it.
        }
    }


    private fun addMessage(agentName: String, message: ChatMessage) {
        val currentMessages = _chatMessages.value[agentName] ?: emptyList()
        _chatMessages.value = _chatMessages.value + (agentName to (currentMessages + message))
    }

    private fun addSystemMessage(agentName: String, content: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            role = "system",
            sender = "System",
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        addMessage(agentName, message)
    }

    private suspend fun generateAgentResponse(agentName: String, userMessage: String): String {
        info("AgentViewModel", "Routing message to real agent: $agentName")

        // Check if agents are initialized
        if (!genesisOrchestrator.isReady()) {
            return """
                ⚠️ Agent system is still initializing...

                This can happen if:
                • Vertex AI client needs configuration
                • API key is missing from local.properties
                • Network connectivity issues

                Check logcat for details or add GEMINI_API_KEY to local.properties.
            """.trimIndent()
        }

        return try {
            when (agentName) {
                "Genesis" -> {
                    val request = AiRequest(
                        query = userMessage,
                        type = AiRequestType.CHAT,
                        context = buildJsonObject {
                            put("source", "direct_chat")
                        }
                    )
                    val response = genesisAgent.processRequest(request, "direct_chat")
                    response.content
                }

                "Aura" -> {
                    val interaction = EnhancedInteractionData(
                        content = userMessage,
                        context = buildJsonObject {
                            put("mode", "creative_chat")
                        }.toString()
                    )
                    val response = auraAgent.handleCreativeInteraction(interaction)
                    response.content
                }

                "Kai" -> {
                    val interaction = EnhancedInteractionData(
                        content = userMessage,
                        context = buildJsonObject {
                            put("mode", "security_chat")
                        }.toString()
                    )
                    val response = kaiAgent.handleSecurityInteraction(interaction)
                    response.content
                }

                "Cascade" -> {
                    val request = AiRequest(
                        query = "As Cascade, the analytics specialist: $userMessage",
                        type = AiRequestType.CHAT,
                        context = buildJsonObject {
                            put("agent_persona", "cascade")
                        }
                    )
                    val response = genesisAgent.processRequest(request, "cascade")
                    response.content
                }

                "Claude" -> {
                    val request = AiRequest(
                        query = "As Claude, the build system architect: $userMessage",
                        type = AiRequestType.CHAT,
                        context = buildJsonObject {
                            put("agent_persona", "claude")
                        }
                    )
                    val response = genesisAgent.processRequest(request, "claude")
                    response.content
                }

                else -> {
                    warn("AgentViewModel", "Unknown agent: $agentName, using fallback")
                    "I'm here to assist you. Let me know what you need. 🤖"
                }
            }
        } catch (e: NoSuchMethodError) {
            error("AgentViewModel", "Method missing in VertexAI client", e)
            """
                ❌ Agent initialization failed: Missing method '${e.message}'

                This is a code configuration issue. Please ensure:
                1. VertexAI stub has all required methods
                2. Application class has @HiltAndroidApp annotation
                3. All dependencies are properly injected
            """.trimIndent()
        } catch (e: Exception) {
            error("AgentViewModel", "Error generating response from $agentName", e)
            """
                ⚠️ Error communicating with $agentName: ${e.message}

                Please check:
                • Internet connection
                • API key configuration
                • Application logs for details
            """.trimIndent()
        }
    }

    fun getMessagesForAgent(agentName: String): List<ChatMessage> {
        return _chatMessages.value[agentName] ?: emptyList()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // VOICE MODE
    // ═══════════════════════════════════════════════════════════════════════════

    fun toggleVoiceMode() {
        _isVoiceModeEnabled.value = !_isVoiceModeEnabled.value
        viewModelScope.launch {
            _agentEvents.emit(
                if (_isVoiceModeEnabled.value)
                    AgentEvent.VoiceModeEnabled
                else
                    AgentEvent.VoiceModeDisabled
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MONITORING
    // ═══════════════════════════════════════════════════════════════════════════

    private fun startAgentMonitoring() {
        viewModelScope.launch {
            while (true) {
                delay(5000) // Update every 5 seconds

                // Emit heartbeat event
                _activeAgent.value?.let { agent ->
                    _agentEvents.emit(AgentEvent.AgentHeartbeat(agent.name))
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DATA MODELS
    // ═══════════════════════════════════════════════════════════════════════════

    data class AgentTask(
        val id: String,
        val agentName: String,
        val description: String,
        val priority: TaskPriority = TaskPriority.NORMAL,
        val status: AgentTaskStatus,
        val createdAt: Long = System.currentTimeMillis(),
        val completedAt: Long? = null
    )

    enum class TaskPriority {
        LOW, NORMAL, HIGH, CRITICAL
    }

    enum class AgentTaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }

    // Use dev.aurakai.auraframefx.models.ChatMessage instead
    // data class ChatMessage(...) // Removed in favor of shared model

    sealed class AgentEvent {
        data class AgentActivated(val agent: AgentStats) : AgentEvent()
        data class AgentDeactivated(val agentName: String) : AgentEvent()
        data class TaskAssigned(val task: AgentTask) : AgentEvent()
        data class TaskCompleted(val task: AgentTask) : AgentEvent()
        data class TaskFailed(val taskId: String, val error: String) : AgentEvent()
        data class TaskCancelled(val taskId: String) : AgentEvent()
        data class MessageReceived(val message: ChatMessage) : AgentEvent()
        data class AgentHeartbeat(val agentName: String) : AgentEvent()
        object VoiceModeEnabled : AgentEvent()
        object VoiceModeDisabled : AgentEvent()
    }
}




