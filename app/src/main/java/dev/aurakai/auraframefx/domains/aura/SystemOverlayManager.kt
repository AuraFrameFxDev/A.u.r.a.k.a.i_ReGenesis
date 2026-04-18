package dev.aurakai.auraframefx.domains.aura

import dev.aurakai.auraframefx.domains.aura.models.OverlayElement
import dev.aurakai.auraframefx.domains.aura.models.OverlayShape
import dev.aurakai.auraframefx.domains.aura.ui.theme.model.OverlayTheme
import dev.aurakai.auraframefx.domains.aura.ui.effects.OverlayAnimation
import dev.aurakai.auraframefx.domains.aura.ui.effects.OverlayTransition

interface SystemOverlayManager {
    fun applyTheme(theme: OverlayTheme)
    fun applyElement(element: OverlayElement)
    fun applyAnimation(animation: OverlayAnimation)
    fun applyTransition(transition: OverlayTransition)
    fun applyShape(shape: OverlayShape)
    fun applyConfig(config: SystemOverlayConfig)
    fun removeElement(elementId: String)
    fun clearAll()

    fun applyAccent(hex: String): Result<String>
    fun applyBackgroundSaturation(percent: Int): Result<String>
}
