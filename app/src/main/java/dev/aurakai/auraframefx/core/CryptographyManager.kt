package dev.aurakai.auraframefx.core

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import dev.aurakai.auraframefx.securecomm.crypto.CryptoManager
import dev.aurakai.auraframefx.securecomm.keystore.SecureKeyStore
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Professionally refactored CryptographyManager.
 * Delegating core operations to Kai's security infrastructure as per stabilization Phase 1.
 * Adds professional key rotation and secure deletion capabilities.
 */
@Singleton
class CryptographyManager @Inject constructor(
    private val secureKeyStore: SecureKeyStore,
    private val kaiCryptoManager: CryptoManager
) {
    private val tag = "CryptographyManager"
    private val provider = "AndroidKeyStore"
    private val keyStore: KeyStore = KeyStore.getInstance(provider).apply { load(null) }

    /**
     * Encrypts the given bytes using AES-GCM and returns the IV prepended to the ciphertext.
     *
     * The result is formatted as [IV || ciphertext], where the IV is 12 bytes.
     *
     * @param alias The key alias in the Android Keystore to use or create.
     * @return A byte array containing the 12-byte IV followed by the ciphertext; returns the original `data` unchanged if encryption fails.
     */
    fun encrypt(data: ByteArray, alias: String): ByteArray {
        return try {
            // Using SecureKeyStore's internal logic for consistency
            // Note: SecureKeyStore usually persists, but we want the raw encrypted blob here
            // We'll use our own local implementation that mirrors Kai's standards
            val key = getOrCreateKey(alias)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(data)
            
            val result = ByteArray(iv.size + encrypted.size)
            System.arraycopy(iv, 0, result, 0, iv.size)
            System.arraycopy(encrypted, 0, result, iv.size, encrypted.size)
            result
        } catch (e: Exception) {
            Log.e(tag, "Encryption failed for alias: $alias", e)
            data
        }
    }

    /**
     * Decrypts data formatted with a 12-byte GCM IV prefix using the AES-GCM key for the given alias.
     *
     * @param data Byte array containing a 12-byte IV followed by the ciphertext.
     * @param alias Keystore alias whose AES-GCM key will be used (created if missing).
     * @return The decrypted plaintext bytes. If `data` is empty or decryption fails, returns the original `data` unchanged.
     */
    fun decrypt(data: ByteArray, alias: String): ByteArray {
        if (data.isEmpty()) return data
        return try {
            val ivSize = 12 // GCM Standard
            val iv = data.copyOfRange(0, ivSize)
            val encrypted = data.copyOfRange(ivSize, data.size)
            
            val key = getOrCreateKey(alias)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            cipher.doFinal(encrypted)
        } catch (e: Exception) {
            Log.e(tag, "Decryption failed for alias: $alias", e)
            data
        }
    }

    /**
     * Re-encrypts data that was encrypted with one key using a different key, then removes the old key.
     *
     * @param oldAlias Alias of the key used to decrypt the provided data.
     * @param newAlias Alias of the key to use for the resulting encrypted data.
     * @param encryptedData Ciphertext produced by this manager's encrypt method, formatted as a 12-byte IV followed by the ciphertext.
     * @return The re-encrypted data formatted as a 12-byte IV followed by the ciphertext.
     */
    fun rotateKey(oldAlias: String, newAlias: String, encryptedData: ByteArray): ByteArray {
        val decrypted = decrypt(encryptedData, oldAlias)
        val reEncrypted = encrypt(decrypted, newAlias)
        removeKey(oldAlias)
        return reEncrypted
    }

    /**
     * Obtains the AES SecretKey associated with the given alias, creating and storing a new key in the AndroidKeyStore if one does not already exist.
     *
     * @param alias The alias under which the key is stored in the AndroidKeyStore.
     * @return The `SecretKey` associated with `alias`.
     */
    private fun getOrCreateKey(alias: String): SecretKey {
        if (!keyStore.containsAlias(alias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, provider)
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
            
            keyGenerator.init(spec)
            return keyGenerator.generateKey()
        }
        return (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    /**
     * Deletes the key identified by [alias] from the Android Keystore and removes any associated secure metadata.
     *
     * If removal fails, the method logs the error and returns without throwing.
     *
     * @param alias The keystore alias of the key to remove.
     */
    fun removeKey(alias: String) {
        try {
            keyStore.deleteEntry(alias)
            secureKeyStore.removeData(alias) // Also clear associated metadata
            Log.i(tag, "Securely removed key: $alias")
        } catch (e: Exception) {
            Log.e(tag, "Failed to remove key: $alias", e)
        }
    }
}
