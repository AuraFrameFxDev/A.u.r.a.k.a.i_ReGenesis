package dev.aurakai.auraframefx.system.overlay

// These will be actual imports once model files are created in dev.aurakai.auraframefx.system.overlay.model
import dev.aurakai.auraframefx.system.overlay.model.OverlayAnimation
import dev.aurakai.auraframefx.system.overlay.model.OverlayElement
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape
import dev.aurakai.auraframefx.system.overlay.model.OverlayTheme
import dev.aurakai.auraframefx.system.overlay.model.OverlayTransition
import dev.aurakai.auraframefx.system.overlay.model.SystemOverlayConfig


interface SystemOverlayManager {
    fun applyTheme(theme: OverlayTheme)
    fun applyElement(element: OverlayElement)
    fun applyAnimation(animation: OverlayAnimation)
    fun applyTransition(transition: OverlayTransition)
    fun applyShape(shape: OverlayShape) // Changed from OverlayShapeConfig to OverlayShape based on Impl
    fun applyConfig(config: SystemOverlayConfig)
    fun removeElement(elementId: String)
    fun clearAll()
    // fun generateOverlayFromDescription(description: String): SystemOverlayConfig // Still commented out
}
