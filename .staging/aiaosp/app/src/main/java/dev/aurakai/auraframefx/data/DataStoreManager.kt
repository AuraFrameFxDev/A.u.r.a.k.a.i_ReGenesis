package dev.aurakai.auraframefx.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
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
}
