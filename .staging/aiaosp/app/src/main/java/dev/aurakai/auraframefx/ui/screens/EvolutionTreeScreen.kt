package dev.aurakai.auraframefx.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aurakai.auraframefx.aura.ui.EvolutionNode
import dev.aurakai.auraframefx.aura.ui.EvolutionTreeScreen as AuraEvolutionTreeScreen

/**
 * Wrapper composable placed in `ui.screens` to provide a stable public API for navigation wiring.
 * Forwards to the real implementation in `aura.ui` so we don't duplicate heavy UI logic.
 */
@Composable
fun EvolutionTreeScreen(
    onNavigateToAgents: () -> Unit = {},
    onNavigateToFusion: () -> Unit = {},
    modifier: Modifier = Modifier,
    onNodeSelected: (EvolutionNode) -> Unit = {}
) {
    AuraEvolutionTreeScreen(
        onNavigateToAgents = onNavigateToAgents,
        onNavigateToFusion = onNavigateToFusion,
        modifier = modifier,
        onNodeSelected = onNodeSelected
    )
}
