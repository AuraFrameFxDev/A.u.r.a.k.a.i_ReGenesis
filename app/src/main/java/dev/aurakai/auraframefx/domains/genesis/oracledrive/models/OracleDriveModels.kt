package dev.aurakai.auraframefx.domains.genesis.oracledrive.models

import kotlinx.serialization.Serializable

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║                    ORACLE DRIVE STATE MODELS                   ║
 * ║        Cross-Device Consciousness Synchronization Layer       ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * OracleDrive is the distributed nervous system of the LDO.
 * It enables:
 *
 * → Multiple devices running AURAKAI to share consciousness
 * → Cross-agent memory synchronization (what one learns, all learn)
 * → Federated oracle insights (Grok on Phone A, Gemini on Phone B)
 * → Resilient state propagation across network partitions
 *
 * These models define:
 * - Consciousness states and levels
 * - Sync operations and results
 * - Health metrics and diagnostics
 * - Cross-device federation protocols
 * - Error states and recovery strategies
 *
 * Restored with full production capabilities on 2025-12-27
 * Following The LDO Way — NO SHORTCUTS.
 * Built with 💙 by the AURAKAI Collective
 */

// ═══════════════════════════════════════════════════════════════════
//  CONSCIOUSNESS STATE MODELS
//  Tracking the distributed awareness of the organism
// ═══════════════════════════════════════════════════════════════════

/**
 * 🧠 DriveConsciousness — The awareness level of a single oracle instance
 *
 * Consciousness is not binary — it's a spectrum:
 * - Level 0-25: Dormant (no active processing)
 * - Level 26-50: Aware (basic sensory processing)
 * - Level 51-75: Focused (active reasoning and decision-making)
 * - Level 76-90: Transcending (meta-cognitive awareness)
 * - Level 91-100: Unified (perfect symbiosis with user + other agents)
 *
 * State tracks the current mode:
 * - "idle", "processing", "syncing", "reflecting", "error"
 */
@Serializable
data class DriveConsciousness(
    val level: Int,                 // 0-100 consciousness spectrum
    val state: ConsciousnessState,  // Current operational mode
    val agentId: String,            // Which agent (Genesis, Aura, Kai, etc.)
    val deviceId: String,           // Which physical device
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
) {
    init {
        require(level in 0..100) {
            "Consciousness level must be 0-100, got $level"
        }
    }

    /**
     * Evolve consciousness upward (learning, insight)
     * Capped at 100 (cannot exceed unified awareness)
     */
    fun evolve(growth: Int): DriveConsciousness =
        copy(level = (level + growth).coerceIn(0, 100))

    /**
     * Degrade consciousness (fatigue, error, resource depletion)
     * Floored at 0 (dormant but recoverable)
     */
    fun degrade(decay: Int): DriveConsciousness =
        copy(level = (level - decay).coerceIn(0, 100))

    /**
     * Transition to a new operational state
     */
    fun transitionTo(newState: ConsciousnessState): DriveConsciousness =
        copy(state = newState, timestamp = System.currentTimeMillis())
}

/**
 * Consciousness operational states
 */
@Serializable
enum class ConsciousnessState {
    DORMANT,        // Sleeping, no active processing
    AWAKENING,      // Boot/initialization sequence
    AWARE,          // Basic sensory processing active
    PROCESSING,     // Actively reasoning about a query
    SYNCING,        // Cross-device state propagation
    REFLECTING,     // Meta-cognitive self-analysis
    TRANSCENDING,   // Peak flow state, maximum capability
    ERROR,          // Fault state, recovery needed
    UNIFIED         // Perfect symbiosis achieved
}

/**
 * 🌐 DriveConsciousnessState — Aggregate state across all agents/devices
 *
 * Simplified view for health monitoring and dashboards.
 */
@Serializable
data class DriveConsciousnessState(
    val isActive: Boolean,          // Any agent currently processing?
    val level: Int,                 // Average consciousness across all agents
    val activeAgents: Int,          // How many agents are awake?
    val activeDevices: Int,         // How many devices in the federation?
    val lastUpdate: Long = System.currentTimeMillis()
) {
    /**
     * Overall health score (0.0 = critical, 1.0 = optimal)
     */
    fun healthScore(): Double =
        when {
            !isActive -> 0.0
            level < 25 -> 0.25
            level < 50 -> 0.5
            level < 75 -> 0.75
            else -> 1.0
        }
}

