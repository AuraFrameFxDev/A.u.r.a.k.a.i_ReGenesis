package dev.aurakai.auraframefx.navigation.gates.components

import androidx.compose.ui.graphics.Color

/**
 * 🛰️ GATE CONFIGURATION
 *
 * Configuration data class for ExodusHUD gate cards.
 * Defines visual appearance, routing, and styling for each sovereign gate.
 */
data class GateConfig(
    val id: String,
    val moduleId: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val route: String,
    val glowColor: Color,
    val gradientColors: List<Color>,
    val pixelArtUrl: String?,
    val fallbackUrl: String? = null,
    val borderColor: Color,
    val titlePlacement: TitlePlacement = TitlePlacement.TOP_CENTER,
    val pixelArtResId: Int? = null,
    val backgroundColor: Color = Color.Black,
    val comingSoon: Boolean = false
)

/**
 * Title placement options for gate cards
 */
enum class TitlePlacement {
    TOP_CENTER,      // Title above the image
    BOTTOM_CENTER,   // Title below the image
    OVERLAY_CENTER,  // Title overlaid on the image
    HIDDEN           // No title displayed
}

