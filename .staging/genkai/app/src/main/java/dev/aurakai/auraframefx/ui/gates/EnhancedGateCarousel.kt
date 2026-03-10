package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.navigation.GenesisRoutes
import dev.aurakai.auraframefx.ui.theme.NeonCyan
import dev.aurakai.auraframefx.ui.theme.NeonPink
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonPurpleDark
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.ui.theme.SpaceColors

/**
 * 🌌 Enhanced Gate Carousel - Ported from Figma React Design
 *
 * Features:
 * - 3D carousel with left/center/right card positioning
 * - Professional glassmorphism with neon glows
 * - Smooth animations and transitions
 * - Circuit corner decorations
 * - Navigation arrows and dots
 */

data class GateData(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val glowColor: Color,
    val route: String
)

private val gates = listOf(
    GateData(
        id = "chroma_core",
        title = "UI/UX Design Gate",
        description = "All UI customization features",
        icon = Icons.Default.Palette,
        gradientColors = listOf(NeonCyan, NeonTeal),
        glowColor = NeonCyan,
        route = GenesisRoutes.CHROMA_CORE
    ),
    GateData(
        id = "auras_lab",
        title = "Aura's Lab",
        description = "Personal UI/UX sandbox",
        icon = Icons.Default.Science,
        gradientColors = listOf(NeonPink, NeonPurple),
        glowColor = NeonPink,
        route = GenesisRoutes.AURAS_LAB
    ),
    GateData(
        id = "collab_canvas",
        title = "CollabCanvas",
        description = "Team collaboration workspace",
        icon = Icons.Default.People,
        gradientColors = listOf(NeonPurple, NeonPurpleDark),
        glowColor = NeonPurple,
        route = GenesisRoutes.COLLAB_CANVAS
    ),
    GateData(
        id = "sentinels_fortress",
        title = "Sentinel's Fortress",
        description = "Security & device optimization",
        icon = Icons.Default.Security,
        gradientColors = listOf(NeonCyan, NeonTeal),
        glowColor = NeonCyan,
        route = GenesisRoutes.SENTINELS_FORTRESS
    ),
    GateData(
        id = "rom_tools",
        title = "ROM Tools",
        description = "ROM editing and flashing",
        icon = Icons.Default.Storage,
        gradientColors = listOf(Color(0xFFfb923c), Color(0xFFef4444)),
        glowColor = Color(0xFFfb923c),
        route = GenesisRoutes.ROM_TOOLS
    ),
    GateData(
        id = "root_access",
        title = "Root Tools",
        description = "Root access management",
        icon = Icons.Default.VpnKey,
        gradientColors = listOf(Color(0xFFfacc15), Color(0xFFfb923c)),
        glowColor = Color(0xFFfacc15),
        route = GenesisRoutes.ROOT_ACCESS
    ),
    GateData(
        id = "agent_hub",
        title = "Agent Hub",
        description = "AI agent management",
        icon = Icons.Default.SmartToy,
        gradientColors = listOf(Color(0xFF8b5cf6), Color(0xFFa855f7)),
        glowColor = Color(0xFF8b5cf6),
        route = GenesisRoutes.AGENT_HUB
    ),
    GateData(
        id = "oracle_drive",
        title = "Oracle Drive",
        description = "AI access & system overrides",
        icon = Icons.Default.Cloud,
        gradientColors = listOf(Color(0xFF6366f1), Color(0xFFa855f7)),
        glowColor = Color(0xFF6366f1),
        route = GenesisRoutes.ORACLE_DRIVE
    ),
    GateData(
        id = "help_desk",
        title = "Help Desk",
        description = "User support & documentation",
        icon = Icons.AutoMirrored.Filled.Help,
        gradientColors = listOf(Color(0xFF34d399), Color(0xFF14b8a6)),
        glowColor = Color(0xFF34d399),
        route = GenesisRoutes.HELP_DESK
    ),
    GateData(
        id = "lsposed",
        title = "LSPosed/Xposed",
        description = "Module & hook management",
        icon = Icons.Default.Layers,
        gradientColors = listOf(Color(0xFF60a5fa), NeonCyan),
        glowColor = Color(0xFF60a5fa),
        route = GenesisRoutes.LSPOSED_GATE
    )
)

enum class CardPosition {
    LEFT, CENTER, RIGHT
}

