package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

/**
 * Displays a placeholder collaborative canvas screen containing a title, a brief description, and a Back button.
 *
 * The Back button will attempt to pop the provided navigation controller's back stack (if non-null) and then invoke the provided callback.
 *
 * @param navController Optional NavHostController used to navigate back; if null no navigation is performed.
 * @param onNavigateBack Callback invoked after attempting to pop the navigation back stack.
 */
@Composable
fun CollabCanvasScreen(navController: NavHostController? = null, onNavigateBack: () -> Unit = {}) {
    Column(modifier = Modifier) {
        Text("Collab Canvas", style = MaterialTheme.typography.titleLarge)
        Text("This is a placeholder wrapper for the collab canvas module.")
        Button(onClick = { navController?.popBackStack(); onNavigateBack() }) {
            Text("Back")
        }
    }
}
