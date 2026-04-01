package dev.aurakai.auraframefx.domains.aura.models

import androidx.compose.ui.graphics.Color

/**
 * Represents a theme configuration for the application UI.
 * @property id A unique identifier for the theme
 * @property name The display name of the theme
 * @property primaryColor The primary color of the theme
 * @property secondaryColor The secondary color of the theme
 * @property backgroundColor The background color
 * @property surfaceColor The surface color for cards and sheets
 * @property onPrimaryColor The color to use for text/icons on primary color
 * @property onSecondaryColor The color to use for text/icons on secondary color
 * @property onBackgroundColor The color to use for text on background color
 * @property onSurfaceColor The color to use for text on surface color
 * @property isDark Whether this is a dark theme variant
 */
data class Theme(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val onPrimaryColor: Color,
    val onSecondaryColor: Color,
    val onBackgroundColor: Color,
    val onSurfaceColor: Color,
    val isDark: Boolean = false
)

