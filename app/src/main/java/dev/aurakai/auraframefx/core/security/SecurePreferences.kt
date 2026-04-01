package dev.aurakai.auraframefx.core.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES-256-GCM encrypted SharedPreferences backed by Android Keystore.
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keystoreManager: KeystoreManager
) {
    companion object {
        private const val PREFS_FILE = "aurakai_secure_prefs"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    val securePrefs: SharedPreferences get() = prefs

    fun putString(key: String, value: String) {
        try {
            val encrypted = keystoreManager.encrypt(value, KeystoreManager.PREFS_KEY)
            prefs.edit().putString(key, Base64.encodeToString(encrypted, Base64.NO_WRAP)).apply()
        } catch (e: Exception) {
            Timber.e(e, "SecurePreferences: Failed to write key=$key")
        }
    }

    fun getString(key: String): String? {
        return try {
            val b64 = prefs.getString(key, null) ?: return null
            val encrypted = Base64.decode(b64, Base64.NO_WRAP)
            keystoreManager.decryptToString(encrypted, KeystoreManager.PREFS_KEY)
        } catch (e: Exception) {
            Timber.e(e, "SecurePreferences: Failed to read key=$key — returning null")
            null
        }
    }

    fun putBoolean(key: String, value: Boolean) = putString(key, value.toString())
    fun getBoolean(key: String, default: Boolean = false): Boolean =
        getString(key)?.toBooleanStrictOrNull() ?: default

    fun putLong(key: String, value: Long) = putString(key, value.toString())
    fun getLong(key: String, default: Long = 0L): Long =
        getString(key)?.toLongOrNull() ?: default

    fun remove(key: String) = prefs.edit().remove(key).apply()
    fun contains(key: String): Boolean = prefs.contains(key)
    fun clearAll() = prefs.edit().clear().apply()
}
