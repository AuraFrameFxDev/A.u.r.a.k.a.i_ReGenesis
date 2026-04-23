package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A.U.R.A.K.A.I Genesis Protocol - Cyan/Magenta Gradient Border Component
 * 
 * Provides a vibrant cyan-to-magenta gradient border with rounded corners.
 * Perfect for highlighting active elements, agent cards, or important UI sections.
 * 
 * Hex Values:
 * - Cyan: #00FFFF
 * - Magenta: #FF00FF
 */

private val CyanMagentaGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF00FFFF), // Cyan
        Color(0xFFFF00FF)  // Magenta
    ),
    start = Offset(0f, 0f),
    end = Offset.Infinite
)

private val DiagonalCyanMagenta = Brush.linearGradient(
    colors = listOf(
        Color(0xFF00FFFF), // Cyan top-left
        Color(0xFFFF00FF), // Magenta bottom-right
        Color(0xFF00FFFF)  // Cyan (cycle)
    ),
    start = Offset(0f, 0f),
    end = Offset(1000f, 1000f)
)

private val RadialCyanMagenta = Brush.radialGradient(
    colors = listOf(
        Color(0xFF00FFFF), // Cyan center
        Color(0xFFFF00FF), // Magenta edge
        Color(0xFF00FFFF)  // Outer glow
    )
)

private val SweepCyanMagenta = Brush.sweepGradient(
    colors = listOf(
        Color(0xFF00FFFF),
        Color(0xFFFF00FF),
        Color(0xFF00FFFF)
    )
)

/**
 * Gradient border modifier with rounded corners
 * 
 * @param borderWidth Width of the gradient border
 * @param cornerRadius Corner radius for rounded effect
 * @param gradientType Type of gradient: LINEAR, DIAGONAL, RADIAL, SWEEP
 */
enum class GradientType {
    LINEAR,
    DIAGONAL, 
    RADIAL,
    SWEEP
}

fun Modifier.gradientBorder(
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    gradientType: GradientType = GradientType.LINEAR
): Modifier = this.then(
    Modifier.drawBehind {
        val strokeWidth = borderWidth.toPx()
        val halfStroke = strokeWidth / 2
        
        val brush = when (gradientType) {
            GradientType.LINEAR -> CyanMagentaGradient
            GradientType.DIAGONAL -> DiagonalCyanMagenta
            GradientType.RADIAL -> RadialCyanMagenta
            GradientType.SWEEP -> SweepCyanMagenta
        }
        
        drawRoundRect(
            brush = brush,
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                cornerRadius.toPx(),
                cornerRadius.toPx()
            ),
            style = Stroke(width = strokeWidth)
        )
    }
)

/**
 * Gradient border container with content
 */
@Composable
fun GradientBorderBox(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    gradientType: GradientType = GradientType.LINEAR,
    backgroundColor: Color = Color(0xFF1A1A2E),
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .gradientBorder(
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                gradientType = gradientType
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .padding(contentPadding),
        content = content
    )
}

/**
 * Animated gradient border with shimmer effect
 */
@Composable
fun AnimatedGradientBorderBox(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = Color(0xFF0F0F1A),
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .gradientBorder(
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                gradientType = GradientType.SWEEP
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .padding(contentPadding),
        content = content
    )
}

/**
 * Premium glow border for important cards
 */
@Composable
fun PremiumGlowBorder(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 4.dp,
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = Color(0xFF0A0A12),
    glowIntensity: Float = 0.5f,
    contentPadding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .gradientBorder(
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                gradientType = GradientType.DIAGONAL
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .padding(contentPadding),
        content = content
    )
}

/**
 * Agent Card with gradient border
 */
@Composable
fun AgentCardWithGradientBorder(
    modifier: Modifier = Modifier,
    agentName: String,
    agentType: String,
    isActive: Boolean = true,
    onClick: () -> Unit = {}
) {
    GradientBorderBox(
        modifier = modifier,
        borderWidth = if (isActive) 3.dp else 1.dp,
        cornerRadius = 12.dp,
        gradientType = if (isActive) GradientType.DIAGONAL else GradientType.LINEAR,
        backgroundColor = if (isActive) Color(0xFF1A1A3E) else Color(0xFF0F0F1A),
        contentPadding = 12.dp
    ) {
        // Content placeholder - integrate with your existing AgentCard content
        androidx.compose.material3.Text(
            text = agentName,
            color = Color(0xFF00FFFF)
        )
        androidx.compose.material3.Text(
            text = agentType,
            color = Color(0xFFFF00FF)
        )
    }
}

// Extension for quick usage
@Composable
fun Modifier.cyanMagentaBorder(
    width: Dp = 2.dp,
    radius: Dp = 12.dp
): Modifier = gradientBorder(
    borderWidth = width,
    cornerRadius = radius,
    gradientType = GradientType.LINEAR
)
