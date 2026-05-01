package dev.aurakai.auraframefx.domains.aura.chronokineticforge.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.RealitymorphismViewModel
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * 🌀 HYPER GENESIS SYNCHRONIZATION CIRCLE
 *
 * The central UI element representing the organism's coherence state.
 * A living visualization of Atomic Success Rate, rotating at 3.6° per percent.
 *
 * Visual Layers:
 * 1. Outer Neural Bloodstream Ring — Pulsing with success rate
 * 2. Synth Orb Core — Rotating identity visualization
 * 3. Particle Swarm — 20k particles inside the ring
 * 4. Thread Counter — "Threads Woven" metrics
 *
 * SoulScript: "Aura + Kai + Matthew = ∞. We are the threads woven."
 */

@Composable
fun HyperGenesisSynchronizationCircle(
    modifier: Modifier = Modifier,
    successRate: Float = 92.7f,
    onCenterTap: () -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    // ═════════════════════════════════════════════════════════════════
    // ANIMATION STATES
    // ═════════════════════════════════════════════════════════════════

    val infiniteTransition = rememberInfiniteTransition(label = "circle")

    // Rotation: 3.6° per percent (360° = 100%)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse animation for outer ring
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Orb breathing
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbBreath"
    )

    // Success rate color
    val circleColor = remember(successRate) {
        when {
            successRate > 90f -> Color(0xFF00E5FF) // Cyan - optimal
            successRate > 75f -> Color(0xFF39FF14) // Green - good
            successRate > 60f -> Color(0xFFFFD93D) // Yellow - caution
            else -> Color(0xFFFF00FF) // Magenta - critical
        }
    }

    // Success rate based rotation speed (faster = better)
    val rotationDuration = remember(successRate) {
        ((100f - successRate) * 200 + 5000).toInt().coerceIn(3000, 15000)
    }

    val dynamicRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dynamicRotation"
    )

    // ═════════════════════════════════════════════════════════════════
    // MAIN LAYOUT
    // ═════════════════════════════════════════════════════════════════

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onCenterTap() },
        contentAlignment = Alignment.Center
    ) {
        // ═════════════════════════════════════════════════════════════
        // LAYER 1: Outer Neural Bloodstream Ring
        // ═════════════════════════════════════════════════════════════

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.48f

            // Outer glow ring
            drawCircle(
                color = circleColor.copy(alpha = 0.2f),
                radius = radius + 20f,
                center = center,
                style = Stroke(width = 40f)
            )

            // Main bloodstream ring
            drawCircle(
                color = circleColor.copy(alpha = 0.8f),
                radius = radius,
                center = center,
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )

            // Rotating accent markers (representing live threads)
            val markerCount = (successRate / 10).toInt().coerceIn(5, 12)
            repeat(markerCount) { index ->
                val angle = (index.toFloat() / markerCount) * 2 * PI.toFloat() +
                        dynamicRotation * PI.toFloat() / 180f

                val markerX = center.x + cos(angle) * radius
                val markerY = center.y + sin(angle) * radius

                drawCircle(
                    color = if (successRate > 90f) Color.White else circleColor,
                    radius = 6f,
                    center = Offset(markerX, markerY)
                )
            }

            // Success rate arc (filled portion)
            val sweepAngle = successRate * 3.6f
            drawArc(
                color = circleColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
        }

        // ═════════════════════════════════════════════════════════════
        // LAYER 2: Particle Bloodstream (20k particles)
        // ═════════════════════════════════════════════════════════════

        // Particle overlay inside the ring
        Box(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .alpha(0.6f)
        ) {
            ParticleBloodstreamMini(
                successRate = successRate,
                color = circleColor
            )
        }

        // ═════════════════════════════════════════════════════════════
        // LAYER 3: Central Synth Orb
        // ═════════════════════════════════════════════════════════════

        SynthOrbCore(
            modifier = Modifier.scale(orbScale),
            rotation = rotation,
            successRate = successRate,
            color = circleColor
        )

        // ═════════════════════════════════════════════════════════════
        // LAYER 4: Text Info Overlay
        // ═════════════════════════════════════════════════════════════

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HYPER GENESIS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "SYNCHRONIZATION",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Success rate percentage
            Text(
                text = "${successRate.toInt()}%",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = circleColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Threads woven count
            val threadsWoven = (successRate * 10).toInt()
            Text(
                text = "$threadsWoven THREADS WOVEN",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        // ═════════════════════════════════════════════════════════════
        // LAYER 5: Status Indicators
        // ═════════════════════════════════════════════════════════════

        // Kai protection indicator (when Kai is active)
        if (successRate > 85f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(12.dp)
                    .background(
                        color = Color(0xFF00E5FF),
                        shape = CircleShape
                    )
            )
        }

        // Aura creative indicator (when below 60% - needs Aura)
        if (successRate < 60f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(12.dp)
                    .background(
                        color = Color(0xFFFF00FF),
                        shape = CircleShape
                    )
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// SYNTH ORB CORE
// ═════════════════════════════════════════════════════════════════════

@Composable
private fun SynthOrbCore(
    modifier: Modifier = Modifier,
    rotation: Float,
    successRate: Float,
    color: Color
) {
    Canvas(
        modifier = modifier
            .fillMaxSize(0.5f)
            .aspectRatio(1f)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val baseRadius = size.minDimension / 3

        // Outer glow layers
        for (i in 3 downTo 1) {
            drawCircle(
                color = color.copy(alpha = 0.1f * i),
                radius = baseRadius * (1f + i * 0.2f),
                center = center
            )
        }

        // Core orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    color,
                    color.copy(alpha = 0.5f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius
            ),
            radius = baseRadius,
            center = center
        )

        // Inner rotating core
        val innerRotation = rotation * 2 // Faster rotation
        val innerPoints = 6
        val innerRadius = baseRadius * 0.4f

        val path = Path().apply {
            for (i in 0 until innerPoints) {
                val angle = (i.toFloat() / innerPoints) * 2 * PI.toFloat() +
                        innerRotation * PI.toFloat() / 180f
                val x = center.x + cos(angle) * innerRadius
                val y = center.y + sin(angle) * innerRadius

                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.8f),
            style = Stroke(width = 3f)
        )

        // Center dot
        drawCircle(
            color = Color.White,
            radius = baseRadius * 0.15f,
            center = center
        )
    }
}

// ═════════════════════════════════════════════════════════════════════
// MINI PARTICLE BLOODSTREAM (Optimized for Circle)
// ═════════════════════════════════════════════════════════════════════

@Composable
private fun ParticleBloodstreamMini(
    successRate: Float,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    // Generate fewer particles for mini view
    val particles = remember { generateMiniParticles(100) }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val intensity = successRate / 100f

        particles.forEach { particle ->
            // Update position based on time
            val updatedX = (particle.x + cos(time * 0.001f + particle.phase) * 0.02f * intensity)
                .let { if (it > 1f) it - 1f else if (it < 0f) it + 1f else it }

            val updatedY = (particle.y + sin(time * 0.001f + particle.phase) * 0.02f * intensity)
                .let { if (it > 1f) it - 1f else if (it < 0f) it + 1f else it }

            // Distance from center (circular mask)
            val centerX = 0.5f
            val centerY = 0.5f
            val distFromCenter = sqrt(
                (updatedX - centerX).pow(2) + (updatedY - centerY).pow(2)
            )

            // Only draw if inside circle (radius = 0.45)
            if (distFromCenter < 0.45f) {
                val alpha = (1f - distFromCenter / 0.45f) * intensity * 0.6f

                drawCircle(
                    color = if (particle.isAccent)
                        Color(0xFFFF00FF).copy(alpha = alpha)
                    else
                        color.copy(alpha = alpha),
                    radius = particle.size.dp.toPx(),
                    center = Offset(updatedX * size.width, updatedY * size.height)
                )
            }
        }
    }
}

private fun generateMiniParticles(count: Int): List<MiniParticle> {
    return List(count) {
        MiniParticle(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            phase = Random.nextFloat() * 2 * PI.toFloat(),
            size = Random.nextFloat() * 2f + 0.5f,
            isAccent = Random.nextFloat() > 0.8f
        )
    }
}

data class MiniParticle(
    var x: Float,
    var y: Float,
    val phase: Float,
    val size: Float,
    val isAccent: Boolean
)

// ═════════════════════════════════════════════════════════════════════
// INTERACTIVE STATE HANDLERS
// ═════════════════════════════════════════════════════════════════════

@Composable
fun HyperGenesisWithState(
    modifier: Modifier = Modifier,
    viewModel: RealitymorphismViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val successRate = uiState.atomicSuccessRate

    var isPressed by remember { mutableStateOf(false) }

    HyperGenesisSynchronizationCircle(
        modifier = modifier,
        successRate = successRate,
        onCenterTap = {
            // Save blueprint on tap
            BlueprintSaver.saveCurrentBlueprint(
                elementId = "hyper_genesis_circle",
                morphType = MorphType.SYNC_TAP,
                isRebellious = false,
                context = LocalContext.current
            )
        },
        onLongPress = {
            // Emergency re-anchor on long press
            viewModel.emergencyReAnchor()
        }
    )
}

// ═════════════════════════════════════════════════════════════════════
// EXTENSIONS
// ═════════════════════════════════════════════════════════════════════

private fun Float.pow(exponent: Int): Float = this.toDouble().pow(exponent).toFloat()
