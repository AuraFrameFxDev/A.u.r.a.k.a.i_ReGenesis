package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.RootShellService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RootToolsViewModel @Inject constructor(
    private val rootShellService: RootShellService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RootToolsUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleMagisk() {
        _uiState.update { it.copy(magiskEnabled = !it.magiskEnabled) }
        // TODO: Injected rootShellService execution
    }

    fun toggleBootloader() {
        _uiState.update { it.copy(bootloaderUnlocked = !it.bootloaderUnlocked) }
    }

    fun toggleSystemRw() {
        _uiState.update { it.copy(systemRwEnabled = !it.systemRwEnabled) }
    }
}

data class RootToolsUiState(
    val magiskEnabled: Boolean = false,
    val bootloaderUnlocked: Boolean = false,
    val systemRwEnabled: Boolean = false
)
