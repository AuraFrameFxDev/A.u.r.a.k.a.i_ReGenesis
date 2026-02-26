package dev.aurakai.auraframefx.domains.aura.aura.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.annotation.ColorInt
import androidx.core.animation.addListener
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import dev.aurakai.auraframefx.domains.cascade.utils.ColorUtils // Fixed import: ColorUtils lives in utils package
import kotlin.math.hypot
import kotlin.math.max

/**
 * Utility class for handling animations in the Quick Settings panel.
 */
object AnimationUtils {
    private const val TAG = "AnimationUtils"

    // Default animation durations
    private const val DEFAULT_DURATION = 200L
    private const val REVEAL_DURATION = 400L
    private const val SPRING_STIFFNESS = 0.7f
    private const val SPRING_DAMPING = 0.8f

    /**
     * Fade in a view with an optional callback when the animation ends.
     *
     * @param view The view to fade in.
     * @param duration The duration of the animation in milliseconds.
     * @param startDelay The delay before starting the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun fadeIn(
        view: View,
        duration: Long = DEFAULT_DURATION,
        startDelay: Long = 0,
        onEnd: (() -> Unit)? = null,
    ) {
        if (view.isVisible && view.alpha == 1f) {
            onEnd?.invoke()
            return
        }

        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setStartDelay(startDelay)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * Fade out a view with an optional callback when the animation ends.
     *
     * @param view The view to fade out.
     * @param duration The duration of the animation in milliseconds.
     * @param startDelay The delay before starting the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun fadeOut(
        view: View,
        duration: Long = DEFAULT_DURATION,
        startDelay: Long = 0,
        onEnd: (() -> Unit)? = null,
    ) {
        if (view.visibility != View.VISIBLE) {
            onEnd?.invoke()
            return
        }

        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setStartDelay(startDelay)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.visibility = View.INVISIBLE
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * Reveal a view with a circular reveal animation.
     *
     * @param view The view to reveal.
     * @param centerX The x-coordinate of the center of the reveal circle.
     * @param centerY The y-coordinate of the center of the reveal circle.
     * @param startRadius The starting radius of the reveal circle.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun revealView(
        view: View,
        centerX: Int,
        centerY: Int,
        startRadius: Float = 0f,
        duration: Long = REVEAL_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        if (view.isVisible) {
            onEnd?.invoke()
            return
        }

        view.visibility = View.VISIBLE

        val finalRadius = max(
            hypot(centerX.toDouble(), centerY.toDouble()),
            hypot(
                (view.width - centerX).toDouble(),
                (view.height - centerY).toDouble()
            )
        ).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            centerX,
            centerY,
            startRadius,
            finalRadius
        )

        anim.duration = duration
        anim.interpolator = DecelerateInterpolator()
        anim.addListener(onEnd = { onEnd?.invoke() })
        anim.start()
    }

    /**
     * Hide a view with a circular reveal animation.
     *
     * @param view The view to hide.
     * @param centerX The x-coordinate of the center of the reveal circle.
     * @param centerY The y-coordinate of the center of the reveal circle.
     * @param endRadius The ending radius of the reveal circle.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun hideWithReveal(
        view: View,
        centerX: Int,
        centerY: Int,
        endRadius: Float = 0f,
        duration: Long = REVEAL_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        if (view.visibility != View.VISIBLE) {
            onEnd?.invoke()
            return
        }

        val startRadius = max(
            view.width.toFloat(),
            view.height.toFloat()
        )

        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            centerX,
            centerY,
            startRadius,
            endRadius
        )

        anim.duration = duration
        anim.interpolator = AccelerateInterpolator()
        anim.addListener(
            onEnd = {
                view.visibility = View.INVISIBLE
                onEnd?.invoke()
            }
        )
        anim.start()
    }

    /**
     * Animate the background color of a view.
     *
     * @param view The view to animate.
     * @param startColor The starting color.
     * @param endColor The ending color.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun animateBackgroundColor(
        view: View,
        @ColorInt startColor: Int,
        @ColorInt endColor: Int,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
        animator.duration = duration
        animator.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            view.background = color.toDrawable()
        }
        animator.addListener(
            onEnd = { onEnd?.invoke() }
        )
        animator.start()
    }

    /**
     * Animate the background color of a view with a ripple effect.
     *
     * @param view The view to animate.
     * @param rippleColor The color of the ripple effect.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun animateRippleEffect(
        view: View,
        @ColorInt rippleColor: Int,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        val startColor = ColorUtils.withAlpha(rippleColor, 0x4D) // 30% alpha
        val endColor = ColorUtils.withAlpha(rippleColor, 0x00) // 0% alpha

        val animator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
        animator.duration = duration
        animator.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            view.setBackgroundColor(color)
        }
        animator.addListener(
            onEnd = { onEnd?.invoke() }
        )
        animator.start()
    }

    /**
     * Create a scale animation for a view.
     *
     * @param view The view to animate.
     * @param fromX The starting X scale.
     * @param toX The ending X scale.
     * @param fromY The starting Y scale.
     * @param toY The ending Y scale.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun scaleView(
        view: View,
        fromX: Float = 1f,
        toX: Float = 1f,
        fromY: Float = 1f,
        toY: Float = 1f,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        view.scaleX = fromX
        view.scaleY = fromY

        view.animate()
            .scaleX(toX)
            .scaleY(toY)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * Create a bounce animation for a view.
     *
     * @param view The view to animate.
     * @param scale The scale factor for the bounce.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun bounceView(
        view: View,
        scale: Float = 1.1f,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(duration / 2)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration / 2)
                    .withEndAction {
                        onEnd?.invoke()
                    }
                    .start()
            }
            .start()
    }

    /**
     * Create a shake animation for a view.
     *
     * @param view The view to animate.
     * @param distance The distance to shake in pixels.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun shakeView(
        view: View,
        distance: Float = 20f,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        val anim = TranslateAnimation(
            -distance, distance, 0f, 0f
        ).apply {
            this.duration = duration / 5
            repeatCount = 5
            repeatMode = Animation.REVERSE
            interpolator = LinearInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    onEnd?.invoke()
                }
            })
        }
        view.startAnimation(anim)
    }

    /**
     * Animate a view's elevation.
     *
     * @param view The view to animate.
     * @param fromElevation The starting elevation in pixels.
     * @param toElevation The ending elevation in pixels.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun animateElevation(
        view: View,
        fromElevation: Float,
        toElevation: Float,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        val animator = ValueAnimator.ofFloat(fromElevation, toElevation)
        animator.duration = duration
        animator.addUpdateListener { animator ->
            ViewCompat.setElevation(view, animator.animatedValue as Float)
        }
        animator.addListener(
            onEnd = { onEnd?.invoke() }
        )
        animator.start()
    }

    /**
     * Crossfade between two views.
     *
     * @param fadeInView The view to fade in.
     * @param fadeOutView The view to fade out.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Callback when the animation ends.
     */
    fun crossfade(
        fadeInView: View,
        fadeOutView: View,
        duration: Long = DEFAULT_DURATION,
        onEnd: (() -> Unit)? = null,
    ) {
        fadeInView.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null)
        }

        fadeOutView.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fadeOutView.visibility = View.GONE
                    onEnd?.invoke()
                }
            })
    }
}

