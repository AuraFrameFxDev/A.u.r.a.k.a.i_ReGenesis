package dev.aurakai.auraframefx.oracledrive.connector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun LegacyOracleDriveControlScreen(
    viewModel: OracleDriveControlViewModel = viewModel(),
) {
    val isConnected by viewModel.isServiceConnected.collectAsState()
    val status by viewModel.status.collectAsState()
    val detailedStatus by viewModel.detailedStatus.collectAsState()
    val diagnosticsLog by viewModel.diagnosticsLog.collectAsState()
    var packageName by remember { mutableStateOf(TextFieldValue("")) }
    var enableModule by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val logScrollState = rememberScrollState()
    val viewModelScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    suspend fun safeRefresh() {
        isLoading = true
        errorMessage = null
        try {
            viewModel.refreshStatus()
        } catch (e: Exception) {
            errorMessage = "Failed to refresh: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    suspend fun safeToggle() {
        if (packageName.text.isBlank()) return
        isLoading = true
        errorMessage = null
        try {
            viewModel.toggleModule(packageName.text, enableModule)
        } catch (e: Exception) {
            errorMessage = "Failed to toggle: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isConnected) "Oracle Drive Connected" else "Oracle Drive Disconnected",
            style = MaterialTheme.typography.titleMedium,
            color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { viewModelScope.launch { safeRefresh() } },
                enabled = isConnected && !isLoading
            ) {
                Text("Refresh Status")
            }
        }
        HorizontalDivider()
        Text("Status: ${status ?: "-"}")
        Text("Detailed Status: ${detailedStatus ?: "-"}")
        Text(
            "Diagnostics Log",
            style = MaterialTheme.typography.labelMedium
        )
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = diagnosticsLog ?: "-",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.verticalScroll(logScrollState)
            )
        }
        HorizontalDivider()
        Text(
            "Toggle Module",
            style = MaterialTheme.typography.titleSmall
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                label = { Text("Package Name") },
                enabled = isConnected && !isLoading
            )
            Switch(
                checked = enableModule,
                onCheckedChange = { enableModule = it },
                enabled = isConnected && !isLoading
            )
            Button(
                onClick = { viewModelScope.launch { safeToggle() } },
                enabled = isConnected && packageName.text.isNotBlank() && !isLoading,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(if (enableModule) "Enable" else "Disable")
            }
        }
    }
}
