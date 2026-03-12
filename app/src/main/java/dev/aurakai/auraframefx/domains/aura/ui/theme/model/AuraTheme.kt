package dev.aurakai.auraframefx.domains.aura.ui.theme.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Aura theme wrapper for Material3
 */
/**
 * Aura theme wrapper for Material3
 */
@Composable
fun AuraAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

/**
 * Aura Theme Data
 */
data class AuraThemeData(
    val accentColor: Color = AuraColors.Primary,
    val animationStyle: AnimationStyle = AnimationStyle.FLOWING
) {
    enum class AnimationStyle {
        ENERGETIC, CALMING, FLOWING, PULSING, SUBTLE
    }
}

/**
 * Aura color palette
 */
object AuraColors {
    val Primary = Color(0xFF00D9FF)
    val Secondary = Color(0xFFFF00D9)
    val Tertiary = Color(0xFFD9FF00)
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF1A1A1A)
}

