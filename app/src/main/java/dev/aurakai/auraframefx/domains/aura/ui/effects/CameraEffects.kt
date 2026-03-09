package dev.aurakai.auraframefx.domains.aura.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 📷 Camera Effects for 3D Gyroscope Workbench
 *
 * Visual effects that can be applied to UI components during manipulation:
 * - Fish Eye: Lens distortion effect
 * - Dissipate: Particle dispersion
 * - Warp: Space-time distortion
 * - Contour: Edge detection/outline
 * - Deconstruct: Already exists in HomeScreenTransitionType
 *
 * These work with gyroscope data to create depth-aware effects.
 *
 * Example:
 * ```
 * Box(modifier = Modifier.fillMaxSize()) {
 *     ComponentBeingEdited()
 *     CameraEffectOverlay(
 *         effect = CameraEffect.FISH_EYE,
 *         intensity = 0.7f
 *     )
 * }
 * ```
 */

/**
 * Camera effect types
 */
enum class CameraEffect {
    NONE,
    FISH_EYE,
    DISSIPATE,
    WARP,
    CONTOUR,
    DECONSTRUCT; // Reference to existing implementation

    fun getDisplayName(): String = when (this) {
        NONE -> "No Effect"
        FISH_EYE -> "Fish Eye Lens"
        DISSIPATE -> "Particle Dissipate"
        WARP -> "Space Warp"
        CONTOUR -> "Contour Outline"
        DECONSTRUCT -> "Digital Deconstruct"
    }

    fun getDescription(): String = when (this) {
        NONE -> "No visual effect applied"
        FISH_EYE -> "Distorts view like a fisheye camera lens"
        DISSIPATE -> "Breaks component into dispersing particles"
        WARP -> "Bends space around the component"
        CONTOUR -> "Highlights edges and outlines"
        DECONSTRUCT -> "Deconstructs component into digital fragments"
    }
}

/**
 * Camera Effect Overlay - Apply effects to content below
 */
@Composable
fun CameraEffectOverlay(
    effect: CameraEffect,
    intensity: Float = 1f,
    animated: Boolean = true,
    gyroscopeX: Float = 0f,
    gyroscopeY: Float = 0f,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    // Animation time
    val infiniteTransition = rememberInfiniteTransition(label = "effect_time")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
    ) {
        when (effect) {
            CameraEffect.NONE -> { /* No effect */
            }

            CameraEffect.FISH_EYE -> {
                FishEyeEffect(
                    intensity = intensity,
                    gyroscopeX = gyroscopeX,
                    gyroscopeY = gyroscopeY,
                    size = size
                )
            }

            CameraEffect.DISSIPATE -> {
                DissipateEffect(
                    intensity = intensity,
                    animTime = if (animated) animTime else 0f,
                    size = size
                )
            }

            CameraEffect.WARP -> {
                WarpEffect(
                    intensity = intensity,
                    gyroscopeX = gyroscopeX,
                    gyroscopeY = gyroscopeY,
                    animTime = if (animated) animTime else 0f,
                    size = size
                )
            }

            CameraEffect.CONTOUR -> {
                ContourEffect(
                    intensity = intensity,
                    size = size
                )
            }

            CameraEffect.DECONSTRUCT -> {
                // Integrates with HomeScreenTransitionType.DIGITAL_DECONSTRUCT
                // Uses fragment-based deconstruction matching the system transition
                DeconstructEffectPlaceholder(intensity, animTime, size)
            }
        }
    }
}

/**
 * 🐠 Fish Eye Effect - Lens distortion
 */
