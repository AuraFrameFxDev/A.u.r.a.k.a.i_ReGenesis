package dev.aurakai.auraframefx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 🪟 Glassmorphism Card Component
 *
 * Beautiful frosted glass effect with blur, transparency, and gradient borders.
 * Perfect for AuraKai's cyberpunk aesthetic.
 */

/**
 * Glass card typography configuration
 */
data class GlassCardStyle(
    val backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    val borderGradient: List<Color> = listOf(
        Color(0xFFFF00FF).copy(alpha = 0.5f), // Magenta
        Color(0xFF00FFFF).copy(alpha = 0.5f)  // Cyan
    ),
    val borderWidth: Dp = 1.dp,
    val blurRadius: Dp = 10.dp,
    val cornerRadius: Dp = 16.dp,
    val padding: Dp = 16.dp
)

/**
 * Preset glass card styles
 */
object GlassCardStyles {
    val Default = GlassCardStyle()

    val Aura = GlassCardStyle(
        backgroundColor = Color(0xFFFF00FF).copy(alpha = 0.15f), // Magenta tint
        borderGradient = listOf(
            Color(0xFFFF00FF).copy(alpha = 0.8f),
            Color(0xFFFF00FF).copy(alpha = 0.3f)
        )
    )

    val Kai = GlassCardStyle(
        backgroundColor = Color(0xFF00FFFF).copy(alpha = 0.15f), // Cyan tint
        borderGradient = listOf(
            Color(0xFF00FFFF).copy(alpha = 0.8f),
            Color(0xFF00FFFF).copy(alpha = 0.3f)
        )
    )

    val Neutral = GlassCardStyle(
        backgroundColor = Color.White.copy(alpha = 0.08f),
        borderGradient = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.1f)
        )
    )

    val Minimal = GlassCardStyle(
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        borderGradient = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.05f)
        ),
        borderWidth = 0.5.dp,
        cornerRadius = 12.dp
    )
}

/**
 * 🪟 Glass Card Composable
 *
 * Usage:
 * ```
 * GlassCard(
 *     typography = GlassCardStyles.Aura,
 *     onClick = { /* handle click */ }
 * ) {
 *     Text("Glass card content")
 * }
 * ```
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    style: GlassCardStyle = GlassCardStyles.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderBrush = Brush.linearGradient(style.borderGradient)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(style.cornerRadius))
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        // Background with glass effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = style.backgroundColor,
                    shape = RoundedCornerShape(style.cornerRadius)
                )
                .border(
                    width = style.borderWidth,
                    brush = borderBrush,
                    shape = RoundedCornerShape(style.cornerRadius)
                )
        )

        // Content
        Column(
            modifier = Modifier.padding(style.padding)
        ) {
            content()
        }
    }
}

/**
 * 🌟 Animated Glass Card
 *
 * Glass card with hover/press effects
 */
@Composable
fun AnimatedGlassCard(
    modifier: Modifier = Modifier,
    style: GlassCardStyle = GlassCardStyles.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedStyle = if (isPressed) {
        style.copy(
            backgroundColor = style.backgroundColor.copy(alpha = style.backgroundColor.alpha * 1.5f),
            borderWidth = style.borderWidth * 1.2f
        )
    } else {
        style
    }

    GlassCard(
        modifier = modifier,
        style = animatedStyle,
        onClick = {
            isPressed = true
            onClick?.invoke()
        },
        content = content
    )
}

/**
 * 🎨 Gradient Glass Card
 *
 * Glass card with gradient background
 */
@Composable
fun GradientGlassCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFFFF00FF).copy(alpha = 0.2f),
        Color(0xFF00FFFF).copy(alpha = 0.2f)
    ),
    borderGradient: List<Color> = listOf(
        Color(0xFFFF00FF).copy(alpha = 0.5f),
        Color(0xFF00FFFF).copy(alpha = 0.5f)
    ),
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val backgroundBrush = Brush.linearGradient(gradientColors)
    val borderBrush = Brush.linearGradient(borderGradient)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = backgroundBrush,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .border(
                    width = borderWidth,
                    brush = borderBrush,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )

        // Content
        Column(
            modifier = Modifier.padding(padding)
        ) {
            content()
        }
    }
}

/**
 * 🔮 Frosted Glass Card
 *
 * Heavy blur effect for true glassmorphism
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    style: GlassCardStyle = GlassCardStyles.Default,
    blurRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .blur(blurRadius)
            .clip(RoundedCornerShape(style.cornerRadius))
    ) {
        GlassCard(
            style = style,
            onClick = onClick,
            content = content
        )
    }
}
