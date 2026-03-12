package dev.aurakai.auraframefx.domains.aura.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class LockScreenAnimationType {
    object Fade : LockScreenAnimationType()
    object Slide : LockScreenAnimationType()
    object Zoom : LockScreenAnimationType()
}

@Composable
fun LockScreenAnimatedContent(
    visible: Boolean,
    animationType: LockScreenAnimationType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when (animationType) {
        is LockScreenAnimationType.Fade -> {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500)),
                modifier = modifier
            ) {
                content()
            }
        }

        is LockScreenAnimationType.Slide -> {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(500)
                ),
                modifier = modifier
            ) {
                content()
            }
        }

        is LockScreenAnimationType.Zoom -> {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.8f, animationSpec = tween(500)),
                exit = scaleOut(targetScale = 0.8f, animationSpec = tween(500)),
                modifier = modifier
            ) {
                content()
            }
        }
    }
}

