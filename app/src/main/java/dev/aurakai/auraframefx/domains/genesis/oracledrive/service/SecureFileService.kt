package dev.aurakai.auraframefx.domains.genesis.oracledrive.service

import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Defines the contract for secure file operations in the Oracle Drive system.
 * All file operations are encrypted at rest and in transit.
 */
interface SecureFileService {

    /**
     * Saves encrypted data to a file, optionally within a specified subdirectory.
     *
     * Emits the result of the save operation as a flow, including success or error states.
     *
     * @param data The byte array to be securely saved.
     * @param fileName The target file name.
     * @param directory The optional subdirectory in which to save the file.
     * @return A flow emitting the result of the file save operation.
     */
    suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        directory: String? = null,
    ): Flow<FileOperationResult>

    /**
     * Reads and decrypts the specified file, optionally from a subdirectory.
     *
     * @param fileName The name of the file to read.
     * @param directory The subdirectory to read from, or null for the root directory.
     * @return A Flow emitting the file data or an error result.
     */
    suspend fun readFile(
        fileName: String,
        directory: String? = null,
    ): Flow<FileOperationResult>

    /**
     * Securely deletes the specified file, optionally from a given subdirectory.
     *
     * @param fileName The name of the file to delete.
     * @param directory The subdirectory containing the file, or null for the root directory.
     * @return The result of the delete operation.
     */
    suspend fun deleteFile(
        fileName: String,
        directory: String? = null,
    ): FileOperationResult

    /**
     * Returns a list of file names (without extensions) in the specified directory.
     *
     * If no directory is provided, lists files in the root directory.
     *
     * @param directory The optional subdirectory to list files from.
     * @return A list of file names without their extensions.
     */
    suspend fun listFiles(directory: String? = null): List<String>
}

/**
 * Represents the result of a file operation.
 */
sealed class FileOperationResult {
    data class Success(val file: File) : FileOperationResult()
    data class Data(val data: ByteArray, val fileName: String) : FileOperationResult()
    data class Error(val message: String, val exception: Exception? = null) : FileOperationResult()

    /**
     * Determines whether this [FileOperationResult] is equal to another object.
     *
     * Compares the type and relevant properties of the instances, including deep content comparison for byte arrays in [Data].
     *
     * @param other The object to compare with.
     * @return `true` if the objects are of the same type and have equal properties; otherwise, `false`.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileOperationResult

        return when (this) {
            is Success -> other is Success && file == other.file
            is Data -> other is Data && data.contentEquals(other.data) && fileName == other.fileName
            is Error -> other is Error && message == other.message && exception == other.exception
        }
    }

    /**
     * Returns a hash code value for the `FileOperationResult` instance based on its type and contained data.
     *
     * Ensures that equal instances produce the same hash code, supporting correct usage in hash-based collections.
     */
    override fun hashCode(): Int {
        return when (this) {
            is Success -> file.hashCode()
            is Data -> data.contentHashCode() + fileName.hashCode()
            is Error -> message.hashCode() + (exception?.hashCode() ?: 0)
        }
    }
}
