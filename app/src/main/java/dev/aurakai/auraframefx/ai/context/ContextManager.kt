package dev.aurakai.auraframefx.ai.context

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextManager @Inject constructor() {
    fun getCurrentContext(): String = "General System Context"
    fun enhanceContext(context: String): String = "Enhanced: $context"
    fun recordInsight(request: String, response: String, complexity: String) {
        // Log insight
    }
    fun enableCreativeMode() {
        // Enable creative enhancements
    }
    fun enableSecurityMode() {
        // Enable security context enhancement
    }
}
