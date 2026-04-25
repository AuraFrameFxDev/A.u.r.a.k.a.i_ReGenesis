package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * IconPicker — Icon selection component for UI customization
 */
@Composable
fun IconPicker(
    selectedIconId: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Stub implementation — full icon picker with search in Phase 2
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        // Placeholder items
        items(listOf("default", "check", "home", "settings")) { iconId ->
            IconButton(onClick = { onIconSelected(iconId) }) {
                Icon(Icons.Default.Check, contentDescription = iconId)
            }
        }
    }
}

/**
 * IconPicker Dialog with search functionality
 */
@Composable
fun IconPickerDialog(
    selectedIconId: String,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stub implementation — full dialog in Phase 2
    IconPicker(
        selectedIconId = selectedIconId,
        onIconSelected = { iconId ->
            onIconSelected(iconId)
            onDismiss()
        },
        modifier = modifier
    )
}
