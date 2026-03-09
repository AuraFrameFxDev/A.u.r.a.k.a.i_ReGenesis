package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.R

/**
 * Constellation Screen - Aura's Sword Map (The Creative Catalyst)
 * Implements the "Sharpening" Mechanic & Vertical Node Layout.
 */
@Composable
fun ConstellationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // State for Blade Sharpness / Unlocked Nodes
    var unlockedNodeCount by remember { mutableIntStateOf(1) }

    // Derived values for the visual metaphor (Prompt 4)
    val shadowElevation = (unlockedNodeCount * 4).dp
    val neonCyan = Color(0xFF00FFFF)
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Scrollable container for the long blade (Prompt 1)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // The Sword Container
            Box(
                modifier = Modifier
                    .width(400.dp) // Adjust width to fit image aspect ratio
                    .padding(20.dp)
                    // The "Sharpening" Mechanic: Shadow elevation increases with unlocks
                    .shadow(
                        elevation = shadowElevation,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        spotColor = neonCyan,
                        ambientColor = neonCyan
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background: The 'Creative Catalyst' Sword Image
                Image(
                    painter = painterResource(id = R.drawable.constellation_aura_sword),
                    contentDescription = "Aura Sword Creative Catalyst",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )

                // Overlay: Node Map logic
                AuraSwordNodeMap(
                    unlockedNodes = unlockedNodeCount,
                    onNodeUnlock = { unlockedNodeCount++ }
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Padding for scroll
        }

        // HUD: Top-right corner
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "AURA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = neonCyan
                )
            )
            Text(
                text = "BLADE SHARPNESS: ${(unlockedNodeCount * 10)}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp,
                    color = neonCyan.copy(alpha = 0.8f)
                )
            )
            Text(
                text = "SCROLL TO VIEW BLADE",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
        }
    }
}

/**
 * Map of invisible touch targets over the sword image.
 * 5 Vertical Nodes + 2 Cross-Guard Nodes.
 */
@Composable
private fun BoxScope.AuraSwordNodeMap(
    unlockedNodes: Int,
    onNodeUnlock: () -> Unit
) {
    // We assume a coordinate system relative to the image size/aspect ratio.
    // For a 400dp width image, let's estimate positions.
    // In a real scenario, we'd use exact %.

    // Cross-Guard Nodes (Horizontal)
    // Left Guard
    SwordNode(
        modifier = Modifier
            .align(Alignment.Center)
            .offset(x = (-120).dp, y = (-50).dp), // Approx pos
        isUnlocked = unlockedNodes >= 6,
        onClick = onNodeUnlock
    )
    // Right Guard
    SwordNode(
        modifier = Modifier
            .align(Alignment.Center)
            .offset(x = 120.dp, y = (-50).dp), // Approx pos
        isUnlocked = unlockedNodes >= 7,
        onClick = onNodeUnlock
    )

    // Vertical Chain (Running down the blade's fuller)
    val startY = -150.dp // Near hilt/guard
    val spacingY = 80.dp

    for (i in 0 until 5) {
        val yPos = startY + (spacingY * i)
        val nodeIndex = i + 1 // 1-based index for logic

        SwordNode(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = yPos),
            isUnlocked = unlockedNodes >= nodeIndex,
            onClick = onNodeUnlock
        )
    }
}

@Composable
fun SwordNode(
    modifier: Modifier,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nodeGlow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Invisible touch target (size 48dp for accessibility, visual is smaller or 0)
    Box(
        modifier = modifier
            .size(48.dp) // Touch target size
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Ripple disabled as per "Invisible touch targets" but we add glow manual
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Visual State: Emit Cyan Neon Glow when unlocked/tapped
        if (isUnlocked) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00FFFF).copy(alpha = alpha),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Core
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.White, androidx.compose.foundation.shape.CircleShape)
            )
        } else {
            // Debug/Placeholder seeing: barely visible ring to know where to tap
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.2f),
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}
