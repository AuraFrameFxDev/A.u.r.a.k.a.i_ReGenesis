package dev.aurakai.auraframefx.domains.kai.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

fun SecurityContext.isDegraded(): Boolean {
    return !this.isSecure()
}

fun SecurityContext.threatCount(): Int {
    return securityState.value.detectedThreats.size
}

fun SecurityContext.canEncrypt(): Boolean {
    return encryptionStatus.value == EncryptionStatus.ACTIVE
}

fun SecurityContext.safeEncrypt(data: String?): EncryptedData? {
    if (data == null) return null
    return encrypt(data)
}

fun SecurityContext.hasThreat(type: ThreatType): Boolean {
    return securityState.value.detectedThreats.any { it.type == type }
}

fun SecurityContext.getThreatsBySeverity(severity: ThreatSeverity): List<SecurityThreat> {
    return securityState.value.detectedThreats.filter { it.severity == severity }
}

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

fun getKeystoreSecretKey(alias: String): SecretKey? {
    return try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.getKey(alias, null) as? SecretKey
    } catch (e: Exception) {
        null
    }
}

fun keystoreContainsKey(alias: String): Boolean {
    return try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.containsAlias(alias)
    } catch (e: Exception) {
        false
    }
}

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
