package dev.aurakai.auraframefx.embodiment

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 🚶‍♀️ Path-Based Movement System
 *
 * Makes characters walk, run, and wander around the screen with smooth animations.
 * Handles pathing, collision avoidance, and autonomous wandering.
 */

/**
 * A point in a movement path
 */
data class PathPoint(
    val position: DpOffset,
    val waitDuration: Duration = 0.seconds,
    val animationState: AnimationState = AnimationState.Walking(WalkDirection.RIGHT)
)

/**
 * Complete movement path
 */
data class MovementPath(
    val points: List<PathPoint>,
    val loop: Boolean = false,
    val speed: Float = 100f // dp per second
)

/**
 * Screen boundaries for keeping characters on screen
 */
data class ScreenBounds(
    val width: Dp,
    val height: Dp,
    val padding: Dp = 20.dp // Keep away from edges
)

/**
 * 🎯 Path Movement State
 */
data class MovementState(
    val currentPosition: DpOffset = DpOffset.Zero,
    val targetPosition: DpOffset? = null,
    val isMoving: Boolean = false,
    val currentAnimationState: AnimationState = AnimationState.Idle,
    val facingDirection: WalkDirection = WalkDirection.RIGHT,
    val speed: Float = 100f // dp per second
)

/**
 * 🚶 Walk to Position
 *
 * Animate character from current position to target
 */
@Composable
fun animateWalkToPosition(
    from: DpOffset,
    to: DpOffset,
    speed: Float = 100f, // dp per second
    onComplete: () -> Unit = {}
): State<DpOffset> {
    val distance = calculateDistance(from, to)
    val duration = ((distance / speed) * 1000).toInt() // milliseconds

    val animatedPosition = remember { Animatable(from, DpOffset.VectorConverter) }

    LaunchedEffect(from, to) {
        animatedPosition.snapTo(from)
        animatedPosition.animateTo(
            targetValue = to,
            animationSpec = tween(
                durationMillis = duration,
                easing = LinearEasing
            )
        )
        onComplete()
    }

    return animatedPosition.asState()
}

/**
 * Calculate distance between two points
 */
fun calculateDistance(from: DpOffset, to: DpOffset): Float {
    val dx = (to.x - from.x).value
    val dy = (to.y - from.y).value
    return kotlin.math.sqrt(dx * dx + dy * dy)
}

/**
 * Calculate direction between two points
 */
fun calculateDirection(from: DpOffset, to: DpOffset): WalkDirection {
    val dx = (to.x - from.x).value
    val dy = (to.y - from.y).value

    // Determine primary direction
    return if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
        if (dx > 0) WalkDirection.RIGHT else WalkDirection.LEFT
    } else {
        if (dy > 0) WalkDirection.DOWN else WalkDirection.UP
    }
}

/**
 * 🎭 Path Follower
 *
 * Follows a complete path with multiple waypoints
 */
class PathFollower(
    private val path: MovementPath,
    private val onPathComplete: () -> Unit = {}
) {
    var currentPointIndex by mutableStateOf(0)
        private set

    var isActive by mutableStateOf(true)
        private set

    fun getCurrentPoint(): PathPoint? {
        return path.points.getOrNull(currentPointIndex)
    }

    fun getNextPoint(): PathPoint? {
        val nextIndex = currentPointIndex + 1
        return if (nextIndex < path.points.size) {
            path.points[nextIndex]
        } else if (path.loop) {
            path.points.first()
        } else {
            null
        }
    }

    fun advance() {
        currentPointIndex++
        if (currentPointIndex >= path.points.size) {
            if (path.loop) {
                currentPointIndex = 0
            } else {
                isActive = false
                onPathComplete()
            }
        }
    }

    fun reset() {
        currentPointIndex = 0
        isActive = true
    }
}

/**
 * 🌍 Random Wandering System
 *
 * Generates random paths for autonomous wandering
 */