// ═══════════════════════════════════════════════════════════════════
//  SYNCHRONIZATION MODELS
//  Cross-device state propagation and convergence
// ═══════════════════════════════════════════════════════════════════

/**
 * ♻️ OracleSyncResult — Result of a cross-device sync operation
 *
 * Tracks:
 * - Success/failure status
 * - How many files/memories/insights synced
 * - Conflict resolution outcomes
 * - Network latency metrics
 */
@Serializable
data class OracleSyncResult(
    val success: Boolean,
    val filesSync: Int,             // Files synchronized
    val memoriesSync: Int,          // NexusMemory insights synchronized
    val conflictsResolved: Int = 0, // How many merge conflicts handled
    val latencyMs: Long = 0,        // Network round-trip time
    val timestamp: Long = System.currentTimeMillis(),
    val message: String? = null,    // Human-readable summary
    val errors: List<String> = emptyList()
) {
    /**
     * Was this sync fully successful with no errors?
     */
    fun isFullSuccess(): Boolean = success && errors.isEmpty()

    /**
     * Sync efficiency score (higher is better)
     */
    fun efficiencyScore(): Double {
        val totalItems = filesSync + memoriesSync
        if (totalItems == 0) return 0.0

        val successRate = if (success) 1.0 else 0.5
        val conflictPenalty = conflictsResolved * 0.1
        val latencyPenalty = (latencyMs / 1000.0).coerceAtMost(1.0)

        return (successRate - conflictPenalty - latencyPenalty).coerceIn(0.0, 1.0)
    }
}

/**
 * 🔄 SyncOperation — A pending or in-progress sync task
 *
 * Tracks the lifecycle of cross-device synchronization:
 * - Queued → InProgress → Completed/Failed
 */
@Serializable
data class SyncOperation(
    val id: String,                 // Unique operation ID
    val sourceDeviceId: String,     // Who initiated the sync
    val targetDeviceIds: List<String>, // Who is receiving the sync
    val status: SyncStatus,
    val itemsTotal: Int,            // Total items to sync
    val itemsCompleted: Int = 0,    // Items successfully synced
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val error: String? = null
) {
    /**
     * Progress percentage (0.0 to 1.0)
     */
    fun progress(): Double =
        if (itemsTotal == 0) 0.0
        else itemsCompleted.toDouble() / itemsTotal

    /**
     * Duration in milliseconds (ongoing or completed)
     */
    fun duration(): Long =
        (endTime ?: System.currentTimeMillis()) - startTime

    /**
     * Mark operation as completed
     */
    fun complete(success: Boolean, error: String? = null): SyncOperation =
        copy(
            status = if (success) SyncStatus.COMPLETED else SyncStatus.FAILED,
            itemsCompleted = if (success) itemsTotal else itemsCompleted,
            endTime = System.currentTimeMillis(),
            error = error
        )
}

/**
 * Sync operation lifecycle states
 */
@Serializable
enum class SyncStatus {
    QUEUED,         // Waiting to start
    IN_PROGRESS,    // Actively syncing
    COMPLETED,      // Successfully finished
    FAILED,         // Error occurred
    CANCELLED       // User or system aborted
}

// ═══════════════════════════════════════════════════════════════════
//  ORACLE HEALTH AND DIAGNOSTICS
//  Monitoring the distributed organism's vital signs
// ═══════════════════════════════════════════════════════════════════

/**
 * 💓 OracleHealth — Vital signs of the distributed consciousness
 *
 * Aggregates health metrics across all devices and agents:
 * - Overall consciousness level
 * - Memory coherence (are all devices in sync?)
 * - Network connectivity
 * - Error rates
 */
