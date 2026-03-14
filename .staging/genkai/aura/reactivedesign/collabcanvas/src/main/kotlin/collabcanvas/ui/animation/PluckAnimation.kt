package collabcanvas.ui.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Animation specification for pluck effects on canvas elements.
 */
fun pluckAnimationSpec(): AnimationSpec<Float> {
    return spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * Creates a pluck animation with the specified duration and easing.
 */
fun pluckAnimation(
    durationMillis: Int = 300,
    easing: Easing = FastOutSlowInEasing
): AnimationSpec<Float> {
    return tween(
        durationMillis = durationMillis,
        easing = easing
    )
}
