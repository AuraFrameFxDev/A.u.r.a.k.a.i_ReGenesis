package dev.aurakai.auraframefx.domains.genesis.oracledrive.security

import dev.aurakai.auraframefx.core.security.KeystoreManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encryption manager for Oracle Drive.
 * Uses AES-256/GCM via Android Keystore for authenticated encryption.
 */
@Singleton
class EncryptionManager @Inject constructor(
    private val keystoreManager: KeystoreManager
) {
    fun encrypt(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data
        return try {
            keystoreManager.encrypt(data)
        } catch (e: Exception) {
            Timber.e(e, "OracleDrive encryption failed")
            throw SecurityException("OracleDrive encryption failed", e)
        }
    }

    fun decrypt(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data
        return try {
            keystoreManager.decrypt(data)
        } catch (e: Exception) {
            Timber.e(e, "OracleDrive decryption failed")
            throw SecurityException("OracleDrive decryption failed", e)
        }
    }

    /**
     * Attempts decryption, falling back to returning raw data if the input
     * appears to be legacy plaintext (pre-hardening). Callers should
     * re-encrypt the returned data to complete the migration.
     */
    fun decryptWithFallback(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data
        return try {
            keystoreManager.decrypt(data)
        } catch (e: Exception) {
            if (isLikelyLegacyPlaintext(data)) {
                Timber.w("OracleDrive: legacy plaintext detected, returning raw data for migration")
                data
            } else {
                throw SecurityException("OracleDrive decryption failed and data is not legacy plaintext", e)
            }
        }
    }

    /**
     * Heuristic: AES-GCM encrypted data has a minimum size of
     * 12 (IV) + 16 (GCM tag) = 28 bytes, and the first 12 bytes
     * are random IV. Legacy plaintext won't follow this structure.
     *
     * For text-based files, we check if the data is valid UTF-8 as
     * an additional signal that it was never encrypted.
     */
    private fun isLikelyLegacyPlaintext(data: ByteArray): Boolean {
        if (data.size < 28) return true
        return try {
            val text = data.decodeToString()
            text.all { it.isDefined() }
        } catch (e: Exception) {
            false
        }
    }
}
