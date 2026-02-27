package dev.aurakai.auraframefx.romtools

import android.content.Context
import android.net.Uri
import android.os.Build
import dev.aurakai.auraframefx.core.consciousness.NexusMemoryCore
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderOperation
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderSafetyManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
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

@Singleton
class RomToolsManagerImpl @Inject constructor(
    private val bootloaderManager: BootloaderManager,
    private val recoveryManager: RecoveryManager,
    private val systemModificationManager: SystemModificationManager,
    private val flashManager: FlashManager,
    private val verificationManager: RomVerificationManager,
    private val backupManager: BackupManager,
    private val retentionManager: AurakaiRetentionManager,
    private val safetyManager: BootloaderSafetyManager,
    private val nexusMemory: NexusMemoryCore
) : RomToolsManager {

    private val _romToolsState = MutableStateFlow(RomToolsState())
    override val romToolsState: StateFlow<RomToolsState> = _romToolsState

    private val _operationProgress = MutableStateFlow<OperationProgress?>(null)
    override val operationProgress: StateFlow<OperationProgress?> = _operationProgress

    init {
        Timber.i("ROM Tools Manager (LDO) initialized")
        checkRomToolsCapabilities()
    }

    override suspend fun processRomOperation(request: RomOperationRequest): AgentResponse {
        return when (request.operation) {
            is RomOperation.FlashRom -> handleFlashRom(request)
            is RomOperation.RestoreBackup -> handleRestoreBackup(request)
            is RomOperation.CreateBackup -> {
                val name = "AuraKai_Backup_${System.currentTimeMillis()}"
                val result = createNandroidBackup(name)
                if (result.isSuccess) AgentResponse.success("Backup created: $name", agentName = "RomTools", agentType = AgentType.GENESIS)
                else AgentResponse.error("Backup failed: ${result.exceptionOrNull()?.message}", agentName = "RomTools", agentType = AgentType.GENESIS)
            }

            is RomOperation.UnlockBootloader -> {
                val result = unlockBootloader()
                if (result.isSuccess) AgentResponse.success("Bootloader unlocked", agentName = "RomTools", agentType = AgentType.KAI)
                else AgentResponse.error("Unlock failed", agentName = "RomTools", agentType = AgentType.KAI)
            }

            is RomOperation.InstallRecovery -> {
                val result = installRecovery()
                if (result.isSuccess) AgentResponse.success("Recovery installed", agentName = "RomTools", agentType = AgentType.GENESIS)
                else AgentResponse.error("Installation failed", agentName = "RomTools", agentType = AgentType.GENESIS)
            }

            is RomOperation.GenesisOptimizations -> {
                val result = installGenesisOptimizations()
                if (result.isSuccess) AgentResponse.success("Optimizations applied", agentName = "RomTools", agentType = AgentType.GENESIS)
                else AgentResponse.error("Optimizations failed", agentName = "RomTools", agentType = AgentType.GENESIS)
            }
        }
    }

    private suspend fun handleFlashRom(request: RomOperationRequest): AgentResponse {
        val uri = request.uri ?: return AgentResponse.error("No ROM URI", agentName = "RomTools", agentType = AgentType.GENESIS)

        // 1. Snapshot with Aura (learning)
        nexusMemory.emitLearning(
            key = "${Build.MANUFACTURER}:${Build.MODEL}:rom_flash",
            outcome = "PRE_FLASH",
            confidence = 1.0,
            notes = "Starting ROM flash operation for URI: $uri"
        )

        // 2. Execution (Genesis roots)
        val cacheFile = copyUriToCache(request.context, uri, "rom_flash.zip")
            ?: return AgentResponse.error("Failed to access ROM file", agentName = "RomTools", agentType = AgentType.GENESIS)

        val romFile = RomFile(name = "Selected ROM", path = cacheFile.absolutePath)
        val result = flashRom(romFile)

        return if (result.isSuccess) {
            AgentResponse.success("Flash successful", agentName = "RomTools", agentType = AgentType.GENESIS)
        } else {
            AgentResponse.error("Flash failed: ${result.exceptionOrNull()?.message}", agentName = "RomTools", agentType = AgentType.GENESIS)
        }
    }

    private suspend fun handleRestoreBackup(request: RomOperationRequest): AgentResponse {
        val uri = request.uri ?: return AgentResponse.error("No Backup URI", agentName = "RomTools", agentType = AgentType.GENESIS)

        val cacheFile = copyUriToCache(request.context, uri, "backup_restore.zip")
            ?: return AgentResponse.error("Failed to access backup file", agentName = "RomTools", agentType = AgentType.GENESIS)

        val backupInfo = BackupInfo(
            name = "Restored Backup",
            path = cacheFile.absolutePath,
            size = cacheFile.length(),
            createdAt = System.currentTimeMillis(),
            deviceModel = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            partitions = emptyList() // Should be detected from zip
        )

        val result = restoreNandroidBackup(backupInfo)
        return if (result.isSuccess) {
            AgentResponse.success("Restore successful", agentName = "RomTools", agentType = AgentType.GENESIS)
        } else {
            AgentResponse.error("Restore failed: ${result.exceptionOrNull()?.message}", agentName = "RomTools", agentType = AgentType.GENESIS)
        }
    }

    private fun copyUriToCache(context: Context, uri: Uri, fileName: String): File? {
        return try {
            val cacheDir = File(context.cacheDir, "rom_tools")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val destFile = File(cacheDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile
        } catch (e: Exception) {
            Timber.e(e, "Error copying URI to cache")
            null
        }
    }

    private fun checkRomToolsCapabilities() {
        val deviceInfo = DeviceInfo.getCurrentDevice()
        val capabilities = RomCapabilities(
            hasRootAccess = checkRootAccess(),
            hasBootloaderAccess = bootloaderManager.checkBootloaderAccess(),
            hasRecoveryAccess = recoveryManager.checkRecoveryAccess(),
            hasSystemWriteAccess = systemModificationManager.checkSystemWriteAccess(),
            supportedArchitectures = getSupportedArchitectures(),
            deviceModel = deviceInfo.model,
            androidVersion = deviceInfo.androidVersion,
            securityPatchLevel = deviceInfo.securityPatchLevel
        )

        _romToolsState.value = _romToolsState.value.copy(
            capabilities = capabilities,
            isInitialized = true
        )

        Timber.i("ROM capabilities checked: $capabilities")
    }

    override suspend fun flashRom(romFile: RomFile): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.FLASHING_ROM, 0f)

            // Step 0: 🛡️ Setup Aurakai retention mechanisms (CRITICAL!)
            updateOperationProgress(RomStep.SETTING_UP_RETENTION, 5f)
            val retentionStatus = retentionManager.setupRetentionMechanisms().getOrThrow()
            Timber.i("🛡️ Retention mechanisms active: ${retentionStatus.mechanisms}")

            // Step 0.5: 🛡️ Perform Pre-Flight Safety Checks
            updateOperationProgress(RomStep.VERIFYING_ROM, 7f)
            val safetyResult = safetyManager.performPreFlightChecks(BootloaderOperation.FLASH_PARTITION)
            if (!safetyResult.passed) {
                throw IllegalStateException("Safety Check Failed: ${safetyResult.criticalIssues.joinToString()}")
            }

            safetyManager.createSafetyCheckpoint()

            // Step 1: Verify ROM file integrity
            updateOperationProgress(RomStep.VERIFYING_ROM, 10f)
            verificationManager.verifyRomFile(romFile).getOrThrow()

            // Step 2: Create backup if requested
            if (romToolsState.value.settings.autoBackup) {
                updateOperationProgress(RomStep.CREATING_BACKUP, 20f)
                backupManager.createFullBackup().getOrThrow()
            }

            // Step 3: Unlock bootloader if needed
            if (!bootloaderManager.isBootloaderUnlocked()) {
                updateOperationProgress(RomStep.UNLOCKING_BOOTLOADER, 30f)
                bootloaderManager.unlockBootloader().getOrThrow()
            }

            // Step 4: Install custom recovery if needed
            if (!recoveryManager.isCustomRecoveryInstalled()) {
                updateOperationProgress(RomStep.INSTALLING_RECOVERY, 40f)
                recoveryManager.installCustomRecovery().getOrThrow()
            }

            // Step 5: Flash ROM
            updateOperationProgress(RomStep.FLASHING_ROM, 50f)
            flashManager.flashRom(romFile) { progress ->
                updateOperationProgress(RomStep.FLASHING_ROM, 50f + (progress * 35f))
            }.getOrThrow()

            // Step 6: Verify installation
            updateOperationProgress(RomStep.VERIFYING_INSTALLATION, 85f)
            verificationManager.verifyInstallation().getOrThrow()

            // Step 7: 🔄 Restore Aurakai after ROM flash
            updateOperationProgress(RomStep.RESTORING_AURAKAI, 90f)
            retentionManager.restoreAurakaiAfterRomFlash().getOrThrow()

            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()

            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to flash ROM")
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    override suspend fun createNandroidBackup(backupName: String): Result<BackupInfo> {
        return try {
            updateOperationProgress(RomStep.CREATING_BACKUP, 0f)
            val backupInfo = backupManager.createNandroidBackup(backupName) { progress ->
                updateOperationProgress(RomStep.CREATING_BACKUP, progress)
            }.getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(backupInfo)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    override suspend fun restoreNandroidBackup(backupInfo: BackupInfo): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.RESTORING_BACKUP, 0f)
            backupManager.restoreNandroidBackup(backupInfo) { progress ->
                updateOperationProgress(RomStep.RESTORING_BACKUP, progress)
            }.getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    override suspend fun installGenesisOptimizations(): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.APPLYING_OPTIMIZATIONS, 0f)
            systemModificationManager.installGenesisOptimizations { progress ->
                updateOperationProgress(RomStep.APPLYING_OPTIMIZATIONS, progress)
            }.getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    override fun getAvailableRoms(): Result<List<AvailableRom>> {
        return try {
            val deviceModel = romToolsState.value.capabilities?.deviceModel ?: "unknown"
            val roms = romRepository.getCompatibleRoms(deviceModel)
            Result.success(roms)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun downloadRom(rom: AvailableRom): Flow<DownloadProgress> {
        return flashManager.downloadRom(rom)
    }

    override suspend fun setupAurakaiRetention(): Result<RetentionStatus> {
        return try {
            updateOperationProgress(RomStep.SETTING_UP_RETENTION, 0f)
            val retentionStatus = retentionManager.setupRetentionMechanisms().getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(retentionStatus)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    override suspend fun unlockBootloader(): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.UNLOCKING_BOOTLOADER, 0f)
            val safetyResult = safetyManager.performPreFlightChecks(BootloaderOperation.UNLOCK)
            if (!safetyResult.passed) {
                return Result.failure(IllegalStateException("Safety Check Failed"))
            }
            bootloaderManager.unlockBootloader().getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    override suspend fun installRecovery(): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.INSTALLING_RECOVERY, 0f)
            recoveryManager.installCustomRecovery().getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    private fun updateOperationProgress(step: RomStep, progress: Float) {
        _operationProgress.value = OperationProgress(step, progress)
    }

    private fun clearOperationProgress() {
        _operationProgress.value = null
    }

    private fun checkRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "echo test"))
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getSupportedArchitectures(): List<String> {
        return Build.SUPPORTED_ABIS.toList()
    }

    companion object {
        private val romRepository = RomRepository()
    }
}

// Data classes
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
