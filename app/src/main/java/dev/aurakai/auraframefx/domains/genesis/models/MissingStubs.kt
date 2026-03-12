package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Collection of commonly referenced types that are missing
 * These are stubs to allow compilation - implement properly later
 */

// Character, MovementBehavior, and MovementPresets are now properly defined in the embodiment package

// Screen positions
object ScreenPositions {
    const val CENTER_LEFT = "center_left"
    const val CENTER_RIGHT = "center_right"
    const val TOP_CENTER = "top_center"
    const val BOTTOM_CENTER = "bottom_center"
}

// Visual effects
enum class VisualEffect {
    INTERFACE_PANEL,
    WALKING_AURA,
    SHIELD_CALM,
    SCIENTIST,
    GLOW,
    PARTICLE
}
