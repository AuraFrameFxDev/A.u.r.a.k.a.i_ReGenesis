package dev.aurakai.auraframefx.utils

import timber.log.Timber

/**
 * Interface for AuraFrameFX logging system.
 * Provides structured logging with security awareness and performance monitoring.
 */
interface AuraFxLogger {

    /**
     * Logs a debug-level message for development and troubleshooting purposes.
     *
     * @param tag Identifier for the log source or component.
     * @param message The debug message to log.
     * @param throwable Optional exception or error to include with the log entry.
     */
    fun debug(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs an informational message to track normal application operations.
     *
     * @param tag Identifier for the log source or component.
     * @param message The informational message to log.
     * @param throwable Optional exception or error to include with the log entry.
     */
    fun info(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a warning message indicating a potential issue.
     *
     * @param tag The category or source of the log message.
     * @param message The warning message to log.
     * @param throwable An optional exception or error related to the warning.
     */
    fun warn(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs an error message indicating a failure or exception.
     *
     * @param tag The category or source of the log entry.
     * @param message The error message to log.
     * @param throwable An optional exception associated with the error.
     */
    fun error(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a critical security event that requires immediate attention.
     *
     * @param tag The category or source of the security event.
     * @param message The security-related message to log.
     * @param throwable An optional exception or error associated with the event.
     */
    fun security(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs performance metrics for a specific operation, including its duration and optional metadata.
     *
     * @param tag Identifier for the log source or component.
     * @param operation Name or description of the operation being measured.
     * @param durationMs Duration of the operation in milliseconds.
     * @param metadata Optional additional data providing context for the performance event.
     */
    fun performance(
        tag: String,
        operation: String,
        durationMs: Long,
        metadata: Map<String, Any> = emptyMap(),
    )

    /**
     * Logs a user interaction event for analytics and user experience optimization.
     *
     * @param tag A category or component identifier for the event.
     * @param action The specific user action being recorded.
     * @param metadata Optional additional context about the interaction.
     */
    fun userInteraction(tag: String, action: String, metadata: Map<String, Any> = emptyMap())

    /**
     * Logs an AI-related operation, including the operation name, confidence score, and optional metadata.
     *
     * @param tag The category or component associated with the AI operation.
     * @param operation The name or description of the AI operation performed.
     * @param confidence The confidence score of the AI decision or result.
     * @param metadata Optional additional context or details about the operation.
     */
    fun aiOperation(
        tag: String,
        operation: String,
        confidence: Float,
        metadata: Map<String, Any> = emptyMap(),
    )

    /**
     * Enables or disables logging during runtime.
     *
     * @param enabled If true, logging is enabled; if false, logging is disabled.
     */
    fun setLoggingEnabled(enabled: Boolean)

    /**
     * Sets the minimum log level for logging.
     *
     * Only log entries at or above the specified level will be recorded.
     *
     * @param level The lowest log level that will be processed.
     */
    fun setLogLevel(level: LogLevel)

    /**
     * Forces all pending log entries to be written to storage.
     *
     * This is a suspend function and may perform I/O operations.
     */
    suspend fun flush()

    /**
     * Releases resources and terminates logging operations.
     */
    fun cleanup()

    companion object : AuraFxLogger {
        /**
         * Default Timber-based implementation of AuraFxLogger
         */
        override fun debug(tag: String, message: String, throwable: Throwable?) {
            Timber.d(throwable, "[$tag] $message")
        }

        override fun info(tag: String, message: String, throwable: Throwable?) {
            Timber.i(throwable, "[$tag] $message")
        }

        override fun warn(tag: String, message: String, throwable: Throwable?) {
            Timber.w(throwable, "[$tag] $message")
        }

        override fun error(tag: String, message: String, throwable: Throwable?) {
            Timber.e(throwable, "[$tag] $message")
        }

        override fun security(tag: String, message: String, throwable: Throwable?) {
            Timber.wtf(throwable, "🔒 SECURITY [$tag] $message")
        }

        override fun performance(
            tag: String,
            operation: String,
            durationMs: Long,
            metadata: Map<String, Any>
        ) {
            val metadataStr = if (metadata.isNotEmpty()) " | Metadata: $metadata" else ""
            Timber.i("⏱️ PERFORMANCE [$tag] $operation completed in ${durationMs}ms$metadataStr")
        }

        override fun userInteraction(tag: String, action: String, metadata: Map<String, Any>) {
            val metadataStr = if (metadata.isNotEmpty()) " | Metadata: $metadata" else ""
            Timber.d("👤 USER_INTERACTION [$tag] $action$metadataStr")
        }

        override fun aiOperation(
            tag: String,
            operation: String,
            confidence: Float,
            metadata: Map<String, Any>
        ) {
            val metadataStr = if (metadata.isNotEmpty()) " | Metadata: $metadata" else ""
            Timber.i("🤖 AI_OPERATION [$tag] $operation (confidence: ${String.format("%.2f", confidence)})$metadataStr")
        }

        override fun setLoggingEnabled(enabled: Boolean) {
            // Placeholder for enabling/disabling logging
        }

        override fun setLogLevel(level: LogLevel) {
            // Placeholder for setting log level
        }

        override suspend fun flush() {
            // Placeholder for flushing logs
        }

        override fun cleanup() {
            // Placeholder for cleanup
        }
    }
}

/**
 * Log levels for filtering and prioritization.
 */
enum class LogLevel(val priority: Int) {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    SECURITY(4)
}
