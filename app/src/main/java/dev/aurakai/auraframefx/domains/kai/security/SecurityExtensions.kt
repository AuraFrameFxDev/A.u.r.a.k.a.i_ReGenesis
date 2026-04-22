package dev.aurakai.auraframefx.domains.kai.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel

/**
 */

/**
 * Check if the security context is in a secure state
 */
    return securityState.value.errorState == false &&
            encryptionStatus.value == EncryptionStatus.ACTIVE
}

/**
 * Check if the security context is NOT secure
 */
    return !this.isSecure()
}

/**
 * Get the current threat count
 */
    return securityState.value.detectedThreats.size
}

/**
 * Check if encryption is ready for use
 */
    return encryptionStatus.value == EncryptionStatus.ACTIVE
}

/**
 * Safely encrypt data with null handling
 */
    if (data == null) return null
    return encrypt(data)
}

/**
 * Safely decrypt data with null handling
 */
    if (encryptedData == null) return null
    return decrypt(encryptedData)
}

/**
 * Check if a specific threat type is present
 */
    return securityState.value.detectedThreats.any { it.type == type }
}

/**
 * Get threats by severity level
 */
    return securityState.value.detectedThreats.filter { it.severity == severity }
}

/**
 * Android Keystore Helper Extensions
 */

/**
 * Generate a new AES secret key in Android Keystore
 */
fun generateKeystoreSecretKey(alias: String): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        "AndroidKeyStore"
    )

    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(false)
        .setRandomizedEncryptionRequired(true)
        .build()

    keyGenerator.init(keyGenParameterSpec)
    return keyGenerator.generateKey()
}

/**
 * Retrieve an existing secret key from Android Keystore
 */
fun getKeystoreSecretKey(alias: String): SecretKey? {
    return try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.getKey(alias, null) as? SecretKey
    } catch (e: Exception) {
        null
    }
}

/**
 * Check if a key exists in Android Keystore
 */
fun keystoreContainsKey(alias: String): Boolean {
    return try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.containsAlias(alias)
    } catch (e: Exception) {
        false
    }
}

/**
 * Delete a key from Android Keystore
 */
fun deleteKeystoreKey(alias: String): Boolean {
    return try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.deleteEntry(alias)
        true
    } catch (e: Exception) {
        false
    }
}
