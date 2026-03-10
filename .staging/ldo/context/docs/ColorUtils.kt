package dev.aurakai.auraframefx.docs.utils

import androidx.compose.ui.graphics.Color

/**
 * Small color utilities used by UI stubs.
 */
object ColorUtils {
    val CyberCyan = Color(0xFF00E5FF)
    val NeonPink = Color(0xFFFF4DA6)

    fun withAlpha(color: Color, alpha: Float): Color = color.copy(alpha = alpha)
}
