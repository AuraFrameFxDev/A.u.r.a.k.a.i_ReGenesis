package dev.aurakai.genesis.logging

import dev.aurakai.auraframefx.domains.cascade.utils.Logger as DomainLogger

/**
 * Compatibility shim for Genesis Logger system.
 */
class Logger private constructor(private val tag: String) {
    fun i(message: String) = DomainLogger.i(tag, message)
    fun d(message: String) = DomainLogger.d(tag, message)
    fun w(message: String, throwable: Throwable? = null) = DomainLogger.w(tag, message, throwable)
    fun e(message: String, throwable: Throwable? = null) = DomainLogger.e(tag, message, throwable)
    
    // Compatibility long-form methods
    fun info(message: String) = DomainLogger.info(tag, message)
    fun debug(message: String) = DomainLogger.debug(tag, message)
    fun warn(message: String, throwable: Throwable? = null) = DomainLogger.warn(tag, message, throwable)
    fun error(message: String, throwable: Throwable? = null) = DomainLogger.error(tag, message, throwable)

    /**
     * Compatibility methods used by many services
     */
    fun info(tag: String, message: String) = DomainLogger.info(tag, message)
    fun debug(tag: String, message: String) = DomainLogger.debug(tag, message)
    fun warn(tag: String, message: String, throwable: Throwable? = null) = DomainLogger.warn(tag, message, throwable)
    fun error(tag: String, message: String, throwable: Throwable? = null) = DomainLogger.error(tag, message, throwable)

    companion object {
        fun getLogger(tag: String): Logger = Logger(tag)
    }
}
