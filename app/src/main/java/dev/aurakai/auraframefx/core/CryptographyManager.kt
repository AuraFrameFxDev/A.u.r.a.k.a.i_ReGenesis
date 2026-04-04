package dev.aurakai.auraframefx.core

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compatibility shim for Genesis CryptographyManager system.
 */
@Singleton
class CryptographyManager @Inject constructor() {
    fun encrypt(data: ByteArray, alias: String): ByteArray = data
    fun decrypt(data: ByteArray, alias: String): ByteArray = data
    fun removeKey(alias: String) {}
}
