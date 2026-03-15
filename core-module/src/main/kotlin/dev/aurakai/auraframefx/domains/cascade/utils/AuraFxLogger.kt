package dev.aurakai.auraframefx.domains.cascade.utils

import android.util.Log

/**
 * Genesis Logger Interface - Complete
 * Provides both short-form (i, d, w, e) and long-form (info, debug, warn, error) methods.
 */
interface AuraFxLogger {
    // Short-form methods (for compatibility with existing call sites)
    fun i(tag: String, message: String) = info(tag, message)
    fun d(tag: String, message: String) = debug(tag, message)
    fun w(tag: String, message: String, throwable: Throwable? = null) =
        warn(tag, message, throwable)

    fun e(tag: String, message: String, throwable: Throwable? = null) =
        error(tag, message, throwable)

    // Long-form methods
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

// Global helper functions to match the companion object's delegation
fun info(tag: String, message: String) = Log.i(tag, message)
fun debug(tag: String, message: String) = Log.d(tag, message)
fun warn(tag: String, message: String, throwable: Throwable? = null) = Log.w(tag, message, throwable)
fun error(tag: String, message: String, throwable: Throwable? = null) = Log.e(tag, message, throwable)
fun i(tag: String, message: String) = info(tag, message)
fun d(tag: String, message: String) = debug(tag, message)
