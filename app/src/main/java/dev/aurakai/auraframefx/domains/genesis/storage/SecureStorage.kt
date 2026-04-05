package dev.aurakai.auraframefx.domains.genesis.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.aurakai.auraframefx.domains.genesis.oracledrive.security.EncryptionManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
        fun getInstance(context: Context, encryptionManager: EncryptionManager): SecureStorage {
            return DefaultSecureStorage(context, encryptionManager)
        }
    }
}

/**
 * Real implementation for SecureStorage using EncryptedSharedPreferences.
 */
class DefaultSecureStorage(
    private val context: Context,
    private val encryptionManager: EncryptionManager
) : SecureStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "oracle_drive_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun storeMetadata(key: String, metadata: FileMetadata) {
        val json = Json.encodeToString(metadata)
        sharedPreferences.edit().putString("meta_$key", json).apply()
    }

    override fun removeMetadata(key: String) {
        sharedPreferences.edit().remove("meta_$key").apply()
    }

    override fun getMetadata(key: String): FileMetadata? {
        val json = sharedPreferences.getString("meta_$key", null) ?: return null
        return try {
            Json.decodeFromString<FileMetadata>(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun saveEncryptedData(key: String, data: ByteArray) {
        val encrypted = encryptionManager.encrypt(data)
        // Store as Base64 string in SharedPreferences
        val base64 = android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)
        sharedPreferences.edit().putString("data_$key", base64).apply()
    }

    override fun deleteEncryptedData(key: String) {
        sharedPreferences.edit().remove("data_$key").apply()
    }
}
