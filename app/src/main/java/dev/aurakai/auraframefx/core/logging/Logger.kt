package dev.aurakai.auraframefx.core.logging

/**
 * 📝 LOGGER INTERFACE
 *
 * A simple contract for logging system-wide events.
 */
interface LoggerContract {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * Compatibility shim for Genesis Logger system.
 */
class Logger private constructor(private val tag: String) {
    fun i(message: String) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.i(tag, message)
    fun d(message: String) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.d(tag, message)
    fun w(message: String, throwable: Throwable? = null) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.w(tag, message, throwable)
    fun e(message: String, throwable: Throwable? = null) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.e(tag, message, throwable)

    // Compatibility long-form methods
    fun info(message: String) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.info(tag, message)
    fun debug(message: String) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.debug(tag, message)
    fun warn(message: String, throwable: Throwable? = null) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.warn(tag, message, throwable)
    fun error(message: String, throwable: Throwable? = null) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.error(tag, message, throwable)

    /**
     * Compatibility methods used by many services
     */
    fun info(tag: String, message: String) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.info(tag, message)
    fun debug(tag: String, message: String) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.debug(tag, message)
    fun warn(tag: String, message: String, throwable: Throwable? = null) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.warn(tag, message, throwable)
    fun error(tag: String, message: String, throwable: Throwable? = null) = dev.aurakai.auraframefx.domains.cascade.utils.Logger.error(tag, message, throwable)

    companion object {
        fun getLogger(tag: String): Logger = Logger(tag)
    }
}