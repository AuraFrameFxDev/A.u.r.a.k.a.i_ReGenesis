// File: embodiment/retrobackdrop/TeleportationEffect.kt
package dev.aurakai.auraframefx.embodiment.retrobackdrop

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin
import kotlin.random.Random

/**
 * Mega Man-typography teleportation beam effect.
 *
 * When Kai gets "tired of Aura's shit" (hit by 3+ cones), he teleports to the top
 * platform with this classic beam-in animation.
 *
 * Features:
 * - Vertical cyan particle beam
 * - White flash at start and end
 * - 1.5-second animation duration
 * - Sound effect trigger point
 */
@Stable
class TeleportationEffect(
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float
) {
    companion object {
        private const val DURATION_MS = 1500f  // 1.5 seconds
        private const val PARTICLE_COUNT = 30
        private const val BEAM_WIDTH = 32f
    }

    private var elapsedTime = 0f
    private var isActive = false
    private val particles = mutableListOf<TeleportParticle>()

    /**
     * Triggers the teleportation effect.
     */
    fun trigger() {
        isActive = true
        elapsedTime = 0f
        particles.clear()

        // Generate particles along the beam path
        repeat(PARTICLE_COUNT) {
            particles.add(
                TeleportParticle(
                    x = targetX + Random.nextFloat() * BEAM_WIDTH - BEAM_WIDTH / 2,
                    y = targetY + Random.nextFloat() * 200f, // Spread along beam
                    speed = 200f + Random.nextFloat() * 100f
                )
            )
        }
    }

    /**
     * Updates the teleportation animation.
     *
     * @param deltaTime Time since last frame in seconds
     * @return true if animation is complete
     */
    fun update(deltaTime: Float): Boolean {
        if (!isActive) return true

        elapsedTime += deltaTime * 1000f // Convert to ms

        // Update particles
        particles.forEach { it.update(deltaTime) }

        // Check if animation is complete
        if (elapsedTime >= DURATION_MS) {
            isActive = false
            return true
        }

        return false
    }

    /**
     * Renders the teleportation effect.
     */
    fun draw(drawScope: DrawScope) {
        if (!isActive) return

        val progress = elapsedTime / DURATION_MS

        with(drawScope) {
            // Flash effect at start and end
            if (progress < 0.1f || progress > 0.9f) {
                drawCircle(
                    color = MegaManPalette.TELEPORT_FLASH.copy(alpha = 0.7f),
                    radius = BEAM_WIDTH * 2,
                    center = Offset(targetX, targetY)
                )
            }

            // Draw particles
            particles.forEach { particle ->
                val alpha = when {
                    progress < 0.3f -> progress / 0.3f  // Fade in
                    progress > 0.7f -> (1f - progress) / 0.3f  // Fade out
                    else -> 1f
                }

                drawCircle(
                    color = MegaManPalette.TELEPORT_PARTICLES.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = 3f,
                    center = Offset(particle.x, particle.y)
                )
            }

            // Beam glow effect
            val beamAlpha = sin(progress * Math.PI).toFloat() * 0.5f
            drawCircle(
                color = MegaManPalette.KAI_TELEPORT.copy(alpha = beamAlpha),
                radius = BEAM_WIDTH,
                center = Offset(targetX, targetY)
            )
        }
    }

    /**
     * Individual particle in the teleportation beam.
     */
    private data class TeleportParticle(
        var x: Float,
        var y: Float,
        val speed: Float
    ) {
        fun update(deltaTime: Float) {
            y -= speed * deltaTime  // Move upward in beam
            // Wrap around if goes too high
            if (y < 0) {
                y += 600f
            }
        }
    }
}
