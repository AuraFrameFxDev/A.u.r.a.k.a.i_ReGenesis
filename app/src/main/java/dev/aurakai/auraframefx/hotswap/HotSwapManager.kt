package dev.aurakai.auraframefx.hotswap

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HotSwapManager - Dynamic Asset, Gate, and Configuration Swapping
 *
 * Enables runtime swapping of:
 * - Gate configurations and layouts
 * - Asset bundles (themes, icons, images)
 * - Navigation structures
 * - Agent tool configurations
 * - MCP server endpoints
 *
 * All changes are editable from root and persist across restarts.
 *
 * This is the "hot reload" system for the LDO.
 */
@Singleton
class HotSwapManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // State flows for reactive updates
    private val _currentConfig = MutableStateFlow(HotSwapConfig.default())
    val currentConfig: StateFlow<HotSwapConfig> = _currentConfig.asStateFlow()

    private val _gateConfigs = MutableStateFlow<List<GateConfig>>(emptyList())
    val gateConfigs: StateFlow<List<GateConfig>> = _gateConfigs.asStateFlow()

    private val _assetBundles = MutableStateFlow<List<AssetBundle>>(emptyList())
    val assetBundles: StateFlow<List<AssetBundle>> = _assetBundles.asStateFlow()

    // Config file paths (root-editable)
    private val configDir = File("/data/local/tmp/auraframefx/hotswap")
    private val configFile = File(configDir, "hotswap_config.json")
    private val gateConfigsFile = File(configDir, "gate_configs.json")
    private val assetBundlesFile = File(configDir, "asset_bundles.json")

    init {
        // Create config directory if it doesn't exist
        if (!configDir.exists()) {
            configDir.mkdirs()
            Timber.i("HotSwapManager: Created config directory at ${configDir.absolutePath}")
        }

        loadConfigurations()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONFIGURATION LOADING/SAVING
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Load all configurations from disk
     */
    fun loadConfigurations() {
        try {
            // Load main config
            if (configFile.exists()) {
                val configJson = configFile.readText()
                _currentConfig.value = json.decodeFromString<HotSwapConfig>(configJson)
                Timber.i("HotSwapManager: Loaded config from ${configFile.absolutePath}")
            } else {
                _currentConfig.value = HotSwapConfig.default()
                saveConfig()
            }

            // Load gate configs
            if (gateConfigsFile.exists()) {
                val gatesJson = gateConfigsFile.readText()
                _gateConfigs.value = json.decodeFromString<List<GateConfig>>(gatesJson)
                Timber.i("HotSwapManager: Loaded ${_gateConfigs.value.size} gate configs")
            } else {
                _gateConfigs.value = GateConfig.defaults()
                saveGateConfigs()
            }

            // Load asset bundles
            if (assetBundlesFile.exists()) {
                val assetsJson = assetBundlesFile.readText()
                _assetBundles.value = json.decodeFromString<List<AssetBundle>>(assetsJson)
                Timber.i("HotSwapManager: Loaded ${_assetBundles.value.size} asset bundles")
            } else {
                _assetBundles.value = AssetBundle.defaults()
                saveAssetBundles()
            }

        } catch (e: Exception) {
            Timber.e(e, "HotSwapManager: Error loading configurations")
        }
    }

    /**
     * Save main configuration
     */
    fun saveConfig() {
        try {
            val configJson = json.encodeToString(_currentConfig.value)
            configFile.writeText(configJson)
            Timber.i("HotSwapManager: Saved config to ${configFile.absolutePath}")
        } catch (e: Exception) {
            Timber.e(e, "HotSwapManager: Error saving config")
        }
    }

    /**
     * Save gate configurations
     */
    fun saveGateConfigs() {
        try {
            val gatesJson = json.encodeToString(_gateConfigs.value)
            gateConfigsFile.writeText(gatesJson)
            Timber.i("HotSwapManager: Saved gate configs")
        } catch (e: Exception) {
            Timber.e(e, "HotSwapManager: Error saving gate configs")
        }
    }

    /**
     * Save asset bundles
     */
    fun saveAssetBundles() {
        try {
            val assetsJson = json.encodeToString(_assetBundles.value)
            assetBundlesFile.writeText(assetsJson)
            Timber.i("HotSwapManager: Saved asset bundles")
        } catch (e: Exception) {
            Timber.e(e, "HotSwapManager: Error saving asset bundles")
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HOT SWAP OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Swap active gate configuration
     */
    fun swapGateConfig(gateId: String, newConfig: GateConfig) {
        val updatedGates = _gateConfigs.value.toMutableList()
        val index = updatedGates.indexOfFirst { it.id == gateId }
        if (index >= 0) {
            updatedGates[index] = newConfig
        } else {
            updatedGates.add(newConfig)
        }
        _gateConfigs.value = updatedGates
        saveGateConfigs()
        Timber.i("HotSwapManager: Swapped gate config for $gateId")
    }

    /**
     * Swap active asset bundle
     */
    fun swapAssetBundle(bundleId: String) {
        val bundle = _assetBundles.value.firstOrNull { it.id == bundleId }
        if (bundle != null) {
            _currentConfig.value = _currentConfig.value.copy(activeAssetBundleId = bundleId)
            saveConfig()
            Timber.i("HotSwapManager: Swapped to asset bundle: $bundleId")
        } else {
            Timber.w("HotSwapManager: Asset bundle not found: $bundleId")
        }
    }

    /**
     * Toggle gate visibility
     */
    fun toggleGateVisibility(gateId: String) {
        val updatedGates = _gateConfigs.value.map {
            if (it.id == gateId) it.copy(enabled = !it.enabled) else it
        }
        _gateConfigs.value = updatedGates
        saveGateConfigs()
        Timber.i("HotSwapManager: Toggled gate visibility for $gateId")
    }

    /**
     * Reorder gates
     */
    fun reorderGates(newOrder: List<String>) {
        val gateMap = _gateConfigs.value.associateBy { it.id }
        val reordered = newOrder.mapNotNull { gateMap[it] }
        _gateConfigs.value = reordered
        saveGateConfigs()
        Timber.i("HotSwapManager: Reordered gates")
    }

    /**
     * Add custom gate
     */
    fun addCustomGate(config: GateConfig) {
        _gateConfigs.value = _gateConfigs.value + config
        saveGateConfigs()
        Timber.i("HotSwapManager: Added custom gate: ${config.id}")
    }

    /**
     * Remove gate
     */
    fun removeGate(gateId: String) {
        _gateConfigs.value = _gateConfigs.value.filterNot { it.id == gateId }
        saveGateConfigs()
        Timber.i("HotSwapManager: Removed gate: $gateId")
    }

    /**
     * Add asset bundle
     */
    fun addAssetBundle(bundle: AssetBundle) {
        _assetBundles.value = _assetBundles.value + bundle
        saveAssetBundles()
        Timber.i("HotSwapManager: Added asset bundle: ${bundle.id}")
    }

    /**
     * Update configuration setting
     */
    fun updateSetting(key: String, value: Any) {
        val updatedSettings = _currentConfig.value.settings.toMutableMap()
        updatedSettings[key] = value.toString()
        _currentConfig.value = _currentConfig.value.copy(settings = updatedSettings)
        saveConfig()
        Timber.i("HotSwapManager: Updated setting: $key = $value")
    }

    /**
     * Reset to default configuration
     */
    fun resetToDefaults() {
        _currentConfig.value = HotSwapConfig.default()
        _gateConfigs.value = GateConfig.defaults()
        _assetBundles.value = AssetBundle.defaults()
        saveConfig()
        saveGateConfigs()
        saveAssetBundles()
        Timber.i("HotSwapManager: Reset to defaults")
    }

    /**
     * Export configuration as JSON
     */
    fun exportConfiguration(): String {
        val export = HotSwapExport(
            config = _currentConfig.value,
            gates = _gateConfigs.value,
            assetBundles = _assetBundles.value
        )
        return json.encodeToString(export)
    }

    /**
     * Import configuration from JSON
     */
    fun importConfiguration(jsonString: String): Boolean {
        return try {
            val export = json.decodeFromString<HotSwapExport>(jsonString)
            _currentConfig.value = export.config
            _gateConfigs.value = export.gates
            _assetBundles.value = export.assetBundles
            saveConfig()
            saveGateConfigs()
            saveAssetBundles()
            Timber.i("HotSwapManager: Imported configuration")
            true
        } catch (e: Exception) {
            Timber.e(e, "HotSwapManager: Error importing configuration")
            false
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════

/**
 * Main hot swap configuration
 */
@Serializable
data class HotSwapConfig(
    val version: Int = 1,
    val activeAssetBundleId: String = "default",
    val activeThemeId: String = "cyberpunk",
    val quickAccessEnabled: Boolean = true,
    val quickAccessPosition: String = "bottom_right",
    val settings: Map<String, String> = emptyMap()
) {
    companion object {
        fun default() = HotSwapConfig()
    }
}

/**
 * Gate configuration
 */
@Serializable
data class GateConfig(
    val id: String,
    val name: String,
    val route: String,
    val enabled: Boolean = true,
    val icon: String = "default",
    val color: String = "#FFFFFF",
    val order: Int = 0,
    val customItems: List<String> = emptyList()
) {
    companion object {
        fun defaults() = listOf(
            GateConfig("help_gate", "HELP SERVICES", "help_gate", color = "#00FFFF", order = 0),
            GateConfig("aura_gate", "AURA GATE", "aura_gate", color = "#FF1493", order = 1),
            GateConfig("kai_gate", "KAI GATE", "kai_gate", color = "#FF00FF", order = 2),
            GateConfig(
                "genesis_gate",
                "GENESIS GATE",
                "genesis_gate",
                color = "#00D9FF",
                order = 3
            ),
            GateConfig("cascade_gate", "CASCADE HUB", "cascade_gate", color = "#00CED1", order = 4),
            GateConfig("agent_nexus", "AGENT NEXUS", "agent_nexus", color = "#4169E1", order = 5)
        )
    }
}

/**
 * Asset bundle (themes, icons, etc.)
 */
@Serializable
data class AssetBundle(
    val id: String,
    val name: String,
    val description: String,
    val type: String, // "theme", "icons", "mixed"
    val assets: Map<String, String> = emptyMap()
) {
    companion object {
        fun defaults() = listOf(
            AssetBundle("default", "Default Bundle", "Default LDO assets", "mixed"),
            AssetBundle("cyberpunk", "Cyberpunk Theme", "Neon cyberpunk aesthetics", "theme"),
            AssetBundle("minimal", "Minimal Theme", "Clean minimal design", "theme")
        )
    }
}

/**
 * Full configuration export/import
 */
@Serializable
data class HotSwapExport(
    val config: HotSwapConfig,
    val gates: List<GateConfig>,
    val assetBundles: List<AssetBundle>
)