@Composable
fun FishEyeEffect(
    intensity: Float,
    gyroscopeX: Float,
    gyroscopeY: Float,
    size: IntSize
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f + (gyroscopeX * 100)
        val centerY = size.height / 2f + (gyroscopeY * 100)
        val maxRadius = size.width.coerceAtLeast(size.height) / 2f

        // Draw distortion grid
        val gridSize = 30
        val cellWidth = size.width / gridSize.toFloat()
        val cellHeight = size.height / gridSize.toFloat()

        // Vertical lines
        for (i in 0..gridSize) {
            val path = Path()
            val x = i * cellWidth

            for (j in 0..gridSize) {
                val y = j * cellHeight
                val distorted = applyFishEyeDistortion(
                    Offset(x, y),
                    Offset(centerX, centerY),
                    maxRadius,
                    intensity
                )

                if (j == 0) {
                    path.moveTo(distorted.x, distorted.y)
                } else {
                    path.lineTo(distorted.x, distorted.y)
                }
            }

            drawPath(
                path = path,
                color = CyberpunkCyan.copy(alpha = 0.3f),
                style = Stroke(width = 1f)
            )
        }

        // Horizontal lines
        for (j in 0..gridSize) {
            val path = Path()
            val y = j * cellHeight

            for (i in 0..gridSize) {
                val x = i * cellWidth
                val distorted = applyFishEyeDistortion(
                    Offset(x, y),
                    Offset(centerX, centerY),
                    maxRadius,
                    intensity
                )

                if (i == 0) {
                    path.moveTo(distorted.x, distorted.y)
                } else {
                    path.lineTo(distorted.x, distorted.y)
                }
            }

            drawPath(
                path = path,
                color = CyberpunkCyan.copy(alpha = 0.3f),
                style = Stroke(width = 1f)
            )
        }

        // Center indicator
        drawCircle(
            color = CyberpunkCyan.copy(alpha = 0.5f),
            radius = 20f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Apply fish eye distortion to a point
 */
fun applyFishEyeDistortion(
    point: Offset,
    center: Offset,
    maxRadius: Float,
    intensity: Float
): Offset {
    val dx = point.x - center.x
    val dy = point.y - center.y
    val distance = sqrt(dx * dx + dy * dy)

    if (distance < 1f) return point

    val normalizedDistance = distance / maxRadius
    val distortionFactor = 1f + (intensity * normalizedDistance * normalizedDistance)

    return Offset(
        center.x + dx * distortionFactor,
        center.y + dy * distortionFactor
    )
}

/**
 * 💨 Dissipate Effect - Particle dispersion
 */
@Composable
fun DissipateEffect(
    intensity: Float,
    animTime: Float,
    size: IntSize
) {
    // Generate particles
    val particles = remember {
        List(200) {
            Particle(
                initialX = Random.nextFloat() * size.width,
                initialY = Random.nextFloat() * size.height,
                velocityX = (Random.nextFloat() - 0.5f) * 200f,
                velocityY = (Random.nextFloat() - 0.5f) * 200f,
                size = Random.nextFloat() * 5f + 2f,
                color = if (Random.nextBoolean()) CyberpunkPink else CyberpunkCyan
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val progress = animTime
            val x = particle.initialX + (particle.velocityX * progress * intensity)
            val y = particle.initialY + (particle.velocityY * progress * intensity)
            val alpha = 1f - (progress * intensity).coerceIn(0f, 1f)

            if (alpha > 0.1f) {
                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.size,
                    center = Offset(x, y)
                )

                // Particle trail
                if (intensity > 0.5f) {
                    drawLine(
                        color = particle.color.copy(alpha = alpha * 0.3f),
                        start = Offset(particle.initialX, particle.initialY),
                        end = Offset(x, y),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

data class Particle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color
)

/**
 * 🌀 Warp Effect - Space-time distortion
 */
@Composable
fun WarpEffect(
    intensity: Float,
    gyroscopeX: Float,
    gyroscopeY: Float,
    animTime: Float,
    size: IntSize
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f + (gyroscopeX * 150)
        val centerY = size.height / 2f + (gyroscopeY * 150)

        // Draw warping rings
        val ringCount = 15
        val maxRadius = size.width.coerceAtLeast(size.height) / 1.5f

        for (i in 0 until ringCount) {
            val normalizedRadius = i / ringCount.toFloat()
            val radius = maxRadius * normalizedRadius

            // Warp intensity decreases with distance
            val warpFactor = intensity * (1f - normalizedRadius)

            // Animate ring pulsation
            val pulseOffset = sin((animTime * PI * 2).toFloat() + (i * 0.3f)) * warpFactor * 20f

            // Draw warped ring
            val path = Path()
            val segments = 60

            for (j in 0..segments) {
                val angle = (j / segments.toFloat()) * 2f * PI.toFloat()

                // Apply warping based on angle and time
                val warpX = cos(angle + animTime * PI.toFloat()) * warpFactor * 30f
                val warpY = sin(angle + animTime * PI.toFloat()) * warpFactor * 30f

                val x = centerX + cos(angle) * (radius + pulseOffset) + warpX
                val y = centerY + sin(angle) * (radius + pulseOffset) + warpY

                if (j == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            val alpha = (1f - normalizedRadius) * 0.5f
            val color = lerp(CyberpunkPink, CyberpunkCyan, normalizedRadius)

            drawPath(
                path = path,
                color = color.copy(alpha = alpha),
                style = Stroke(width = 2f)
            )
        }

        // Center vortex
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyberpunkPink.copy(alpha = 0.8f),
                    CyberpunkCyan.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = 100f
            ),
            radius = 100f,
            center = Offset(centerX, centerY)
        )
    }
}

/**
 * 📐 Contour Effect - Edge detection/outline
 */
@Composable
fun ContourEffect(
    intensity: Float,
    size: IntSize
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw contour lines across the screen
        val lineSpacing = 15f / intensity.coerceAtLeast(0.1f)
        val lineCount = (size.height / lineSpacing).toInt()

        for (i in 0 until lineCount) {
            val y = i * lineSpacing
            val path = Path()

            // Create wavy contour line
            for (x in 0..size.width step 5) {
                val wave = sin((x / 50f) + (i * 0.2f)) * 10f * intensity
                val pointY = y + wave

                if (x == 0) {
                    path.moveTo(x.toFloat(), pointY)
                } else {
                    path.lineTo(x.toFloat(), pointY)
                }
            }

            // Alternate colors for depth perception
            val color = if (i % 2 == 0) CyberpunkCyan else CyberpunkPink

            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f * intensity),
                style = Stroke(width = 1.5f)
            )
        }

        // Vertical contours
        val verticalSpacing = 15f / intensity.coerceAtLeast(0.1f)
        val verticalCount = (size.width / verticalSpacing).toInt()

        for (i in 0 until verticalCount) {
            val x = i * verticalSpacing
            val path = Path()

            for (y in 0..size.height step 5) {
                val wave = sin((y / 50f) + (i * 0.2f)) * 10f * intensity
                val pointX = x + wave

                if (y == 0) {
                    path.moveTo(pointX, y.toFloat())
                } else {
                    path.lineTo(pointX, y.toFloat())
                }
            }

            val color = if (i % 2 == 0) CyberpunkPink else CyberpunkCyan

            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f * intensity),
                style = Stroke(width = 1.5f)
            )
        }

        // Edge highlights
        drawRect(
            color = CyberpunkCyan.copy(alpha = 0.4f * intensity),
            topLeft = Offset.Zero,
            size = Size(size.width.toFloat(), 2f)
        )
        drawRect(
            color = CyberpunkPink.copy(alpha = 0.4f * intensity),
            topLeft = Offset(0f, size.height.toFloat() - 2f),
            size = Size(size.width.toFloat(), 2f)
        )
        drawRect(
            color = CyberpunkCyan.copy(alpha = 0.4f * intensity),
            topLeft = Offset.Zero,
            size = Size(2f, size.height.toFloat())
        )
        drawRect(
            color = CyberpunkPink.copy(alpha = 0.4f * intensity),
            topLeft = Offset(size.width.toFloat() - 2f, 0f),
            size = Size(2f, size.height.toFloat())
        )
    }
}

/**
 * 🔨 Deconstruct Effect
 *
 * Integrated with HomeScreenTransitionType.DIGITAL_DECONSTRUCT for consistent
 * fragment-based deconstruction effects across camera and system transitions.
 * Creates animated cyberpunk fragments that scatter based on intensity and time.
 */
@Composable
fun DeconstructEffectPlaceholder(
    intensity: Float,
    animTime: Float,
    size: IntSize
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Simple fragment simulation
        val fragmentCount = (intensity * 100).toInt()

        repeat(fragmentCount) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            val fragmentSize = Random.nextFloat() * 20f + 5f

            val offsetX = (Random.nextFloat() - 0.5f) * 100f * animTime * intensity
            val offsetY = (Random.nextFloat() - 0.5f) * 100f * animTime * intensity

            drawRect(
                color = if (Random.nextBoolean()) CyberpunkPink else CyberpunkCyan,
                topLeft = Offset(x + offsetX, y + offsetY),
                size = Size(fragmentSize, fragmentSize),
                alpha = (1f - animTime * intensity).coerceIn(0f, 1f)
            )
        }

        // Grid overlay
        val gridSize = 20
        for (i in 0..gridSize) {
            val x = (size.width / gridSize.toFloat()) * i
            val offset = sin((animTime + i * 0.1f) * PI.toFloat()) * 20f * intensity

            drawLine(
                color = CyberpunkCyan.copy(alpha = 0.3f),
                start = Offset(x + offset, 0f),
                end = Offset(x + offset, size.height.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}

/**
 * Effect selector UI
 */
@Composable
fun CameraEffectSelector(
    currentEffect: CameraEffect,
    onEffectSelected: (CameraEffect) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation in separate file if needed for UI
}

