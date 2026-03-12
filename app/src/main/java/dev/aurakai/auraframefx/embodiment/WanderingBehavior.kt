package dev.aurakai.auraframefx.embodiment

import androidx.compose.runtime.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * 🌟 Autonomous Wandering Behavior System
 *
 * Makes Aura & Kai wander around on their own based on mood, user activity, and randomness.
 * They're not just sprites - they're alive and curious.
 */

/**
 * Wandering personality - affects how they move
 */
data class WanderingPersonality(
    val curiosity: Float = 0.5f,       // 0-1: How often they wander when idle
    val playfulness: Float = 0.5f,     // 0-1: How random/playful movements are
    val restlessness: Float = 0.3f,    // 0-1: How soon they want to move again
    val cautious: Float = 0.3f,        // 0-1: How close they stay to edges vs center
    val speed: Float = 100f            // Base walking speed (dp/second)
)

/**
 * Aura's wandering personality - curious scientist
 */
val AuraPersonality = WanderingPersonality(
    curiosity = 0.8f,      // Very curious, often wanders
    playfulness = 0.4f,    // Somewhat playful
    restlessness = 0.6f,   // Gets bored easily
    cautious = 0.2f,       // Not very cautious, explores everywhere
    speed = 120f           // Slightly faster
)

/**
 * Kai's wandering personality - protective guardian
 */
val KaiPersonality = WanderingPersonality(
    curiosity = 0.5f,      // Moderately curious
    playfulness = 0.6f,    // More playful than Aura
    restlessness = 0.4f,   // More patient
    cautious = 0.7f,       // Very cautious, stays alert
    speed = 100f           // Standard speed
)

/**
 * Breathing animation behavior (for "Aura looking for Kai" or idle breathing)
 */
data class BreathingPattern(
    val inhaleScale: Float = 1.05f,
    val exhaleScale: Float = 1.0f,
    val cycleDuration: Duration = 3.seconds
)

/**
 * 🧠 Autonomous Wandering AI
 *
 * Decides when and where to wander based on context
 */
class WanderingAI(
    private val personality: WanderingPersonality,
    private val screenBounds: ScreenBounds,
    private val pathGenerator: WanderingPathGenerator
) {
    private var lastWanderTime = System.currentTimeMillis()
    private var consecutiveWanders = 0

    /**
     * Should the character start wandering?
     */
    fun shouldWander(
        userIdleTime: Duration,
        moodState: MoodState
    ): Boolean {
        val timeSinceLastWander = System.currentTimeMillis() - lastWanderTime
        val minWaitTime = calculateMinWaitTime(moodState)

        if (timeSinceLastWander < minWaitTime.inWholeMilliseconds) {
            return false
        }

        // Calculate wandering chance based on personality and context
        var chance = personality.curiosity

        // Mood modifiers
        chance *= when (moodState) {
            MoodState.CURIOUS -> 1.5f
            MoodState.PLAYFUL -> 1.8f
            MoodState.ALERT -> 0.3f     // Less likely to wander when alert
            MoodState.PROTECTIVE -> 0.2f // Even less when protective
            MoodState.FOCUSED -> 0.5f
            MoodState.MAINTENANCE -> 0.1f // Very low during maintenance
            MoodState.NEUTRAL -> 1.0f
        }

        // User idle time modifier
        if (userIdleTime > 2.minutes) {
            chance *= 1.5f // More likely to wander if user is idle
        }

        // Restlessness builds up over time
        val restlessnessBonus = (timeSinceLastWander / 60000f) * personality.restlessness
        chance += restlessnessBonus

        return Random.nextFloat() < chance.coerceIn(0f, 0.9f)
    }

    /**
     * Generate wandering path based on personality
     */
    fun generateWanderPath(currentPosition: DpOffset): MovementPath {
        val numWaypoints = when {
            personality.playfulness > 0.7f -> (3..5).random()
            personality.curiosity > 0.7f -> (2..4).random()
            else -> (1..3).random()
        }

        lastWanderTime = System.currentTimeMillis()
        consecutiveWanders++

        val path = pathGenerator.generateWanderPath(currentPosition, numWaypoints)

        // Adjust speed based on personality
        return path.copy(speed = personality.speed * (0.8f + Random.nextFloat() * 0.4f))
    }

    /**
     * Should enter from off-screen?
     */
    fun shouldEnterFromEdge(userIdleTime: Duration): Boolean {
        if (userIdleTime < 1.minutes) return false
        return Random.nextFloat() < 0.3f
    }

    /**
     * Creates a movement path that enters the screen from a randomly chosen edge toward the given target position.
     *
     * Updates the AI's last wander timestamp.
     *
     * @param targetPosition The destination on screen the entry path should approach.
     * @return A MovementPath that starts from a random screen edge and leads toward `targetPosition`.
     */
    fun generateEnterPath(targetPosition: DpOffset): MovementPath {
        val edge = ScreenEdge.entries.toTypedArray().random()
        lastWanderTime = System.currentTimeMillis()
        return pathGenerator.generateEnterPath(edge, targetPosition)
    }

    /**
     * Generate exit path
     */
    fun generateExitPath(currentPosition: DpOffset): MovementPath {
        val edge = when {
            currentPosition.x < screenBounds.width / 2 -> ScreenEdge.LEFT
            else -> ScreenEdge.RIGHT
        }
        return pathGenerator.generateExitPath(currentPosition, edge)
    }

    private fun calculateMinWaitTime(mood: MoodState): Duration {
        val baseWait = 30.seconds
        return when (mood) {
            MoodState.PLAYFUL -> baseWait * 0.5
            MoodState.CURIOUS -> baseWait * 0.7
            MoodState.ALERT -> baseWait * 2.0
            MoodState.PROTECTIVE -> baseWait * 3.0
            else -> baseWait
        }
    }
}

