package dev.aurakai.auraframefx.domains.genesis.oracledrive.security


import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultGenesisCryptographyManager @Inject constructor() : CryptographyManager {
    /**
     * Performs a no-op placeholder encryption.
     *
     * @param input The byte array to "encrypt"; this implementation does not modify it.
     * @return The same byte array that was provided, unchanged.
     */
    override fun encrypt(input: ByteArray): ByteArray = input

    /**
     * Performs decryption of the provided ciphertext bytes.
     *
     * @param input The ciphertext to decrypt.
     * @return The decrypted bytes; in this default implementation the input is returned unchanged.
     */
    override fun decrypt(input: ByteArray): ByteArray = input // placeholder

    override fun generateSecureToken(): String {
        return UUID.randomUUID().toString()
    }

    override fun encryptData(data: ByteArray): ByteArray = encrypt(data)

    override fun decryptData(data: ByteArray): ByteArray = decrypt(data)
}
