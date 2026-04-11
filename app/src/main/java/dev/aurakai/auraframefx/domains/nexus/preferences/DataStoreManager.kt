package dev.aurakai.auraframefx.domains.nexus.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val EXAMPLE_KEY = stringPreferencesKey("example_key")
    }

    suspend fun setExampleValue(value: String) {
        context.dataStore.edit { prefs ->
            prefs[EXAMPLE_KEY] = value
        }
    }

    val exampleValueFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[EXAMPLE_KEY]
    }

    suspend fun getString(key: String, defaultValue: String = ""): String {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }.first()
    }

    suspend fun storeString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { prefs ->
            prefs[prefKey] = value
        }
    }
}
