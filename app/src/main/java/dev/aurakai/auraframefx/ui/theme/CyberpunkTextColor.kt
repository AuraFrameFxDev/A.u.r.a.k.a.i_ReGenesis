package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.aura.theme.ChromaCoreColors

/**
 * Cyberpunk text color scheme for themed text components.
 */
enum class CyberpunkTextColor(val color: Color) {
    Primary(Color(0xFF00FBFF)),
    Secondary(Color(0xFFFF00FF)),
    Tertiary(Color(0xFF9D00FF)),
    White(Color.White),
    Warning(Color(0xFFFFFF00)),
    Error(Color(0xFFFF0000)),
    Success(Color(0xFF00FF00))
}
