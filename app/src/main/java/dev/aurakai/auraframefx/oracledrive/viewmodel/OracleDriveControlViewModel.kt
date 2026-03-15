package dev.aurakai.auraframefx.oracledrive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.OracleDriveServiceConnector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Oracle Drive Control Screen.
 * Orchestrates the connection to the Oracle Drive IPC service.
 */
class OracleDriveControlViewModel(application: Application) : AndroidViewModel(application) {

    private val connector = OracleDriveServiceConnector(application)

    val isServiceConnected: StateFlow<Boolean> = connector.isServiceConnected

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    private val _detailedStatus = MutableStateFlow<String?>(null)
    val detailedStatus: StateFlow<String?> = _detailedStatus.asStateFlow()

    private val _diagnosticsLog = MutableStateFlow<String?>(null)
    val diagnosticsLog: StateFlow<String?> = _diagnosticsLog.asStateFlow()

    fun bindService() {
        connector.bindService()
    }

    fun unbindService() {
        connector.unbindService()
    }

    fun refreshStatus() {
        viewModelScope.launch {
            _status.value = connector.getStatusFromOracleDrive()
            _detailedStatus.value = connector.getDetailedInternalStatus()
            _diagnosticsLog.value = connector.getInternalDiagnosticsLog()
        }
    }

    suspend fun toggleModule(packageName: String, enable: Boolean) {
        val result = connector.toggleModuleOnOracleDrive(packageName, enable)
        // Refresh after toggle to reflect changes
        refreshStatus()
        if (result == null || result == "Failed") {
            throw Exception("Failed to toggle module: $packageName")
        }
    }
}
