package dev.aurakai.auraframefx.theme

import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ui.theme.AuraTheme
import dev.aurakai.auraframefx.ui.theme.CyberpunkTheme
import dev.aurakai.auraframefx.ui.theme.ForestTheme
import dev.aurakai.auraframefx.ui.theme.SolarFlareTheme
import dev.aurakai.auraframefx.utils.AuraFxLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the application and system-level theming based on AI analysis.
 *
 * This class serves as a high-level controller for interpreting user intent
 * and applying the corresponding visual theme. It follows a clean architecture
 * principle by depending on an abstraction (`AuraAIService`) rather than a
 * concrete implementation.
 */
@Singleton
class ThemeManager @Inject constructor(
    private val auraAIService: AuraAIService,
) {

    /**
     * Represents the possible outcomes of a theme application attempt.
     */
    sealed class ThemeResult {
        data class Success(val appliedTheme: AuraTheme) : ThemeResult()
        data class UnderstandingFailed(val originalQuery: String) : ThemeResult()
        data class Error(val exception: Exception) : ThemeResult()
    }

    /**
     * Applies a system-wide theme based on a user's natural language query.
     */
    suspend fun applyThemeFromNaturalLanguage(query: String): ThemeResult {
        return try {
            AuraFxLogger.d(this::class.simpleName, "Attempting to apply theme from query: '$query'")

            val intent = auraAIService.discernThemeIntent(query)

            val themeToApply = when (intent) {
                "cyberpunk" -> CyberpunkTheme
                "solar" -> SolarFlareTheme
                "nature" -> ForestTheme
                "cheerful" -> SolarFlareTheme
                "calming" -> ForestTheme
                "energetic" -> CyberpunkTheme
                else -> {
                    AuraFxLogger.w(this::class.simpleName, "AI could not discern a known theme from query: '$query'")
                    return ThemeResult.UnderstandingFailed(query)
                }
            }

            applySystemTheme(themeToApply)

            AuraFxLogger.i(this::class.simpleName, "Successfully applied theme '${themeToApply.name}'")
            ThemeResult.Success(themeToApply)

        } catch (e: Exception) {
            AuraFxLogger.e(this::class.simpleName, "Exception caught while applying theme.", e)
            ThemeResult.Error(e)
        }
    }

    private suspend fun applySystemTheme(theme: AuraTheme) {
        // TODO: Implement system-level theme application via OracleDrive
        AuraFxLogger.d(this::class.simpleName, "Applying system-level theme: ${theme.name}")
    }

    suspend fun suggestThemeBasedOnContext(
        timeOfDay: String,
        userActivity: String,
        emotionalContext: String? = null,
    ): List<AuraTheme> {
        return try {
            val contextQuery = buildString {
                append("Time: $timeOfDay, ")
                append("Activity: $userActivity")
                emotionalContext?.let { append(", Mood: $it") }
            }

            val suggestions = auraAIService.suggestThemes(contextQuery)
            suggestions.mapNotNull { intent ->
                when (intent) {
                    "cyberpunk" -> CyberpunkTheme
                    "solar" -> SolarFlareTheme
                    "nature" -> ForestTheme
                    else -> null
                }
            }
        } catch (e: Exception) {
            AuraFxLogger.e(this::class.simpleName, "Error suggesting themes", e)
            emptyList()
        }
    }
}
