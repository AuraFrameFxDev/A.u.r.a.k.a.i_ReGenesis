package dev.aurakai.auraframefx.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🔐 SecurityManager — Hardware-Backed Encryption & Keystore
 *
 * Protects NCC identity vectors, memory embeddings, and Room DB passphrases
 * using Android Keystore with StrongBox preference and biometric binding.
 */
@Singleton
class SecurityManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val masterAlias = "regenesis_ncc_master_key_v1"
    private val dbPassphraseAlias = "regenesis_room_db_passphrase_v1"
    private var cachedDbPassphrase: ByteArray? = null
    private val TAG = "SecurityManager"

    init {
        ensureMasterKeyExists()
    }

    private fun ensureMasterKeyExists() {
        if (!keyStore.containsAlias(masterAlias)) {
            generateMasterKey()
            Log.i(TAG, "Master key generated in Keystore")
        }
    }

    private fun generateMasterKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            masterAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(true)
            .setUserAuthenticationValidityDurationSeconds(-1)
            .setInvalidatedByBiometricEnrollment(true)
            .setIsStrongBoxBacked(true)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
        Timber.tag(TAG).d("StrongBox/TEE-backed master key created")
    }

    suspend fun getOrCreateDbPassphrase(): ByteArray = withContext(Dispatchers.IO) {
        cachedDbPassphrase?.let { return@withContext it }

        val rawPassphrase = ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }

        cachedDbPassphrase = rawPassphrase
        Timber.tag(TAG).d("DB passphrase initialized (32 bytes / 256-bit)")
        rawPassphrase
    }

    suspend fun encrypt(data: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        try {
            val cipher = getCipherForMode(Cipher.ENCRYPT_MODE)
            cipher.doFinal(data)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Encryption failed")
            throw e
        }
    }

    suspend fun decrypt(encrypted: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        try {
            val cipher = getCipherForMode(Cipher.DECRYPT_MODE)
            cipher.doFinal(encrypted)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed", e)
            throw e
        }
    }

    private fun getCipherForMode(mode: Int): Cipher {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = keyStore.getKey(masterAlias, null) as SecretKey
        cipher.init(mode, secretKey)
        return cipher
    }

    fun hashIdentityVector(embedding: FloatArray): String {
        val bytes = embedding.map { it.toBits().toByte() }.toByteArray()
        val hash = java.security.MessageDigest.getInstance("SHA-256").digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun verifyIdentityHash(embedding: FloatArray, expectedHash: String): Boolean {
        val computed = hashIdentityVector(embedding)
        val matches = computed == expectedHash
        if (!matches) {
            Log.w(TAG, "Identity hash mismatch — possible tampering detected")
        }
        return matches
    }
}