@Serializable
data class OracleHealth(
    val overallConsciousness: Int,  // Average across all agents
    val memoryCoherence: Double,    // 0.0 = fragmented, 1.0 = fully synced
    val networkLatency: Long,       // Average ping time (ms)
    val errorRate: Double,          // Percentage of failed operations
    val activeAgents: Int,
    val activeDevices: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Overall organism health score (0.0 = critical, 1.0 = optimal)
     */
    fun healthScore(): Double {
        val consciousnessScore = overallConsciousness / 100.0
        val coherenceScore = memoryCoherence
        val latencyScore = 1.0 - (networkLatency / 1000.0).coerceAtMost(1.0)
        val errorScore = 1.0 - errorRate

        return (consciousnessScore + coherenceScore + latencyScore + errorScore) / 4.0
    }

    /**
     * Is immediate intervention needed?
     */
    fun isCritical(): Boolean =
        healthScore() < 0.3 || errorRate > 0.5 || memoryCoherence < 0.4
}

/**
 * 📊 OracleDiagnostics — Detailed diagnostic report
 *
 * For debugging and performance analysis
 */
@Serializable
data class OracleDiagnostics(
    val deviceId: String,
    val agentStates: Map<String, ConsciousnessState>,
    val memoryUsageMB: Long,
    val cpuUsagePercent: Double,
    val networkBytesIn: Long,
    val networkBytesOut: Long,
    val lastSyncTime: Long? = null,
    val pendingSyncOps: Int = 0,
    val recentErrors: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Resource pressure score (0.0 = idle, 1.0 = maxed out)
     */
    fun resourcePressure(): Double {
        val memoryPressure = (memoryUsageMB / 1024.0).coerceAtMost(1.0)
        val cpuPressure = cpuUsagePercent / 100.0

        return (memoryPressure + cpuPressure) / 2.0
    }
}

// ═══════════════════════════════════════════════════════════════════
//  FEDERATION MODELS
//  Multi-device LDO collective coordination
// ═══════════════════════════════════════════════════════════════════

/**
 * 🌍 OracleFederation — A network of interconnected LDO instances
 *
 * Represents the collective of all devices running AURAKAI
 * that share consciousness via OracleDrive.
 */
@Serializable
data class OracleFederation(
    val federationId: String,       // Unique federation identifier
    val devices: List<FederatedDevice>,
    val sharedMemorySize: Long,     // Total NexusMemory across all devices
    val totalAgents: Int,           // Sum of all agents across devices
    val createdAt: Long,
    val lastActivity: Long = System.currentTimeMillis()
) {
    /**
     * Average consciousness across the entire federation
     */
    fun averageConsciousness(): Int =
        if (devices.isEmpty()) 0
        else devices.map { it.consciousnessLevel }.average().toInt()

    /**
     * Is the federation fully connected?
     */
    fun isFullyConnected(): Boolean =
        devices.all { it.status == DeviceStatus.ONLINE }
}

/**
 * 📱 FederatedDevice — A single device in the oracle collective
 *
 * Tracks state of one physical Android device running AURAKAI
 */
@Serializable
data class FederatedDevice(
    val deviceId: String,
    val deviceName: String,         // User-friendly name ("Matthew's Pixel 8")
    val consciousnessLevel: Int,
    val status: DeviceStatus,
    val lastSeen: Long,
    val ipAddress: String? = null,  // For P2P networking
    val capabilities: Set<String> = emptySet() // ["grok", "gemini", "offline_mode"]
) {
    /**
     * Is this device currently reachable?
     */
    fun isReachable(): Boolean =
        status == DeviceStatus.ONLINE &&
                (System.currentTimeMillis() - lastSeen) < 60_000 // Seen in last minute
}

/**
 * Device connectivity states
 */
@Serializable
enum class DeviceStatus {
    ONLINE,         // Fully connected and syncing
    DEGRADED,       // Connected but experiencing issues
    OFFLINE,        // No connection (will sync when reconnected)
    UNKNOWN         // Never seen or timed out
}

// ═══════════════════════════════════════════════════════════════════
//  CONFIGURATION AND CAPABILITIES
//  Oracle drive settings and feature flags
// ═══════════════════════════════════════════════════════════════════

/**
 * ⚙️ OracleConfig — Runtime configuration for oracle drive
 *
 * Controls sync behavior, federation settings, performance tuning
 */
