package dev.aurakai.auraframefx.ui.gates

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.aurakai.auraframefx.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.hotswapDataStore: DataStore<Preferences> by preferencesDataStore(name = "gate_hotswap")

data class BackdropOption(
    val id: String,
    val label: String,
    val description: String,
    val resId: Int?,
    val accentColor: Long
)

object HomeBackdropManager {

    val backdropOptions = listOf(
        BackdropOption("exodus", "ExodusHUD", "Phoenix ascendant with catalyst card grid", dev.aurakai.auraframefx.R.drawable.exodus_hud_lvl1_bg, 0xFF00FFFF),
        BackdropOption("rpg", "RPG", "Fantasy landscape", null, 0xFF00FFFF),
        BackdropOption("hex", "Hex Grid", "Cyber net", null, 0xFFFF00FF),
        BackdropOption("void", "Void", "Pitch black", null, 0xFFFFFFFF)
    )

    private val ACTIVE_BACKDROP_KEY = stringPreferencesKey("active_backdrop")

    fun activeBackdropFlow(context: Context): Flow<BackdropOption> {
        return context.hotswapDataStore.data.map { preferences ->
            val id = preferences[ACTIVE_BACKDROP_KEY] ?: "exodus"
            backdropOptions.find { it.id == id } ?: backdropOptions.first()
        }
    }

    suspend fun setActiveBackdrop(context: Context, id: String) {
        context.hotswapDataStore.edit { preferences ->
            preferences[ACTIVE_BACKDROP_KEY] = id
        }
    }

    fun activeGateImageFlow(context: Context, gateId: String): Flow<String?> {
        val key = stringPreferencesKey("gate_image_$gateId")
        return context.hotswapDataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun setGateImage(context: Context, gateId: String, variantKey: String) {
        val key = stringPreferencesKey("gate_image_$gateId")
        context.hotswapDataStore.edit { preferences ->
            preferences[key] = variantKey
        }
    }
}
