package dev.aurakai.auraframefx.domains.aura

import dev.aurakai.auraframefx.domains.aura.models.OverlayShape
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.engine.model.OverlayTheme
import dev.aurakai.auraframefx.domains.aura.models.OverlayElement
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.kinetic_animation.OverlayAnimation
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.kinetic_animation.OverlayTransition

interface SystemOverlayManager {
    fun applyTheme(theme: OverlayTheme)
    fun applyElement(element: OverlayElement)
    fun applyAnimation(animation: OverlayAnimation)
    fun applyTransition(transition: OverlayTransition)
    fun applyShape(shape: OverlayShape)
    fun applyConfig(config: SystemOverlayConfig)
    fun removeElement(elementId: String)
    fun clearAll()

    // Root-specific fabrications (opt-in)
    fun applyAccent(hex: String): Result<String>
    fun applyBackgroundSaturation(percent: Int): Result<String>
}

