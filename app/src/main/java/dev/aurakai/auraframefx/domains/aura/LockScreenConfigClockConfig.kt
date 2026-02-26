package dev.aurakai.auraframefx.domains.aura

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
