package dev.aurakai.colorblendr

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 🎨 CHROMACORE - Advanced Color Manipulation Engine
 * 
 * ColorBlendr + Monet Engine combined.
 * Powers Aura's color customization domain.
 */
object ChromaCore {

    /**
     * Blend two colors with advanced perceptual mixing
     */
    fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
        val clampedRatio = ratio.coerceIn(0f, 1f)
        val invRatio = 1f - clampedRatio

        // Perceptual blending in linear RGB space
        val r = sqrt(color1.red * color1.red * invRatio + color2.red * color2.red * clampedRatio)
        val g =
            sqrt(color1.green * color1.green * invRatio + color2.green * color2.green * clampedRatio)
        val b =
            sqrt(color1.blue * color1.blue * invRatio + color2.blue * color2.blue * clampedRatio)
        val a = color1.alpha * invRatio + color2.alpha * clampedRatio

        return Color(r, g, b, a)
    }

    /**
     * Generate harmonic color palettes
     */
    fun generateHarmonics(baseColor: Color): List<Color> {
        val hsv = rgbToHsv(baseColor)
        return listOf(
            baseColor,
            hsvToRgb(hsv.copy(hue = (hsv.hue + 30f) % 360f)),   // Analogous
            hsvToRgb(hsv.copy(hue = (hsv.hue + 180f) % 360f)),  // Complementary
            hsvToRgb(hsv.copy(hue = (hsv.hue + 120f) % 360f)),  // Triadic 1
            hsvToRgb(hsv.copy(hue = (hsv.hue + 240f) % 360f))   // Triadic 2
        )
    }

    /**
     * Adjust color saturation
     */
    fun adjustSaturation(color: Color, factor: Float): Color {
        val hsv = rgbToHsv(color)
        return hsvToRgb(hsv.copy(saturation = (hsv.saturation * factor).coerceIn(0f, 1f)))
    }

    /**
     * Adjust color brightness/value
     */
    fun adjustBrightness(color: Color, factor: Float): Color {
        val hsv = rgbToHsv(color)
        return hsvToRgb(hsv.copy(value = (hsv.value * factor).coerceIn(0f, 1f)))
    }

    /**
     * Rotate hue by degrees
     */
    fun rotateHue(color: Color, degrees: Float): Color {
        val hsv = rgbToHsv(color)
        return hsvToRgb(hsv.copy(hue = (hsv.hue + degrees) % 360f))
    }

    /**
     * Set alpha channel
     */
    fun withAlpha(color: Color, alpha: Float): Color {
        return color.copy(alpha = alpha.coerceIn(0f, 1f))
    }

    // HSV conversion utilities
    private data class HSV(
        val hue: Float,
        val saturation: Float,
        val value: Float,
        val alpha: Float = 1f
    )

    private fun rgbToHsv(color: Color): HSV {
        val r = color.red
        val g = color.green
        val b = color.blue

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val hue = when {
            delta == 0f -> 0f
            max == r -> 60f * (((g - b) / delta) % 6f)
            max == g -> 60f * (((b - r) / delta) + 2f)
            else -> 60f * (((r - g) / delta) + 4f)
        }.let { if (it < 0) it + 360f else it }

        val saturation = if (max == 0f) 0f else delta / max
        val value = max

        return HSV(hue, saturation, value, color.alpha)
    }

    private fun hsvToRgb(hsv: HSV): Color {
        val c = hsv.value * hsv.saturation
        val x = c * (1f - abs((hsv.hue / 60f) % 2f - 1f))
        val m = hsv.value - c

        val (r, g, b) = when {
            hsv.hue < 60f -> Triple(c, x, 0f)
            hsv.hue < 120f -> Triple(x, c, 0f)
            hsv.hue < 180f -> Triple(0f, c, x)
            hsv.hue < 240f -> Triple(0f, x, c)
            hsv.hue < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(r + m, g + m, b + m, hsv.alpha)
    }

    // Pulse feedback for InterfaceForge
    fun applyPulse(pulseType: String) {
        println("ChromaCore: Pulse applied - $pulseType")
    }
}

/**
 * Darken a color
 */
fun darken(color: Color, factor: Float): Color {
    return ChromaCore.adjustBrightness(color, 1f - factor.coerceIn(0f, 1f))
}

/**
 * Lighten a color
 */
fun lighten(color: Color, factor: Float): Color {
    return ChromaCore.adjustBrightness(color, 1f + factor.coerceIn(0f, 1f))
}
