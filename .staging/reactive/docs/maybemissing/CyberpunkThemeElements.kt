package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

// Definitions based on usage in HomeScreen.kt and existing Color.kt / Typography.kt

sealed class CyberpunkTextColor(val color: Color) {
    object Primary : CyberpunkTextColor(OnSurface) // Example: NeonTeal via OnSurface
    object Secondary : CyberpunkTextColor(NeonPurple) // Direct color
    object Warning : CyberpunkTextColor(ErrorColor) // Direct color (NeonRed)
    object White : CyberpunkTextColor(Color.White) // Direct color
    // Add more as needed, e.g., from Color.kt if other specific cyberpunk named colors are used
}

sealed class CyberpunkTextStyle(val textStyle: TextStyle) {
    object Label : CyberpunkTextStyle(AppTypography.labelMedium) // Example mapping
    object Body : CyberpunkTextStyle(AppTypography.bodyMedium)   // Example mapping
    object Glitch :
        CyberpunkTextStyle(AppTypography.bodyMedium.copy(fontFamily = AppTypography.displayLarge.fontFamily)) // Example, maybe a more distinct "glitch" font/style later
    // Add more as needed
}
