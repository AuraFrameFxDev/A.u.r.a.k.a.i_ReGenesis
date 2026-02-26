package dev.aurakai.auraframefx.domains.cascade.utils

import androidx.compose.ui.graphics.Color
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🗣️ Voice Command Processor
 * 
 * Parses natural language voice commands and converts them to UI actions.
 * 
 * Supported Commands:
 * - "Hey Aura, move the status bar down"
 * - "Hey Aura, make it cyberpunk pink"
 * - "Hey Aura, rotate the clock widget 45 degrees"
 * - "Hey Aura, change the background to blue"
 * - "Hey Aura, increase opacity"
 * - "Hey Aura, hide the navigation bar"
 */
@Singleton
class VoiceCommandProcessor @Inject constructor() {

    companion object {
        private const val WAKE_WORD = "hey aura"
        private const val WAKE_WORD_ALT = "hey aurora"
    }

    /**
     * Process voice command and return the action
     */
    fun processCommand(text: String): VoiceCommand {
        val normalized = text.lowercase().trim()

        // Check for wake word
        if (!normalized.startsWith(WAKE_WORD) && !normalized.startsWith(WAKE_WORD_ALT)) {
            return VoiceCommand.Invalid("Command must start with 'Hey Aura'")
        }

        // Remove wake word
        val command = normalized
            .removePrefix(WAKE_WORD)
            .removePrefix(WAKE_WORD_ALT)
            .trim()
            .removeSuffix(".")
            .removeSuffix(",")

        Timber.d("Processing command: $command")

        return when {
            // Theme commands
            command.contains("make it") || command.contains("change theme") -> {
                parseThemeCommand(command)
            }

            // Component movement
            command.contains("move") -> {
                parseMoveCommand(command)
            }

            // Rotation
            command.contains("rotate") -> {
                parseRotateCommand(command)
            }

            // Color changes
            command.contains("change") && (command.contains("color") || command.contains("background")) -> {
                parseColorCommand(command)
            }

            // Opacity
            command.contains("opacity") || command.contains("transparency") -> {
                parseOpacityCommand(command)
            }

            // Visibility
            command.contains("hide") || command.contains("show") -> {
                parseVisibilityCommand(command)
            }

            // Scale/Size
            command.contains("scale") || command.contains("size") || command.contains("bigger") || command.contains(
                "smaller"
            ) -> {
                parseScaleCommand(command)
            }

            // Reset
            command.contains("reset") -> {
                VoiceCommand.ResetComponent(extractComponentName(command))
            }

            else -> VoiceCommand.Invalid("Unknown command: $command")
        }
    }

    private fun parseThemeCommand(command: String): VoiceCommand {
        val theme = when {
            command.contains("cyberpunk pink") -> "Cyberpunk Pink"
            command.contains("cyberpunk") || command.contains("neon") -> "Cyberpunk Neon"
            command.contains("dark purple") || command.contains("purple") -> "Dark Neon Purple"
            command.contains("pure dark") || command.contains("black") -> "Pure Dark"
            command.contains("minimal") || command.contains("light") -> "Minimal Light"
            command.contains("futuristic") || command.contains("blue") -> "Futuristic Blue"
            command.contains("matrix") || command.contains("green") -> "Matrix Green"
            command.contains("sunset") || command.contains("warm") || command.contains("orange") -> "Sunset Warm"
            else -> return VoiceCommand.Invalid("Unknown theme")
        }
        return VoiceCommand.ChangeTheme(theme)
    }

    private fun parseMoveCommand(command: String): VoiceCommand {
        val componentName = extractComponentName(command)

        val direction = when {
            command.contains("up") -> "up"
            command.contains("down") -> "down"
            command.contains("left") -> "left"
            command.contains("right") -> "right"
            else -> return VoiceCommand.Invalid("No direction specified")
        }

        val amount = extractNumber(command) ?: 50f

        return VoiceCommand.MoveComponent(componentName, direction, amount)
    }

    private fun parseRotateCommand(command: String): VoiceCommand {
        val componentName = extractComponentName(command)
        val degrees = extractNumber(command) ?: 45f
        return VoiceCommand.RotateComponent(componentName, degrees)
    }

    private fun parseColorCommand(command: String): VoiceCommand {
        val componentName = extractComponentName(command)
        val color = extractColor(command) ?: return VoiceCommand.Invalid("No color specified")
        return VoiceCommand.ChangeColor(componentName, color)
    }

    private fun parseOpacityCommand(command: String): VoiceCommand {
        val componentName = extractComponentName(command)
        val opacity = when {
            command.contains("increase") || command.contains("more") -> 1f
            command.contains("decrease") || command.contains("less") -> -1f
            else -> extractNumber(command)?.div(100f) ?: 0.5f
        }
        return VoiceCommand.ChangeOpacity(componentName, opacity)
    }

    private fun parseVisibilityCommand(command: String): VoiceCommand {
        val componentName = extractComponentName(command)
        val visible = command.contains("show")
        return VoiceCommand.ChangeVisibility(componentName, visible)
    }

    private fun parseScaleCommand(command: String): VoiceCommand {
        val componentName = extractComponentName(command)
        val scale = when {
            command.contains("bigger") || command.contains("increase") -> 1.2f
            command.contains("smaller") || command.contains("decrease") -> 0.8f
            else -> extractNumber(command) ?: 1f
        }
        return VoiceCommand.ScaleComponent(componentName, scale)
    }

    private fun extractComponentName(command: String): String {
        return when {
            command.contains("status bar") -> "Status Bar"
            command.contains("navigation bar") || command.contains("nav bar") -> "Navigation Bar"
            command.contains("clock") -> "Clock Widget"
            command.contains("app grid") || command.contains("apps") -> "App Grid"
            else -> "Status Bar" // Default
        }
    }

    private fun extractNumber(command: String): Float? {
        val regex = "\\d+".toRegex()
        return regex.find(command)?.value?.toFloatOrNull()
    }

    private fun extractColor(command: String): Color? {
        return when {
            command.contains("red") -> Color.Red
            command.contains("blue") -> Color.Blue
            command.contains("green") -> Color.Green
            command.contains("yellow") -> Color.Yellow
            command.contains("purple") -> Color(0xFF9370DB)
            command.contains("pink") -> Color(0xFFFF00FF)
            command.contains("cyan") -> Color.Cyan
            command.contains("white") -> Color.White
            command.contains("black") -> Color.Black
            else -> null
        }
    }
}

/**
 * Voice command result
 */
sealed class VoiceCommand {
    data class ChangeTheme(val themeName: String) : VoiceCommand()
    data class MoveComponent(val componentName: String, val direction: String, val amount: Float) :
        VoiceCommand()

    data class RotateComponent(val componentName: String, val degrees: Float) : VoiceCommand()
    data class ChangeColor(val componentName: String, val color: Color) : VoiceCommand()
    data class ChangeOpacity(val componentName: String, val opacity: Float) : VoiceCommand()
    data class ChangeVisibility(val componentName: String, val visible: Boolean) : VoiceCommand()
    data class ScaleComponent(val componentName: String, val scale: Float) : VoiceCommand()
    data class ResetComponent(val componentName: String) : VoiceCommand()
    data class Invalid(val reason: String) : VoiceCommand()
}

