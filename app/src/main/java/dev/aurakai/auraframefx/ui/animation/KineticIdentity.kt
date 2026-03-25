package dev.aurakai.auraframefx.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.*
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

/**
 * KineticIdentity ⚡
 *
 * Aura's signature animation language - defining the rhythm and soul of every movement
 * in AuraFrameFX. Every animation is intentional, part of a living digital ecosystem.
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
    val DaringEnter: FiniteAnimationSpec<Float> = tween(
        durationMillis = DRAMATIC_DURATION,
        easing = FastOutSlowInEasing
    )

    /** A quick, subtle exit animation */
    val SubtleExit: FiniteAnimationSpec<Float> = tween(
        durationMillis = STANDARD_DURATION,
        easing = FastOutLinearInEasing
    )

    /** A bouncy, slightly chaotic spring for interactive feedback - The "Mad Hatter" touch */
    val GlitchyFocus: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Smooth, confident spring for primary interactions */
    val ConfidentSpring: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val ConfidentSpringFloat: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Energetic bounce for success states */
    val VictoryBounce: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )
    
    val VictoryBounceFloat: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /** Dramatic slow-motion for critical moments */
    val DramaticSlow: FiniteAnimationSpec<Float> = tween(
        durationMillis = EPIC_DURATION,
        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    )

    /** Quick pulse for notifications */
    val AlertPulse: FiniteAnimationSpec<Float> = tween(
        durationMillis = MICRO_DURATION,
        easing = LinearEasing
    )

    /** Organic breathing animation */
    val OrganicBreath: FiniteAnimationSpec<Float> = tween(
        durationMillis = 2000,
        easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
    )

    // ========== ENTER TRANSITIONS ==========

    /** Digital materialization - particles coalescing into form */
    val MaterializeEnter: EnterTransition =
        fadeIn(animationSpec = DaringEnter) +
                scaleIn(
                    animationSpec = DaringEnter,
                    initialScale = 0.3f,
                    transformOrigin = TransformOrigin.Center
                )

    /** Glitch-style entrance from the void */
    val GlitchEnter: EnterTransition =
        fadeIn(tween(MICRO_DURATION)) +
                slideInHorizontally(
                    animationSpec = GlitchyFocus,
                    initialOffsetX = { -it / 4 }
                )

    /** Confident slide from right - for navigation */
    val SlideFromRight: EnterTransition =
        slideInHorizontally(
            animationSpec = ConfidentSpring,
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = ConfidentSpringFloat)

    /** Floating up from bottom - for dialogs */
    val FloatFromBottom: EnterTransition =
        slideInVertically(
            animationSpec = VictoryBounce,
            initialOffsetY = { it }
        ) + fadeIn(animationSpec = VictoryBounceFloat)

    /** Dramatic zoom entrance */
    val DramaticZoom: EnterTransition =
        scaleIn(
            animationSpec = DramaticSlow,
            initialScale = 0.1f
        ) + fadeIn(animationSpec = DramaticSlow)

    // ========== EXIT TRANSITIONS ==========

    /** Digital deconstruction - form dissolving into particles */
    val DeconstructExit: ExitTransition =
        fadeOut(animationSpec = SubtleExit) +
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
            animationSpec = ConfidentSpring,
            targetOffsetX = { -it }
        ) + fadeOut(animationSpec = ConfidentSpringFloat)

    /** Sink down - for dialogs */
    val SinkDown: ExitTransition =
        slideOutVertically(
            animationSpec = SubtleExit.toIntOffsetSpec(),
            targetOffsetY = { it }
        ) + fadeOut(animationSpec = SubtleExit)

    /** Dramatic zoom out */
    val DramaticZoomOut: ExitTransition =
        scaleOut(
            animationSpec = DramaticSlow,
            targetScale = 2f
        ) + fadeOut(animationSpec = DramaticSlow)

    // ========== COMBINED TRANSITION SETS ==========

    /** Navigation between screens */
    object Navigation {
        val enterFromRight = SlideFromRight
        val exitToLeft = SlideToLeft
        val enterFromLeft =
            slideInHorizontally(animationSpec = ConfidentSpring) { -it } + fadeIn(animationSpec = ConfidentSpringFloat)
        val exitToRight =
            slideOutHorizontally(animationSpec = ConfidentSpring) { it } + fadeOut(animationSpec = ConfidentSpringFloat)
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

    fun createBreathingAnimation(
        durationMillis: Int = 2000,
        targetValue: Float = 1.1f,
    ): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    )

    fun createGlitchShake(
        durationMillis: Int = MICRO_DURATION,
        intensity: Float = 10f,
    ): FiniteAnimationSpec<Float> = tween(
        durationMillis = durationMillis,
        easing = LinearEasing
    )

    fun createDramaticPause(
        pauseDurationMillis: Int = 500,
        actionSpec: FiniteAnimationSpec<Float> = DaringEnter,
    ): FiniteAnimationSpec<Float> = tween(
        durationMillis = pauseDurationMillis + ((actionSpec as? TweenSpec<*>)?.durationMillis
            ?: STANDARD_DURATION),
        easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    )
    
    private fun FiniteAnimationSpec<Float>.toIntOffsetSpec(): FiniteAnimationSpec<IntOffset> {
        return if (this is TweenSpec) {
            tween(this.durationMillis, this.delay, this.easing)
        } else {
            spring()
        }
    }
}

/**
 * Extension functions for easier animation chaining
 */

fun <T> AnimationSpec<T>.afterDelay(delayMillis: Int): AnimationSpec<T> =
    when (this) {
        is TweenSpec -> tween(delayMillis + this.durationMillis, this.delay, this.easing)
        is SpringSpec -> this // Spring doesn't easily support additive delay this way
        else -> this
    }

fun <T> AnimationSpec<T>.infinite(repeatMode: RepeatMode = RepeatMode.Restart): InfiniteRepeatableSpec<T> =
    infiniteRepeatable(this as DurationBasedAnimationSpec<T>, repeatMode)

fun AnimationSpec<Float>.withEasing(easing: Easing): AnimationSpec<Float> = when (this) {
    is TweenSpec -> tween(this.durationMillis, this.delay, easing)
    else -> this
}
