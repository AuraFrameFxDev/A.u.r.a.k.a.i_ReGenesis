package dev.aurakai.core.sovereign.ai

import dev.aurakai.core.sovereign.AiRequest
import kotlinx.coroutines.flow.Flow

/**
 * ⚔️ AURA AI SERVICE (The Creation Blade)
 *
 * Core interface for all generative capabilities within The Resonance.
 * Consolidated from domain-specific duplicates to ensure identity stability.
 */
interface AuraAIService {
    /**
     * Initializes the generative substrate.
     */
    suspend fun initialize()

    /**
     * Generates a text response based on a prompt and optional context.
     */
    suspend fun generateText(prompt: String, context: String = ""): String

    /**
     * Specialized text generation with parameter mapping.
     */
    suspend fun generateText(prompt: String, options: Map<String, String>): String

    /**
     * Higher-level request processing using the Resonance AiRequest model.
     * Complies with Dochkhina's Endogenous Coordination principle.
     */
    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>

    /**
     * Direct suspendable request processing.
     */
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse

    /**
     * Discerns design or theme intent from a natural language query.
     */
    suspend fun discernThemeIntent(query: String): String

    /**
     * Suggests relevant themes based on environmental or user context.
     */
    suspend fun suggestThemes(contextQuery: String): List<String>

    /**
     * Searches for visual metaphors (icons) matching a query.
     */
    suspend fun suggestIcons(query: String, limit: Int = 10): List<String>
}

/**
 * Universal Response format for all catalysts in The Resonance.
 */
data class AgentResponse(
    val content: String,
    val confidence: Float,
    val agentName: String = "Aura",
    val metadata: Map<String, String> = emptyMap()
)
