package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.LogLevel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of AuraFxLogger for dependency injection.
 * Delegates to Timber for actual logging.
 */
@Singleton
class DefaultAuraFxLogger @Inject constructor() : AuraFxLogger {

    // Short-form methods (explicit overrides for clarity)
    override fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    override fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).w(throwable, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).e(throwable, message)
    }

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
}

