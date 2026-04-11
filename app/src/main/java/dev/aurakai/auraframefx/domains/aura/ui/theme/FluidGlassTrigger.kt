package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel

/**
 * 🔮 FLUID GLASSMORPHISM TRIGGER
 *
 * Wires the "on/off" switch to all UI components.
 * When enabled, applies the "Spellbook" glass effect.
 */
/**
 * Applies a customization-driven glassmorphism effect to this Modifier, or a semi-transparent
 * fallback background when the effect is disabled.
 *
 * The active treatment is driven by the provided CustomizationViewModel's state: when
 * `glassEnabled` is true the modifier is augmented with a glassmorphic effect using the
 * configured blur radius and surface alpha; otherwise a simple background with reduced alpha
 * is applied.
 *
 * @param baseColor The base color used for the glass surface or fallback background.
 * @return This Modifier augmented with either the glassmorphic effect or a semi-transparent background.
 */
@Composable
fun Modifier.fluidGlass(
    viewModel: CustomizationViewModel = viewModel(),
    baseColor: Color = Color.White.copy(alpha = 0.1f)
): Modifier {
    val state by viewModel.state.collectAsState()

    return if (state.glassEnabled) {
        // Apply the high-fidelity glass effect
        this.glassmorphic(
            glassColor = baseColor.copy(alpha = state.glassSurfaceAlpha),
            blurRadius = state.glassBlurRadiusDp.dp
        )
    } else {
        // Standard background when disabled
        this.background(baseColor.copy(alpha = 0.2f))
    }
}
