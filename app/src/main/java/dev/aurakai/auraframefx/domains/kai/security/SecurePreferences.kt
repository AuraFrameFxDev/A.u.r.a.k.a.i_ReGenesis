package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages secure storage of sensitive data like OAuth tokens or API keys.
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Use applicationContext to prevent activity/fragment context leaks
    private val appContext = context.applicationContext

    // Get or create master key for encryption
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    // Create encrypted shared preferences
    private val securePrefs by lazy {
        EncryptedSharedPreferences.create(
            appContext,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Retrieves the stored OAuth token.
     * @return The OAuth token as a String, or null if not found.
     */
    fun getOAuthToken(): String? {
        return securePrefs.getString("oauth_token", null)
    }

    /**
     * Saves the OAuth token securely.
     * @param token The OAuth token to save.
     */
    fun saveOAuthToken(token: String?) {
        securePrefs.edit().putString("oauth_token", token).apply()
    }

    /**
     * Retrieves API key for Generative AI models
     * @return The API key as a String, or null if not found.
     */
    fun getApiKey(): String? {
        return securePrefs.getString("api_key", null)
    }

    /**
     * Saves the API key securely.
     * @param key The API key to save.
     */
    fun saveApiKey(key: String) {
        securePrefs.edit().putString("api_key", key).apply()
    }

    // --- QUICK TOGGLE STATUSES ---

    fun isVetoEnabled(): Boolean = securePrefs.getBoolean("veto_enabled", true)
    fun setVetoEnabled(enabled: Boolean) =
        securePrefs.edit().putBoolean("veto_enabled", enabled).apply()

    fun isConsciousnessEnabled(): Boolean = securePrefs.getBoolean("consciousness_enabled", true)
    fun setConsciousnessEnabled(enabled: Boolean) =
        securePrefs.edit().putBoolean("consciousness_enabled", enabled).apply()

    fun isAuraBubbleEnabled(): Boolean = securePrefs.getBoolean("aura_bubble_enabled", true)
    fun setAuraBubbleEnabled(enabled: Boolean) =
        securePrefs.edit().putBoolean("aura_bubble_enabled", enabled).apply()

    /**
     * Clear all secure preferences
     */
    fun clearAll() {
        securePrefs.edit().clear().apply()
    }
}
