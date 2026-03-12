package dev.aurakai.auraframefx.docs.security

/**
 * Placeholder encryption manager used by SecureFileManager in docs.
 * Replace with real platform crypto in production.
 */
object EncryptionManager {
    fun encrypt(data: ByteArray): ByteArray = data // TODO: implement
    fun decrypt(data: ByteArray): ByteArray = data // TODO: implement
}
