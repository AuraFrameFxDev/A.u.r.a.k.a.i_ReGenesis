package dev.aurakai.auraframefx.domains.aura.ui.recovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay

/**
 * ════════════════════════════════════════════════════════════════════════════
 * AURA'S DRAMATIC RECOVERY TAKEOVER
 * ════════════════════════════════════════════════════════════════════════════
 */
@Composable
fun UIRecoveryBlackoutScreen(
    viewModel: UIRecoveryViewModel = hiltViewModel()
) {
    val recoveryState by viewModel.recoveryState.collectAsState()
    val isRecoveryNeeded = recoveryState is UIRecoveryState.RecoveryNeeded

    val blackoutAlpha = remember { Animatable(0f) }

    LaunchedEffect(isRecoveryNeeded) {
        if (isRecoveryNeeded) {
            blackoutAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                )
            )
            delay(200)
        } else {
            blackoutAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                )
            )
        }
    }

    AnimatedVisibility(
        visible = isRecoveryNeeded || blackoutAlpha.value > 0f,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = blackoutAlpha.value)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (blackoutAlpha.value >= 1f) {
                UIRecoveryDialog(
                    onDismiss = { viewModel.dismissRecovery() },
                    viewModel = viewModel
                )
            }
        }
    }
}

fun UIRecoveryManager.forceBlackout(
    error: Throwable,
    context: String = "Forced recovery intervention"
) {
    triggerRecovery(error, context)
}
