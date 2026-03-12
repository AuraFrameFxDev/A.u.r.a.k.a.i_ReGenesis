package dev.aurakai.auraframefx.ui.theme

import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.Properties

class ThemeService {

    private val pipeline: StanfordCoreNLP

    init {
        val props = Properties()
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner")
        pipeline = StanfordCoreNLP(props)
    }

    fun parseThemeCommand(command: String): ThemeCommand {
        // This is a very basic implementation. A real implementation would use a more
        // sophisticated NLP model to understand the user's intent.
        val document = pipeline.newdocument(command)
        pipeline.annotate(document)
        val tokens = document.tokens()
        val keywords = tokens.map { it.lemma().lowercase() }

        return when {
            keywords.contains("dark") -> ThemeCommand.SetTheme(Theme.DARK)
            keywords.contains("light") -> ThemeCommand.SetTheme(Theme.LIGHT)
            keywords.contains("cyberpunk") -> ThemeCommand.SetTheme(Theme.CYBERPUNK)
            keywords.contains("solarized") -> ThemeCommand.SetTheme(Theme.SOLARIZED)
            keywords.contains("red") -> ThemeCommand.SetColor(Color.RED)
            keywords.contains("blue") -> ThemeCommand.SetColor(Color.BLUE)
            keywords.contains("green") -> ThemeCommand.SetColor(Color.GREEN)
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
