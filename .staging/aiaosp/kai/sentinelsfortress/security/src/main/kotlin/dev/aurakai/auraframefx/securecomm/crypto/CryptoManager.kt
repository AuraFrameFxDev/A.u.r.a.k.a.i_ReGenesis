package dev.aurakai.auraframefx.securecomm.crypto

import android.annotation.TargetApi
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manages cryptographic operations for secure communications.
 * Handles key generation, key agreement, and secure key storage.
 */
@Singleton
class CryptoManager @Inject constructor(
    @get:ApplicationContext private val context: Context
) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val keyPairAlias = "aura_ec_keypair"
    private val keyAgreementAlgorithm = KeyProperties.KEY_ALGORITHM_EC
    private val keyAgreementProvider = "AndroidKeyStore"
    private val keyAgreementCurve = "secp256r1"
    private val keyAgreementKeySize = 256

    /**
     * Generates or retrieves the device's key pair for ECDH key exchange.
     */
    @TargetApi(24)
    fun getOrCreateKeyPair(): KeyPair {
        if (!keyStore.containsAlias(keyPairAlias)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                keyAgreementProvider
            )

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyPairAlias,
                KeyProperties.PURPOSE_AGREE_KEY
            ).apply {
                setAlgorithmParameterSpec(ECGenParameterSpec(keyAgreementCurve))
                setKeySize(keyAgreementKeySize)
                setUserAuthenticationRequired(false)
                setInvalidatedByBiometricEnrollment(false)
            }.build()

            keyPairGenerator.initialize(keyGenParameterSpec)
            keyPairGenerator.generateKeyPair()
        }

        val privateKey = keyStore.getKey(keyPairAlias, null) as PrivateKey
        val publicKey = keyStore.getCertificate(keyPairAlias).publicKey

        return KeyPair(publicKey, privateKey)
    }

    /**
     * Performs ECDH key agreement to derive a shared secret.
     * @param privateKey The local private key.
     * @param publicKey The remote party's public key.
     * @return The derived shared secret.
     */
    fun performKeyAgreement(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        return keyAgreement.generateSecret()
    }

    /**
     * Derives a secure session key from a shared secret using HKDF.
     * @param sharedSecret The shared secret from ECDH.
     * @param salt Optional salt for HKDF.
     * @param info Optional context/application-specific info for HKDF.
     * @return A derived session key.
     */
    fun deriveSessionKey(
        sharedSecret: ByteArray,
        salt: ByteArray = ByteArray(32),
        info: ByteArray = "AuraFrameFX-SecureComm".toByteArray()
    ): SecretKey {
        // Using Bouncy Castle's HKDF implementation
        val hkdf = org.bouncycastle.crypto.generators.HKDFBytesGenerator(
            org.bouncycastle.crypto.digests.SHA256Digest()
        )

        val params = org.bouncycastle.crypto.params.HKDFParameters(
            sharedSecret,
            salt,
            info
        )

        hkdf.init(params)
        val derivedKey = ByteArray(32) // 256-bit key
        hkdf.generateBytes(derivedKey, 0, derivedKey.size)

        return SecretKeySpec(derivedKey, "AES")
    }

    /**
     * Encrypts data using AES-GCM.
     * @param data The data to encrypt.
     * @param key The encryption key.
     * @return A pair of (ciphertext, iv).
     */
    fun encrypt(data: ByteArray, key: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(data)
        return ciphertext to iv
    }

    /**
     * Asynchronously encrypts data with AES-GCM and returns the ciphertext and IV.
     *
     * This suspend variant performs the encryption off the caller thread (uses Dispatchers.Default)
     * to avoid blocking. The returned IV must be supplied to the corresponding decrypt call.
     *
     * @param data Plaintext bytes to encrypt.
     * @param key AES SecretKey used for encryption (expects 128/256-bit AES key).
     * @return Pair where the first element is the ciphertext and the second is the initialization vector (IV).
     */
    suspend fun encryptAsync(data: ByteArray, key: SecretKey): Pair<ByteArray, ByteArray> =
        withContext(Dispatchers.Default) {
            val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val ciphertext = cipher.doFinal(data)
            ciphertext to iv
        }

    /**
     * Decrypts data using AES-GCM.
     * @param encryptedData The encrypted data.
     * @param key The decryption key.
     * @param iv The initialization vector.
     * @return The decrypted data.
     */
    fun decrypt(encryptedData: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        val spec = javax.crypto.spec.GCMParameterSpec(128, iv)
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encryptedData)
    }

    /**
     * Signs data using the device's private key.
     * @param data The data to sign.
     * @return The signature.
     */
    fun sign(data: ByteArray): ByteArray {
        val privateKey = getOrCreateKeyPair().private
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    /**
     * Verifies a signature using the device's public key.
     * @param data The original data.
     * @param signature The signature to verify.
     * @return True if the signature is valid, false otherwise.
     */
    fun verify(data: ByteArray, signature: ByteArray): Boolean {
        return try {
            val publicKey = getOrCreateKeyPair().public
            val sig = Signature.getInstance("SHA256withECDSA")
            sig.initVerify(publicKey)
            sig.update(data)
            sig.verify(signature)
        } catch (e: Exception) {
            false
        }
    }
}
