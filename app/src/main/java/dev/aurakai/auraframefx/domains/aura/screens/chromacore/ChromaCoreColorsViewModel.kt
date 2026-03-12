package dev.aurakai.auraframefx.domains.aura.screens.chromacore

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ChromaCoreColorsViewModel - Orchestrates system-wide color application.
 *
 * Bridges the UI color selection with the RootOverlayManager to apply
 * changes to the entire Android OS.
 */
@HiltViewModel
class ChromaCoreColorsViewModel @Inject constructor(
    private val overlayManager: SystemOverlayManager
) : ViewModel() {

    /**
     * Converts the selected Compose Color to a hex string and triggers
     * a system-wide fabricated overlay update.
     */
    fun applySystemWideAccent(color: Color) {
        viewModelScope.launch {
            try {
                // Formatting to #AARRGGBB hex for the shell command
                val argb = color.toArgb()
                val hex = String.format("#%08X", argb)

                Timber.d("Requesting system-wide accent change to: $hex")
                val result = overlayManager.applyAccent(hex)

                if (result.isSuccess) {
                    Timber.i("Successfully applied system accent: $hex")
                } else {
                    Timber.e("Failed to apply system accent: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in applySystemWideAccent")
            }
        }
    }

    /**
     * Updates the system background saturation level.
     */
    fun updateSystemSaturation(percent: Int) {
        viewModelScope.launch {
            overlayManager.applyBackgroundSaturation(percent)
        }
    }

    /**
     * Clears all fabricated overlays to restore system defaults.
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            overlayManager.clearAll()
        }
    }
}

