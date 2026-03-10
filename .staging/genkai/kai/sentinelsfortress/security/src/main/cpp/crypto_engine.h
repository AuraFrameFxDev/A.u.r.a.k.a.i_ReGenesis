#ifndef CRYPTO_ENGINE_H
#define CRYPTO_ENGINE_H

#include <vector>
#include <cstdint>
#include <string>

/**
 * Genesis Protocol Secure Communication - Crypto Engine V2.
 * Provides static, platform-agnostic cryptographic helpers used by the Genesis
 * secure communication layer.
 */

/**
 * Initialize the cryptographic engine and any required global state.
 *
 * This must be called before other CryptoEngine methods. Subsequent calls are
 * a no-op and return true if initialization already succeeded.
 *
 * @return true if the engine is ready for use; false on initialization failure.
 */

/**
 * Encrypt a contiguous buffer with the Genesis secure algorithm.
 *
 * The function copies `length` bytes from `data` and returns the ciphertext as
 * a byte vector. The `key` is a null-terminated C string representing the
 * symmetric key or key identifier used by the algorithm (encoding and length
 * semantics are implementation-defined).
 *
 * @param data Pointer to the plaintext buffer (may be nullptr only if length is 0).
 * @param length Number of bytes to encrypt from `data`.
 * @param key Null-terminated C string representing the encryption key.
 * @return Ciphertext as a vector of bytes.
 */

/**
 * Decrypt a contiguous buffer produced by `encrypt`.
 *
 * The function copies `length` bytes from `data` (ciphertext) and returns the
 * resulting plaintext as a byte vector. The `key` must match the key used for
 * encryption.
 *
 * @param data Pointer to the ciphertext buffer (may be nullptr only if length is 0).
 * @param length Number of bytes to decrypt from `data`.
 * @param key Null-terminated C string representing the decryption key.
 * @return Plaintext as a vector of bytes.
 */

/**
 * Generate a secure communication key suitable for use with the engine.
 *
 * The returned string contains the key in an implementation-defined encoding
 * (for example base64 or hex). Callers should treat the result as secret and
 * store/transmit it using appropriate protections.
 *
 * @return Newly generated key as a std::string.
 */

/**
 * Verify integrity of a buffer using a provided signature.
 *
 * The `signature` is a null-terminated C string containing the integrity
 * signature produced by the engine (encoding is implementation-defined). Returns
 * true if the signature validates against the provided data.
 *
 * @param data Pointer to the buffer whose integrity is being checked.
 * @param length Number of bytes in `data`.
 * @param signature Null-terminated C string containing the signature to verify.
 * @return true if the signature is valid for the data; false otherwise.
 */
class CryptoEngine {
public:
    /**
     * Initialize the cryptographic engine
     */
    static bool initialize();

    /**
     * Encrypt data using Genesis secure algorithm
     */
    static std::vector <uint8_t> encrypt(const uint8_t *data, size_t length, const char *key);

    /**
     * Decrypt data using Genesis secure algorithm
     */
    static std::vector <uint8_t> decrypt(const uint8_t *data, size_t length, const char *key);

    /**
     * Generate secure communication key
     */
    static std::string generateSecureKey();

    /**
     * Verify data integrity
     */
    static bool verifyIntegrity(const uint8_t *data, size_t length, const char *signature);

private:
    static bool initialized_;

    static void initializeRandomGenerator();
};

#endif // CRYPTO_ENGINE_H