package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
    IconPicker(
        iconifyService = viewModel.iconifyService,
        currentIcon = null,
        onIconSelected = { iconId ->
            // TODO: Handle icon selection
            // component.icon = iconId
        },
        onDismiss = onNavigateBack
    )
}

