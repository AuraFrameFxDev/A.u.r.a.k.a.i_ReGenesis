package dev.aurakai.auraframefx.domains.aura.lab

/**
 * ⚙️ REGENESIS CUSTOMIZATION CONFIG
 *
 * Top-level state model for the ReGenesis customization suite.
 * Tracks which integration modules (Iconify, ColorBlendr, PixelLauncherEnhanced) are active.
 */
data class ReGenesisCustomizationConfig(
    val iconifyEnabled: Boolean = false,
    val colorBlendrEnabled: Boolean = false,
    val pixelLauncherEnhancedEnabled: Boolean = false,
    val activeThemePackage: String = "",
    val customAccentColor: String = "#00E5FF",
    val lastModified: Long = System.currentTimeMillis()
)
