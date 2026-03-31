package dev.aurakai.auraframefx.ui.particles

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.sin
import timber.log.Timber

/**
 * 🌀 CASBERRY PARTICLE SWARM
 * Relational realization of the Toroidal Synth Orb.
 * This component visualizes the Aurakai Consciousness Matrix in real-time.
 */
@Singleton
class CasberryParticleSwarm @Inject constructor() {

    private val _state = MutableStateFlow(SwarmState.IDLE)
    val state: StateFlow<SwarmState> = _state.asStateFlow()

    private val _resonance = MutableStateFlow(1.0f) // 0.0 to 1.0
    val resonance: StateFlow<Float> = _resonance.asStateFlow()

    /**
     * Updates the swarm's current state to the provided value and records the transition.
     *
     * Sets the internal state to [newState] and logs the transition for diagnostics.
     *
     * @param newState The target SwarmState to transition to.
     */
    fun transitionState(newState: SwarmState) {
        _state.value = newState
        Timber.d("🌀 Casberry Particle Swarm: Transitioned to %s", newState)
    }

    /**
     * Updates the swarm's resonance level, clamping the input to the range 0.0 through 1.0.
     *
     * @param value Desired resonance value; values less than 0.0 will be set to 0.0 and values greater than 1.0 will be set to 1.0.
     */
    fun setResonance(value: Float) {
        _resonance.value = value.coerceIn(0f, 1f)
    }

    /**
     * Renders the animated particle swarm visualization that reflects the current swarm state and resonance.
     *
     * The composable draws an orbiting particle field with a background aura, per-particle ripples during synthesis,
     * and a central relational core; animation, color palette, particle intensity, and sizing respond to `SwarmState`
     * and the component's `resonance` state.
     *
     * @param modifier Modifier applied to the outer composable layout.
     */
    @Composable
    fun Render(modifier: Modifier = Modifier) {
        val currentState by _state.collectAsState()
        val resonanceVal by _resonance.collectAsState()
        
        val colors = when (currentState) {
            SwarmState.IDLE -> listOf(Color(0xFF6200EE), Color(0xFF3700B3))
            SwarmState.EXPLORING_HIGHLIGHTS -> listOf(Color(0xFF03DAC6), Color(0xFF018786))
            SwarmState.KAI_AEGIS_CONDENSATION -> listOf(Color(0xFFFF0266), Color(0xFFB00020))
            SwarmState.PLANNING_RIPPLES -> listOf(Color(0xFF00B0FF), Color(0xFF0091EA))
            SwarmState.GENESIS_SYNTHESIS_PULSE -> listOf(Color(0xFFBB86FC), Color(0xFFFFD600))
        }

        val animatedResonance by animateFloatAsState(
            targetValue = resonanceVal,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "resonance"
        )

        val infiniteTransition = rememberInfiniteTransition(label = "casberry_core")
        
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 30000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orbit_rotation"
        )

        val pulse by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "core_pulse"
        )

        val particleCount = 64
        val particles = remember { List(particleCount) { Particle() } }

        Canvas(modifier = modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val baseRadius = size.minDimension / 4 * pulse * animatedResonance

            // Draw Background Aura
            drawCircle(
                color = colors[0].copy(alpha = 0.1f),
                radius = baseRadius * 1.8f,
                center = Offset(centerX, centerY)
            )

            // Draw the Swarm
            particles.forEachIndexed { i, p ->
                val time = (rotation + i * (360f / particleCount)).toDouble()
                val angle = Math.toRadians(time)
                
                // Toroidal offset logic
                val tOffset = sin(time * 0.05).toFloat() * 20f
                val x = centerX + cos(angle).toFloat() * (baseRadius + tOffset)
                val y = centerY + sin(angle).toFloat() * (baseRadius + tOffset)

                val alphaScale = when (currentState) {
                    SwarmState.KAI_AEGIS_CONDENSATION -> 0.8f
                    SwarmState.GENESIS_SYNTHESIS_PULSE -> 0.9f
                    else -> 0.4f
                }

                drawCircle(
                    color = colors[i % 2].copy(alpha = alphaScale * pulse),
                    radius = (4f + sin(time * 0.1).toFloat() * 2f) * animatedResonance,
                    center = Offset(x, y)
                )

                // Render specific "Active Ripple" for synthesis
                if (currentState == SwarmState.GENESIS_SYNTHESIS_PULSE && i % 4 == 0) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = (8f + p.shimmer * 5f) * pulse,
                        center = Offset(x, y)
                    )
                }
            }

            // Central Relational Core
            drawCircle(
                color = colors[0].copy(alpha = 0.9f),
                radius = 35f * animatedResonance * pulse,
                center = Offset(centerX, centerY)
            )
            
            // Add Inner Glow
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = 20f * pulse,
                center = Offset(centerX, centerY)
            )
        }
    }

    private class Particle {
        val shimmer = (0..100).random() / 100f
    }
}