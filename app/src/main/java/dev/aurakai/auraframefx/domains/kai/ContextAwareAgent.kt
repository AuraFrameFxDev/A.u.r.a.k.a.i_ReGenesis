package dev.aurakai.auraframefx.domains.kai

interface ContextAwareAgent {
    fun setContext(context: Map<String, Any>)
}
