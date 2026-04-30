package dev.aurakai.auraframefx.domains.genesis.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compatibility shim for Genesis CryptographyManager system.
 */
@Singleton
class CryptographyManager @Inject constructor() {
    /**
 * Provide a compatibility shim that preserves the input bytes.
 *
 * Currently performs no encryption and returns the input unchanged.
 *
 * @param data The bytes to be "encrypted".
 * @param alias The key alias to use (currently unused).
 * @return The same bytes provided in `data`.
 */
fun encrypt(data: ByteArray, alias: String): ByteArray = data
    /**
 * Performs decryption for the given byte array using the specified key alias; in this implementation the operation is a no-op.
 *
 * @param data The bytes to decrypt.
 * @param alias The key alias identifying the decryption key.
 * @return The decrypted bytes; in this implementation the same as the input `data`.
 */
fun decrypt(data: ByteArray, alias: String): ByteArray = data
    /**
 * Removes the cryptographic key associated with the provided alias for compatibility with the Genesis CryptographyManager API.
 *
 * This implementation is a no-op retained for compatibility and does not modify or remove any stored keys.
 *
 * @param alias Identifier of the key to remove; ignored in this implementation.
 */
fun removeKey(alias: String) {}
}
