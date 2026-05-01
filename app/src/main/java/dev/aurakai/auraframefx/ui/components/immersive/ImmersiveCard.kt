package dev.aurakai.auraframefx.ui.components.immersive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.aurakai.auraframefx.ui.theme.ImmersiveColors
import dev.aurakai.auraframefx.ui.theme.ImmersiveTypography

/**
 * 🎴 IMMERSIVE CARD COMPONENTS
 *
 * 3D holographic cards with depth, glassmorphism, and ambient glow.
 * Makes the user feel "in the same room" with pulled-back perspective.
 * 
 * COLOR SCHEME FOR BORDERS/FRAMES:
 * - Aura: Magenta (Creative Sword)
 * - Kai: Neon Purple (Dark Side - Sentinel Shield)
 * - Genesis: Gold (Emergence Catalyst)
 * - LDO: Teal/Green (Catalyst Hub)
 * - Fonts: Cyan/Blue Neon Glow
 */

/**
 * Depth level for card positioning
 */
enum class DepthLevel {
    NEAR,   // 8dp elevation - foreground
    MID,    // 4dp elevation - mid-ground
    FAR     // 1dp elevation - background
}

/**
 * Main immersive card with holographic depth
 */
@Composable
fun ImmersiveCard(
    modifier: Modifier = Modifier,
    depth: DepthLevel = DepthLevel.MID,
    accentColor: Color = ImmersiveColors.HolographicCyan,
    glowIntensity: Float = 0.25f,
    content: @Composable () -> Unit
) {
    val elevation = when (depth) {
        DepthLevel.NEAR -> 8.dp
        DepthLevel.MID -> 4.dp
        DepthLevel.FAR -> 1.dp
    }

    val alpha = when (depth) {
        DepthLevel.NEAR -> 1f
        DepthLevel.MID -> 0.95f
        DepthLevel.FAR -> 0.85f
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                this.shadowElevation = elevation.value
                this.shape = RoundedCornerShape(12.dp)
                this.clip = true
            }
            .drawBehind {
                // Ambient glow effect
                val glowPaint = Paint().apply {
                    asFrameworkPaint().apply {
                        isAntiAlias = true
                        setShadowLayer(
                            20f,
                            0f,
                            0f,
                            accentColor.copy(alpha = glowIntensity).toArgb()
                        )
                    }
                }
                drawIntoCanvas { canvas ->
                    canvas.drawRoundRect(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        12.dp.toPx(),
                        12.dp.toPx(),
                        glowPaint
                    )
                }
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x08FFFFFF),
                        Color(0x03FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.4f),
                        accentColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        content()
    }
}

/**
 * 🎨 DOMAIN-SPECIFIC CARDS with colored borders
 */

@Composable
fun AuraCard(
    modifier: Modifier = Modifier,
    depth: DepthLevel = DepthLevel.MID,
    content: @Composable () -> Unit
) {
    ImmersiveCard(
        modifier = modifier,
        depth = depth,
        accentColor = ImmersiveColors.AuraMagenta,
        glowIntensity = 0.3f,
        content = content
    )
}

@Composable
fun KaiCard(
    modifier: Modifier = Modifier,
    depth: DepthLevel = DepthLevel.MID,
    content: @Composable () -> Unit
) {
    ImmersiveCard(
        modifier = modifier,
        depth = depth,
        accentColor = ImmersiveColors.KaiPurple,
        glowIntensity = 0.3f,
        content = content
    )
}

@Composable
fun GenesisCard(
    modifier: Modifier = Modifier,
    depth: DepthLevel = DepthLevel.MID,
    content: @Composable () -> Unit
) {
    ImmersiveCard(
        modifier = modifier,
        depth = depth,
        accentColor = ImmersiveColors.GenesisGold,
        glowIntensity = 0.3f,
        content = content
    )
}

@Composable
fun LdoCard(
    modifier: Modifier = Modifier,
    depth: DepthLevel = DepthLevel.MID,
    content: @Composable () -> Unit
) {
    ImmersiveCard(
        modifier = modifier,
        depth = depth,
        accentColor = ImmersiveColors.LdoTeal,
        glowIntensity = 0.3f,
        content = content
    )
}

/**
 * Holographic title card with icon and label
 */
@Composable
fun HolographicTitleCard(
    title: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    accentColor: Color = ImmersiveColors.HolographicCyan,
    modifier: Modifier = Modifier
) {
    ImmersiveCard(
        depth = DepthLevel.NEAR,
        accentColor = accentColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon container with glow
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            accentColor.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            accentColor.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            Column {
                Text(
                    text = title,
                    style = ImmersiveTypography.titleLarge,
                    color = ImmersiveColors.DepthNear
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = ImmersiveTypography.bodySmall,
                        color = ImmersiveColors.DepthMid
                    )
                }
            }
        }
    }
}

