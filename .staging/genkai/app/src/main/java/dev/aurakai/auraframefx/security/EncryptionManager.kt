package dev.aurakai.auraframefx.security

import android.content.Context

/**
 * Minimal EncryptionManager stub for DI and compilation.
 * Replace with real implementation using AndroidX Security or Keystore.
 */
interface EncryptionManager {
    /**
 * Encrypts the provided byte array using the key identified by the given alias.
 *
 * @param data The input bytes to encrypt.
 * @param alias The key alias to use; defaults to "default".
 * @return The encrypted bytes corresponding to the input data.
 */
fun encrypt(data: ByteArray, alias: String = "default"): ByteArray
    /**
 * Decrypts the provided byte array using the specified key alias.
 *
 * @param alias The key alias to use for decryption; defaults to "default".
 * @return The decrypted byte array.
 */
fun decrypt(data: ByteArray, alias: String = "default"): ByteArray
}

class NoOpEncryptionManager(private val context: Context?) : EncryptionManager {
    /**
 * No-op encryption: returns the provided bytes unchanged.
 *
 * @param data The plaintext bytes to "encrypt".
 * @param alias Identifier for the encryption key; ignored by this implementation.
 * @return The input `ByteArray` unchanged.
 */
override fun encrypt(data: ByteArray, alias: String): ByteArray = data
    /**
 * Provide a no-op decryption that returns the input bytes unchanged.
 *
 * @param data The ciphertext bytes to "decrypt"; returned as-is.
 * @param alias The key alias to select the decryption key (ignored in this implementation).
 * @return The original `data` unchanged.
 */
override fun decrypt(data: ByteArray, alias: String): ByteArray = data
}
