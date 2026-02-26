package dev.aurakai.colorblendr

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 🎨 MONET ENGINE - Material You Color Science
 * 
 * Generates dynamic color schemes from source colors using CAM16 and HCT.
 * Based on Android 12+ Material You theming algorithm.
 */
object MonetEngine {

    /**
     * Generate Material You tonal palette from source color
     */
    fun generateTonalPalette(sourceColor: Color): TonalPalette {
        val hct = rgbToHct(sourceColor)

        return TonalPalette(
            tone0 = hctToRgb(hct.copy(tone = 0f)),
            tone10 = hctToRgb(hct.copy(tone = 10f)),
            tone20 = hctToRgb(hct.copy(tone = 20f)),
            tone30 = hctToRgb(hct.copy(tone = 30f)),
            tone40 = hctToRgb(hct.copy(tone = 40f)),
            tone50 = hctToRgb(hct.copy(tone = 50f)),
            tone60 = hctToRgb(hct.copy(tone = 60f)),
            tone70 = hctToRgb(hct.copy(tone = 70f)),
            tone80 = hctToRgb(hct.copy(tone = 80f)),
            tone90 = hctToRgb(hct.copy(tone = 90f)),
            tone95 = hctToRgb(hct.copy(tone = 95f)),
            tone99 = hctToRgb(hct.copy(tone = 99f)),
            tone100 = hctToRgb(hct.copy(tone = 100f))
        )
    }

    /**
     * Generate complete Material You scheme (primary, secondary, tertiary, etc.)
     */
    fun generateColorScheme(sourceColor: Color): MaterialYouScheme {
        val hct = rgbToHct(sourceColor)

        // Primary palette (source color)
        val primaryPalette = generateTonalPalette(sourceColor)

        // Secondary palette (rotated hue)
        val secondaryHue = (hct.hue + 60f) % 360f
        val secondaryPalette = generateTonalPalette(hctToRgb(HCT(secondaryHue, hct.chroma, 50f)))

        // Tertiary palette (rotated further)
        val tertiaryHue = (hct.hue + 120f) % 360f
        val tertiaryPalette =
            generateTonalPalette(hctToRgb(HCT(tertiaryHue, hct.chroma * 0.7f, 50f)))

        // Neutral palette (desaturated)
        val neutralPalette = generateTonalPalette(hctToRgb(HCT(hct.hue, 4f, 50f)))

        return MaterialYouScheme(
            primary = primaryPalette,
            secondary = secondaryPalette,
            tertiary = tertiaryPalette,
            neutral = neutralPalette
        )
    }

    // HCT color space (Hue, Chroma, Tone)
    private data class HCT(val hue: Float, val chroma: Float, val tone: Float)

    private fun rgbToHct(color: Color): HCT {
        val r = color.red
        val g = color.green
        val b = color.blue

        // RGB to XYZ
        val x = 0.4124f * r + 0.3576f * g + 0.1805f * b
        val y = 0.2126f * r + 0.7152f * g + 0.0722f * b
        val z = 0.0193f * r + 0.1192f * g + 0.9505f * b

        // XYZ to LAB
        val fx = if (x > 0.008856f) x.pow(1f / 3f) else (7.787f * x + 16f / 116f)
        val fy = if (y > 0.008856f) y.pow(1f / 3f) else (7.787f * y + 16f / 116f)
        val fz = if (z > 0.008856f) z.pow(1f / 3f) else (7.787f * z + 16f / 116f)

        val l = 116f * fy - 16f
        val a = 500f * (fx - fy)
        val bLab = 200f * (fy - fz)

        // LAB to HCT
        val c = sqrt(a * a + bLab * bLab)
        val h = (atan2(bLab, a) * 180f / PI.toFloat()).let {
            if (it < 0) it + 360f else it
        }

        return HCT(h, c, l)
    }

    private fun hctToRgb(hct: HCT): Color {
        val h = hct.hue * PI.toFloat() / 180f
        val c = hct.chroma
        val l = hct.tone

        // HCT to LAB
        val a = c * cos(h)
        val bLab = c * sin(h)

        // LAB to XYZ
        val fy = (l + 16f) / 116f
        val fx = a / 500f + fy
        val fz = fy - bLab / 200f

        val x = if (fx > 0.2069f) fx.pow(3f) else (fx - 16f / 116f) / 7.787f
        val y = if (fy > 0.2069f) fy.pow(3f) else (fy - 16f / 116f) / 7.787f
        val z = if (fz > 0.2069f) fz.pow(3f) else (fz - 16f / 116f) / 7.787f

        // XYZ to RGB
        val r = (3.2406f * x - 1.5372f * y - 0.4986f * z).coerceIn(0f, 1f)
        val g = (-0.9689f * x + 1.8758f * y + 0.0415f * z).coerceIn(0f, 1f)
        val b = (0.0557f * x - 0.2040f * y + 1.0570f * z).coerceIn(0f, 1f)

        return Color(r, g, b)
    }
}

data class TonalPalette(
    val tone0: Color,
    val tone10: Color,
    val tone20: Color,
    val tone30: Color,
    val tone40: Color,
    val tone50: Color,
    val tone60: Color,
    val tone70: Color,
    val tone80: Color,
    val tone90: Color,
    val tone95: Color,
    val tone99: Color,
    val tone100: Color
)

data class MaterialYouScheme(
    val primary: TonalPalette,
    val secondary: TonalPalette,
    val tertiary: TonalPalette,
    val neutral: TonalPalette
)
