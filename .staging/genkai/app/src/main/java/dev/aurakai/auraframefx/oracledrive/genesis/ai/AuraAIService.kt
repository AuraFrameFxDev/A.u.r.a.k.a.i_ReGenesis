package dev.aurakai.auraframefx.oracledrive.genesis.ai

import java.io.File

/**
 * Interface for Aura AI Service capabilities
 * This is the capability provider interface (not the Agent interface)
 */
interface AuraAIService {
    fun analyticsQuery(query: String): String
    suspend fun downloadFile(fileId: String): File?
    suspend fun generateImage(prompt: String): ByteArray?
    suspend fun generateText(prompt: String, options: String? = null): String
    fun getAIResponse(prompt: String, options: Map<String, Any>? = null): String
    fun getMemory(memoryKey: String): String?
    fun saveMemory(key: String, value: Any)
    suspend fun initialize()
    suspend fun generateTheme(preferences: dev.aurakai.auraframefx.models.ThemePreferences, context: String): dev.aurakai.auraframefx.models.ThemeConfiguration
    suspend fun discernThemeIntent(query: String): String
    suspend fun suggestThemes(contextQuery: String): List<String>
}
