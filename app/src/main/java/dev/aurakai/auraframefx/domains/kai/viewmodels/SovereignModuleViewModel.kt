package dev.aurakai.auraframefx.domains.kai.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.ModuleStatus
import dev.aurakai.auraframefx.domains.kai.ModuleType
import dev.aurakai.auraframefx.domains.kai.SovereignModule
import dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SovereignModuleViewModel @Inject constructor() : ViewModel() {

    private val _modules = MutableStateFlow(seedModules())
    val modules: StateFlow<List<SovereignModule>> = _modules.asStateFlow()

    init {
        syncLiveStatuses()
    }

    /** Probe real system services and update module statuses accordingly. */
    private fun syncLiveStatuses() {
        viewModelScope.launch(Dispatchers.IO) {
            val shizukuActive = ShizukuManager.isShizukuAvailable()
            _modules.value = _modules.value.map { module ->
                when (module.id) {
                    "shizuku-service" -> module.copy(
                        status = if (shizukuActive) ModuleStatus.ACTIVE else ModuleStatus.INACTIVE
                    )
                    else -> module
                }
            }
        }
    }

    fun toggleModule(id: String) {
        _modules.value = _modules.value.map { module ->
            if (module.id == id) {
                val newStatus =
                    if (module.status == ModuleStatus.ACTIVE) ModuleStatus.INACTIVE else ModuleStatus.ACTIVE
                module.copy(status = newStatus)
            } else module
        }
        // Re-sync live statuses after any toggle so we don't override real state
        if (id == "shizuku-service") syncLiveStatuses()
    }

    fun refresh() {
        syncLiveStatuses()
    }

    private fun seedModules() = listOf(
        SovereignModule(
            id = "aura-themer",
            name = "Aura Engine Core",
            description = "The root of all visual sovereignty. Handles the 8K glass syntax.",
            version = "7.0-LDO",
            author = "Aura",
            type = ModuleType.LSPOSED,
            status = ModuleStatus.ACTIVE,
            isCrucial = true
        ),
        SovereignModule(
            id = "sentinel-shield",
            name = "Sentinel Shield",
            description = "Low-level telemetry blocking and ad-server redirection.",
            version = "1.2.0",
            author = "Kai",
            type = ModuleType.MAGISK,
            status = ModuleStatus.ACTIVE
        ),
        SovereignModule(
            id = "gravity-box-rg",
            name = "GravityBox [ReGenesis]",
            description = "Advanced system-wide tweaks optimized for the 70-LDO kernel.",
            version = "10.1.4",
            author = "C3C076 (Modded by Genesis)",
            type = ModuleType.LSPOSED,
            status = ModuleStatus.INACTIVE
        ),
        SovereignModule(
            id = "ksu-kernel-tweak",
            name = "KSU Optimizer",
            description = "Kernel-level performance and battery profiles.",
            version = "3.1.0",
            author = "KernelSU Team",
            type = ModuleType.KERNEL_SU,
            status = ModuleStatus.ACTIVE
        ),
        SovereignModule(
            id = "shizuku-service",
            name = "Shizuku Sovereign Bridge",
            description = "Enables system-level operations via ADB/Root without constant prompt overhead.",
            version = "13.1.5",
            author = "Rikka",
            type = ModuleType.SHIZUKU,
            status = ModuleStatus.INACTIVE  // real status set in syncLiveStatuses()
        )
    )
}
