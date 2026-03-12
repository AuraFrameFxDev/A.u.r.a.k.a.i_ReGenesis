package dev.aurakai.auraframefx.domains.aura.chromacore.engine

import kotlinx.serialization.Serializable

/**
 * 🎨 CHROMA CORE CONFIG
 * Unified configuration for the UXUI Engine.
 * Combines settings for Colors (ColorBlendr), Icons (Iconify), and Launcher (PLE).
 */
@Serializable
data class ChromaCoreConfig(
    // 🎨 COLOR ENGINE (ColorBlendr)
    val useDynamicColors: Boolean = true,
    val themeSeedColor: Int = 0xFF6200EE.toInt(),
    val colorStyle: String = "Vibrant", // Tonic, Vibrant, Expressive, etc.
    val customPaletteEnabled: Boolean = false,
    val perAppColorsEnabled: Boolean = false,

    // ✨ ICONIFY TWEAKS
    val statusbarLogoEnabled: Boolean = false,
    val statusbarLogoStyle: Int = 0,
    val statusbarLogoPosition: Int = 0, // Left, Center, Right
    val uiRoundness: Int = 12, // dp
    val coloredBatteryEnabled: Boolean = false,
    val qsTileStyle: Int = 0,
    val qsRowColumnConfig: String = "2x4",
    val navbarStyle: Int = 0,
    val volumePanelStyle: Int = 0,

    // 🚀 LAUNCHER MODS (Pixel Launcher Enhanced)
    val launcherIconTextSize: Float = 12f,
    val launcherGridConfig: String = "5x5",
    val hideAppsList: List<String> = emptyList(),
    val clearAllButtonEnabled: Boolean = true,
    val smartSpaceEnabled: Boolean = true,
    val hotseatenabled: Boolean = true,

    // 🎬 ANIMATIONS
    val sysuiAnimationDuration: Int = 300,
    val launchAnimationScale: Float = 1.0f,
    val bootAnimationOverride: String? = null
)

/**
 * 🛠️ CHROMA CORE SERVICE MODES
 */
enum class ChromaCoreMode {
    STOCK,      // No modifications
    ROOT,       // Full root access (Libsu)
    SHIZUKU,    // Shizuku-based (Wireless ADB)
    XPOSED      // Dynamic hooks only (YukiHook)
}
