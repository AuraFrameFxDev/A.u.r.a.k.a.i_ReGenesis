package dev.aurakai.auraframefx.system.quicksettings.model

import kotlinx.serialization.Serializable

@Serializable
data class QuickSettingsConfig(
    val tiles: TileConfig = TileConfig(),
    val layout: LayoutConfig = LayoutConfig(),
    val showGenesisIndicator: Boolean = true
)

@Serializable
data class TileConfig(
    val style: String = "cyberpunk"
)

@Serializable
data class LayoutConfig(
    val padding: PaddingConfig = PaddingConfig()
)

@Serializable
data class PaddingConfig(
    val start: Int = 16,
    val top: Int = 16,
    val end: Int = 16,
    val bottom: Int = 16
)
