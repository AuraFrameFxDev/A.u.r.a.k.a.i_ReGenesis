package dev.aurakai.auraframefx.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 🏙️ CYBERPUNK THEME COMPONENTS
 * Unified enums for text styling and colors in ReGenesis.
 */

enum class CyberpunkTextColor(val color: Color) {
    Primary(Color.NeonCyan),
    Secondary(Color.NeonBlue),
    Tertiary(Color.NeonPurple),
    Warning(Color(0xFFFFD700)),
    Error(Color.Red),
    Success(Color(0xFF00FF85)),
    White(Color.White)
}

enum class CyberpunkTextStyle(val textStyle: TextStyle) {
    Title(TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)),
    Heading(TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)),
    Body(TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)),
    Label(TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)),
    Caption(TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal))
}
