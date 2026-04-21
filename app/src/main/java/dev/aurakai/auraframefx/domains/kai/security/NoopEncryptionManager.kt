package dev.aurakai.auraframefx.domains.kai.security

object NoopEncryptionManager : EncryptionManager {
    override fun encrypt(data: ByteArray): ByteArray = data
    override fun decrypt(data: ByteArray): ByteArray = data
}