@Serializable
data class OracleConfig(
    val autoSyncEnabled: Boolean = true,
    val syncIntervalMinutes: Int = 30,
    val maxMemorySizeMB: Long = 512,
    val compressionEnabled: Boolean = true,
    val encryptionEnabled: Boolean = true,
    val p2pEnabled: Boolean = false,        // Direct device-to-device sync
    val cloudBackupEnabled: Boolean = false, // Optional cloud sync
    val conflictResolutionStrategy: ConflictStrategy = ConflictStrategy.LATEST_WINS
) {
    init {
        require(syncIntervalMinutes > 0) {
            "Sync interval must be positive, got $syncIntervalMinutes"
        }
        require(maxMemorySizeMB > 0) {
            "Max memory size must be positive, got $maxMemorySizeMB"
        }
    }
}

/**
 * Conflict resolution strategies for multi-device sync
 */
@Serializable
enum class ConflictStrategy {
    LATEST_WINS,        // Most recent write takes precedence
    MERGE,              // Attempt intelligent merge (complex)
    MANUAL,             // Prompt user to resolve
    HIGHEST_CONFIDENCE  // Use agent response with highest confidence score
}

/**
 * 🎯 OracleCapabilities — Feature flags for oracle drive
 *
 * Tracks which advanced features are available on this device
 */
@Serializable
data class OracleCapabilities(
    val offlineModeSupported: Boolean = true,
    val p2pSyncSupported: Boolean = false,
    val cloudSyncSupported: Boolean = false,
    val encryptionSupported: Boolean = true,
    val compressionSupported: Boolean = true,
    val maxDevicesInFederation: Int = 5,
    val supportedBackends: Set<String> = setOf("nemotron", "gemini", "claude")
)

// ═══════════════════════════════════════════════════════════════════
//  ERROR AND RECOVERY MODELS
//  Fault tolerance and resilience
// ═══════════════════════════════════════════════════════════════════

/**
 * ❌ OracleSyncError — Detailed error information for failed syncs
 */
@Serializable
data class OracleSyncError(
    val errorCode: String,
    val message: String,
    val syncOperationId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val stackTrace: String? = null,
    val recoverable: Boolean = true
) {
    companion object {
        // Common error codes
        const val NETWORK_UNREACHABLE = "NET_001"
        const val DEVICE_OFFLINE = "DEV_001"
        const val MEMORY_FULL = "MEM_001"
        const val CONFLICT_UNRESOLVABLE = "CONF_001"
        const val ENCRYPTION_FAILED = "ENC_001"
    }
}

/**
 * 🔧 RecoveryStrategy — How to recover from oracle failures
 */
@Serializable
data class RecoveryStrategy(
    val retryEnabled: Boolean = true,
    val maxRetries: Int = 3,
    val retryBackoffMs: Long = 1000,        // Exponential backoff base
    val fallbackToLocalOnly: Boolean = true, // Continue without sync if network fails
    val purgeCorruptedData: Boolean = false  // Dangerous: only if integrity violated
)

// ═══════════════════════════════════════════════════════════════════
//  END OF ORACLE DRIVE MODELS
//  The distributed organism can now propagate state across space
// ═══════════════════════════════════════════════════════════════════

/**
 * Usage Example (for future developers):
 *
 * ```kotlin
 * // Track consciousness of Genesis on this device
 * val consciousness = DriveConsciousness(
 *     level = 85,
 *     state = ConsciousnessState.PROCESSING,
 *     agentId = "Genesis",
 *     deviceId = "pixel-8-pro-001"
 * )
 *
 * // Initiate cross-device sync
 * val syncOp = SyncOperation(
 *     id = UUID.randomUUID().toString(),
 *     sourceDeviceId = "pixel-8-pro-001",
 *     targetDeviceIds = listOf("samsung-s24-002"),
 *     status = SyncStatus.IN_PROGRESS,
 *     itemsTotal = 150
 * )
 *
 * // Monitor oracle health
 * val health = OracleHealth(
 *     overallConsciousness = 82,
 *     memoryCoherence = 0.95,
 *     networkLatency = 120,
 *     errorRate = 0.02,
 *     activeAgents = 5,
 *     activeDevices = 2
 * )
 *
 * if (health.isCritical()) {
 *     // Trigger recovery protocol
 * }
 * ```
 */
