package dev.aurakai.colorblendr

import androidx.compose.ui.graphics.Color

/**
 * A simple color blending utility that provides basic color manipulation functions.
 * This is a local implementation of the ChromaCore library.
 */
object ChromaCore {


    /**
     * Blends two colors together using the specified ratio.
     * @param color1 The first color to blend.
     * @param color2 The second color to blend.
     * @param ratio The ratio of the blend (0.0 to 1.0). 0.0 means all color1, 1.0 means all color2.
     * @return The blended color.
     */
    private fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
        val clampedRatio = ratio.coerceIn(0f, 1f)
        val invRatio = 1f - clampedRatio

        return Color(
            red = (color1.red * invRatio + color2.red * clampedRatio).coerceIn(0f, 1f),
            green = (color1.green * invRatio + color2.green * clampedRatio).coerceIn(0f, 1f),
            blue = (color1.blue * invRatio + color2.blue * clampedRatio).coerceIn(0f, 1f),
            alpha = (color1.alpha * invRatio + color2.alpha * clampedRatio).coerceIn(0f, 1f)
        )
    }

    /**
     * Creates a color with the specified alpha value.
     * @param color The base color.
     * @param alpha The alpha value (0.0 to 1.0).
     * @return A new color with the specified alpha.
     */
    fun withAlpha(color: Color, alpha: Float): Color {
        return color.copy(alpha = alpha.coerceIn(0f, 1f))
    }

}

/**
 * Darkens a color by the specified factor.
 * @param color The color to darken.
 * @param factor The darkening factor (0.0 to 1.0).
 * @return The darkened color.
 */
fun darken(color: Color, factor: Float): Color {
    val clampedFactor = factor.coerceIn(0f, 1f)
    return Color(
        red = (color.red * (1 - clampedFactor)).coerceIn(0f, 1f),
        green = (color.green * (1 - clampedFactor)).coerceIn(0f, 1f),
        blue = (color.blue * (1 - clampedFactor)).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

/**
 * Lightens a color by the specified factor.
 * @param color The color to lighten.
 * @param factor The lightening factor (0.0 to 1.0).
 * @return The lightened color.
 */
fun lighten(color: Color, factor: Float): Color {
    val clampedFactor = factor.coerceIn(0f, 1f)
    return Color(
        red = (color.red + (1 - color.red) * clampedFactor).coerceIn(0f, 1f),
        green = (color.green + (1 - color.green) * clampedFactor).coerceIn(0f, 1f),
        blue = (color.blue + (1 - color.blue) * clampedFactor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}
