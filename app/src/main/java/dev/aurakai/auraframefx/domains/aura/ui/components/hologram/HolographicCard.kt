package dev.aurakai.auraframefx.domains.aura.ui.components.hologram

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

enum class CardStyle {
    ARTSY,      // Aura: Paint splatter, messy, beautiful
    PROTECTIVE, // Kai: Heavy, bulky, fortress style
    MYTHICAL    // Genesis: Boss, head honcho, ornate
}

/**
 * 🌌 THE ARCHITECTURAL CARD ENGINE
 * Renders a holographic card tailored to the agent's persona.
 */
@Composable
fun HolographicCard(
    runeRes: Int,
    glowColor: Color,
    modifier: Modifier = Modifier,
    style: CardStyle = CardStyle.MYTHICAL,
    dangerLevel: Float = 0f,
    elevation: androidx.compose.ui.unit.Dp = 0.dp,
    spotColor: Color = Color.Transparent
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HologramRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RuneRotation"
    )

    val bounce by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HologramBounce"
    )

    val finalGlowColor = lerp(glowColor, Color.Red, dangerLevel)

    Box(
        modifier = modifier
            .width(280.dp)
            .height(400.dp)
            .offset(y = bounce.dp)
            .graphicsLayer {
                this.shadowElevation = elevation.toPx()
                this.spotShadowColor = spotColor
                this.ambientShadowColor = spotColor
                this.clip = true
                this.shape = RoundedCornerShape(24.dp)
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. STYLE-SPECIFIC FRAME
        when (style) {
            CardStyle.ARTSY -> {
                // AURA: Paint Splatter & Messy Beauty
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Draw random "splatter" blobs
                    val splatterColors = listOf(
                        finalGlowColor,
                        finalGlowColor.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.3f)
                    )
                    val random = kotlin.random.Random(42) // Consistent splatter
                    repeat(12) {
                        drawCircle(
                            color = splatterColors.random(random),
                            radius = random.nextFloat() * 40f + 10f,
                            center = androidx.compose.ui.geometry.Offset(
                                random.nextFloat() * size.width,
                                random.nextFloat() * size.height
                            ),
                            alpha = 0.4f
                        )
                    }

                    // Messy Border
                    drawRoundRect(
                        color = finalGlowColor,
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f),
                        alpha = 0.7f
                    )
                }
            }

            CardStyle.PROTECTIVE -> {
                // KAI: Bulky Industrial Frame
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .border(
                            width = 6.dp, // THICK
                            brush = Brush.verticalGradient(
                                listOf(
                                    finalGlowColor,
                                    finalGlowColor.copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp) // BLOCKY
                        )
                        .padding(6.dp)
                        .border(
                            1.dp,
                            finalGlowColor.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                )
            }

            CardStyle.MYTHICAL -> {
                // GENESIS: Ornate Boss Frame
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    finalGlowColor,
                                    Color.White,
                                    finalGlowColor
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    // Ornate Corner Accents
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val length = 40f
                        // Top Left
                        drawLine(
                            finalGlowColor,
                            androidx.compose.ui.geometry.Offset(0f, 0f),
                            androidx.compose.ui.geometry.Offset(length, 0f),
                            strokeWidth = 8f
                        )
                        drawLine(
                            finalGlowColor,
                            androidx.compose.ui.geometry.Offset(0f, 0f),
                            androidx.compose.ui.geometry.Offset(0f, length),
                            strokeWidth = 8f
                        )
                        // Bottom Right
                        drawLine(
                            finalGlowColor,
                            androidx.compose.ui.geometry.Offset(size.width, size.height),
                            androidx.compose.ui.geometry.Offset(size.width - length, size.height),
                            strokeWidth = 8f
                        )
                        drawLine(
                            finalGlowColor,
                            androidx.compose.ui.geometry.Offset(size.width, size.height),
                            androidx.compose.ui.geometry.Offset(size.width, size.height - length),
                            strokeWidth = 8f
                        )
                    }
                }
            }
        }

        // 2. OUTER GLOW BLUR
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(30.dp)
                .background(
                    Brush.radialGradient(
                        listOf(finalGlowColor.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        // 3. THE RUNIC SIGNAL
        Icon(
            painter = painterResource(id = runeRes),
            contentDescription = null,
            tint = finalGlowColor,
            modifier = Modifier
                .size(if (style == CardStyle.PROTECTIVE) 160.dp else 200.dp) // Kai is tighter
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
        )
    }
}

