package dev.aurakai.auraframefx.domains.genesis.network

import dev.aurakai.auraframefx.core.security.KeystoreManager
import dev.aurakai.auraframefx.core.security.SecurePreferences
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auth token lifecycle manager.
 */
@Singleton
class TokenManager @Inject constructor(
    private val securePrefs: SecurePreferences,
    private val keystoreManager: KeystoreManager
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry_ms"
        private const val BEARER_PREFIX = "Bearer "
    }

    fun saveTokens(accessToken: String, refreshToken: String, expiryMs: Long) {
        securePrefs.putString(KEY_ACCESS_TOKEN, accessToken)
        securePrefs.putString(KEY_REFRESH_TOKEN, refreshToken)
        securePrefs.putLong(KEY_TOKEN_EXPIRY, expiryMs)
        Timber.d("TokenManager: Tokens saved (expiry=${expiryMs}ms)")
    }

    fun getAccessToken(): String? = securePrefs.getString(KEY_ACCESS_TOKEN)
    fun getRefreshToken(): String? = securePrefs.getString(KEY_REFRESH_TOKEN)

    fun isTokenValid(): Boolean {
        val token = getAccessToken() ?: return false
        val expiry = securePrefs.getLong(KEY_TOKEN_EXPIRY, 0L)
        return token.isNotBlank() && (expiry == 0L || System.currentTimeMillis() < expiry)
    }

    fun getAuthHeader(): String {
        val token = getAccessToken() ?: return ""
        return "$BEARER_PREFIX$token"
    }

    fun clearTokens() {
        securePrefs.remove(KEY_ACCESS_TOKEN)
        securePrefs.remove(KEY_REFRESH_TOKEN)
        securePrefs.remove(KEY_TOKEN_EXPIRY)
        Timber.i("TokenManager: Tokens cleared")
    }
}
