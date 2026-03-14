@file:Suppress("unused")

package dev.aurakai.auraframefx.navigation

import android.content.Context
import dev.aurakai.auraframefx.genesis.security.CryptographyManager

/**
 * Placeholder implementation of SecureStorage for build compatibility
 */
class SecureStorage private constructor(
    private val context: Context,
    private val cryptoManager: CryptographyManager
) {

    fun store(key: String, value: String) {
        // Placeholder implementation
    }

    fun retrieve(key: String): String? {
        // Placeholder implementation
        return null
    }

    fun delete(key: String) {
        // Placeholder implementation
    }

    companion object {
        fun getInstance(context: Context, cryptoManager: CryptographyManager): SecureStorage {
            return SecureStorage(context, cryptoManager)
        }
    }
}
