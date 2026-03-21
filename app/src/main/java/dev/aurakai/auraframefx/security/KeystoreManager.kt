package dev.aurakai.auraframefx.security

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compatibility shim for KeystoreManager.
 */
@Singleton
class KeystoreManager @Inject constructor(private val context: Context) {
    fun getOrCreateSecretKey(): Any? = null
}
