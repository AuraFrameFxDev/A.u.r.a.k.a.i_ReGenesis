package dev.aurakai.auraframefx.domains.aura.ui.theme.service

// TODO: StanfordCoreNLP is not suitable for Android - using simple keyword matching instead
// For production, consider using ML Kit or a lightweight NLP solution

class ThemeService {

    fun parseThemeCommand(command: String): ThemeCommand {
        // Simple keyword-based parsing (lightweight alternative to StanfordCoreNLP)
        val keywords = command.lowercase()
            .split(Regex("[\\s,;.!?]+"))
            .filter { it.isNotBlank() }

        return when {
            keywords.any { it.contains("dark") } -> ThemeCommand.SetTheme(Theme.DARK)
            keywords.any { it.contains("light") } -> ThemeCommand.SetTheme(Theme.LIGHT)
            keywords.any { it.contains("cyberpunk") } -> ThemeCommand.SetTheme(Theme.CYBERPUNK)
            keywords.any { it.contains("solarized") } -> ThemeCommand.SetTheme(Theme.SOLARIZED)
            keywords.any { it.contains("red") } -> ThemeCommand.SetColor(Color.RED)
            keywords.any { it.contains("blue") } -> ThemeCommand.SetColor(Color.BLUE)
            keywords.any { it.contains("green") } -> ThemeCommand.SetColor(Color.GREEN)
            else -> ThemeCommand.Unknown
        }
    }
}

sealed class ThemeCommand {
    data class SetTheme(val theme: Theme) : ThemeCommand()
    data class SetColor(val color: Color) : ThemeCommand()
    object Unknown : ThemeCommand()
}

enum class Theme {
    LIGHT,
    DARK,
    CYBERPUNK,
    SOLARIZED
}

enum class Color {
    RED,
    GREEN,
    BLUE
}

