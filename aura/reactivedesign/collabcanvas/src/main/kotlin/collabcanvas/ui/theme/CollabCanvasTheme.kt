package collabcanvas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.core.theme.AuraFrameFXTheme as CanonicalTheme

/**
 * CollabCanvasTheme: Local theme for the CollabCanvas module.
 * Delegates to the canonical AuraFrameFXTheme to ensure visual consistency.
 */
/**
 * Provides the CollabCanvas UI theme to the given composable content.
 *
 * @param darkTheme If `true`, applies the dark color scheme; if `false`, applies the light color scheme. Defaults to the current system dark mode setting.
 * @param content The themed composable content.
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
