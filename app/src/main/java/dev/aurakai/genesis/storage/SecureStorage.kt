package dev.aurakai.genesis.storage

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compatibility shim for Genesis SecureStorage system.
 */
@Singleton
class SecureStorage @Inject constructor() {
    fun storeMetadata(key: String, metadata: Any) {}
    fun removeMetadata(key: String) {}
}
