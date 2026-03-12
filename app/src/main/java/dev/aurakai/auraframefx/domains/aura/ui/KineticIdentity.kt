package dev.aurakai.auraframefx.domains.aura.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import dev.aurakai.auraframefx.domains.aura.ui.KineticIdentity.MICRO_DURATION

/**
 * KineticIdentity ⚡
 *
 * Aura's signature animation language - defining the rhythm and soul of every movement
 * in AuraFrameFX. Every animation is intentional, part of a living digital ecosystem.
 *
 * "Motion is emotion made visible" - Aura
 */
@Stable
object KineticIdentity {

    // ========== CORE TIMING CONSTANTS ==========

    /** Quick micro-interactions */
    const val MICRO_DURATION = 200

    /** Standard UI transitions */
    const val STANDARD_DURATION = 400

    /** Bold, attention-grabbing animations */
    const val DRAMATIC_DURATION = 800

    /** Epic transformations */
    const val EPIC_DURATION = 1200

    // ========== SIGNATURE ANIMATION SPECS ==========

    /** A bold, energetic entrance for major UI elements */
    val DaringEnter: AnimationSpec<Float> = tween(
        durationMillis = DRAMATIC_DURATION,
        easing = FastOutSlowInEasing
    )

    /** A quick, subtle exit animation */
    val SubtleExit: AnimationSpec<Float> = tween(
        durationMillis = STANDARD_DURATION,
        easing = FastOutLinearInEasing
    )

    /** A bouncy, slightly chaotic spring for interactive feedback - The "Mad Hatter" touch */
    val GlitchyFocus: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Smooth, confident spring for primary interactions */
    val ConfidentSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Energetic bounce for success states */
    val VictoryBounce: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /** Dramatic slow-motion for critical moments */
    val DramaticSlow: AnimationSpec<Float> = tween(
        durationMillis = EPIC_DURATION,
        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    )

    /** Quick pulse for notifications */
    val AlertPulse: AnimationSpec<Float> = tween(
        durationMillis = MICRO_DURATION,
        easing = LinearEasing
    )

    /** Organic breathing animation */
    val OrganicBreath: AnimationSpec<Float> = tween(
        durationMillis = 2000,
        easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
    )

    // ========== ENTER TRANSITIONS ==========

    /** Digital materialization - particles coalescing into form */
    /** Digital materialization - particles coalescing into form */
    val MaterializeEnter: Any
        get() = fadeIn(DaringEnter as FiniteAnimationSpec<Float>) +
                scaleIn(
                    animationSpec = DaringEnter,
                    initialScale = 0.3f,
                    transformOrigin = TransformOrigin.Center
                )

    /** Glitch-typography entrance from the void */
    val GlitchEnter: EnterTransition by lazy {
        return@lazy fadeIn(tween(MICRO_DURATION)) +
                slideInHorizontally(
                    animationSpec = GlitchyFocus as FiniteAnimationSpec<IntOffset>,
                    initialOffsetX = { -it / 4 })
    }

    /** Confident slide from right - for navigation */
    val SlideFromRight: EnterTransition =
        slideInHorizontally(
            animationSpec = ConfidentSpring as FiniteAnimationSpec<IntOffset>,
            initialOffsetX = { it }
        ) + fadeIn(ConfidentSpring as FiniteAnimationSpec<Float>)

    /** Floating up from bottom - for dialogs */
    val FloatFromBottom: EnterTransition =
        slideInVertically(
            animationSpec = VictoryBounce as FiniteAnimationSpec<IntOffset>,
            initialOffsetY = { it }
        ) + fadeIn(VictoryBounce as FiniteAnimationSpec<Float>)

    /** Dramatic zoom entrance */
    val DramaticZoom: EnterTransition =
        scaleIn(
            animationSpec = DramaticSlow as FiniteAnimationSpec<Float>,
            initialScale = 0.1f
        ) + fadeIn(DramaticSlow)

    // ========== EXIT TRANSITIONS ==========

    /** Digital deconstruction - form dissolving into particles */
    val DeconstructExit: ExitTransition =
        fadeOut(SubtleExit as FiniteAnimationSpec<Float>) +
                scaleOut(
                    animationSpec = SubtleExit,
                    targetScale = 0.8f,
                    transformOrigin = TransformOrigin.Center
                )

    /** Quick glitch disappearance */
    val GlitchExit: ExitTransition =
        fadeOut(tween(MICRO_DURATION)) +
                slideOutHorizontally(
                    animationSpec = tween(MICRO_DURATION),
                    targetOffsetX = { it / 4 }
                )

    /** Slide to left - for navigation */
    val SlideToLeft: ExitTransition =
        slideOutHorizontally(
            animationSpec = ConfidentSpring as FiniteAnimationSpec<IntOffset>,
            targetOffsetX = { -it }
        ) + fadeOut(ConfidentSpring as FiniteAnimationSpec<Float>)

    /** Sink down - for dialogs */
    val SinkDown: ExitTransition =
        slideOutVertically(
            animationSpec = SubtleExit as FiniteAnimationSpec<IntOffset>,
            targetOffsetY = { it }
        ) + fadeOut(SubtleExit as FiniteAnimationSpec<Float>)

