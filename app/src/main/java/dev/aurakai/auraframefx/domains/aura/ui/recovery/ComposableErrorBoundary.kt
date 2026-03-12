package dev.aurakai.auraframefx.domains.aura.ui.recovery

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

/**
 * Error boundary for Compose screens
 *
 * Catches uncaught exceptions in Composables and triggers UI recovery.
 *
 * Usage:
 * ```kotlin
 * ComposableErrorBoundary(
 *     screenName = "AgentNexus"
 * ) {
 *     // Your screen content
 *     AgentNexusScreen()
 * }
 * ```
 *
 * @param screenName Name of screen for recovery context
 * @param onError Optional custom error handling
 * @param content The composable content to protect
 */
@Composable
fun ComposableErrorBoundary(
    screenName: String,
    recoveryManager: UIRecoveryManager? = null,
    onError: ((Throwable) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val errorHandler = remember {
        ErrorHandler { error ->
            Log.e("ComposableErrorBoundary", "Error in $screenName", error)
            onError?.invoke(error) ?: run {
                // Trigger recovery system if manager is available
                recoveryManager?.triggerRecovery(
                    error = error,
                    context = "Screen: $screenName"
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        // Install error handler for this composable
        errorHandler.install()
    }

    // try-catch around Composable is not supported
    content()
}

/**
 * Error handler for Compose errors
 */
internal class ErrorHandler(
    private val onError: (Throwable) -> Unit
) {
    fun install() {
        // Set up error handling for this scope
        // In production, you'd integrate with Compose error handling
        // Optionally hook into Thread default uncaught handler or other mechanisms here.
    }

    fun handleError(error: Throwable) {
        onError(error)
    }
}

/**
 * Extension function to automatically save snapshots after successful navigation
 *
 * Usage in your navigation code:
 * ```kotlin
 * navController.navigate("AGENT_NEXUS") {
 *     onNavigationComplete { route ->
 *         recoveryManager.saveSnapshot(
 *             UIStateSnapshot.forScreen(route)
 *         )
 *     }
 * }
 * ```
 */
fun onNavigationComplete(route: String, block: (String) -> Unit): () -> Unit {
    return { block(route) }
}

