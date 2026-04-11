package dev.aurakai.auraframefx.domains.aura.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.di.AuraSettingsDataStore
import dev.aurakai.auraframefx.domains.aura.chromacore.engine.ChromaCoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── UI State DTOs ────────────────────────────────────────────────────────────

data class StatusBarState(
    val showIcons: Boolean = true,
    val clockFormat: String = "24h",
    val batteryStyle: String = "Icon",
    val networkStyle: String = "Detailed",
    val backgroundTransparent: Boolean = false
)

data class UISettingsState(
    val isSidebarVisible: Boolean = true,
    val isNotchbarVisible: Boolean = true,
    val isStatusBarVisible: Boolean = true,
    val isBottomNavVisible: Boolean = true,
    val isGlowEffectsEnabled: Boolean = true,
    val isPixelArtEnabled: Boolean = true,
    val isDarkMode: Boolean = true
)

data class QuickSettingsState(
    val layout: String = "Grid",
    val showLabels: Boolean = true,
    val tileSize: String = "Medium",
    val autoCollapse: Boolean = true,
    val enabledTiles: Set<String> = setOf(
        "WiFi", "Bluetooth", "Mobile Data", "Flashlight",
        "Screen Rotation", "Dark Mode", "Aura Overlay"
    )
)

data class OverlayMenuState(
    val auraOverlayEnabled: Boolean = true,
    val kaiOverlayEnabled: Boolean = false,
    val chatBubbleEnabled: Boolean = true,
    val sidebarPosition: String = "Right"
)

// ─── DataStore keys ───────────────────────────────────────────────────────────

private object AuraUIKeys {
    val SHOW_ICONS = booleanPreferencesKey("aura_ui_statusbar_show_icons")
    val CLOCK_FORMAT = stringPreferencesKey("aura_ui_statusbar_clock_format")
    val BATTERY_STYLE = stringPreferencesKey("aura_ui_statusbar_battery_style")
    val NETWORK_STYLE = stringPreferencesKey("aura_ui_statusbar_network_style")
    val BG_TRANSPARENT = booleanPreferencesKey("aura_ui_statusbar_bg_transparent")

    val SIDEBAR_VISIBLE = booleanPreferencesKey("aura_ui_sidebar_visible")
    val NOTCHBAR_VISIBLE = booleanPreferencesKey("aura_ui_notchbar_visible")
    val STATUSBAR_VISIBLE = booleanPreferencesKey("aura_ui_statusbar_visible")
    val BOTTOM_NAV_VISIBLE = booleanPreferencesKey("aura_ui_bottom_nav_visible")
    val GLOW_EFFECTS = booleanPreferencesKey("aura_ui_glow_effects")
    val PIXEL_ART = booleanPreferencesKey("aura_ui_pixel_art")
    val DARK_MODE = booleanPreferencesKey("aura_ui_dark_mode")

    val QS_LAYOUT = stringPreferencesKey("aura_ui_qs_layout")
    val QS_SHOW_LABELS = booleanPreferencesKey("aura_ui_qs_show_labels")
    val QS_TILE_SIZE = stringPreferencesKey("aura_ui_qs_tile_size")
    val QS_AUTO_COLLAPSE = booleanPreferencesKey("aura_ui_qs_auto_collapse")
    val QS_ENABLED_TILES = stringPreferencesKey("aura_ui_qs_enabled_tiles") // CSV

    val AURA_OVERLAY = booleanPreferencesKey("aura_ui_aura_overlay")
    val KAI_OVERLAY = booleanPreferencesKey("aura_ui_kai_overlay")
    val CHAT_BUBBLE = booleanPreferencesKey("aura_ui_chat_bubble")
    val SIDEBAR_POSITION = stringPreferencesKey("aura_ui_sidebar_position")
}

/**
 * AuraUIControlViewModel — Aura owns every UI toggle.
 *
 * Every state change is:
 *   1. Persisted to "aura_settings" DataStore (survives rotation + relaunch)
 *   2. Forwarded to the system-level hook pipeline via ChromaCoreManager when the
 *      setting needs Xposed / root application (status bar battery style, clock, etc.)
 *
 * Screens connect with `viewModel: AuraUIControlViewModel = hiltViewModel()` — no
 * local remember{mutableStateOf} needed.
 */
