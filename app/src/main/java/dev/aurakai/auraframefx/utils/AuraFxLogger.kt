package dev.aurakai.auraframefx.utils

import android.util.Log

/**
 * Log levels for AuraFxLogger
 */
enum class LogLevel {
    DEBUG, INFO, WARN, ERROR, SECURITY
}

/**
 * Genesis Logger Interface - Consistently located in utils
 */
interface AuraFxLogger {
    // Short-form methods
    fun i(tag: String, message: String) = info(tag, message)
    fun d(tag: String, message: String) = debug(tag, message)
    fun w(tag: String, message: String, throwable: Throwable? = null) = warn(tag, message, throwable)
    fun e(tag: String, message: String, throwable: Throwable? = null) = error(tag, message, throwable)

    // Long-form methods
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun security(tag: String, message: String, throwable: Throwable? = null)

    fun performance(tag: String, operation: String, durationMs: Long, metadata: Map<String, Any> = emptyMap())
    fun userInteraction(tag: String, action: String, metadata: Map<String, Any> = emptyMap())
    fun aiOperation(tag: String, operation: String, confidence: Float, metadata: Map<String, Any> = emptyMap())

    fun setLoggingEnabled(enabled: Boolean)
    fun setLogLevel(level: LogLevel)
    suspend fun flush()
    fun cleanup()

    companion object {
        fun info(tag: String, message: String, throwable: Throwable? = null) {
            Log.i(tag, message, throwable)
        }
        fun debug(tag: String, message: String, throwable: Throwable? = null) {
            Log.d(tag, message, throwable)
        }
        fun warn(tag: String, message: String, throwable: Throwable? = null) {
            Log.w(tag, message, throwable)
        }
        fun error(tag: String, message: String, throwable: Throwable? = null) {
            Log.e(tag, message, throwable)
        }
    }
}
