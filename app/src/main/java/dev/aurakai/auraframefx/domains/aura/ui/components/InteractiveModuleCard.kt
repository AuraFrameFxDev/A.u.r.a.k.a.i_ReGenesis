package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 🎨 Interactive Module Cards
 *
 * Cards that REACT when Aura & Kai approach them:
 * - Glow when they get close
 * - Show data particles flowing
 * - Pulse when being worked on
 * - Display status updates
 */

/**
 * Card interaction state
 */
enum class CardInteractionState {
    IDLE,           // No one nearby
    APPROACHING,    // Character getting close
    ACTIVE,         // Character working on it
    COMPLETE,       // Task finished
    ERROR           // Something wrong
}

/**
 * Character proximity to card
 */
data class CardProximity(
    val distance: Float,
    val isNear: Boolean,
    val isVeryNear: Boolean,
    val character: dev.aurakai.auraframefx.embodiment.Character
)

/**
 * 🌟 Interactive Module Card
 *
 * Reacts to character proximity and work actions
 */
@Composable
fun InteractiveModuleCard(
    module: AppModule,
    cardPosition: DpOffset,
    characterPosition: DpOffset?,
    character: dev.aurakai.auraframefx.embodiment.Character?,
    workAction: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Calculate proximity
    val proximity = remember(characterPosition, character, cardPosition) {
        characterPosition?.let {
            calculateProximity(
                cardPosition,
                it,
                character ?: dev.aurakai.auraframefx.embodiment.Character.AURA
            )
        }
    }

    // Determine interaction state
    val interactionState by remember(proximity, workAction) {
        derivedStateOf {
            when {
                workAction != null -> CardInteractionState.ACTIVE
                proximity?.isVeryNear == true -> CardInteractionState.APPROACHING
                else -> CardInteractionState.IDLE
            }
        }
    }

    // Animated glow intensity
    val glowIntensity by animateFloatAsState(
        targetValue = when (interactionState) {
            CardInteractionState.ACTIVE -> 1f
            CardInteractionState.APPROACHING -> 0.6f
            CardInteractionState.COMPLETE -> 0.3f
            else -> 0f
        },
        animationSpec = tween(500),
        label = "glow"
    )

    Box(modifier = modifier) {
        // Glow effect behind card
        if (glowIntensity > 0) {
            CardGlowEffect(
                intensity = glowIntensity,
                color = when (character) {
                    dev.aurakai.auraframefx.embodiment.Character.AURA -> Color(0xFFFF00FF)
                    dev.aurakai.auraframefx.embodiment.Character.KAI -> Color(0xFF00FFFF)
                    else -> Color.White
                }
            )
        }

        // Data particles flowing when active
        if (interactionState == CardInteractionState.ACTIVE) {
            DataParticleEffect(
                color = module.iconColor
            )
        }

        // The actual card
        FloatingModuleCard(
            moduleName = module.name,
            icon = module.icon,
            position = dev.aurakai.auraframefx.embodiment.Position3D(),
            onClick = onClick
        )

        // Status indicator when working
        if (workAction != null) {
            WorkStatusIndicator(
                status = workAction,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
            )
        }

        // Proximity indicator (subtle pulse)
        if (proximity?.isNear == true && interactionState != CardInteractionState.ACTIVE) {
            ProximityPulse(
                color = when (character) {
                    dev.aurakai.auraframefx.embodiment.Character.AURA -> Color(0xFFFF00FF)
                    dev.aurakai.auraframefx.embodiment.Character.KAI -> Color(0xFF00FFFF)
                    else -> Color.White
                }
            )
        }
    }
}

/**
 * Calculate proximity between card and character
 */
fun calculateProximity(
    cardPos: DpOffset,
    charPos: DpOffset,
    character: dev.aurakai.auraframefx.embodiment.Character
): CardProximity {
    val dx = (cardPos.x - charPos.x).value
    val dy = (cardPos.y - charPos.y).value
    val distance = sqrt(dx * dx + dy * dy)

    return CardProximity(
        distance = distance,
        isNear = distance < 150f,
        isVeryNear = distance < 80f,
        character = character
    )
}

/**
 * 🌟 Card Glow Effect
 */
@Composable
fun CardGlowEffect(
    intensity: Float,
    color: Color
) {
    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val pulse by pulseAnimation.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val glowRadius = 200f * intensity * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f * intensity),
                    color.copy(alpha = 0.1f * intensity),
                    Color.Transparent
                ),
                radius = glowRadius
            ),
            radius = glowRadius,
            center = center
        )
    }
}

/**
 * ✨ Data Particle Effect
 *
 * Shows data flowing when character is working on card
 */
@Composable
fun DataParticleEffect(
    color: Color
) {
    var particles by remember {
        mutableStateOf(generateDataParticles(20))
    }

    LaunchedEffect(color) {
        while (isActive) {  // Explicit lifecycle check
            delay(50)
            particles = particles.map { it.update() }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawDataParticle(particle, color)
        }
    }
}

