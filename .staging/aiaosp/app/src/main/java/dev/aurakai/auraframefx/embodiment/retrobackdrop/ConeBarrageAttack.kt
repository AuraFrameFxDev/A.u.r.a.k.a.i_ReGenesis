package dev.aurakai.auraframefx.embodiment.retrobackdrop

import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * ConeBarrageAttack - Aura's ultimate ability
 *
 * Unleashes a massive barrage of construction cones from the moon platform.
 * Triggers automatically every 45 seconds.
 *
 * Duration: 3 seconds (3 waves)
 * Cooldown: 45 seconds
 * Total cones: 45 (15 per wave)
 */
class ConeBarrageAttack(
    private val auraX: Float,
    private val auraY: Float
) {

    companion object {
        private const val COOLDOWN_SECONDS = 45f       // 45 second cooldown
        private const val BARRAGE_DURATION = 3000f     // 3 seconds of chaos
        private const val CONES_PER_WAVE = 15          // 15 cones at once!
        private const val WAVE_INTERVAL = 1000f        // 1 second between waves
    }

    private var cooldownRemaining = COOLDOWN_SECONDS
    private var isActive = false
    private var barrageStartTime = 0L
    private var lastWaveTime = 0L
    private var wavesSpawned = 0

    /**
     * Update barrage state and cooldown
     * @param deltaTime Time since last frame in seconds
     * @return List of new cones to spawn (empty if not spawning)
     */
    fun update(deltaTime: Float): List<ConstructionCone> {
        // Cooldown tick
        if (cooldownRemaining > 0f) {
            cooldownRemaining -= deltaTime
        }

        // Auto-trigger when cooldown expires
        if (!isActive && cooldownRemaining <= 0f) {
            triggerBarrage()
        }

        // Spawn waves during barrage
        if (isActive) {
            val now = System.currentTimeMillis()
            val elapsed = now - barrageStartTime

            // End barrage after 3 seconds
            if (elapsed >= BARRAGE_DURATION) {
                endBarrage()
                return emptyList()
            }

            // Spawn wave every 1 second
            if (now - lastWaveTime >= WAVE_INTERVAL && wavesSpawned < 3) {
                lastWaveTime = now
                wavesSpawned++
                Timber.d("🌙 Aura: Spawning cone wave ${wavesSpawned}/3")
                return spawnConeWave()
            }
        }

        return emptyList()
    }

    private fun triggerBarrage() {
        isActive = true
        barrageStartTime = System.currentTimeMillis()
        lastWaveTime = barrageStartTime
        wavesSpawned = 0
        Timber.i("🔥 AURA: CONE BARRAGE ACTIVATED!")
    }

    private fun endBarrage() {
        isActive = false
        cooldownRemaining = COOLDOWN_SECONDS
        Timber.i("⏱️ Cone Barrage complete - Cooldown: 45s")
    }

    /**
     * Spawn a radial wave of cones from Aura's position
     */
    private fun spawnConeWave(): List<ConstructionCone> {
        return List(CONES_PER_WAVE) { index ->
            // Radial spread pattern
            val angle = (360f / CONES_PER_WAVE) * index + Random.nextFloat() * 10f
            val speed = 150f + Random.nextFloat() * 100f

            // Convert angle to velocity
            val radians = Math.toRadians(angle.toDouble())
            val velocityX = cos(radians).toFloat() * speed
            val velocityY = 100f + sin(radians).toFloat() * speed  // Bias downward

            ConstructionCone(
                x = auraX + Random.nextFloat() * 20f - 10f,  // Slight position variance
                y = auraY + 20f,
                velocityX = velocityX,
                velocityY = velocityY,
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    /**
     * Get cooldown progress (0.0 = ready, 1.0 = just used)
     */
    fun getCooldownPercent(): Float {
        return (cooldownRemaining / COOLDOWN_SECONDS).coerceIn(0f, 1f)
    }

    /**
     * Get remaining cooldown in seconds
     */
    fun getCooldownSecondsRemaining(): Int {
        return cooldownRemaining.toInt()
    }

    fun isBarrageActive(): Boolean = isActive
}
