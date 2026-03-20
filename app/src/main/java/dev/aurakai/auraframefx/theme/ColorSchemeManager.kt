package dev.aurakai.auraframefx.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.network.model.Theme as NetworkTheme
import dev.aurakai.auraframefx.network.model.ThemeColors as NetworkThemeColors

/**
 * Manages color schemes for the application, including dynamic theming and color manipulation.
 * Works alongside ThemeManager to provide comprehensive theming capabilities.
 */
class ColorSchemeManager {

    /**
     * Converts a network theme to a Material3 ColorScheme
     */
    fun networkThemeToColorScheme(
        theme: NetworkTheme,
        isDarkTheme: Boolean = false,
    ): androidx.compose.material3.ColorScheme {
        val colors = theme.colors ?: return if (isDarkTheme) darkColorScheme() else lightColorScheme()

        return if (isDarkTheme) {
            darkColorScheme(
                primary = Color(android.graphics.Color.parseColor(colors.primary)),
                onPrimary = Color(android.graphics.Color.parseColor(colors.onPrimary)),
                primaryContainer = Color(android.graphics.Color.parseColor(colors.primary)),
                onPrimaryContainer = Color(android.graphics.Color.parseColor(colors.onPrimary)),
                secondary = Color(android.graphics.Color.parseColor(colors.secondary)),
                onSecondary = Color(android.graphics.Color.parseColor(colors.onSecondary ?: colors.onPrimary)),
                secondaryContainer = Color(android.graphics.Color.parseColor(colors.secondary)),
                onSecondaryContainer = Color(android.graphics.Color.parseColor(colors.onSecondary ?: colors.onPrimary)),
                background = Color(android.graphics.Color.parseColor(colors.background)),
                onBackground = Color(android.graphics.Color.parseColor(colors.onBackground)),
                surface = Color(android.graphics.Color.parseColor(colors.surface)),
                onSurface = Color(android.graphics.Color.parseColor(colors.onSurface)),
                error = Color(android.graphics.Color.parseColor(colors.error)),
                onError = Color(android.graphics.Color.parseColor(colors.onError))
            )
        } else {
            lightColorScheme(
                primary = Color(android.graphics.Color.parseColor(colors.primary)),
                onPrimary = Color(android.graphics.Color.parseColor(colors.onPrimary)),
                primaryContainer = Color(android.graphics.Color.parseColor(colors.primary)),
                onPrimaryContainer = Color(android.graphics.Color.parseColor(colors.onPrimary)),
                secondary = Color(android.graphics.Color.parseColor(colors.secondary)),
                onSecondary = Color(android.graphics.Color.parseColor(colors.onSecondary ?: colors.onPrimary)),
                secondaryContainer = Color(android.graphics.Color.parseColor(colors.secondary)),
                onSecondaryContainer = Color(android.graphics.Color.parseColor(colors.onSecondary ?: colors.onPrimary)),
                background = Color(android.graphics.Color.parseColor(colors.background)),
                onBackground = Color(android.graphics.Color.parseColor(colors.onBackground)),
                surface = Color(android.graphics.Color.parseColor(colors.surface)),
                onSurface = Color(android.graphics.Color.parseColor(colors.onSurface)),
                error = Color(android.graphics.Color.parseColor(colors.error)),
                onError = Color(android.graphics.Color.parseColor(colors.onError))
            )
        }
    }

    /**
     * Generates a color scheme based on a seed color
     */
    fun generateColorScheme(
        seedColor: Color,
        isDarkTheme: Boolean = false,
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
        themeName: String = "Custom Theme",
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
