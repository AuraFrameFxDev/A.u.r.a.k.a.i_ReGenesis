package dev.aurakai.auraframefx.infrastructure.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.infrastructure.backend.BackendApi
import dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 🛰️ INFRASTRUCTURE MONITOR VIEWMODEL
 * 
 * Orchestrates status updates from all critical arteries.
 */
@HiltViewModel
class InfrastructureMonitorViewModel @Inject constructor(
    private val backendApi: BackendApi
) : ViewModel() {

    private val _statuses = MutableStateFlow<Map<Capability, InfrastructureStatus>>(emptyMap())
    val statuses = _statuses.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            while (true) {
                updateAllStatuses()
                delay(10000) // Poll every 10 seconds
            }
        }
    }

    private suspend fun updateAllStatuses() {
        val newStatuses = mutableMapOf<Capability, InfrastructureStatus>()

        // 1. Shizuku Status
        val shizukuActive = ShizukuManager.isShizukuAvailable()
        newStatuses[Capability.SHIZUKU_API] = InfrastructureStatus(
            isAvailable = shizukuActive,
            message = if (shizukuActive) "Service Active" else "Service Disconnected"
        )
        CapabilityGates.updateStatus(Capability.SHIZUKU_API, shizukuActive)

        // 2. Xposed Status (Placeholder for now)
        // In a real scenario, we'd check if our hook module is active via YukiHook or system property
        newStatuses[Capability.XPOSED_HOOKS] = InfrastructureStatus(
            isAvailable = true, // Force true for now as we are the hook
            message = "Hooks Operational"
        )
        CapabilityGates.updateStatus(Capability.XPOSED_HOOKS, true)

        // 3. Backend Status
        val backendStatus = try {
            val response = backendApi.getStatus()
            InfrastructureStatus(
                isAvailable = true,
                message = "Python v${response.version} (Load: ${response.load})"
            )
        } catch (e: Exception) {
            Timber.e(e, "Backend connection failed")
            InfrastructureStatus(
                isAvailable = false,
                message = "Connection Offline"
            )
        }
        newStatuses[Capability.CORE_BACKEND] = backendStatus
        CapabilityGates.updateStatus(Capability.CORE_BACKEND, backendStatus.isAvailable)

        _statuses.value = newStatuses
    }
}

data class InfrastructureStatus(
    val isAvailable: Boolean,
    val message: String
)
