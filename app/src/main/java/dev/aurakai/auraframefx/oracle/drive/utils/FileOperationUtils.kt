package dev.aurakai.auraframefx.oracle.drive.utils

import dev.aurakai.genesis.logging.Logger
import dev.aurakai.genesis.monitoring.PerformanceMonitor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

/**
 * Utility class for common file operations with proper error handling and logging.
 * Follows Genesis patterns for monitoring and logging.
 */
internal object FileOperationUtils {
    private const val TAG = "FileOperationUtils"
    private val logger = Logger.getLogger(TAG)

    /**
     * Ensures that the specified directory exists, creating it if necessary.
     *
     * Attempts to create the directory and any missing parent directories if they do not already exist.
     * Returns a [Result] indicating success or containing an [IOException] if creation fails.
     *
     * @param directory The directory to check or create.
     * @param coroutineContext The coroutine dispatcher to use for IO operations.
     * @return [Result.success] if the directory exists or was created successfully, or [Result.failure] with an [IOException] on failure.
     */
    suspend fun ensureDirectoryExists(
        directory: File,
        coroutineContext: CoroutineDispatcher = Dispatchers.IO,
    ): Result<Unit> = withContext(coroutineContext) {
        return@withContext try {
            if (!directory.exists()) {
                val created = directory.mkdirs()
                if (!created) {
                    throw IOException("Failed to create directory: ${directory.absolutePath}")
                }
                logger.debug("Created directory: ${directory.absolutePath}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = "Error ensuring directory exists: ${e.message}"
            logger.error(errorMsg, e)
            Result.failure(IOException(errorMsg, e))
        }
    }

    /**
     * Recursively deletes the specified file or directory and all its contents.
     *
     * Performs the deletion operation on the provided coroutine dispatcher. Returns a [Result] indicating success or containing an [IOException] if deletion fails.
     */
    suspend fun deleteFileOrDirectory(
        file: File,
        coroutineContext: CoroutineDispatcher = Dispatchers.IO,
    ): Result<Unit> = withContext(coroutineContext) {
        return@withContext try {
            if (file.exists()) {
                if (file.isDirectory) {
                    file.listFiles()?.forEach { deleteFileOrDirectory(it).getOrThrow() }
                }
                val deleted = file.delete()
                if (!deleted) {
                    throw IOException("Failed to delete: ${file.absolutePath}")
                }
                logger.debug("Deleted: ${file.absolutePath}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = "Error deleting ${file.absolutePath}: ${e.message}"
            logger.error(errorMsg, e)
            Result.failure(IOException(errorMsg, e))
        }
    }

    /**
     * Copies a file from the source to the destination with optional progress reporting.
     *
     * Performs the copy operation asynchronously on the specified coroutine dispatcher. Progress can be tracked via a callback that receives the number of bytes copied and the total bytes. Returns a [Result] indicating success or failure; failure includes an [IOException] if the source file does not exist or an error occurs during copying.
     *
     * @param source The file to copy from.
     * @param destination The file to copy to.
     * @param bufferSize The size of the buffer used for copying, in bytes.
     * @param progressCallback Optional callback invoked with the number of bytes copied and the total bytes after each write.
     * @return [Result.success] if the copy completes successfully, or [Result.failure] with an [IOException] on error.
     */
    suspend fun copyFileWithProgress(
        source: File,
        destination: File,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        coroutineContext: CoroutineDispatcher = Dispatchers.IO,
        progressCallback: ((bytesCopied: Long, totalBytes: Long) -> Unit)? = null,
    ): Result<Unit> = withContext(coroutineContext) {
        val monitor = PerformanceMonitor.start("file_copy")

        return@withContext try {
            if (!source.exists()) {
                throw FileNotFoundException("Source file not found: ${source.absolutePath}")
            }

            FileInputStream(source).use { input ->
                FileOutputStream(destination).use { output ->
                    val buffer = ByteArray(bufferSize)
                    var bytesCopied = 0L
                    val totalBytes = source.length()

                    while (true) {
                        val bytes = input.read(buffer)
                        if (bytes <= 0) break

                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes

                        // Update progress if callback provided
                        progressCallback?.invoke(bytesCopied, totalBytes)
                    }
                }
            }

            monitor.stop()
            logger.debug("Copied ${source.absolutePath} to ${destination.absolutePath}")
            Result.success(Unit)
        } catch (e: Exception) {
            monitor.fail(e)
            val errorMsg =
                "Error copying ${source.absolutePath} to ${destination.absolutePath}: ${e.message}"
            logger.error(errorMsg, e)
            Result.failure(IOException(errorMsg, e))
        }
    }

    /**
     * Validates a file name to ensure it does not contain unsafe or disallowed patterns.
     *
     * Checks for directory traversal sequences, path separators, null characters, and empty or whitespace-only names.
     * Returns a successful result with the file name if valid, or a failure with an exception if invalid.
     *
     * @param fileName The file name to validate.
     * @return A Result containing the valid file name or a failure with the validation exception.
     */
    fun validateFileName(fileName: String): Result<String> {
        return try {
            // Basic validation - prevent directory traversal and other unsafe patterns
            if (fileName.contains("..") ||
                fileName.contains("/") ||
                fileName.contains("\\") ||
                fileName.contains("\0") ||
                fileName.trim().isEmpty()
            ) {
                throw SecurityException("Invalid file name: $fileName")
            }

            // Additional security checks can be added here

            Result.success(fileName)
        } catch (e: Exception) {
            logger.error("File name validation failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Returns the MIME type corresponding to the file extension of the given file name.
     *
     * If the extension is unrecognized, returns "application/octet-stream".
     *
     * @param fileName The name of the file whose MIME type is to be determined.
     * @return The MIME type as a string.
     */
    fun getMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.').lowercase()) {
            "txt", "log", "json", "xml", "html", "css", "js" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "pdf" -> "application/pdf"
            "doc", "docx" -> "application/msword"
            "xls", "xlsx" -> "application/vnd.ms-excel"
            "ppt", "pptx" -> "application/vnd.ms-powerpoint"
            "zip" -> "application/zip"
            "mp3" -> "audio/mpeg"
            "mp4" -> "video/mp4"
            else -> "application/octet-stream"
        }
    }
}
