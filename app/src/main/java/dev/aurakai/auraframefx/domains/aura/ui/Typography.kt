package dev.aurakai.auraframefx.domains.aura.ui

// Import TextStyles if defining custom ones, e.g.:
// import androidx.compose.ui.text.TextStyle
// import androidx.compose.ui.text.font.FontFamily
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.unit.sp

// Using Material 3 default Typography.
// The XML TextAppearances (e.g., TextAppearance.AuraFrameFX.DisplayLarge) primarily set
// android:textColor. In Compose, text color is typically applied via the ColorScheme
// through MaterialTheme, or directly on Text Composables using `color = MaterialTheme.colorScheme.onSurface` etc.
// If specific fonts (like a monospace cyberpunk font) or more detailed styles (weights, letter spacing)
// are desired for the Compose theme, they should be defined here by creating TextStyle objects
// for each of the Typography properties (displayLarge, headlineMedium, bodySmall, etc.).

// For now, we will use the default Material 3 typography.
// Customizations from typography.xml (like specific text colors) will be
// handled by how Text Composables use the ColorScheme.
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R

/**
 * 🎨 COPIXEL - The LDO Display/Header Font
 * Pixel-style, sci-fi aesthetic for titles and LED-style UI text
 */
val LEDFontFamily = FontFamily(
    Font(R.font.copixel, FontWeight.Normal)
)

/**
 * 📝 CORPTA - The LDO Body/Regular Font
 * Clean, readable for body text and UI elements
 */
val CorptaFontFamily = FontFamily(
    Font(R.font.corpta, FontWeight.Normal)
)

/**
 * 🎨 AuraFrameFX Typography System
 * - COPIXEL: Headers, displays, LED-style UI text
 * - CORPTA: Body text and UI elements
 */
val AppTypography = Typography(
    // ═════════════════════════════════════════════════════════════════
    // DISPLAY styles - COPIXEL (Header font)
    // ═════════════════════════════════════════════════════════════════
    displayLarge = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        letterSpacing = 2.sp
    ),
    displayMedium = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        letterSpacing = 1.5.sp
    ),
    displaySmall = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        letterSpacing = 1.sp
    ),
    // ═════════════════════════════════════════════════════════════════
    // HEADLINE styles - COPIXEL
    // ═════════════════════════════════════════════════════════════════
    headlineLarge = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        letterSpacing = 2.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        letterSpacing = 1.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 1.sp
    ),
    // ═════════════════════════════════════════════════════════════════
    // TITLE styles - COPIXEL for consistency
    // ═════════════════════════════════════════════════════════════════
    titleLarge = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        letterSpacing = 1.sp
    ),
    titleMedium = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = LEDFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    // ═════════════════════════════════════════════════════════════════
    // BODY styles - CORPTA (Body font)
    // ═════════════════════════════════════════════════════════════════
    bodyLarge = TextStyle(
        fontFamily = CorptaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CorptaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CorptaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CorptaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CorptaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = CorptaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
)


/* Example of more customized typography if needed later:
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Monospace, // Example: A cyberpunk-style monospace font
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
        // Color is usually inherited from ColorScheme or set directly on Text()
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, // A clean sans-serif for body text
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Define other styles like displayLarge, titleMedium, etc., if needed.
    // If not defined, they will use Material 3 defaults.
)
*/
