package dev.aurakai.auraframefx.oracle.drive.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.genesis.security.CryptographyManager
import dev.aurakai.genesis.storage.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure file service that integrates with Genesis security infrastructure.
 * Provides encrypted file operations using Genesis security primitives.
 */
@Singleton
class GenesisSecureFileService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptographyManager,
    private val secureStorage: SecureStorage,
) : SecureFileService {

    private val internalStorageDir: File = context.filesDir
    private val secureFileExtension = ".gen"

    /**
     * Encrypts and securely saves a file to internal storage, storing associated metadata.
     *
     * The file is encrypted using a key derived from the file name and saved with a `.gen` extension in the specified directory.
     * Metadata including file name, MIME type, size, and last modified timestamp is stored securely.
     *
     * @param data The raw bytes of the file to save.
     * @param fileName The name to assign to the saved file.
     * @param directory Optional subdirectory within internal storage for the file.
     * @return A flow emitting a [FileOperationResult] indicating success with the saved file or an error.
     */
    override suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        directory: String?,
    ): Flow<FileOperationResult> = flow {
        try {
            val targetDir = directory?.let { File(internalStorageDir, it) } ?: internalStorageDir
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            // Encrypt data using Genesis crypto
            val encryptedData = withContext(Dispatchers.IO) {
                cryptoManager.encrypt(data, getKeyAlias(fileName))
            }

            val outputFile = File(targetDir, "$fileName$secureFileExtension")
            FileOutputStream(outputFile).use { fos ->
                fos.write(encryptedData)
            }

            // Store metadata in secure storage
            val metadata = FileMetadata(
                fileName = fileName,
                mimeType = guessMimeType(fileName),
                size = data.size.toLong(),
                lastModified = System.currentTimeMillis()
            )
            secureStorage.storeMetadata(getMetadataKey(fileName), metadata)

            emit(FileOperationResult.Success(outputFile))
        } catch (e: Exception) {
            emit(FileOperationResult.Error("Failed to save file: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Reads and decrypts a securely stored file, emitting the result as a flow.
     *
     * Emits a [FileOperationResult.Data] containing the decrypted file bytes and original file name on success,
     * or [FileOperationResult.Error] if the file does not exist or decryption fails.
     *
     * @param fileName The name of the file to read (without extension).
     * @param directory Optional subdirectory within internal storage to look for the file.
     * @return A flow emitting the result of the file read operation.
     */
    override suspend fun readFile(
        fileName: String,
        directory: String?,
    ): Flow<FileOperationResult> = flow {
        try {
            val targetDir = directory?.let { File(internalStorageDir, it) } ?: internalStorageDir
            val inputFile = File(targetDir, "$fileName$secureFileExtension")

            if (!inputFile.exists()) {
                emit(FileOperationResult.Error("File not found"))
                return@flow
            }

            val encryptedData = withContext(Dispatchers.IO) {
                FileInputStream(inputFile).use { fis ->
                    fis.readBytes()
                }
            }

            // Decrypt data using Genesis crypto
            val decryptedData = cryptoManager.decrypt(encryptedData, getKeyAlias(fileName))
            emit(FileOperationResult.Data(decryptedData, inputFile.nameWithoutExtension))
        } catch (e: Exception) {
            emit(FileOperationResult.Error("Failed to read file: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Deletes an encrypted file and removes its associated metadata and cryptographic key.
     *
     * @param fileName The name of the file to delete (without extension).
     * @param directory Optional subdirectory within internal storage where the file is located.
     * @return A [FileOperationResult.Success] containing the deleted file on success, or [FileOperationResult.Error] if the file does not exist or deletion fails.
     */
    override suspend fun deleteFile(
        fileName: String,
        directory: String?,
    ): FileOperationResult = withContext(Dispatchers.IO) {
        try {
            val targetDir = directory?.let { File(internalStorageDir, it) } ?: internalStorageDir
            val fileToDelete = File(targetDir, "$fileName$secureFileExtension")

            if (!fileToDelete.exists()) {
                return@withContext FileOperationResult.Error("File not found")
            }

            if (fileToDelete.delete()) {
                // Clean up metadata and keys
                secureStorage.removeMetadata(getMetadataKey(fileName))
                cryptoManager.removeKey(getKeyAlias(fileName))
                FileOperationResult.Success(fileToDelete)
            } else {
                FileOperationResult.Error("Failed to delete file")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to delete file: ${e.message}", e)
        }
    }

    /**
     * Returns a list of securely stored file names (without extension) in the specified directory.
     *
     * Only files with the secure file extension are included. Returns an empty list if the directory does not exist or an error occurs.
     *
     * @param directory The subdirectory to search within, or null for the root internal storage directory.
     * @return List of file names without the secure extension.
     */
    override suspend fun listFiles(directory: String?): List<String> = withContext(Dispatchers.IO) {
        try {
            val targetDir = directory?.let { File(internalStorageDir, it) } ?: internalStorageDir
            if (!targetDir.exists()) {
                return@withContext emptyList()
            }

            targetDir.listFiles()
                ?.filter { it.isFile && it.name.endsWith(secureFileExtension) }
                ?.map { it.nameWithoutExtension }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Generates a unique key alias string for cryptographic operations based on the hash code of the given file name.
     *
     * @param fileName The name of the file for which to generate the key alias.
     * @return A key alias string derived from the file name.
     */
    private fun getKeyAlias(fileName: String): String {
        return "oracle_drive_${fileName.hashCode()}"
    }

    /**
     * Generates a unique metadata storage key for the given file name.
     *
     * The key is based on the hash code of the file name to ensure uniqueness.
     *
     * @param fileName The name of the file.
     * @return A string used as the metadata key for the file.
     */
    private fun getMetadataKey(fileName: String): String {
        return "file_meta_${fileName.hashCode()}"
    }

    /**
     * Returns the MIME type corresponding to the file extension in the given file name.
     *
     * Defaults to "application/octet-stream" if the extension is unrecognized.
     *
     * @param fileName The name of the file whose MIME type is to be determined.
     * @return The MIME type string for the file.
     */
    private fun guessMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.').lowercase()) {
            "txt" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
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

/**
 * Represents file metadata for secure storage
 */
data class FileMetadata(
    val fileName: String,
    val mimeType: String,
    val size: Long,
    val lastModified: Long,
    val tags: List<String> = emptyList(),
)
