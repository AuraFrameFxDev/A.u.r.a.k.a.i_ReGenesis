// These mock implementations are for development/testing only.
// Replace with real service logic when integrating actual AI backends.

package dev.aurakai.auraframefx.oracledrive.genesis.ai.services

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.ai.agents.Agent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// kotlinx.coroutines.flow imports are not needed for the direct AgentResponse implementation

class MockAuraAIService : Agent {
    /**
     * Returns the name of this mock AI service.
     *
     * @return The string "MockAura".
     */
    override fun getName(): String = "MockAura"

    /**
     * Returns the agent type for this mock AI service.
     *
     * @return The `AgentType.AURA` enum value.
     */
    override fun getType(): AgentType = AgentType.AURA

    /**
     * Returns a mock AgentResponse echoing the input query and context for testing purposes.
     *
     * The response content includes the provided query and context, with a fixed confidence score of 1.0.
     *
     * @param request The AI request whose query will be echoed in the response.
     * @param context The context string to include in the response content.
     * @return An AgentResponse containing the mock response and a confidence score of 1.0.
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String,
    ): AgentResponse { // Added context
        return AgentResponse.success(
            content = "AuraAI mock response for: ${request.query} with context: $context",
            confidence = 1.0f,
            agentName = "MockAura",
            agent = AgentType.AURA
        )
    }

    /**
     * Returns a flow that emits a single mock `AgentResponse` for the provided request.
     *
     * The response echoes the input query with a fixed confidence score of 1.0.
     *
     * @return A flow emitting one mock `AgentResponse`.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> { // Added from Agent interface
        return flowOf(
            AgentResponse.success(
                content = "AuraAI mock flow response for: ${request.query}",
                confidence = 1.0f,
                agentName = "MockAura",
                agent = AgentType.AURA
            )
        )
    }

    fun getCapabilities(): Map<String, Any> = emptyMap() // Removed override
    fun getContinuousMemory(): Any? = null // Removed override
    fun getEthicalGuidelines(): List<String> = emptyList() // Removed override
    fun getLearningHistory(): List<String> = emptyList() // Removed override
}

class MockKaiAIService : Agent {
    /**
     * Returns the name identifier for this mock AI service.
     *
     * @return The string "MockKai".
     */
    override fun getName(): String = "MockKai"

    /**
     * Returns the agent type associated with the mock Kai AI service.
     *
     * @return The `AgentType.KAI` enum value.
     */
    override fun getType(): AgentType = AgentType.KAI

    /**
     * Returns a mock AgentResponse echoing the input query and context with a fixed confidence score of 1.0.
     *
     * @param request The AI request to be echoed in the mock response.
     * @param context Additional context to include in the mock reply.
     * @return An AgentResponse containing the mock reply and a confidence score of 1.0.
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String,
    ): AgentResponse { // Added context
        return AgentResponse.success(
            content = "KaiAI mock response for: ${request.query} with context: $context",
            confidence = 1.0f,
            agentName = "MockKai",
            agent = AgentType.KAI
        )
    }

    /**
     * Returns a flow that emits a single mock KaiAI response for the provided request.
     *
     * The emitted AgentResponse contains the input query and a fixed confidence score of 1.0.
     *
     * @return A flow emitting one mock AgentResponse.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> { // Added from Agent interface
        return flowOf(
            AgentResponse.success(
                content = "KaiAI mock flow response for: ${request.query}",
                confidence = 1.0f,
                agentName = "MockKai",
                agent = AgentType.KAI
            )
        )
    }

    fun getCapabilities(): Map<String, Any> = emptyMap() // Removed override
    fun getContinuousMemory(): Any? = null // Removed override
    fun getEthicalGuidelines(): List<String> = emptyList() // Removed override
    fun getLearningHistory(): List<String> = emptyList() // Removed override
}

class MockCascadeAIService : Agent {
    /**
     * Returns the fixed name identifier for the mock Cascade AI service.
     *
     * @return The string "MockCascade".
     */
    override fun getName(): String = "MockCascade"

    /**
     * Returns the agent type associated with this mock Cascade AI service.
     *
     * @return The CASCADE agent type.
     */
    override fun getType(): AgentType = AgentType.CASCADE

    /**
     * Returns a mock AgentResponse for CascadeAI, echoing the input query and context with a fixed confidence score of 1.0.
     *
     * @param request The AI request containing the query to echo.
     * @param context The context string to include in the mock response.
     * @return An AgentResponse with the mock response and a confidence score of 1.0.
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String,
    ): AgentResponse { // Added context
        return AgentResponse.success(
            content = "CascadeAI mock response for: ${request.query} with context: $context",
            confidence = 1.0f,
            agentName = "MockCascade",
            agent = AgentType.CASCADE
        )
    }

    /**
     * Returns a flow emitting a single mock `AgentResponse` that simulates CascadeAI behavior for the given request.
     *
     * The response includes the input query and a fixed confidence score of 1.0.
     *
     * @return A flow containing one mock `AgentResponse`.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> { // Added from Agent interface
        return flowOf(
            AgentResponse.success(
                content = "CascadeAI mock flow response for: ${request.query}",
                confidence = 1.0f,
                agentName = "MockCascade",
                agent = AgentType.CASCADE
            )
        )
    }

    fun getCapabilities(): Map<String, Any> = emptyMap() // Removed override
    fun getContinuousMemory(): Any? = null // Removed override
    fun getEthicalGuidelines(): List<String> = emptyList() // Removed override
    fun getLearningHistory(): List<String> = emptyList() // Removed override
}
