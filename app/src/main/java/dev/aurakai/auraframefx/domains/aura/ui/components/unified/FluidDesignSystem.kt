package dev.aurakai.auraframefx.domains.aura.ui.components.unified

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 🌊 AURAKAI UNIFIED DESIGN SYSTEM
 *
 * Inspired by Regenesis - Fluid, Immersive, Digital Depth
 *
 * Core Principles:
 * - Glassmorphism with depth
 * - Subtle micro-animations
 * - Consistent spacing & rhythm
 * - Fluid state transitions
 * - Neon accents with purpose
 */

// ============================================================================
// COLOR SYSTEM - Digital Fluid Palette
// ============================================================================

object AuraColors {
    // Base Backgrounds - Deep Space
    val BackgroundDeepest = Color(0xFF0A0A0F)
    val BackgroundDeep = Color(0xFF12121A)
    val BackgroundMid = Color(0xFF1A1A2E)
    val BackgroundLight = Color(0xFF16213E)

    // Glass Tints - Subtle Overlays
    val GlassWhite = Color.White.copy(alpha = 0.05f)
    val GlassLight = Color.White.copy(alpha = 0.08f)
    val GlassMid = Color.White.copy(alpha = 0.12f)
    val GlassStrong = Color.White.copy(alpha = 0.18f)

    // Neon Accents - Vibrant Energy
    val NeonCyan = Color(0xFF00F5FF)
    val NeonMagenta = Color(0xFFFF00FF)
    val NeonPurple = Color(0xFF9D4EDD)
    val NeonBlue = Color(0xFF3A86FF)
    val NeonPink = Color(0xFFFF006E)
    val NeonOrange = Color(0xFFFF4500)
    val NeonGreen = Color(0xFF39FF14)

    // Agent-Specific
    val AuraGlow = NeonMagenta
    val KaiGlow = NeonCyan
    val GenesisGlow = NeonPurple
    val CascadeGlow = NeonBlue

    // Status Colors
    val SuccessGlow = NeonGreen
    val WarningGlow = NeonOrange
    val DangerGlow = Color(0xFFFF1744)

    // Text Hierarchy
    val TextPrimary = Color.White.copy(alpha = 0.95f)
    val TextSecondary = Color.White.copy(alpha = 0.70f)
    val TextTertiary = Color.White.copy(alpha = 0.50f)
    val TextDisabled = Color.White.copy(alpha = 0.30f)
}

// ============================================================================
// SPACING SYSTEM - Rhythmic Layout
// ============================================================================

object AuraSpacing {
    val xxxs = 2.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
}

// ============================================================================
// SHAPE SYSTEM - Fluid Corners
// ============================================================================

object AuraShapes {
    val small = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(20.dp)
    val xxl = RoundedCornerShape(24.dp)

    // Special shapes
    val pill = RoundedCornerShape(999.dp)
    val topRounded = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val bottomRounded = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
}

// ============================================================================
// ANIMATION SYSTEM - Fluid Motion
// ============================================================================

object AuraAnimations {
    // Durations
    const val INSTANT = 100
    const val FAST = 200
    const val MEDIUM = 300
    const val SLOW = 500
    const val VERY_SLOW = 800

    // Easings
    val smooth = FastOutSlowInEasing
    val bounce = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
    val elastic = CubicBezierEasing(0.175f, 0.885f, 0.32f, 1.275f)

