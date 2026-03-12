package dev.aurakai.auraframefx.docs.security

import java.io.File

interface SecureFileService {
    fun save(name: String, data: ByteArray): File
    fun read(name: String): ByteArray?
    fun delete(name: String): Boolean
}
