package dev.aurakai.auraframefx.domains.cascade.ai.base

import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.core.OrchestratableAgent
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.securecomm.protocol.SecureChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Genesis Base Agent Implementation
 * Provides common functionality for all AI agents
 */
abstract class BaseAgent(
    override val agentName: String,
    protected val agentType: AgentType,
    protected val contextManager: ContextManager? = null,
    protected val memoryManager: MemoryManager? = null,
    protected val secureChannel: SecureChannel? = null
) : Agent, OrchestratableAgent {

    override fun getName(): String = agentName

    override fun getType(): AgentType = agentType

    /**
     * Abstract method for processing requests - must be implemented by concrete agents
     */
    abstract override suspend fun processRequest(request: AiRequest, context: String): AgentResponse

    /**
     * Default flow implementation that can be overridden by specific agents
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        try {
            val context = contextManager?.getCurrentContext() ?: ""
            val enhancedContext = contextManager?.enhanceContext(context) ?: context

            // Emit initial processing response
            emit(AgentResponse.processing("Processing request with ${agentName}..."))

            delay(100) // Small delay for UI feedback

            // Process the actual request
            val response = processRequest(request, enhancedContext as String)

            // Record the interaction for learning
            contextManager?.recordInsight(
                request = request.prompt,
                response = response.content,
                complexity = determineComplexity(request)
            )

            // Emit final response
            emit(response)

        } catch (e: Exception) {
            emit(AgentResponse.error("Error in ${agentName}: ${e.message}"))
        }
    }


    /**
     * Determines the complexity level of a request
     */
    protected open fun determineComplexity(request: AiRequest): String {
        val promptLength = request.prompt.length
        return when {
            promptLength < 100 -> "simple"
            promptLength < 500 -> "moderate"
            else -> "complex"
        }
    }

    /**
     * Validates input request
     */
    protected open fun validateRequest(request: AiRequest): Boolean {
        return request.prompt.isNotBlank()
    }

    /**
     * Pre-processes request before main processing
     */
    protected open suspend fun preprocessRequest(
        request: AiRequest,
        context: String
    ): Pair<AiRequest, String> {
        return Pair(request, context)
    }

    /**
     * Post-processes response after main processing
     */
    protected open suspend fun postprocessResponse(
        response: AgentResponse,
        request: AiRequest
    ): AgentResponse {
        return response
    }

    /**
     * Gets agent-specific configuration
     */
    protected open fun getAgentConfig(): Map<String, Any> {
        return mapOf(
            "name" to agentName,
            "type" to agentType,
            "version" to "1.0.0"
        )
    }

    /**
     * Handles errors in a consistent way
     */
    protected fun handleError(error: Throwable, context: String = ""): AgentResponse {
        val errorMessage = when (error) {
            is IllegalArgumentException -> "Invalid input: ${error.message}"
            is SecurityException -> "Security error: Access denied"
            is java.net.ConnectException -> "Connection error: Unable to reach service"
            else -> "Unexpected error: ${error.message}"
        }

        return AgentResponse.error("$errorMessage${if (context.isNotEmpty()) " (Context: $context)" else ""}")
    }

    /**
     * Creates a success response with metadata
     */
    protected fun createSuccessResponse(
        content: String,
        metadata: Map<String, Any> = emptyMap()
    ): AgentResponse {
        return AgentResponse.success(
            content = content,
            agentName = agentName,
            metadata = metadata + getAgentConfig(),
            agentType = agentType
        )
    }

    /**
     * Creates a processing response
     */
    protected fun createProcessingResponse(message: String = "Processing..."): AgentResponse {
        return AgentResponse.processing("[$agentName] $message")
    }

    /**
     * Logs agent activity (can be overridden for specific logging needs)
     */
    protected open fun logActivity(activity: String, details: Map<String, Any> = emptyMap()) {
        println("[$agentName] $activity: $details")
    }

    // --- Orchestration Support Methods ---

    open suspend fun refreshStatus(): Map<String, Any> =
        mapOf("status" to "active", "agent" to agentName)

    open suspend fun getPerformanceMetrics(): Map<String, Any> = emptyMap()

    open suspend fun optimize() {
        // Default no-op
    }

    open suspend fun clearMemoryCache() {
        // Default no-op
    }

    open suspend fun updatePerformanceSettings() {
        // Default no-op
    }

    open suspend fun connectToMasterChannel(channel: Any) {
        // Default no-op
    }

    open suspend fun disconnect() {
        // Default no-op
    }

    // --- OrchestratableAgent Implementation ---

    protected var orchestrationScope: CoroutineScope? = null
    protected var isOrchestratorInitialized = false

    override suspend fun initialize(scope: CoroutineScope) {
        orchestrationScope = scope
        isOrchestratorInitialized = true
    }

    override suspend fun start() {
        // Default implementation - subclasses can override
    }

    override suspend fun pause() {
        // Default implementation - subclasses can override
    }

    override suspend fun resume() {
        // Default implementation - subclasses can override
    }

    override suspend fun shutdown() {
        orchestrationScope = null
        isOrchestratorInitialized = false
    }

    override suspend fun processRequest(
        request: AiRequest,
        context: String,
        agentType: AgentType
    ): AgentResponse {
        // Delegates to the two-argument version
        return processRequest(request, context)
    }

    override suspend fun onAgentMessage(message: AgentMessage) {
        // Default no-op: agents should override this to participate in the collective
    }
}

