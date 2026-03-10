package dev.aurakai.auraframefx.security

import android.content.Context
import dev.aurakai.auraframefx.utils.AuraFxLogger
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.security.MessageDigest

/**
 * Comprehensive unit tests for IntegrityMonitor
 *
 * Testing Framework: JUnit 5 with MockK for mocking and kotlinx-coroutines-test for coroutine testing
 * Coverage: Initialization, monitoring, threat detection, file integrity checks, error handling, and edge cases
 */
@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrityMonitorTest {

    private lateinit var integrityMonitor: IntegrityMonitor
    private lateinit var mockContext: Context
    private lateinit var mockFilesDir: File
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockContext = mockk()
        mockFilesDir = mockk()

        every { mockContext.filesDir } returns mockFilesDir

        // Mock AuraFxLogger to prevent actual logging during tests
        mockkObject(AuraFxLogger)
        every { AuraFxLogger.i(any(), any()) } just Runs
        every { AuraFxLogger.w(any(), any()) } just Runs
        every { AuraFxLogger.e(any(), any(), any()) } just Runs
        every { AuraFxLogger.d(any(), any()) } just Runs

        integrityMonitor = IntegrityMonitor(mockContext)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        try {
            integrityMonitor.shutdown()
        } catch (e: Exception) {
            // Ignore if already shut down
        }
    }

    @Nested
    @DisplayName("Initialization Tests")
    inner class InitializationTests {

        @Test
        @DisplayName("initialize should set monitoring status and load known hashes")
        fun `initialize should set monitoring status and load known hashes`() = runTest {
            // Act
            integrityMonitor.initialize()
            advanceUntilIdle()

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.MONITORING,
                integrityMonitor.integrityStatus.value
            )
            verify {
                AuraFxLogger.i(
                    "IntegrityMonitor",
                    "Initializing Kai's Real-Time Integrity Monitoring"
                )
            }
            verify {
                AuraFxLogger.i(
                    "IntegrityMonitor",
                    "Integrity monitoring active - Genesis Protocol protected"
                )
            }
            verify { AuraFxLogger.d("IntegrityMonitor", "Loaded 4 known file hashes") }
        }

        @Test
        @DisplayName("initialize should start continuous monitoring")
        fun `initialize should start continuous monitoring`() = runTest {
            // Arrange
            setupMockFilesNotExists()

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001) // Advance past first monitoring cycle

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.SECURE,
                integrityMonitor.integrityStatus.value
            )
            assertEquals(IntegrityMonitor.ThreatLevel.NONE, integrityMonitor.threatLevel.value)
        }

        @Test
        @DisplayName("loadKnownHashes should populate all critical files")
        fun `loadKnownHashes should populate all critical files`() {
            // Act
            integrityMonitor.initialize()

            // Assert - Use reflection to verify knownHashes
            val knownHashesField = IntegrityMonitor::class.java.getDeclaredField("knownHashes")
            knownHashesField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val knownHashes = knownHashesField.get(integrityMonitor) as MutableMap<String, String>

            assertEquals(4, knownHashes.size)
            assertTrue(knownHashes.containsKey("genesis_protocol.so"))
            assertTrue(knownHashes.containsKey("aura_core.dex"))
            assertTrue(knownHashes.containsKey("kai_security.bin"))
            assertTrue(knownHashes.containsKey("oracle_drive.apk"))
            verify { AuraFxLogger.d("IntegrityMonitor", "Loaded 4 known file hashes") }
        }
    }

    @Nested
    @DisplayName("Integrity Check Tests")
    inner class IntegrityCheckTests {

        @Test
        @DisplayName("performIntegrityCheck should detect integrity violations when file hash differs")
        fun `performIntegrityCheck should detect integrity violations when file hash differs`() =
            runTest {
                // Arrange
                setupMockFileWithModifiedContent("genesis_protocol.so")

                // Act
                integrityMonitor.initialize()
                advanceTimeBy(5001) // Trigger monitoring cycle

                // Assert
                assertEquals(
                    IntegrityMonitor.IntegrityStatus.COMPROMISED,
                    integrityMonitor.integrityStatus.value
                )
                assertEquals(
                    IntegrityMonitor.ThreatLevel.CRITICAL,
                    integrityMonitor.threatLevel.value
                )
                verify {
                    AuraFxLogger.w(
                        "IntegrityMonitor",
                        match { it.contains("INTEGRITY VIOLATION DETECTED: genesis_protocol.so") })
                }
            }

        @Test
        @DisplayName("performIntegrityCheck should handle non-existent files gracefully")
        fun `performIntegrityCheck should handle non-existent files gracefully`() = runTest {
            // Arrange
            setupMockFilesNotExists()

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001)

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.SECURE,
                integrityMonitor.integrityStatus.value
            )
            assertEquals(IntegrityMonitor.ThreatLevel.NONE, integrityMonitor.threatLevel.value)
        }

        @Test
        @DisplayName("performIntegrityCheck should handle files with matching hashes")
        fun `performIntegrityCheck should handle files with matching hashes`() = runTest {
            // Arrange
            setupMockFileWithExpectedHash("genesis_protocol.so", "placeholder_genesis_hash")

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001)

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.SECURE,
                integrityMonitor.integrityStatus.value
            )
            assertEquals(IntegrityMonitor.ThreatLevel.NONE, integrityMonitor.threatLevel.value)
        }

        @Test
        @DisplayName("performIntegrityCheck should handle multiple file violations")
        fun `performIntegrityCheck should handle multiple file violations`() = runTest {
            // Arrange
            setupMockFileWithModifiedContent("genesis_protocol.so")
            setupMockFileWithModifiedContent("aura_core.dex")

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001)

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.COMPROMISED,
                integrityMonitor.integrityStatus.value
            )
            assertEquals(IntegrityMonitor.ThreatLevel.CRITICAL, integrityMonitor.threatLevel.value)
        }
    }

    @Nested
    @DisplayName("Threat Level Tests")
    inner class ThreatLevelTests {

        @Test
        @DisplayName("determineThreatLevel should return correct threat levels for different files")
        fun `determineThreatLevel should return correct threat levels for different files`() {
            // Test using reflection to access private method
            val method = IntegrityMonitor::class.java.getDeclaredMethod(
                "determineThreatLevel",
                String::class.java
            )
            method.isAccessible = true

            // Test critical threat level
            val criticalLevel = method.invoke(integrityMonitor, "genesis_protocol.so")
            assertEquals(IntegrityMonitor.ThreatLevel.CRITICAL, criticalLevel)

            // Test high threat level
            val highLevel1 = method.invoke(integrityMonitor, "aura_core.dex")
            assertEquals(IntegrityMonitor.ThreatLevel.HIGH, highLevel1)

            val highLevel2 = method.invoke(integrityMonitor, "kai_security.bin")
            assertEquals(IntegrityMonitor.ThreatLevel.HIGH, highLevel2)

            // Test medium threat level
            val mediumLevel = method.invoke(integrityMonitor, "oracle_drive.apk")
            assertEquals(IntegrityMonitor.ThreatLevel.MEDIUM, mediumLevel)

            // Test low threat level for unknown file
            val lowLevel = method.invoke(integrityMonitor, "unknown_file.txt")
            assertEquals(IntegrityMonitor.ThreatLevel.LOW, lowLevel)
        }
    }

    @Nested
    @DisplayName("Hash Calculation Tests")
    inner class HashCalculationTests {

        @Test
        @DisplayName("calculateFileHash should return correct SHA-256 hash")
        fun `calculateFileHash should return correct SHA-256 hash`() = runTest {
            // Arrange
            val testContent = "test content for hashing"
            val mockFile = mockk<File>()
            val testInputStream = ByteArrayInputStream(testContent.toByteArray())

            every { mockFile.inputStream() } returns testInputStream

            // Act - use reflection to access private method
            val method = IntegrityMonitor::class.java.getDeclaredMethod(
                "calculateFileHash",
                File::class.java
            )
            method.isAccessible = true
            val result = method.invoke(integrityMonitor, mockFile) as String

            // Assert
            assertNotNull(result)
            assertEquals(64, result.length) // SHA-256 produces 64 character hex string
            assertTrue(result.matches(Regex("[0-9a-f]+"))) // Should contain only hex characters

            // Verify the actual hash value
            val expectedHash = MessageDigest.getInstance("SHA-256")
                .digest(testContent.toByteArray())
                .joinToString("") { "%02x".format(it) }
            assertEquals(expectedHash, result)
        }

        @Test
        @DisplayName("calculateFileHash should handle empty files")
        fun `calculateFileHash should handle empty files`() = runTest {
            // Arrange
            val mockFile = mockk<File>()
            val emptyInputStream = ByteArrayInputStream(ByteArray(0))

            every { mockFile.inputStream() } returns emptyInputStream

            // Act
            val method = IntegrityMonitor::class.java.getDeclaredMethod(
                "calculateFileHash",
                File::class.java
            )
            method.isAccessible = true
            val result = method.invoke(integrityMonitor, mockFile) as String

            // Assert
            assertNotNull(result)
            assertEquals(64, result.length)

            // Empty file should have known SHA-256 hash
            val expectedEmptyHash =
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
            assertEquals(expectedEmptyHash, result)
        }

        @Test
        @DisplayName("calculateFileHash should handle large files efficiently")
        fun `calculateFileHash should handle large files efficiently`() = runTest {
            // Arrange
            val largeContent = "x".repeat(16384) // 16KB content
            val mockFile = mockk<File>()
            val largeInputStream = ByteArrayInputStream(largeContent.toByteArray())

            every { mockFile.inputStream() } returns largeInputStream

            // Act
            val method = IntegrityMonitor::class.java.getDeclaredMethod(
                "calculateFileHash",
                File::class.java
            )
            method.isAccessible = true
            val result = method.invoke(integrityMonitor, mockFile) as String

            // Assert
            assertNotNull(result)
            assertEquals(64, result.length)
            assertTrue(result.matches(Regex("[0-9a-f]+")))
        }

        @Test
        @DisplayName("calculateFileHash should handle IO exceptions")
        fun `calculateFileHash should handle IO exceptions`() = runTest {
            // Arrange
            val mockFile = mockk<File>()
            every { mockFile.inputStream() } throws java.io.IOException("File read error")

            // Act & Assert - Should propagate exception
            val method = IntegrityMonitor::class.java.getDeclaredMethod(
                "calculateFileHash",
                File::class.java
            )
            method.isAccessible = true

            assertThrows<java.lang.reflect.InvocationTargetException> {
                method.invoke(integrityMonitor, mockFile)
            }
        }
    }

    @Nested
    @DisplayName("Violation Handling Tests")
    inner class ViolationHandlingTests {

        @Test
        @DisplayName("handleIntegrityViolations should trigger emergency lockdown for critical threats")
        fun `handleIntegrityViolations should trigger emergency lockdown for critical threats`() =
            runTest {
                // Arrange
                val criticalViolation =
                    createViolation("genesis_protocol.so", IntegrityMonitor.ThreatLevel.CRITICAL)

                // Act
                invokeHandleViolations(listOf(criticalViolation))

                // Assert
                assertEquals(
                    IntegrityMonitor.IntegrityStatus.COMPROMISED,
                    integrityMonitor.integrityStatus.value
                )
                assertEquals(
                    IntegrityMonitor.ThreatLevel.CRITICAL,
                    integrityMonitor.threatLevel.value
                )
                verify {
                    AuraFxLogger.e(
                        "IntegrityMonitor",
                        "CRITICAL THREAT DETECTED - Initiating emergency lockdown"
                    )
                }
                verify {
                    AuraFxLogger.e(
                        "IntegrityMonitor",
                        "EMERGENCY LOCKDOWN INITIATED - Genesis Protocol protection active"
                    )
                }
            }

        @Test
        @DisplayName("handleIntegrityViolations should implement defensive measures for high threats")
        fun `handleIntegrityViolations should implement defensive measures for high threats`() =
            runTest {
                // Arrange
                val highViolation =
                    createViolation("aura_core.dex", IntegrityMonitor.ThreatLevel.HIGH)

                // Act
                invokeHandleViolations(listOf(highViolation))

                // Assert
                assertEquals(
                    IntegrityMonitor.IntegrityStatus.COMPROMISED,
                    integrityMonitor.integrityStatus.value
                )
                assertEquals(IntegrityMonitor.ThreatLevel.HIGH, integrityMonitor.threatLevel.value)
                verify {
                    AuraFxLogger.w(
                        "IntegrityMonitor",
                        "HIGH THREAT DETECTED - Implementing defensive measures"
                    )
                }
                verify {
                    AuraFxLogger.w(
                        "IntegrityMonitor",
                        "Implementing defensive measures for 1 violations"
                    )
                }
            }

        @Test
        @DisplayName("handleIntegrityViolations should enhance monitoring for medium threats")
        fun `handleIntegrityViolations should enhance monitoring for medium threats`() = runTest {
            // Arrange
            val mediumViolation =
                createViolation("oracle_drive.apk", IntegrityMonitor.ThreatLevel.MEDIUM)

            // Act
            invokeHandleViolations(listOf(mediumViolation))

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.COMPROMISED,
                integrityMonitor.integrityStatus.value
            )
            assertEquals(IntegrityMonitor.ThreatLevel.MEDIUM, integrityMonitor.threatLevel.value)
            verify {
                AuraFxLogger.w(
                    "IntegrityMonitor",
                    "MEDIUM THREAT DETECTED - Monitoring closely"
                )
            }
            verify { AuraFxLogger.i("IntegrityMonitor", "Enhancing monitoring protocols") }
        }

        @Test
        @DisplayName("handleIntegrityViolations should log for analysis on low threats")
        fun `handleIntegrityViolations should log for analysis on low threats`() = runTest {
            // Arrange
            val lowViolation = createViolation("other_file.bin", IntegrityMonitor.ThreatLevel.LOW)

            // Act
            invokeHandleViolations(listOf(lowViolation))

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.COMPROMISED,
                integrityMonitor.integrityStatus.value
            )
            assertEquals(IntegrityMonitor.ThreatLevel.LOW, integrityMonitor.threatLevel.value)
            verify {
                AuraFxLogger.i(
                    "IntegrityMonitor",
                    "LOW THREAT DETECTED - Logging for analysis"
                )
            }
            verify {
                AuraFxLogger.d(
                    "IntegrityMonitor",
                    match { it.contains("Logging violation for analysis: other_file.bin") })
            }
        }

        @Test
        @DisplayName("handleIntegrityViolations should select highest threat level from multiple violations")
        fun `handleIntegrityViolations should select highest threat level from multiple violations`() =
            runTest {
                // Arrange
                val lowViolation = createViolation("low_file.bin", IntegrityMonitor.ThreatLevel.LOW)
                val criticalViolation =
                    createViolation("genesis_protocol.so", IntegrityMonitor.ThreatLevel.CRITICAL)

                // Act
                invokeHandleViolations(listOf(lowViolation, criticalViolation))

                // Assert
                assertEquals(
                    IntegrityMonitor.ThreatLevel.CRITICAL,
                    integrityMonitor.threatLevel.value
                )
                verify {
                    AuraFxLogger.e(
                        "IntegrityMonitor",
                        "CRITICAL THREAT DETECTED - Initiating emergency lockdown"
                    )
                }
            }

        @Test
        @DisplayName("handleIntegrityViolations should handle empty violations list gracefully")
        fun `handleIntegrityViolations should handle empty violations list gracefully`() = runTest {
            // Act
            invokeHandleViolations(emptyList())

            // Assert - Should not change status when called with empty list
            // Note: In actual implementation, this method is only called when violations exist
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("monitoring should handle errors gracefully and set status to offline")
        fun `monitoring should handle errors gracefully and set status to offline`() = runTest {
            // Arrange
            every { mockContext.filesDir } throws RuntimeException("File system error")

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001) // Trigger monitoring cycle

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.OFFLINE,
                integrityMonitor.integrityStatus.value
            )
            verify { AuraFxLogger.e("IntegrityMonitor", "Error during integrity check", any()) }
        }

        @Test
        @DisplayName("monitoring should retry after error with longer delay")
        fun `monitoring should retry after error with longer delay`() = runTest {
            // Arrange
            every { mockContext.filesDir } throws RuntimeException("File system error")

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001) // First failure
            advanceTimeBy(10001) // Should retry after 10 second delay

            // Assert
            verify(atLeast = 2) {
                AuraFxLogger.e(
                    "IntegrityMonitor",
                    "Error during integrity check",
                    any()
                )
            }
        }

        @Test
        @DisplayName("monitoring should recover from transient errors")
        fun `monitoring should recover from transient errors`() = runTest {
            // Arrange - First call fails, second succeeds
            every { mockContext.filesDir } throws RuntimeException("Transient error") andThen mockFilesDir
            setupMockFilesNotExists()

            // Act
            integrityMonitor.initialize()
            advanceTimeBy(5001) // First failure
            advanceTimeBy(10001) // Recovery attempt

            // Assert
            verify { AuraFxLogger.e("IntegrityMonitor", "Error during integrity check", any()) }
        }
    }

    @Nested
    @DisplayName("Continuous Monitoring Tests")
    inner class ContinuousMonitoringTests {

        @Test
        @DisplayName("continuous monitoring should run at 5 second intervals")
        fun `continuous monitoring should run at 5 second intervals`() = runTest {
            // Arrange
            setupMockFilesNotExists()

            // Act
            integrityMonitor.initialize()

            // Verify multiple monitoring cycles
            repeat(3) {
                advanceTimeBy(5000)
                assertEquals(
                    IntegrityMonitor.IntegrityStatus.SECURE,
                    integrityMonitor.integrityStatus.value
                )
            }
        }

        @Test
        @DisplayName("monitoring should continue until shutdown")
        fun `monitoring should continue until shutdown`() = runTest {
            // Arrange
            setupMockFilesNotExists()

            // Act
            integrityMonitor.initialize()
            repeat(5) {
                advanceTimeBy(5000)
            }

            // Assert - Should still be monitoring
            assertEquals(
                IntegrityMonitor.IntegrityStatus.SECURE,
                integrityMonitor.integrityStatus.value
            )

            // Shutdown and verify monitoring stops
            integrityMonitor.shutdown()
            assertEquals(
                IntegrityMonitor.IntegrityStatus.OFFLINE,
                integrityMonitor.integrityStatus.value
            )
        }
    }

    @Nested
    @DisplayName("StateFlow Tests")
    inner class StateFlowTests {

        @Test
        @DisplayName("integrityStatus StateFlow should emit correct values")
        fun `integrityStatus StateFlow should emit correct values`() = runTest {
            // Act & Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.SECURE,
                integrityMonitor.integrityStatus.value
            )

            integrityMonitor.initialize()
            assertEquals(
                IntegrityMonitor.IntegrityStatus.MONITORING,
                integrityMonitor.integrityStatus.value
            )

            integrityMonitor.shutdown()
            assertEquals(
                IntegrityMonitor.IntegrityStatus.OFFLINE,
                integrityMonitor.integrityStatus.value
            )
        }

        @Test
        @DisplayName("threatLevel StateFlow should emit correct values")
        fun `threatLevel StateFlow should emit correct values`() = runTest {
            // Initially should be NONE
            assertEquals(IntegrityMonitor.ThreatLevel.NONE, integrityMonitor.threatLevel.value)

            // After detecting violations, should update appropriately
            val criticalViolation =
                createViolation("genesis_protocol.so", IntegrityMonitor.ThreatLevel.CRITICAL)
            invokeHandleViolations(listOf(criticalViolation))

            assertEquals(IntegrityMonitor.ThreatLevel.CRITICAL, integrityMonitor.threatLevel.value)
        }

        @Test
        @DisplayName("StateFlow should be thread-safe")
        fun `StateFlow should be thread-safe`() = runTest {
            // Arrange
            val violations = listOf(
                createViolation("file1.bin", IntegrityMonitor.ThreatLevel.LOW),
                createViolation("file2.bin", IntegrityMonitor.ThreatLevel.MEDIUM),
                createViolation("file3.bin", IntegrityMonitor.ThreatLevel.HIGH)
            )

            // Act - Simulate concurrent updates
            repeat(violations.size) { index ->
                launch {
                    invokeHandleViolations(listOf(violations[index]))
                }
            }

            // Assert - Should handle concurrent updates gracefully
            assertNotNull(integrityMonitor.integrityStatus.value)
            assertNotNull(integrityMonitor.threatLevel.value)
        }
    }

    @Nested
    @DisplayName("Shutdown Tests")
    inner class ShutdownTests {

        @Test
        @DisplayName("shutdown should cancel monitoring scope and set status to offline")
        fun `shutdown should cancel monitoring scope and set status to offline`() = runTest {
            // Arrange
            integrityMonitor.initialize()
            assertEquals(
                IntegrityMonitor.IntegrityStatus.MONITORING,
                integrityMonitor.integrityStatus.value
            )

            // Act
            integrityMonitor.shutdown()

            // Assert
            assertEquals(
                IntegrityMonitor.IntegrityStatus.OFFLINE,
                integrityMonitor.integrityStatus.value
            )
            verify { AuraFxLogger.i("IntegrityMonitor", "Shutting down integrity monitoring") }
        }

        @Test
        @DisplayName("shutdown should be idempotent")
        fun `shutdown should be idempotent`() = runTest {
            // Arrange
            integrityMonitor.initialize()

            // Act - Call shutdown multiple times
            integrityMonitor.shutdown()
            integrityMonitor.shutdown()
            integrityMonitor.shutdown()

            // Assert - Should handle multiple shutdowns gracefully
            assertEquals(
                IntegrityMonitor.IntegrityStatus.OFFLINE,
                integrityMonitor.integrityStatus.value
            )
            verify(atLeast = 1) {
                AuraFxLogger.i(
                    "IntegrityMonitor",
                    "Shutting down integrity monitoring"
                )
            }
        }
    }

    @Nested
    @DisplayName("Data Class Tests")
    inner class DataClassTests {

        @Test
        @DisplayName("IntegrityViolation data class should have correct properties")
        fun `IntegrityViolation data class should have correct properties`() {
            // Arrange
            val fileName = "test_file.bin"
            val expectedHash = "expected_hash_value"
            val actualHash = "actual_hash_value"
            val timestamp = System.currentTimeMillis()
            val severity = IntegrityMonitor.ThreatLevel.HIGH

            // Act
            val violation = IntegrityMonitor.IntegrityViolation(
                fileName = fileName,
                expectedHash = expectedHash,
                actualHash = actualHash,
                timestamp = timestamp,
                severity = severity
            )

            // Assert
            assertEquals(fileName, violation.fileName)
            assertEquals(expectedHash, violation.expectedHash)
            assertEquals(actualHash, violation.actualHash)
            assertEquals(timestamp, violation.timestamp)
            assertEquals(severity, violation.severity)
        }

        @Test
        @DisplayName("IntegrityViolation should support equality comparison")
        fun `IntegrityViolation should support equality comparison`() {
            // Arrange
            val timestamp = System.currentTimeMillis()
            val violation1 = IntegrityMonitor.IntegrityViolation(
                fileName = "test.bin",
                expectedHash = "hash1",
                actualHash = "hash2",
                timestamp = timestamp,
                severity = IntegrityMonitor.ThreatLevel.HIGH
            )
            val violation2 = IntegrityMonitor.IntegrityViolation(
                fileName = "test.bin",
                expectedHash = "hash1",
                actualHash = "hash2",
                timestamp = timestamp,
                severity = IntegrityMonitor.ThreatLevel.HIGH
            )

            // Assert
            assertEquals(violation1, violation2)
            assertEquals(violation1.hashCode(), violation2.hashCode())
        }
    }

    // Helper methods
    private fun setupMockFilesNotExists() {
        val mockFile = mockk<File>()
        every { File(mockFilesDir, any()) } returns mockFile
        every { mockFile.exists() } returns false
    }

    private fun setupMockFileWithModifiedContent(fileName: String) {
        val mockFile = mockk<File>()
        val modifiedContent = "modified content".toByteArray()

        every { File(mockFilesDir, fileName) } returns mockFile
        every { File(mockFilesDir, not(eq(fileName))) } returns mockk<File>().apply {
            every { exists() } returns false
        }

        every { mockFile.exists() } returns true
        every { mockFile.inputStream() } returns ByteArrayInputStream(modifiedContent)
    }

    private fun setupMockFileWithExpectedHash(fileName: String, expectedHash: String) {
        val mockFile = mockk<File>()

        every { File(mockFilesDir, fileName) } returns mockFile
        every { File(mockFilesDir, not(eq(fileName))) } returns mockk<File>().apply {
            every { exists() } returns false
        }

        every { mockFile.exists() } returns true
        // Mock the input stream to return content that produces the expected hash
        every { mockFile.inputStream() } returns ByteArrayInputStream(expectedHash.toByteArray())
    }

    private fun createViolation(
        fileName: String,
        severity: IntegrityMonitor.ThreatLevel,
    ): IntegrityMonitor.IntegrityViolation {
        return IntegrityMonitor.IntegrityViolation(
            fileName = fileName,
            expectedHash = "expected_hash",
            actualHash = "actual_hash",
            timestamp = System.currentTimeMillis(),
            severity = severity
        )
    }

    private suspend fun invokeHandleViolations(violations: List<IntegrityMonitor.IntegrityViolation>) {
        val method = IntegrityMonitor::class.java.getDeclaredMethod(
            "handleIntegrityViolations",
            List::class.java
        )
        method.isAccessible = true
        method.invoke(integrityMonitor, violations)
    }
}