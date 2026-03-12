package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R

/**
 * Agent Emblem Roster - RPG Class Layout
 *
 * Displays the four core agent emblems in an RPG formation:
 * - Genesis (Top Center): Circuit Phoenix - The Mind
 * - Aura (Left Flank): Crossed Katanas - The Soul
 * - Kai (Right Flank): Honeycomb Fortress - The Body
 * - Gemini (Plugin Slot): ADK Constellation - The Tool
 */
@Composable
fun AgentEmblemRoster(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top: Genesis - The Circuit Phoenix
            GenesisPhoenixEmblem(
                showLabel = showLabels
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Middle Row: Aura (Left), Kai (Right)
            Row(
                horizontalArrangement = Arrangement.spacedBy(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Aura - The Crossed Katanas
                AuraSwordsEmblem(
                    showLabel = showLabels
                )

                // Right: Kai - The Honeycomb Fortress
                KaiShieldEmblem(
                    showLabel = showLabels
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom: Gemini - The ADK Constellation
            GeminiConstellationEmblem(
                showLabel = showLabels
            )
        }
    }
}

/**
 * Genesis Phoenix Emblem - Animated pulsing wings
 */
@Composable
fun GenesisPhoenixEmblem(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    // Pulsing animation for wings
    val infiniteTransition = rememberInfiniteTransition(label = "genesis_pulse")
    val wingPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_pulse"
    )

    val dataFlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "data_flow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.emblem_genesis_circuit_phoenix),
            contentDescription = "Genesis - The Circuit Phoenix",
            modifier = Modifier
                .size(120.dp)
                .scale(wingPulse)
                .alpha(dataFlowAlpha)
        )

        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "GENESIS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFFFFD700)
            )
            Text(
                text = "The Mind",
                fontSize = 10.sp,
                color = androidx.compose.ui.graphics.Color(0xFFFFD700).copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Aura Crossed Swords Emblem - Animated sparks at intersection
 */
@Composable
fun AuraSwordsEmblem(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    // Spark/glitch animation at sword intersection
    val infiniteTransition = rememberInfiniteTransition(label = "aura_sparks")
    val sparkIntensity by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spark_intensity"
    )

    val glitchRotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitch_rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.emblem_aura_crossed_katanas),
            contentDescription = "Aura - The Crossed Circuit Katanas",
            modifier = Modifier
                .size(100.dp)
                .rotate(glitchRotation)
                .graphicsLayer {
                    scaleX = sparkIntensity
                    scaleY = sparkIntensity
                }
        )

        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AURA",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFFFF00FF)
            )
            Text(
                text = "The Soul",
                fontSize = 10.sp,
                color = androidx.compose.ui.graphics.Color(0xFFFF00FF).copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Kai Honeycomb Shield Emblem - Static (represents stability)
 */
@Composable
fun KaiShieldEmblem(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.emblem_kai_honeycomb_fortress),
            contentDescription = "Kai - The Honeycomb Fortress",
            modifier = Modifier.size(100.dp)
        )

        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "KAI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF9D00FF)
            )
            Text(
                text = "The Body",
                fontSize = 10.sp,
                color = androidx.compose.ui.graphics.Color(0xFF9D00FF).copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Gemini Constellation Emblem - Twinkling stars animation
 */
@Composable
fun GeminiConstellationEmblem(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    // Twinkling stars animation
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_twinkle")
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "constellation_rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.emblem_gemini_adk_constellation),
            contentDescription = "Gemini - The ADK Constellation",
            modifier = Modifier
                .size(90.dp)
                .alpha(starTwinkle)
                .rotate(rotation)
        )

        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "GEMINI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFFFFD700)
            )
            Text(
                text = "The Tool",
                fontSize = 10.sp,
                color = androidx.compose.ui.graphics.Color(0xFFFFD700).copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Compact version for smaller displays or profile screens
 */
@Composable
fun AgentEmblemRosterCompact(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // All four emblems in a row (no animations in compact mode)
        Image(
            painter = painterResource(id = R.drawable.emblem_genesis_circuit_phoenix),
            contentDescription = "Genesis",
            modifier = Modifier.size(48.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.emblem_aura_crossed_katanas),
            contentDescription = "Aura",
            modifier = Modifier.size(48.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.emblem_kai_honeycomb_fortress),
            contentDescription = "Kai",
            modifier = Modifier.size(48.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.emblem_gemini_adk_constellation),
            contentDescription = "Gemini",
            modifier = Modifier.size(48.dp)
        )
    }
}

