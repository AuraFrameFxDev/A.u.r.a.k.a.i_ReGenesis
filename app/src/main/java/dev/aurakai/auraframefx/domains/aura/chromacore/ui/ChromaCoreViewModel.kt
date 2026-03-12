package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.engine.ChromaCoreConfig
import dev.aurakai.auraframefx.domains.aura.chromacore.engine.ChromaCoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🎨 CHROMA CORE VIEW MODEL
 * Manages the UI state for the unified UXUI engine.
 */
@HiltViewModel
class ChromaCoreViewModel @Inject constructor(
    private val manager: ChromaCoreManager
) : ViewModel() {

    // Persistent settings from DataStore
    val settings: StateFlow<ChromaCoreConfig> = manager.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChromaCoreConfig()
    )

    /**
     * Updates and applies a setting.
     */
    fun updateSettings(update: ChromaCoreConfig.() -> ChromaCoreConfig) {
        viewModelScope.launch {
            val current = settings.value
            val next = current.update()
            // Save to DataStore logic will be called by manager
            // manager.saveSettings(next)
            manager.applyConfiguration(next)
        }
    }

    /**
     * Toggles the statusbar logo.
     */
    fun toggleStatusbarLogo(enabled: Boolean) {
        viewModelScope.launch {
            manager.updateStatusbarLogo(enabled)
            manager.applyConfiguration(settings.value.copy(statusbarLogoEnabled = enabled))
        }
    }

    /**
     * Refreshes colors using the selected seed.
     */
    fun updateSeedColor(color: Int) {
        viewModelScope.launch {
            val next = settings.value.copy(themeSeedColor = color)
            manager.applyConfiguration(next)
        }
    }
}
