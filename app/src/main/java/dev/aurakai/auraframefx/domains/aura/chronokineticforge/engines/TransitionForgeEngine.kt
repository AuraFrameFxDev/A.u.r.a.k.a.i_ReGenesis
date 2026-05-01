package dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlinx.serialization.Serializable

/**
 * ⚡ TRANSITION FORGE ENGINE
 *
 * Unified engine consolidating 9 transition implementations into one sovereign system.
 * Powers: Home screen, app launch, QS expand, and lock screen transitions.
 *
 * SoulScript: "Motion binds the organism's reality."
 */

@Serializable
sealed class TransitionForgeEffect {
    abstract val duration: Int
    abstract val direction: TransitionDirection

    @Serializable
    data class Slide(
        override val duration: Int = 300,
        override val direction: TransitionDirection = TransitionDirection.LEFT_TO_RIGHT,
        val easing: TransitionEasing = TransitionEasing.EASE_OUT
    ) : TransitionForgeEffect()

    @Serializable
    data class Fade(
        override val duration: Int = 250,
        override val direction: TransitionDirection = TransitionDirection.NONE,
        val easing: TransitionEasing = TransitionEasing.EASE_IN_OUT
    ) : TransitionForgeEffect()

    @Serializable
    data class Zoom(
        override val duration: Int = 350,
        override val direction: TransitionDirection = TransitionDirection.CENTER_OUT,
        val scaleFrom: Float = 0.8f,
        val scaleTo: Float = 1.0f,
        val easing: TransitionEasing = TransitionEasing.SPRING
    ) : TransitionForgeEffect()

    @Serializable
    data class Cube3D(
        override val duration: Int = 400,
        override val direction: TransitionDirection = TransitionDirection.LEFT_TO_RIGHT
    ) : TransitionForgeEffect()

    @Serializable
    data class Glitch(
        override val duration: Int = 200,
        override val direction: TransitionDirection = TransitionDirection.NONE,
        val intensity: Float = 1.0f
    ) : TransitionForgeEffect()

    @Serializable
    data class Hologram(
        override val duration: Int = 500,
        override val direction: TransitionDirection = TransitionDirection.BOTTOM_TO_TOP,
        val scanlineEffect: Boolean = true
    ) : TransitionForgeEffect()

    @Serializable
    data class CRT(
        override val duration: Int = 400,
        override val direction: TransitionDirection = TransitionDirection.NONE,
        val curvature: Float = 0.1f
    ) : TransitionForgeEffect()

    @Serializable
    data class Spin(
        override val duration: Int = 450,
        override val direction: TransitionDirection = TransitionDirection.CLOCKWISE,
        val rotations: Float = 1.0f
    ) : TransitionForgeEffect()
}

enum class TransitionDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    CENTER_OUT,
    CENTER_IN,
    CLOCKWISE,
    COUNTER_CLOCKWISE,
    NONE
}

enum class TransitionEasing {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT,
    SPRING,
    BOUNCE
}

data class TransitionForgeConfig(
    val homeScreenEffect: TransitionForgeEffect = TransitionForgeEffect.Slide(),
    val appOpenEffect: TransitionForgeEffect = TransitionForgeEffect.Zoom(),
    val appCloseEffect: TransitionForgeEffect = TransitionForgeEffect.Slide(
        direction = TransitionDirection.BOTTOM_TO_TOP
    ),
    val qsExpandEffect: TransitionForgeEffect = TransitionForgeEffect.Hologram(),
    val lockScreenEffect: TransitionForgeEffect = TransitionForgeEffect.CRT(),
    val globalSpeedMultiplier: Float = 1.0f
)

object TransitionForgeEngine {

