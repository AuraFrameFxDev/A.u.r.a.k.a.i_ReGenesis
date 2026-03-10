package dev.aurakai.auraframefx.oracledrive.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.viewmodel.OracleDriveControlViewModel
import dev.aurakai.auraframefx.R
import kotlinx.coroutines.launch

/**
 * Displays the Oracle Drive control screen with UI controls and status information for managing the Oracle Drive service.
 *
 * This composable shows the service connection status, current and detailed status, diagnostics log, and provides controls to enable or disable modules by package name. It manages service binding and unbinding based on lifecycle events and displays error feedback to the user.
 *
 * @param viewModel The ViewModel supplying state and handling actions for the Oracle Drive control UI.
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
    val viewModelScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    // --- UI logic for actions ---
    fun safeRefresh() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                viewModel.refreshStatus()
            } catch (e: Exception) {
                errorMessage = "Failed to refresh: ${e.localizedMessage ?: e.toString()}"
            } finally {
                isLoading = false
            }
        }
    }

    fun safeToggle() {
        if (packageName.text.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                viewModel.toggleModule(packageName.text, enableModule)
            } catch (e: Exception) {
                errorMessage = "Failed to toggle: ${e.localizedMessage ?: e.toString()}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isConnected) stringResource(R.string.oracle_drive_connected) else stringResource(
                R.string.oracle_drive_not_connected
            ),
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
                onClick = { safeRefresh() },
                enabled = isConnected && !isLoading
            ) {
                Text(stringResource(R.string.refresh_status))
            }
        }
        HorizontalDivider()
        Text(stringResource(R.string.status_label, status ?: "-"))
        Text(stringResource(R.string.detailed_status_label, detailedStatus ?: "-"))
        Text(
            stringResource(R.string.diagnostics_log_label),
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
            stringResource(R.string.toggle_module_label),
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
                label = { Text(stringResource(R.string.module_package_name)) },
                enabled = isConnected && !isLoading
            )
            Switch(
                checked = enableModule,
                onCheckedChange = { enableModule = it },
                enabled = isConnected && !isLoading
            )
            Button(
                onClick = { safeToggle() },
                enabled = isConnected && packageName.text.isNotBlank() && !isLoading,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(stringResource(if (enableModule) R.string.enable else R.string.disable))
            }
        }
    }
}

/**
 * Displays a UI screen for controlling and monitoring the Oracle Drive service.
 *
 * Provides controls to refresh service status, view diagnostics logs, and enable or disable modules by package name. The screen reflects real-time connection status and displays error messages for failed operations.
 */
/**
 * Displays the Oracle Drive control screen, providing UI controls and status information for managing the Oracle Drive service.
 *
 * This composable shows the current connection status, service status, detailed status, diagnostics log, and allows enabling or disabling modules by package name. It also provides refresh and toggle actions, reflecting loading and error states as needed.
 */
/**
 * Displays the Oracle Drive control and monitoring UI.
 *
 * This composable provides controls to connect to the Oracle Drive service, view its status and diagnostics log, refresh service status, and enable or disable modules by package name. It manages service binding and unbinding based on composition lifecycle and displays error messages for failed operations.
 */
/**
 * Displays the Oracle Drive control screen, providing UI controls and status information for managing the Oracle Drive service.
 *
 * This composable shows the service connection status, current and detailed status, diagnostics log, and allows enabling or disabling modules by package name. It manages service binding and unbinding based on the composable lifecycle, and provides error feedback for user actions.
 *
 * @param viewModel The ViewModel that supplies service state and handles control actions for the Oracle Drive service.
 */
/**
 * Displays the Oracle Drive control screen, providing UI controls and status information for managing the Oracle Drive service.
 *
 * The screen shows connection status, service status, detailed status, diagnostics log, and allows enabling or disabling modules by package name. It manages service binding and unbinding based on lifecycle events and provides error feedback to the user.
 *
 * @param viewModel The ViewModel that supplies state and handles actions for the Oracle Drive control UI.
 */
/**
 * Displays the Oracle Drive control screen with UI controls and status information for managing the Oracle Drive service.
 *
 * This composable shows the service connection status, current and detailed status, diagnostics log, and provides controls to enable or disable modules by package name. It manages service binding and unbinding based on lifecycle events and displays error feedback to the user.
 *
 * @param viewModel The ViewModel supplying state and handling actions for the Oracle Drive control UI.
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
    val viewModelScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    // --- UI logic for actions ---
    suspend fun safeRefresh() {
        isLoading = true
        errorMessage = null
        try {
            viewModel.refreshStatus()
        } catch (e: Exception) {
            errorMessage =
                context.getString(R.string.failed_to_refresh, e.localizedMessage ?: e.toString())
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
            errorMessage =
                context.getString(R.string.failed_to_toggle, e.localizedMessage ?: e.toString())
        } finally {
            isLoading = false
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isConnected) stringResource(R.string.oracle_drive_connected) else stringResource(
                R.string.oracle_drive_not_connected
            ),
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
                Text(stringResource(R.string.refresh_status))
            }
        }
        Divider()
        Text(stringResource(R.string.status_label, status ?: "-"))
        Text(stringResource(R.string.detailed_status_label, detailedStatus ?: "-"))
        Text(
            stringResource(R.string.diagnostics_log_label),
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
        Divider()
        Text(
            stringResource(R.string.toggle_module_label),
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
                label = { Text(stringResource(R.string.module_package_name)) },
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
                Text(stringResource(if (enableModule) R.string.enable else R.string.disable))
            }
        }
    }
}
