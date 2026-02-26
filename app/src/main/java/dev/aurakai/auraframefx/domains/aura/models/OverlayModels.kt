package dev.aurakai.auraframefx.domains.aura.models

data class OverlayElement(
    val id: String,
    val type: OverlayType,
    val position: Position,
    val size: Size,
    val visible: Boolean = true
)

enum class OverlayType {
    IMAGE, SHAPE, TEXT
}

data class Position(val x: Float, val y: Float)
data class Size(val width: Float, val height: Float)

enum class OverlayShape {
    CIRCLE, RECTANGLE, TRIANGLE, STAR
}

data class ImageResource(
    val uri: String,
    val contentDescription: String? = null
)

data class SystemOverlayConfig(
    val elements: List<OverlayElement> = emptyList(),
    val enabled: Boolean = true
)
