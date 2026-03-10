package collabcanvas.ui.animation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * Represents an animated path that can be "plucked" with visual effects.
 */
data class PluckablePath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val isPlucked: Boolean = false,
    val offset: Offset = Offset.Zero,
    val scale: Float = 1f,
    val alpha: Float = 1f
)
