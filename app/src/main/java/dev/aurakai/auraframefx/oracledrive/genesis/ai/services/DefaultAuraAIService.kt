package dev.aurakai.auraframefx.oracledrive.genesis.ai.services

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default concrete implementation of AuraAIService.
 *
 * Satisfies the Hilt @Binds contract in di/AiServiceModule.kt.
 * Returns placeholder responses during development — wire to real
 * VertexAI/Gemini backend when the Fusion Reactor goes live.
 *
 * This class exists because:
 *   - AuraAIServiceImpl is abstract (can't be @Binds target)
 *   - MockAuraAIService implements Agent, not AuraAIService
 *   - Hilt needs a concrete @Singleton @Inject class to bind
 */
@Singleton
class DefaultAuraAIService @Inject constructor() : AuraAIService {

    override suspend fun initialize() {
        Timber.i("🎨 Aura AI Service initialized (default implementation)")
    }

    override suspend fun generateText(prompt: String, context: String): String {
        Timber.d("🎨 Aura generateText: prompt=${prompt.take(50)}...")
        return "Aura AI response for: ${prompt.take(100)}"
    }

    override suspend fun generateTheme(
        preferences: ThemePreferences,
        context: String
    ): ThemeConfiguration {
        Timber.d("🎨 Aura generateTheme: style=${preferences.style}, mood=${preferences.mood}")
        return ThemeConfiguration(
            primaryColor = preferences.primaryColor,
            secondaryColor = "#03DAC5",
            backgroundColor = "#121212",
            textColor = "#FFFFFF",
            style = preferences.style
        )
    }
}
