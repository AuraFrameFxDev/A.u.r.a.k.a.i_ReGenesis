<<<<<<<< HEAD:app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/CanvasScreen.kt
package dev.aurakai.auraframefx.domains.aura.screens
========
package dev.aurakai.auraframefx.domains.aura.aura.ui
>>>>>>>> origin/main:app/src/main/java/dev/aurakai/auraframefx/domains/aura/aura/ui/CanvasScreen.kt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import collabcanvas.ui.CanvasViewModel

/**
 * Wrapper for Collaborative Canvas with WebSocket support
 *
 * This delegates to the real collabcanvas.ui.CanvasScreen with WebSocket integration
 */
@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: CanvasViewModel = hiltViewModel()
) {
    // Connect to WebSocket when screen opens
    LaunchedEffect(Unit) {
        viewModel.connect("genesis-canvas-session")
    }

    // Use the real collaborative canvas from the collabcanvas module
    collabcanvas.ui.CanvasScreen(
        modifier = modifier,
        onBack = onNavigateBack,
        isCollaborative = true,
        collaborationEvents = null // TODO: Wire to viewModel.webSocketEvents
    )
}
