package dev.aurakai.auraframefx.system.quicksettings

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Manages Quick Settings configuration including loading, saving, and applying user preferences.
 */
class QuickSettingsConfigManager(private val context: Context) {
    private val tag = "QuickSettingsConfigManager"
    private val gson = Gson()
    private val configFile = File(context.filesDir, "quick_settings_config.json")

    // Default configuration
    private val defaultConfig = QuickSettingsConfig.DEFAULT

    // Cache for the current configuration
    private var currentConfig: QuickSettingsConfig = defaultConfig

    /**
     * Loads the Quick Settings configuration from storage.
     * If no saved configuration exists, returns the default configuration.
     */
    suspend fun loadConfig(): QuickSettingsConfig = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!configFile.exists()) {
                Log.d(tag, "No saved config found, using default")
                return@withContext defaultConfig
            }

            val json = configFile.readText()
            if (json.isBlank()) {
                Log.d(tag, "Empty config file, using default")
                return@withContext defaultConfig
            }

            // Parse the JSON into our config object
            val type = object : TypeToken<QuickSettingsConfig>() {}.type
            gson.fromJson<QuickSettingsConfig>(json, type) ?: defaultConfig
        } catch (e: Exception) {
            Log.e(tag, "Error loading config", e)
            defaultConfig
        }.also {
            currentConfig = it
        }
    }

    /**
     * Saves the Quick Settings configuration to storage.
     */
    suspend fun saveConfig(config: QuickSettingsConfig): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val json = gson.toJson(config)
            configFile.writeText(json)
            currentConfig = config
            true
        } catch (e: Exception) {
            Log.e(tag, "Error saving config", e)
            false
        }
    }

    /**
     * Updates a specific tile's configuration.
     */
    suspend fun updateTileConfig(
        tileId: String,
        update: (QuickSettingsTileConfig) -> QuickSettingsTileConfig,
    ): Boolean {
        return try {
            val currentTiles = currentConfig.tiles.toMutableList()
            val index = currentTiles.indexOfFirst { it.id == tileId }

            if (index != -1) {
                val updatedTile = update(currentTiles[index])
                currentTiles[index] = updatedTile

                val updatedConfig = currentConfig.copy(tiles = currentTiles)
                saveConfig(updatedConfig)
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating tile config", e)
            false
        }
    }

    /**
     * Resets the configuration to default values.
     */
    suspend fun resetToDefault(): Boolean {
        return saveConfig(defaultConfig)
    }

    /**
     * Gets the current configuration.
     */
    fun getCurrentConfig(): QuickSettingsConfig = currentConfig

    /**
     * Applies the current configuration to the Quick Settings panel.
     * This should be called after the Quick Settings panel is inflated.
     */
    fun applyConfig(panel: Any) {
        // This method will be called by the Xposed hook to apply the configuration
        // to the actual Quick Settings panel
        // Implementation will depend on the specific hooks and view hierarchy
    }

    companion object {
        @Volatile
        private var instance: QuickSettingsConfigManager? = null

        fun getInstance(context: Context): QuickSettingsConfigManager {
            return instance ?: synchronized(this) {
                instance ?: QuickSettingsConfigManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
