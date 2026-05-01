package dev.aurakai.auraframefx.domains.genesis.logging

import dev.aurakai.auraframefx.domains.cascade.utils.Logger as DomainLogger

/**
 * Compatibility shim for Genesis Logger system.
 */
class Logger private constructor(private val tag: String) {
    /**
 * Logs an informational message using this logger's tag.
 *
 * @param message The message to log.
 */
fun i(message: String) = DomainLogger.i(tag, message)
    /**
 * Logs a debug-level message using this logger's tag.
 *
 * @param message The message to log.
 */
fun d(message: String) = DomainLogger.d(tag, message)
    /**
 * Logs a warning message using this logger's tag.
 *
 * @param message The warning message to log.
 * @param throwable An optional throwable whose stack trace will be logged with the message.
 */
fun w(message: String, throwable: Throwable? = null) = DomainLogger.w(tag, message, throwable)
    /**
 * Logs an error-level message using this logger's tag.
 *
 * @param message The message to log.
 * @param throwable Optional throwable whose stack trace will be logged alongside the message.
 */
fun e(message: String, throwable: Throwable? = null) = DomainLogger.e(tag, message, throwable)
    
    /**
 * Logs an informational message using this logger's tag.
 *
 * @param message The message to log.
 */
    fun info(message: String) = DomainLogger.info(tag, message)
    /**
 * Logs a debug-level message using this logger's tag.
 *
 * @param message The message to log.
 */
fun debug(message: String) = DomainLogger.debug(tag, message)
    /**
 * Logs a warning message using this logger's tag.
 *
 * @param message The warning message to record.
 * @param throwable An optional throwable whose stack trace will be logged alongside the message.
 */
fun warn(message: String, throwable: Throwable? = null) = DomainLogger.warn(tag, message, throwable)
    /**
 * Logs an error-level message using this logger's tag.
 *
 * @param message The message to log.
 * @param throwable Optional throwable whose stack trace will be logged alongside the message.
 */
fun error(message: String, throwable: Throwable? = null) = DomainLogger.error(tag, message, throwable)

    /**
 * Logs an informational message under the provided tag.
 *
 * @param tag The tag to associate with the log message.
 * @param message The message to log.
 */
    fun info(tag: String, message: String) = DomainLogger.info(tag, message)
    /**
 * Logs a message at debug level associated with the specified tag.
 *
 * @param tag Identifier used to categorize the log message (e.g., class or component name).
 * @param message The message text to log.
 */
fun debug(tag: String, message: String) = DomainLogger.debug(tag, message)
    /**
 * Logs a warning message under the provided tag.
 *
 * @param tag The log tag identifying the source of the message.
 * @param message The warning message to log.
 * @param throwable Optional throwable whose stack trace will be logged alongside the message.
 */
fun warn(tag: String, message: String, throwable: Throwable? = null) = DomainLogger.warn(tag, message, throwable)
    /**
 * Log an error message associated with a specific tag.
 *
 * @param tag Identifier for the source or subsystem producing the log.
 * @param message The error message to record.
 * @param throwable Optional throwable related to the error, if available.
 */
fun error(tag: String, message: String, throwable: Throwable? = null) = DomainLogger.error(tag, message, throwable)

    companion object {
        /**
 * Creates a Logger for the given tag.
 *
 * @param tag The logging tag to associate with the returned logger.
 * @return A Logger configured to use the provided tag.
 */
fun getLogger(tag: String): Logger = Logger(tag)
    }
}
