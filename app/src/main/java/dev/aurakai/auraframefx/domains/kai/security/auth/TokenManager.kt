package dev.aurakai.auraframefx.domains.kai.security.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.cascade.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages authentication tokens and session state for the application.
 * Handles token storage, expiration, and refresh logic.
 * Uses EncryptedSharedPreferences for hardware-backed token protection.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context,
    private val dispatchers: AppCoroutineDispatchers,
) {
    private val appContext = context.applicationContext

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(appContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                appContext,
                ENCRYPTED_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to create EncryptedSharedPreferences, using fallback")
            appContext.getSharedPreferences(ENCRYPTED_PREFS_FILE, Context.MODE_PRIVATE)
        }
    }

    init {
        migrateFromPlaintextIfNeeded()
    }

    private fun migrateFromPlaintextIfNeeded() {
        try {
            val oldPrefs = appContext.getSharedPreferences(LEGACY_PREFS_FILE, Context.MODE_PRIVATE)
            val oldToken = oldPrefs.getString(KEY_ACCESS_TOKEN, null) ?: return

            val oldRefresh = oldPrefs.getString(KEY_REFRESH_TOKEN, null)
            val oldExpiry = oldPrefs.getLong(KEY_ACCESS_TOKEN_EXPIRY, 0)

            prefs.edit {
                putString(KEY_ACCESS_TOKEN, oldToken)
                if (oldRefresh != null) putString(KEY_REFRESH_TOKEN, oldRefresh)
                putLong(KEY_ACCESS_TOKEN_EXPIRY, oldExpiry)
            }

            oldPrefs.edit().clear().apply()
            File(appContext.filesDir.parent, "shared_prefs/$LEGACY_PREFS_FILE.xml").delete()
            Timber.i("TokenManager: Migrated tokens from plaintext to encrypted storage")
        } catch (e: Exception) {
            Timber.e(e, "Token migration failed")
        }
    }

    private val _authState = MutableStateFlow<AuthState>(
        if (hasValidAccessToken()) AuthState.Authenticated else AuthState.Unauthenticated
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * The current access token, or null if not authenticated.
     */
    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)
        private set(value) = prefs.edit { putString(KEY_ACCESS_TOKEN, value) }

    /**
     * The current refresh token, or null if not authenticated.
     */
    var refreshToken: String?
        get() = prefs.getString(KEY_REFRESH_TOKEN, null)
        private set(value) = prefs.edit { putString(KEY_REFRESH_TOKEN, value) }

    /**
     * The expiration time of the current access token in milliseconds since epoch.
     */
    var accessTokenExpiry: Long
        get() = prefs.getLong(KEY_ACCESS_TOKEN_EXPIRY, 0)
        private set(value) = prefs.edit { putLong(KEY_ACCESS_TOKEN_EXPIRY, value) }

    /**
     * Updates the authentication tokens and their expiration.
     *
     * @param accessToken The new access token.
     * @param refreshToken The new refresh token.
     * @param expiresInSeconds The number of seconds until the access token expires.
     */
    fun updateTokens(accessToken: String, refreshToken: String, expiresInSeconds: Long) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.accessTokenExpiry = System.currentTimeMillis() + (expiresInSeconds * 1000)
        _authState.value = AuthState.Authenticated
    }

    /**
     * Clears all authentication tokens and marks the user as unauthenticated.
     */
    fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_ACCESS_TOKEN_EXPIRY)
        }
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Checks if the user is currently authenticated with a valid access token.
     *
     * @return True if the user is authenticated with a valid token, false otherwise.
     */
    fun isLoggedIn(): Boolean = hasValidAccessToken()

    /**
     * Checks if the current access token is valid and not expired.
     *
     * @return True if the access token exists and is not expired, false otherwise.
     */
    fun hasValidAccessToken(): Boolean {
        val token = accessToken
        return !token.isNullOrBlank() && !isAccessTokenExpired()
    }

    /**
     * Checks if the current access token has expired.
     *
     * @return True if the access token has expired or doesn't exist, false otherwise.
     */
    private fun isAccessTokenExpired(): Boolean {
        return System.currentTimeMillis() >= accessTokenExpiry
    }

    /**
     * Gets the authorization header value for API requests.
     *
     * @return The authorization header value (e.g., "Bearer <token>"), or null if not authenticated.
     */
    fun getAuthorizationHeader(): String? {
        return accessToken?.let { "Bearer $it" }
    }

    /**
     * Sealed class representing the authentication state of the user.
     */
    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_ACCESS_TOKEN_EXPIRY = "access_token_expiry"

        private const val ENCRYPTED_PREFS_FILE = "auth_prefs_secure"
        private const val LEGACY_PREFS_FILE = "auth_prefs"

        // Token expiration buffer in milliseconds (5 minutes)
        private const val TOKEN_EXPIRATION_BUFFER = 5 * 60 * 1000L
    }
}

