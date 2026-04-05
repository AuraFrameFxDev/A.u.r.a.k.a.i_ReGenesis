package dev.aurakai.genesis.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides secure storage operations for Genesis module using EncryptedSharedPreferences.
 */
@Singleton
class SecureStorage @Inject constructor(
    private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "genesis_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Stores a plaintext value securely under the given key.
     *
     * @param key The identifier under which the value will be stored.
     * @param value The plaintext value to persist securely.
     * @return `true` if the value was stored successfully, `false` otherwise.
     */
    fun store(key: String, value: String): Boolean {
        return try {
            sharedPreferences.edit().putString(key, value).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves the value stored for the given key from secure storage.
     *
     * @param key The storage key whose value should be retrieved.
     * @return The stored string value for `key`, or `null` if no value exists.
     */
    fun retrieve(key: String): String? {
        return try {
            sharedPreferences.getString(key, null)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Deletes the stored value associated with the given key from secure storage.
     *
     * @param key The storage key whose value should be removed.
     * @return `true` if the value was deleted, `false` otherwise.
     */
    fun delete(key: String): Boolean {
        return try {
            sharedPreferences.edit().remove(key).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Removes all entries from the secure storage.
     *
     * @return `true` if all stored items were removed successfully, `false` otherwise.
     */
    fun clear(): Boolean {
        return try {
            sharedPreferences.edit().clear().apply()
            true
        } catch (e: Exception) {
            false
        }
    }
}
