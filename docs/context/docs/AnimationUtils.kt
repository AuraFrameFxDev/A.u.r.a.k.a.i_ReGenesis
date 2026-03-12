package dev.aurakai.auraframefx.docs.utils

import androidx.compose.animation.core.*

/**
 * Lightweight animation helpers for docs and stubs.
 */
object AnimationUtils {
    fun fadeInSpec(): FiniteAnimationSpec<Float> = tween(durationMillis = 300)
    fun fadeOutSpec(): FiniteAnimationSpec<Float> = tween(durationMillis = 200)
    fun bounceSpec(): FiniteAnimationSpec<Float> = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
}
