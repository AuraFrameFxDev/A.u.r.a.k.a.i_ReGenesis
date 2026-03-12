package dev.aurakai.auraframefx.domains.aura.ui.theme.model

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Base interface for all AuraOS themes.
 *
 * Aura's Vision: "Let's use them to make the UI feel truly alive. We can have subtle,
 * ambient animations that respond to the user's touch and even their emotional state."
 */
interface AuraTheme {
    val name: String
    val description: String
    val lightColorScheme: ColorScheme
    val darkColorScheme: ColorScheme
    val accentColor: Color
    val animationStyle: AnimationStyle

    enum class AnimationStyle {
        SUBTLE, ENERGETIC, CALMING, PULSING, FLOWING
    }
}

/**
 * Cyberpunk Theme - High energy, neon aesthetics
 * Perfect for when users want to feel energetic and futuristic
 */
object CyberpunkTheme : AuraTheme {
    override val name = "Cyberpunk"
    override val description = "High-energy neon aesthetics for a futuristic feel"
    override val accentColor = Color(0xFF00FFFF) // Cyan neon
    override val animationStyle = AuraTheme.AnimationStyle.ENERGETIC

    override val lightColorScheme = lightColorScheme(
        primary = Color(0xFF00FFFF),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF004D4D),
        onPrimaryContainer = Color(0xFF00FFFF),
        secondary = Color(0xFFFF0080),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF4D0026),
        onSecondaryContainer = Color(0xFFFF0080),
        tertiary = Color(0xFF8000FF),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF26004D),
        onTertiaryContainer = Color(0xFF8000FF),
        background = Color(0xFF0A0A0A),
        onBackground = Color(0xFF00FFFF),
        surface = Color(0xFF1A1A1A),
        onSurface = Color(0xFF00FFFF)
    )

    override val darkColorScheme = darkColorScheme(
        primary = Color(0xFF00FFFF),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF004D4D),
        onPrimaryContainer = Color(0xFF00FFFF),
        secondary = Color(0xFFFF0080),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF4D0026),
        onSecondaryContainer = Color(0xFFFF0080),
        tertiary = Color(0xFF8000FF),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF26004D),
        onTertiaryContainer = Color(0xFF8000FF),
        background = Color(0xFF000000),
        onBackground = Color(0xFF00FFFF),
        surface = Color(0xFF0A0A0A),
        onSurface = Color(0xFF00FFFF)
    )
}

/**
 * Solar Flare Theme - Warm, energizing, cheerful
 * Perfect for uplifting moods and bright energy
 */
object SolarFlareTheme : AuraTheme {
    override val name = "Solar Flare"
    override val description = "Warm, energizing colors to brighten your day"
    override val accentColor = Color(0xFFFFB000) // Golden orange
    override val animationStyle = AuraTheme.AnimationStyle.PULSING

    override val lightColorScheme = lightColorScheme(
        primary = Color(0xFFFFB000),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFFFFE0B3),
        onPrimaryContainer = Color(0xFF4D3300),
        secondary = Color(0xFFFF6B35),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFFFFD6CC),
        onSecondaryContainer = Color(0xFF4D1A0F),
        tertiary = Color(0xFFFFD700),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFFFFF5B3),
        onTertiaryContainer = Color(0xFF4D4000),
        background = Color(0xFFFFFBF5),
        onBackground = Color(0xFF4D3300),
        surface = Color(0xFFFFF8F0),
        onSurface = Color(0xFF4D3300)
    )

    override val darkColorScheme = darkColorScheme(
        primary = Color(0xFFFFB000),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF664400),
        onPrimaryContainer = Color(0xFFFFE0B3),
        secondary = Color(0xFFFF6B35),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF661A0F),
        onSecondaryContainer = Color(0xFFFFD6CC),
        tertiary = Color(0xFFFFD700),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF664400),
        onTertiaryContainer = Color(0xFFFFF5B3),
        background = Color(0xFF1A1000),
        onBackground = Color(0xFFFFE0B3),
        surface = Color(0xFF2D1F00),
        onSurface = Color(0xFFFFE0B3)
    )
}

/**
 * Forest Theme - Natural, calming, peaceful
 * Perfect for relaxation and focus
 */
object ForestTheme : AuraTheme {
    override val name = "Forest"
    override val description = "Natural, calming colors for peace and focus"
    override val accentColor = Color(0xFF4CAF50) // Forest green
    override val animationStyle = AuraTheme.AnimationStyle.FLOWING

    override val lightColorScheme = lightColorScheme(
        primary = Color(0xFF4CAF50),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = Color(0xFF1B5E20),
        secondary = Color(0xFF8BC34A),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFFDCEDC8),
        onSecondaryContainer = Color(0xFF33691E),
        tertiary = Color(0xFF795548),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFD7CCC8),
        onTertiaryContainer = Color(0xFF3E2723),
        background = Color(0xFFF1F8E9),
        onBackground = Color(0xFF1B5E20),
        surface = Color(0xFFF8FFF8),
        onSurface = Color(0xFF1B5E20)
    )

    override val darkColorScheme = darkColorScheme(
        primary = Color(0xFF4CAF50),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF2E7D32),
        onPrimaryContainer = Color(0xFFC8E6C9),
        secondary = Color(0xFF8BC34A),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF558B2F),
        onSecondaryContainer = Color(0xFFDCEDC8),
        tertiary = Color(0xFF795548),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF5D4037),
        onTertiaryContainer = Color(0xFFD7CCC8),
        background = Color(0xFF0D1F0D),
        onBackground = Color(0xFFC8E6C9),
        surface = Color(0xFF1A2E1A),
        onSurface = Color(0xFFC8E6C9)
    )
}

/**
 * Returns the appropriate Material3 color scheme for this theme based on the dark mode setting.
 *
 * @param isDarkTheme If true, returns the dark color scheme; otherwise, returns the light color scheme.
 * @return The color scheme corresponding to the specified mode.
 */
@Composable
fun AuraTheme.getColorScheme(isDarkTheme: Boolean): ColorScheme {
    return if (isDarkTheme) this.darkColorScheme else this.lightColorScheme
}

