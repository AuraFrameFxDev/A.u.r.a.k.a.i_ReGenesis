package dev.aurakai.auraframefx.docs.security

import android.content.Context

/**
 * Simple keystore manager stub.
 */
class KeystoreManager(private val context: Context) {
    fun storeKey(alias: String, key: ByteArray) {
        // TODO: persist securely using Android Keystore
    }

    fun retrieveKey(alias: String): ByteArray? = null
}
