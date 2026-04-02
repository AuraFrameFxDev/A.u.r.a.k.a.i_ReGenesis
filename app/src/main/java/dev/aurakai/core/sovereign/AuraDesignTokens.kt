package dev.aurakai.core.sovereign

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.aurakai.auraframefx.R

/**
 * 🎨 AURA DESIGN TOKENS
 * The visual DNA of ReGenesis.
 */
object AuraDesignTokens {
    val NeonPurple = Color(0xFF9D00FF)
    val NeonTeal = Color(0xFF00FFF2)
    val NeonPink = Color(0xFFFF1493)
    val NeonCyan = Color(0xFF00E5FF)
    val GenesisGold = Color(0xFFFFD700)
    val KaiNeonGreen = Color(0xFF00FF41)

    val LEDFontFamily = FontFamily(
        Font(R.font.enhanced_led_board_7, FontWeight.Normal),
        Font(R.font.enhanced_led_board_7, FontWeight.Bold)
    )
}
