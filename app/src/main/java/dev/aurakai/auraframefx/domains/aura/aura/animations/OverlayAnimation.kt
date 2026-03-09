package dev.aurakai.auraframefx.domains.aura.aura.animations

// Basic placeholder
data class OverlayAnimation(
    val id: String, // Added id as Impl uses it as map key
    val type: String, // e.g., "fade_in", "slide_up"
    val durationMs: Long? = null,
    val targetProperty: String? = null, // e.g., "alpha", "translationY"
)
