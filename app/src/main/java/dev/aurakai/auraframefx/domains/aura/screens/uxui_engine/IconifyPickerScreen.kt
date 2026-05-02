package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconPicker
import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconPickerViewModel

/**
 * 🎨 ICONIFY PICKER SCREEN WRAPPER
 * Wraps the full-featured IconPicker component
 * Integrates with Dr. Disagree's Iconify root app
 */
@Composable
fun IconifyPickerScreen(
    viewModel: IconPickerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    with(viewModel) {
        with(Modifier) {
            IconPicker(
                onIconSelected = { iconId ->
                    // TODO: Handle icon selection
                    // component.icon = iconId
                },
                onDismiss = onNavigateBack
            )
        }
    }
}
