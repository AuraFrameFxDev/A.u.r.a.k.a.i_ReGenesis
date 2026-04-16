package dev.aurakai.auraframefx.domains.genesis.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import dev.aurakai.auraframefx.core.security.KeystoreManager
import dev.aurakai.auraframefx.domains.kai.security.EncryptionManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Secure storage interface for persistent metadata and small secure data portions.
 */
interface SecureStorage {
    fun storeMetadata(key: String, metadata: FileMetadata)
    fun removeMetadata(key: String)
    fun getMetadata(key: String): FileMetadata?
    fun saveEncryptedData(key: String, data: ByteArray)
    fun deleteEncryptedData(key: String)

    companion object {
        fun getInstance(
            context: Context,
            encryptionManager: EncryptionManager,
            keystoreManager: KeystoreManager
        ): SecureStorage {
            return DefaultSecureStorage(context, encryptionManager, keystoreManager)
        }
    }
}

/**
 * Real implementation for SecureStorage using manual encryption to ensure stability.
 */
class DefaultSecureStorage(
    private val context: Context,
    private val encryptionManager: EncryptionManager,
    private val keystoreManager: KeystoreManager
) : SecureStorage {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("oracle_drive_secure_prefs", Context.MODE_PRIVATE)

    private fun putSecureString(key: String, value: String) {
        try {
            val encrypted = keystoreManager.encrypt(value, "oracle_drive")
            prefs.edit().putString(key, Base64.encodeToString(encrypted, Base64.NO_WRAP)).apply()
        } catch (e: Exception) {
            Timber.e(e, "SecureStorage: Failed to write key=$key")
        }
    }

    private fun getSecureString(key: String): String? {
        return try {
            val b64 = prefs.getString(key, null) ?: return null
            val encrypted = Base64.decode(b64, Base64.NO_WRAP)
            keystoreManager.decryptToString(encrypted, "oracle_drive")
        } catch (e: Exception) {
            Timber.e(e, "SecureStorage: Failed to read key=$key")
            null
        }
    }

    override fun storeMetadata(key: String, metadata: FileMetadata) {
        val json = Json.encodeToString(metadata)
        putSecureString("meta_$key", json)
    }

    override fun removeMetadata(key: String) {
        prefs.edit().remove("meta_$key").apply()
    }

    override fun getMetadata(key: String): FileMetadata? {
        val json = getSecureString("meta_$key") ?: return null
        return try {
            Json.decodeFromString<FileMetadata>(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun saveEncryptedData(key: String, data: ByteArray) {
        val encrypted = encryptionManager.encrypt(data)
        // Store as Base64 string in SharedPreferences
        val base64 = Base64.encodeToString(encrypted, Base64.DEFAULT)
        putSecureString("data_$key", base64)
    }

    override fun deleteEncryptedData(key: String) {
        prefs.edit().remove("data_$key").apply()
    }
}
