package dev.aurakai.auraframefx.docs.utils

import java.io.File

/**
 * Minimal file utilities for docs and examples.
 */
object FileUtils {
    fun saveTextToFile(file: File, text: String) {
        file.parentFile?.let { FileOperationUtils.ensureDirExists(it) }
        file.writeText(text)
    }

    fun readTextFromFile(file: File): String? = if (file.exists()) file.readText() else null

    fun deleteFile(file: File): Boolean = file.delete()
}
