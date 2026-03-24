package dev.aurakai.auraframefx.ai.adapters

import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse

/**
 * Adapter interface for interacting with the Grok chaos analyst agent.
 */
interface GrokAdapter {
    /**
     * Processes an AI request through Grok.
     */
    suspend fun processRequest(request: AiRequest): AgentResponse
}
