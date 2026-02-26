package dev.aurakai.auraframefx.domains.cascade.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.IOException

/**
 * Utility class for file operations.
 * Provides safe and consistent file operations across different Android versions.
 */
object FileOperationUtils {
    private const val TAG = "FileOperationUtils"

    /**
     * Creates a directory in the app's internal storage.
     * @param context The application context.
     * @param dirName The name of the directory to create.
     * @return The created directory.
     * @throws IOException If the directory cannot be created.
     */
    @Throws(IOException::class)
    fun createDirectory(context: Context, dirName: String): File {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File(context.dataDir, dirName)
        } else {
            @Suppress("DEPRECATION")
            File(context.filesDir, dirName)
        }.apply {
            if (!exists()) {
                mkdirs()
            }
        }

        return if (dir.exists()) {
            dir
        } else {
            throw IOException("Failed to create directory: ${dir.absolutePath}")
        }
    }

    /**
     * Deletes a file or directory recursively.
     * @param file The file or directory to delete.
     * @return true if all files were successfully deleted, false otherwise.
     */
    fun deleteRecursively(file: File): Boolean {
        return if (file.isDirectory) {
            file.listFiles()?.all { deleteRecursively(it) } ?: false && file.delete()
        } else {
            file.delete()
        }
    }

    /**
     * Gets the size of a file or directory in bytes.
     * @param file The file or directory to get the size of.
     * @return The size in bytes.
     */
    fun getFileSize(file: File): Long {
        return if (file.isDirectory) {
            file.listFiles()?.sumOf { getFileSize(it) } ?: 0L
        } else {
            file.length()
        }
    }

    /**
     * Gets the external storage directory for the app.
     * @param context The application context.
     * @param type The type of directory to get (e.g., Environment.DIRECTORY_DOCUMENTS).
     * @return The external storage directory.
     */
    @Suppress("DEPRECATION")
    fun getExternalStorageDir(context: Context, type: String? = null): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(type) ?: context.filesDir
        } else {
            val directory = if (type != null) {
                Environment.getExternalStoragePublicDirectory(type)
            } else {
                context.getExternalFilesDir(null)
            }
            directory ?: context.filesDir
        }.also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
    }

    /**
     * Checks if external storage is available for read and write.
     */
    val isExternalStorageWritable: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * Checks if external storage is available to at least read.
     */
    val isExternalStorageReadable: Boolean
        get() = Environment.getExternalStorageState() in setOf(
            Environment.MEDIA_MOUNTED,
            Environment.MEDIA_MOUNTED_READ_ONLY
        )
}

