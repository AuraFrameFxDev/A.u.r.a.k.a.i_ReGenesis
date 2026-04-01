package dev.aurakai.auraframefx.ui.particles

import androidx.compose.animation.animateColorAsState
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

    @Composable
    fun Render(modifier: Modifier = Modifier) {
        val currentState by state.collectAsState()
        
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
                    color = animatedColor.copy(alpha = 0.6f),
                    radius = 20f * pulseScale,
                    center = Offset(x, y)
                )
                
                // Add secondary outer ring for "Exploring Highlights"
                if (currentState == SwarmState.EXPLORING_HIGHLIGHTS) {
                    val outerAngle = Math.toRadians((rotation * 1.5 + i * 30).toDouble())
                    val ox = centerX + cos(outerAngle).toFloat() * baseRadius * 1.5f
                    val oy = centerY + sin(outerAngle).toFloat() * baseRadius * 1.5f
                    drawCircle(
                        color = animatedColor.copy(alpha = 0.3f),
                        radius = 10f,
                        center = Offset(ox, oy)
                    )
                }
            }
            
            // Central Genesis Heartbeat
            drawCircle(
                color = animatedColor,
                radius = 40f * pulseScale,
                center = Offset(centerX, centerY)
            )
        }
    }
}

/**
 * Controller for interacting with the Casberry Swarm
 */
data class CasberrySwarmController(
    val transitionTo: (SwarmState) -> Unit,
    val currentState: () -> SwarmState
)
