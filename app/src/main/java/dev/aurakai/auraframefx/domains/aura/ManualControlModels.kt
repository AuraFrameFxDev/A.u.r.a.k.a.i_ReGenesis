package dev.aurakai.auraframefx.domains.aura

import androidx.compose.runtime.Immutable

@Immutable
data class MonetConfiguration(
    val accentSaturation: Float = 100f,
    val backgroundSaturation: Float = 100f,
    val backgroundLightness: Float = 100f,
    val isPitchBlack: Boolean = false,
    val isAccurateShades: Boolean = true,
    val style: String = "TONAL_SPOT",
    val seedColor: String = "#00E5FF",
    val customPrimary: String? = null,
    val customSecondary: String? = null,
    val customTertiary: String? = null
)

@Immutable
data class SystemUIConfiguration(
    val lockscreenClockStyle: Int = 0,
    val showLockscreenClock: Boolean = true,
    val batteryStyle: Int = 0,
    val qsTransparency: Float = 1.0f,
    val notificationTransparency: Float = 1.0f,
    val showStatusBarLogo: Boolean = false,
    val blurRadius: Int = 25,
    val hidePill: Boolean = false,
    val headerImageEnabled: Boolean = false,
    val volumePanelStyle: Int = 0
)

@Immutable
data class LauncherConfiguration(
    val desktopRows: Int = 5,
    val desktopColumns: Int = 5,
    val iconSize: Float = 1.0f,
    val textSize: Float = 1.0f,
    val drawerOpacity: Float = 1.0f,
    val themedIcons: Boolean = false,
    val hideLabels: Boolean = false,
    val doubleTapToSleep: Boolean = true
)
