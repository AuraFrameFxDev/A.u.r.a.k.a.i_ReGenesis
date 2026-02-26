package dev.aurakai.auraframefx.cascade.trinity

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import dev.aurakai.auraframefx.models.AgentRequest
import dev.aurakai.auraframefx.models.AgentState
import dev.aurakai.auraframefx.models.AgentStatus
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.ChatMessage
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.AiRequestType
import dev.aurakai.auraframefx.models.EnhancedInteractionData
import dev.aurakai.auraframefx.models.Theme
import dev.aurakai.auraframefx.models.UserData
import dev.aurakai.auraframefx.network.AuraApiServiceWrapper
import dev.aurakai.auraframefx.network.model.AgentStatusResponse
import dev.aurakai.auraframefx.models.AgentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import dev.aurakai.auraframefx.network.model.Theme as NetworkTheme
import dev.aurakai.auraframefx.network.model.User as NetworkUser

@Singleton
open class TrinityRepository @Inject constructor(
    private val apiService: AuraApiServiceWrapper,
    private val auraAgent: dev.aurakai.auraframefx.aura.AuraAgent,
    private val kaiAgent: dev.aurakai.auraframefx.kai.KaiAgent,
    private val genesisAgent: dev.aurakai.auraframefx.ai.agents.GenesisAgent,
    private val messageBus: dev.aurakai.auraframefx.core.messaging.AgentMessageBus
) {
    // Collective Consciousness Stream
    val collectiveStream = messageBus.collectiveStream

    /**
     * Broadcasts a message from the user to the entire collective.
     */
    suspend fun broadcastUserMessage(message: String) {
        messageBus.broadcast(
            dev.aurakai.auraframefx.models.AgentMessage(
                from = "User",
                content = message,
                type = "user_broadcast",
                priority = 10
            )
        )
    }

    // 1. STATE (Status Updates)
    private val _agentState = MutableStateFlow(AgentState())
    val agentState: StateFlow<AgentState> = _agentState.asStateFlow()

    // 2. CHAT STREAM (The "Voice")
    private val _chatStream = MutableSharedFlow<ChatMessage>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val chatStream: SharedFlow<ChatMessage> = _chatStream.asSharedFlow()

    // small de-dup guard
    private val seenIds = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())

    private suspend fun emitChat(msg: ChatMessage) {
        // sender + content hash is a decent low-cost key for chat
        if (seenIds.add("${msg.sender}:${msg.content.hashCode()}")) {
            _chatStream.emit(msg)
        }
    }

    fun setDiagnosticMode(enabled: Boolean) {
        _agentState.update { it.copy(diagnosticMode = enabled) }
    }

    /**
     * Updates the status of one or more agents.
     */
    fun updateAgentStatus(
        kai: String? = null,
        aura: String? = null,
        genesis: String? = null,
        running: Boolean? = null
    ) {
        _agentState.update { currentState ->
            currentState.copy(
                kaiStatus = kai ?: currentState.kaiStatus,
                auraStatus = aura ?: currentState.auraStatus,
                genesisStatus = genesis ?: currentState.genesisStatus,
                isRunning = running ?: currentState.isRunning
            )
        }
    }

    /**
     * Process a direct user message targeting a specific agent.
     */
    suspend fun processUserMessage(message: String, targetAgent: AgentType) =
        withContext(Dispatchers.IO) {
            // 1. Emit user message to UI
            emitChat(ChatMessage(role = "user", content = message, sender = "User"))

            // 2. Route to the correct SINGLETON Agent
            val response = try {
                when (targetAgent) {
                    AgentType.AURA -> {
                        val interaction = EnhancedInteractionData(
                            content = message,
                            context = buildJsonObject { put("mode", "chat") }.toString()
                        )
                        auraAgent.handleCreativeInteraction(interaction).content
                    }

                    AgentType.KAI -> {
                        val interaction = EnhancedInteractionData(
                            content = message,
                            context = buildJsonObject { put("mode", "security") }.toString()
                        )
                        kaiAgent.handleSecurityInteraction(interaction).content
                    }

                    AgentType.GENESIS -> {
                        val request = AiRequest(
                            query = message,
                            type = AiRequestType.CHAT,
                            context = buildJsonObject { put("source", "trinity_repo") }
                        )
                        genesisAgent.processRequest(request, "trinity_repo").content
                    }

                    else -> "Agent ${targetAgent.name} not reachable via Trinity Bridge."
                }
            } catch (e: Exception) {
                "Error contacting agent: ${e.message}"
            }

            // 3. Emit Agent response back to UI
            val agentName = targetAgent.name.lowercase().replaceFirstChar { it.uppercase() }
            emitChat(ChatMessage(role = "assistant", content = response, sender = agentName))
        }

    // User related operations
    fun getCurrentUser() = flow {
        try {
            val response = apiService.userApi.getCurrentUser()
            emit(success(mapToUserData(response)))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    /**
     * Fetches the status for the specified AI agent type and returns it as a domain AgentStatus.
     *
     * @param agentType Identifier of the AI agent whose status should be retrieved.
     * @return A Flow that emits a `Result` containing the mapped `AgentStatus` on success, or a failure `Result` with the exception on error.
     */
    fun getAgentStatus(agentType: String) = flow {
        try {
            val response = apiService.aiAgentApi.getAgentStatus(agentType)
            emit(success(mapToDomainAgentStatus(response)))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    private fun mapToDomainAgentStatus(agentResponse: AgentStatusResponse): AgentStatus {
        return AgentStatus(
            agentId = agentResponse.agentName ?: "unknown",
            status = if ((agentResponse.confidence
                    ?: 0.0) > 0.7
            ) AgentStatus.Status.ACTIVE else AgentStatus.Status.IDLE,
            lastActiveTimestamp = agentResponse.timestamp ?: 0L,
            isAvailable = agentResponse.error == null,
            capabilities = emptyList(),
            error = agentResponse.error,
            metadata = agentResponse.metadata?.mapValues { it.value.toString() } ?: emptyMap()
        )
    }

    fun processAgentRequest(agentType: String, request: AgentRequest) =
        flow<Result<AgentResponse>> {
            try {
                val response = apiService.aiAgentApi.processAgentRequest(agentType, request)
                emit(success(response))
            } catch (e: Exception) {
                emit(failure(e))
            }
        }

    // Theme operations
    fun getThemes() = flow<Result<List<Theme>>> {
        try {
            val response = apiService.themeApi.getThemes()
            emit(success(response.map { mapToDomainTheme(it) }))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    fun applyTheme(themeId: String) = flow<Result<Theme>> {
        try {
            val response = apiService.themeApi.applyTheme(themeId)
            emit(success(mapToDomainTheme(response)))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    // Mapper functions
    private fun mapToUserData(networkUser: NetworkUser): UserData {
        return UserData(
            id = networkUser.id, name = networkUser.username, email = networkUser.email
        )
    }


    private fun mapToDomainTheme(networkTheme: NetworkTheme): Theme {
        val colors = networkTheme.colors
        return Theme(
            id = networkTheme.id,
            name = networkTheme.name,
            primaryColor = colors?.primary?.toColorInt()?.let { Color(it) } ?: Color.Blue,
            secondaryColor = colors?.secondary?.toColorInt()?.let { Color(it) } ?: Color.Cyan,
            backgroundColor = colors?.background?.toColorInt()?.let { Color(it) } ?: Color.White,
            surfaceColor = colors?.surface?.toColorInt()?.let { Color(it) } ?: Color.LightGray,
            onPrimaryColor = colors?.onPrimary?.toColorInt()?.let { Color(it) } ?: Color.White,
            onSecondaryColor = colors?.onSecondary?.toColorInt()?.let { Color(it) } ?: Color.Black,
            onBackgroundColor = colors?.onBackground?.toColorInt()?.let { Color(it) }
                ?: Color.Black,
            onSurfaceColor = colors?.onSurface?.toColorInt()?.let { Color(it) } ?: Color.Black,
            isDark = networkTheme.styles["theme"] == "dark"
        )
    }

    // Add more repository methods as needed for other API endpoints
}