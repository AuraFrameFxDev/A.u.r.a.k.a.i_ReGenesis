package dev.aurakai.auraframefx.aura

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal

/**
 * Composable container with card styling for fragment-like content.
 *
 * Provides a styled container with padding and Material Design 3 theming,
 * useful for creating modular UI sections similar to Android Fragments.
 *
 * @param modifier Optional modifier for the container
 * @param name Display name for the fragment content
 */
@Composable
fun FragmentComposable(
    modifier: Modifier = Modifier,
    name: String = "Fragment",
) {
    Card(
        modifier = modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Fragment Content",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Animated Aura Orb component with pulsing glow and color effects.
 *
 * Features:
 * - Continuous pulsing scale animation
 * - Radial gradient glow with animated blur
 * - Neon color scheme (Purple/Teal)
 * - Circular shadow and clip shape
 *
 * This component represents the visual presence of the Aura AI assistant.
 *
 * @param modifier Optional modifier for the orb
 */
@Composable
fun PlaceholderAuraOrb(modifier: Modifier = Modifier) {
    // Infinite pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "aura_orb_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer glow layer
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulseScale)
                .alpha(glowAlpha * 0.5f)
                .blur(24.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonTeal.copy(alpha = glowAlpha),
                            NeonPurple.copy(alpha = glowAlpha * 0.7f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main orb
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.9f),
                            NeonPurple.copy(alpha = 0.6f)
                        )
                    )
                )
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    spotColor = NeonTeal,
                    ambientColor = NeonPurple
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Orb",
                color = NeonTeal,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "Placeholder Aura Orb Preview")
@Composable
fun PlaceholderAuraOrbPreview() { // Renamed
    PlaceholderAuraOrb()
}

@Preview(showBackground = true, name = "Fragment Composable Preview")
@Composable
fun FragmentComposablePreview() { // Renamed from FragmentPreview
    FragmentComposable(name = "Sample Fragment")
}
