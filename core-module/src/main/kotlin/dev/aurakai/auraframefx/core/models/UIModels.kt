package dev.aurakai.auraframefx.core.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * 🗣️ CONVERSATION STATE
 */
@Serializable
sealed class ConversationState {
    @Serializable
    data object Idle : ConversationState()
    @Serializable
    data object Listening : ConversationState()
    @Serializable
    data object Speaking : ConversationState()
    @Serializable
    data object Recording : ConversationState()
    
    @Serializable
    data class Processing(val partialTranscript: String? = null) : ConversationState()
    @Serializable
    data class Responding(val responseText: String? = null) : ConversationState()
    @Serializable
    data class Error(val errorMessage: String) : ConversationState()
}

/**
 * 🌀 HOME SCREEN TRANSITION TYPE
 */
@Serializable
enum class HomeScreenTransitionType {
    GLOBE_ROTATE,
    DIGITAL_DECONSTRUCT,
    HOLOGRAM,
    PIXELATE
}

/**
 * 🎨 AURA THEME DATA
 */
@Serializable
data class AuraThemeData(
    val themeName: String = "Default",
    @Contextual val accentColor: Color = Color.Cyan,
    val animationStyle: AnimationStyle = AnimationStyle.FLOWING
) {
    enum class AnimationStyle {
        ENERGETIC, CALMING, FLOWING, PULSING, SUBTLE
    }
}

/**
 * 🖼️ SYSTEM OVERLAY CONFIGURATION
 */
@Serializable
data class SystemOverlayConfig(
    val theme: OverlayTheme? = null,
    val defaultAnimation: OverlayAnimation? = null,
    val notchBar: NotchBarConfig = NotchBarConfig(),
    val activeThemeName: String? = null,
    val uiNetworkMode: String? = null
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
data class OverlayTheme(
    val primaryColor: String = "#FFFFFF",
    val secondaryColor: String = "#000000",
    val accentColor: String = "#00BCD4",
    val backgroundColor: String = "#FFFFFF",
    val isDarkTheme: Boolean = false
) {
    val name: String get() = "OverlayTheme"
}

@Serializable
data class OverlayElement(
    val id: String,
    val type: String,
    val shape: OverlayShape? = null,
    val content: String? = null,
    val positionX: Int = 0,
    val positionY: Int = 0,
    val width: Int = 100,
    val height: Int = 100
)

@Serializable
data class OverlayAnimation(
    val type: String = "fade",
    val duration: Long = 300L,
    val interpolator: String = "linear"
)

@Serializable
data class OverlayTransition(
    val type: String = "crossfade",
    val duration: Long = 500L
) {
    val name: String get() = type
}

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
) {
    val name: String get() = type
}

@Serializable
data class ShapeShadow(
    val color: String? = null,
    val radius: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)

@Serializable
data class ShapeMargins(val top: Int = 0, val bottom: Int = 0, val left: Int = 0, val right: Int = 0)

@Serializable
data class ShapePadding(val top: Int = 0, val bottom: Int = 0, val left: Int = 0, val right: Int = 0)

@Serializable
data class ShapeBorder(val color: String = "#FFFFFF", val width: Int = 1, val style: String = "solid")

@Serializable
data class ShadowOffset(val x: Float = 0f, val y: Float = 2f)
