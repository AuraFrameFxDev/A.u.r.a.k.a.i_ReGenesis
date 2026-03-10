package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable panel exposing quick Xposed hooks and module tools with a Back action.
 *
 * Displays a title, a placeholder description, and a Back button that invokes the provided callback.
 *
 * @param onNavigateBack Lambda invoked when the Back button is pressed; defaults to a no-op.
 */
@JvmOverloads
@Composable
fun XposedQuickAccessPanel(onNavigateBack: () -> Unit = {}) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Xposed Quick Access", style = MaterialTheme.typography.titleLarge)
        Text("Placeholder panel exposing quick hooks and module tools.")
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}
