import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * HologramTransition composable for futuristic lockscreen or UI transitions.
 * Combines fade, scale, and optional color/blur for a holographic effect.
 */
/**
 * Animates a holographic scale-and-fade transition around the provided content.
 *
 * When `visible` becomes true the composable scales from `startScale` to `endScale`
 * and fades from `startAlpha` to `endAlpha`; when `visible` becomes false the
 * animations run in reverse. The scale and alpha animations use the same
 * `durationMillis` and run sequentially each time `visible` changes.
 *
 * @param visible Controls whether the transition moves to the visible state or to the hidden state.
 * @param modifier Modifier applied to the container hosting the content.
 * @param durationMillis Duration of each animation phase in milliseconds.
 * @param startScale Scale value at the start of the visible transition (and end of the hidden transition).
 * @param endScale Scale value at the end of the visible transition (and start of the hidden transition).
 * @param startAlpha Alpha value at the start of the visible transition (and end of the hidden transition).
 * @param endAlpha Alpha value at the end of the visible transition (and start of the hidden transition).
 * @param content Composable content rendered inside the animated container.
 */
/**
 * Animates content with a holographic scale-and-fade transition when visibility changes.
 *
 * When `visible` is true the content scales from `startScale` to `endScale` and fades from
 * `startAlpha` to `endAlpha`; when `visible` is false the animations run in reverse. The scale
 * animation runs first, followed by the alpha animation, and both use `durationMillis`.
 *
 * @param visible Target visibility state; controls whether the transition plays forward or in reverse.
 * @param modifier Modifier applied to the animated container.
 * @param durationMillis Duration in milliseconds for each animation phase (scale and alpha).
 * @param startScale Scale value at the start of the enter animation (or end of the exit animation).
 * @param endScale Scale value at the end of the enter animation (or start of the exit animation).
 * @param startAlpha Alpha value at the start of the enter animation (or end of the exit animation).
 * @param endAlpha Alpha value at the end of the enter animation (or start of the exit animation).
 * @param content Composable content to be rendered inside the animated container.
 */
/**
 * Animates a holographic scale-and-fade transition around the provided composable content.
 *
 * When `visible` becomes `true` the component first animates scale from `startScale` to
 * `endScale`, then animates alpha from `startAlpha` to `endAlpha`. When `visible` becomes
 * `false` the animations run in reverse. Each phase uses a tween with `durationMillis`.
 *
 * @param visible Controls whether the transition runs forward (`true`) or in reverse (`false`).
 * @param modifier Modifier applied to the outer container.
 * @param durationMillis Duration in milliseconds for each animation phase (scale and alpha).
 * @param startScale Scale value at the beginning of the enter animation (or end of the exit).
 * @param endScale Scale value at the end of the enter animation (or beginning of the exit).
 * @param startAlpha Alpha value at the beginning of the enter animation (or end of the exit).
 * @param endAlpha Alpha value at the end of the enter animation (or beginning of the exit).
 * @param content Composable content that will be rendered inside the animated container.
 */
@Composable
fun HologramTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = 700,
    startScale: Float = 0.85f,
    endScale: Float = 1f,
    startAlpha: Float = 0.3f,
    endAlpha: Float = 1f,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(if (visible) startScale else endScale) }
    val alpha = remember { Animatable(if (visible) startAlpha else endAlpha) }

    LaunchedEffect(visible) {
        scale.animateTo(
            if (visible) endScale else startScale,
            animationSpec = tween(durationMillis)
        )
        alpha.animateTo(
            if (visible) endAlpha else startAlpha,
            animationSpec = tween(durationMillis)
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.scaleX = scale.value
                this.scaleY = scale.value
                this.alpha = alpha.value
                // Optionally add blur or color filter for more hologram effect
            }
    ) {
        content()
    }
}