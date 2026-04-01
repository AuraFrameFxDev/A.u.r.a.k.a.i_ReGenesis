package dev.aurakai.auraframefx.domains.nexus.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.userPrefsManagerDataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val EXAMPLE_KEY = stringPreferencesKey("example_key")
    }

    suspend fun setExampleValue(value: String) {
        context.userPrefsManagerDataStore.edit { prefs ->
            prefs[EXAMPLE_KEY] = value
        }
    }

    val exampleValueFlow: Flow<String?> = context.userPrefsManagerDataStore.data.map { prefs ->
        prefs[EXAMPLE_KEY]
    }

    suspend fun getString(key: String, defaultValue: String = ""): String {
        val prefKey = stringPreferencesKey(key)
        return context.userPrefsManagerDataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }.first()
    }

    suspend fun storeString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.userPrefsManagerDataStore.edit { prefs ->
            prefs[prefKey] = value
        }
    }
}