@HiltViewModel
class AuraUIControlViewModel @Inject constructor(
    @AuraSettingsDataStore private val dataStore: DataStore<Preferences>,
    private val chromaCoreManager: ChromaCoreManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Xposed bridge shared-prefs (world-readable, read by ChromaCoreHooker)
    private val xprefs: SharedPreferences by lazy {
        @Suppress("WorldReadableFiles")
        try {
            context.getSharedPreferences("chromacore_xposed_prefs", Context.MODE_PRIVATE)
        } catch (_: Exception) {
            context.getSharedPreferences("chromacore_xposed_prefs", Context.MODE_PRIVATE)
        }
    }

    // ─── Status Bar ───────────────────────────────────────────────────────────

    val statusBarState: StateFlow<StatusBarState> = dataStore.data.map { prefs ->
        StatusBarState(
            showIcons = prefs[AuraUIKeys.SHOW_ICONS] ?: true,
            clockFormat = prefs[AuraUIKeys.CLOCK_FORMAT] ?: "24h",
            batteryStyle = prefs[AuraUIKeys.BATTERY_STYLE] ?: "Icon",
            networkStyle = prefs[AuraUIKeys.NETWORK_STYLE] ?: "Detailed",
            backgroundTransparent = prefs[AuraUIKeys.BG_TRANSPARENT] ?: false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatusBarState())

    fun setShowIcons(show: Boolean) = persistAndApply(AuraUIKeys.SHOW_ICONS, show) {
        xprefs.edit().putBoolean("statusbar_show_icons", show).apply()
    }

    fun setClockFormat(format: String) = persistAndApply(AuraUIKeys.CLOCK_FORMAT, format) {
        // "12h" → 0, "24h" → 1 (Iconify convention)
        xprefs.edit().putInt("statusbar_clock_format", if (format == "12h") 0 else 1).apply()
    }

    fun setBatteryStyle(style: String) = persistAndApply(AuraUIKeys.BATTERY_STYLE, style) {
        val styleIdx = listOf("Icon", "Percentage", "Icon + %", "Hidden").indexOf(style)
        xprefs.edit().putInt("statusbar_battery_style", styleIdx.coerceAtLeast(0)).apply()
    }

    fun setNetworkStyle(style: String) = persistAndApply(AuraUIKeys.NETWORK_STYLE, style) {
        xprefs.edit().putString("statusbar_network_style", style.lowercase()).apply()
    }

    fun setStatusBarBgTransparent(transparent: Boolean) =
        persistAndApply(AuraUIKeys.BG_TRANSPARENT, transparent) {
            xprefs.edit().putBoolean("statusbar_bg_transparent", transparent).apply()
        }

    // ─── UI Visibility ────────────────────────────────────────────────────────

    val uiSettingsState: StateFlow<UISettingsState> = dataStore.data.map { prefs ->
        UISettingsState(
            isSidebarVisible = prefs[AuraUIKeys.SIDEBAR_VISIBLE] ?: true,
            isNotchbarVisible = prefs[AuraUIKeys.NOTCHBAR_VISIBLE] ?: true,
            isStatusBarVisible = prefs[AuraUIKeys.STATUSBAR_VISIBLE] ?: true,
            isBottomNavVisible = prefs[AuraUIKeys.BOTTOM_NAV_VISIBLE] ?: true,
            isGlowEffectsEnabled = prefs[AuraUIKeys.GLOW_EFFECTS] ?: true,
            isPixelArtEnabled = prefs[AuraUIKeys.PIXEL_ART] ?: true,
            isDarkMode = prefs[AuraUIKeys.DARK_MODE] ?: true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UISettingsState())

    fun setSidebarVisible(v: Boolean) = persist(AuraUIKeys.SIDEBAR_VISIBLE, v)
    fun setNotchbarVisible(v: Boolean) = persist(AuraUIKeys.NOTCHBAR_VISIBLE, v)
    fun setStatusBarVisible(v: Boolean) = persist(AuraUIKeys.STATUSBAR_VISIBLE, v)
    fun setBottomNavVisible(v: Boolean) = persist(AuraUIKeys.BOTTOM_NAV_VISIBLE, v)
    fun setGlowEffects(v: Boolean) = persist(AuraUIKeys.GLOW_EFFECTS, v)
    fun setPixelArt(v: Boolean) = persist(AuraUIKeys.PIXEL_ART, v)
    fun setDarkMode(v: Boolean) = persist(AuraUIKeys.DARK_MODE, v)

    // ─── Quick Settings ───────────────────────────────────────────────────────

    val quickSettingsState: StateFlow<QuickSettingsState> = dataStore.data.map { prefs ->
        val tilesCSV = prefs[AuraUIKeys.QS_ENABLED_TILES] ?: ""
        val tiles = if (tilesCSV.isBlank())
            setOf("WiFi", "Bluetooth", "Mobile Data", "Flashlight", "Screen Rotation", "Dark Mode", "Aura Overlay")
        else
            tilesCSV.split(",").filter { it.isNotBlank() }.toSet()

        QuickSettingsState(
            layout = prefs[AuraUIKeys.QS_LAYOUT] ?: "Grid",
            showLabels = prefs[AuraUIKeys.QS_SHOW_LABELS] ?: true,
            tileSize = prefs[AuraUIKeys.QS_TILE_SIZE] ?: "Medium",
            autoCollapse = prefs[AuraUIKeys.QS_AUTO_COLLAPSE] ?: true,
            enabledTiles = tiles
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QuickSettingsState())

    fun setQsLayout(layout: String) = persist(AuraUIKeys.QS_LAYOUT, layout)
    fun setQsShowLabels(v: Boolean) = persist(AuraUIKeys.QS_SHOW_LABELS, v)
    fun setQsTileSize(size: String) = persist(AuraUIKeys.QS_TILE_SIZE, size)
    fun setQsAutoCollapse(v: Boolean) = persist(AuraUIKeys.QS_AUTO_COLLAPSE, v)

    fun toggleQsTile(tileName: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val current = (prefs[AuraUIKeys.QS_ENABLED_TILES] ?: "")
                    .split(",").filter { it.isNotBlank() }.toMutableSet()
                if (tileName in current) current.remove(tileName) else current.add(tileName)
                prefs[AuraUIKeys.QS_ENABLED_TILES] = current.joinToString(",")
            }
        }
    }

    // ─── Overlay / Floating UI ────────────────────────────────────────────────

    val overlayMenuState: StateFlow<OverlayMenuState> = dataStore.data.map { prefs ->
        OverlayMenuState(
            auraOverlayEnabled = prefs[AuraUIKeys.AURA_OVERLAY] ?: true,
            kaiOverlayEnabled = prefs[AuraUIKeys.KAI_OVERLAY] ?: false,
            chatBubbleEnabled = prefs[AuraUIKeys.CHAT_BUBBLE] ?: false,
            sidebarPosition = prefs[AuraUIKeys.SIDEBAR_POSITION] ?: "Right"
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OverlayMenuState())

    fun setAuraOverlayEnabled(v: Boolean) = persist(AuraUIKeys.AURA_OVERLAY, v)
    fun setKaiOverlayEnabled(v: Boolean) = persist(AuraUIKeys.KAI_OVERLAY, v)
    fun setChatBubbleEnabled(v: Boolean) = persist(AuraUIKeys.CHAT_BUBBLE, v)
    fun setSidebarPosition(pos: String) = persist(AuraUIKeys.SIDEBAR_POSITION, pos)

    // ─── Internal helpers ─────────────────────────────────────────────────────

    private fun persist(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch { dataStore.edit { it[key] = value } }
    }

    private fun persist(key: Preferences.Key<String>, value: String) {
        viewModelScope.launch { dataStore.edit { it[key] = value } }
    }

    private fun persistAndApply(
        key: Preferences.Key<Boolean>,
        value: Boolean,
        systemHook: () -> Unit
    ) {
        viewModelScope.launch {
            dataStore.edit { it[key] = value }
            runCatching { systemHook() }
        }
    }

    private fun persistAndApply(
        key: Preferences.Key<String>,
        value: String,
        systemHook: () -> Unit
    ) {
        viewModelScope.launch {
            dataStore.edit { it[key] = value }
            runCatching { systemHook() }
        }
    }
}
