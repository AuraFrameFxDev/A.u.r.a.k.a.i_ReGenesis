package dev.aurakai.auraframefx.hotswap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * HotSwapViewModel - ViewModel for HotSwap Screen
 *
 * Manages hot-swap operations and exposes configuration state to the UI.
 */
@HiltViewModel
class HotSwapViewModel @Inject constructor(
    private val hotSwapManager: HotSwapManager
) : ViewModel() {

    // Expose state from manager
    val currentConfig: StateFlow<HotSwapConfig> = hotSwapManager.currentConfig
    val gateConfigs: StateFlow<List<GateConfig>> = hotSwapManager.gateConfigs
    val assetBundles: StateFlow<List<AssetBundle>> = hotSwapManager.assetBundles

    init {
        Timber.d("HotSwapViewModel: Initialized")
        loadConfigurations()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONFIGURATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    fun loadConfigurations() {
        viewModelScope.launch {
            hotSwapManager.loadConfigurations()
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            hotSwapManager.resetToDefaults()
            Timber.i("HotSwapViewModel: Reset to defaults")
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GATE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    fun toggleGateVisibility(gateId: String) {
        viewModelScope.launch {
            hotSwapManager.toggleGateVisibility(gateId)
        }
    }

    fun moveGate(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val currentGates = gateConfigs.value.toMutableList()
            if (fromIndex in currentGates.indices && toIndex in currentGates.indices) {
                val gate = currentGates.removeAt(fromIndex)
                currentGates.add(toIndex, gate)
                val newOrder = currentGates.map { it.id }
                hotSwapManager.reorderGates(newOrder)
            }
        }
    }

    fun addCustomGate(config: GateConfig) {
        viewModelScope.launch {
            hotSwapManager.addCustomGate(config)
        }
    }

    fun removeGate(gateId: String) {
        viewModelScope.launch {
            hotSwapManager.removeGate(gateId)
        }
    }

    fun swapGateConfig(gateId: String, newConfig: GateConfig) {
        viewModelScope.launch {
            hotSwapManager.swapGateConfig(gateId, newConfig)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ASSET OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    fun swapAssetBundle(bundleId: String) {
        viewModelScope.launch {
            hotSwapManager.swapAssetBundle(bundleId)
        }
    }

    fun addAssetBundle(bundle: AssetBundle) {
        viewModelScope.launch {
            hotSwapManager.addAssetBundle(bundle)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // IMPORT/EXPORT
    // ═══════════════════════════════════════════════════════════════════════════

    fun exportConfiguration() {
        viewModelScope.launch {
            val json = hotSwapManager.exportConfiguration()
            Timber.i("HotSwapViewModel: Exported configuration")
            // TODO: Save to file or share
            Timber.d("Export JSON:\n$json")
        }
    }

    fun importConfiguration(json: String) {
        viewModelScope.launch {
            val success = hotSwapManager.importConfiguration(json)
            if (success) {
                Timber.i("HotSwapViewModel: Imported configuration successfully")
            } else {
                Timber.e("HotSwapViewModel: Failed to import configuration")
            }
        }
    }
}
