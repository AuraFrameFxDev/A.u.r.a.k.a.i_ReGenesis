package dev.aurakai.auraframefx.domains.aura.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for UI Recovery Dialog
 *
 * Manages recovery state and user actions:
 * - Reload last change
 * - Reset to default
 * - Dismiss recovery
 */
@HiltViewModel
open class UIRecoveryViewModel @Inject constructor(
    private val recoveryManager: UIRecoveryManager
) : ViewModel() {

    val recoveryState: StateFlow<UIRecoveryState> = recoveryManager.recoveryState

    /**
     * User chose "Reload last change"
     *
     * @return The restored snapshot, or null if restoration failed
     */
    suspend fun reloadLastChange(): UIStateSnapshot? {
        return recoveryManager.reloadLastChange()
    }

    /**
     * User chose "Reset"
     *
     * Clears all state and returns to HOME screen.
     */
    fun resetToDefault() {
        viewModelScope.launch {
            recoveryManager.resetToDefault()
        }
    }

    /**
     * User dismissed recovery dialog without action
     */
    fun dismissRecovery() {
        recoveryManager.dismissRecovery()
    }

    /**
     * Get recovery statistics for diagnostics
     */
    fun getRecoveryStats() = viewModelScope.launch {
        recoveryManager.getRecoveryStats()
        // Could emit to UI for settings/diagnostics screen
    }
}

