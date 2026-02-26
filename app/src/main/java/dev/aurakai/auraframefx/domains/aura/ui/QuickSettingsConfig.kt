package dev.aurakai.auraframefx.domains.aura.ui

import kotlinx.serialization.Serializable

@Serializable
data class QuickSettingsConfig(
    val tiles: List<QuickSettingsTileConfig> = emptyList(),
    val background: ImageResource? = null,
    val layout: LayoutConfig = LayoutConfig(),
    val showGenesisIndicator: Boolean = true,
) {
    companion object {
        val DEFAULT = QuickSettingsConfig()
    }
}

@Serializable
data class QuickSettingsTileConfig(
    val id: String,
    val label: String,
    val shape: OverlayShape,
    val animation: QuickSettingsAnimation,
    val style: String = "default",
    val enabled: Boolean = true,
    val enableClicks: Boolean = true,
    val rippleEffect: Boolean = true,
    val background: QuickSettingsBackground? = null
)

@Serializable
sealed class QuickSettingsBackground {
    abstract val cornerRadius: Float?
    abstract val elevation: Float?

    @Serializable
    data class Solid(
        val color: Long,
        val alpha: Float,
        override val cornerRadius: Float? = null,
        override val elevation: Float? = null
    ) : QuickSettingsBackground()
}

@Serializable
data class LayoutConfig(
    val padding: PaddingConfig = PaddingConfig(),
    val spacing: Int = 8,
    val columns: Int = 4
)

@Serializable
data class PaddingConfig(
    val start: Int = 16,
    val top: Int = 16,
    val end: Int = 16,
    val bottom: Int = 16
)

@Serializable
enum class QuickSettingsAnimation {
    FADE,
    SLIDE,
    PULSE
}

