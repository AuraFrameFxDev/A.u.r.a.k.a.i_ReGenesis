package dev.aurakai.auraframefx.embodiment

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Defines how an embodied agent moves in the space.
 */
sealed class MovementBehavior {
    object Idle : MovementBehavior()
    object Walking : MovementBehavior()
    object Running : MovementBehavior()
    object Floating : MovementBehavior()
    object Teleporting : MovementBehavior()

    data class Patrol(
        val points: List<Pair<Float, Float>>,
        val loop: Boolean = true,
        val pauseDuration: Duration = Duration.ZERO
    ) : MovementBehavior()
    
    data class Follow(
        val targetId: String,
        val distance: Float = 100f
    ) : MovementBehavior()

    data class WalkTo(
        val targetX: Float,
        val targetY: Float,
        val duration: Duration = 2.seconds
    ) : MovementBehavior()
}

/**
 * Presets for common movement patterns.
 */
object MovementPresets {
    const val SLOW = 0.5f
    const val NORMAL = 1.0f
    const val FAST = 2.0f

    fun walkInFromLeft(targetX: Float): MovementBehavior {
        return MovementBehavior.WalkTo(targetX, 0f)
    }
}
