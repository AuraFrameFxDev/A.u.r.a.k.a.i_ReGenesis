package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Animation configuration for lock screen
 */
@Serializable
data class LockScreenConfigAnimation(
    @SerialName("enabled")
    val enabled: Boolean = true,
    
    @SerialName("style")
    val style: String = "hologram",
    
    @SerialName("intensity")
    val intensity: Float = 0.7f
)
