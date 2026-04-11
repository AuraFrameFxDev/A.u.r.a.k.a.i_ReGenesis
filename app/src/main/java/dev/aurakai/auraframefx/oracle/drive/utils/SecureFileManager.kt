package dev.aurakai.auraframefx.oracle.drive.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
 * Handles secure file operations using Toolshed's encryption.
 * Integrates with the Genesis protocol for secure storage and retrieval.
 */
@Singleton
class SecureFileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager,
) {
    private val internalStorageDir: File = context.filesDir
    private val secureFileExtension = ".aes"

    /**
     * Encrypts and saves data as a file in internal storage, emitting the operation result as a Flow.
     *
     * The file is saved with a `.aes` extension in the specified subdirectory or the default internal directory.
     * Emits a `Success` result with the saved file on success, or an `Error` with details on failure.
     *
     * @param data The raw bytes to encrypt and save.
     * @param fileName The desired name for the saved file (without extension).
     * @param directory Optional subdirectory within internal storage to save the file.
     * @return A Flow emitting the result of the save operation as a `FileOperationResult`.
     */
    suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        directory: String? = null,
    ): Flow<FileOperationResult> = flow {
        try {
            val targetDir = directory?.let { File(internalStorageDir, it) } ?: internalStorageDir
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val encryptedData = withContext(Dispatchers.IO) {
                encryptionManager.encrypt(data)
            }

            val outputFile = File(targetDir, "$fileName$secureFileExtension")
            FileOutputStream(outputFile).use { fos ->
                fos.write(encryptedData)
            }

            emit(FileOperationResult.Success(outputFile))
        } catch (e: Exception) {
            emit(FileOperationResult.Error("Failed to save file: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Reads and decrypts an encrypted file, emitting the result as a flow.
     *
     * Attempts to locate and decrypt a file with the `.aes` extension in the specified or default directory.
     * Emits a [FileOperationResult.Data] containing the decrypted bytes and file name on success,
     * or a [FileOperationResult.Error] if the file is missing or decryption fails.
     *
     * @param fileName The name of the file to read (without extension).
     * @param directory Optional subdirectory within internal storage to search for the file.
     * @return A flow emitting the result of the file read and decryption operation.
     */
    suspend fun readFile(
        fileName: String,
        directory: String? = null,
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

            val decryptedData = encryptionManager.decrypt(encryptedData)
            emit(FileOperationResult.Data(decryptedData, inputFile.nameWithoutExtension))
        } catch (e: Exception) {
            emit(FileOperationResult.Error("Failed to read file: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Deletes an encrypted file from internal storage.
     *
     * Attempts to remove the specified file with the secure extension from the given directory. Returns a result indicating success or failure, including error details if the file does not exist or deletion fails.
     *
     * @param fileName The name of the file to delete (without extension).
     * @param directory Optional subdirectory within internal storage.
     * @return A [FileOperationResult] representing the outcome of the deletion.
     */
    suspend fun deleteFile(
        fileName: String,
        directory: String? = null,
    ): FileOperationResult = withContext(Dispatchers.IO) {
        try {
            val targetDir = directory?.let { File(internalStorageDir, it) } ?: internalStorageDir
            val fileToDelete = File(targetDir, "$fileName$secureFileExtension")

            if (!fileToDelete.exists()) {
                return@withContext FileOperationResult.Error("File not found")
            }

            if (fileToDelete.delete()) {
                FileOperationResult.Success(fileToDelete)
            } else {
                FileOperationResult.Error("Failed to delete file")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to delete file: ${e.message}", e)
        }
    }

    /**
     * Returns a list of decrypted file names (without extensions) from the specified directory.
     *
     * Only files with the secure encrypted extension are included. Returns an empty list if the directory does not exist or an error occurs.
     *
     * @param directory Optional subdirectory to search within the internal storage directory.
     * @return List of file names without the encrypted extension.
     */
    suspend fun listFiles(directory: String? = null): List<String> = withContext(Dispatchers.IO) {
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
}

/**
 * Represents the result of a file operation
 */
sealed class FileOperationResult {
    data class Success(val file: File) : FileOperationResult()
    data class Data(val data: ByteArray, val fileName: String) : FileOperationResult()
    data class Error(val message: String, val exception: Exception? = null) : FileOperationResult()
}
