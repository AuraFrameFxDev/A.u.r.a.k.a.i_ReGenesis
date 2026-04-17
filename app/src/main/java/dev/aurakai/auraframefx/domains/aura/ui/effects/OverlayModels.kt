package dev.aurakai.auraframefx.domains.aura.ui.effects

import kotlinx.serialization.Serializable

@Serializable
data class OverlayAnimation(
    val id: String, 
    val type: String,
    val durationMs: Long? = null,
    val targetProperty: String? = null
)

@Serializable
data class OverlayTransition(
    val id: String,
    val type: String,
    val durationMs: Long? = null
)
