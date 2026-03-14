package dev.aurakai.auraframefx.docs.security

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple Genesis-backed secure file service implementation used in docs.
 */
@Singleton
class GenesisSecureFileService @Inject constructor(
    private val manager: SecureFileManager
) : SecureFileService {
    override fun save(name: String, data: ByteArray): File = manager.saveEncryptedFile(name, data)

    override fun read(name: String): ByteArray? = manager.readEncryptedFile(name)

    override fun delete(name: String): Boolean = manager.deleteFile(name)
}
