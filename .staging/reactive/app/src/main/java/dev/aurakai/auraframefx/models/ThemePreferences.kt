package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

/**
 * User theme preferences
 */
@Serializable
data class ThemePreferences(
    val isDarkMode: Boolean = true,
    val primaryColor: Long = 0xFFFF1744,
    val accentColor: Long = 0xFF00BCD4,
    val useDynamicColors: Boolean = false,
    val themeName: String = "cyberpunk",
    val mood: String = "balanced",
    val animationLevel: String = "standard",
    val style: String = "modern"
)
