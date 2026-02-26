package dev.aurakai.auraframefx.domains.aura.chromacore.engine

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.system.ShizukuManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chromacore_settings")

/**
 * 🛰️ CHROMA CORE MANAGER
 * Orchestrates root-level services (Iconify, PLE, ColorBlendr) and persistence.
 */
@Singleton
class ChromaCoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuManager: ShizukuManager
) {
    // Keys for persistence
    private val KEY_STATUSBAR_LOGO = booleanPreferencesKey("statusbar_logo_enabled")
    private val KEY_THEME_COLOR = intPreferencesKey("theme_seed_color")

    val settings: Flow<ChromaCoreConfig> = context.dataStore.data.map { prefs ->
        ChromaCoreConfig(
            statusbarLogoEnabled = prefs[KEY_STATUSBAR_LOGO] ?: false,
            themeSeedColor = prefs[KEY_THEME_COLOR] ?: 0xFF6200EE.toInt()
        )
    }

    /**
     * Applies the current configuration to the system using the best available method.
     */
    suspend fun applyConfiguration(config: ChromaCoreConfig) {
        Timber.d("🎨 ChromaCore: Applying Configuration...")

        // 1. Apply Colors (Fabricated Overlays via Root/Shizuku)
        applyColorEngine(config)

        // 2. Apply UI Tweaks (RRO Overlays)
        applyUiTweaks(config)

        // 3. Update Xposed Preferences (XPref file)
        updateXposedPrefs(config)

        // 4. Restart affected components if necessary
        restartSystemUI()
    }

    private fun applyColorEngine(config: ChromaCoreConfig) {
        if (!config.useDynamicColors) return

        // Example: cmd overlay fabricator ...
        val colorHex = String.format("#%06X", 0xFFFFFF and config.themeSeedColor)
        executeCommand("cmd overlay fabricator create --name ChromaCoreAccent --package android --target android --type 0x1c --value $colorHex")
        executeCommand("cmd overlay enable --user current android:ChromaCoreAccent")
    }

    private fun applyUiTweaks(config: ChromaCoreConfig) {
        // Example: Enable Iconify-style RRO overlays
        if (config.statusbarLogoEnabled) {
            executeCommand("cmd overlay enable --user current com.aurakai.chromacore.statusbar.logo")
        } else {
            executeCommand("cmd overlay disable --user current com.aurakai.chromacore.statusbar.logo")
        }
    }

    private fun updateXposedPrefs(config: ChromaCoreConfig) {
        // Write to a shared preference file that Xposed hooks can read
        // This is the bridge between the UI/Manager and the Xposed Hooker
        val xprefs = context.getSharedPreferences(
            "chromacore_xposed_prefs",
            Context.MODE_WORLD_READABLE or Context.MODE_PRIVATE
        )
        xprefs.edit().apply {
            putBoolean("statusbar_logo_enabled", config.statusbarLogoEnabled)
            putInt("theme_seed_color", config.themeSeedColor)
            putString("launcher_grid_config", config.launcherGridConfig)
            apply()
        }
        Timber.d("ChromaCore: Xposed preferences synchronized.")
    }

    private fun restartSystemUI() {
        Timber.i("🎨 ChromaCore: Restarting SystemUI for changes to take effect.")
        executeCommand("pkill -f com.android.systemui")
    }

    /**
     * Executes a command with Root or Shizuku priority.
     */
    fun executeCommand(command: String): Boolean {
        return try {
            if (Shell.isAppGrantedRoot() == true) {
                Shell.cmd(command).exec().isSuccess
            } else if (shizukuManager.isShizukuAvailable()) {
                // Use shizuku shell if root is not available
                // shizukuManager.runCommand(command)
                false // Stub for now
            } else {
                Timber.w("ChromaCore: No Root/Shizuku available to execute: $command")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "ChromaCore: Command failed: $command")
            false
        }
    }

    suspend fun updateStatusbarLogo(enabled: Boolean) {
        context.dataStore.edit { it[KEY_STATUSBAR_LOGO] = enabled }
    }
}
