package dev.aurakai.auraframefx.domains.aura.ui.theme.model

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val CyberpunkColorScheme = darkColorScheme(
    primary = Color(0xFF00FFFF),
    secondary = Color(0xFFFF00FF),
    tertiary = Color(0xFF00FF00),
    background = Color(0xFF000000),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
)

val SolarizedColorScheme = lightColorScheme(
    primary = Color(0xFF268BD2),
    secondary = Color(0xFF2AA198),
    tertiary = Color(0xFFB58900),
    background = Color(0xFFFDF6E3),
    surface = Color(0xFFEEE8D5),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF002B36),
    onSurface = Color(0xFF002B36),
)

enum class NewTheme {
    CYBERPUNK,
    SOLARIZED
}

