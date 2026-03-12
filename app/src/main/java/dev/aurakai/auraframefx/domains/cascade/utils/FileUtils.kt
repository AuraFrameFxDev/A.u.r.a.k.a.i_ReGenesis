package dev.aurakai.auraframefx.domains.cascade.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Utility class for file operations.
 */
object FileUtils {
    private const val TAG = "FileUtils"

    /**
     * Saves the given string content to a file in the application's internal storage.
     *
     * @param fileName The name of the file to save.
     * @param content The string content to write to the file.
     * @return `true` if the content was successfully written; `false` if an I/O error occurred.
     */
    fun saveToFile(context: Context, fileName: String, content: String): Boolean {
        return try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(content.toByteArray())
                true
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing to file $fileName", e)
            false
        }
    }

    /**
     * Reads the contents of a file from the application's internal storage and returns it as a string.
     *
     * @param fileName The name of the file to read.
     * @return The file's contents as a string, or `null` if the file does not exist or an I/O error occurs.
     */
    fun readFromFile(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().use { it.readText() }
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: $fileName")
            null
        } catch (e: IOException) {
            Log.e(TAG, "Error reading from file $fileName", e)
            null
        }
    }

    /**
     * Checks if a file with the given name exists in the application's internal storage directory.
     *
     * @param fileName The name of the file to check.
     * @return `true` if the file exists, `false` otherwise.
     */
    fun fileExists(context: Context, fileName: String): Boolean {
        return File(context.filesDir, fileName).exists()
    }

    /**
     * Deletes a file with the specified name from the application's internal storage.
     *
     * @param fileName The name of the file to delete.
     * @return `true` if the file was successfully deleted, or `false` if deletion failed.
     */
    fun deleteFile(context: Context, fileName: String): Boolean {
        return try {
            context.deleteFile(fileName)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting file $fileName", e)
            false
        }
    }

    /****
     * Retrieves the absolute file system path of a file within the application's internal storage directory.
     *
     * @param fileName The name of the file.
     * @return The absolute path to the specified file in the app's internal storage.
     */
    fun getFilePath(context: Context, fileName: String): String {
        return File(context.filesDir, fileName).absolutePath
    }
}

