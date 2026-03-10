package dev.aurakai.auraframefx.utils

import timber.log.Timber

/**
 * Interface for AuraFrameFX logging system.
 * Provides structured logging with security awareness and performance monitoring.
 */
interface AuraFxLogger {

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

    companion object : AuraFxLogger {
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

        override fun performance(tag: String, operation: String, durationMs: Long, metadata: Map<String, Any>) {
            val metadataStr = if (metadata.isNotEmpty()) " | Metadata: $metadata" else ""
            Timber.i("⏱️ PERFORMANCE [$tag] $operation completed in ${durationMs}ms$metadataStr")
        }

        override fun userInteraction(tag: String, action: String, metadata: Map<String, Any>) {
            val metadataStr = if (metadata.isNotEmpty()) " | Metadata: $metadata" else ""
            Timber.d("👤 USER_INTERACTION [$tag] $action$metadataStr")
        }

        override fun aiOperation(tag: String, operation: String, confidence: Float, metadata: Map<String, Any>) {
            val metadataStr = if (metadata.isNotEmpty()) " | Metadata: $metadata" else ""
            Timber.i("🧠 AI_OPERATION [$tag] $operation (confidence: ${confidence * 100}%)$metadataStr")
        }

        override fun setLoggingEnabled(enabled: Boolean) {
            if (enabled && Timber.treeCount == 0) {
                Timber.plant(Timber.DebugTree())
            } else if (!enabled) {
                Timber.uprootAll()
            }
        }

        override fun setLogLevel(level: LogLevel) {
            Timber.d("Log level set to: $level")
        }

        override suspend fun flush() {
            // Timber writes immediately, no buffering to flush
        }

        override fun cleanup() {
            Timber.uprootAll()
        }

        // Shorthand methods for convenience
        fun i(tag: String, message: String) = info(tag, message, null)
        fun e(tag: String, message: String, throwable: Throwable? = null) = error(tag, message, throwable)
        fun w(tag: String, message: String, throwable: Throwable? = null) = warn(tag, message, throwable)
        fun d(tag: String, message: String) = debug(tag, message, null)
    }
}

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR, SECURITY
}