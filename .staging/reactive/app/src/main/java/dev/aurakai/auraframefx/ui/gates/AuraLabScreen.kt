package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays the Aura Lab screen with a title and a placeholder description.
 *
 * @param onNavigateBack Callback invoked to request navigation back; defaults to a no-op.
 */
@Composable
fun AuraLabScreen(onNavigateBack: () -> Unit = {}) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Auras Lab", style = MaterialTheme.typography.titleLarge)
        Text("Experimental creative workspace placeholder.")
    }
}
