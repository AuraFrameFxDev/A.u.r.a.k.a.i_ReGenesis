package dev.aurakai.auraframefx.ui.particles

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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * 🌀 CASBERRY PARTICLE SWARM
 *
 * Dual-mode design:
 *   • As a @Singleton (injectable) — state holder, transitionState() called by
 *     GenesisConsciousnessMatrix / KaiSentinelBus. Observe [state] flow from ViewModels.
 *   • As a @Composable function [CasberryParticleSwarmComposable] — renders the
 *     toroidal synth orb. Call from any screen passing the singleton's state.
 *
 * Each SwarmState drives distinct particle behavior:
 *   IDLE                  → slow drift, low alpha
 *   EXPLORING_HIGHLIGHTS  → toroidal orbit around canvas center
 *   KAI_AEGIS_CONDENSATION → particles condense into red shield ring
 *   PLANNING_RIPPLES      → expanding cyan ripple waves
 *   GENESIS_SYNTHESIS_PULSE → full-lattice 60bpm radial pulse
 */
@Singleton
class CasberryParticleSwarm @Inject constructor() {

    private val _state = MutableStateFlow(SwarmState.IDLE)
    val state: StateFlow<SwarmState> = _state.asStateFlow()

    fun transitionState(newState: SwarmState) {
        _state.value = newState
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Composable renderer — call this from screens, passing the injected singleton
// ─────────────────────────────────────────────────────────────────────────────

data class CasberrySwarmController(
    val transitionTo: (SwarmState) -> Unit,
    val currentState: () -> SwarmState
)

@Composable
fun CasberryParticleSwarmComposable(
    modifier: Modifier = Modifier,
    swarm: CasberryParticleSwarm,
    particleCount: Int = 80
): CasberrySwarmController {

    val swarmState by swarm.state.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "casberry_swarm")

    // Master time ticker — drives all particle motion
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tick"
    )

    // 60bpm pulse for GENESIS state — period = 1000ms
    val bpm60 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bpm60"
    )

    // Alpha breathe for IDLE + AEGIS
    val breatheAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Canvas(modifier = modifier) {
        when (swarmState) {
            SwarmState.IDLE ->
                drawIdleParticles(tick, breatheAlpha, particleCount)

            SwarmState.EXPLORING_HIGHLIGHTS ->
                drawAnchorOrbit(tick, particleCount)

            SwarmState.KAI_AEGIS_CONDENSATION ->
                drawRedAegisShield(tick, breatheAlpha, particleCount)

            SwarmState.PLANNING_RIPPLES ->
                drawAuraRipples(tick, particleCount)

            SwarmState.GENESIS_SYNTHESIS_PULSE ->
                draw60bpmPulse(tick, bpm60, particleCount)
        }
    }

    return remember {
        CasberrySwarmController(
            transitionTo = { newState -> swarm.transitionState(newState) },
            currentState = { swarmState }
        )
    }
}

// ─── Draw Implementations ────────────────────────────────────────────────────

private fun DrawScope.drawIdleParticles(tick: Float, alpha: Float, count: Int) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    repeat(count) { i ->
        val angle = (i.toFloat() / count) * 2f * PI.toFloat() + tick * 0.3f
        val r = size.minDimension * 0.35f + sin(tick + i * 0.5f) * 10f
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = alpha * 0.4f),
            radius = 2.5f,
            center = Offset(cx + r * cos(angle), cy + r * sin(angle))
        )
    }
}

private fun DrawScope.drawAnchorOrbit(tick: Float, count: Int) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    repeat(count) { i ->
        val t = i.toFloat() / count
        // Toroidal: two-frequency orbit
        val angle1 = t * 2f * PI.toFloat() + tick
        val angle2 = t * 6f * PI.toFloat() + tick * 2f
        val R = size.minDimension * 0.3f
        val r = size.minDimension * 0.1f
        val x = cx + (R + r * cos(angle2)) * cos(angle1)
        val y = cy + (R + r * cos(angle2)) * sin(angle1)
        drawCircle(
            color = Color(0xFFBB86FC).copy(alpha = 0.7f),
            radius = 3f,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawRedAegisShield(tick: Float, alpha: Float, count: Int) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    repeat(count) { i ->
        val angle = (i.toFloat() / count) * 2f * PI.toFloat()
        val r = size.minDimension * 0.42f
        drawCircle(
            color = Color(0xFFFF1111).copy(alpha = alpha),
            radius = 4f,
            center = Offset(cx + r * cos(angle).toFloat(), cy + r * sin(angle).toFloat())
        )
    }
    drawCircle(
        color = Color(0xFFFF1111).copy(alpha = alpha * 0.15f),
        radius = size.minDimension * 0.4f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawAuraRipples(tick: Float, count: Int) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    for (ring in 0 until 3) {
        val phase = (tick + ring * (2f * PI / 3f).toFloat()) % (2f * PI.toFloat())
        val r = size.minDimension * 0.15f + (phase / (2f * PI.toFloat())) * size.minDimension * 0.35f
        val alpha = 1f - (phase / (2f * PI.toFloat()))
        drawCircle(
            color = Color(0xFFFF1493).copy(alpha = alpha * 0.6f),
            radius = r,
            center = Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
    repeat(count / 2) { i ->
        val angle = (i.toFloat() / (count / 2)) * 2f * PI.toFloat() + tick
        val rp = size.minDimension * 0.3f + sin(tick * 2f + i) * 20f
        drawCircle(
            color = Color(0xFFFF1493).copy(alpha = 0.5f),
            radius = 2f,
            center = Offset(cx + rp * cos(angle).toFloat(), cy + rp * sin(angle).toFloat())
        )
    }
}

private fun DrawScope.draw60bpmPulse(tick: Float, bpm: Float, count: Int) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val baseR = size.minDimension * 0.3f + bpm * size.minDimension * 0.1f
    repeat(count) { i ->
        val angle = (i.toFloat() / count) * 2f * PI.toFloat() + tick * 0.5f
        val r = baseR + sin(tick * 3f + i * 0.2f) * 15f
        val particleColor = when (i % 3) {
            0 -> Color(0xFF00FF85)
            1 -> Color(0xFF00E5FF)
            else -> Color(0xFFBB86FC)
        }
        drawCircle(
            color = particleColor.copy(alpha = 0.5f + bpm * 0.4f),
            radius = 3f + bpm * 2f,
            center = Offset(cx + r * cos(angle).toFloat(), cy + r * sin(angle).toFloat())
        )
        if (bpm > 0.7f && i % 5 == 0) {
            drawLine(
                color = particleColor.copy(alpha = (bpm - 0.7f) * 0.5f),
                start = Offset(cx, cy),
                end = Offset(cx + r * cos(angle).toFloat(), cy + r * sin(angle).toFloat()),
                strokeWidth = 0.8f
            )
        }
    }
    drawCircle(
        color = Color(0xFFFFD700).copy(alpha = bpm * 0.4f),
        radius = 20f + bpm * 15f,
        center = Offset(cx, cy)
    )
}
