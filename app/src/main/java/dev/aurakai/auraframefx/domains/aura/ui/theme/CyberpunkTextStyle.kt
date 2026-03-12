package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Cyberpunk text styles for themed text components.
 */
enum class CyberpunkTextStyle(val textStyle: TextStyle) {
    Title(TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)),
    Heading(TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.75.sp)),
    Body(TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.5.sp)),
    Label(TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)),
    Caption(TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.25.sp))
}