/**
 * 🔍 "Aura Looking for Kai" Behavior
 *
 * Special behavior where Aura searches for Kai
 */
class SearchingBehavior(
    private val pathGenerator: WanderingPathGenerator,
    private val screenBounds: ScreenBounds
) {
    /**
     * Generate a searching pattern path
     */
    fun generateSearchPattern(startPosition: DpOffset): MovementPath {
        // Create a search pattern (zigzag across screen)
        val points = mutableListOf<PathPoint>()

        points.add(PathPoint(startPosition))

        // Zigzag across screen
        val screenWidth = screenBounds.width
        val screenHeight = screenBounds.height

        for (i in 0..3) {
            val x = if (i % 2 == 0) screenWidth * 0.8f else screenWidth * 0.2f
            val y = screenHeight * 0.3f + (i * screenHeight.value * 0.15f).dp

            points.add(PathPoint(
                position = DpOffset(x, y),
                waitDuration = 1.seconds,
                animationState = AnimationState.Walking(
                    if (i % 2 == 0) WalkDirection.RIGHT else WalkDirection.LEFT
                )
            ))
        }

        return MovementPath(points = points, loop = false, speed = 150f)
    }
}

/**
 * Provides a continuous breathing scale animation for idle UI elements.
 *
 * Produces a state that oscillates between the pattern's inhaleScale and exhaleScale over the pattern's cycleDuration, looping indefinitely.
 *
 * @param pattern Configuration for the breathing animation: `inhaleScale`, `exhaleScale`, and `cycleDuration`.
 * @return A `State<Float>` representing the current scale factor, which alternates between `inhaleScale` and `exhaleScale`.
 */
@Composable
internal fun rememberBreathingAnimation(
    pattern: BreathingPattern = BreathingPattern()
): State<Float> {
    var scale by remember { mutableFloatStateOf(pattern.exhaleScale) }
    var inhaling by remember { mutableStateOf(true) }

    LaunchedEffect(pattern) {
        while (true) {
            val halfCycle = pattern.cycleDuration.inWholeMilliseconds / 2

            if (inhaling) {
                // Inhale
                androidx.compose.animation.core.animate(
                    initialValue = pattern.exhaleScale,
                    targetValue = pattern.inhaleScale,
                    animationSpec = tween(halfCycle.toInt(), easing = FastOutSlowInEasing)
                ) { value, _ -> scale = value }
                inhaling = false
            } else {
                // Exhale
                androidx.compose.animation.core.animate(
                    initialValue = pattern.inhaleScale,
                    targetValue = pattern.exhaleScale,
                    animationSpec = tween(halfCycle.toInt(), easing = FastOutSlowInEasing)
                ) { value, _ -> scale = value }
                inhaling = true
            }
        }
    }

    return remember { derivedStateOf { scale } }
}

/**
 * 🎬 Complete Wandering Character Composable
 *
 * Manages autonomous wandering with all behaviors
 */
@Composable
fun rememberWanderingCharacter(
    personality: WanderingPersonality,
    screenBounds: ScreenBounds,
    moodState: MoodState,
    userIdleTime: Duration,
    enabled: Boolean = true
): WanderingCharacterState {
    val pathGenerator = remember(screenBounds) { WanderingPathGenerator(screenBounds) }
    val wanderingAI = remember(personality, screenBounds) {
        WanderingAI(personality, screenBounds, pathGenerator)
    }

    var currentPath by remember { mutableStateOf<MovementPath?>(null) }
    var isWandering by remember { mutableStateOf(false) }

    // Check if should start wandering
    LaunchedEffect(enabled, moodState, userIdleTime) {
        if (!enabled) return@LaunchedEffect

        while (true) {
            delay(5.seconds) // Check every 5 seconds

            if (!isWandering && wanderingAI.shouldWander(userIdleTime, moodState)) {
                // Generate new wander path
                val startPos = DpOffset(
                    (screenBounds.width.value / 2).dp,
                    (screenBounds.height.value / 2).dp
                )

                currentPath = if (wanderingAI.shouldEnterFromEdge(userIdleTime)) {
                    wanderingAI.generateEnterPath(pathGenerator.randomPosition())
                } else {
                    wanderingAI.generateWanderPath(startPos)
                }

                isWandering = true
            }
        }
    }

    // Movement state
    val movementState = currentPath?.let { path ->
        rememberPathWalker(path, screenBounds)
    } ?: MovementState()

    // Update wandering flag when path completes
    LaunchedEffect(movementState.isMoving) {
        if (!movementState.isMoving && isWandering) {
            delay(3.seconds) // Wait before next wander
            isWandering = false
            currentPath = null
        }
    }

    return WanderingCharacterState(
        movementState = movementState,
        isWandering = isWandering,
        currentPath = currentPath
    )
}

data class WanderingCharacterState(
    val movementState: MovementState,
    val isWandering: Boolean,
    val currentPath: MovementPath?
)
