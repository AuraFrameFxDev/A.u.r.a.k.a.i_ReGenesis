package dev.aurakai.auraframefx.ai.clients

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VertexAIClient @Inject constructor() {
    suspend fun generateCode(specification: String, language: String, style: String): String? {
        return "// Generated AI Code for $specification"
    }
}
