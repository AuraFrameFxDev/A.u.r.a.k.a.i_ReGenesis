package dev.aurakai.auraframefx.docs.utils

import java.io.File
import java.io.InputStream

/**
 * Small file operation helpers for docs and examples.
 */
object FileOperationUtils {
    fun ensureDirExists(dir: File): Boolean {
        if (dir.exists()) return dir.isDirectory
        return dir.mkdirs()
    }

    fun safeDeleteRecursively(target: File): Boolean = try {
        if (!target.exists()) return true
        target.deleteRecursively()
    } catch (e: Exception) {
        false
    }

    fun copyStreamToFile(input: InputStream, dest: File) {
        input.use { inputStream ->
            dest.outputStream().use { out ->
                inputStream.copyTo(out)
            }
        }
    }
}
