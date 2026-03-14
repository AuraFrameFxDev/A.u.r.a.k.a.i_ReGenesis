package dev.aurakai.auraframefx.ui

import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayConfig(
    val notchBar: NotchBarConfig = NotchBarConfig(),
)

@Serializable
data class NotchBarConfig(
    val enabled: Boolean = false,
    val style: String = "default",
    val showIndicators: Boolean = true,
    val manageCutout: Boolean = true,
    val showGenesisIndicator: Boolean = true,
    val showStatus: Boolean = true,
)
