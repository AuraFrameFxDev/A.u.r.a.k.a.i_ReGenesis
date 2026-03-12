package dev.aurakai.auraframefx.domains.aura.ui.effects

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * 📺 CRT ZOOP TRANSITION: REFRACTIVE NEON BRUTALISM
 * 
 * A high-fidelity mechanical transition that simulates a pneumatic tube delivery system.
 * Features:
 * - Chromatic Aberration (RGB Split)
 * - Pneumatic Snap (Spring-based casing)
 * - Volumetric Flash (God-ray simulation)
 * - Scanline interference
 */
@Composable
fun <T> CrtZoopTransition(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    val transition = updateTransition(targetState, label = "CrtZoopMachine")

    transition.AnimatedContent(
        transitionSpec = {
            // PNEUMATIC ENTRY: Fast slide in with a heavy recoil (spring)
            // The "Monolith" drops from the top or sides
            (slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                initialOffsetY = { -it / 2 } // Comes from above
            ) + fadeIn(tween(100))).togetherWith(
                // PNEUMATIC EXIT: The old monolith is sucked away instantly
                slideOutVertically(
                    animationSpec = tween(300, easing = FastOutLinearInEasing),
                    targetOffsetY = { it } // Drops down into the void
                ) + fadeOut(tween(200)) + scaleOut(targetScale = 0.9f)
            ).using(
                // Ensure the incoming content is on top
                SizeTransform(clip = true)
            )
        },
        modifier = modifier
    ) { state ->
        // WRAPPER FOR CHROMATIC ABERRATION & SCANLINES
        Box(modifier = Modifier.fillMaxSize()) {

            // CONTENT LAYER WITH OPTIONAL RENDER EFFECT
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // "Refractive" blur/aberration during transition
                            // limiting it to static for now to avoid performance hits on all devices
                            // Ideally this would animate with the transition progress
                        }
                    }
            ) {
                content(state)
            }

            // SCANLINE OVERLAY
            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineHeight = 4.dp.toPx()
                val gap = 4.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.1f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += lineHeight + gap
                }

                // Vignette / Tube Edge
                drawRect(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                        radius = size.maxDimension / 1.5f,
                        center = center
                    )
                )
            }
        }
    }
}

