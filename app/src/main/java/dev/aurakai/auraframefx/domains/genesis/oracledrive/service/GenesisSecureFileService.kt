package dev.aurakai.auraframefx.domains.genesis.oracledrive.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.genesis.oracledrive.security.CryptographyManager
import dev.aurakai.auraframefx.domains.genesis.storage.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis-backed secure file service implementation.
 * Handles encrypted file operations for the Oracle Drive system.
 *
 * All files are encrypted at rest using the provided CryptographyManager
 * and stored in app-private storage via SecureStorage.
 */
@Singleton
class GenesisSecureFileService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptographyManager: CryptographyManager,
    private val secureStorage: SecureStorage
) : SecureFileService {

    private val baseDir: File by lazy {
        File(context.filesDir, "genesis_secure_files").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        directory: String?
    ): Flow<FileOperationResult> = flow {
        try {
            val targetDir = if (directory != null) {
                File(baseDir, directory).apply { if (!exists()) mkdirs() }
            } else {
                baseDir
            }

            val file = File(targetDir, fileName)

            // Encrypt the data using CryptographyManager
            val encryptedData = cryptographyManager.encryptData(data)

            // Write encrypted data to file
            file.writeBytes(encryptedData)

            // Store metadata in SecureStorage
            secureStorage.saveEncryptedData(
                key = "file_meta_$fileName",
                data = "size:${data.size},encrypted_size:${encryptedData.size}".toByteArray()
            )

            Timber.d("Successfully saved encrypted file: ${file.absolutePath}")
            emit(FileOperationResult.Success(file))
        } catch (e: Exception) {
            Timber.e(e, "Failed to save file: $fileName")
            emit(FileOperationResult.Error("Failed to save file: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun readFile(
        fileName: String,
        directory: String?
    ): Flow<FileOperationResult> = flow {
        try {
            val targetDir = if (directory != null) {
                File(baseDir, directory)
            } else {
                baseDir
            }

            val file = File(targetDir, fileName)

            if (!file.exists()) {
                emit(FileOperationResult.Error("File not found: $fileName"))
                return@flow
            }

            // Read encrypted data
            val encryptedData = file.readBytes()

            // Decrypt using CryptographyManager
            val decryptedData = cryptographyManager.decryptData(encryptedData)

            Timber.d("Successfully read and decrypted file: ${file.absolutePath}")
            emit(FileOperationResult.Data(decryptedData, fileName))
        } catch (e: Exception) {
            Timber.e(e, "Failed to read file: $fileName")
            emit(FileOperationResult.Error("Failed to read file: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteFile(
        fileName: String,
        directory: String?
    ): FileOperationResult {
        return try {
            val targetDir = if (directory != null) {
                File(baseDir, directory)
            } else {
                baseDir
            }

            val file = File(targetDir, fileName)

            if (!file.exists()) {
                return FileOperationResult.Error("File not found: $fileName")
            }

            // Delete the file
            val deleted = file.delete()

            // Clean up metadata
            secureStorage.deleteEncryptedData("file_meta_$fileName")

            if (deleted) {
                Timber.d("Successfully deleted file: ${file.absolutePath}")
                FileOperationResult.Success(file)
            } else {
                FileOperationResult.Error("Failed to delete file: $fileName")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting file: $fileName")
            FileOperationResult.Error("Error deleting file: ${e.message}", e)
        }
    }

    override suspend fun listFiles(directory: String?): List<String> {
        return try {
            val targetDir = if (directory != null) {
                File(baseDir, directory)
            } else {
                baseDir
            }

            if (!targetDir.exists() || !targetDir.isDirectory) {
                emptyList()
            } else {
                targetDir.listFiles()
                    ?.filter { it.isFile }
                    ?.map { it.nameWithoutExtension }
                    ?: emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error listing files in directory: $directory")
            emptyList()
        }
    }
}
