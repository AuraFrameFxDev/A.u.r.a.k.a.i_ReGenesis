package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Haptic feedback configuration for lock screen
 */
@Serializable
data class LockScreenConfigHapticFeedback(
    @SerialName("enabled")
    val enabled: Boolean = true,
    
    @SerialName("intensity")
    val intensity: Int = 50
)
