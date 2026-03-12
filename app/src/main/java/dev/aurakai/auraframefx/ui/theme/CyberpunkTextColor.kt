package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.ui.*

/**
 * Cyberpunk text color scheme for themed text components.
 */
enum class CyberpunkTextColor(val color: Color) {
    Primary(CyberpunkCyan),
    Secondary(CyberpunkPink),
    Tertiary(CyberpunkPurple),
    White(Color.White),
    Warning(NeonYellow),
    Error(NeonRed),
    Success(NeonGreen)
}