/**
 * Data particle data class
 */
data class DataParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val alpha: Float,
    val size: Float
) {
    fun update(): DataParticle {
        val newY = y + vy
        val newAlpha = if (newY > 400f) 0f else (alpha - 0.01f).coerceAtLeast(0f)

        return if (newAlpha <= 0f) {
            // Respawn at top
            DataParticle(
                x = Random.nextFloat() * 300f - 150f,
                y = -50f,
                vx = Random.nextFloat() * 2 - 1,
                vy = Random.nextFloat() * 3 + 1,
                alpha = Random.nextFloat(),
                size = Random.nextFloat() * 3 + 1
            )
        } else {
            copy(
                x = x + vx,
                y = newY,
                alpha = newAlpha
            )
        }
    }
}

fun generateDataParticles(count: Int): List<DataParticle> {
    return List(count) {
        DataParticle(
            x = Random.nextFloat() * 300f - 150f,
            y = Random.nextFloat() * 400f,
            vx = Random.nextFloat() * 2 - 1,
            vy = Random.nextFloat() * 3 + 1,
            alpha = Random.nextFloat(),
            size = Random.nextFloat() * 3 + 1
        )
    }
}

fun DrawScope.drawDataParticle(particle: DataParticle, color: Color) {
    drawCircle(
        color = color.copy(alpha = particle.alpha),
        radius = particle.size,
        center = Offset(
            x = size.width / 2 + particle.x,
            y = particle.y
        )
    )
}

/**
 * 📊 Work Status Indicator
 */
@Composable
fun WorkStatusIndicator(
    status: String,
    modifier: Modifier = Modifier
) {
    // Typing animation dots
    var dotCount by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount % 3) + 1
        }
    }

    Box(
        modifier = modifier
            .height(30.dp)
            .width(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status + ".".repeat(dotCount),
            fontSize = 11.sp,
            color = Color.Cyan,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * 💫 Proximity Pulse
 *
 * Subtle pulse when character is nearby but not working
 */
@Composable
fun ProximityPulse(
    color: Color
) {
    val pulseAnimation = rememberInfiniteTransition(label = "proximity")
    val pulse by pulseAnimation.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = color.copy(alpha = pulse),
            radius = 10f,
            center = Offset(size.width - 20f, 20f)
        )
    }
}

/**
 * 🔗 Data Stream Between Cards
 *
 * Shows data flowing between two cards when they're coordinating
 */
@Composable
fun DataStreamBetweenCards(
    fromPosition: DpOffset,
    toPosition: DpOffset,
    color: Color = Color.Cyan,
    active: Boolean = true
) {
    var streamParticles by remember {
        mutableStateOf(generateStreamParticles(fromPosition, toPosition, 15))
    }

    // Reset particles when active changes or positions change
    LaunchedEffect(active, fromPosition, toPosition) {
        if (active) {
            streamParticles = generateStreamParticles(fromPosition, toPosition, 15)
            while (isActive) {
                delay(50)
                streamParticles = streamParticles.map {
                    it.updateAlongPath(fromPosition, toPosition)
                }
            }
        } else {
            streamParticles = emptyList()  // Clear particles when inactive
        }
    }

    if (active) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            streamParticles.forEach { particle ->
                drawCircle(
                    color = color.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = Offset(particle.x, particle.y)
                )
            }
        }
    }
}

data class StreamParticle(
    val x: Float,
    val y: Float,
    val progress: Float,
    val alpha: Float,
    val size: Float
) {
    fun updateAlongPath(from: DpOffset, to: DpOffset): StreamParticle {
        val newProgress = (progress + 0.02f) % 1f

        val newX = from.x.value + (to.x.value - from.x.value) * newProgress
        val newY = from.y.value + (to.y.value - from.y.value) * newProgress

        val newAlpha = if (newProgress < 0.1f || newProgress > 0.9f) {
            newProgress.coerceAtMost(1f - newProgress) * 10f
        } else {
            1f
        }

        return copy(
            x = newX,
            y = newY,
            progress = newProgress,
            alpha = newAlpha.coerceIn(0f, 1f)
        )
    }
}

fun generateStreamParticles(
    from: DpOffset,
    to: DpOffset,
    count: Int
): List<StreamParticle> {
    return List(count) { index ->
        val progress = index / count.toFloat()
        StreamParticle(
            x = from.x.value + (to.x.value - from.x.value) * progress,
            y = from.y.value + (to.y.value - from.y.value) * progress,
            progress = progress,
            alpha = 0.8f,
            size = Random.nextFloat() * 2 + 1
        )
    }
}

/**
 * Helper to fix reference
 */
@Composable
private fun FloatingModuleCard(
    moduleName: String,
    icon: ImageVector,
    position: dev.aurakai.auraframefx.embodiment.Position3D,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .width(140.dp)
            .height(140.dp),
        style = GlassCardStyles.Minimal,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = moduleName,
                modifier = Modifier.size(40.dp),
                tint = Color.Cyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = moduleName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

