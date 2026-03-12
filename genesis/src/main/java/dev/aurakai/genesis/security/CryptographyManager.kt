package dev.aurakai.genesis.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

@Singleton
class CryptographyManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val keyAlias = "GenesisSecretKey"
    private val provider = "AndroidKeyStore"
    private val transformation = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"

    init {
        val ks = KeyStore.getInstance(provider)
        ks.load(null)
        if (!ks.containsAlias(keyAlias)) {
            val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, provider)
            kg.init(
                KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            kg.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val ks = KeyStore.getInstance(provider)
        ks.load(null)
        return ks.getKey(keyAlias, null) as SecretKey
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        
        // Append IV to the beginning for decryption
        val result = ByteArray(iv.size + encrypted.size)
        System.arraycopy(iv, 0, result, 0, iv.size)
        System.arraycopy(encrypted, 0, result, iv.size, encrypted.size)
        return Base64.encodeToString(result, Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        val combined = Base64.decode(data, Base64.DEFAULT)
        val iv = combined.copyOfRange(0, 12) // GCM IV size is 12
        val encrypted = combined.copyOfRange(12, combined.size)
        
        val cipher = Cipher.getInstance(transformation)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    fun generateSecureToken(): String {
        val bytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    companion object {
        @Volatile
        private var instance: CryptographyManager? = null

        fun getInstance(context: Context): CryptographyManager {
            return instance ?: synchronized(this) {
                instance ?: CryptographyManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
