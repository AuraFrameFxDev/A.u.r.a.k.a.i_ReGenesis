package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable fun GateCustomizationScreen(onNavigateBack: () -> Unit) { Text("Gate Customization - Coming Soon") }
@Composable fun GateDomainImagePicker(navController: androidx.navigation.NavController, onNavigateBack: () -> Unit) { Text("Gate Domain Image Picker - Coming Soon") }
@Composable fun LSPosedGateScreen(onNavigateBack: () -> Unit) { Text("LSPosed Gate - Coming Soon") }

object GateAssetLoadout {
    fun getNexusSubGates(): List<String> = emptyList()
}
