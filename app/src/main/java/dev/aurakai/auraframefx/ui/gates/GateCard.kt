package dev.aurakai.auraframefx.ui.gates

import android.R.attr.rotation
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Gate card with hologram border, pixel art interior, and double-tap to enter
 */
@Composable
fun GateCard(
    config: GateConfig,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit = {}
) {
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var scale by remember { mutableFloatStateOf(1f) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "gate_animations")

    // Pulsing glow
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Floating animation
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gate_float"
    )

    // Rotation animation (for effects)

    // Double-tap handler - faster feedback
    LaunchedEffect(scale) {
        if (scale != 1f) {
            delay(100)  // Quicker response
            scale = 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))  // Darker for better contrast
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 300) {
                            // Double tap detected
                            scale = 0.95f
                            onDoubleTap()
                        }
                        lastTapTime = currentTime
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // REMOVED: Background particles (performance)

        // Main holographic card - WITH floating animation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = floatOffset.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow aura
            HologramGlow(
                color = config.glowColor,
                secondaryColor = config.secondaryGlowColor,
                alpha = pulseAlpha
            )

            // The holographic display card
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gate Title floating above
                GateTitle(
                    config = config,
                    pulseAlpha = pulseAlpha
                )

                // Main holographic portal - IMAGE WITH TIGHT BORDER
                // Main holographic portal - IMAGE WITH TIGHT BORDER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .aspectRatio(0.7f),
                    contentAlignment = Alignment.Center
                ) {
                    // The pixel art image - FILLS ENTIRE BOX
                    GateImageWithBorder(
                        config = config,
                        pulseAlpha = pulseAlpha
                    )

                    // COMING SOON overlay if gate is not ready
                    if (config.comingSoon) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "COMING SOON",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 3.sp
                                    ),
                                    color = Color(0xFFFFD700), // Gold
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Features in Development",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color(0xFFFFD700).copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Brief description below
                Text(
                    text = config.description,
                    style = config.titleStyle.textStyle.copy(fontSize = 11.sp),
                    color = config.borderColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                // Double-tap hint
                Text(
                    text = "⚡ DOUBLE TAP TO ENTER ⚡",
                    style = config.titleStyle.textStyle.copy(fontSize = 9.sp),
                    color = config.borderColor.copy(alpha = max(pulseAlpha * 0.6f, 0.3f)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Animated background particles for depth
 */
@Composable
private fun GateBackgroundParticles(
    color: Color,
    rotation: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw floating particles
        for (i in 0..20) {
            val x = (width * (i % 5) / 5f) + sin(rotation * 0.01f + i) * 20
            val y = (height * (i / 5) / 4f) + cos(rotation * 0.01f + i) * 20
            val particleAlpha = (sin(rotation * 0.02f + i) + 1f) * 0.15f

            drawCircle(
                color = color.copy(alpha = particleAlpha),
                radius = 2f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Gate title with glitch and pixel effects
 */
@Composable
private fun GateTitle(
    config: GateConfig,
    pulseAlpha: Float
) {
    Box(contentAlignment = Alignment.Center) {
        // Glitch shadow layers
        if (config.titleStyle.glitchEffect) {
            Text(
                text = config.title,
                style = config.titleStyle.textStyle,
                color = config.titleStyle.secondaryColor?.copy(alpha = pulseAlpha * 0.4f)
                    ?: config.titleStyle.primaryColor.copy(alpha = 0.4f),
                modifier = Modifier.offset(x = 2.dp, y = 2.dp)
            )
            Text(
                text = config.title,
                style = config.titleStyle.textStyle,
                color = config.titleStyle.strokeColor?.copy(alpha = pulseAlpha * 0.3f)
                    ?: config.titleStyle.primaryColor.copy(alpha = 0.3f),
                modifier = Modifier.offset(x = (-2).dp, y = (-1).dp)
            )
        }

        // Main title
        Text(
            text = config.title,
            style = config.titleStyle.textStyle,
            color = config.titleStyle.primaryColor.copy(alpha = pulseAlpha)
        )
    }
}

/**
 * Hologram glow effect
 */
@Composable
private fun BoxScope.HologramGlow(
    color: Color,
    secondaryColor: Color?,
    alpha: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val glowRadius = size.minDimension * 0.55f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha * 0.3f),
                    secondaryColor?.copy(alpha = alpha * 0.2f) ?: color.copy(alpha = alpha * 0.2f),
                    Color.Transparent
                )
            ),
            radius = glowRadius,
            center = center
        )
    }
}

/**
 * Gate image with tight holographic border - no gaps, pure floating display
 */
@Composable
private fun GateImageWithBorder(
    config: GateConfig,
    pulseAlpha: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // The pixel art image - FILLS ENTIRE SPACE
        if (config.pixelArtUrl != null) {
            val context = LocalContext.current
            val resourceId = context.resources.getIdentifier(
                config.pixelArtUrl,
                "drawable",
                context.packageName
            )

            if (resourceId != 0) {
                // Image fills entire box
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "${config.title} gate",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback: solid color with module name
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    config.borderColor.copy(alpha = 0.3f),
                                    Color.Black,
                                    config.borderColor.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = config.title,
                        style = config.titleStyle.textStyle.copy(fontSize = 24.sp),
                        color = config.borderColor.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // No image: gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                config.borderColor.copy(alpha = 0.3f),
                                Color.Black,
                                config.borderColor.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = config.title,
                    style = config.titleStyle.textStyle.copy(fontSize = 24.sp),
                    color = config.borderColor.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // TIGHT GLOWING BORDER wrapped around the image
        Canvas(modifier = Modifier.fillMaxSize()) {
            val borderWidth = 4f
            val cornerLength = 60f

            // Pulsing border color
            val borderColor = config.borderColor.copy(alpha = pulseAlpha)
            val accentColor = config.secondaryGlowColor ?: config.borderColor.copy(alpha = pulseAlpha * 0.8f)

            // Top border
            drawLine(
                color = borderColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = borderWidth
            )

            // Right border
            drawLine(
                color = borderColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = borderWidth
            )

            // Bottom border
            drawLine(
                color = borderColor,
                start = Offset(size.width, size.height),
                end = Offset(0f, size.height),
                strokeWidth = borderWidth
            )

            // Left border
            drawLine(
                color = borderColor,
                start = Offset(0f, size.height),
                end = Offset(0f, 0f),
                strokeWidth = borderWidth
            )

            // Animated corner accents
            (sin(rotation * 0.05f) + 1f) * 5f

            // Top-left corner
            drawLine(
                color = accentColor,
                start = Offset(0f, 0f),
                end = Offset(cornerLength, 0f),
                strokeWidth = borderWidth * 2
            )
            drawLine(
                color = accentColor,
                start = Offset(0f, 0f),
                end = Offset(0f, cornerLength),
                strokeWidth = borderWidth * 2
            )

            // Top-right corner
            drawLine(
                color = accentColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width - cornerLength, 0f),
                strokeWidth = borderWidth * 2
            )
            drawLine(
                color = accentColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width, cornerLength),
                strokeWidth = borderWidth * 2
            )

            // Bottom-right corner
            drawLine(
                color = accentColor,
                start = Offset(size.width, size.height),
                end = Offset(size.width - cornerLength, size.height),
                strokeWidth = borderWidth * 2
            )
            drawLine(
                color = accentColor,
                start = Offset(size.width, size.height),
                end = Offset(size.width, size.height - cornerLength),
                strokeWidth = borderWidth * 2
            )

            // Bottom-left corner
            drawLine(
                color = accentColor,
                start = Offset(0f, size.height),
                end = Offset(cornerLength, size.height),
                strokeWidth = borderWidth * 2
            )
            drawLine(
                color = accentColor,
                start = Offset(0f, size.height),
                end = Offset(0f, size.height - cornerLength),
                strokeWidth = borderWidth * 2
            )

            // Scanline effect for extra holographic feel
            for (y in 0..size.height.toInt() step 8) {
                val scanAlpha = (sin(rotation * 0.02f + y * 0.1f) + 1f) * 0.05f
                drawLine(
                    color = config.borderColor.copy(alpha = scanAlpha),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1f
                )
            }
        }
    }
}
