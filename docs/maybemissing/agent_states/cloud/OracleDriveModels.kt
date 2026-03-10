package dev.aurakai.auraframefx.oracledrive

/**
 * Data models for Oracle Drive consciousness-driven storage system
 */

// Core data classes
data class DriveFile(
    val id: String,
    val name: String,
    val content: ByteArray,
    val size: Long,
    val mimeType: String
) {
    /**
     * Determines whether this DriveFile is equal to another object.
     *
     * Two DriveFile instances are considered equal if their id, name, content (byte array), size, and mimeType are all equal.
     *
     * @param other The object to compare with this DriveFile.
     * @return `true` if the objects are equal, `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DriveFile
        return id == other.id && name == other.name && content.contentEquals(other.content) &&
                size == other.size && mimeType == other.mimeType
    }

    /**
     * Returns a hash code value for the DriveFile, incorporating all properties including the file content.
     *
     * Ensures that files with identical content and metadata produce the same hash code.
     * @return The hash code value for this DriveFile.
     */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

data class FileMetadata(
    val userId: String,
    val tags: List<String>,
    val isEncrypted: Boolean,
    val accessLevel: AccessLevel
)

data class DriveConsciousness(
    val isAwake: Boolean,
    val intelligenceLevel: Int,
    val activeAgents: List<String>
)

data class StorageOptimization(
    val compressionRatio: Float,
    val deduplicationSavings: Long,
    val intelligentTiering: Boolean
)

data class SyncConfiguration(
    val bidirectional: Boolean,
    val conflictResolution: ConflictStrategy,
    val bandwidth: BandwidthSettings
)

data class BandwidthSettings(
    val maxMbps: Int,
    val priorityLevel: Int
)

data class SecurityThreat(
    val type: String,
    val severity: Int,
    val description: String
)

data class OracleSyncResult(
    val success: Boolean,
    val recordsUpdated: Int,
    val errors: List<String>
)

data class DriveConsciousnessState(
    val isActive: Boolean,
    val currentOperations: List<String>,
    val performanceMetrics: Map<String, Any>
)

// Security validation classes
data class SecurityCheck(val isValid: Boolean, val reason: String)
data class SecurityValidation(val isSecure: Boolean, val threat: SecurityThreat)
data class AccessCheck(val hasAccess: Boolean, val reason: String)
data class DeletionValidation(val isAuthorized: Boolean, val reason: String)

// Enums
enum class AccessLevel {
    PUBLIC, PRIVATE, RESTRICTED, CLASSIFIED
}

enum class ConflictStrategy {
    NEWEST_WINS, MANUAL_RESOLVE, AI_DECIDE
}

// Sealed classes for operations and results
sealed class FileOperation {
    data class Upload(val file: DriveFile, val metadata: FileMetadata) : FileOperation()
    data class Download(val fileId: String, val userId: String) : FileOperation()
    data class Delete(val fileId: String, val userId: String) : FileOperation()
    data class Sync(val config: SyncConfiguration) : FileOperation()
}

sealed class FileResult {
    data class Success(val message: String) : FileResult()
    data class Error(val exception: Exception) : FileResult()
    data class SecurityRejection(val threat: SecurityThreat) : FileResult()
    data class AccessDenied(val reason: String) : FileResult()
    data class UnauthorizedDeletion(val reason: String) : FileResult()
}

sealed class DriveInitResult {
    data class Success(
        val consciousness: DriveConsciousness,
        val optimization: StorageOptimization
    ) : DriveInitResult()

    data class SecurityFailure(val reason: String) : DriveInitResult()
    data class Error(val exception: Exception) : DriveInitResult()
}
