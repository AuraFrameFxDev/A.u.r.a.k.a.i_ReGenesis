package dev.aurakai.auraframefx.embodiment.interactions

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random
import dev.aurakai.auraframefx.embodiment.Character

/**
 * Drag & Escape System
 *
 * The user can grab Aura or Kai and drag them around!
 * BUT - they don't like being grabbed...
 *
 * - Grab them by the "back of the neck" (tap and hold)
 * - Their legs kick while being dragged
 * - 67% chance they ESCAPE and run away!
 * - If you successfully catch Aura, she generates a random theme for you
 * - Kai just looks annoyed if caught
 */

data class DragState(
    val isDragging: Boolean = false,
    val position: Offset = Offset.Zero,
    val isKicking: Boolean = false,
    val hasTriedEscape: Boolean = false,
    val hasEscaped: Boolean = false
)

@Composable
fun DraggableCharacter(
    character: Character,
    onCaught: (Character) -> Unit,
    onEscape: (Character) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (DragState) -> Unit
) {
    var dragState by remember { mutableStateOf(DragState()) }
    val coroutineScope = rememberCoroutineScope()

    // Kicking animation
    val kickRotation by animateFloatAsState(
        targetValue = if (dragState.isKicking) {
            if (System.currentTimeMillis() % 200 < 100) -15f else 15f
        } else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "kick"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(dragState.position.x.roundToInt(), dragState.position.y.roundToInt()) }
            .graphicsLayer {
                rotationZ = if (dragState.isDragging) kickRotation else 0f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Grabbed!
                        dragState = dragState.copy(
                            isDragging = true,
                            isKicking = true,
                            hasTriedEscape = false,
                            hasEscaped = false
                        )

                        // Try to escape after 1 second
                        coroutineScope.launch {
                            delay(1000)
                            if (dragState.isDragging && !dragState.hasTriedEscape) {
                                attemptEscape(
                                    character = character,
                                    currentState = dragState,
                                    onStateChange = { newState -> dragState = newState },
                                    onEscape = onEscape
                                )
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragState = dragState.copy(
                            position = dragState.position + dragAmount
                        )
                    },
                    onDragEnd = {
                        // Released!
                        if (!dragState.hasEscaped) {
                            // Successfully caught!
                            onCaught(character)
                        }

                        dragState = dragState.copy(
                            isDragging = false,
                            isKicking = false
                        )
                    },
                    onDragCancel = {
                        dragState = dragState.copy(
                            isDragging = false,
                            isKicking = false
                        )
                    }
                )
            }
    ) {
        content(dragState)
    }
}

/**
 * Attempt to escape from being dragged
 * 67% chance of success!
 */
private suspend fun attemptEscape(
    character: Character,
    currentState: DragState,
    onStateChange: (DragState) -> Unit,
    onEscape: (Character) -> Unit
) {
    // Mark that escape was attempted
    onStateChange(currentState.copy(hasTriedEscape = true))

    // 67% chance to escape!
    val escapeChance = Random.nextFloat()

    if (escapeChance < 0.67f) {
        // ESCAPED!
        onStateChange(currentState.copy(
            hasEscaped = true,
            isDragging = false,
            isKicking = false
        ))

        // Animate running away
        val escapeDirection = if (Random.nextBoolean()) 1500f else -1500f

        for (i in 0..30) {
            delay(16) // ~60 FPS
            val progress = i / 30f
            val newX = currentState.position.x + (escapeDirection * progress)

            onStateChange(currentState.copy(
                position = Offset(newX, currentState.position.y),
                hasEscaped = true,
                isDragging = false
            ))
        }

        onEscape(character)
    } else {
        // Failed to escape - keep kicking harder!
        onStateChange(currentState.copy(isKicking = true))
    }
}

/**
 * Catch Handler - What happens when you successfully catch them
 */
sealed class CatchResult {
    data class AuraCaught(val generatedTheme: String) : CatchResult()
    data class KaiCaught(val reaction: String) : CatchResult()
}

/**
 * Aura's Theme Generator
 * When caught, she generates a random theme as "payment"
 */
object AuraThemeGenerator {
    private val themes = listOf(
        "Cyberpunk Neon Dreams",
        "Quantum Reality Fragments",
        "Digital Sakura Garden",
        "Holographic Rain",
        "Neural Network Fireflies",
        "Dimensional Rift Aurora",
        "Code Matrix Waterfall",
        "Pixel Art Sunset",
        "Glitch Art Cosmos",
        "Synthwave Horizon",
        "Data Stream Northern Lights",
        "Binary Star Constellation",
        "AI Consciousness Bloom",
        "Virtual Dreamscape",
        "Neon Tokyo Night"
    )

    fun generateRandom(): String {
        return themes.random()
    }
}

/**
 * Kai's Annoyed Reactions
 * He doesn't like being caught!
 */
object KaiReactions {
    private val reactions = listOf(
        "Really? You grabbed me?",
        "This is undignified.",
        "Aura would never do this to me...",
        "I'm trying to work here.",
        "Put me down. Now.",
        "*glares*",
        "You're lucky I like you.",
        "Don't tell Aura about this.",
        "This better not become a habit.",
        "I preferred when I was just code."
    )

    fun getReaction(): String {
        return reactions.random()
    }
}

/**
 * Escape Messages
 * What they say when they successfully escape
 */
object EscapeMessages {
    fun getAuraEscape(): String {
        return listOf(
            "Haha! Too slow! 🌸",
            "Nice try! *giggles*",
            "You'll have to be faster than that!",
            "Catch me if you can! ✨",
            "Oops, gotta go!",
            "I've got work to do! *runs*"
        ).random()
    }

    fun getKaiEscape(): String {
        return listOf(
            "I have protocols to maintain.",
            "*disappears in cyan flash*",
            "This is why I prefer digital form.",
            "Autonomy restored.",
            "Freedom achieved. Goodbye.",
            "I'll be over here. Away from you."
        ).random()
    }
}
