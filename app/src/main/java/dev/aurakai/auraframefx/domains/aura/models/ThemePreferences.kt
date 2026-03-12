package dev.aurakai.auraframefx.domains.aura.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * User theme preferences with dual color representation
 */
@Serializable
data class ThemePreferences(
    val isDarkMode: Boolean = true,
    val primaryColorString: String = "#FF1744", // Persisted hex string
    val accentColor: Long = 0xFF00BCD4,
    val useDynamicColors: Boolean = false,
    val themeName: String = "cyberpunk",
    val mood: String = "balanced",
    val animationLevel: String = "standard",
    val style: String = "modern"
) {
    /**
     * Derived Color property for UI consumption
     * Non-serialized, computed from primaryColorString
     */
    @Transient
    val primaryColor: Color = runCatching {
        Color(android.graphics.Color.parseColor(primaryColorString))
    }.getOrElse { Color(0xFFFF1744) } // Fallback to default red
}
