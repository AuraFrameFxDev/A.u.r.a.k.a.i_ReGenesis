package dev.aurakai.auraframefx.romtools

import android.content.Context
import android.net.Uri
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.memory.NexusMemoryCore
import dev.aurakai.auraframefx.domains.genesis.network.model.AgentResponse
import dev.aurakai.auraframefx.domains.cascade.network.infrastructure.success
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.UnlockTier
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderOperation
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderSafetyManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import dev.aurakai.auraframefx.romtools.retention.RetentionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RomToolsManager that coordinates all ROM-related operations.
 */
@Singleton
class RomToolsManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bootloaderManager: BootloaderManager,
    private val safetyManager: BootloaderSafetyManager,
    private val recoveryManager: RecoveryManager,
    private val flashManager: FlashManager,
    private val verificationManager: RomVerificationManager,
    private val backupManager: BackupManager,
    private val systemModificationManager: SystemModificationManager,
    private val retentionManager: AurakaiRetentionManager,
    private val nexusMemory: NexusMemoryCore,
    private val pandoraBoxService: PandoraBoxService
) : RomToolsManager {

    private val _romToolsState = MutableStateFlow(RomToolsState())
    override val romToolsState: StateFlow<RomToolsState> = _romToolsState.asStateFlow()

    private val _operationProgress = MutableStateFlow<OperationProgress?>(null)
    override val operationProgress: StateFlow<OperationProgress?> = _operationProgress.asStateFlow()

    init {
        Timber.i("ROM Tools Manager (LDO) initialized")
        checkRomToolsCapabilities()
    }

    private fun checkRomToolsCapabilities() {
        try {
            val capabilities = RomCapabilities(
                hasRootAccess = checkRootAccess(),
                hasBootloaderAccess = bootloaderManager.checkBootloaderAccess(),
                hasRecoveryAccess = recoveryManager.checkRecoveryAccess(),
                hasSystemWriteAccess = systemModificationManager.checkSystemWriteAccess(),
                supportedArchitectures = getSupportedArchitectures(),
                deviceModel = Build.MODEL,
                androidVersion = Build.VERSION.RELEASE,
                securityPatchLevel = Build.VERSION.SECURITY_PATCH
            )

            _romToolsState.value = _romToolsState.value.copy(
                capabilities = capabilities,
                isInitialized = true
            )

            Timber.i("ROM capabilities checked: $capabilities")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize ROM Tools")
            _romToolsState.value = _romToolsState.value.copy(
                isInitialized = false,
                lastError = e.message
            )
        }
    }

    override suspend fun processRomOperation(request: RomOperationRequest): AgentResponse {
        val currentTier = pandoraBoxService.getCurrentState().value.currentTier

        // Tier enforcement: System tier required for these
        if (request.operation is RomOperation.FlashRom ||
            request.operation is RomOperation.RestoreBackup ||
            request.operation is RomOperation.CreateBackup) {
            if (currentTier.level < UnlockTier.System.level) {
                return AgentResponse.error(
                    "Operation rejected: Pandora's Box 'System' tier required.",
                    agentName = "RomTools"
                )
            }
        }

        // Tier enforcement: Sovereign tier required for these
        if (request.operation is RomOperation.UnlockBootloader ||
            request.operation is RomOperation.GenesisOptimizations) {
            if (currentTier.level < UnlockTier.Sovereign.level) {
                return AgentResponse.error(
                    "Operation rejected: Pandora's Box 'Sovereign' tier required.",
                    agentName = "RomTools"
                )
            }
        }

        return when (request.operation) {
            is RomOperation.FlashRom -> handleFlashRom(request)
            is RomOperation.RestoreBackup -> handleRestoreBackup(request)
            is RomOperation.CreateBackup -> {
                val name = "AuraKai_Backup_${System.currentTimeMillis()}"
                val result = createNandroidBackup(name)
                if (result.isSuccess) {
                    AgentResponse.success("Backup created: $name", agentName = "RomTools")
                } else AgentResponse.error(
                    "Backup failed: ${result.exceptionOrNull()?.message}",
                    agentName = "RomTools"
                )
            }

            is RomOperation.UnlockBootloader -> {
                val result = unlockBootloader()
                if (result.isSuccess) AgentResponse.success("Bootloader unlocked", agentName = "RomTools")
                else AgentResponse.error("Unlock failed", agentName = "RomTools")
            }

            is RomOperation.InstallRecovery -> {
                val result = installRecovery()
                if (result.isSuccess) AgentResponse.success("Recovery installed", agentName = "RomTools")
                else AgentResponse.error("Installation failed", agentName = "RomTools")
            }

            is RomOperation.GenesisOptimizations -> {
                val result = installGenesisOptimizations()
                if (result.isSuccess) AgentResponse.success("Optimizations applied", agentName = "RomTools")
                else AgentResponse.error("Optimizations failed", agentName = "RomTools")
            }
        }
    }

    private suspend fun handleFlashRom(request: RomOperationRequest): AgentResponse {
        val uri = request.uri ?: return AgentResponse.error("No ROM URI", agentName = "RomTools")

        // 1. Snapshot with Aura (learning)
        nexusMemory.emitLearning(
            key = "${Build.MANUFACTURER}:${Build.MODEL}:rom_flash",
            outcome = "PRE_FLASH",
            confidence = 1.0,
            notes = "Starting ROM flash operation for URI: $uri"
        )

        // 2. Execution (Genesis roots)
        val cacheFile = copyUriToCache(request.context, uri, "rom_flash.zip")
            ?: return AgentResponse.error("Failed to access ROM file", agentName = "RomTools")

        val romFile = RomFile(
            name = "Selected ROM",
            path = cacheFile.absolutePath,
            size = cacheFile.length()
        )
        val result = flashRom(romFile)

        return if (result.isSuccess) {
            AgentResponse.success("Flash successful", agentName = "RomTools")
        } else {
            AgentResponse.error(
                "Flash failed: ${result.exceptionOrNull()?.message}",
                agentName = "RomTools"
            )
        }
    }

    private suspend fun handleRestoreBackup(request: RomOperationRequest): AgentResponse {
        val uri = request.uri ?: return AgentResponse.error("No Backup URI", agentName = "RomTools")

        val cacheFile = copyUriToCache(request.context, uri, "backup_restore.zip")
            ?: return AgentResponse.error("Failed to access backup file", agentName = "RomTools")

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
            AgentResponse.success("Restore successful", agentName = "RomTools")
        } else {
            AgentResponse.error(
                "Restore failed: ${result.exceptionOrNull()?.message}",
                agentName = "RomTools"
            )
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

    override suspend fun flashRom(romFile: RomFile): Result<Unit> {
        return try {
            updateProgress(RomStep.FLASHING_ROM, 0f, "Preparing...")

            // Step 0: 🛡️ Setup Aurakai retention mechanisms (CRITICAL!)
            updateProgress(RomStep.SETTING_UP_RETENTION, 5f, "Setting up retention...")
            val retentionStatus = retentionManager.setupRetentionMechanisms().getOrThrow()
            Timber.i("🛡️ Retention mechanisms active: ${retentionStatus.mechanisms}")

            // Step 0.5: 🛡️ Perform Pre-Flight Safety Checks
            updateProgress(RomStep.VERIFYING_ROM, 7f, "Performing safety checks...")
            val safetyResult =
                safetyManager.performPreFlightChecks(BootloaderOperation.FLASH_PARTITION)
            if (!safetyResult.passed) {
                throw IllegalStateException("Safety Check Failed: ${safetyResult.criticalIssues.joinToString()}")
            }

            safetyManager.createSafetyCheckpoint()

            // Step 1: Verify ROM file integrity
            updateProgress(RomStep.VERIFYING_ROM, 10f, "Verifying ROM integrity...")
            verificationManager.verifyRomFile(romFile).getOrThrow()

            // Step 2: Create backup if requested
            if (romToolsState.value.settings.autoBackup) {
                updateProgress(RomStep.CREATING_BACKUP, 20f, "Creating auto-backup...")
                createNandroidBackup("AutoBackup_${System.currentTimeMillis()}").getOrThrow()
            }

            // Step 3: Unlock bootloader if needed
            if (!bootloaderManager.isBootloaderUnlocked()) {
                updateProgress(RomStep.UNLOCKING_BOOTLOADER, 30f, "Unlocking bootloader...")
                unlockBootloader().getOrThrow()
            }

            // Step 4: Install custom recovery if needed
            if (!recoveryManager.isCustomRecoveryInstalled()) {
                updateProgress(RomStep.INSTALLING_RECOVERY, 40f, "Installing recovery...")
                installRecovery().getOrThrow()
            }

            // Step 5: Flash ROM
            updateProgress(RomStep.FLASHING_ROM, 50f, "Flashing ROM...")
            flashManager.flashRom(romFile) { progress ->
                updateProgress(RomStep.FLASHING_ROM, 50f + (progress * 35f), "Flashing...")
            }.getOrThrow()

            // Step 6: Verify installation
            updateProgress(RomStep.VERIFYING_INSTALLATION, 85f, "Verifying installation...")
            verificationManager.verifyInstallation().getOrThrow()

            // Step 7: 🔄 Restore Aurakai after ROM flash
            updateProgress(RomStep.RESTORING_AURAKAI, 90f, "Restoring AuraKai...")
            retentionManager.restoreAurakaiAfterRomFlash().getOrThrow()

            updateProgress(RomStep.COMPLETED, 100f, "Flash successful")
            clearProgress()

            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to flash ROM")
            updateProgress(RomStep.FAILED, 0f, "Flash failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    override suspend fun createNandroidBackup(backupName: String): Result<BackupInfo> {
        return try {
            updateProgress(RomStep.CREATING_BACKUP, 0f, "Initializing backup...")
            val backupInfo = backupManager.createNandroidBackup(backupName) { progress ->
                updateProgress(RomStep.CREATING_BACKUP, progress, "Backing up...")
            }.getOrThrow()
            updateProgress(RomStep.COMPLETED, 100f, "Backup completed")
            clearProgress()
            Result.success(backupInfo)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create backup")
            updateProgress(RomStep.FAILED, 0f, "Backup failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    override suspend fun restoreNandroidBackup(backupInfo: BackupInfo): Result<Unit> {
        return try {
            updateProgress(RomStep.RESTORING_BACKUP, 0f, "Preparing restore...")
            backupManager.restoreNandroidBackup(backupInfo) { progress ->
                updateProgress(RomStep.RESTORING_BACKUP, progress, "Restoring...")
            }.getOrThrow()
            updateProgress(RomStep.COMPLETED, 100f, "Restore completed")
            clearProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to restore backup")
            updateProgress(RomStep.FAILED, 0f, "Restore failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    override suspend fun installGenesisOptimizations(): Result<Unit> {
        return try {
            updateProgress(RomStep.APPLYING_OPTIMIZATIONS, 0f, "Installing optimizations...")
            systemModificationManager.installGenesisOptimizations { progress ->
                updateProgress(RomStep.APPLYING_OPTIMIZATIONS, progress, "Applying...")
            }.getOrThrow()
            updateProgress(RomStep.COMPLETED, 100f, "Optimizations applied")
            clearProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to install optimizations")
            updateProgress(RomStep.FAILED, 0f, "Optimization failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    override fun getAvailableRoms(): Result<List<AvailableRom>> {
        return try {
            // TODO: Implement ROM repository/API
            Result.success(emptyList())
        } catch (e: Exception) {
            Timber.e(e, "Failed to get available ROMs")
            Result.failure(e)
        }
    }

    override fun downloadRom(rom: AvailableRom): Flow<DownloadProgress> {
        return flashManager.downloadRom(rom)
    }

    override suspend fun setupAurakaiRetention(): Result<RetentionStatus> {
        return try {
            updateProgress(RomStep.SETTING_UP_RETENTION, 0f, "Configuring retention...")
            val result = retentionManager.setupRetentionMechanisms()
            updateProgress(RomStep.COMPLETED, 100f, "Retention ready")
            clearProgress()
            result
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup retention")
            updateProgress(RomStep.FAILED, 0f, "Retention failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    override suspend fun unlockBootloader(): Result<Unit> {
        return try {
            updateProgress(RomStep.UNLOCKING_BOOTLOADER, 0f, "Unlocking bootloader...")
            val safetyResult = safetyManager.performPreFlightChecks(BootloaderOperation.UNLOCK)
            if (!safetyResult.passed) {
                return Result.failure(IllegalStateException("Safety Check Failed"))
            }
            bootloaderManager.unlockBootloader().getOrThrow()
            updateProgress(RomStep.COMPLETED, 100f, "Bootloader unlocked")
            clearProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to unlock bootloader")
            updateProgress(RomStep.FAILED, 0f, "Unlock failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    override suspend fun installRecovery(): Result<Unit> {
        return try {
            updateProgress(RomStep.INSTALLING_RECOVERY, 0f, "Installing recovery...")
            recoveryManager.installCustomRecovery().getOrThrow()
            updateProgress(RomStep.COMPLETED, 100f, "Recovery installed")
            clearProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to install recovery")
            updateProgress(RomStep.FAILED, 0f, "Installation failed: ${e.message}")
            clearProgress()
            Result.failure(e)
        }
    }

    // Helper methods

    private fun checkRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getSupportedArchitectures(): List<String> {
        return Build.SUPPORTED_ABIS.toList()
    }

    private fun updateProgress(step: RomStep, progress: Float, status: String) {
        _operationProgress.value = OperationProgress(
            step = step,
            progress = progress,
            status = status,
            isIndeterminate = false
        )
    }

    private fun clearProgress() {
        _operationProgress.value = null
    }
}
