package collabcanvas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.core.theme.AuraFrameFXTheme as CanonicalTheme

/**
 * CollabCanvasTheme: Local theme for the CollabCanvas module.
 * Delegates to the canonical AuraFrameFXTheme to ensure visual consistency.
 */
@Composable
fun CollabCanvasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CanonicalTheme(
        darkTheme = darkTheme,
        content = content
    )
}
