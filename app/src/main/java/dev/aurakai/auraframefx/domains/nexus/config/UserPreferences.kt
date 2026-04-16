package dev.aurakai.auraframefx.domains.nexus.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.aurakai.auraframefx.domains.nexus.models.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * UserPreferences: Manages user-specific settings and data persistence.
 */
class UserPreferences(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    init {
        // DataStore initialized automatically via lazy delegate
        timber.log.Timber.d("UserPreferences initialized with DataStore")
    }

    // Flow-based reactive properties for observing preferences
    val apiKeyFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[API_KEY] }
    val userIdFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[USER_ID] }
    val userNameFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[USER_NAME] }
    val userEmailFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[USER_EMAIL] }

    /**
     * Stores the API key for the user.
     *
     * @param key The API key to store
     */
    suspend fun setApiKey(key: String) {
        dataStore.edit { prefs ->
            prefs[API_KEY] = key
        }
    }

    /**
     * Stores the user ID.
     *
     * @param id The user ID to store
     */
    suspend fun setUserId(id: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID] = id
        }
    }

    /**
     * Stores the username.
     *
     * @param name The username to store
     */
    suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[USER_NAME] = name
        }
    }

    /**
     * Stores the user email.
     *
     * @param email The email to store
     */
    suspend fun setUserEmail(email: String) {
        dataStore.edit { prefs ->
            prefs[USER_EMAIL] = email
        }
    }

    /**
     * Returns the stored string value for the specified preference key, or the provided default if the key is absent.
     *
     * @param key The preference key to look up.
     * @param defaultValue The value to return if the key is not found.
     * @return The value associated with the key, or the default value if the key does not exist.
     */
    suspend fun getPreference(key: String, defaultValue: String): String {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }.first()
    }

    /**
     * Stores or updates the string value for the given key in DataStore.
     *
     * If the key already exists, its value is overwritten.
     *
     * @param key The preference key to set.
     * @param value The string value to associate with the key.
     */
    suspend fun setPreference(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { prefs ->
            prefs[prefKey] = value
        }
    }

    /**
     * Retrieves complete user data from DataStore.
     *
     * Reads all user-related preferences and constructs a UserData object.
     *
     * @return UserData object with current user information, or null if no user data is set
     */
    suspend fun getUserData(): UserData? {
        val prefs = dataStore.data.first()

        val userId = prefs[USER_ID]
        val userName = prefs[USER_NAME]
        val userEmail = prefs[USER_EMAIL]
        val apiKey = prefs[API_KEY]

        // Return null if no user data is set
        if (userId == null && userName == null && userEmail == null) {
            return null
        }

        return UserData(
            id = userId ?: "",
            name = userName ?: "",
            email = userEmail ?: "",
            apiKey = apiKey,
        )
    }

    /**
     * Saves complete user data to DataStore.
     *
     * @param userData The user data to save
     */
    suspend fun saveUserData(userData: UserData) {
        dataStore.edit { prefs ->
            prefs[USER_ID] = userData.id ?: ""
            prefs[USER_NAME] = userData.name ?: ""
            prefs[USER_EMAIL] = userData.email ?: ""
            userData.apiKey?.let { prefs[API_KEY] = it }
        }
    }

    /**
     * Clears all user data from DataStore.
     * Useful for logout operations.
     */
    suspend fun clearUserData() {
        dataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(API_KEY)
        }
    }
}
