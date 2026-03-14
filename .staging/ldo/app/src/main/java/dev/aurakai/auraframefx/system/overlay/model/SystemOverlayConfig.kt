package dev.aurakai.auraframefx.system.overlay.model

import androidx.compose.ui.graphics.Color

/**
 * Configuration for system overlay
 */
data class SystemOverlayConfig(
    val isEnabled: Boolean = true,
    val backgroundColor: Color = Color.Transparent,
    val opacity: Float = 1.0f,
    val elements: List<OverlayElement> = emptyList(),
    val animationDuration: Long = 300L,
    val allowInteraction: Boolean = true,
    val showOnLockscreen: Boolean = false,
    val metadata: Map<String, Any> = emptyMap()
)
