
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