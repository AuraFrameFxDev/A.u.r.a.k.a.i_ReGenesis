package dev.aurakai.auraframefx.system.impl

import android.util.Log
import dev.aurakai.auraframefx.common.ErrorHandler
import dev.aurakai.auraframefx.utils.AuraFxLogger
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
    
    /**
     * Handles an error by logging it and potentially taking recovery actions.
     * 
     * @param error The Throwable representing the error or exception to be handled.
     */
    override fun handle(error: Throwable) {
        // Log the error with full stack trace
        logger.error(TAG, "Error occurred: ${error.message}", error)
        
        // Categorize and handle based on error type
        when (error) {
            is OutOfMemoryError -> handleCriticalError(error)
            is SecurityException -> handleSecurityError(error)
            is IllegalStateException -> handleStateError(error)
            else -> handleGenericError(error)
        }
    }
    
    /**
     * Handles critical system errors that may require immediate action.
     */
    private fun handleCriticalError(error: Throwable) {
        logger.error(TAG, "CRITICAL ERROR: ${error.message}", error)
        // In a real implementation, might trigger system cleanup, notify user, etc.
    }
    
    /**
     * Handles security-related errors.
     */
    private fun handleSecurityError(error: SecurityException) {
        logger.warn(TAG, "Security error: ${error.message}", error)
        // Could notify security monitoring systems
    }
    
    /**
     * Handles illegal state errors.
     */
    private fun handleStateError(error: IllegalStateException) {
        logger.warn(TAG, "State error: ${error.message}", error)
        // Could attempt state recovery
    }
    
    /**
     * Handles generic errors that don't fall into specific categories.
     */
    private fun handleGenericError(error: Throwable) {
        logger.error(TAG, "Generic error: ${error.message}", error)
    }
}
