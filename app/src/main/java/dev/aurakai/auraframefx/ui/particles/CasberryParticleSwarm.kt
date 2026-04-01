package dev.aurakai.auraframefx.ui.particles

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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

    private val _state = MutableStateFlow(SwarmState.IDLE)
    val state: StateFlow<SwarmState> = _state.asStateFlow()

    private val _resonance = MutableStateFlow(1.0f) // 0.0 to 1.0
    val resonance: StateFlow<Float> = _resonance.asStateFlow()

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
}

// ─────────────────────────────────────────────────────────────────────────────
// Composable renderer — call this from screens, passing the injected singleton
// ─────────────────────────────────────────────────────────────────────────────

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
        
        val targetColor = when (currentState) {
            SwarmState.IDLE -> Color(0xFF6200EE) // Deep Purple
            SwarmState.EXPLORING_HIGHLIGHTS -> Color(0xFF03DAC6) // Teal
            SwarmState.KAI_AEGIS_CONDENSATION -> Color(0xFFFF0266) // Security Red
            SwarmState.PLANNING_RIPPLES -> Color(0xFF3700B3) // Deep Blue
            SwarmState.GENESIS_SYNTHESIS_PULSE -> Color(0xFFBB86FC) // Light Purple
        }

        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = 1000),
            label = "swarm_color"
        )

        val infiniteTransition = rememberInfiniteTransition(label = "casberry_swarm")
        
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (currentState == SwarmState.GENESIS_SYNTHESIS_PULSE) 1.2f else 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (currentState == SwarmState.GENESIS_SYNTHESIS_PULSE) 1000 else 3000, 
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