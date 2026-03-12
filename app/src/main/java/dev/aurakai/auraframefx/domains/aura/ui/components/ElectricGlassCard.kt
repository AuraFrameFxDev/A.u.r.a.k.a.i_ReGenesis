package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * ⚡ ELECTRIC GLASS CARD
 * A premium glassmorphic card with a dynamic "electric" glow border.
 * Designed for the Holo-Projector.
 */
@Composable
fun ElectricGlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF00E5FF),
    content: @Composable BoxScope.() -> Unit
) {
    val sineWaveTransition = rememberInfiniteTransition(label = "AntiGravity")
    val glowAnimation by sineWaveTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val animatedTranslationY by sineWaveTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Float"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = animatedTranslationY
                shadowElevation = 32f
                shape = RoundedCornerShape(24.dp)
                clip = true
            }
            .background(Color.White.copy(alpha = 0.05f))
    ) {
        // --- 1. THE GLASS LAYER ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        // --- 2. THE ELECTRIC BORDER (Pulsing) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            val radius = 24.dp.toPx()

            drawRoundRect(
                color = glowColor,
                style = Stroke(width = strokeWidth * glowAnimation),
                cornerRadius = Stroke.DefaultCap.let {
                    androidx.compose.ui.geometry.CornerRadius(radius, radius)
                },
                alpha = glowAnimation * 0.6f
            )
        }

        // --- 3. THE OUTER GLOW ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(30.dp)
                .border(
                    width = 8.dp,
                    color = glowColor.copy(alpha = 0.1f * glowAnimation),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        // --- 4. CONTENT ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

