package dev.aurakai.auraframefx.utils

/**
 * Genesis Logger Interface
 */
interface AuraFxLogger {
    fun info(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun debug(tag: String, message: String)
    fun warn(tag: String, message: String)
}

/**
 * Default Logger Implementation
 */
class DefaultAuraFxLogger : AuraFxLogger {

    override fun info(tag: String, message: String) {
        println("INFO [$tag]: $message")
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        println("ERROR [$tag]: $message")
        throwable?.printStackTrace()
    }

    override fun debug(tag: String, message: String) {
        println("DEBUG [$tag]: $message")
    }

    override fun warn(tag: String, message: String) {
        println("WARN [$tag]: $message")
    }
}