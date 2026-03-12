package dev.aurakai.auraframefx.domains.aura.ui.theme.manager

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import java.util.Locale
import dev.aurakai.auraframefx.domains.genesis.network.model.Theme as NetworkTheme
import dev.aurakai.auraframefx.domains.genesis.network.model.ThemeColors as NetworkThemeColors

/**
 * Manages color schemes for the application, including dynamic theming and color manipulation.
 * Works alongside ThemeManager to provide comprehensive theming capabilities.
 */
open class ColorSchemeManager {

    /**
     * Converts a network theme to a Material3 ColorScheme
     */
    fun networkThemeToColorScheme(
        theme: NetworkTheme,
        isDarkTheme: Boolean = false
    ): androidx.compose.material3.ColorScheme {
        val colors =
            theme.colors ?: return if (isDarkTheme) darkColorScheme() else lightColorScheme()

        return if (isDarkTheme) {
            darkColorScheme(
                primary = Color(colors.primary.toColorInt()),
                onPrimary = Color(colors.onPrimary.toColorInt()),
                primaryContainer = Color(colors.primary.toColorInt()),
                onPrimaryContainer = Color(colors.onPrimary.toColorInt()),
                secondary = Color(colors.secondary.toColorInt()),
                onSecondary = Color(
                    (colors.onSecondary ?: colors.onPrimary).toColorInt()
                ),
                secondaryContainer = Color(colors.secondary.toColorInt()),
                onSecondaryContainer = Color(
                    (colors.onSecondary ?: colors.onPrimary).toColorInt()
                ),
                background = Color(colors.background.toColorInt()),
                onBackground = Color(colors.onBackground.toColorInt()),
                surface = Color(colors.surface.toColorInt()),
                onSurface = Color(colors.onSurface.toColorInt()),
                error = Color(colors.error.toColorInt()),
                onError = Color(colors.onError.toColorInt())
            )
        } else {
            lightColorScheme(
                primary = Color(colors.primary.toColorInt()),
                onPrimary = Color(colors.onPrimary.toColorInt()),
                primaryContainer = Color(colors.primary.toColorInt()),
                onPrimaryContainer = Color(colors.onPrimary.toColorInt()),
                secondary = Color(colors.secondary.toColorInt()),
                onSecondary = Color(
                    colors.onSecondary.toColorInt()
                ),
                secondaryContainer = Color(colors.secondary.toColorInt()),
                onSecondaryContainer = Color(
                    colors.onSecondary.toColorInt()
                ),
                background = Color(colors.background.toColorInt()),
                onBackground = Color(colors.onBackground.toColorInt()),
                surface = Color(colors.surface.toColorInt()),
                onSurface = Color(colors.onSurface.toColorInt()),
                error = Color(colors.error.toColorInt()),
                onError = Color(colors.onError.toColorInt())
            )
        }
    }

    /**
     * Generates a color scheme based on a seed color
     */
    fun generateColorScheme(
        seedColor: Color,
        isDarkTheme: Boolean = false
    ): androidx.compose.material3.ColorScheme {
        return if (isDarkTheme) {
            darkColorScheme(
                primary = seedColor,
                secondary = seedColor.copy(alpha = 0.7f),
                tertiary = seedColor.copy(alpha = 0.5f)
            )
        } else {
            lightColorScheme(
                primary = seedColor,
                secondary = seedColor.copy(alpha = 0.7f),
                tertiary = seedColor.copy(alpha = 0.5f)
            )
        }
    }

    /**
     * Converts a Compose Color to a hex string
     */
    fun colorToHex(color: Color): String {
        return String.format(
            Locale.US,
            "#%02X%02X%02X",
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
    }

    /**
     * Creates a NetworkTheme from a Compose ColorScheme
     */
    fun colorSchemeToNetworkTheme(
        colorScheme: androidx.compose.material3.ColorScheme,
        themeName: String = "Custom Theme"
    ): NetworkTheme {
        return NetworkTheme(
            id = "custom_${System.currentTimeMillis()}",
            name = themeName,
            isActive = true,
            colors = NetworkThemeColors(
                primary = colorToHex(colorScheme.primary),
                secondary = colorToHex(colorScheme.secondary),
                background = colorToHex(colorScheme.background),
                surface = colorToHex(colorScheme.surface),
                error = colorToHex(colorScheme.error),
                onPrimary = colorToHex(colorScheme.onPrimary),
                onSecondary = colorToHex(colorScheme.onSecondary),
                onBackground = colorToHex(colorScheme.onBackground),
                onSurface = colorToHex(colorScheme.onSurface),
                onError = colorToHex(colorScheme.onError)
            )
        )
    }
}

