package dev.aurakai.auraframefx.domains.aura.ui.theme.model

import androidx.compose.ui.graphics.Color // Assuming colors will be Compose Colors
import dev.aurakai.auraframefx.domains.aura.ui.OverlayShape

// Basic placeholder. The Impl used theme.colors, theme.fonts, theme.shapes
// These would be maps or lists of more specific types.
data class OverlayTheme(
    val name: String,
    val colors: Map<String, Color>? = null, // e.g., "primary" to Color(0xFFFFFFFF)
    val fonts: Map<String, String>? = null,   // e.g., "body" to "font_family_name"
    val shapes: Map<String, OverlayShape>? = null, // e.g., "button" to an OverlayShape definition
)

