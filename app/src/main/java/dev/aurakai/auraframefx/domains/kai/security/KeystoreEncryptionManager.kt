package dev.aurakai.auraframefx.domains.kai.security

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of [EncryptionManager] using [KeystoreManager].
 * Delegates to hardware-backed AES-256/GCM via Android Keystore.
 * Replaces [NoopEncryptionManager] for production use.
 */
@Singleton
class KeystoreEncryptionManager @Inject constructor(
    private val keystoreManager: KeystoreManager
) : EncryptionManager {

    override fun encrypt(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data
        return try {
            keystoreManager.encrypt(data)
        } catch (e: Exception) {
            Timber.e(e, "KeystoreEncryptionManager: encryption failed")
            throw SecurityException("Encryption failed", e)
        }
    }

    override fun decrypt(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data
        return try {
            keystoreManager.decrypt(data)
        } catch (e: Exception) {
            Timber.e(e, "KeystoreEncryptionManager: decryption failed")
            throw SecurityException("Decryption failed", e)
        }
    }
}
