package dev.aurakai.auraframefx.embodiment.retrobackdrop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * CardExplosionEffect - Transforms static backdrop card into live animation
 *
 * Creates the illusion of a trading card "coming to life" when ROM operations start.
 * The static stage image fragments into colored pixels, which then reorganize
 * into animated character sprites.
 *
 * Timeline:
 * - 0.0-0.2s: Cracks appear
 * - 0.2-0.4s: Explosion (pixels fly outward)
 * - 0.4-0.6s: Swirl (pixels orbit and group by color)
 * - 0.6-0.8s: Reorganize (form Aura/Kai shapes)
 * - 0.8s: Complete (transition to full animation)
 */
class CardExplosionEffect {

    companion object {
        private const val DURATION_MS = 1000f          // 1 second total
        private const val PIXEL_SAMPLE_RATE = 6        // Sample every Nth pixel
        private const val MAX_PIXELS = 800             // Performance limit

        // Timeline phases
        private const val PHASE_CRACK = 0.2f
        private const val PHASE_EXPLODE = 0.4f
        private const val PHASE_SWIRL = 0.6f
        private const val PHASE_REORGANIZE = 0.8f
    }

    private var elapsedTime = 0f
    private var isActive = false
    private val pixels = mutableListOf<ExplodingPixel>()

    // Character target positions (where pixels reorganize to)
    private val auraTargetX = 400f
    private val auraTargetY = 100f
    private val kaiTargetX = 200f
    private val kaiTargetY = 400f

    /**
     * Initialize explosion from static image
     * Samples the image into colored pixels
     */
    fun initializeFromImage(
        imageBitmap: ImageBitmap?,
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        pixels.clear()

        if (imageBitmap == null) {
            // Fallback: Create pixels from palette
            Timber.w("No image provided, using fallback pixel generation")
            generateFallbackPixels(canvasWidth, canvasHeight)
            return
        }

        try {
            // Sample image into pixel grid
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2
            var pixelCount = 0

            for (x in 0 until imageBitmap.width step PIXEL_SAMPLE_RATE) {
                for (y in 0 until imageBitmap.height step PIXEL_SAMPLE_RATE) {
                    if (pixelCount >= MAX_PIXELS) break

                    // Map image coordinates to canvas coordinates
                    val canvasX = (x.toFloat() / imageBitmap.width) * canvasWidth
                    val canvasY = (y.toFloat() / imageBitmap.height) * canvasHeight

                    // Sample pixel color
                    val color = samplePixelColor(imageBitmap, x, y)

                    // Determine target based on color
                    val target = determineTarget(color, canvasX, canvasY)

                    pixels.add(
                        ExplodingPixel(
                            startX = canvasX,
                            startY = canvasY,
                            color = color,
                            targetX = target.first,
                            targetY = target.second,
                            centerX = centerX,
                            centerY = centerY
                        )
                    )

                    pixelCount++
                }
            }

            Timber.i("🎴 Card explosion initialized: $pixelCount pixels sampled")

        } catch (e: Exception) {
            Timber.e(e, "Failed to sample image, using fallback")
            generateFallbackPixels(canvasWidth, canvasHeight)
        }
    }

    private fun samplePixelColor(image: ImageBitmap, x: Int, y: Int): Color {
        // Sample center of pixel region for more accurate color
        return try {
            Color(0xFFFF6B35)  // Placeholder - actual sampling would use image.readPixels
        } catch (e: Exception) {
            MegaManPalette.CONSTRUCTION_ORANGE
        }
    }

    private fun determineTarget(color: Color, x: Float, y: Float): Pair<Float, Float> {
        // Orange/warm colors → Aura (top)
        // Blue/cool colors → Kai (middle)
        // Gray/neutral → Platforms

        val hue = color.red + color.green * 0.5f + color.blue * 0.3f

        return when {
            color.red > 0.6f -> Pair(auraTargetX, auraTargetY)  // Orange → Aura
            color.blue > 0.6f -> Pair(kaiTargetX, kaiTargetY)   // Blue → Kai
            else -> Pair(x * 0.8f, y)  // Neutral → Keep original position
        }
    }

    private fun generateFallbackPixels(width: Float, height: Float) {
        // Generate colored pixels from palette if image unavailable
        val centerX = width / 2
        val centerY = height / 2

        repeat(MAX_PIXELS / 2) {
            pixels.add(
                ExplodingPixel(
                    startX = Random.nextFloat() * width,
                    startY = Random.nextFloat() * height,
                    color = MegaManPalette.AURA_PRIMARY,
                    targetX = auraTargetX,
                    targetY = auraTargetY,
                    centerX = centerX,
                    centerY = centerY
                )
            )
        }

        repeat(MAX_PIXELS / 2) {
            pixels.add(
                ExplodingPixel(
                    startX = Random.nextFloat() * width,
                    startY = Random.nextFloat() * height,
                    color = MegaManPalette.KAI_PRIMARY,
                    targetX = kaiTargetX,
                    targetY = kaiTargetY,
                    centerX = centerX,
                    centerY = centerY
                )
            )
        }
    }

    /**
     * Trigger the explosion
     */
    fun trigger() {
        isActive = true
        elapsedTime = 0f
        Timber.i("💥 Card explosion triggered!")
    }

