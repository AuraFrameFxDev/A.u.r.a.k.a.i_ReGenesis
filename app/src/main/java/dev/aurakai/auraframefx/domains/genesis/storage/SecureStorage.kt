package dev.aurakai.auraframefx.domains.genesis.storage

import android.content.Context
import dev.aurakai.auraframefx.domains.genesis.oracledrive.security.CryptographyManager

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
        fun getInstance(context: Context, cryptoManager: CryptographyManager): SecureStorage {
            return DefaultSecureStorage(context, cryptoManager)
        }
    }
}

/**
 * Placeholder implementation for SecureStorage
 */
class DefaultSecureStorage(
    private val context: Context,
    private val cryptoManager: CryptographyManager
) : SecureStorage {
    override fun storeMetadata(key: String, metadata: FileMetadata) {
        // Placeholder
    }

    override fun removeMetadata(key: String) {
        // Placeholder
    }

    override fun getMetadata(key: String): FileMetadata? {
        return null
    }

    override fun saveEncryptedData(key: String, data: ByteArray) {
        // Placeholder - save encrypted data
    }

    override fun deleteEncryptedData(key: String) {
        // Placeholder - delete encrypted data
    }
}


// FileMetadata is now provided by dev.aurakai.auraframefx.genesis.storage.StorageModels.kt
