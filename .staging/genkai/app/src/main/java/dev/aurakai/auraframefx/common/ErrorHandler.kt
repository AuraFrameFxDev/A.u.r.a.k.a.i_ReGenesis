package dev.aurakai.auraframefx.common

import dev.aurakai.auraframefx.models.AgentType

/**
 * Interface for handling errors across the application
 */
interface ErrorHandler {
    fun handleError(error: Throwable, agent: AgentType, context: String)
    fun logError(tag: String, message: String, error: Throwable? = null)
}
