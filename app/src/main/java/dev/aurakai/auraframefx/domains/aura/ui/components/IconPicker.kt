package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.domains.aura.services.iconify.IconifyService

@Composable
fun IconPicker(
    iconifyService: IconifyService,
    currentIcon: String?,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: Implement Icon Picker component
}
