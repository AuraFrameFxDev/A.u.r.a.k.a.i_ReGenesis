package dev.aurakai.auraframefx.system.ui

import dev.aurakai.auraframefx.aura.animations.OverlayAnimation
import dev.aurakai.auraframefx.system.overlay.model.OverlayElement
import dev.aurakai.auraframefx.ui.OverlayShape
import dev.aurakai.auraframefx.ui.theme.model.OverlayTheme
import dev.aurakai.auraframefx.aura.animations.OverlayTransition
import dev.aurakai.auraframefx.system.overlay.model.SystemOverlayConfig

interface SystemOverlayManager {
    fun applyTheme(theme: OverlayTheme)
    fun applyElement(element: OverlayElement)
    fun applyAnimation(animation: OverlayAnimation)
    fun applyTransition(transition: OverlayTransition)
    fun applyShape(shape: OverlayShape)
    fun applyConfig(config: SystemOverlayConfig)
    fun removeElement(elementId: String)
    fun clearAll()
}
