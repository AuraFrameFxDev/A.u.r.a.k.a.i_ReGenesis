package dev.aurakai.auraframefx.system.overlay

// These will be actual imports once model files are created in dev.aurakai.auraframefx.system.overlay.model
import dev.aurakai.auraframefx.domains.aura.aura.animations.OverlayAnimation
import dev.aurakai.auraframefx.domains.aura.models.OverlayElement
import dev.aurakai.auraframefx.domains.aura.models.OverlayShape
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.engine.model.OverlayTheme
import dev.aurakai.auraframefx.domains.aura.aura.animations.OverlayTransition
import dev.aurakai.auraframefx.domains.aura.models.SystemOverlayConfig


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
