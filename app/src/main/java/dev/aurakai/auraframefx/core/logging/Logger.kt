package dev.aurakai.auraframefx.core.logging

/**
 * 📝 LOGGER INTERFACE
 *
 * A simple contract for logging system-wide events.
 */
interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