    /**
     * Update animation state
     * @return true if complete, false if still animating
     */
    fun update(deltaTime: Float): Boolean {
        if (!isActive) return true

        elapsedTime += deltaTime * 1000f
        val progress = (elapsedTime / DURATION_MS).coerceIn(0f, 1f)

        // Update all pixels based on current phase
        pixels.forEach { it.update(progress) }

        if (progress >= 1f) {
            isActive = false
            Timber.i("✨ Card explosion complete - backdrop is now alive!")
            return true
        }

        return false
    }

    /**
     * Draw the explosion effect
     */
    fun draw(scope: DrawScope) {
        if (!isActive) return

        with(scope) {
            val progress = (elapsedTime / DURATION_MS).coerceIn(0f, 1f)

            // Draw crack effect during early phase
            if (progress < PHASE_CRACK) {
                drawCracks(progress / PHASE_CRACK)
            }

            // Draw all pixels
            pixels.forEach { pixel ->
                pixel.draw(this, progress)
            }
        }
    }

    private fun DrawScope.drawCracks(crackProgress: Float) {
        // Draw lightning-bolt style cracks across the screen
        val crackAlpha = crackProgress * 0.8f

        // Vertical cracks
        for (i in 0..5) {
            val x = size.width * (i / 5f)
            drawLine(
                color = Color.White.copy(alpha = crackAlpha),
                start = Offset(x + Random.nextFloat() * 20f, 0f),
                end = Offset(x + Random.nextFloat() * 20f, size.height),
                strokeWidth = 2f
            )
        }

        // Horizontal cracks
        for (i in 0..5) {
            val y = size.height * (i / 5f)
            drawLine(
                color = Color.White.copy(alpha = crackAlpha),
                start = Offset(0f, y + Random.nextFloat() * 20f),
                end = Offset(size.width, y + Random.nextFloat() * 20f),
                strokeWidth = 2f
            )
        }
    }

    fun isExplosionActive(): Boolean = isActive

    fun reset() {
        isActive = false
        elapsedTime = 0f
        pixels.clear()
    }
}

/**
 * Individual pixel in the explosion effect
 */
private data class ExplodingPixel(
    val startX: Float,
    val startY: Float,
    val color: Color,
    val targetX: Float,
    val targetY: Float,
    val centerX: Float,
    val centerY: Float
) {
    var currentX = startX
    var currentY = startY
    var velocityX = 0f
    var velocityY = 0f
    var alpha = 1f
    var size = 3f

    // Explosion parameters
    private val explosionSpeed = 300f + Random.nextFloat() * 200f
    private val swirlRadius = 50f + Random.nextFloat() * 50f
    private val swirlSpeed = Random.nextFloat() * 360f
    private var angle = Random.nextFloat() * 360f

    fun update(progress: Float) {
        when {
            // Phase 1: Explode outward (0.0-0.4)
            progress < 0.4f -> {
                val explosionProgress = progress / 0.4f
                val dx = startX - centerX
                val dy = startY - centerY
                val distance = sqrt(dx * dx + dy * dy)
                val normalizedDx = if (distance > 0) dx / distance else 0f
                val normalizedDy = if (distance > 0) dy / distance else 0f

                currentX = startX + normalizedDx * explosionSpeed * explosionProgress
                currentY = startY + normalizedDy * explosionSpeed * explosionProgress
                alpha = 1f
                size = 3f + explosionProgress * 2f
            }

            // Phase 2: Swirl (0.4-0.6)
            progress < 0.6f -> {
                val swirlProgress = (progress - 0.4f) / 0.2f
                angle += swirlSpeed * 0.016f

                val radians = Math.toRadians(angle.toDouble())
                val orbitX = cos(radians).toFloat() * swirlRadius
                val orbitY = sin(radians).toFloat() * swirlRadius

                // Blend between explosion position and target
                val blendX = currentX + (targetX - currentX) * swirlProgress * 0.3f
                val blendY = currentY + (targetY - currentY) * swirlProgress * 0.3f

                currentX = blendX + orbitX * (1f - swirlProgress)
                currentY = blendY + orbitY * (1f - swirlProgress)
                size = 4f
            }

            // Phase 3: Reorganize to target (0.6-0.8)
            progress < 0.8f -> {
                val reorganizeProgress = (progress - 0.6f) / 0.2f
                currentX = currentX + (targetX - currentX) * reorganizeProgress * 0.5f
                currentY = currentY + (targetY - currentY) * reorganizeProgress * 0.5f
                size = 4f - reorganizeProgress * 1f
            }

            // Phase 4: Solidify (0.8-1.0)
            else -> {
                val solidifyProgress = (progress - 0.8f) / 0.2f
                currentX = targetX
                currentY = targetY
                alpha = 1f - solidifyProgress  // Fade out as we transition to sprites
                size = 3f
            }
        }
    }

    fun draw(scope: DrawScope, progress: Float) {
        scope.drawCircle(
            color = color.copy(alpha = alpha),
            radius = size,
            center = Offset(currentX, currentY)
        )

        // Add glow effect during explosion phase
        if (progress < 0.4f) {
            scope.drawCircle(
                color = Color.White.copy(alpha = alpha * 0.3f),
                radius = size * 1.5f,
                center = Offset(currentX, currentY)
            )
        }
    }
}