    @Composable
    fun <T> AnimatedForgeTransition(
        targetState: T,
        effect: TransitionForgeEffect,
        speedMultiplier: Float = 1.0f,
        content: @Composable (T) -> Unit
    ) {
        val adjustedDuration = (effect.duration / speedMultiplier).toInt()

        when (effect) {
            is TransitionForgeEffect.Slide -> SlideTransition(
                targetState = targetState,
                direction = effect.direction,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.Fade -> FadeTransition(
                targetState = targetState,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.Zoom -> ZoomTransition(
                targetState = targetState,
                scaleFrom = effect.scaleFrom,
                scaleTo = effect.scaleTo,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.Cube3D -> Cube3DTransition(
                targetState = targetState,
                direction = effect.direction,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.Glitch -> GlitchTransition(
                targetState = targetState,
                intensity = effect.intensity,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.Hologram -> HologramTransition(
                targetState = targetState,
                direction = effect.direction,
                scanlineEffect = effect.scanlineEffect,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.CRT -> CRTTransition(
                targetState = targetState,
                curvature = effect.curvature,
                duration = adjustedDuration,
                content = content
            )
            is TransitionForgeEffect.Spin -> SpinTransition(
                targetState = targetState,
                direction = effect.direction,
                rotations = effect.rotations,
                duration = adjustedDuration,
                content = content
            )
        }
    }

    @Composable
    private fun <T> SlideTransition(
        targetState: T,
        direction: TransitionDirection,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        val offsetX = when (direction) {
            TransitionDirection.LEFT_TO_RIGHT -> 300
            TransitionDirection.RIGHT_TO_LEFT -> -300
            else -> 0
        }
        val offsetY = when (direction) {
            TransitionDirection.TOP_TO_BOTTOM -> 300
            TransitionDirection.BOTTOM_TO_TOP -> -300
            else -> 0
        }

        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = duration)
                ) { offsetX } togetherWith
                slideOutHorizontally(
                    animationSpec = tween(durationMillis = duration)
                ) { -offsetX }
            },
            label = "slide"
        ) { state ->
            content(state)
        }
    }

    @Composable
    private fun <T> FadeTransition(
        targetState: T,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = duration)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = duration))
            },
            label = "fade"
        ) { state ->
            content(state)
        }
    }

    @Composable
    private fun <T> ZoomTransition(
        targetState: T,
        scaleFrom: Float,
        scaleTo: Float,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                scaleIn(
                    initialScale = scaleFrom,
                    animationSpec = tween(durationMillis = duration)
                ) togetherWith
                scaleOut(
                    targetScale = scaleFrom,
                    animationSpec = tween(durationMillis = duration)
                )
            },
            label = "zoom"
        ) { state ->
            Box(modifier = Modifier.scale(scaleTo)) {
                content(state)
            }
        }
    }

    @Composable
    private fun <T> Cube3DTransition(
        targetState: T,
        direction: TransitionDirection,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        val rotation by animateFloatAsState(
            targetValue = if (direction == TransitionDirection.LEFT_TO_RIGHT) 90f else -90f,
            animationSpec = tween(durationMillis = duration),
            label = "cube"
        )

        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
            },
            label = "cube"
        ) { state ->
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8f * density
                }
            ) {
                content(state)
            }
        }
    }

    @Composable
    private fun <T> GlitchTransition(
        targetState: T,
        intensity: Float,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        val glitchOffset by animateFloatAsState(
            targetValue = if (intensity > 0.5f) 10f else 0f,
            animationSpec = keyframes {
                durationMillis = duration
                0f at 0
                20f * intensity at duration / 4
                -15f * intensity at duration / 2
                10f * intensity at 3 * duration / 4
                0f at duration
            },
            label = "glitch"
        )

        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                fadeIn(tween(duration / 2)) togetherWith fadeOut(tween(duration / 2))
            },
            label = "glitch"
        ) { state ->
            Box(
                modifier = Modifier.graphicsLayer {
                    translationX = glitchOffset
                }
            ) {
                content(state)
            }
        }
    }

    @Composable
    private fun <T> HologramTransition(
        targetState: T,
        direction: TransitionDirection,
        scanlineEffect: Boolean,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        val alpha by animateFloatAsState(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = duration
                0f at 0
                0.3f at duration / 5
                0.6f at 2 * duration / 5
                0.4f at 3 * duration / 5
                0.8f at 4 * duration / 5
                1f at duration
            },
            label = "hologram"
        )

        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
            },
            label = "hologram"
        ) { state ->
            Box(
                modifier = Modifier.alpha(alpha)
            ) {
                content(state)
            }
        }
    }

    @Composable
    private fun <T> CRTTransition(
        targetState: T,
        curvature: Float,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        val scanlineProgress by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(durationMillis = duration, easing = EaseInOutQuad),
            label = "crt"
        )

        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
            },
            label = "crt"
        ) { state ->
            Box(
                modifier = Modifier.graphicsLayer {
                    // Simulate CRT turn-on effect with vertical reveal
                    clip = true
                }
            ) {
                content(state)
            }
        }
    }

    @Composable
    private fun <T> SpinTransition(
        targetState: T,
        direction: TransitionDirection,
        rotations: Float,
        duration: Int,
        content: @Composable (T) -> Unit
    ) {
        val rotation by animateFloatAsState(
            targetValue = if (direction == TransitionDirection.CLOCKWISE) 360f * rotations else -360f * rotations,
            animationSpec = tween(durationMillis = duration, easing = EaseOutCirc),
            label = "spin"
        )

        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
            },
            label = "spin"
        ) { state ->
            Box(
                modifier = Modifier.rotate(rotation)
            ) {
                content(state)
            }
        }
    }

    // ================= MODIFIER EXTENSIONS FOR DIRECT USE =================

    fun Modifier.forgeTransition(effect: TransitionForgeEffect, progress: Float): Modifier {
        return when (effect) {
            is TransitionForgeEffect.Fade -> this.alpha(progress)
            is TransitionForgeEffect.Zoom -> this.scale(0.8f + 0.2f * progress)
            is TransitionForgeEffect.Spin -> this.rotate(360f * progress)
            else -> this
        }
    }
}
