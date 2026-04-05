package dev.aurakai.auraframefx.ui.particles

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.ui.particles.SwarmState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🌀 CASBERRY PARTICLE SWARM
 * Native implementation of the toroidal synth orb.
 * Reacts instantly to agent states via [SwarmState].
 */
@Singleton
class CasberryParticleSwarm @Inject constructor() {

    private val _state = MutableStateFlow(IDLE)
    val state: StateFlow<SwarmState> = _state.asStateFlow()

    private val _resonance = MutableStateFlow(1.0f) // 0.0 to 1.0
    val resonance: StateFlow<Float> = _resonance.asStateFlow()

    /**
     * Triggers a momentary resonance surge in the swarm.
     */
    fun triggerResonance(event: String, intensity: Float, colorVector: Pair<Color, Color>? = null) {
        Timber.d("🌀 Swarm Resonance: %s (Intensity: %f)", event, intensity)
        setResonance(intensity)
        // In a real implementation, this might trigger a specific animation state or color shift
    }

    /**
     * Updates the swarm's current state to the provided value and records the transition.
     */
    fun transitionState(newState: SwarmState) {
        _state.value = newState
        Timber.d("🌀 Casberry Particle Swarm: Transitioned to %s", newState)
    }

    /**
     * Updates the swarm's resonance level, clamping the input to the range 0.0 through 1.0.
     */
    fun setResonance(value: Float) {
        _resonance.value = value.coerceIn(0f, 1f)
    }

    /**
     * ─────────────────────────────────────────────────────────────────────────────
     * Composable renderer — call this from screens, passing the injected singleton
     * ─────────────────────────────────────────────────────────────────────────────
     */
    @Composable
    fun Render(modifier: Modifier = Modifier) {
        val currentState by state.collectAsState()
        val resonanceVal by resonance.collectAsState()

        val targetColor = when (currentState) {
            IDLE -> Color(0xFF6200EE) // Deep Purple
            EXPLORING_HIGHLIGHTS -> Color(0xFF03DAC6) // Teal
            KAI_AEGIS_CONDENSATION -> Color(0xFFFF0266) // Security Red
            PLANNING_RIPPLES -> Color(0xFF3700B3) // Deep Blue
            GENESIS_SYNTHESIS_PULSE -> Color(0xFFBB86FC) // Light Purple
        }

        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = 1000),
            label = "swarm_color"
        )

        val infiniteTransition = rememberInfiniteTransition(label = "casberry_swarm")

        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (currentState == GENESIS_SYNTHESIS_PULSE) 1.2f else 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (currentState == GENESIS_SYNTHESIS_PULSE) 1000 else 3000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        val time by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "time"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val baseRadius = size.minDimension / 4 * pulseScale

            // Draw core Toroidal Synth Orb representation
            for (i in 0 until 12) {
                val angle = Math.toRadians((rotation + i * 30).toDouble())
                val x = centerX + cos(angle).toFloat() * baseRadius
                val y = centerY + sin(angle).toFloat() * baseRadius

                drawCircle(
                    color = animatedColor.copy(alpha = 0.6f * pulseScale),
                    radius = (4f + sin(time + i) * 2f) * resonanceVal,
                    center = Offset(x, y)
                )

                // Render specific "Active Ripple" for synthesis
                if (currentState == GENESIS_SYNTHESIS_PULSE && i % 4 == 0) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = (8f + sin(time * 2) * 5f) * pulseScale,
                        center = Offset(x, y)
                    )
                }
            }

            // Central Relational Core
            drawCircle(
                color = animatedColor.copy(alpha = 0.9f),
                radius = 35f * resonanceVal * pulseScale,
                center = Offset(centerX, centerY)
            )

            // Add Inner Glow
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = 20f * pulseScale,
                center = Offset(centerX, centerY)
            )
        }
    }
}
