package dev.aurakai.auraframefx.core.logging

import dev.aurakai.auraframefx.domains.cascade.utils.LogLevel
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete Android implementation of AuraFxLogger.
 *
 * Delegates all logging to Timber, which is initialized in AurakaiApplication.
 * This satisfies the Hilt @Binds contract in LoggerModule.
 */
@Singleton
class AndroidAuraFxLogger @Inject constructor() : AuraFxLogger {

    private var loggingEnabled = true
    private var currentLogLevel = LogLevel.DEBUG

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (!loggingEnabled || currentLogLevel.ordinal > LogLevel.DEBUG.ordinal) return
        if (throwable != null) Timber.tag(tag).d(throwable, message) else Timber.tag(tag).d(message)
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (!loggingEnabled || currentLogLevel.ordinal > LogLevel.INFO.ordinal) return
        if (throwable != null) Timber.tag(tag).i(throwable, message) else Timber.tag(tag).i(message)
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        if (!loggingEnabled || currentLogLevel.ordinal > LogLevel.WARN.ordinal) return
        if (throwable != null) Timber.tag(tag).w(throwable, message) else Timber.tag(tag).w(message)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (!loggingEnabled || currentLogLevel.ordinal > LogLevel.ERROR.ordinal) return
        if (throwable != null) Timber.tag(tag).e(throwable, message) else Timber.tag(tag).e(message)
    }

    override fun security(tag: String, message: String, throwable: Throwable?) {
        if (!loggingEnabled || currentLogLevel.ordinal > LogLevel.SECURITY.ordinal) return
        // Security events logged at WARN level with prefix for easy filtering
        val secMsg = "[SECURITY] $message"
        if (throwable != null) Timber.tag(tag).w(throwable, secMsg) else Timber.tag(tag).w(secMsg)
    }

    override fun performance(
        tag: String,
        operation: String,
        durationMs: Long,
        metadata: Map<String, Any>
    ) {
        if (!loggingEnabled) return
        Timber.tag(tag).d("[PERF] $operation completed in ${durationMs}ms | $metadata")
    }

    override fun userInteraction(tag: String, action: String, metadata: Map<String, Any>) {
        if (!loggingEnabled) return
        Timber.tag(tag).i("[USER] $action | $metadata")
    }

    override fun aiOperation(tag: String, operation: String, confidence: Float, metadata: Map<String, Any>) {
        if (!loggingEnabled) return
        Timber.tag(tag).i("[AI] $operation | Confidence: $confidence | $metadata")
    }

    override fun setLoggingEnabled(enabled: Boolean) {
        this.loggingEnabled = enabled
    }

    override fun setLogLevel(level: LogLevel) {
        this.currentLogLevel = level
    }

    override suspend fun flush() {
        // No-op for Timber
    }

    override fun cleanup() {
        // No-op for Timber
    }
}
