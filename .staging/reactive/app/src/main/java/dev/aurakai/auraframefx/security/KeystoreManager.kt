package dev.aurakai.auraframefx.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KeystoreManager(private val context: Context) {

    private companion object {
        private const val KEY_ALIAS = "AURAFRAMEFX_MAIN_ENC_KEY"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val AES_MODE =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        private const val TAG = "KeystoreManager"
    }

    fun getOrCreateSecretKey(): SecretKey? {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                val parameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).apply {
                    setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    setUserAuthenticationRequired(true) // Require user authentication (e.g., fingerprint, PIN)
                    // Set to false if you don't want the key to be invalidated when new biometrics are enrolled.
                    // For higher security, this could be true, but might require re-encryption of data.
                    setInvalidatedByBiometricEnrollment(false)
                }.build()
                keyGenerator.init(parameterSpec)
                keyGenerator.generateKey()
            }
            return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        } catch (e: KeyStoreException) {
            Log.e(TAG, "Keystore error while getting or creating secret key", e)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Algorithm error while getting or creating secret key", e)
        } catch (e: CertificateException) {
            Log.e(TAG, "Certificate error while getting or creating secret key", e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while loading keystore", e)
        } catch (e: Exception) { // Catch-all for other potential errors during key generation/retrieval
            Log.e(TAG, "Unexpected error while getting or creating secret key", e)
        }
        return null
    }

    fun getEncryptionCipher(): Cipher? {
        try {
            val secretKey = getOrCreateSecretKey()
            if (secretKey == null) {
                Log.e(TAG, "Failed to get or create secret key for encryption cipher.")
                return null
            }
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Algorithm error while getting encryption cipher", e)
        } catch (e: NoSuchPaddingException) {
            Log.e(TAG, "Padding error while getting encryption cipher", e)
        } catch (e: java.security.InvalidKeyException) {
            Log.e(TAG, "Invalid key error while getting encryption cipher", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while getting encryption cipher", e)
        }
        return null
    }

    fun getDecryptionCipher(iv: ByteArray): Cipher? {
        try {
            val secretKey = getOrCreateSecretKey()
            if (secretKey == null) {
                Log.e(TAG, "Failed to get or create secret key for decryption cipher.")
                return null
            }
            val cipher = Cipher.getInstance(AES_MODE)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            return cipher
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Algorithm error while getting decryption cipher", e)
        } catch (e: NoSuchPaddingException) {
            Log.e(TAG, "Padding error while getting decryption cipher", e)
        } catch (e: java.security.InvalidKeyException) {
            Log.e(TAG, "Invalid key error while getting decryption cipher", e)
        } catch (e: java.security.InvalidAlgorithmParameterException) {
            Log.e(TAG, "Invalid algorithm parameter error while getting decryption cipher", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while getting decryption cipher", e)
        }
        return null
    }
}
