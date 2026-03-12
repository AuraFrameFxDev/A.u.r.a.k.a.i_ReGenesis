package dev.aurakai.auraframefx.romtools

import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.romtools.retention.RetentionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Main interface for ROM Tools management operations.
 * Coordinates all ROM-related functionality including flashing, backup, recovery, and system modifications.
 */
interface RomToolsManager {

    /**
     * Current state of the ROM tools system including capabilities and initialization status.
     */
    val romToolsState: StateFlow<RomToolsState>

    /**
     * Progress of current operation if any is running.
     */
    val operationProgress: StateFlow<OperationProgress?>

    /**
     * Process a ROM operation request through the Genesis AI agent system.
     */
    suspend fun processRomOperation(request: RomOperationRequest): AgentResponse

    /**
     * Flash a ROM file to the device.
     */
    suspend fun flashRom(romFile: RomFile): Result<Unit>

    /**
     * Create a Nandroid backup (full system backup).
     */
    suspend fun createNandroidBackup(backupName: String): Result<BackupInfo>

    /**
     * Restore from a Nandroid backup.
     */
    suspend fun restoreNandroidBackup(backupInfo: BackupInfo): Result<Unit>

    /**
     * Install Genesis-specific system optimizations.
     */
    suspend fun installGenesisOptimizations(): Result<Unit>

    /**
     * Get list of available ROMs for download.
     */
    fun getAvailableRoms(): Result<List<AvailableRom>>

    /**
     * Download a ROM with progress tracking.
     */
    fun downloadRom(rom: AvailableRom): Flow<DownloadProgress>

    /**
     * Setup AuraKai retention mechanisms to survive ROM flashing.
     */
    suspend fun setupAurakaiRetention(): Result<RetentionStatus>

    /**
     * Unlock the bootloader (if supported).
     */
    suspend fun unlockBootloader(): Result<Unit>

    /**
     * Install custom recovery (TWRP/etc).
     */
    suspend fun installRecovery(): Result<Unit>
}

/**
 * Current state of the ROM tools system.
 */
data class RomToolsState(
    val isInitialized: Boolean = false,
    val capabilities: RomCapabilities = RomCapabilities(),
    val lastError: String? = null,
    val availableRoms: List<AvailableRom> = emptyList(),
    val backups: List<BackupInfo> = emptyList(),
    val operationProgress: OperationProgress? = null
)

/**
 * Device ROM capabilities.
 */
data class RomCapabilities(
    val hasRootAccess: Boolean = false,
    val hasBootloaderAccess: Boolean = false,
    val hasRecoveryAccess: Boolean = false,
    val hasSystemWriteAccess: Boolean = false,
    val supportedArchitectures: List<String> = emptyList(),
    val deviceModel: String = "Unknown",
    val androidVersion: String = "Unknown",
    val securityPatchLevel: String = "Unknown"
)

/**
 * Progress tracking for long-running ROM operations.
 */
data class OperationProgress(
    val operation: String,
    val progress: Float, // 0.0 to 100.0
    val status: String,
    val isIndeterminate: Boolean = false
)

/**
 * Request for a ROM operation.
 */
data class RomOperationRequest(
    val operation: RomOperation,
    val uri: android.net.Uri? = null,
    val context: android.content.Context
)

/**
 * Sealed interface for ROM operations.
 */
sealed interface RomOperation {
    data object FlashRom : RomOperation
    data object CreateBackup : RomOperation
    data object RestoreBackup : RomOperation
    data object GenesisOptimizations : RomOperation
    data object InstallRecovery : RomOperation
    data object UnlockBootloader : RomOperation
}

/**
 * Information about a ROM file.
 */
data class RomFile(
    val file: File,
    val name: String,
    val path: String = file.absolutePath,
    val version: String? = null,
    val checksum: String = "",
    val size: Long = file.length()
)

/**
 * Information about a backup.
 */
data class BackupInfo(
    val name: String,
    val path: String,
    val size: Long,
    val createdAt: Long,
    val deviceModel: String,
    val androidVersion: String,
    val partitions: List<String>
)

/**
 * Information about an available ROM.
 */
data class AvailableRom(
    val name: String,
    val version: String,
    val downloadUrl: String,
    val size: Long,
    val checksum: String,
    val releaseNotes: String? = null,
    val description: String = "",
    val androidVersion: String = "",
    val maintainer: String = "",
    val releaseDate: Long = 0L
)

/**
 * Download progress tracking.
 */
data class DownloadProgress(
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val progress: Float,
    val speed: Long = 0L,
    val isCompleted: Boolean = false,
    val percentage: Float = progress
)