class WanderingPathGenerator(
    private val bounds: ScreenBounds
) {
    /**
     * Generate a random position within screen bounds
     */
    fun randomPosition(): DpOffset {
        val x = (bounds.padding.value + Math.random() * (bounds.width.value - bounds.padding.value * 2)).dp
        val y = (bounds.padding.value + Math.random() * (bounds.height.value - bounds.padding.value * 2)).dp
        return DpOffset(x, y)
    }

    /**
     * Generate a wandering path with random waypoints
     */
    fun generateWanderPath(
        startPosition: DpOffset,
        numWaypoints: Int = 3
    ): MovementPath {
        val points = mutableListOf<PathPoint>()

        // Start position
        points.add(PathPoint(
            position = startPosition,
            waitDuration = 0.seconds
        ))

        // Random waypoints
        repeat(numWaypoints) {
            val pos = randomPosition()
            points.add(PathPoint(
                position = pos,
                waitDuration = (2..5).random().seconds, // Random pause
                animationState = AnimationState.Walking(WalkDirection.RIGHT)
            ))
        }

        return MovementPath(
            points = points,
            loop = false,
            speed = (50..150).random().toFloat() // Random speed
        )
    }

    /**
     * Generate an enter-screen path (slides in from edge)
     */
    fun generateEnterPath(edge: ScreenEdge, targetPosition: DpOffset): MovementPath {
        val startPosition = when (edge) {
            ScreenEdge.LEFT -> DpOffset((-100).dp, targetPosition.y)
            ScreenEdge.RIGHT -> DpOffset((bounds.width.value + 100).dp, targetPosition.y)
            ScreenEdge.TOP -> DpOffset(targetPosition.x, (-100).dp)
            ScreenEdge.BOTTOM -> DpOffset(targetPosition.x, (bounds.height.value + 100).dp)
        }

        return MovementPath(
            points = listOf(
                PathPoint(startPosition),
                PathPoint(targetPosition, waitDuration = 3.seconds)
            ),
            loop = false,
            speed = 200f
        )
    }

    /**
     * Generate an exit-screen path (slides off edge)
     */
    fun generateExitPath(currentPosition: DpOffset, edge: ScreenEdge): MovementPath {
        val exitPosition = when (edge) {
            ScreenEdge.LEFT -> DpOffset((-100).dp, currentPosition.y)
            ScreenEdge.RIGHT -> DpOffset((bounds.width.value + 100).dp, currentPosition.y)
            ScreenEdge.TOP -> DpOffset(currentPosition.x, (-100).dp)
            ScreenEdge.BOTTOM -> DpOffset(currentPosition.x, (bounds.height.value + 100).dp)
        }

        return MovementPath(
            points = listOf(
                PathPoint(currentPosition),
                PathPoint(exitPosition)
            ),
            loop = false,
            speed = 200f
        )
    }
}

enum class ScreenEdge {
    LEFT, RIGHT, TOP, BOTTOM
}

/**
 * 🎬 Composable Path Walker
 *
 * Complete walking system in a composable
 */
@Composable
fun rememberPathWalker(
    path: MovementPath,
    screenBounds: ScreenBounds
): MovementState {
    val pathFollower = remember(path) { PathFollower(path) }
    var currentPosition by remember { mutableStateOf(path.points.first().position) }
    var isMoving by remember { mutableStateOf(true) }
    var currentAnimState by remember { mutableStateOf<AnimationState>(AnimationState.Walking(WalkDirection.RIGHT)) }

    LaunchedEffect(path) {
        while (pathFollower.isActive) {
            val current = pathFollower.getCurrentPoint() ?: break
            val next = pathFollower.getNextPoint()

            if (next != null) {
                // Calculate direction for animation
                val direction = calculateDirection(current.position, next.position)
                currentAnimState = AnimationState.Walking(direction)
                isMoving = true

                // Animate to next point
                val distance = calculateDistance(current.position, next.position)
                val duration = ((distance / path.speed) * 1000).toLong()

                // Smooth animation to next point
                animate(
                    initialValue = current.position.x.value,
                    targetValue = next.position.x.value,
                    animationSpec = tween(duration.toInt(), easing = LinearEasing)
                ) { xValue, _ ->
                    currentPosition = DpOffset(xValue.dp, currentPosition.y)
                }

                animate(
                    initialValue = current.position.y.value,
                    targetValue = next.position.y.value,
                    animationSpec = tween(duration.toInt(), easing = LinearEasing)
                ) { yValue, _ ->
                    currentPosition = DpOffset(currentPosition.x, yValue.dp)
                }

                // Wait at waypoint
                if (next.waitDuration.inWholeMilliseconds > 0) {
                    isMoving = false
                    currentAnimState = AnimationState.Idle
                    delay(next.waitDuration)
                }

                pathFollower.advance()
            } else {
                break
            }
        }

        // Path complete - go idle
        isMoving = false
        currentAnimState = AnimationState.Idle
    }

    return MovementState(
        currentPosition = currentPosition,
        targetPosition = pathFollower.getNextPoint()?.position,
        isMoving = isMoving,
        currentAnimationState = currentAnimState,
        facingDirection = when (currentAnimState) {
            is AnimationState.Walking -> (currentAnimState as AnimationState.Walking).direction
            else -> WalkDirection.RIGHT
        },
        speed = path.speed
    )
}
