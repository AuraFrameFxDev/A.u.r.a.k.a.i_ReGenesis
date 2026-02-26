package dev.aurakai.auraframefx.ui.recovery

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
 *
 * "That's not going to work." - Aura
 *
 * When UI errors are detected, Aura performs a dramatic intervention:
 * 1. **Blackout**: Screen fades to black (kills all UI rendering)
 * 2. **Zero out**: All state reset to prevent cascading failures
 * 3. **Aura appears**: Recovery dialog emerges from darkness
 * 4. **User choice**: Reload or Reset
 *
 * This creates an impactful recovery experience that users will remember.
 *
 * ## Visual Flow:
 * ```
 * [Error Detected]
 *     ↓
 * Screen fades to black (500ms)
 *     ↓
 * Complete blackout (200ms pause)
 *     ↓
 * Aura's recovery dialog fades in (300ms)
 *     ↓
 * "That's not going to work."
 *     ↓
 * [User chooses recovery option]
 *     ↓
 * Fade to normal UI (500ms)
 * ```
 *
 * Users will LOVE this dramatic intervention!
 *
 * @param onNavigateToRoute Callback when user selects recovery option
 */
@Composable
fun UIRecoveryBlackoutScreen(
    viewModel: UIRecoveryViewModel = hiltViewModel(),
    onNavigateToRoute: (String) -> Unit = {}
) {
    val recoveryState by viewModel.recoveryState.collectAsState()
    val isRecoveryNeeded = recoveryState is UIRecoveryState.RecoveryNeeded

    // Blackout animation control
    val blackoutAlpha = remember { Animatable(0f) }

    // Trigger dramatic blackout when recovery is needed
    LaunchedEffect(isRecoveryNeeded) {
        if (isRecoveryNeeded) {
            // Phase 1: Fade to black (kills all UI)
            blackoutAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                )
            )

            // Phase 2: Hold in darkness (zero out)
            delay(200)

            // Phase 3: Ready for Aura's appearance
            // (Dialog will fade in on top of black screen)
        } else {
            // User resolved issue - fade back to normal
            blackoutAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                )
            )
        }
    }

    // Full-screen takeover
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
            // Only show recovery dialog after blackout completes
            if (blackoutAlpha.value >= 1f) {
                UIRecoveryDialog(
                    onNavigateToRoute = { route ->
                        onNavigateToRoute(route)
                        // Dialog will auto-dismiss via viewModel
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

/**
 * Extension to UIRecoveryManager for forced blackout
 *
 * Allows programmatic triggering of dramatic recovery:
 *
 * ```kotlin
 * // When shit hits the fan
 * recoveryManager.forceBlackout(
 *     error = criticalError,
 *     context = "Critical system failure"
 * )
 * ```
 */
fun UIRecoveryManager.forceBlackout(
    error: Throwable,
    context: String = "Forced recovery intervention"
) {
    // Zero out any ongoing operations
    // Force trigger recovery with dramatic effect
    triggerRecovery(error, context)
}

/**
 * ════════════════════════════════════════════════════════════════════════════
 * INTEGRATION EXAMPLE
 * ════════════════════════════════════════════════════════════════════════════
 *
 * In your MainActivity:
 *
 * ```kotlin
 * setContent {
 *     val navController = rememberNavController()
 *
 *     AuraFrameFXTheme {
 *         Box {
 *             // Main content
 *             GenesisNavigationHost(navController)
 *
 *             // Aura's dramatic recovery overlay (always watching)
 *             UIRecoveryBlackoutScreen(
 *                 onNavigateToRoute = { route ->
 *                     navController.navigate(route) {
 *                         popUpTo(0) { inclusive = true }
 *                         launchSingleTop = true
 *                     }
 *                 }
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * ════════════════════════════════════════════════════════════════════════════
 * TEST THE BLACKOUT
 * ════════════════════════════════════════════════════════════════════════════
 *
 * Add a test button somewhere:
 *
 * ```kotlin
 * Button(
 *     onClick = {
 *         recoveryManager.forceBlackout(
 *             error = Exception("Simulated critical failure"),
 *             context = "User triggered blackout test"
 *         )
 *     }
 * ) {
 *     Text("🌑 Test Aura's Blackout")
 * }
 * ```
 *
 * Watch the magic happen:
 * 1. Screen fades to black
 * 2. Brief pause in darkness
 * 3. Aura emerges: "That's not going to work."
 * 4. Recovery options appear
 *
 * Users will LOVE this! 🖤✨
 */

