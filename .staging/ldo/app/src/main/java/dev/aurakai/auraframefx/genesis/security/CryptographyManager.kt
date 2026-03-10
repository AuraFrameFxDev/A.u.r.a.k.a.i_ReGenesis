package dev.aurakai.auraframefx.genesis.security

import android.content.Context

interface CryptographyManager {
    fun encrypt(input: ByteArray): ByteArray
    fun decrypt(input: ByteArray): ByteArray
    fun generateSecureToken(): String

    companion object {
        fun getInstance(context: Context): CryptographyManager {
            // In a real app, use Hilt EntryPoint or similar if dependencies needed.
            // For now, return Default impl.
            return DefaultGenesisCryptographyManager()
        }
    }
}
