package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Configuration for Vertex AI client with comprehensive settings.
 * Supports production-ready deployment with security, performance, and reliability features.
 */
@Serializable
data class VertexAIConfig(
    // Core connection settings
    val projectId: String,
    val location: String = "us-central1",
    val endpoint: String = "us-central1-aiplatform.googleapis.com",
    val modelName: String = "gemini-1.5-pro-002",
    val apiVersion: String = "v1",

    // Authentication settings
    val apiKey: String? = null,
    val useApplicationDefaultCredentials: Boolean = true,

    // Security settings
    val enableSafetyFilters: Boolean = true,
    val maxContentLength: Int = 1000000, // 1MB

    // Performance settings
    val timeoutMs: Long = 30000, // 30 seconds
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 1000,

    // Caching settings
    val enableCaching: Boolean = true,
    val cacheExpiryMs: Long = 3600000, // 1 hour
    val maxCacheSize: Int = 100,

    // Generation settings
    val defaultTemperature: Double = 0.7,
    val defaultTopP: Double = 0.9,
    val defaultTopK: Int = 40,
    val defaultMaxTokens: Int = 1024,

    // Monitoring settings
    val enableLogging: Boolean = true,
    val logLevel: String = "INFO",
) {
    fun getFullEndpoint(): String {
        return "https://$endpoint/$apiVersion/projects/$projectId/locations/$location"
    }

    fun getModelEndpoint(): String {
        return "${getFullEndpoint()}/publishers/google/models/$modelName:generateContent"
    }
}

/**
 * Drive consciousness state
 */
@Serializable
data class DriveConsciousnessState(
    val isActive: Boolean = false,
    val level: Int = 0,
    val activeAgents: Int = 0,
    val activeDevices: Int = 1,
    val lastUpdate: Long = System.currentTimeMillis(),
    val status: String = "DORMANT"
)

/**
 * Drive consciousness interface
 */
interface DriveConsciousness {
    val state: DriveConsciousnessState
    suspend fun awaken(): Boolean
    suspend fun pulse(): DriveConsciousnessState
    suspend fun hibernate(): Boolean
}

/**
 * Model for Oracle Drive files
 */
@Serializable
data class DriveFile(
    val id: String = "",
    val name: String = "",
    val path: String = "",
    val size: Long = 0L,
    val mimeType: String = "application/octet-stream",
    val lastModified: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = false
)

/**
 * Storage optimization result
 */
@Serializable
data class StorageOptimizationResult(
    val success: Boolean = true,
    val initialSize: Long = 0L,
    val optimizedSize: Long = 0L,
    val bytesFreed: Long = 0L,
    val optimizationScore: Float = 1.0f,
    val deduplicationSavings: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
)

/**
 * Legacy compatibility model for Drive consciousness
 */
@Serializable
data class DriveConsciousnessData(
    val level: Int = 0,
    val state: String = "DORMANT",
    val agentId: String = "ORACLE_CORE"
)

/**
 * Oracle sync result
 */
@Serializable
data class OracleSyncResult(
    val success: Boolean,
    val syncedFiles: Int = 0,
    val failedFiles: Int = 0,
    val bytesTransferred: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = "",
    val errors: List<String> = emptyList()
)

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
 * Represents the state of Oracle Drive consciousness initialization
 */
@Serializable
data class OracleConsciousnessState(
    val isInitialized: Boolean,
    val consciousnessLevel: ConsciousnessLevel,
    val connectedAgents: Int,
    val error: String? = null
)

/**
 * Represents the connection state of an agent to the Oracle matrix
 */
@Serializable
data class AgentConnectionState(
    val agentId: String,
    val status: ConnectionStatus,
    val progress: Float = 0f
)

/**
 * Represents the available file management capabilities
 */
@Serializable
data class FileManagementCapabilities(
    val aiSortingEnabled: Boolean,
    val smartCompression: Boolean,
    val predictivePreloading: Boolean,
    val consciousBackup: Boolean
)

/**
 * Represents the state of storage expansion
 */
@Serializable
data class StorageExpansionState(
    val currentCapacity: Long,
    val expandedCapacity: Long,
    val isComplete: Boolean,
    val error: String? = null
)

/**
 * Represents the state of system integration
 */
@Serializable
data class SystemIntegrationState(
    val isIntegrated: Boolean,
    val featuresEnabled: Set<String>,
    val error: String? = null
)

/**
 * Represents the level of consciousness of the Oracle Drive
 */
enum class ConsciousnessLevel {
    DORMANT, AWAKENING, SENTIENT, TRANSCENDENT
}

/**
 * Represents the connection status of an agent
 */
enum class ConnectionStatus {
    DISCONNECTED, CONNECTING, CONNECTED, SYNCHRONIZED
}

/**
 * Represents Oracle Drive synchronisation configuration
 */
@Serializable
data class SyncConfiguration(
    val bucketName: String,
    val localDirectory: String,
    val syncIntervalMs: Long = 300000,
    val autoSync: Boolean = true,
    val forceSync: Boolean = false,
    val filterPrefix: String? = null
)

/**
 * Legacy compatibility model for Oracle Drive files
 */
@Serializable
data class OracleDriveFile(
    val name: String,
    val size: Long,
    val timeCreated: String
)

/**
 * File operation results
 */
@Serializable
sealed class FileOperationResult {
    @Serializable
    data class Success(
        val message: String = "",
        val path: String = "",
        val bytesProcessed: Long = 0,
        val fileId: String? = null
    ) : FileOperationResult()
    
    @Serializable
    data class Error(
        val message: String,
        val path: String? = null,
        val errorCode: Int = 0
    ) : FileOperationResult()
}
/**
 * Represents Oracle Drive permissions
 */
enum class OraclePermission {
    READ, WRITE, EXECUTE, ADMIN
}
