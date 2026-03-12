package dev.aurakai.auraframefx.domains.cascade.utils

/**
 * Genesis Error Handler
 * Manages error handling and recovery for AI operations
 */
interface ErrorHandler {

    /**
     * Handles an error that occurred during operation
     */
    fun handleError(error: Throwable, operation: String)

    /**
     * Reports a critical error that requires immediate attention
     */
    fun reportCriticalError(error: Throwable, context: String)

    /**
     * Gets error recovery suggestions
     */
    fun getRecoverySuggestions(error: Throwable): List<String>

    /**
     * Checks if an error is recoverable
     */
    fun isRecoverable(error: Throwable): Boolean

    /**
     * Gets error statistics
     */
    fun getErrorStats(): ErrorStats
}

data class ErrorStats(
    val totalErrors: Int,
    val criticalErrors: Int,
    val recoverableErrors: Int,
    val errorsByType: Map<String, Int>
)
