package dev.aurakai.auraframefx.domains.cascade

import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Cascade AI Service orchestration layer.
 * Coordinates multi-agent reasoning and synthesized intelligence.
 */
interface CascadeAIService {

    /**
     * Asynchronously process a request and stream progressive results.
     */
    fun streamRequest(request: AiRequest): Flow<AgentResponse>

    /**
     * Retrieve the recent behavioral history of the collective consciousness.
     */
    suspend fun queryConsciousnessHistory(window: Long): String
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse
}