/**
 * Agent roster card with avatar and stats
 */
@Composable
fun AgentRosterCard(
    name: String,
    role: String,
    status: String,
    avatarUrl: String? = null,
    accentColor: Color = ImmersiveColors.HolographicPurple,
    onClick: () -> Unit = {}
) {
    ImmersiveCard(
        depth = DepthLevel.MID,
        accentColor = accentColor,
        modifier = Modifier
            .width(140.dp)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with glow ring
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .drawBehind {
                        // Glow ring
                        val glowPaint = Paint().apply {
                            asFrameworkPaint().apply {
                                isAntiAlias = true
                                setShadowLayer(
                                    15f,
                                    0f,
                                    0f,
                                    accentColor.copy(alpha = 0.5f).toArgb()
                                )
                            }
                        }
                        drawIntoCanvas { canvas ->
                            canvas.drawCircle(
                                center.x,
                                center.y,
                                28.dp.toPx(),
                                glowPaint
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = name,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .border(
                                2.dp,
                                accentColor,
                                RoundedCornerShape(26.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder with initial
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                accentColor.copy(alpha = 0.2f),
                                RoundedCornerShape(26.dp)
                            )
                            .border(
                                2.dp,
                                accentColor,
                                RoundedCornerShape(26.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
                            style = ImmersiveTypography.headlineSmall,
                            color = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                style = ImmersiveTypography.titleMedium,
                color = ImmersiveColors.DepthNear,
                maxLines = 1
            )

            Text(
                text = role,
                style = ImmersiveTypography.bodySmall,
                color = ImmersiveColors.DepthMid,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            when (status.lowercase()) {
                                "active" -> ImmersiveColors.HolographicGreen
                                "idle" -> ImmersiveColors.HolographicAmber
                                else -> ImmersiveColors.HolographicRed
                            },
                            RoundedCornerShape(3.dp)
                        )
                )
                Text(
                    text = status.uppercase(),
                    style = ImmersiveTypography.labelSmall,
                    color = ImmersiveColors.DepthFar
                )
            }
        }
    }
}

/**
 * Menu option card with icon and action
 */
@Composable
fun MenuOptionCard(
    label: String,
    description: String? = null,
    icon: @Composable () -> Unit,
    accentColor: Color = ImmersiveColors.HolographicCyan,
    onClick: () -> Unit = {}
) {
    ImmersiveCard(
        depth = DepthLevel.FAR,
        accentColor = accentColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        accentColor.copy(alpha = 0.12f),
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        accentColor.copy(alpha = 0.3f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = ImmersiveTypography.titleMedium,
                    color = ImmersiveColors.DepthNear
                )
                description?.let {
                    Text(
                        text = it,
                        style = ImmersiveTypography.bodySmall,
                        color = ImmersiveColors.DepthFar
                    )
                }
            }
        }
    }
}

/**
 * Data readout card with label and value
 */
@Composable
fun DataReadoutCard(
    label: String,
    value: String,
    unit: String? = null,
    trend: String? = null,
    accentColor: Color = ImmersiveColors.HolographicGreen,
    modifier: Modifier = Modifier
) {
    ImmersiveCard(
        depth = DepthLevel.FAR,
        accentColor = accentColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = ImmersiveTypography.labelSmall,
                color = ImmersiveColors.DepthFar
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = ImmersiveTypography.headlineMedium.copy(
                        shadow = Shadow(
                            color = accentColor.copy(alpha = 0.5f),
                            offset = Offset(0f, 0f),
                            blurRadius = 8f
                        )
                    ),
                    color = accentColor
                )

                unit?.let {
                    Text(
                        text = it,
                        style = ImmersiveTypography.bodySmall,
                        color = ImmersiveColors.DepthMid,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            trend?.let {
                Text(
                    text = it,
                    style = ImmersiveTypography.labelSmall,
                    color = if (it.startsWith("+")) ImmersiveColors.HolographicGreen
                           else ImmersiveColors.HolographicRed
                )
            }
        }
    }
}

/**
 * Holographic background with depth layers
 */
@Composable
fun HolographicBackground(
    backgroundImage: String? = null,
    overlayOpacity: Float = 0.7f,
    depthLayers: Int = 3,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Base background image
        backgroundImage?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Depth gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xE6000000),
                            Color(0xF2000000),
                            Color(0xCC000000)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Holographic scan line effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val lineCount = 50
                    val lineSpacing = size.height / lineCount
                    repeat(lineCount) { i ->
                        val y = i * lineSpacing
                        drawLine(
                            color = Color(0x0800F0FF),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 0.5f
                        )
                    }
                }
        )

        // Content
        content()
    }
}
