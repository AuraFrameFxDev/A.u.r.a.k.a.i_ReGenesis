package dev.aurakai.auraframefx.domains.aura.models

import kotlinx.serialization.Serializable
import dev.aurakai.auraframefx.domains.aura.ui.theme.model.AuraTheme as OverlayTheme
import dev.aurakai.auraframefx.domains.aura.ui.effects.OverlayAnimation
import dev.aurakai.auraframefx.domains.aura.ui.effects.OverlayTransition

@Serializable
data class SystemOverlayConfig(
    val theme: OverlayTheme? = null,
    val defaultAnimation: OverlayAnimation? = null,
    val notchBar: NotchBarConfig = NotchBarConfig(),
    val activeThemeName: String? = null,
    val uiNetworkMode: String? = null,
    val elements: List<OverlayElement> = emptyList(),
    val enabled: Boolean = true
)

@Serializable
data class NotchBarConfig(
    val enabled: Boolean = false,
    val customBackgroundColorEnabled: Boolean = false,
    val customBackgroundColor: String? = null,
    val customImageBackgroundEnabled: Boolean = false,
    val imagePath: String? = null,
    val applySystemTransparency: Boolean = true,
    val paddingTopPx: Int = 0,
    val paddingBottomPx: Int = 0,
    val paddingStartPx: Int = 0,
    val paddingEndPx: Int = 0,
    val marginTopPx: Int = 0,
    val marginBottomPx: Int = 0,
    val marginStartPx: Int = 0,
    val marginEndPx: Int = 0
)

@Serializable
data class OverlayElement(
    val id: String,
    val type: String, 
    val shape: OverlayShape? = null,
    val content: String? = null,
    val positionX: Int = 0,
    val positionY: Int = 0,
    val width: Int = 100,
    val height: Int = 100,
    val visible: Boolean = true
)

@Serializable
data class OverlayShape(
    val id: String = "",
    val type: String = "rectangle",
    val shapeType: String = type,
    val background: String = "#000000",
    val cornerRadius: Float = 0f,
    val sides: Int = 0,
    val rotationDegrees: Float = 0f,
    val fillColor: String? = null,
    val strokeColor: String? = null,
    val strokeWidthPx: Float = 0f,
    val shadow: ShapeShadow? = null
)

@Serializable
data class ShapeShadow(
    val color: String? = null,
    val radius: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)
