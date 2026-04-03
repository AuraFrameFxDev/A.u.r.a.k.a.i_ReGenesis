package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.core.theme.AuraFrameFXTheme as CanonicalTheme

/**
 * Applies the AuraFrameFX theme to the provided composable content.
 *
 * @param darkTheme When `true`, the theme uses a dark color scheme; when `false`, it uses a light color scheme. Defaults to the system dark theme setting.
 * @param content Composable content to be styled by the theme.
 */
@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CanonicalTheme(
        darkTheme = darkTheme,
        content = content
    )
}
