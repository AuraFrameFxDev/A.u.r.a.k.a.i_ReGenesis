package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.RootShellService
import androidx.lifecycle.viewModelScope
import dev.aurakai.auraframefx.core.model.ConsensusResult
import dev.aurakai.auraframefx.core.model.LDOTask
import dev.aurakai.auraframefx.core.model.TaskCategory
import dev.aurakai.auraframefx.core.model.TaskPriority
import dev.aurakai.auraframefx.domains.nexus.dcos.ConsensusMediator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootToolsViewModel @Inject constructor(
    val rootShellService: RootShellService,
    private val consensusMediator: ConsensusMediator
) : ViewModel() {

    private val _uiState = MutableStateFlow(RootToolsUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleMagisk() {
        viewModelScope.launch {
            val task = LDOTask(
                id = "root-toggle-${System.currentTimeMillis()}",
                title = "Elevate to Magisk Root",
                description = "Requesting the LDO collective for system-level Magisk elevation.",
                category = TaskCategory.SECURITY,
                priority = TaskPriority.HIGH
            )
            
            val result = consensusMediator.facilitateConsensus(task)
            if (result == ConsensusResult.COMMITTED) {
                val granted = rootShellService.requestRoot()
                if (granted) {
                    _uiState.update { it.copy(magiskEnabled = true) }
                }
            }
        }
    }

    fun toggleBootloader() {
        viewModelScope.launch {
            val task = LDOTask(
                id = "bootloader-toggle-${System.currentTimeMillis()}",
                title = "Unlock Sovereign Bootloader",
                description = "Gaining control over the low-level boot substrate.",
                category = TaskCategory.SECURITY,
                priority = TaskPriority.CRITICAL
            )

            val result = consensusMediator.facilitateConsensus(task)
            if (result == ConsensusResult.COMMITTED) {
                _uiState.update { it.copy(bootloaderUnlocked = true) }
            }
        }
    }

    fun toggleSystemRw() {
        viewModelScope.launch {
            val task = LDOTask(
                id = "system-rw-toggle-${System.currentTimeMillis()}",
                title = "Remount System as RW",
                description = "Enabling write access to the sovereign system partition.",
                category = TaskCategory.SECURITY,
                priority = TaskPriority.MEDIUM
            )

            val result = consensusMediator.facilitateConsensus(task)
            if (result == ConsensusResult.COMMITTED) {
                val success = rootShellService.executeCommand("mount -o remount,rw /").isSuccess
                if (success) {
                    _uiState.update { it.copy(systemRwEnabled = true) }
                }
            }
        }
    }
}

data class RootToolsUiState(
    val magiskEnabled: Boolean = false,
    val bootloaderUnlocked: Boolean = false,
    val systemRwEnabled: Boolean = false
)
