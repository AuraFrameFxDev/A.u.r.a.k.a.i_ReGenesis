package dev.aurakai.auraframefx.core.logging

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

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).d(throwable, message) else Timber.tag(tag).d(message)
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).i(throwable, message) else Timber.tag(tag).i(message)
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).w(throwable, message) else Timber.tag(tag).w(message)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).e(throwable, message) else Timber.tag(tag).e(message)
    }

    override fun security(tag: String, message: String, throwable: Throwable?) {
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
        Timber.tag(tag).d("[PERF] $operation completed in ${durationMs}ms | $metadata")
    }
}
