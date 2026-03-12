package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

/**
 * A custom composable that creates a holographic transition effect with scan lines, grid, and edge glow.
 *
 * @param visible Controls the visibility of the hologram effect
 * @param modifier Modifier to be applied to the layout
 * @param content The content to be displayed with the hologram effect
 * @param primaryColor Primary color for the hologram effect
 * @param secondaryColor Secondary color for the hologram effect
 * @param scanLineDensity Number of scan lines to display
 * @param glitchIntensity Intensity of the glitch effect (0f to 1f)
 * @param edgeGlowIntensity Intensity of the edge glow effect (0f to 1f)
 */
@Composable
fun HologramTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    primaryColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Magenta,
    scanLineDensity: Int = 8,
    glitchIntensity: Float = 0.1f,
    edgeGlowIntensity: Float = 0.3f
) {
    // Animation states
    val transition = updateTransition(visible, label = "hologramTransition")
    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = if (visible) 800 else 500) },
        label = "alpha"
    ) { if (it) 1f else 0f }

    // Scan line animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLineOffset"
    )

    // Edge glow animation
    val edgeGlow by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "edgeGlow"
    )

    // Draw the hologram effect
    Box(
        modifier = modifier
            .clipToBounds()
            .graphicsLayer { this.alpha = alpha }
    ) {
        // Content
        content()

        // Hologram overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val density = LocalDensity.current.density

            // Draw edge glow
            val edgeGlowBrush = Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.3f * edgeGlowIntensity * alpha),
                    primaryColor.copy(alpha = 0f)
                ),
                start = Offset(0f, 0f),
                end = Offset(width * 0.3f, 0f)
            )

            // Draw grid
            val gridSize = 20.dp.toPx()
            val gridColor = primaryColor.copy(alpha = 0.1f * alpha)
            val gridStroke = 0.5f / density
            
            // Scan line settings
            val scanLineColor = secondaryColor.copy(alpha = 0.1f * alpha)
            val scanLineStroke = 1f / density

            // Draw grid
            for (x in 0..width.toInt() step gridSize.toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), height),
                    strokeWidth = gridStroke
                )
            }
            for (y in 0..height.toInt() step gridSize.toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y.toFloat()),
                    end = Offset(width, y.toFloat()),
                    strokeWidth = gridStroke
                )
            }

            // Draw scan lines
            val scanLineSpacing = height / scanLineDensity
            val scanLineY = (scanLineOffset * scanLineSpacing * 2) - scanLineSpacing
            for (i in -1..scanLineDensity) {
                val y = scanLineY + (i * scanLineSpacing)
                drawLine(
                    color = scanLineColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = scanLineStroke
                )
            }
            
            // Apply glitch effect
            val glitchOffset = (1f - glitchIntensity) + (Random.nextFloat() * glitchIntensity * 2f)
            
            withTransform(
                transformBlock = {
                    translate(
                        left = (Random.nextFloat() - 0.5f) * 2f * glitchIntensity * 10f,
                        top = (Random.nextFloat() - 0.5f) * 2f * glitchIntensity * 10f
                    )
                    scale(
                        scaleX = glitchOffset,
                        scaleY = glitchOffset,
                        pivotX = width / 2f,
                        pivotY = height / 2f
                    )
                }
            ) {
                // Draw edge glow on all four sides
                // Left edge
                drawRect(
                    brush = edgeGlowBrush,
                    topLeft = Offset(0f, 0f),
                    size = Size(width * 0.3f, height)
                )
                
                // Right edge
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0f),
                            primaryColor.copy(alpha = 0.3f * edgeGlowIntensity * alpha)
                        )
                    ),
                    topLeft = Offset(width * 0.7f, 0f),
                    size = Size(width * 0.3f, height)
                )
                
                // Top edge
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f * edgeGlowIntensity * alpha),
                            primaryColor.copy(alpha = 0f)
                        )
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(width, height * 0.3f)
                )
                
                // Bottom edge
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0f),
                            primaryColor.copy(alpha = 0.3f * edgeGlowIntensity * alpha)
                        )
                    ),
                    topLeft = Offset(0f, height * 0.7f),
                    size = Size(width, height * 0.3f)
                )
            }

            // Draw corner brackets
            val bracketSize = 20f
            val bracketWidth = 2f
            val bracketColor = primaryColor.copy(alpha = 0.8f * alpha)

            // Top-left bracket
            drawLine(
                color = bracketColor,
                start = Offset(0f, 0f),
                end = Offset(bracketSize, 0f),
                strokeWidth = bracketWidth
            )
            drawLine(
                color = bracketColor,
                start = Offset(0f, 0f),
                end = Offset(0f, bracketSize),
                strokeWidth = bracketWidth
            )

            // Top-right bracket
            drawLine(
                color = bracketColor,
                start = Offset(width - bracketSize, 0f),
                end = Offset(width, 0f),
                strokeWidth = bracketWidth
            )
            drawLine(
                color = bracketColor,
                start = Offset(width, 0f),
                end = Offset(width, bracketSize),
                strokeWidth = bracketWidth
            )

            // Bottom-left bracket
            drawLine(
                color = bracketColor,
                start = Offset(0f, height - bracketSize),
                end = Offset(0f, height),
                strokeWidth = bracketWidth
            )
            drawLine(
                color = bracketColor,
                start = Offset(0f, height),
                end = Offset(bracketSize, height),
                strokeWidth = bracketWidth
            )

            // Bottom-right bracket
            drawLine(
                color = bracketColor,
                start = Offset(width - bracketSize, height),
                end = Offset(width, height),
                strokeWidth = bracketWidth
            )
            drawLine(
                color = bracketColor,
                start = Offset(width, height - bracketSize),
                end = Offset(width, height),
                strokeWidth = bracketWidth
            )

            // Draw some random digital noise
            if (visible && glitchIntensity > 0.1f) {
                val noiseCount = (width * height * 0.0005f * glitchIntensity).toInt()
                repeat(noiseCount) {
                    val x = Random.nextFloat() * width
                    val y = Random.nextFloat() * height
                    val size = Random.nextFloat() * 2f * glitchIntensity
                    drawCircle(
                        color = primaryColor.copy(alpha = Random.nextFloat() * 0.5f * alpha),
                        radius = size,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
