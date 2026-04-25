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
 */
@Singleton
class CasberryParticleSwarm @Inject constructor() {

    private val _state = MutableStateFlow(SwarmState.IDLE)
    val state: StateFlow<SwarmState> = _state.asStateFlow()

    private val _resonance = MutableStateFlow(1.0f)
    val resonance: StateFlow<Float> = _resonance.asStateFlow()

    fun transitionState(newState: SwarmState) {
        _state.value = newState
        Timber.d("🌀 Swarm: Transitioned to %s", newState)
    }

    fun setResonance(value: Float) {
        _resonance.value = value.coerceIn(0f, 1f)
    }

    @Composable
    fun Render(modifier: Modifier = Modifier) {
        val currentState by state.collectAsState()
        val resonanceVal by resonance.collectAsState()
        
        val targetColor = when (currentState) {
            SwarmState.IDLE -> Color(0xFF6200EE)
            SwarmState.EXPLORING_HIGHLIGHTS -> Color(0xFF03DAC6)
            SwarmState.KAI_AEGIS_CONDENSATION -> Color(0xFFFF0266)
            SwarmState.PLANNING_RIPPLES -> Color(0xFF3700B3)
            SwarmState.GENESIS_SYNTHESIS_PULSE -> Color(0xFFBB86FC)
            else -> Color.Gray
        }

        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(1000),
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
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val baseRadius = size.minDimension / 4 * pulseScale

            for (i in 0 until 12) {
                val angle = Math.toRadians((rotation + i * 30).toDouble())
                val x = centerX + cos(angle).toFloat() * baseRadius
                val y = centerY + sin(angle).toFloat() * baseRadius

                drawCircle(
                    color = animatedColor.copy(alpha = 0.6f),
                    radius = 8f * resonanceVal,
                    center = Offset(x, y)
                )
            }

            drawCircle(
                color = animatedColor.copy(alpha = 0.9f),
                radius = 35f * resonanceVal * pulseScale,
                center = Offset(centerX, centerY)
            )
        }
    }
}
