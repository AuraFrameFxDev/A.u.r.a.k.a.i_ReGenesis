package dev.aurakai.auraframefx.genesis.storage

import android.content.Context
import dev.aurakai.auraframefx.genesis.security.CryptographyManager
import dev.aurakai.auraframefx.oracledrive.service.FileMetadata

/**
 * Secure storage interface for persistent metadata and small secure data portions.
 */
interface SecureStorage {
    fun storeMetadata(key: String, metadata: FileMetadata)
    fun removeMetadata(key: String)
    fun getMetadata(key: String): FileMetadata?

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
}
