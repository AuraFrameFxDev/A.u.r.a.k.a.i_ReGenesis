package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of ErrorHandler for the AuraFrameFx system.
 *
 * Provides centralized error handling with logging and optional recovery strategies.
 * This implementation serves as the primary error handler throughout the application.
 */
@Singleton
class DefaultErrorHandler @Inject constructor(
    private val logger: AuraFxLogger
) : ErrorHandler {

    companion object {
        private const val TAG = "DefaultErrorHandler"
    }

    override fun handleError(error: Throwable, agent: AgentType, context: String) {
        logger.error(TAG, "Error from agent $agent in context: $context", error)
        handleCriticalActions(error)
    }

    override fun handleError(
        error: Throwable,
        agent: AgentType,
        context: String,
        metadata: Map<String, String>
    ) {
        logger.error(
            TAG,
            "Error from agent $agent in context: $context (Metadata: $metadata)",
            error
        )
        handleCriticalActions(error)
    }

    override fun logError(tag: String, message: String, error: Throwable?) {
        if (error != null) {
            logger.error(tag, message, error)
        } else {
            logger.error(tag, message)
        }
    }

    private fun handleCriticalActions(error: Throwable) {
        when (error) {
            is OutOfMemoryError -> logger.error(TAG, "CRITICAL: Out of memory")
            is SecurityException -> logger.error(TAG, "SECURITY VIOLATION")
        }
    }
}

