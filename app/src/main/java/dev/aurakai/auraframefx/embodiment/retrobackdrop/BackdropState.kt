package dev.aurakai.auraframefx.embodiment.retrobackdrop

/**
 * BackdropState - Lifecycle states for ROM Tools backdrop
 *
 * Manages the transition from static card image to live animation and back.
 *
 * State Flow:
 * STATIC → EXPLODING → ACTIVE → COMPLETING → VICTORY → STATIC
 */
enum class BackdropState {
    /**
     * STATIC - Idle state, showing static screenshot
     *
     * The backdrop appears as a frozen "agent artifact card" showing the
     * chutes & ladders stage. No animation, no CPU usage.
     *
     * Triggered when: No operation running
     * Visual: Static screenshot image
     */
    STATIC,

    /**
     * EXPLODING - Transition: Static → Animated
     *
     * The card "comes to life" with pixel explosion effect.
     * Image fragments into colored pixels which reorganize into characters.
     *
     * Duration: 1 second
     * Triggered when: ROM operation starts
     * Visual: Pixel explosion, swirl, reorganization
     */
    EXPLODING,

    /**
     * ACTIVE - Full animation running
     *
     * The backdrop is fully animated at 60 FPS with:
     * - Aura throwing construction cones
     * - Kai climbing ladders (progress-synced)
     * - Cone Barrage attacks (45s cooldown)
     * - Hexagonal Shield defenses (23s cooldown)
     * - Teleportation effects
     *
     * Triggered when: Operation is running
     * Visual: Full game loop active
     */
    ACTIVE,

    /**
     * COMPLETING - Operation finishing
     *
     * Brief state when operation reaches 100% but before victory pose.
     * Characters freeze mid-action.
     *
     * Duration: 0.5 seconds
     * Triggered when: Progress reaches 100%
     * Visual: Freeze frame
     */
    COMPLETING,

    /**
     * VICTORY - Success state
     *
     * Shows victory pose with characters celebrating.
     * Optional: Can reverse pixel effect back to static.
     *
     * Duration: 2 seconds (then return to STATIC)
     * Triggered when: Operation complete
     * Visual: Victory animations, then fade
     */
    VICTORY
}

/**
 * State transition helper
 */
object BackdropStateManager {

    /**
     * Get next state based on operation status
     */
    fun getNextState(
        currentState: BackdropState,
        operationRunning: Boolean,
        operationProgress: Float
    ): BackdropState {
        return when (currentState) {
            BackdropState.STATIC -> {
                if (operationRunning) BackdropState.EXPLODING
                else BackdropState.STATIC
            }

            BackdropState.EXPLODING -> {
                // Explosion complete (handled by CardExplosionEffect)
                BackdropState.ACTIVE
            }

            BackdropState.ACTIVE -> {
                when {
                    !operationRunning -> BackdropState.COMPLETING
                    operationProgress >= 100f -> BackdropState.COMPLETING
                    else -> BackdropState.ACTIVE
                }
            }

            BackdropState.COMPLETING -> {
                BackdropState.VICTORY
            }

            BackdropState.VICTORY -> {
                // After 2 seconds, return to static
                BackdropState.STATIC
            }
        }
    }
}
