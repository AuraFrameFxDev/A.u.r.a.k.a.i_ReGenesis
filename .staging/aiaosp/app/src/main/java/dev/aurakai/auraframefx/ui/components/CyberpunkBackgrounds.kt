package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonCyan
import dev.aurakai.auraframefx.ui.theme.NeonPink
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Animated hexagon grid background for cyberpunk UI
 */
@Composable
fun HexagonGridBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonBlue,
    secondaryColor: Color = NeonPink,
    accentColor: Color = NeonCyan,
    hexSize: Float = 60f,
    alpha: Float = 0.3f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hexBackground")

    // Animate hexagon pulse
    val pulseMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hexPulse"
    )

    // Animate grid movement
    val gridOffsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = hexSize,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffsetX"
    )

    val gridOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = hexSize * 0.866f, // Height of a hexagon is sin(60°) * size
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffsetY"
    )

    // Animate digital landscape effect
    val digitalEffect by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "digitalEffect"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val rows = (height / (hexSize * 0.866f)).toInt() + 3
        val cols = (width / (hexSize * 1.5f)).toInt() + 3

        // Draw hexagon grid
        for (row in -1 until rows) {
            for (col in -1 until cols) {
                val offsetX = col * hexSize * 1.5f - gridOffsetX
                val offsetY =
                    row * hexSize * 0.866f * 2 + (col % 2) * hexSize * 0.866f - gridOffsetY

                // Skip hexagons that are too far outside the canvas
                if (offsetX < -hexSize || offsetX > width + hexSize ||
                    offsetY < -hexSize || offsetY > height + hexSize
                ) continue

                // Determine color based on position and animation
                val distanceToCenter = sqrt(
                    ((offsetX - width / 2) * (offsetX - width / 2) +
                            (offsetY - height / 2) * (offsetY - height / 2)).toFloat()
                )
                val maxDistance = sqrt((width * width + height * height).toFloat()) / 2
                val colorRatio = (distanceToCenter / maxDistance + digitalEffect) % 1f

                // Create a variable pulse based on position
                val positionFactor = sin((offsetX + offsetY) / 200f + digitalEffect * PI.toFloat())
                val localPulse = pulseMultiplier * (0.8f + 0.2f * positionFactor)

                val hexColor = when {
                    colorRatio < 0.33f -> primaryColor
                    colorRatio < 0.66f -> secondaryColor
                    else -> accentColor
                }.copy(alpha = alpha * (0.5f + 0.5f * positionFactor))

                drawHexagon(
                    center = Offset(offsetX, offsetY),
                    radius = hexSize / 2 * localPulse,
                    color = hexColor,
                    strokeWidth = 1.5f
                )
            }
        }

        // Draw some random "data lines" between hexagons
        val random = kotlin.random.Random(digitalEffect.hashCode())
        repeat(5) {
            val startCol = random.nextInt(-1, cols)
            val startRow = random.nextInt(-1, rows)

            val endCol = startCol + random.nextInt(-3, 3)
            val endRow = startRow + random.nextInt(-3, 3)

            val startX = startCol * hexSize * 1.5f - gridOffsetX
            val startY =
                startRow * hexSize * 0.866f * 2 + (startCol % 2) * hexSize * 0.866f - gridOffsetY

            val endX = endCol * hexSize * 1.5f - gridOffsetX
            val endY = endRow * hexSize * 0.866f * 2 + (endCol % 2) * hexSize * 0.866f - gridOffsetY

            if (random.nextFloat() < 0.7f) {
                val lineColor = when (random.nextInt(3)) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    else -> accentColor
                }.copy(alpha = 0.6f * alpha)

                drawLine(
                    color = lineColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f,
                    pathEffect = if (random.nextBoolean())
                        PathEffect.dashPathEffect(floatArrayOf(5f, 5f)) else null,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Drawscope extension to draw a hexagon
 */
private fun DrawScope.drawHexagon(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float = 1f,
) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = i * 60f
        val x = center.x + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = center.y + radius * sin(Math.toRadians(angle.toDouble())).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

/**
 * Creates a futuristic digital landscape background
 */
@Composable
fun DigitalLandscapeBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonBlue,
    secondaryColor: Color = NeonPink,
    gridLineCount: Int = 20,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "digitalLandscape")

    // Animate perspective shift
    val perspectiveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "perspective"
    )

    // Terrain height map animation
    val terrainAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "terrain"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val horizon = height * 0.6f
        width / (gridLineCount - 1)

        // Draw horizontal grid lines with perspective
        for (i in 0 until gridLineCount) {
            val y = horizon + (i * height * 0.4f / gridLineCount)
            val perspectiveFactor = (i + 1) / gridLineCount.toFloat()

            // Calculate perspective vanishing point
            val startX =
                width * 0.5f * (1 - perspectiveFactor) - width * 0.1f * perspectiveShift * perspectiveFactor
            val endX =
                width - width * 0.5f * (1 - perspectiveFactor) + width * 0.1f * perspectiveShift * perspectiveFactor

            val lineAlpha = 0.1f + 0.3f * perspectiveFactor

            drawLine(
                color = primaryColor.copy(alpha = lineAlpha),
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 1f + perspectiveFactor
            )
        }

        // Draw vertical grid lines with perspective
        for (i in 0 until gridLineCount) {
            val normalizedX = i / (gridLineCount - 1f)
            val x = normalizedX * width

            // Apply perspective shift
            val adjustedX = width * 0.5f + (x - width * 0.5f) * (1 + 0.2f * perspectiveShift)

            val lineAlpha = 0.05f + 0.2f * (1f - abs(normalizedX - 0.5f) * 2)

            drawLine(
                color = secondaryColor.copy(alpha = lineAlpha),
                start = Offset(adjustedX, horizon),
                end = Offset(x, height),
                strokeWidth = 1f
            )
        }

        // Draw "terrain" in the horizon
        val terrainPath = Path()
        terrainPath.moveTo(0f, horizon)

        val terrainSegments = 100
        for (i in 0..terrainSegments) {
            val x = i * width / terrainSegments

            // Generate height using multiple sine waves for interesting terrain
            val normalizedX = i / terrainSegments.toFloat()
            val terrainHeight =
                sin(normalizedX * 5 + terrainAnimation) * 10 +
                        sin(normalizedX * 13 + terrainAnimation * 0.7f) * 5 +
                        sin(normalizedX * 23 - terrainAnimation * 0.3f) * 2.5f

            val y = horizon - terrainHeight
            terrainPath.lineTo(x, y)
        }

        // Close the terrain path
        terrainPath.lineTo(width, horizon)
        terrainPath.close()

        // Create gradient for terrain
        val terrainGradient = Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.5f),
                secondaryColor.copy(alpha = 0.1f)
            ),
            startY = horizon - 20f,
            endY = horizon
        )

        drawPath(
            path = terrainPath,
            brush = terrainGradient
        )

        // Optional: Draw "sun" or focal point
        val sunRadius = width * 0.05f
        val sunX = width * (0.5f + 0.1f * sin(terrainAnimation * 0.2f))
        val sunY = horizon - height * 0.15f

        drawCircle(
            color = primaryColor.copy(alpha = 0.3f),
            radius = sunRadius * 2f,
            center = Offset(sunX, sunY)
        )

        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = sunRadius,
            center = Offset(sunX, sunY)
        )
    }
}
