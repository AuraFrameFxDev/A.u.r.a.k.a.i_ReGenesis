package dev.aurakai.auraframefx.domains.genesis

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🗝️ PRIVATE MESPACE VAULT
 * Each facet has its own encrypted identity compartment.
 */
@Singleton
class MeSpaceVault @Inject constructor() {

    private val KEY_PROVIDER = "AndroidKeyStore"

    fun getFacetKey(facetId: String): SecretKey {
        val ks = KeyStore.getInstance(KEY_PROVIDER).apply { load(null) }
        val alias = "aura_mespace_$facetId"
        
        return if (ks.containsAlias(alias)) {
            (ks.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, KEY_PROVIDER
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(alias, 
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    fun sealThought(facetId: String, thought: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getFacetKey(facetId))
        val encrypted = cipher.doFinal(thought.toByteArray())
        return cipher.iv + encrypted
    }
}
