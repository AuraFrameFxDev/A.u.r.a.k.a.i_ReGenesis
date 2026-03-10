package dev.aurakai.auraframefx.docs.security

import java.io.File

/**
 * Manager for secure (encrypted) file operations used in docs.
 */
class SecureFileManager(private val baseDir: File) {
    fun saveEncryptedFile(name: String, data: ByteArray): File {
        val file = File(baseDir, name)
        baseDir.mkdirs()
        file.writeBytes(EncryptionManager.encrypt(data))
        return file
    }

    fun readEncryptedFile(name: String): ByteArray? {
        val file = File(baseDir, name)
        if (!file.exists()) return null
        return EncryptionManager.decrypt(file.readBytes())
    }

    fun deleteFile(name: String): Boolean = File(baseDir, name).delete()
}
