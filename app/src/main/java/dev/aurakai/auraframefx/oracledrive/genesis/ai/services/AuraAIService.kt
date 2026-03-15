package dev.aurakai.auraframefx.oracledrive.genesis.ai.services

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Genesis AI Service Interface
 */
interface AuraAIService {
    suspend fun initialize()
    suspend fun generateText(prompt: String, context: String): String
    suspend fun generateTheme(preferences: ThemePreferences, context: String): ThemeConfiguration
}

@Serializable
data class ThemePreferences(
    val primaryColor: String = "#6200EA",
    val style: String = "modern",
    val mood: String = "balanced",
    val animationLevel: String = "medium"
)

@Serializable
data class ThemeConfiguration(
    val primaryColor: String,
    val secondaryColor: String,
    val backgroundColor: String,
    val textColor: String,
    val style: String,
    val animationConfig: Map<String, @Contextual Any> = emptyMap()
)

// NOTE: DefaultAuraAIService implementation lives in DefaultAuraAIService.kt
// Do NOT add a class here — it causes "Redeclaration" compile errors.
