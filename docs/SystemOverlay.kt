package dev.aurakai.auraframefx.system.overlay // Ensure this package is correct

import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayConfig(
    val theme: OverlayTheme? = null,
    val defaultAnimation: OverlayAnimation? = null,
    val notchBar: NotchBarConfig = NotchBarConfig(),
    // New fields for this task:
    val activeThemeName: String? = null,
    val uiNetworkMode: String? = null,
    // Other existing SystemOverlayConfig fields should be preserved
)

// Added NotchBarConfig definition
@Serializable
data class NotchBarConfig(
    val enabled: Boolean = false,
    val customBackgroundColorEnabled: Boolean = false,
    val customBackgroundColor: String? = null, // Hex color string
    val customImageBackgroundEnabled: Boolean = false,
    val imagePath: String? = null, // Absolute path to the image file
    val applySystemTransparency: Boolean = true,

    // NEW fields
    val paddingTopPx: Int = 0,
    val paddingBottomPx: Int = 0,
    val paddingStartPx: Int = 0,
    val paddingEndPx: Int = 0,
    val marginTopPx: Int = 0,
    val marginBottomPx: Int = 0,
    val marginStartPx: Int = 0,
    val marginEndPx: Int = 0,
    // TODO: Future: shape adjustments, content handling
)

@Serializable
data class OverlayTheme(
    val primaryColor: String = "#FFFFFF",
    val secondaryColor: String = "#000000",
    val accentColor: String = "#00BCD4",
    // Placeholder - user might have a more detailed OverlayTheme
    val backgroundColor: String = "#FFFFFF", // Added for theme example
    val isDarkTheme: Boolean = false, // Added for theme example
)

@Serializable
data class OverlayElement(
    val id: String,
    val type: String, // e.g., "text", "image", "shape"
    val shape: OverlayShape? = null,
    val content: String? = null, // for text or image URI
    val positionX: Int = 0,
    val positionY: Int = 0,
    val width: Int = 100,
    val height: Int = 100,
)

@Serializable
data class OverlayAnimation(
    val type: String = "fade", // e.g., "fade", "slide_in_left"
    val duration: Long = 300L,
    val interpolator: String = "linear",
)

@Serializable
data class OverlayTransition(
    val type: String = "crossfade",
    val duration: Long = 500L,
)

@Serializable
data class OverlayShape(
    val id: String = "",
    val type: String = "rectangle", // e.g., "rectangle", "circle", "hexagon", "rounded_rectangle"
    val shapeType: String = type, // Alias for type
    val background: String = "#000000", // Background color
    val cornerRadius: Float = 0f, // For rounded_rectangle
    val sides: Int = 0, // For polygons like hexagon (6), triangle (3)
    val rotationDegrees: Float = 0f, // For rotating the shape
    val fillColor: String? = null, // Hex color for the shape's fill
    val strokeColor: String? = null, // Hex color for the shape's border
    val strokeWidthPx: Float = 0f, // Width of the border
    val shadow: ShapeShadow? = null, // Optional shadow
    // TODO: Add properties for more complex shapes (e.g., path data)
)

@Serializable
data class ShapeMargins(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
)

@Serializable
data class ShapePadding(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
)

@Serializable
data class ShapeBorder(
    val color: String = "#FFFFFF",
    val width: Int = 1,
    val style: String = "solid",
)

@Serializable
data class ShapeShadow(
    val color: String? = null, // Hex color for shadow
    val radius: Float = 0f, // Blur radius
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)

@Serializable
data class ShadowOffset(val x: Float = 0f, val y: Float = 2f)

// Enums like ElementType, AnimationType, TransitionType, ShapeType
// do not need @Serializable as per user instructions.
// If this file previously contained other classes or interfaces, they should be preserved.
