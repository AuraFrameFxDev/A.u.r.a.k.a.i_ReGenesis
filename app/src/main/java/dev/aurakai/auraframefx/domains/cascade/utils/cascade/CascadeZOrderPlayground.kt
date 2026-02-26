package dev.aurakai.auraframefx.domains.cascade.utils.cascade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CascadeDebugViewModel @Inject constructor(
    private val cascadeAgent: CascadeAgent,
) : ViewModel() {
    val visionState: StateFlow<VisionState> = cascadeAgent.visionState
    val processingState: StateFlow<ProcessingState> = cascadeAgent.processingState

    /**
     * Sets the CascadeAgent's vision state to the specified value.
     *
     * @param newState The new vision state to apply.
     */
    fun updateVisionState(newState: VisionState) {
        cascadeAgent.updateVisionState(newState)
    }

    fun updateProcessingState(newState: ProcessingState) {
        cascadeAgent.updateProcessingState(newState)
    }
}

/**
 * Displays a debug UI for inspecting and updating the CascadeAgent's vision and processing states.
 *
 * Provides interactive controls to view and modify the current vision and processing states, as well as to review their respective histories. Intended for use in development or debugging environments to facilitate real-time state inspection and manipulation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CascadeZOrderPlayground(
    viewModel: CascadeDebugViewModel = hiltViewModel(),
) {
    var newVisionState by remember { mutableStateOf(VisionState()) }
    var newProcessingState by remember { mutableStateOf(ProcessingState()) }

    // Collect StateFlow values safely using collectAsState()
    val visionState by viewModel.visionState.collectAsState()
    val processingState by viewModel.processingState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cascade State Debugger",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Vision State",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Current State: $visionState",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = newVisionState.toString(),
                    onValueChange = {
                        // Parse and update vision state
                    },
                    label = { Text("New Vision State") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { viewModel.updateVisionState(newVisionState) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Vision State")
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Processing State",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Current State: $processingState",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = newProcessingState.toString(),
                    onValueChange = {
                        // Parse and update processing state
                    },
                    label = { Text("New Processing State") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { viewModel.updateProcessingState(newProcessingState) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Processing State")
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "State History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    item {
                        Text(
                            text = "Vision History (Not implemented)",
                            style = MaterialTheme.typography.titleSmall
                        )
                        /*
                        visionState.history?.forEach { entry ->
                            Text(text = "- $entry")
                        }
                        */
                    }
                    item {
                        Text(
                            text = "Processing History (Not implemented)",
                            style = MaterialTheme.typography.titleSmall
                        )
                        /*
                        processingState.history?.forEach { entry ->
                            Text(text = "- $entry")
                        }
                        */
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Displays a preview of the Cascade state debugger UI within a Material theme.
 *
 * This composable enables IDE preview of the `CascadeZOrderPlayground` for design and layout inspection.
 */
@Composable
@Preview(showBackground = true)
fun CascadeZOrderPlaygroundPreview() {
    MaterialTheme {
        CascadeZOrderPlayground()
    }
}

