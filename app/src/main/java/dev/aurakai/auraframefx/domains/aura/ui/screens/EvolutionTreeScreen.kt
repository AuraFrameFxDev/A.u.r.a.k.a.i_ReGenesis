package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionNode
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen as NexusEvolutionTreeScreen

/**
 * Wrapper composable placed in `ui.screens` to provide a stable public API for navigation wiring.
 * Forwards to the real implementation in `domains.nexus.screens` so we don't duplicate heavy UI logic.
 */
@Composable
fun EvolutionTreeScreen(
    onNavigateToAgents: () -> Unit,
    onNavigateToFusion: () -> Unit,
    onNodeSelected: (EvolutionNode) -> Unit,
    modifier: Modifier = Modifier
) {
    NexusEvolutionTreeScreen(
        onNavigateToAgents = onNavigateToAgents,
        onNavigateToFusion = onNavigateToFusion,
        onNodeSelected = onNodeSelected,
        modifier = modifier
    )
}

