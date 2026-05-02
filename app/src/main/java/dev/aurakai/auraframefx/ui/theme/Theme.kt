package dev.aurakai.auraframefx.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.models.Emotion
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AuraMoodViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.*
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Theme
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Color as ThemeColor
import dev.aurakai.auraframefx.ui.theme.model.CyberpunkColorScheme
import dev.aurakai.auraframefx.ui.theme.model.SolarizedColorScheme

private val DarkColorScheme = darkColorScheme(
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

val LocalMoodGlow = compositionLocalOf { Color.Transparent }
val LocalMoodState = compositionLocalOf { Emotion.NEUTRAL }

@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    moodViewModel: AuraMoodViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val currentEmotion by moodViewModel.moodState.collectAsState()
    val themeState by themeViewModel.theme.collectAsState(initial = Theme.DARK)
    val colorState by themeViewModel.color.collectAsState(initial = ThemeColor.BLUE)

    val useDarkTheme = when (themeState) {
        Theme.LIGHT -> false
        Theme.DARK, Theme.CYBERPUNK -> true
        Theme.SOLARIZED -> false
    }

    val baseColorScheme = when (themeState) {
        Theme.CYBERPUNK -> CyberpunkColorScheme
        Theme.SOLARIZED -> SolarizedColorScheme
        else -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (useDarkTheme) DarkColorScheme else LightColorScheme
            }
        }
    }

    val finalColorScheme = baseColorScheme.copy(
        primary = when (colorState) {
            ThemeColor.RED -> NeonRed
            ThemeColor.GREEN -> NeonGreen
            ThemeColor.BLUE -> NeonBlue
            else -> baseColorScheme.primary
        }
    )

    val glowColor = getMoodGlowColor(currentEmotion, 0.5f, baseColorScheme)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = baseColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    CompositionLocalProvider(
        LocalMoodGlow provides glowColor,
        LocalMoodState provides currentEmotion
    ) {
        MaterialTheme(
            colorScheme = finalColorScheme,
            typography = Typography,
            content = content
        )
    }
}

private fun getMoodGlowColor(
    emotion: Emotion,
    intensity: Float,
    baseColorScheme: ColorScheme,
): Color {
    val baseAlpha = (intensity * 0.5f).coerceIn(0.1f, 0.7f)

    val color = when (emotion) {
        Emotion.HAPPY -> Color(0xFFFFD700)
        Emotion.EXCITED -> Color(0xFFFF4500)
        Emotion.ANGRY -> Color(0xFFDC143C)
        Emotion.SERENE -> Color(0xFF00CED1)
        Emotion.CONTEMPLATIVE -> Color(0xFF9932CC)
        Emotion.MISCHIEVOUS -> Color(0xFFADFF2F)
        Emotion.FOCUSED -> Color(0xFF4682B4)
        Emotion.CONFIDENT -> Color(0xFFDB7093)
        Emotion.MYSTERIOUS -> Color(0xFF2F4F4F)
        Emotion.MELANCHOLIC -> Color(0xFF6A5ACD)
        else -> baseColorScheme.primary
    }
    return color.copy(alpha = baseAlpha)
}
