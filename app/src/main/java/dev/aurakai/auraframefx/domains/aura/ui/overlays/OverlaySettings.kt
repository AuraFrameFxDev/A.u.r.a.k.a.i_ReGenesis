package dev.aurakai.auraframefx.domains.aura.ui.overlays

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.setValue

class OverlaySettings(
    enabled: Boolean = true,
    order: List<String> = listOf("Agent Edge", "Aura Presence", "Chat Bubble", "Sidebar")
) {
    var overlaysEnabled by mutableStateOf(enabled)
    var overlayZOrder by mutableStateOf(order)
    var transitionStyle by mutableStateOf("none")
    var transitionSpeed by mutableStateOf(0)
}

val LocalOverlaySettings = staticCompositionLocalOf { OverlaySettings() }

