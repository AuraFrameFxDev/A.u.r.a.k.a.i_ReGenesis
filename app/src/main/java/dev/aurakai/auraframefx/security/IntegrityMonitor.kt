package dev.aurakai.auraframefx.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.data.logging.AuraFxLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Integrity Monitor class for checking critical file integrity.
 */
@Singleton
class IntegrityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AuraFxLogger,
) {
    data class FileViolation(val fileName: String, val timestamp: Long)

    private val criticalFiles = listOf(
        "libNativeCore.so",
        "classes.dex"
    )

    private val storedHashes = mutableMapOf<String, String>()

    suspend fun verifyCriticalFiles(): List<FileViolation> = withContext(Dispatchers.IO) {
        val violations = mutableListOf<FileViolation>()
        criticalFiles.forEach { fileName ->
            val file = File(context.applicationInfo.nativeLibraryDir, fileName)
            if (file.exists()) {
                val currentHash = calculateFileHash(file)
                val storedHash = storedHashes[fileName]
                if (storedHash != null && currentHash != storedHash) {
                    violations.add(FileViolation(fileName, System.currentTimeMillis()))
                } else if (storedHash == null) {
                    storedHashes[fileName] = currentHash
                }
            }
        }
        violations
    }

    private fun calculateFileHash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead = input.read(buffer)
            while (bytesRead != -1) {
                digest.update(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
    
    suspend fun calculateFileHashSync(file: File): String = calculateFileHash(file)
}
