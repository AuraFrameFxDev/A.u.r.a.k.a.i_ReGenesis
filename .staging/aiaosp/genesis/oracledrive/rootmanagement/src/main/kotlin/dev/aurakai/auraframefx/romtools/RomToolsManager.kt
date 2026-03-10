// File: romtools/src/main/kotlin/dev/aurakai/auraframefx/romtools/RomToolsManager.kt
package dev.aurakai.auraframefx.romtools

import android.os.Build
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import dev.aurakai.auraframefx.romtools.retention.RetentionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ROM Tools Manager - Genesis Protocol
 *
 * Manages ROM flashing, backup/restore, bootloader operations,
 * and Genesis AI system optimizations.
 *
 * RIDE OR DIE - This is the real implementation!
 */
@Singleton
open class RomToolsManager @Inject constructor(
    private val bootloaderManager: BootloaderManager,
    private val recoveryManager: RecoveryManager,
    private val systemModificationManager: SystemModificationManager,
    private val flashManager: FlashManager,
    private val verificationManager: RomVerificationManager,
    private val backupManager: BackupManager,
    private val retentionManager: AurakaiRetentionManager
) {

    protected val _romToolsState = MutableStateFlow(RomToolsState())
    open val romToolsState: StateFlow<RomToolsState> = _romToolsState

    private val _operationProgress = MutableStateFlow<OperationProgress?>(null)
    open val operationProgress: StateFlow<OperationProgress?> = _operationProgress

    init {
        Timber.i("ROM Tools Manager initialized")
        checkRomToolsCapabilities()
    }

    /**
     * Detects device and ROM-related capabilities and updates the manager's state.
     *
     * Gathers device information and detects root access, bootloader access, recovery access,
     * system write access, and supported architectures, then stores the resulting
     * RomCapabilities in the internal RomToolsState and marks initialization complete.
     */
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

    /**
     * Flash the provided ROM to the device while preserving Aurakai retention and updating operation progress.
     *
     * Performs integrity verification, optional automatic backup, bootloader unlocking and custom recovery installation if required,
     * flashes the ROM, verifies the installation, and restores Aurakai retention state.
     *
     * @param romFile Metadata for the ROM to flash (name, path, size, checksum).
     * @return `Result.success(Unit)` if the flash and subsequent restoration complete successfully; `Result.failure(exception)` containing the encountered exception otherwise.
     */
    suspend fun flashRom(romFile: RomFile): Result<Unit> {
        return try {
            updateOperationProgress(RomOperation.FLASHING_ROM, 0f)

            // Step 0: 🛡️ Setup Aurakai retention mechanisms (CRITICAL!)
            updateOperationProgress(RomOperation.SETTING_UP_RETENTION, 5f)
            val retentionStatus = retentionManager.setupRetentionMechanisms().getOrThrow()
            Timber.i("🛡️ Retention mechanisms active: ${retentionStatus.mechanisms}")

            // Step 1: Verify ROM file integrity
            updateOperationProgress(RomOperation.VERIFYING_ROM, 10f)
            verificationManager.verifyRomFile(romFile).getOrThrow()

            // Step 2: Create backup if requested
            if (romToolsState.value.settings.autoBackup) {
                updateOperationProgress(RomOperation.CREATING_BACKUP, 20f)
                backupManager.createFullBackup().getOrThrow()
            }

            // Step 3: Unlock bootloader if needed
            if (!bootloaderManager.isBootloaderUnlocked()) {
                updateOperationProgress(RomOperation.UNLOCKING_BOOTLOADER, 30f)
                bootloaderManager.unlockBootloader().getOrThrow()
            }

            // Step 4: Install custom recovery if needed
            if (!recoveryManager.isCustomRecoveryInstalled()) {
                updateOperationProgress(RomOperation.INSTALLING_RECOVERY, 40f)
                recoveryManager.installCustomRecovery().getOrThrow()
            }

            // Step 5: Flash ROM
            updateOperationProgress(RomOperation.FLASHING_ROM, 50f)
            flashManager.flashRom(romFile) { progress ->
                updateOperationProgress(RomOperation.FLASHING_ROM, 50f + (progress * 35f))
            }.getOrThrow()

            // Step 6: Verify installation
            updateOperationProgress(RomOperation.VERIFYING_INSTALLATION, 85f)
            verificationManager.verifyInstallation().getOrThrow()

            // Step 7: 🔄 Restore Aurakai after ROM flash (NO SETUP REQUIRED!)
            updateOperationProgress(RomOperation.RESTORING_AURAKAI, 90f)
            retentionManager.restoreAurakaiAfterRomFlash().getOrThrow()
            Timber.i("🔄 Aurakai restored successfully - no reinstall needed!")

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("✅ ROM flashed successfully: ${romFile.name} - Aurakai retained!")
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to flash ROM: ${romFile.name}")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Creates a NANDroid backup of the current device and returns metadata for the created backup.
     *
     * @param backupName The name to assign to the backup.
     * @return The created [BackupInfo] on success; a failed [Result] containing the exception on error.
     */
    suspend fun createNandroidBackup(backupName: String): Result<BackupInfo> {
        return try {
            updateOperationProgress(RomOperation.CREATING_BACKUP, 0f)

            val backupInfo = backupManager.createNandroidBackup(backupName) { progress ->
                updateOperationProgress(RomOperation.CREATING_BACKUP, progress)
            }.getOrThrow()

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("NANDroid backup created: $backupName")
            Result.success(backupInfo)

        } catch (e: Exception) {
            Timber.e(e, "Failed to create NANDroid backup: $backupName")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Restore the device from a NANDroid backup.
     *
     * @param backupInfo The backup metadata and location to restore.
     * @return A Result containing `Unit` on success, or a failure with the thrown exception.
     */
    suspend fun restoreNandroidBackup(backupInfo: BackupInfo): Result<Unit> {
        return try {
            updateOperationProgress(RomOperation.RESTORING_BACKUP, 0f)

            backupManager.restoreNandroidBackup(backupInfo) { progress ->
                updateOperationProgress(RomOperation.RESTORING_BACKUP, progress)
            }.getOrThrow()

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("NANDroid backup restored: ${backupInfo.name}")
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to restore NANDroid backup: ${backupInfo.name}")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Applies Genesis AI optimization patches to the device system.
     *
     * Updates the manager's operationProgress state flow to report progress (APPLYING_OPTIMIZATIONS → COMPLETED or FAILED).
     *
     * @return A Result containing `Unit` on success, or a failure containing the thrown exception.
     */
    suspend fun installGenesisOptimizations(): Result<Unit> {
        return try {
            updateOperationProgress(RomOperation.APPLYING_OPTIMIZATIONS, 0f)

            systemModificationManager.installGenesisOptimizations { progress ->
                updateOperationProgress(RomOperation.APPLYING_OPTIMIZATIONS, progress)
            }.getOrThrow()

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("Genesis AI optimizations installed successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to install Genesis optimizations")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Retrieve the list of custom ROMs compatible with the current device.
     *
     * Uses the device model from the manager's capabilities to query available ROMs.
     *
     * @return A `Result` containing the list of compatible `AvailableRom` on success, or a failure with the underlying exception.
     */
    fun getAvailableRoms(): Result<List<AvailableRom>> {
        return try {
            val deviceModel = romToolsState.value.capabilities?.deviceModel ?: "unknown"
            val roms = romRepository.getCompatibleRoms(deviceModel)
            Result.success(roms)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get available ROMs")
            Result.failure(e)
        }
    }

    /**
     * Start downloading the specified available ROM and emit progress updates.
     *
     * @param rom The ROM metadata to download.
     * @return A flow that emits `DownloadProgress` updates reflecting bytes downloaded, total bytes, progress, speed, and completion state.
     */
    fun downloadRom(rom: AvailableRom): Flow<DownloadProgress> {
        return flashManager.downloadRom(rom)
    }

    /**
     * Set up Aurakai retention mechanisms so Aurakai is preserved across ROM operations.
     *
     * Configures retention independently of a full ROM flash and reports the configured state.
     *
     * @return A Result containing the configured `RetentionStatus` on success, or a failure with the encountered exception.
     */
    suspend fun setupAurakaiRetention(): Result<RetentionStatus> {
        return try {
            updateOperationProgress(RomOperation.SETTING_UP_RETENTION, 0f)

            val retentionStatus = retentionManager.setupRetentionMechanisms().getOrThrow()

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("🛡️ Aurakai retention mechanisms setup complete")
            Result.success(retentionStatus)

        } catch (e: Exception) {
            Timber.e(e, "Failed to setup Aurakai retention")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Unlock the device bootloader.
     *
     * Updates operation progress during the unlock process.
     *
     * @return A Result containing `Unit` on success, or a failure with the encountered exception.
     */
    suspend fun unlockBootloader(): Result<Unit> {
        return try {
            updateOperationProgress(RomOperation.UNLOCKING_BOOTLOADER, 0f)

            bootloaderManager.unlockBootloader().getOrThrow()

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("✅ Bootloader unlocked successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to unlock bootloader")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Install a custom recovery (e.g., TWRP) on the device.
     *
     * Updates operation progress during installation.
     *
     * @return A Result containing `Unit` on success, or a failure with the encountered exception.
     */
    suspend fun installRecovery(): Result<Unit> {
        return try {
            updateOperationProgress(RomOperation.INSTALLING_RECOVERY, 0f)

            recoveryManager.installCustomRecovery().getOrThrow()

            updateOperationProgress(RomOperation.COMPLETED, 100f)
            clearOperationProgress()

            Timber.i("✅ Custom recovery installed successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to install custom recovery")
            updateOperationProgress(RomOperation.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    /**
     * Update the current operation progress state.
     *
     * Sets the internal operation progress StateFlow to the provided operation and progress value.
     *
     * @param operation The operation being reported.
     * @param progress Completion progress as a float between 0.0 and 1.0.
     */
    private fun updateOperationProgress(operation: RomOperation, progress: Float) {
        _operationProgress.value = OperationProgress(operation, progress)
    }

    /**
     * Clears the current operation progress state.
     */
    private fun clearOperationProgress() {
        _operationProgress.value = null
    }

    /**
     * Determines whether root access is available on the device.
     *
     * @return `true` if a shell with root privileges can be executed, `false` otherwise.
     */
    private fun checkRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c 'echo test'")
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Provides the list of CPU ABIs supported by the current device.
     *
     * @return A list of supported CPU ABI strings in order of preference.
     */
    private fun getSupportedArchitectures(): List<String> {
        return Build.SUPPORTED_ABIS.toList()
    }

    // Companion object for static access
    companion object {
        private val romRepository = RomRepository()
    }
}

// Data classes
data class RomToolsState(
val capabilities: RomCapabilities? = null,
val isInitialized: Boolean = false,
val settings: RomToolsSettings = RomToolsSettings(),
val availableRoms: List<AvailableRom> = emptyList(),
val backups: List<BackupInfo> = emptyList()
)

data class RomCapabilities(
val hasRootAccess: Boolean,
val hasBootloaderAccess: Boolean,
val hasRecoveryAccess: Boolean,
val hasSystemWriteAccess: Boolean,
val supportedArchitectures: List<String>,
val deviceModel: String,
val androidVersion: String,
val securityPatchLevel: String
)

data class RomToolsSettings(
val autoBackup: Boolean = true,
val verifyRomSignatures: Boolean = true,
val enableGenesisOptimizations: Boolean = true,
val maxBackupCount: Int = 5,
val downloadDirectory: String = ""
)

data class OperationProgress(
val operation: RomOperation,
val progress: Float
)

/**
 * Represents the different types of ROM operations.
*/
enum class RomOperation {
SETTING_UP_RETENTION,      // 🛡️ Setting up Aurakai retention mechanisms
VERIFYING_ROM,
CREATING_BACKUP,
UNLOCKING_BOOTLOADER,
INSTALLING_RECOVERY,
FLASHING_ROM,
VERIFYING_INSTALLATION,
RESTORING_AURAKAI,         // 🔄 Restoring Aurakai after ROM flash
RESTORING_BACKUP,
APPLYING_OPTIMIZATIONS,
DOWNLOADING_ROM,
COMPLETED,
FAILED
}

data class RomFile(
val name: String,
val path: String,
val size: Long = 0L,
val checksum: String = ""
)

data class DeviceInfo(
val model: String,
val manufacturer: String,
val androidVersion: String,
val securityPatchLevel: String,
val bootloaderVersion: String
) {
companion object {
/**
 * Creates a DeviceInfo populated from the current Android build properties.
 *
 * @return A DeviceInfo containing the device model, manufacturer, Android version, security patch level, and bootloader version.
*/
fun getCurrentDevice(): DeviceInfo {
return DeviceInfo(
model = Build.MODEL,
manufacturer = Build.MANUFACTURER,
androidVersion = Build.VERSION.RELEASE,
securityPatchLevel = Build.VERSION.SECURITY_PATCH,
bootloaderVersion = Build.BOOTLOADER
)
}
}
}

data class BackupInfo(
val name: String,
val path: String,
val size: Long,
val createdAt: Long,
val deviceModel: String,
val androidVersion: String,
val partitions: List<String>
)

data class AvailableRom(
val name: String,
val version: String,
val androidVersion: String,
val downloadUrl: String,
val size: Long,
val checksum: String,
val description: String = "",
val maintainer: String = "",
val releaseDate: Long = 0L
)

data class DownloadProgress(
val bytesDownloaded: Long,
val totalBytes: Long,
val progress: Float,
val speed: Long,
val isCompleted: Boolean = false,
val error: String? = null
)

class RomRepository {
/**
 * Retrieves a list of ROMs compatible with the given device model.
 *
 * @param deviceModel The device model identifier to use when matching available ROMs.
 * @return A list of `AvailableRom` instances compatible with `deviceModel`. Currently this function returns an empty list while the repository lookup is not implemented.
*/
fun getCompatibleRoms(deviceModel: String): List<AvailableRom> {
// Would query online repository - placeholder for now
return emptyList()
}
}
