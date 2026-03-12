package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Glassmorphic color palette
 */
object GlassPalette {
    val TransparentWhite = Color(0x80FFFFFF)
    val FrostWhite = Color(0xCCFFFFFF)
    val DeepPurple = Color(0xFF3D2C8D)
    val NeonBlueGlass = Color(0xFF00C2FF)
    val SoftBlack = Color(0xFF0C0F14)
    val SlateGray = Color(0xFF2B2F3A)
}

@Composable
fun GlassmorphicTheme(dark: Boolean = true, content: @Composable () -> Unit) {
    val colors = if (dark) {
        darkColorScheme(
            primary = GlassPalette.NeonBlueGlass,
            secondary = GlassPalette.DeepPurple,
            background = GlassPalette.SoftBlack,
            surface = GlassPalette.SlateGray,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = GlassPalette.NeonBlueGlass,
            secondary = GlassPalette.DeepPurple,
            background = Color.White,
            surface = GlassPalette.FrostWhite,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}

