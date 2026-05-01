package dev.aurakai.auraframefx.domains.aura

/**
 * Placeholder configuration models for the Customization module
 */

// Launcher Configuration
data class LauncherConfiguration(
    val desktopRows: Int = 5,
    val desktopColumns: Int = 4,
    val iconSize: Float = 1.0f,
    val themedIcons: Boolean = true
)

// Monet Configuration
data class MonetConfiguration(
    val accentSaturation: Float = 100f,
    val backgroundSaturation: Float = 100f,
    val backgroundLightness: Float = 50f,
    val chromaFactor: Float = 1.0f,
    val style: String = "tonal_spot",
    val seedColor: String = "#00E5FF",
    val isPitchBlack: Boolean = false
)

// System UI Configuration
data class SystemUIConfiguration(
    val lockscreenClockStyle: Int = 0,
    val batteryStyle: Int = 0,
    val qsTransparency: Float = 1.0f,
    val hidePill: Boolean = false,
    val blurRadius: Int = 25
)
