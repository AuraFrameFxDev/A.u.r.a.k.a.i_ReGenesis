package dev.aurakai.auraframefx.domains.genesis.storage

import kotlinx.serialization.Serializable

/**
 * Metadata for files stored in secure storage
 */
@Serializable
data class FileMetadata(
    val fileId: String,
    val fileName: String,
    val mimeType: String = "application/octet-stream",
    val size: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val checksum: String? = null,
    val encryptionKey: String? = null,
    val isEncrypted: Boolean = false,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val path: String = "",
    val parentId: String? = null,
    val version: Int = 1
)

/**
 * Result of storage operations
 */
@Serializable
sealed class StorageResult<out T> {
    @Serializable
    data class Success<T>(val data: T, val message: String = "Success") : StorageResult<T>()

    @Serializable
    data class Error(val error: String, val code: Int = -1) : StorageResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}

/**
 * File result wrapper
 */
@Serializable
data class FileResult(
    val success: Boolean,
    val fileId: String? = null,
    val path: String? = null,
    val size: Long = 0,
    val message: String = "",
    val metadata: FileMetadata? = null,
    val data: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileResult
        if (success != other.success) return false
        if (fileId != other.fileId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + (fileId?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun success(
            fileId: String,
            path: String = "",
            size: Long = 0,
            message: String = "Success"
        ) =
            FileResult(true, fileId, path, size, message)

        fun error(message: String) = FileResult(false, message = message)
    }
}
