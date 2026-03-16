package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.core.identity.AgentType

/**
 * Interface for handling errors across the application
 */
interface ErrorHandler {
    fun handleError(error: Throwable, agent: AgentType, context: String)
    fun handleError(
        error: Throwable,
        agent: AgentType,
        context: String,
        metadata: Map<String, String>
    ) {
        // Default implementation delegates to simpler version
        handleError(error, agent, context)
    }

    fun logError(tag: String, message: String, error: Throwable? = null)
}
