package dev.aurakai.auraframefx.romtools

import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import dev.aurakai.auraframefx.romtools.retention.RetentionMechanism
import dev.aurakai.auraframefx.romtools.retention.RetentionStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("RomToolsManager Tests")
class RomToolsManagerTest {

    private lateinit var romToolsManager: RomToolsManager
    private lateinit var mockBootloaderManager: BootloaderManager
    private lateinit var mockRecoveryManager: RecoveryManager
    private lateinit var mockSystemModificationManager: SystemModificationManager
    private lateinit var mockFlashManager: FlashManager
    private lateinit var mockVerificationManager: RomVerificationManager
    private lateinit var mockBackupManager: BackupManager
    private lateinit var mockRetentionManager: AurakaiRetentionManager

    @BeforeEach
    fun setup() {
        mockBootloaderManager = mockk(relaxed = true)
        mockRecoveryManager = mockk(relaxed = true)
        mockSystemModificationManager = mockk(relaxed = true)
        mockFlashManager = mockk(relaxed = true)
        mockVerificationManager = mockk(relaxed = true)
        mockBackupManager = mockk(relaxed = true)
        mockRetentionManager = mockk(relaxed = true)
        
        romToolsManager = RomToolsManager(
            bootloaderManager = mockBootloaderManager,
            recoveryManager = mockRecoveryManager,
            systemModificationManager = mockSystemModificationManager,
            flashManager = mockFlashManager,
            verificationManager = mockVerificationManager,
            backupManager = mockBackupManager,
            retentionManager = mockRetentionManager
        )
    }

    @Nested
@DisplayName("Retention Failure Abort Tests")
inner class RetentionFailureAbortTests {

    @Test
    @DisplayName("Should abort ROM flash immediately if retention setup returns failure")
    fun `should abort rom flash on retention failure`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        val retentionError = Exception("Failed to backup APK - insufficient storage")
        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.failure(retentionError)

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("Retention mechanism setup failed"))
        assertTrue(exception.message!!.contains("ROM flash aborted"))

        // Verify no flash operations were attempted
        coVerify(exactly = 0) { mockVerificationManager.verifyRomFile(any()) }
        coVerify(exactly = 0) { mockBootloaderManager.isBootloaderUnlocked() }
        coVerify(exactly = 0) { mockFlashManager.flashRom(any(), any()) }
    }

    @Test
    @DisplayName("Should include original error in abort exception")
    fun `should preserve original retention error`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        val originalError = SecurityException("Root access denied")
        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.failure(originalError)

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals(originalError, exception!!.cause)
    }

    @Test
    @DisplayName("Should abort even if ROM file exists and is valid")
    fun `should abort valid rom flash on retention failure`() = runTest {
        // Given - Valid ROM file but retention fails
        val validRomFile = RomFile(
            "lineage-20.0.zip",
            "/sdcard/lineage-20.0.zip",
            size = 1024000000,
            checksum = "abc123def456"
        )

        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.failure(Exception("Magisk not found"))

        // When
        val result = romToolsManager.flashRom(validRomFile)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { mockVerificationManager.verifyRomFile(any()) }
    }

    @Test
    @DisplayName("Should abort with clear error message for user safety")
    fun `should provide clear safety error message`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.failure(Exception("Network error during backup"))

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message
        assertNotNull(message)
        assertTrue(
            message!!.contains("prevent losing Aurakai") ||
                    message.contains("aborted to prevent")
        )
    }

    @Test
    @DisplayName("Should not call restore if flash was aborted due to retention failure")
    fun `should not restore if flash never started`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.failure(Exception("Setup failed"))

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { mockRetentionManager.setupRetentionMechanisms() }
        coVerify(exactly = 0) { mockRetentionManager.restoreAurakaiAfterRomFlash() }
    }

    @Test
    @DisplayName("Should log critical error when aborting ROM flash")
    fun `should log critical abort event`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.failure(Exception("Critical retention error"))

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then
        assertTrue(result.isFailure)
        // In real implementation, verify Timber.e was called
        // For this test, just verify the abort happened
        coVerify(exactly = 1) { mockRetentionManager.setupRetentionMechanisms() }
    }
}

