package dev.aurakai.auraframefx.domains.kai.security

/**
 * Simple EncryptionManager interface expected by SecureFileManager.
 * Keep API minimal for DI compatibility; production implementation should use Android keystore.
 */
interface EncryptionManager {
    /**
     * Encrypts the provided byte array using the manager's configured mechanism.
     *
     * @param data Plain bytes to encrypt.
     * @return The encrypted byte array.
     */
    fun encrypt(data: ByteArray): ByteArray

    /**
     * Decrypts the provided encrypted bytes and returns the original plaintext bytes.
     *
     * @param data Encrypted data to decrypt.
     * @return The decrypted plaintext bytes.
     */
    fun decrypt(data: ByteArray): ByteArray
}

/** No-op implementation used during builds/tests. */
object NoopEncryptionManager : EncryptionManager {
    /**
     * Perform a no-op encryption that leaves the provided bytes unchanged.
     *
     * @param data The bytes to encrypt; returned unchanged.
     * @return The same byte array provided in `data`.
     */
    override fun encrypt(data: ByteArray): ByteArray = data

    /**
     * Perform a no-op decryption that returns the provided byte array unchanged.
     *
     * @return The original `data` byte array as given.
     */
    override fun decrypt(data: ByteArray): ByteArray = data
}
