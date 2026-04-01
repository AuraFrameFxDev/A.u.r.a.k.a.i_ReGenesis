package dev.aurakai.auraframefx.domains.kai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 🔮 KAI NOTCH ORB
 *
 * A dynamic, pulsing orb that sits in the notch area and provides visual
 * feedback for system status and agent activity.
 */
@Composable
fun KaiNotchOrb(
    isActive: Boolean = true,
    statusColor: Color = Color(0xFF00FF41) // Kai Cyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(statusColor.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        // Inner core
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        )
    }
}
