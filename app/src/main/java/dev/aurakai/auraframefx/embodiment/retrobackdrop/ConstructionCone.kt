// File: embodiment/retrobackdrop/ConstructionCone.kt
package dev.aurakai.auraframefx.embodiment.retrobackdrop

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate

/**
 * Construction cone with physics for bouncing, rolling, and sliding down chutes.
 *
 * Aura throws these from the top platform, and they can:
 * - Bounce on platforms
 * - Slide down chutes (red slides)
 * - Hit Kai and trigger teleportation
 */
@Stable
data class ConstructionCone(
    var x: Float,
    var y: Float,
    var velocityX: Float = 0f,
    var velocityY: Float = 0f,
    var rotation: Float = 0f,
    var onChute: Boolean = false
) {
    companion object {
        private const val GRAVITY = 400f  // Pixels per second squared
        private const val CHUTE_GRAVITY = 800f  // 2x gravity on chutes
        private const val BOUNCE_COEFFICIENT = 0.6f
        private const val FRICTION = 0.8f
        private const val CONE_SIZE = 12f  // 12x12 pixel cone
    }

    /**
     * Updates cone physics.
     *
     * @param deltaTime Time since last frame in seconds
     * @param platforms List of platform rectangles
     * @param chutes List of chute rectangles
     */
    fun update(
        deltaTime: Float,
        platforms: List<Rect>,
        chutes: List<Rect>
    ) {
        if (onChute) {
            // Chute physics: faster slide, no bounce
            velocityY += CHUTE_GRAVITY * deltaTime
            velocityX = 0.5f * velocityY  // Follow chute angle (~45 degrees)
            rotation += 5f * deltaTime  // Faster spin on chute

            // Check if exited chute
            var stillOnChute = false
            for (chute in chutes) {
                if (chute.contains(Offset(x, y))) {
                    stillOnChute = true
                    break
                }
            }
            if (!stillOnChute) {
                onChute = false
            }
        } else {
            // Normal free-fall physics
            velocityY += GRAVITY * deltaTime

            // Update position
            x += velocityX * deltaTime
            y += velocityY * deltaTime

            // Rotation based on velocity
            rotation += velocityX * 2f * deltaTime

            // Check for chute entry
            for (chute in chutes) {
                if (chute.contains(Offset(x, y))) {
                    onChute = true
                    velocityX = 0f  // Reset horizontal velocity
                    break
                }
            }

            // Platform collision detection
            if (!onChute) {
                for (platform in platforms) {
                    if (platform.contains(Offset(x, y)) && velocityY > 0) {
                        // Bounce!
                        velocityY *= -BOUNCE_COEFFICIENT
                        velocityX *= FRICTION
                        y = platform.top - 1f  // Position just above platform
                        break
                    }
                }
            }
        }
    }

    /**
     * Draws the construction cone.
     */
    fun draw(drawScope: DrawScope) {
        with(drawScope) {
            rotate(rotation, pivot = Offset(x, y)) {
                // Draw cone as triangle
                val path = Path().apply {
                    moveTo(x, y - CONE_SIZE)  // Top point
                    lineTo(x - CONE_SIZE / 2, y + CONE_SIZE / 2)  // Bottom left
                    lineTo(x + CONE_SIZE / 2, y + CONE_SIZE / 2)  // Bottom right
                    close()
                }

                drawPath(
                    path = path,
                    color = MegaManPalette.CONSTRUCTION_ORANGE
                )

                // White stripes for construction cone look
                val stripeY = y
                drawCircle(
                    color = MegaManPalette.WHITE,
                    radius = 3f,
                    center = Offset(x, stripeY)
                )
            }
        }
    }

    /**
     * Checks if this cone hit Kai.
     */
    fun hitsKai(kaiX: Float, kaiY: Float, kaiSize: Float = 16f): Boolean {
        val dx = x - kaiX
        val dy = y - kaiY
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
        return distance < (CONE_SIZE + kaiSize) / 2
    }

    /**
     * Checks if cone is off-screen (can be removed).
     */
    fun isOffScreen(screenHeight: Float): Boolean {
        return y > screenHeight + 50f
    }
}