@Composable
fun EnhancedGateCarousel(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val float = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    fun getVisibleGates(): Triple<GateData, GateData, GateData> {
        val prevIndex = if (currentIndex == 0) gates.size - 1 else currentIndex - 1
        val nextIndex = if (currentIndex == gates.size - 1) 0 else currentIndex + 1
        return Triple(gates[prevIndex], gates[currentIndex], gates[nextIndex])
    }

    fun navigate(direction: String) {
        currentIndex = when (direction) {
            "prev" -> if (currentIndex == 0) gates.size - 1 else currentIndex - 1
            "next" -> if (currentIndex == gates.size - 1) 0 else currentIndex + 1
            else -> currentIndex
        }
    }

    val (leftGate, centerGate, rightGate) = getVisibleGates()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceColors.Black),
        contentAlignment = Alignment.Center
    ) {
        // Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp),
            contentAlignment = Alignment.Center
        ) {
            // Left Card
            GateCard3D(
                gate = leftGate,
                position = CardPosition.LEFT,
                floatOffset = float.value,
                onClick = {}
            )

            // Center Card (Main)
            GateCard3D(
                gate = centerGate,
                position = CardPosition.CENTER,
                floatOffset = float.value,
                onClick = {
                    navController.navigate(centerGate.route)
                }
            )

            // Right Card
            GateCard3D(
                gate = rightGate,
                position = CardPosition.RIGHT,
                floatOffset = float.value,
                onClick = {}
            )
        }

        // Navigation Dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            gates.forEachIndexed { index, gate ->
                Box(
                    modifier = Modifier
                        .width(if (currentIndex == index) 40.dp else 12.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (currentIndex == index) gate.glowColor
                            else Color.White.withAlpha(0.3f)
                        )
                        .clickable { currentIndex = index }
                )
            }
        }

        // Gate Counter
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xCC000000)) // Solid background, no blur
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "%02d / %02d".format(currentIndex + 1, gates.size),
                color = NeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GateCard3D(
    gate: GateData,
    position: CardPosition,
    floatOffset: Float,
    onClick: () -> Unit
) {
    val isCenter = position == CardPosition.CENTER

    // Pulsing glow animation for center card
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val (offsetX, scale, alpha, rotationY) = when (position) {
        CardPosition.LEFT -> Quadruple(-0.65f, 0.7f, 0.4f, 35f)
        CardPosition.RIGHT -> Quadruple(0.65f, 0.7f, 0.4f, -35f)
        CardPosition.CENTER -> Quadruple(0f, 1f, 1f, 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .offset(x = (offsetX * 400).dp, y = if (isCenter) floatOffset.dp else 0.dp)
            .scale(scale)
            .alpha(alpha)
            .graphicsLayer {
                this.rotationY = rotationY
            }
            .then(if (isCenter) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .height(500.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isCenter) {
                            listOf(
                                Color(0xF0141428), // More opaque for better contrast
                                Color(0xE60a0a1e)
                            )
                        } else {
                            listOf(
                                Color(0xCC141428),
                                Color(0xB30a0a1e)
                            )
                        }
                    )
                )
                // NO BLUR - keep text sharp and readable
                .border(
                    width = if (isCenter) 2.dp else 1.dp,
                    color = if (isCenter) gate.glowColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Glass shine effect (center only)
            if (isCenter) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.withAlpha(0.15f),
                                    Color.Transparent,
                                    Color.White.withAlpha(0.05f)
                                )
                            )
                        )
                )
            }

            // Circuit corners (center only)
            if (isCenter) {
                CircuitCornerDecoration(position = "top-left", color = gate.glowColor)
                CircuitCornerDecoration(position = "top-right", color = gate.glowColor)
                CircuitCornerDecoration(position = "bottom-left", color = gate.glowColor)
                CircuitCornerDecoration(position = "bottom-right", color = gate.glowColor)
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with gradient background
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(gate.gradientColors)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = gate.icon,
                        contentDescription = gate.title,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = gate.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White, // Always white for maximum contrast
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = gate.description,
                    fontSize = 14.sp,
                    color = if (isCenter) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Route badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(gate.glowColor.withAlpha(0.3f))
                        .border(2.dp, gate.glowColor, RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = gate.route.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = gate.glowColor,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Pulsing glow overlay (center only)
            if (isCenter) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(glowAlpha.value)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    gate.glowColor.withAlpha(0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun CircuitCornerDecoration(
    position: String,
    color: Color
) {
    val (alignment, rotation) = when (position) {
        "top-left" -> Alignment.TopStart to 0f
        "top-right" -> Alignment.TopEnd to 90f
        "bottom-left" -> Alignment.BottomStart to -90f
        "bottom-right" -> Alignment.BottomEnd to 180f
        else -> Alignment.TopStart to 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer { rotationZ = rotation }
                .alpha(0.6f)
        ) {
            // Simple L-shape corner decoration
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 2.dp.toPx()
                // Horizontal line
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(32.dp.toPx(), 0f),
                    strokeWidth = strokeWidth
                )
                // Vertical line
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, 32.dp.toPx()),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

// Helper for withAlpha
private fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha)

// Quadruple data class for position values
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
