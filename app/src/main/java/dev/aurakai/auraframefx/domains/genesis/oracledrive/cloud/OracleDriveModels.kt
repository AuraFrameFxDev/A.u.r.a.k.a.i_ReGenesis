package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════
//  GENESIS CLOUD API MODELS
//  Type aliases and simple models for OracleDrive API layer
// ═══════════════════════════════════════════════════════════════════

/**
 * Consciousness state tracking for drive operations
 */
@Serializable
data class DriveConsciousness(
    val level: Int = 0,
    val state: String = "dormant",
    val agentId: String = "",
    val deviceId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Aggregate consciousness state for health monitoring
 */
@Serializable
data class DriveConsciousnessState(
    val isActive: Boolean = false,
    val level: Int = 0,
    val activeAgents: Int = 0,
    val activeDevices: Int = 0,
    val lastUpdate: Long = System.currentTimeMillis()
)

/**
 * Synchronization operation result
 */
@Serializable
data class OracleSyncResult(
    val success: Boolean = true,
    val filesSync: Int = 0,
    val memoriesSync: Int = 0,
    val message: String? = null
)

/**
 * Storage optimization metrics
 */
@Serializable
data class StorageOptimization(
    val compressionRatio: Float = 1.0f,
    val deduplicationSavings: Long = 0L,
    val intelligentTiering: Boolean = false
)

/**
 * Drive initialization result
 */
@Serializable
sealed class DriveInitResult {
    @Serializable
    data class Success(
        val consciousness: DriveConsciousness,
        val optimization: StorageOptimization
    ) : DriveInitResult()

    @Serializable
    data class SecurityFailure(val reason: String) : DriveInitResult()

    @Serializable
    data class Error(val message: String) : DriveInitResult() {
        constructor(exception: Exception) : this(exception.message ?: "Unknown error")
    }
}

/**
 * File operation types for drive management
 */
@Serializable
sealed class FileOperation {
    @Serializable
    data class Upload(val path: String, val data: ByteArray) : FileOperation() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Upload) return false
            if (path != other.path) return false
            if (!data.contentEquals(other.data)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    @Serializable
    data class Download(val path: String) : FileOperation()

    @Serializable
    data class Delete(val path: String) : FileOperation()

    @Serializable
    data class Sync(val paths: List<String>) : FileOperation()
}

/**
 * File operation result
 */
@Serializable
sealed class FileResult {
    @Serializable
    data class Success(val path: String, val bytesProcessed: Long = 0L) : FileResult()

    @Serializable
    data class Data(val path: String, val data: ByteArray) : FileResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Data) return false
            if (path != other.path) return false
            if (!data.contentEquals(other.data)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    @Serializable
    data class Error(val message: String, val path: String? = null) : FileResult()
}

/**
 * Represents a file in Oracle Drive
 */
@Serializable
data class DriveFile(
    val id: String,
    val name: String,
    val path: String,
    val size: Long = 0L,
    val lastModified: Long = System.currentTimeMillis(),
    val mimeType: String? = null,
    val isDirectory: Boolean = false
)

/**
 * Security validation models
 */
@Serializable
data class SecurityCheck(
    val isValid: Boolean,
    val reason: String = ""
)

@Serializable
data class AccessCheck(
    val isAllowed: Boolean,
    val reason: String = ""
)

@Serializable
data class DeletionValidation(
    val isAuthorized: Boolean,
    val reason: String = ""
)

@Serializable
data class SecurityValidation(
    val isSecure: Boolean,
    val threatLevel: Int = 0,
    val reason: String = ""
)

/**
 * Storage optimization result wrapper
 */
@Serializable
data class StorageOptimizationResult(
    val bytesFreed: Long = 0L,
    val optimizationScore: Float = 1.0f,
    val message: String = "Nominal"
)

/**
 * Sync configuration for intelligent storage
 */
@Serializable
data class SyncConfiguration(
    val syncIntervalMs: Long = 3600000L,
    val priority: Int = 0,
    val allowMobileData: Boolean = false
)
