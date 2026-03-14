package dev.aurakai.auraframefx.system.overlay.model

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.ui.OverlayShape

/**
 * Represents an overlay element in the system UI
 */
data class OverlayElement(
    val id: String,
    val type: OverlayElementType,
    val position: OverlayPosition,
    val size: OverlaySize,
    val color: Color = Color.White,
    val alpha: Float = 1.0f,
    val shape: OverlayShape = OverlayShape.RECTANGLE,
    val isVisible: Boolean = true,
    val zIndex: Int = 0,
    val metadata: Map<String, Any> = emptyMap()
)

enum class OverlayElementType {
    TEXT,
    IMAGE,
    SHAPE,
    ANIMATION,
    CUSTOM
}

data class OverlayPosition(
    val x: Float,
    val y: Float
)

data class OverlaySize(
    val width: Float,
    val height: Float
)
