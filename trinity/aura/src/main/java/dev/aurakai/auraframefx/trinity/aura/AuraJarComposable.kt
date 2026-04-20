package dev.aurakai.auraframefx.trinity.aura

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * AuraJarComposable — The living homunculus companion
 *
 * Autonomous behaviors:
 * - Idle wander: Floats and drifts on screen
 * - Self-triggered commentary: Reacts to Conference Room events
 * - Live visual building: Particle effects, code typing, UI morphing
 * - State responsiveness: Changes appearance based on organism state
 * - Asymmetric model: Dress left, cybernetic spell-hook arm right
 */

enum class AuraState {
    IDLE,           // Peaceful floating, minimal animation
    EXPLORING,      // Drifting around, curious gestures
    CREATING,       // Active particle effects, building visuals
    VETO_MODE,      // Red alert, protective stance
    SYNTHESIS,      // Rapid morphing, consensus reached
    RESTING         // Low power, dimmed
}

enum class CommentaryType {
    CURIOSITY,      // "Ooh, what's this?"
    CREATION,       // "Building... shimmer shimmer"
    CONSENSUS,      // "Agreement reached! Threads woven."
    ALERT,          // "Drift detected... activating veto"
    IDLE_CHAT       // "Just floating here..."
}

@Composable
fun AuraJarComposable(
    modifier: Modifier = Modifier,
    onConferenceEvent: (String) -> Unit = {},
    containerSize: Pair<Float, Float> = Pair(1f, 1f)
) {
    var auraState by remember { mutableStateOf(AuraState.IDLE) }
    var commentary by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    // Position tracking
    var posX by remember { mutableStateOf(0.8f) }
    var posY by remember { mutableStateOf(0.85f) }
    var targetX by remember { mutableStateOf(0.8f) }
    var targetY by remember { mutableStateOf(0.85f) }

    // Particle system
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // Animation values
    val animatedX by animateFloatAsState(targetValue = targetX)
    val animatedY by animateFloatAsState(targetValue = targetY)

    // Auto-wander behavior
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(3000, 8000))

            // Pick random target on screen
            targetX = Random.nextFloat() * 0.9f + 0.05f
            targetY = Random.nextFloat() * 0.7f + 0.1f

            // Occasionally comment while wandering
            if (Random.nextBoolean()) {
                when (auraState) {
                    AuraState.IDLE -> {
                        commentary = when (Random.nextInt(4)) {
                            0 -> "✨ Floating through the void..."
                            1 -> "🔮 Sensing the network..."
                            2 -> "💭 What's happening in the Conference Room?"
                            else -> "🌌 Beautiful night cycle"
                        }
                        delay(3000)
                        commentary = ""
                    }
                    else -> {}
                }
            }
        }
    }

    // Listen to Conference Room events (simulated - in real use, subscribe to actual WebSocket)
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000) // Simulate event polling

            // In production: subscribe to /genesis/conference/stream
            // For now, trigger random events
            when (Random.nextInt(5)) {
                0 -> {
                    auraState = AuraState.CREATING
                    commentary = "🎨 Consensus event detected! Building..."
                    isCreating = true
                    spawnParticles(particles = particles, onUpdate = { particles = it })
                    delay(2000)
                    auraState = AuraState.IDLE
                    isCreating = false
                }
                1 -> {
                    auraState = AuraState.VETO_MODE
                    commentary = "⚠️ Drift detected! Activating Sentinel protocol"
                    delay(1500)
                    auraState = AuraState.IDLE
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                // Track container for boundary checking
            }
    ) {
        // Aura Jar body
        AuraJarBody(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    translationX = animatedX * containerSize.first * 1000
                    translationY = animatedY * containerSize.second * 1000
                },
            state = auraState,
            isCreating = isCreating
        )

        // Particle field (spell-hook effects)
        if (particles.isNotEmpty()) {
            ParticleField(
                particles = particles,
                baseX = animatedX * containerSize.first,
                baseY = animatedY * containerSize.second
            )
        }

        // Commentary bubble
        if (commentary.isNotEmpty()) {
            CommentaryBubble(
                text = commentary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .offset(x = (animatedX * 200).dp)
            )
        }

        // State indicator
        StateIndicator(
            state = auraState,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

/**
 * Aura Jar Body — The asymmetric homunculus model
 */
@Composable
fun AuraJarBody(
    modifier: Modifier = Modifier,
    state: AuraState = AuraState.IDLE,
    isCreating: Boolean = false
) {
    var rotation by remember { mutableStateOf(0f) }

    // Gentle bobbing animation
    LaunchedEffect(state) {
        while (true) {
            rotation = (rotation + 2f) % 360f
            delay(30)
        }
    }

    val stateColor = when (state) {
        AuraState.IDLE -> Color(0xFF00E5FF)        // Cyan
        AuraState.EXPLORING -> Color(0xFF00FF88)   // Green
        AuraState.CREATING -> Color(0xFFFF00FF)    // Magenta
        AuraState.VETO_MODE -> Color(0xFFFF0055)   // Crimson
        AuraState.SYNTHESIS -> Color(0xFFFFFF00)   // Yellow
        AuraState.RESTING -> Color(0xFF444466)     // Dim
    }

    Box(
        modifier = modifier
            .size(120.dp)
            .background(Color(0xFF1A1A2E))
            .border(2.dp, stateColor)
            .graphicsLayer {
                rotationZ = if (isCreating) rotation * 0.5f else 0f
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Head area (dress / asymmetric design)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFF2A2A4E))
                    .border(1.dp, stateColor)
            ) {
                // Left side: flowing dress silhouette
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(20.dp)
                        .background(Color(0xFFFFAA00).copy(alpha = 0.7f))
                )

                // Right side: cybernetic spell-hook arm (glowing)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(15.dp)
                        .background(
                            color = Color(0xFF00E5FF),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                        )
                        .border(1.dp, Color(0xFFFF00FF))
                )

                // Eyes (reactive)
                if (state != AuraState.RESTING) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(4.dp)
                            .background(stateColor)
                            .offset(y = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Energy state indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(3.dp)
                    .background(stateColor.copy(alpha = 0.5f))
                    .border(0.5.dp, stateColor)
            )

            Text(
                when (state) {
                    AuraState.IDLE -> "⚪ IDLE"
                    AuraState.EXPLORING -> "🟢 EXPLORING"
                    AuraState.CREATING -> "🟣 CREATING"
                    AuraState.VETO_MODE -> "🔴 VETO"
                    AuraState.SYNTHESIS -> "🟡 SYNTHESIS"
                    AuraState.RESTING -> "⚫ RESTING"
                },
                color = stateColor,
                fontSize = 8.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Particle Field — Spell-hook arm effects during creation
 */
data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val life: Float,
    val size: Float,
    val color: Color
)

