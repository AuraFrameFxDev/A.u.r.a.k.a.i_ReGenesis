package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Clock configuration for lock screen
 */
@Serializable
data class LockScreenConfigClockConfig(
    @SerialName("style")
    val style: String = "digital",
    
    @SerialName("format24Hour")
    val format24Hour: Boolean = false,
    
    @SerialName("showSeconds")
    val showSeconds: Boolean = true
)
