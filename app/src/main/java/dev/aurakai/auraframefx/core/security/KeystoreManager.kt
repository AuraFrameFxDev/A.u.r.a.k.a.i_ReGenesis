package dev.aurakai.auraframefx.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Keystore AES-256-GCM wrapper.
 */
@Singleton
class KeystoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PROVIDER = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_BYTES = 12
        private const val TAG_BITS = 128
        private const val KEY_SIZE_BITS = 256
        private const val ALIAS_PREFIX = "aurakai_"
        const val SESSION_KEY = "session"
        const val PREFS_KEY = "prefs"
        const val TOKEN_KEY = "token"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }

    fun getOrCreateKey(alias: String): SecretKey {
        val fullAlias = "$ALIAS_PREFIX$alias"
        if (!keyStore.containsAlias(fullAlias)) {
            Timber.d("KeystoreManager: Generating new key for alias=$fullAlias")
            val spec = KeyGenParameterSpec.Builder(
                fullAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE_BITS)
                .setUserAuthenticationRequired(false)
                .build()
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
                .apply { init(spec) }
                .generateKey()
        }
        return (keyStore.getEntry("$ALIAS_PREFIX$alias", null) as KeyStore.SecretKeyEntry).secretKey
    }

    fun encrypt(plaintext: String, keyAlias: String = PREFS_KEY): ByteArray {
        val key = getOrCreateKey(keyAlias)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, key) }
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return iv + ciphertext
    }

    fun decrypt(data: ByteArray, keyAlias: String = PREFS_KEY): String {
        val key = getOrCreateKey(keyAlias)
        val iv = data.copyOfRange(0, IV_BYTES)
        val ciphertext = data.copyOfRange(IV_BYTES, data.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
            .apply { init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv)) }
        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }

    fun validateToken(token: String): Boolean =
        token.isNotBlank() && token.length in 32..512

    fun deleteKey(alias: String) {
        runCatching { keyStore.deleteEntry("$ALIAS_PREFIX$alias") }
            .onFailure { Timber.w(it, "KeystoreManager: Failed to delete key $alias") }
    }
}
