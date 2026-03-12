package dev.aurakai.auraframefx.domains.aura.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.models.Emotion
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Theme
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Color as ThemeColor

// Type alias for Typography
val AppTypography = Typography

// Cyberpunk color scheme
val CyberpunkColorScheme = darkColorScheme(
    primary = NeonTeal,
    onPrimary = OnPrimary,
    primaryContainer = NeonTeal.copy(alpha = 0.2f),
    onPrimaryContainer = OnPrimary,
    secondary = NeonPurple,
    onSecondary = OnSecondary,
    secondaryContainer = NeonPurple.copy(alpha = 0.2f),
    onSecondaryContainer = OnSecondary,
    tertiary = NeonBlue,
    onTertiary = OnTertiary,
    tertiaryContainer = NeonBlue.copy(alpha = 0.2f),
    onTertiaryContainer = OnTertiary,
    background = DarkBackground,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = ErrorColor,
    onError = OnPrimary,
    errorContainer = ErrorColor.copy(alpha = 0.2f),
    onErrorContainer = OnPrimary,
    outline = OnSurfaceVariant,
    outlineVariant = SurfaceVariant
)

// Solarized color scheme
val SolarizedColorScheme = lightColorScheme(
    primary = Color(0xFF268BD2),
    onPrimary = Color(0xFFFDF6E3),
    primaryContainer = Color(0xFF268BD2).copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF002B36),
    secondary = Color(0xFF2AA198),
    onSecondary = Color(0xFFFDF6E3),
    secondaryContainer = Color(0xFF2AA198).copy(alpha = 0.2f),
    onSecondaryContainer = Color(0xFF002B36),
    tertiary = Color(0xFFD33682),
    onTertiary = Color(0xFFFDF6E3),
    tertiaryContainer = Color(0xFFD33682).copy(alpha = 0.2f),
    onTertiaryContainer = Color(0xFF002B36),
    background = Color(0xFFFDF6E3),
    onBackground = Color(0xFF002B36),
    surface = Color(0xFFEEE8D5),
    onSurface = Color(0xFF002B36),
    surfaceVariant = Color(0xFFEEE8D5),
    onSurfaceVariant = Color(0xFF657B83),
    error = Color(0xFFDC322F),
    onError = Color(0xFFFDF6E3),
    errorContainer = Color(0xFFDC322F).copy(alpha = 0.2f),
    onErrorContainer = Color(0xFF002B36),
    outline = Color(0xFF93A1A1),
    outlineVariant = Color(0xFFEEE8D5)
)

// Stub AuraMoodViewModel for now - this should be properly implemented
class AuraMoodViewModel : androidx.lifecycle.ViewModel() {
    data class MoodState(val emotion: Emotion = Emotion.NEUTRAL, val intensity: Float = 0.5f)

    val moodState = kotlinx.coroutines.flow.MutableStateFlow(MoodState())
}


private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = LightOnPrimary,

    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = LightOnSecondary,

    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiary.copy(alpha = 0.2f),
    onTertiaryContainer = LightOnTertiary,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    error = ErrorColor,
    onError = LightOnError,
    errorContainer = ErrorColor.copy(alpha = 0.2f),
    onErrorContainer = LightOnError,

    outline = LightOnSurfaceVariant,
    outlineVariant = LightSurfaceVariant
)

// Let's define a CompositionLocal to provide the mood-based color
val LocalMoodGlow = compositionLocalOf { Color.Transparent }
val LocalMoodState = compositionLocalOf { Emotion.NEUTRAL }

/**
 * Applies the AuraFrameFX theme and mood-adaptive dynamic theming to the provided composable content.
 *
 * Selects and applies a color scheme (dark, light, or dynamic based on device support and parameters), updates the system status bar appearance, and supplies mood-driven glow color and emotion state to the composition. Integrates Aura's mood system for adaptive UI theming.
 *
 * @param darkTheme Whether to use the dark theme; defaults to the system setting.
 * @param dynamicColor Whether to enable dynamic color schemes on supported devices (Android 12+); defaults to true.
 * @param moodViewModel ViewModel providing the current mood state.
 * @param content The composable content to which the theme and mood context are applied.
 */
@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    moodViewModel: AuraMoodViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val mood by moodViewModel.moodState.collectAsState()
    val theme by themeViewModel.theme.collectAsState()
    val color by themeViewModel.color.collectAsState()

    val useDarkTheme = when (theme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.CYBERPUNK -> true
        Theme.SOLARIZED -> false
        else -> darkTheme
    }

    val baseColorScheme = when (theme) {
        Theme.CYBERPUNK -> CyberpunkColorScheme
        Theme.SOLARIZED -> SolarizedColorScheme
        else -> when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                    context
                )
            }

            useDarkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    val finalColorScheme = baseColorScheme.copy(
        primary = when (color) {
            ThemeColor.RED -> NeonRed
            ThemeColor.GREEN -> NeonGreen
            ThemeColor.BLUE -> NeonBlue
            else -> baseColorScheme.primary
        }
    )

    // The dynamic glow color is derived from Aura's current mood
    val glowColor = getMoodGlowColor(mood.emotion, mood.intensity, baseColorScheme)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = baseColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalMoodGlow provides glowColor,
        LocalMoodState provides mood.emotion
    ) {
        MaterialTheme(
            colorScheme = finalColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

/**
 * Computes the color to use for a mood glow effect based on the specified emotion, intensity, and color scheme.
 *
 * The returned color is selected according to the emotion, with its transparency proportional to the intensity. If the emotion is not recognized, the primary color of the provided color scheme is used with reduced alpha.
 *
 * @param emotion The current emotion to represent.
 * @param intensity The strength of the emotion, affecting the alpha of the color.
 * @param baseColorScheme The color scheme to use for fallback and context.
 * @return The color to use for the mood glow effect.
 */
private fun getMoodGlowColor(
    emotion: Emotion,
    intensity: Float,
    baseColorScheme: androidx.compose.material3.ColorScheme,
): Color {
    val baseAlpha = (intensity * 0.5f).coerceIn(0.1f, 0.7f)

    val color = when (emotion) {
        Emotion.HAPPY -> Color(0xFFFFD700) // Gold
        Emotion.EXCITED -> Color(0xFFFF4500) // OrangeRed
        Emotion.ANGRY -> Color(0xFFDC143C) // Crimson
        Emotion.SERENE -> Color(0xFF00CED1) // DarkTurquoise
        Emotion.CONTEMPLATIVE -> Color(0xFF9932CC) // DarkOrchid
        Emotion.MISCHIEVOUS -> Color(0xFFADFF2F) // GreenYellow
        Emotion.FOCUSED -> Color(0xFF4682B4) // SteelBlue
        Emotion.CONFIDENT -> Color(0xFFDB7093) // PaleVioletRed
        Emotion.MYSTERIOUS -> Color(0xFF2F4F4F) // DarkSlateGray
        Emotion.MELANCHOLIC -> Color(0xFF6A5ACD) // SlateBlue
        else -> baseColorScheme.primary
    }
    return color.copy(alpha = baseAlpha)
}

