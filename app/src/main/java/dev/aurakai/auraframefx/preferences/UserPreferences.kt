package dev.aurakai.auraframefx.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val THEME_KEY = stringPreferencesKey("theme")
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val AGENT_MODE_KEY = stringPreferencesKey("agent_mode")
    private val GENDER_IDENTITY_KEY = stringPreferencesKey("gender_identity")
    private val ONBOARDING_COMPLETE_KEY = stringPreferencesKey("onboarding_complete")

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "dark"
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }

    val agentModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AGENT_MODE_KEY] ?: "dual"
    }

    val genderIdentityFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[GENDER_IDENTITY_KEY]
    }

    val onboardingCompleteFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETE_KEY]?.toBoolean() ?: false
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

    suspend fun setGenderIdentity(identity: String) {
        context.dataStore.edit { preferences ->
            preferences[GENDER_IDENTITY_KEY] = identity
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY] = complete.toString()
        }
    }

    suspend fun getGenderIdentity(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[GENDER_IDENTITY_KEY]
        }.first()
    }

    suspend fun isOnboardingComplete(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY]?.toBoolean() ?: false
        }.first()
    }

    fun getTheme(): String = "dark"
    fun getLanguage(): String = "en"
    fun getAgentMode(): String = "dual"
}
