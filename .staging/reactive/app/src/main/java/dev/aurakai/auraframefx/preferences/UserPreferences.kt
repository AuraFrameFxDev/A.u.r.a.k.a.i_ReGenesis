package dev.aurakai.auraframefx.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    private val THEME_KEY = stringPreferencesKey("theme")
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val AGENT_MODE_KEY = stringPreferencesKey("agent_mode")
    
    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "dark"
    }
    
    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }
    
    val agentModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AGENT_MODE_KEY] ?: "dual"
    }
    
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
    
    suspend fun setAgentMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[AGENT_MODE_KEY] = mode
        }
    }
    
    fun getTheme(): String = "dark"
    fun getLanguage(): String = "en"
    fun getAgentMode(): String = "dual"
}
