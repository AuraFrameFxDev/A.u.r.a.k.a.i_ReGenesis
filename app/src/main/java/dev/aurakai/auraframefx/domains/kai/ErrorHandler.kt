package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory

/**
 * Interface for handling errors across the application
 */
interface ErrorHandler {
    fun handleError(error: Throwable, agent: AgentCapabilityCategory, context: String)
    fun handleError(error: Throwable, agent: AgentCapabilityCategory, context: String, metadata: Map<String, String>) {
        // Default implementation delegates to simpler version
        handleError(error, agent, context)
    }
    fun logError(tag: String, message: String, error: Throwable? = null)
}