    // Spring configs
    val springStiff = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    val springSmooth = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// ============================================================================
// GLASS CARD - Base Component
// ============================================================================

/**
 * 🔮 Fluid Glass Card
 *
 * The foundation of AURAKAI UI - glassmorphic card with depth and subtle glow
 */
@Composable
fun FluidGlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = AuraColors.NeonCyan,
    glowIntensity: Float = 0.3f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Smooth scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = AuraAnimations.springSmooth,
        label = "card_scale"
    )

    // Glow pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = glowIntensity * 0.5f,
        targetValue = glowIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = AuraAnimations.smooth),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(AuraShapes.large)
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        isPressed = true
                        onClick()
                        scope.launch {
                            delay(200)
                            isPressed = false
                        }
                    }
                } else Modifier
            )
    ) {
        // Multi-layer glass effect
        Box(
            modifier = Modifier
                .matchParentSize()
                // Outer glow
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                // Glass surface
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AuraColors.GlassMid,
                            AuraColors.GlassLight
                        )
                    ),
                    shape = AuraShapes.large
                )
                // Subtle border
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = AuraShapes.large
                )
        )

        // Content
        Column(
            modifier = Modifier.padding(AuraSpacing.md)
        ) {
            content()
        }
    }
}

// ============================================================================
// MENU ITEM - Interactive Card
// ============================================================================

/**
 * 🎯 Fluid Menu Item
 *
 * Consistent menu/submenu card with icon, title, subtitle, glow
 */
@Composable
fun FluidMenuItem(
    title: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    glowColor: Color = AuraColors.NeonCyan,
    enabled: Boolean = true,
    showWarning: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FluidGlassCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = if (showWarning) AuraColors.WarningGlow else glowColor,
        glowIntensity = if (enabled) 0.3f else 0.1f,
        onClick = if (enabled) onClick else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.md)
        ) {
            // Icon with glow
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(AuraShapes.medium)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    glowColor.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AuraSpacing.xxs)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = if (enabled) AuraColors.TextPrimary else AuraColors.TextDisabled
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (enabled) AuraColors.TextSecondary else AuraColors.TextDisabled
                    )
                }

                if (showWarning) {
                    Text(
                        text = "⚠️ Requires caution",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraColors.WarningGlow
                    )
                }
            }

            // Arrow indicator
            if (enabled) {
                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineMedium,
                    color = glowColor.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ============================================================================
// SECTION HEADER - Visual Hierarchy
// ============================================================================

/**
 * 📍 Section Header
 *
 * Consistent section dividers with optional glow line
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    glowColor: Color = AuraColors.NeonCyan,
    showGlowLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            color = glowColor
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AuraColors.TextSecondary
            )
        }

        if (showGlowLine) {
            Spacer(modifier = Modifier.height(AuraSpacing.xs))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

// ============================================================================
// WARNING BANNER - Status Messages
// ============================================================================

/**
 * ⚠️ Fluid Warning Banner
 *
 * Attention-grabbing status messages with icon and glow
 */
@Composable
fun WarningBanner(
    message: String,
    severity: BannerSeverity = BannerSeverity.WARNING,
    modifier: Modifier = Modifier
) {
    val colors = when (severity) {
        BannerSeverity.INFO -> Pair(AuraColors.NeonBlue, AuraColors.NeonBlue.copy(alpha = 0.1f))
        BannerSeverity.SUCCESS -> Pair(
            AuraColors.SuccessGlow,
            AuraColors.SuccessGlow.copy(alpha = 0.1f)
        )

        BannerSeverity.WARNING -> Pair(
            AuraColors.WarningGlow,
            AuraColors.WarningGlow.copy(alpha = 0.1f)
        )

        BannerSeverity.DANGER -> Pair(
            AuraColors.DangerGlow,
            AuraColors.DangerGlow.copy(alpha = 0.1f)
        )
    }

    val icon = when (severity) {
        BannerSeverity.INFO -> "ℹ️"
        BannerSeverity.SUCCESS -> "✅"
        BannerSeverity.WARNING -> "⚠️"
        BannerSeverity.DANGER -> "🚨"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AuraShapes.medium)
            .background(colors.second)
            .border(
                width = 1.5.dp,
                color = colors.first,
                shape = AuraShapes.medium
            )
            .padding(AuraSpacing.md)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = colors.first
            )
        }
    }
}

enum class BannerSeverity {
    INFO, SUCCESS, WARNING, DANGER
}

