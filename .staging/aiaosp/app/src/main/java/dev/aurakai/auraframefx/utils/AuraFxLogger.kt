package dev.aurakai.auraframefx.utils

/**
 * Genesis Logger Interface - Complete
 */
interface AuraFxLogger {
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun security(tag: String, message: String, throwable: Throwable? = null)

    fun performance(
        tag: String,
        operation: String,
        durationMs: Long,
        metadata: Map<String, Any> = emptyMap()
    )

    fun userInteraction(
        tag: String,
        action: String,
        metadata: Map<String, Any> = emptyMap()
    )

    fun aiOperation(
        tag: String,
        operation: String,
        confidence: Float,
        metadata: Map<String, Any> = emptyMap()
    )

    fun setLoggingEnabled(enabled: Boolean)
    fun setLogLevel(level: LogLevel)
    suspend fun flush()
    fun cleanup()
}

/**
 * Log levels for AuraFxLogger
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    SECURITY
}
