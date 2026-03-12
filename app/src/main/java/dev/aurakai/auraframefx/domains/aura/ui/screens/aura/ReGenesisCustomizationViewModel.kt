package dev.aurakai.auraframefx.domains.aura.ui.screens.aura

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.lab.ReGenesisCustomizationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ⚙️ REGENESIS CUSTOMIZATION VIEWMODEL
 *
 * Manages the global state for all system customizations including
 * Iconify, ColorBlendr, and PixelLauncherEnhanced.
 * Handles persistence via SharedPreferences/DataStore.
 */
@HiltViewModel
class ReGenesisCustomizationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ReGenesisCustomizationConfig())
    val state = _state.asStateFlow()

    // Persistence and logic for updating specific customization categories
    // will be implemented here as the integration deepens.

    fun toggleIconify(enabled: Boolean) {
        _state.value = _state.value.copy(iconifyEnabled = enabled)
    }

    fun toggleColorBlendr(enabled: Boolean) {
        _state.value = _state.value.copy(colorBlendrEnabled = enabled)
    }

    fun togglePixelLauncherEnhanced(enabled: Boolean) {
        _state.value = _state.value.copy(pixelLauncherEnhancedEnabled = enabled)
    }
}

