package dev.aurakai.auraframefx.domains.kai.security

interface EncryptionManager {
    fun encrypt(data: ByteArray): ByteArray
    fun decrypt(data: ByteArray): ByteArray
}