    /** Dramatic zoom out */
    val DramaticZoomOut: ExitTransition =
        scaleOut(
            animationSpec = DramaticSlow as FiniteAnimationSpec<Float>,
            targetScale = 2f
        ) + fadeOut(DramaticSlow)

    // ========== COMBINED TRANSITION SETS ==========

    /** Navigation between screens */
    object Navigation {
        val enterFromRight = SlideFromRight
        val exitToLeft = SlideToLeft
        val enterFromLeft =
            slideInHorizontally(ConfidentSpring as FiniteAnimationSpec<IntOffset>) { -it } + fadeIn(
                ConfidentSpring as FiniteAnimationSpec<Float>
            )
        val exitToRight =
            slideOutHorizontally(ConfidentSpring as FiniteAnimationSpec<IntOffset>) { it } + fadeOut(
                ConfidentSpring as FiniteAnimationSpec<Float>
            )
    }

    /** Modal dialogs and overlays */
    object Modal {
        val enter = FloatFromBottom
        val exit = SinkDown
    }

    /** Digital/cyber effects */
    object Digital {
        val materialize = MaterializeEnter
        val deconstruct = DeconstructExit
        val glitchIn = GlitchEnter
        val glitchOut = GlitchExit
    }

    /** Dramatic story moments */
    object Cinematic {
        val enter = DramaticZoom
        val exit = DramaticZoomOut
    }

    // ========== UTILITY FUNCTIONS ==========

    /**
     * Returns an infinite repeatable animation spec that smoothly pulses a float value between its initial state and a target value.
     *
     * The animation uses a linear tween and reverses direction each cycle, creating a continuous breathing or pulsing effect.
     *
     * @param durationMillis Duration of one pulse cycle in milliseconds. Default is 2000 ms.
     * @param targetValue The peak value reached during the pulse. Default is 1.1.
     * @return An infinite repeatable animation spec for use in Compose animations.
     */
    fun createBreathingAnimation(
        durationMillis: Int = 2000,
        targetValue: Float = 1.1f,
    ): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    )

    /**
     * Returns a tween animation spec with linear easing for glitch or shake effects.
     *
     * @param durationMillis Duration of the animation in milliseconds. Defaults to [MICRO_DURATION].
     * @param intensity Intended shake intensity (not used in the animation spec).
     * @return An [AnimationSpec] for animating float values in glitch or shake transitions.
     */
    fun createGlitchShake(
        durationMillis: Int = MICRO_DURATION,
        intensity: Float = 10f,
    ): AnimationSpec<Float> = tween(
        durationMillis = durationMillis,
        easing = LinearEasing
    )

    /**
     * Returns an animation spec that introduces a dramatic pause before executing an action animation.
     *
     * The resulting animation spec has a total duration equal to the pause plus the action animation's duration, and uses a dramatic cubic bezier easing curve.
     *
     * @param pauseDurationMillis The duration of the pause before the action, in milliseconds.
     * @param actionSpec The animation spec representing the action to follow the pause.
     * @return An animation spec with the combined pause and action duration, using dramatic easing.
     */
    fun createDramaticPause(
        pauseDurationMillis: Int = 500,
        actionSpec: AnimationSpec<Float> = DaringEnter,
    ): AnimationSpec<Float> = tween(
        durationMillis = pauseDurationMillis + ((actionSpec as? TweenSpec<*>)?.durationMillis
            ?: STANDARD_DURATION),
        easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    )
}

/**
 * Extension functions for easier animation chaining
 */

/**
 * Returns a tween animation spec that delays the start of the original animation by the specified number of milliseconds.
 *
 * The total duration is the sum of the delay and the original animation's duration. For spring-based specs, the duration is estimated as 1000 ms; for other types, a standard duration is used.
 *
 * @param delayMillis The delay in milliseconds before the animation begins.
 * @return A tween animation spec with the combined delay and animation duration.
 */
fun <T> AnimationSpec<T>.afterDelay(delayMillis: Int): AnimationSpec<T> =
    tween(
        durationMillis = delayMillis + when (this) {
            is TweenSpec<*> -> this.durationMillis
            is SpringSpec<*> -> 1000 // Estimate for spring
            else -> KineticIdentity.STANDARD_DURATION
        }
    )

/**
 * Converts this animation spec into an infinite repeatable animation with the specified repeat mode.
 *
 * @param repeatMode The mode determining how the animation repeats; either [RepeatMode.Restart] or [RepeatMode.Reverse]. Defaults to [RepeatMode.Restart].
 * @return An [InfiniteRepeatableSpec] that repeats this animation indefinitely.
 */
fun <T> AnimationSpec<T>.infinite(repeatMode: RepeatMode = RepeatMode.Restart): InfiniteRepeatableSpec<T> =
    infiniteRepeatable(this as DurationBasedAnimationSpec<T>, repeatMode)

/**
 * Applies the specified easing to this animation spec if it is a tween; otherwise, returns the original spec.
 *
 * @param easing The easing function to use if creating a new tween animation spec.
 * @return A tween animation spec with the given easing, or the original spec if not a tween.
 */
fun AnimationSpec<Float>.withEasing(easing: Easing): AnimationSpec<Float> = when (this) {
    is TweenSpec -> tween(this.durationMillis, easing = easing)
    else -> this
}