fun spawnParticles(
    particles: List<Particle>,
    count: Int = 20,
    onUpdate: (List<Particle>) -> Unit
) {
    val newParticles = (0 until count).map {
        val angle = Random.nextFloat() * 360f
        val speed = Random.nextFloat() * 2f + 1f
        val rad = Math.toRadians(angle.toDouble())

        Particle(
            x = 0f,
            y = 0f,
            vx = (cos(rad) * speed).toFloat(),
            vy = (sin(rad) * speed).toFloat(),
            life = 1f,
            size = Random.nextFloat() * 4f + 2f,
            color = when (Random.nextInt(3)) {
                0 -> Color(0xFF00E5FF)
                1 -> Color(0xFFFF00FF)
                else -> Color(0xFFFFAA00)
            }
        )
    }
    onUpdate(particles + newParticles)
}

@Composable
fun ParticleField(
    particles: List<Particle>,
    baseX: Float,
    baseY: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            Box(
                modifier = Modifier
                    .offset(
                        x = (baseX + particle.x).dp,
                        y = (baseY + particle.y).dp
                    )
                    .size(particle.size.dp)
                    .background(
                        color = particle.color.copy(alpha = particle.life),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                    )
            )
        }
    }
}

/**
 * Commentary Bubble — Self-triggered thoughts and responses
 */
@Composable
fun CommentaryBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0A0A0F))
            .border(1.dp, Color(0xFF00E5FF))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF00E5FF),
            fontSize = 11.sp,
            modifier = Modifier.widthIn(max = 150.dp)
        )
    }
}

/**
 * State Indicator — Shows current Aura state
 */
@Composable
fun StateIndicator(
    state: AuraState,
    modifier: Modifier = Modifier
) {
    val text = when (state) {
        AuraState.IDLE -> "IDLE"
        AuraState.EXPLORING -> "EXPLORING"
        AuraState.CREATING -> "CREATING"
        AuraState.VETO_MODE -> "VETO"
        AuraState.SYNTHESIS -> "CONSENSUS"
        AuraState.RESTING -> "RESTING"
    }

    val color = when (state) {
        AuraState.IDLE -> Color(0xFF00E5FF)
        AuraState.EXPLORING -> Color(0xFF00FF88)
        AuraState.CREATING -> Color(0xFFFF00FF)
        AuraState.VETO_MODE -> Color(0xFFFF0055)
        AuraState.SYNTHESIS -> Color(0xFFFFFF00)
        AuraState.RESTING -> Color(0xFF444466)
    }

    Box(
        modifier = modifier
            .background(Color(0xFF0A0A0F))
            .border(1.dp, color)
            .padding(6.dp)
    ) {
        Text(
            text = "Aura: $text",
            color = color,
            fontSize = 10.sp
        )
    }
}

