package dev.aurakai.auraframefx.system.overlay.model

import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayConfig(
    val notchBar: NotchBarConfigDetails = NotchBarConfigDetails()
)

@Serializable
data class NotchBarConfigDetails(
    val enabled: Boolean = true,
    val showIndicators: Boolean = true,
    val manageCutout: Boolean = true,
    val showGenesisIndicator: Boolean = true,
    val showStatus: Boolean = true
)
