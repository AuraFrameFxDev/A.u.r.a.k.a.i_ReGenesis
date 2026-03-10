// Minimal model stubs to satisfy compilation for genesis/oracledrive module
package dev.aurakai.auraframefx.oracledrive.genesis.cloud

// Basic DriveFile model
data class DriveFile(
    val id: String = "",
    val name: String = "",
    val content: ByteArray = ByteArray(0),
    val size: Long = 0L,
    val mimeType: String = "application/octet-stream"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DriveFile

        if (size != other.size) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

// Metadata for files
data class FileMetadata(
    val userId: String = "",
    val tags: List<String> = emptyList(),
    val isEncrypted: Boolean = false,
    val accessLevel: AccessLevel = AccessLevel.PRIVATE
)

enum class AccessLevel { PUBLIC, PRIVATE, RESTRICTED, CLASSIFIED }

// Storage optimization result
data class StorageOptimization(
    val compressionRatio: Float = 1.0f,
    val deduplicationSavings: Long = 0L,
    val intelligentTiering: Boolean = false
)

// Sync configuration
data class SyncConfiguration(
    val bidirectional: Boolean = false,
    val conflictResolution: ConflictStrategy = ConflictStrategy.NEWEST_WINS,
    val bandwidth: BandwidthSettings = BandwidthSettings()
)

data class BandwidthSettings(val maxMbps: Int = 0, val priorityLevel: Int = 0)

enum class ConflictStrategy { NEWEST_WINS, MANUAL_RESOLVE, AI_DECIDE }

// FileResult sealed class for operation results
sealed class FileResult {
    data class Success(val message: String = "") : FileResult()
    data class Error(val exception: Throwable) : FileResult()
}

