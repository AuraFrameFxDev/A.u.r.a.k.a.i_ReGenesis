package dev.aurakai.auraframefx.ui.screens

// import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.aurakai.auraframefx.aura.animations.HomeScreenTransitionType
import dev.aurakai.auraframefx.aura.animations.DigitalTransitionRow
import dev.aurakai.auraframefx.ui.components.HologramTransition

/**
 * Displays the Ecosystem Menu screen with a holographic transition and transition type selector.
 *
 * @param transitionType The currently selected home screen transition type.
 * @param showHologram Whether to display the hologram transition effect.
 */
/**
 * Displays the Ecosystem Menu screen with a holographic transition effect and a transition type selector.
 *
 * @param transitionType The currently selected home screen transition type.
 * @param showHologram Whether to display the holographic transition effect.
 */
@Composable
fun EcosystemMenuScreen(
    transitionType: HomeScreenTransitionType = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
    showHologram: Boolean = true,
) {
    HologramTransition(visible = showHologram) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Ecosystem Menu Screen (Placeholder)")
                DigitalTransitionRow(
                    currentType = transitionType,
                    onTypeSelected = {}
                )
            }
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun EcosystemMenuScreenPreview() { // Renamed
//     EcosystemMenuScreen()
// }
