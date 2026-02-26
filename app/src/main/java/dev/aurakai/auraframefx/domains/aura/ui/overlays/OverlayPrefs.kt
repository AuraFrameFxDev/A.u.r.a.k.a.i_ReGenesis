package dev.aurakai.auraframefx.domains.aura.ui.overlays

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.overlayDataStore by preferencesDataStore(name = "overlay_settings")

object OverlayPrefs {
    private val KEY_ENABLED = booleanPreferencesKey("enabled")
    private val KEY_ORDER = stringPreferencesKey("order")
    private val KEY_TRANSITION_STYLE = stringPreferencesKey("transition_style")
    private val KEY_TRANSITION_SPEED = stringPreferencesKey("transition_speed")

    fun enabledFlow(context: Context): Flow<Boolean> =
        context.overlayDataStore.data.map { it[KEY_ENABLED] ?: true }

    fun orderFlow(context: Context): Flow<List<String>> =
        context.overlayDataStore.data.map { prefs ->
            prefs[KEY_ORDER]?.split('|')?.filter { it.isNotBlank() }
                ?: listOf("Agent Edge", "Aura Presence", "Chat Bubble", "Sidebar")
        }

    fun transitionStyleFlow(context: Context): Flow<String> =
        context.overlayDataStore.data.map { it[KEY_TRANSITION_STYLE] ?: "none" }

    fun transitionSpeedFlow(context: Context): Flow<Int> =
        context.overlayDataStore.data.map { it[KEY_TRANSITION_SPEED]?.toIntOrNull() ?: 0 }

    suspend fun saveEnabled(context: Context, enabled: Boolean) {
        context.overlayDataStore.edit { it[KEY_ENABLED] = enabled }
    }

    suspend fun saveOrder(context: Context, order: List<String>) {
        context.overlayDataStore.edit { it[KEY_ORDER] = order.joinToString("|") }
    }

    suspend fun saveTransitionStyle(context: Context, style: String) {
        context.overlayDataStore.edit { it[KEY_TRANSITION_STYLE] = style }
    }

    suspend fun saveTransitionSpeed(context: Context, speed: Int) {
        context.overlayDataStore.edit { it[KEY_TRANSITION_SPEED] = speed.toString() }
    }
}

