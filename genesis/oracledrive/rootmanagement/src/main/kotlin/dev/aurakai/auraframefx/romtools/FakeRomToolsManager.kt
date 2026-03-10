package dev.aurakai.auraframefx.romtools

import android.annotation.SuppressLint
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import dev.aurakai.auraframefx.romtools.retention.RetentionStatus
import dev.aurakai.auraframefx.romtools.retention.RetentionMechanism
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake implementation of RomToolsManager for Jetpack Compose previews.
 * Provides a pre-initialized state for UI rendering in previews.
 */
open class FakeRomToolsManager : RomToolsManager(
    bootloaderManager = FakeBootloaderManager(),
    recoveryManager = FakeRecoveryManager(),
    systemModificationManager = FakeSystemModificationManager(),
    flashManager = FakeFlashManager(),
    verificationManager = FakeRomVerificationManager(),
    backupManager = FakeBackupManager(),
    retentionManager = FakeRetentionManager()
) {
    // Pre-configured fake state for previews
    private val previewState: RomToolsState = RomToolsState(
        isInitialized = true,
        capabilities = RomCapabilities(
            hasRootAccess = true,
            hasBootloaderAccess = true,
            hasRecoveryAccess = true,
            hasSystemWriteAccess = true,
            supportedArchitectures = listOf("arm64-v8a", "armeabi-v7a"),
            deviceModel = "Pixel 7 Pro",
            androidVersion = "14",
            securityPatchLevel = "2025-10-05"
        )
    )

    init {
        // Initialize with preview state
        _romToolsState.value = previewState
    }
}

// === Minimal Fake Managers for Preview ===

class FakeBootloaderManager : BootloaderManager {
    override fun checkBootloaderAccess(): Boolean = true
    override fun isBootloaderUnlocked(): Boolean = true
    override suspend fun unlockBootloader(): Result<Unit> = Result.success(Unit)
    
    override fun collectPreflightSignals(): BootloaderManager.PreflightSignals {
        return BootloaderManager.PreflightSignals(
            isBootloaderUnlocked = true,
            oemUnlockSupported = true,
            verifiedBootState = "green",
            batteryLevel = 85,
            developerOptionsEnabled = true,
            oemUnlockAllowedUser = true,
            deviceFingerprint = "google/cheetah/cheetah:14/UP1A.231105.003/11010452:user/release-keys"
        )
    }
}

class FakeRecoveryManager : RecoveryManager {
    override fun checkRecoveryAccess(): Boolean = true
    override fun isCustomRecoveryInstalled(): Boolean = true
    override suspend fun installCustomRecovery(): Result<Unit> = Result.success(Unit)
}

class FakeSystemModificationManager : SystemModificationManager {
    override fun checkSystemWriteAccess(): Boolean = true
    override suspend fun installGenesisOptimizations(progressCallback: (Float) -> Unit): Result<Unit> {
        progressCallback(100f)
        return Result.success(Unit)
    }
}

class FakeFlashManager : FlashManager {
    override suspend fun flashRom(romFile: RomFile, progressCallback: (Float) -> Unit): Result<Unit> {
        // Simulate flashing progress
        for (i in 0..100 step 10) {
            progressCallback(i.toFloat())
        }
        return Result.success(Unit)
    }

    override fun downloadRom(rom: AvailableRom): Flow<DownloadProgress> {
        // Return a fake download progress flow
        return flowOf(
            DownloadProgress(
                bytesDownloaded = 1024 * 1024 * 500, // 500 MB
                totalBytes = 1024 * 1024 * 1000,      // 1 GB
                progress = 0.5f,
                speed = 1024 * 1024 * 10,             // 10 MB/s
                isCompleted = false
            )
        )
    }
}

class FakeRomVerificationManager : RomVerificationManager {
    override suspend fun verifyRomFile(romFile: RomFile): Result<Unit> = Result.success(Unit)
    override suspend fun verifyInstallation(): Result<Unit> = Result.success(Unit)
}

class FakeBackupManager : BackupManager {
    override suspend fun createFullBackup(): Result<Unit> = Result.success(Unit)

    @SuppressLint("SdCardPath")
    override suspend fun createNandroidBackup(
        name: String,
        progressCallback: (Float) -> Unit
    ): Result<BackupInfo> {
        progressCallback(100f)
        return Result.success(
            BackupInfo(
                name = "fake-backup",
                path = "/sdcard/fake-backup",
                size = 1024 * 1024 * 500L, // 500 MB
                createdAt = System.currentTimeMillis(),
                deviceModel = "Pixel 7 Pro",
                androidVersion = "14",
                partitions = listOf("system", "boot", "vendor")
            )
        )
    }

    override suspend fun restoreNandroidBackup(
        backup: BackupInfo,
        progressCallback: (Float) -> Unit
    ): Result<Unit> {
        progressCallback(100f)
        return Result.success(Unit)
    }
}

class FakeRetentionManager : AurakaiRetentionManager {
    override suspend fun setupRetentionMechanisms(): Result<RetentionStatus> {
        return Result.success(
            RetentionStatus(
                mechanisms = mapOf(
                    RetentionMechanism.APK_BACKUP to true,
                    RetentionMechanism.ADDON_D_SCRIPT to true,
                    RetentionMechanism.RECOVERY_ZIP to true,
                    RetentionMechanism.MAGISK_MODULE to false
                ),
                retentionDirPath = "/data/local/genesis_retention",
                packageName = "dev.aurakai.auraframefx",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun restoreAurakaiAfterRomFlash(): Result<Unit> {
        return Result.success(Unit)
    }
}
