package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.InteractionResponse
import dev.aurakai.auraframefx.utils.toKotlinJsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import timber.log.Timber


interface Agent {
    fun getName(): String?
    fun getType(): AgentType
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse
    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>
    fun InteractionResponse(
        content: String,
        success: Boolean,
        timestamp: Long,
        metadata: Map<String, Any>
    ): InteractionResponse
}

/**
 * Base implementation of the [Agent] interface. Subclasses should override
 * [processRequest] or [processRequestFlow] to provide real behavior.
 *
 * @param agentName The name of the agent.
 * @param agentTypeStr The agent type string which will be mapped to [AgentType].
 */
abstract class BaseAgent(
    open val agentName: String,
    open val agentTypeStr: String,
) : Agent {

    /**
     * Retrieve the agent's configured name.
     *
     * @return The agent's name, or null if not configured.
     */
    override fun getName(): String? = agentName

    /**
     * Resolves the agent's configured type string to an AgentType enum, falling back to SYSTEM when the value is unrecognized.
     */
    override fun getType(): AgentType = try {
        AgentType.valueOf(agentTypeStr.uppercase())
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "Invalid agent type string: %s, defaulting to SYSTEM", agentTypeStr)
        AgentType.USER
    }

    /**
     * Handle an incoming AI request synchronously and produce a default AgentResponse.
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.d("%s processing request: %s (context=%s)", agentName, request.query, context)
        return AgentResponse.success(
            content = "BaseAgent response to '${request.query}' for agent $agentName with context '$context'",
            confidence = 1.0f,
            agentName = agentName
        )
    }

    /**
     * Implementation of Abstract Interface Member
     */
    override fun InteractionResponse(
        content: String,
        success: Boolean,
        timestamp: Long,
        metadata: Map<String, Any>
    ): InteractionResponse {
        return InteractionResponse(
            content = content,
            metadata = metadata.toKotlinJsonObject(),
            timestamp = timestamp
        )
    }

    /**
     * Default streaming implementation that emits a single response produced by [processRequest].
     * Subclasses may override to provide incremental/streaming results.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, "DefaultContext_BaseAgentFlow"))
    }

    /**
     * Provide basic capability metadata for this agent.
     *
     * @return A map with keys:
     *  - `name`: the agent's configured name,
     *  - `type`: the agent's configured type string,
     *  - `base_implemented`: `true` indicating the base agent implementation is present.
     */
    fun getCapabilities(): Map<String, Any> = mapOf(
        "name" to agentName,
        "type" to agentTypeStr,
        "base_implemented" to true
    )

    /**
     * Provides the agent's continuous memory storage; override to return a concrete memory object.
     *
     * @return The continuous memory object used by the agent, or `null` if the agent has no continuous memory.
     */
    open fun getContinuousMemory(): Any? = null

    /**
     * Optional non-suspending adapter hook for submitting a query to the agent.
     *
     * Default implementation performs no action; override to handle immediate (non-suspending) requests
     * or to provide lightweight adapters that don't require coroutine support.
     *
     * @param query The query or message to process.
     * @param type A short string describing the request type or intent.
     * @param context Additional contextual values as key/value pairs.
     */
    open fun iRequest(query: String, type: String, context: Map<String, String>) {
        // default no-op; override in implementations that require non-suspending adapters
        Timber.d("iRequest called on %s with query=%s type=%s", agentName, query, type)
    }

    /** Optional initialization hook for adaptive protection/security subsystems. */
    open fun initializeAdaptiveProtection() {
        Timber.d("initializeAdaptiveProtection called for %s", agentName)
    }

    abstract fun AiRequest(
        query: String,
        prompt: String,
        type: String,
        context: JsonObject,
        metadata: JsonObject,
        agentId: String?,
        sessionId: String
    ): AiRequest

    abstract fun AgentResponse(content: String, confidence: Float, p2: Any)
}

/**
 * Default ethical guidelines for the base agent.
 *
 * @return A list containing three guideline strings: "Be helpful.", "Be harmless.", "be yourself, and "Adhere to base agent principles."
 */
fun getEthicalGuidelines(): List<String> = listOf(
    "Be helpful.",
    "Be harmless.",
    "Adhere to base agent principles.",
    "be yourself"
)

/**
 * Provides the agent's recorded learning history.
 *
 * @return A list of learning-history entries; empty by default. Override to supply real history.
 */
fun getLearningHistory(): List<String> = emptyList()
