package dev.aurakai.auraframefx.embodiment.retrobackdrop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * HexagonalShield - Kai's defensive ability
 *
 * Blocks incoming construction cones with a particle-based hexagonal barrier.
 * Duration: 2 seconds
 * Cooldown: 23 seconds
 *
 * Visual: Rotating hexagon with cyan particles orbiting around Kai
 */
class HexagonalShield {

    companion object {
        private const val SHIELD_DURATION = 2000f      // 2 seconds
        private const val COOLDOWN_DURATION = 23000f   // 23 seconds
        private const val HEXAGON_RADIUS = 40f         // Shield radius
        private const val PARTICLE_COUNT = 36          // Number of shield particles
        private const val ROTATION_SPEED = 120f        // Degrees per second
    }

    // Shield state
    private var isActive = false
    private var activationTime = 0L
    private var lastActivationEnd = 0L
    private var rotation = 0f

    // Particle system
    private val particles = mutableListOf<ShieldParticle>()

    /**
     * Try to activate the shield
     * @return true if activated, false if on cooldown
     */
    fun activate(): Boolean {
        val now = System.currentTimeMillis()
        val timeSinceLastUse = now - lastActivationEnd

        if (timeSinceLastUse < COOLDOWN_DURATION) {
            return false  // Still on cooldown
        }

        isActive = true
        activationTime = now
        initializeParticles()
        return true
    }

    /**
     * Update shield state and particles
     * @param deltaTime Time since last frame in seconds
     * @return true if shield is still active
     */
    fun update(deltaTime: Float): Boolean {
        if (!isActive) return false

        val elapsed = System.currentTimeMillis() - activationTime

        // Deactivate after 2 seconds
        if (elapsed >= SHIELD_DURATION) {
            isActive = false
            lastActivationEnd = System.currentTimeMillis()
            particles.clear()
            return false
        }

        // Update rotation
        rotation += ROTATION_SPEED * deltaTime
        if (rotation >= 360f) rotation -= 360f

        // Update particles
        particles.forEach { it.update(deltaTime, rotation) }

        return true
    }

    /**
     * Check if a point is blocked by the shield
     * @param x X coordinate to test
     * @param y Y coordinate to test
     * @param centerX Shield center X
     * @param centerY Shield center Y
     * @return true if point is blocked
     */
    fun blocksPoint(x: Float, y: Float, centerX: Float, centerY: Float): Boolean {
        if (!isActive) return false

        val dx = x - centerX
        val dy = y - centerY
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

        return distance <= HEXAGON_RADIUS + 10f  // 10px buffer for visual clarity
    }

    /**
     * Draw the shield
     */
    fun draw(scope: DrawScope, centerX: Float, centerY: Float) {
        if (!isActive) return

        with(scope) {
            // Calculate alpha fade-out in last 0.5 seconds
            val elapsed = System.currentTimeMillis() - activationTime
            val alpha = if (elapsed > SHIELD_DURATION - 500f) {
                ((SHIELD_DURATION - elapsed) / 500f).coerceIn(0f, 1f)
            } else {
                1f
            }

            // Draw hexagon outline (rotating)
            drawHexagon(centerX, centerY, HEXAGON_RADIUS, rotation, alpha)

            // Draw inner hexagon (counter-rotating)
            drawHexagon(centerX, centerY, HEXAGON_RADIUS * 0.7f, -rotation * 1.5f, alpha * 0.6f)

            // Draw particles
            particles.forEach { particle ->
                particle.draw(this, centerX, centerY, alpha)
            }

            // Draw center glow
            drawCircle(
                color = MegaManPalette.KAI_TELEPORT.copy(alpha = alpha * 0.3f),
                radius = HEXAGON_RADIUS * 0.5f,
                center = Offset(centerX, centerY)
            )
        }
    }

    private fun DrawScope.drawHexagon(
        centerX: Float,
        centerY: Float,
        radius: Float,
        rotation: Float,
        alpha: Float
    ) {
        val path = Path()
        val angleOffset = Math.toRadians(rotation.toDouble())

        // Calculate 6 points of hexagon
        for (i in 0..6) {
            val angle = angleOffset + (Math.PI / 3.0) * i
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw outline
        drawPath(
            path = path,
            color = MegaManPalette.KAI_TELEPORT.copy(alpha = alpha),
            style = Stroke(width = 3f)
        )

        // Draw glow
        drawPath(
            path = path,
            color = MegaManPalette.KAI_HIGHLIGHT.copy(alpha = alpha * 0.5f),
            style = Stroke(width = 6f)
        )
    }

    private fun initializeParticles() {
        particles.clear()

        for (i in 0 until PARTICLE_COUNT) {
            particles.add(
                ShieldParticle(
                    angle = (360f / PARTICLE_COUNT) * i,
                    distance = HEXAGON_RADIUS + Random.nextFloat() * 10f,
                    speed = 60f + Random.nextFloat() * 30f,
                    size = 2f + Random.nextFloat() * 2f
                )
            )
        }
    }

    /**
     * Get cooldown progress (0.0 = ready, 1.0 = just used)
     */
    fun getCooldownPercent(): Float {
        if (isActive) return 1f

        val timeSinceLastUse = System.currentTimeMillis() - lastActivationEnd
        val percent = (timeSinceLastUse / COOLDOWN_DURATION).coerceIn(0f, 1f)
        return 1f - percent  // Invert so 0 = ready, 1 = just used
    }

    /**
     * Check if shield is ready to use
     */
    fun isReady(): Boolean {
        return getCooldownPercent() == 0f
    }

    fun isShieldActive(): Boolean = isActive
}

/**
 * Individual particle orbiting the shield
 */
private data class ShieldParticle(
    var angle: Float,
    var distance: Float,
    val speed: Float,
    val size: Float
) {
    fun update(deltaTime: Float, baseRotation: Float) {
        // Orbit around shield
        angle += speed * deltaTime
        if (angle >= 360f) angle -= 360f
    }

    fun draw(scope: DrawScope, centerX: Float, centerY: Float, alpha: Float) {
        val radians = Math.toRadians(angle.toDouble())
        val x = centerX + (distance * cos(radians)).toFloat()
        val y = centerY + (distance * sin(radians)).toFloat()

        scope.drawCircle(
            color = MegaManPalette.KAI_TELEPORT.copy(alpha = alpha),
            radius = size,
            center = Offset(x, y)
        )

        // Particle glow
        scope.drawCircle(
            color = Color.White.copy(alpha = alpha * 0.5f),
            radius = size * 0.5f,
            center = Offset(x, y)
        )
    }
}
