package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.overlays

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NeuralLinkSidebarUI(
    isVisible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    onActionClick: (String) -> Unit
) {
    if (isVisible) {
        Text("Neural Link Sidebar - Coming Soon")
    }
}
