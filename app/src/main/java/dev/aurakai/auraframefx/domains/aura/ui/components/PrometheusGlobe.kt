package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * 🌍 PROMETHEUS GLOBE
 *
 * Interactive 3D-ish navigation orb.
 * Draggable to scroll the ExodusHUD pager.
 * Tap to proceed.
 */
@Composable
fun PrometheusGlobe(
    color: Color,
    pulseIntensity: Float,
    modifier: Modifier = Modifier,
    onDrag: (Float) -> Unit = {},
    onTap: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlobeRotation")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(
        modifier = modifier
            .size(120.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    onTap()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height) // Bottom edge
            val radius = size.width / 2.5f

            // 1. Soft Outer Glow (Faint pulsing)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = pulse * 0.4f),
                        color.copy(alpha = pulse * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2.5f
                ),
                radius = radius * 2.5f,
                center = center
            )

            // 2. Half-Globe Core (Top hemisphere only)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.6f),
                        color.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // 3. Subtle Inner Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = pulse * 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 0.6f
                ),
                radius = radius * 0.6f,
                center = center
            )
        }
    }
}

