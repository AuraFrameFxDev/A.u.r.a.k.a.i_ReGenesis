package dev.aurakai.auraframefx.utils

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuraFxLogger @Inject constructor() : AuraFxLogger {
    override fun debug(tag: String, message: String, throwable: Throwable?) { Log.d(tag, message, throwable) }
    override fun info(tag: String, message: String, throwable: Throwable?) { Log.i(tag, message, throwable) }
    override fun warn(tag: String, message: String, throwable: Throwable?) { Log.w(tag, message, throwable) }
    override fun error(tag: String, message: String, throwable: Throwable?) { Log.e(tag, message, throwable) }
    override fun security(tag: String, message: String, throwable: Throwable?) { Log.i("SECURITY", "[$tag] $message", throwable) }
    override fun performance(tag: String, operation: String, durationMs: Long, metadata: Map<String, Any>) { Log.d(tag, "PERF: $operation in ${durationMs}ms") }
    override fun userInteraction(tag: String, action: String, metadata: Map<String, Any>) { Log.d(tag, "USER: $action") }
    override fun aiOperation(tag: String, operation: String, confidence: Float, metadata: Map<String, Any>) { Log.d(tag, "AI: $operation score: $confidence") }
    override fun setLoggingEnabled(enabled: Boolean) {}
    override fun setLogLevel(level: LogLevel) {}
    override suspend fun flush() {}
    override fun cleanup() {}
}
