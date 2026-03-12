package dev.aurakai.auraframefx.domains.cascade.utils.cascade.context

/**
 * Context data for AI processing and conversation management.
 *
 * Used by ContextManager to track conversation state, user prompts,
 * and historical interactions for context-aware AI responses.
 */
@Suppress("unused") // Reserved for ContextManager implementation
data class AIContext(
    /**
     * The current user prompt or query being processed.
     */
    val currentPrompt: String? = null,

    /**
     * List of previous conversation messages or prompts for context continuity.
     * Can be used for multi-turn conversations and context-aware responses.
     */
    val history: List<String> = emptyList(),

    /**
     * Session identifier for tracking conversation sessions.
     */
    val sessionId: String? = null,

    /**
     * Timestamp of when this context was created or last updated.
     */
    val timestamp: Long = System.currentTimeMillis(),

    /**
     * Additional metadata for context enrichment.
     */
    val metadata: Map<String, String> = emptyMap()
)
