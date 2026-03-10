package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * Minimal stub for the Agent Hub main screen.
 * Replace with real implementation later; this keeps navigation alive.
 */
/**
 * Displays a minimal placeholder "Agent Hub" screen with a title, descriptive text, and a Back button.
 *
 * The Back button pops the provided NavHostController's back stack when `navController` is non-null.
 *
 * @param navController Optional NavHostController used to navigate back; if null the Back button has no effect.
 */
@Composable
fun AgentHubScreen(navController: NavHostController? = null) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Agent Hub", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Placeholder Agent Hub - HOME / MISSIONS / INVENTORY / SETTINGS / LOGOUT")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController?.popBackStack() }) {
            Text("Back")
        }
    }
}
