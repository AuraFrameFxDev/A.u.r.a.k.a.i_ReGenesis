package dev.aurakai.auraframefx.ui.components.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

/**
 * A shimmering particle effect that can be used as an overlay
 * @param particleCount Number of particles (default: 50)
 * @param particleSize Size of each particle in dp (default: 2.dp)
 * @param baseColor Base color of the particles (default: Cyan)
 * @param secondaryColor Secondary color for variation (default: Magenta)
 * @param shimmerIntensity Intensity of the shimmer effect (0f to 1f, default: 0.8f)
 * @param animationDuration Duration of one animation cycle in milliseconds (default: 3000)
 * @param speedMultiplier Speed multiplier for particle movement (default: 1f)
 */
/**
 * Displays an animated shimmering particle overlay effect.
 *
 * Renders a configurable number of particles that move smoothly and shimmer across the composable area. Each particle randomly varies in size, speed, color (between the provided base and secondary colors), and initial position. The shimmer intensity, animation duration, and movement speed can be customized.
 *
 * @param modifier Modifier to apply to the particle overlay.
 * @param particleCount Number of particles to display.
 * @param particleSize Base size of each particle.
 * @param baseColor Primary color used for particles.
 * @param secondaryColor Secondary color used for particles.
 * @param shimmerIntensity Controls the strength of the shimmer effect (0 to 1).
 * @param animationDuration Duration of the shimmer animation cycle in milliseconds.
 * @param speedMultiplier Multiplies the base speed of all particles.
 */
/**
 * Displays an animated shimmering particle overlay effect.
 *
 * Renders a customizable number of particles that move smoothly and shimmer across the composable area.
 * Each particle has randomized size, speed, color (between base and secondary colors), and initial position.
 * The shimmer intensity, animation duration, and movement speed can be adjusted.
 *
 * @param modifier Modifier to apply to the particle overlay.
 * @param particleCount Number of particles to display.
 * @param particleSize Base size of each particle.
 * @param baseColor Primary color used for particles.
 * @param secondaryColor Secondary color used for particles.
 * @param shimmerIntensity Controls the strength of the shimmer effect (0 to 1).
 * @param animationDuration Duration of the shimmer animation in milliseconds.
 * @param speedMultiplier Multiplies the base movement speed of all particles.
 */
@Composable
fun ShimmerParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    particleSize: Dp = 2.dp,
    baseColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Magenta,
    shimmerIntensity: Float = 0.8f,
    animationDuration: Int = 3000,
    speedMultiplier: Float = 1f,
) {
    val density = LocalDensity.current
    val particleSizePx = with(density) { particleSize.toPx() }

    // Generate random particles
    val particles = remember(particleCount) {
        List(particleCount) {
            Particle(
                id = it,
                size = particleSizePx * (0.5f + Random.nextFloat() * 1.5f),
                baseSpeed = 0.2f + Random.nextFloat() * 0.8f,
                color = if (Random.nextBoolean()) baseColor else secondaryColor,
                startX = Random.nextFloat(),
                startY = Random.nextFloat(),
                offsetPhase = Random.nextFloat() * 2f * PI.toFloat(),
                movementPhase = Random.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    // Animation for the shimmer effect
    val infiniteTransition = rememberInfiniteTransition(label = "shimmerParticles")
    val time = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            )
        ),
        label = "time"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Draw each particle
        particles.forEach { particle ->
            // Calculate position with smooth movement
            val angle =
                time.value * 2f * PI.toFloat() * particle.baseSpeed * speedMultiplier + particle.movementPhase
            val offsetX = sin(angle) * canvasWidth * 0.1f
            val offsetY = cos(angle * 1.3f) * canvasHeight * 0.1f

            // Calculate shimmer effect
            val shimmer =
                (sin(time.value * 2f * PI.toFloat() * particle.baseSpeed + particle.offsetPhase) + 1f) / 2f
            val alpha = 0.2f + 0.8f * shimmer * shimmerIntensity

            // Calculate final position
            val x = (particle.startX * canvasWidth + offsetX).coerceIn(0f, canvasWidth)
            val y = (particle.startY * canvasHeight + offsetY).coerceIn(0f, canvasHeight)

            // Draw particle with glow
            val radius = particle.size * (0.8f + 0.4f * shimmer)

            // Outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        particle.color.copy(alpha = alpha * 0.5f),
                        particle.color.copy(alpha = 0f)
                    ),
                    radius = radius * 3f,
                    center = Offset(x, y)
                ),
                radius = radius * 3f,
                center = Offset(x, y)
            )

            // Particle
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Data class representing a single particle in the effect
 */
private data class Particle(
    val id: Int,
    val size: Float,
    val baseSpeed: Float,
    val color: Color,
    val startX: Float,
    val startY: Float,
    val offsetPhase: Float,
    val movementPhase: Float,
)

/**
 * Displays a preview of the ShimmerParticles effect with 100 particles filling the available space.
 */
@Composable
@Preview
fun ShimmerParticlesPreview() {
    ShimmerParticles(
        modifier = Modifier.fillMaxSize(),
        particleCount = 100,
        baseColor = Color.Cyan,
        secondaryColor = Color.Magenta
    )
}
