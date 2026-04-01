package dev.aurakai.auraframefx.domains.genesis.network

import dev.aurakai.auraframefx.core.security.SecurePreferences
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auth token lifecycle manager.
 */
@Singleton
class TokenManager @Inject constructor(
    private val securePrefs: SecurePreferences
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }

    val accessToken: String?
        get() = securePrefs.getString(KEY_ACCESS_TOKEN)

    val refreshToken: String?
        get() = securePrefs.getString(KEY_REFRESH_TOKEN)

    val isTokenExpired: Boolean
        get() {
            val expiry = securePrefs.getLong(KEY_TOKEN_EXPIRY, 0L)
            return System.currentTimeMillis() >= expiry
        }

    val isAuthenticated: Boolean
        get() = !accessToken.isNullOrBlank() && !isTokenExpired

    fun updateTokens(accessToken: String, refreshToken: String, expiresInSeconds: Long) {
        securePrefs.putString(KEY_ACCESS_TOKEN, accessToken)
        securePrefs.putString(KEY_REFRESH_TOKEN, refreshToken)
        val expiry = System.currentTimeMillis() + (expiresInSeconds * 1000)
        securePrefs.putLong(KEY_TOKEN_EXPIRY, expiry)
        Timber.d("TokenManager: Tokens updated (expires in ${expiresInSeconds}s)")
    }

    fun clearTokens() {
        securePrefs.remove(KEY_ACCESS_TOKEN)
        securePrefs.remove(KEY_REFRESH_TOKEN)
        securePrefs.remove(KEY_TOKEN_EXPIRY)
        Timber.i("TokenManager: Tokens cleared")
    }
}
