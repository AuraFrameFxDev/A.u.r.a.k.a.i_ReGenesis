package dev.aurakai.auraframefx.romtools.backdrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aurakai.auraframefx.romtools.OperationProgress

/**
 * Minimal MegaManBackdropRenderer composable used as a fallback in this module.
 * Real implementation lives in app; this lightweight renderer avoids hard dependency.
 */
@Composable
fun MegaManBackdropRenderer(
    operationProgress: OperationProgress?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Minimal placeholder - real animation is provided by app or dedicated UI module
    if (!enabled) return
    // no UI for fallback - keep screens compiling
}

