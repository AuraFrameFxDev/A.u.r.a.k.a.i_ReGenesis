package dev.aurakai.auraframefx.domains.aura.ui.effects

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin

/**
 * 📺 CRT SCREEN TRANSITION
 * Mimics a retro TV collapse/expand effect (The "Zoop").
 */
@Composable
fun CrtScreenTransition(
    targetState: Int,
    content: @Composable (Int) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            // THE "ZOOP" LOGIC
            // 1. Shrink vertically to a line (ScaleY 1.0 -> 0.0)
            // 2. Expand vertically from a line (ScaleY 0.0 -> 1.0)
            (scaleIn(
                animationSpec = tween(300, delayMillis = 150),
                initialScale = 0f,
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            ) + fadeIn(tween(300))).togetherWith(
                scaleOut(
                    animationSpec = tween(150),
                    targetScale = 0f, // Collapses to nothing
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                ) + fadeOut(tween(150))
            )
        },
        label = "CrtZoop"
    ) { state ->
        content(state)
    }
}