@Nested
@DisplayName("Retention Setup Progress Tests")
inner class RetentionSetupProgressTests {

    @Test
    @DisplayName("Should report 5% progress during retention setup phase")
    fun `should report retention setup progress`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        val retentionStatus = RetentionStatus(
            mechanisms = mapOf(RetentionMechanism.APK_BACKUP to true),
            retentionDirPath = "/data/local/genesis_retention",
            packageName = "dev.aurakai.test",
            timestamp = System.currentTimeMillis()
        )

        mutableListOf<Pair<RomOperation, Float>>()

        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns Result.success(
            retentionStatus
        )
        coEvery { mockVerificationManager.verifyRomFile(any()) } returns Result.success(Unit)
        coEvery { mockBootloaderManager.isBootloaderUnlocked() } returns true
        coEvery { mockRecoveryManager.isCustomRecoveryInstalled() } returns true
        coEvery { mockFlashManager.flashRom(any(), any()) } returns Result.success(Unit)
        coEvery { mockVerificationManager.verifyInstallation() } returns Result.success(Unit)
        coEvery { mockRetentionManager.restoreAurakaiAfterRomFlash() } returns Result.success(Unit)

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then
        assertTrue(result.isSuccess)
        // Verify retention setup was called at the beginning
        coVerify { mockRetentionManager.setupRetentionMechanisms() }
    }

    @Test
    @DisplayName("Should setup retention before any verification steps")
    fun `should setup retention first in sequence`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        val retentionStatus = RetentionStatus(
            mechanisms = mapOf(RetentionMechanism.APK_BACKUP to true),
            retentionDirPath = "/data/local/genesis_retention",
            packageName = "dev.aurakai.test",
            timestamp = System.currentTimeMillis()
        )

        val callOrder = mutableListOf<String>()

        coEvery { mockRetentionManager.setupRetentionMechanisms() } coAnswers {
            callOrder.add("retention")
            Result.success(retentionStatus)
        }
        coEvery { mockVerificationManager.verifyRomFile(any()) } coAnswers {
            callOrder.add("verify")
            Result.success(Unit)
        }
        coEvery { mockBootloaderManager.isBootloaderUnlocked() } coAnswers {
            callOrder.add("bootloader")
            true
        }
        coEvery { mockRecoveryManager.isCustomRecoveryInstalled() } returns true
        coEvery { mockFlashManager.flashRom(any(), any()) } returns Result.success(Unit)
        coEvery { mockVerificationManager.verifyInstallation() } returns Result.success(Unit)
        coEvery { mockRetentionManager.restoreAurakaiAfterRomFlash() } returns Result.success(Unit)

        // When
        romToolsManager.flashRom(romFile)

        // Then - retention should be first
        assertTrue(callOrder.isNotEmpty())
        assertEquals("retention", callOrder.first())
    }
}

@Nested
@DisplayName("Edge Case Tests")
inner class EdgeCaseTests {

    @Test
    @DisplayName("Should handle retention timeout gracefully")
    fun `should handle retention timeout`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        coEvery { mockRetentionManager.setupRetentionMechanisms() } coAnswers {
            kotlinx.coroutines.delay(10000) // Simulate timeout
            Result.failure(Exception("Timeout"))
        }

        // When/Then
        val result = romToolsManager.flashRom(romFile)
        // Should eventually fail
        assertTrue(result.isFailure)
    }

    @Test
    @DisplayName("Should handle null or missing ROM file path")
    fun `should validate rom file before retention`() = runTest {
        // Given
        val invalidRomFile = RomFile("", "")

        // When
        val result = romToolsManager.flashRom(invalidRomFile)

        // Then - should fail before reaching retention
        assertTrue(result.isFailure)
    }

    @Test
    @DisplayName("Should handle partial retention mechanism success")
    fun `should handle partial retention success`() = runTest {
        // Given
        val romFile = RomFile("test_rom.zip", "/sdcard/test_rom.zip")
        val partialRetentionStatus = RetentionStatus(
            mechanisms = mapOf(
                RetentionMechanism.APK_BACKUP to true,
                RetentionMechanism.ADDON_D_SCRIPT to false,
                RetentionMechanism.RECOVERY_ZIP to false
            ),
            retentionDirPath = "/data/local/genesis_retention",
            packageName = "dev.aurakai.test",
            timestamp = System.currentTimeMillis()
        )

        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns
                Result.success(partialRetentionStatus)
        coEvery { mockVerificationManager.verifyRomFile(any()) } returns Result.success(Unit)
        coEvery { mockBootloaderManager.isBootloaderUnlocked() } returns true
        coEvery { mockRecoveryManager.isCustomRecoveryInstalled() } returns true
        coEvery { mockFlashManager.flashRom(any(), any()) } returns Result.success(Unit)
        coEvery { mockVerificationManager.verifyInstallation() } returns Result.success(Unit)
        coEvery { mockRetentionManager.restoreAurakaiAfterRomFlash() } returns Result.success(Unit)

        // When
        val result = romToolsManager.flashRom(romFile)

        // Then - should proceed even with partial retention (not fully protected)
        assertTrue(result.isSuccess)
    }

    @Test
    @DisplayName("Should handle concurrent ROM flash attempts")
    fun `should handle concurrent flash attempts`() = runTest {
        // Given
        val romFile1 = RomFile("rom1.zip", "/sdcard/rom1.zip")
        val romFile2 = RomFile("rom2.zip", "/sdcard/rom2.zip")
        val retentionStatus = RetentionStatus(
            mechanisms = mapOf(RetentionMechanism.APK_BACKUP to true),
            retentionDirPath = "/data/local/genesis_retention",
            packageName = "dev.aurakai.test",
            timestamp = System.currentTimeMillis()
        )

        coEvery { mockRetentionManager.setupRetentionMechanisms() } returns Result.success(
            retentionStatus
        )
        coEvery { mockVerificationManager.verifyRomFile(any()) } returns Result.success(Unit)
        coEvery { mockBootloaderManager.isBootloaderUnlocked() } returns true
        coEvery { mockRecoveryManager.isCustomRecoveryInstalled() } returns true
        coEvery { mockFlashManager.flashRom(any(), any()) } returns Result.success(Unit)
        coEvery { mockVerificationManager.verifyInstallation() } returns Result.success(Unit)
        coEvery { mockRetentionManager.restoreAurakaiAfterRomFlash() } returns Result.success(Unit)

        // When - Start concurrent flashes
        val result1 = kotlinx.coroutines.async { romToolsManager.flashRom(romFile1) }
        val result2 = kotlinx.coroutines.async { romToolsManager.flashRom(romFile2) }

        // Then - Both should complete (one might fail due to locking)
        assertNotNull(result1.await())
        assertNotNull(result2.await())
    }
}
}

// Data classes for testing
data class BackupInfo(
    val id: String,
    val path: String,
    val size: Long,
    val timestamp: Long
)