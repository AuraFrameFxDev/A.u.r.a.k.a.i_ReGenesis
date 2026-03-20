package com.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.ipc.OracleDriveServiceConnector
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OracleDriveControlViewModel(application: Application) : AndroidViewModel(application) {
    private val connector = OracleDriveServiceConnector(application)

    val isServiceConnected: StateFlow<Boolean> = connector.isServiceConnected

    private val _status = MutableStateFlow<String?>("-")
    val status: StateFlow<String?> = _status.asStateFlow()

    private val _detailedStatus = MutableStateFlow<String?>("-")
    val detailedStatus: StateFlow<String?> = _detailedStatus.asStateFlow()

    private val _diagnosticsLog = MutableStateFlow<String?>("-")
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
        refreshStatus() // Refresh after toggle
    }
}
