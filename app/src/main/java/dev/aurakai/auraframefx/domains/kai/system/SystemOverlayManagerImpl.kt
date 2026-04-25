package dev.aurakai.auraframefx.domains.kai.system

import javax.inject.Inject
import javax.inject.Singleton
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.aura.ui.theme.model.OverlayTheme
import dev.aurakai.auraframefx.domains.aura.ui.OverlayElement
import dev.aurakai.auraframefx.domains.aura.animations.OverlayAnimation
import dev.aurakai.auraframefx.domains.aura.animations.OverlayTransition
import dev.aurakai.auraframefx.domains.aura.ui.OverlayShape
import dev.aurakai.auraframefx.domains.aura.SystemOverlayConfig

@Singleton
class SystemOverlayManagerImpl @Inject constructor() : SystemOverlayManager {
    override fun applyTheme(theme: OverlayTheme) { }
    override fun applyElement(element: OverlayElement) { }
    override fun applyAnimation(animation: OverlayAnimation) { }
    override fun applyTransition(transition: OverlayTransition) { }
    override fun applyShape(shape: OverlayShape) { }
    override fun applyConfig(config: SystemOverlayConfig) { }
    override fun removeElement(elementId: String) { }
    override fun clearAll() { }
    override fun applyAccent(hex: String): Result<String> = Result.success("Applied accent: $hex")
    override fun applyBackgroundSaturation(percent: Int): Result<String> = Result.success("Set saturation: $percent%")
}
