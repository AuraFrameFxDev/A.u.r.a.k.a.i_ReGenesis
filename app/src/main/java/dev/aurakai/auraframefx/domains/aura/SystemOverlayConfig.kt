package dev.aurakai.auraframefx.domains.aura

import androidx.compose.ui.graphics.Color

/**
 * 🎨 SYSTEM OVERLAY CONFIG — Aura Domain
 *
 * Configuration for system UI overlays managed by the Aura agent.
 * Controls visual properties of on-screen overlays (status bars, notch bars, etc.)
 */
data class SystemOverlayConfig(
    val isEnabled: Boolean = false,
    val overlayName: String = "",
    val overlayType: OverlayType = OverlayType.STATUS_BAR,
    val backgroundColor: Color = Color.Transparent,
    val borderColor: Color = Color.Transparent,
    val opacity: Float = 1f,
    val zIndex: Int = 0,
    val blurRadius: Float = 0f,
    val animationDuration: Long = 300L
) {
    enum class OverlayType {
        STATUS_BAR,
        NAVIGATION_BAR,
        NOTIFICATION_SHADE,
        QUICK_SETTINGS,
        LOCK_SCREEN,
        FULL_SCREEN,
        CUSTOM
    }
}
