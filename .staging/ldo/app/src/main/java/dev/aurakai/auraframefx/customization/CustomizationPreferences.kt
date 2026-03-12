package dev.aurakai.auraframefx.customization

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.customizationDataStore by preferencesDataStore(name = "customization_prefs")

/**
 * Persistent storage for user customizations using Jetpack DataStore.
 * Covers themes, glass effects, animations, UI elements, and agent colors.
 */
object CustomizationPreferences {
    // Theme keys
    private val KEY_THEME_NAME = stringPreferencesKey("theme_name")
    private val KEY_THEME_ACCENT = stringPreferencesKey("theme_accent")
    private val KEY_THEME_DARK = booleanPreferencesKey("theme_dark")

    // Glass effects
    private val KEY_GLASS_ENABLED = booleanPreferencesKey("glass_enabled")
    private val KEY_GLASS_BLUR_RADIUS_DP = floatPreferencesKey("glass_blur_radius_dp")
    private val KEY_GLASS_SURFACE_ALPHA = floatPreferencesKey("glass_surface_alpha")

    // Animations
    private val KEY_ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
    private val KEY_ANIMATION_SPEED = intPreferencesKey("animation_speed") // 0..5

    // UI elements toggles
    private val KEY_SHOW_STATUS_BAR = booleanPreferencesKey("show_status_bar")
    private val KEY_SHOW_NOTCH_BAR = booleanPreferencesKey("show_notch_bar")
    private val KEY_SHOW_OVERLAY_MENUS = booleanPreferencesKey("show_overlay_menus")

    // Agent colors (store as hex strings by agent name)
    private const val KEY_AGENT_COLOR_PREFIX = "agent_color_" // e.g. agent_color_Genesis

    // Reads
    fun themeNameFlow(context: Context): Flow<String> =
        context.customizationDataStore.data.map { it[KEY_THEME_NAME] ?: "CyberGlow" }

    fun themeAccentFlow(context: Context): Flow<String> =
        context.customizationDataStore.data.map { it[KEY_THEME_ACCENT] ?: "NeonBlue" }

    fun themeDarkFlow(context: Context): Flow<Boolean> =
        context.customizationDataStore.data.map { it[KEY_THEME_DARK] ?: true }

    fun glassEnabledFlow(context: Context): Flow<Boolean> =
        context.customizationDataStore.data.map { it[KEY_GLASS_ENABLED] ?: false }

    fun glassBlurRadiusFlow(context: Context): Flow<Float> =
        context.customizationDataStore.data.map { it[KEY_GLASS_BLUR_RADIUS_DP] ?: 0f }

    fun glassSurfaceAlphaFlow(context: Context): Flow<Float> =
        context.customizationDataStore.data.map { it[KEY_GLASS_SURFACE_ALPHA] ?: 0.12f }

    fun animationsEnabledFlow(context: Context): Flow<Boolean> =
        context.customizationDataStore.data.map { it[KEY_ANIMATIONS_ENABLED] ?: false }

    fun animationSpeedFlow(context: Context): Flow<Int> =
        context.customizationDataStore.data.map { it[KEY_ANIMATION_SPEED] ?: 0 }

    fun showStatusBarFlow(context: Context): Flow<Boolean> =
        context.customizationDataStore.data.map { it[KEY_SHOW_STATUS_BAR] ?: true }

    fun showNotchBarFlow(context: Context): Flow<Boolean> =
        context.customizationDataStore.data.map { it[KEY_SHOW_NOTCH_BAR] ?: false }

    fun showOverlayMenusFlow(context: Context): Flow<Boolean> =
        context.customizationDataStore.data.map { it[KEY_SHOW_OVERLAY_MENUS] ?: false }

    fun agentColorFlow(context: Context, agentName: String): Flow<String> =
        context.customizationDataStore.data.map { prefs ->
            prefs[stringPreferencesKey(KEY_AGENT_COLOR_PREFIX + agentName)] ?: "#FFFFFF"
        }

    // Writes
    suspend fun setTheme(context: Context, name: String, accent: String, dark: Boolean) {
        context.customizationDataStore.edit {
            it[KEY_THEME_NAME] = name
            it[KEY_THEME_ACCENT] = accent
            it[KEY_THEME_DARK] = dark
        }
    }

    suspend fun setGlass(context: Context, enabled: Boolean, blurRadiusDp: Float, surfaceAlpha: Float) {
        context.customizationDataStore.edit {
            it[KEY_GLASS_ENABLED] = enabled
            it[KEY_GLASS_BLUR_RADIUS_DP] = blurRadiusDp
            it[KEY_GLASS_SURFACE_ALPHA] = surfaceAlpha
        }
    }

    suspend fun setAnimations(context: Context, enabled: Boolean, speed: Int) {
        context.customizationDataStore.edit {
            it[KEY_ANIMATIONS_ENABLED] = enabled
            it[KEY_ANIMATION_SPEED] = speed.coerceIn(0, 5)
        }
    }

    suspend fun setUiElements(context: Context, showStatusBar: Boolean, showNotchBar: Boolean, showOverlayMenus: Boolean) {
        context.customizationDataStore.edit {
            it[KEY_SHOW_STATUS_BAR] = showStatusBar
            it[KEY_SHOW_NOTCH_BAR] = showNotchBar
            it[KEY_SHOW_OVERLAY_MENUS] = showOverlayMenus
        }
    }

    suspend fun setAgentColor(context: Context, agentName: String, hexColor: String) {
        context.customizationDataStore.edit {
            it[stringPreferencesKey(KEY_AGENT_COLOR_PREFIX + agentName)] = hexColor
        }
    }
}

