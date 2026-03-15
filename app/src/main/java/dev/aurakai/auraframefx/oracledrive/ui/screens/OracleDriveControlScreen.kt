package dev.aurakai.auraframefx.oracledrive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.oracledrive.viewmodel.OracleDriveControlViewModel
import dev.aurakai.auraframefx.R
import kotlinx.coroutines.launch

/**
 * Displays the Oracle Drive control screen with UI controls and status information for managing the Oracle Drive service.
 */
@Composable
fun OracleDriveControlScreen(
    viewModel: OracleDriveControlViewModel = viewModel(),
) {
    val context = LocalContext.current
    val isConnected by viewModel.isServiceConnected.collectAsState()
    val status by viewModel.status.collectAsState()
    val detailedStatus by viewModel.detailedStatus.collectAsState()
    val diagnosticsLog by viewModel.diagnosticsLog.collectAsState()
    
    var packageName by remember { mutableStateOf(TextFieldValue("")) }
    var enableModule by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val logScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    // --- UI Logic ---
    fun performRefresh() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                viewModel.refreshStatus()
            } catch (e: Exception) {
                errorMessage = "Refresh failed: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun performToggle() {
        if (packageName.text.isBlank()) return
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                viewModel.toggleModule(packageName.text, enableModule)
            } catch (e: Exception) {
                errorMessage = "Toggle failed: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isConnected) stringResource(R.string.oracle_drive_connected) 
                   else stringResource(R.string.oracle_drive_not_connected),
            style = MaterialTheme.typography.titleLarge,
            color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = { performRefresh() },
            enabled = isConnected && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.refresh_status))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(stringResource(R.string.status_label, status ?: "Unknown"), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.detailed_status_label, detailedStatus ?: "No details"), style = MaterialTheme.typography.bodySmall)

        Text("Diagnostics Log", style = MaterialTheme.typography.labelLarge)
        Surface(
            modifier = Modifier.height(150.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = diagnosticsLog ?: "Log empty",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(8.dp).verticalScroll(logScrollState)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Module Management", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = packageName,
            onValueChange = { packageName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Package Name") },
            placeholder = { Text("com.example.module") },
            singleLine = true,
            enabled = isConnected && !isLoading
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable Module")
            Switch(
                checked = enableModule,
                onCheckedChange = { enableModule = it },
                enabled = isConnected && !isLoading
            )
        }

        Button(
            onClick = { performToggle() },
            enabled = isConnected && packageName.text.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (enableModule) "Apply Enable" else "Apply Disable")
        }
    }
}
