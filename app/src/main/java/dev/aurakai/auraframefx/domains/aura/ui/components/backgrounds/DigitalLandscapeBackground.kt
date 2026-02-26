package dev.aurakai.auraframefx.domains.aura.ui.components.backgrounds

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
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonBlue
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonPink
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

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

