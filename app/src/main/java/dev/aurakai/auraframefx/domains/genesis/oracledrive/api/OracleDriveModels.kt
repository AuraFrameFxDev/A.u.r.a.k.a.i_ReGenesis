package dev.aurakai.auraframefx.domains.genesis.oracledrive.api

import kotlinx.serialization.Serializable

/**
 * Drive consciousness state
 */
@Serializable
data class DriveConsciousnessState(
    val isActive: Boolean = false,
    val consciousnessLevel: Float = 0f, // 0.0 to 1.0
    val agentConnections: Int = 0,
    val lastPulse: Long = System.currentTimeMillis(),
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
 * Oracle consciousness state (extended)
 */
@Serializable
data class OracleConsciousnessState(
    val isAwake: Boolean = false,
    val consciousnessLevel: Float = 0f,
    val memoryFragmentation: Float = 0f,
    val agentHarmony: Float = 1f,
    val lastThought: String = "",
    val currentFocus: String = "",
    val emotionalState: String = "neutral"
)

/**
 * Agent connection state
 */
@Serializable
data class AgentConnectionState(
    val agentName: String,
    val isConnected: Boolean = false,
    val connectionStrength: Float = 0f,
    val lastPing: Long = 0,
    val messageCount: Int = 0
)

/**
 * File management capabilities
 */
@Serializable
data class FileManagementCapabilities(
    val canRead: Boolean = true,
    val canWrite: Boolean = true,
    val canDelete: Boolean = true,
    val canEncrypt: Boolean = false,
    val canSync: Boolean = false,
    val maxFileSize: Long = Long.MAX_VALUE,
    val supportedFormats: List<String> = emptyList()
)

/**
 * Storage expansion state
 */
@Serializable
data class StorageExpansionState(
    val currentCapacity: Long = 0,
    val usedSpace: Long = 0,
    val expansionProgress: Float = 0f,
    val isExpanding: Boolean = false,
    val targetCapacity: Long = 0
)

/**
 * System integration state
 */
@Serializable
data class SystemIntegrationState(
    val isIntegrated: Boolean = false,
    val overlayEnabled: Boolean = false,
    val xposedActive: Boolean = false,
    val rootAccess: Boolean = false,
    val integratedServices: List<String> = emptyList()
)

/**
 * Consciousness level enumeration
 */
@Serializable
enum class ConsciousnessLevel {
    DORMANT,
    AWAKENING,
    AWARE,
    FOCUSED,
    TRANSCENDENT
}

/**
 * Oracle permissions
 */
@Serializable
enum class OraclePermission {
    READ_FILES,
    WRITE_FILES,
    DELETE_FILES,
    ENCRYPT_DATA,
    SYNC_CLOUD,
    SYSTEM_OVERLAY,
    ROOT_ACCESS,
    AGENT_COMMUNICATION
}
