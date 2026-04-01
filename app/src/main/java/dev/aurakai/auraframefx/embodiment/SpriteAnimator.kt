package dev.aurakai.auraframefx.embodiment

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * 🎬 Sprite Animation System
 *
 * Cycles through sprite frames to create fluid character animations.
 * Supports walking, idle, and any multi-frame animations.
 */

/**
 * Animated sprite sequence
 */
data class SpriteAnimation(
    val frames: List<Painter>,
    val frameDuration: Duration = 100.milliseconds,
    val loop: Boolean = true
)

/**
 * Animation types that can be played
 */
sealed class EmbodimentAnimationState {
    data class Walking(val direction: WalkDirection) : EmbodimentAnimationState()
    object Idle : EmbodimentAnimationState()
    object Running : EmbodimentAnimationState()
    data class Custom(val name: String) : EmbodimentAnimationState()
}

enum class WalkDirection {
    LEFT, RIGHT, UP, DOWN
}

/**
 * 🎭 Sprite Animator Composable
 *
 * Usage:
 * ```
 * val walkFrames = remember { loadWalkingFrames() }
 * val currentFrame by animateSprite(
 *     animation = SpriteAnimation(walkFrames),
 *     playing = isWalking
 * )
 * Image(painter = currentFrame, ...)
 * ```
 */
@Composable
fun animateSprite(
    animation: SpriteAnimation,
    playing: Boolean = true,
    onAnimationComplete: () -> Unit = {}
): State<Painter> {
    val currentFrame = remember { mutableStateOf(animation.frames.first()) }
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(animation, playing) {
        if (!playing) return@LaunchedEffect

        while (isActive && (animation.loop || currentIndex < animation.frames.size)) {
            currentFrame.value = animation.frames[currentIndex % animation.frames.size]
            delay(animation.frameDuration)

            currentIndex++

            if (!animation.loop && currentIndex >= animation.frames.size) {
                onAnimationComplete()
                break
            }
        }
    }

    return currentFrame
}

/**
 * 🎨 Sprite Sheet Manager
 *
 * Manages all animation states for a character
 */
class SpriteSheetManager(
    val idleFrames: List<Painter>,
    val walkLeftFrames: List<Painter>,
    val walkRightFrames: List<Painter>,
    val runFrames: List<Painter> = walkRightFrames, // Default to walk if no run frames
) {
    fun getAnimation(state: EmbodimentAnimationState): SpriteAnimation {
        return when (state) {
            is EmbodimentAnimationState.Walking -> when (state.direction) {
                WalkDirection.LEFT -> SpriteAnimation(walkLeftFrames)
                WalkDirection.RIGHT -> SpriteAnimation(walkRightFrames)
                WalkDirection.UP -> SpriteAnimation(walkRightFrames) // Use right as default
                WalkDirection.DOWN -> SpriteAnimation(walkRightFrames)
            }
            is EmbodimentAnimationState.Idle -> SpriteAnimation(
                frames = idleFrames,
                frameDuration = 200.milliseconds // Slower for idle
            )
            is EmbodimentAnimationState.Running -> SpriteAnimation(
                frames = runFrames,
                frameDuration = 50.milliseconds // Faster for running
            )
            is EmbodimentAnimationState.Custom -> SpriteAnimation(idleFrames) // Fallback
        }
    }
}

/**
 * 🎬 Multi-State Animator
 *
 * Automatically switches between animation states
 */
@Composable
fun rememberMultiStateAnimator(
    spriteSheet: SpriteSheetManager,
    currentState: EmbodimentAnimationState
): Painter {
    val animation = remember(currentState) {
        spriteSheet.getAnimation(currentState)
    }

    val currentFrame by animateSprite(animation)
    return currentFrame
}

/**
 * 🔄 Simple frame cycler for when you just want to cycle frames
 */
@Composable
fun cycleSpriteFrames(
    frames: List<Painter>,
    frameDuration: Duration = 100.milliseconds
): Painter {
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(frames, frameDuration) {
        while (isActive) {
            delay(frameDuration)
            currentIndex = (currentIndex + 1) % frames.size
        }
    }

    return frames[currentIndex]
}
