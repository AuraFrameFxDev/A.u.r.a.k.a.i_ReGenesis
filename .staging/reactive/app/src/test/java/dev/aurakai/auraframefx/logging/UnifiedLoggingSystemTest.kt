package dev.aurakai.auraframefx.logging

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import timber.log.Timber
import java.io.File

/**
 * Comprehensive unit tests for UnifiedLoggingSystem
 * Testing Framework: JUnit 5 with Mockito and Kotlin Coroutines Test
 */
@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnifiedLoggingSystemTest {

    @TempDir
    lateinit var tempDir: File

    @Mock
    private lateinit var mockContext: Context

    private lateinit var unifiedLoggingSystem: UnifiedLoggingSystem
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeAll
    fun setupAll() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @BeforeEach
    fun setup() {
        whenever(mockContext.filesDir).thenReturn(tempDir)
        unifiedLoggingSystem = UnifiedLoggingSystem(mockContext)
    }

    @AfterEach
    fun tearDown() {
        try {
            unifiedLoggingSystem.shutdown()
        } catch (e: Exception) {
            // Ignore shutdown errors in tests
        }
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("Initialization Tests")
    inner class InitializationTests {

        @Test
        fun `initialize should create log directory when it doesn't exist`() {
            // Given
            val logDir = File(tempDir, "aura_logs")
            assertFalse(logDir.exists())

            // When
            unifiedLoggingSystem.initialize()

            // Then
            assertTrue(logDir.exists())
            assertTrue(logDir.isDirectory)
        }

        @Test
        fun `initialize should not fail when log directory already exists`() {
            // Given
            val logDir = File(tempDir, "aura_logs")
            logDir.mkdirs()
            assertTrue(logDir.exists())

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.initialize()
            }
            assertTrue(logDir.exists())
        }

        @Test
        fun `initialize should handle exceptions gracefully`() {
            // Given
            whenever(mockContext.filesDir).thenThrow(RuntimeException("File system error"))

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.initialize()
            }
        }
    }

    @Nested
    @DisplayName("Basic Logging Tests")
    inner class BasicLoggingTests {

        @BeforeEach
        fun setupLogging() {
            unifiedLoggingSystem.initialize()
        }

        @Test
        fun `verbose logging should complete successfully`() {
            // Given
            val category = UnifiedLoggingSystem.LogCategory.UI
            val tag = "VerboseTag"
            val message = "Verbose message"

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.verbose(category, tag, message)
            }
        }

        @Test
        fun `debug logging should complete successfully`() {
            // Given
            val category = UnifiedLoggingSystem.LogCategory.NETWORK
            val tag = "DebugTag"
            val message = "Debug message"
            val metadata = mapOf("userId" to 123)

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.debug(category, tag, message, metadata)
            }
        }

        @Test
        fun `info logging should complete successfully`() {
            // Given
            val category = UnifiedLoggingSystem.LogCategory.STORAGE
            val tag = "InfoTag"
            val message = "Info message"

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.info(category, tag, message)
            }
        }

        @Test
        fun `warning logging with throwable should complete successfully`() {
            // Given
            val category = UnifiedLoggingSystem.LogCategory.PERFORMANCE
            val tag = "WarnTag"
            val message = "Warning message"
            val exception = IllegalStateException("Warning exception")
            val metadata = mapOf("performance" to "slow")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.warning(category, tag, message, exception, metadata)
            }
        }

        @Test
        fun `error logging should complete successfully`() {
            // Given
            val category = UnifiedLoggingSystem.LogCategory.SYSTEM
            val tag = "ErrorTag"
            val message = "Error message"
            val exception = Exception("Test error")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.error(category, tag, message, exception)
            }
        }

        @Test
        fun `fatal logging should complete successfully`() {
            // Given
            val category = UnifiedLoggingSystem.LogCategory.SECURITY
            val tag = "FatalTag"
            val message = "Fatal message"
            val exception = RuntimeException("Fatal error")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.fatal(category, tag, message, exception)
            }
        }
    }

    @Nested
    @DisplayName("Specialized Logging Method Tests")
    inner class SpecializedLoggingTests {

        @BeforeEach
        fun setupLogging() {
            unifiedLoggingSystem.initialize()
        }

        @Test
        fun `logSecurityEvent should use default WARNING level`() {
            // Given
            val event = "Unauthorized access attempt"
            val details = mapOf("ip" to "192.168.1.1", "user" to "attacker")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logSecurityEvent(event, details = details)
            }
        }

        @Test
        fun `logSecurityEvent should accept custom severity level`() {
            // Given
            val event = "Critical security breach"
            val severity = UnifiedLoggingSystem.LogLevel.FATAL

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logSecurityEvent(event, severity)
            }
        }

        @Test
        fun `logPerformanceMetric should accept custom unit`() {
            // Given
            val metric = "Response time"
            val value = 250.5
            val unit = "ms"

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logPerformanceMetric(metric, value, unit)
            }
        }

        @Test
        fun `logPerformanceMetric should use default unit ms`() {
            // Given
            val metric = "Database query time"
            val value = 150.0

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logPerformanceMetric(metric, value)
            }
        }

        @Test
        fun `logUserAction should complete successfully`() {
            // Given
            val action = "Button clicked"
            val details = mapOf("buttonId" to "submit", "screen" to "login")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logUserAction(action, details)
            }
        }

        @Test
        fun `logAIEvent should include confidence when provided`() {
            // Given
            val agent = "ChatBot"
            val event = "Response generated"
            val confidence = 0.95f
            val details = mapOf("responseTime" to 500)

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logAIEvent(agent, event, confidence, details)
            }
        }

        @Test
        fun `logAIEvent should work without confidence score`() {
            // Given
            val agent = "VisionAI"
            val event = "Image processed"

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logAIEvent(agent, event)
            }
        }

        @Test
        fun `logGenesisProtocol should use default INFO level`() {
            // Given
            val event = "Protocol handshake initiated"
            val details = mapOf("version" to "1.0", "peer" to "node-123")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logGenesisProtocol(event, details = details)
            }
        }

        @Test
        fun `logGenesisProtocol should accept custom log level`() {
            // Given
            val event = "Protocol error detected"
            val level = UnifiedLoggingSystem.LogLevel.ERROR

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.logGenesisProtocol(event, level)
            }
        }
    }

    @Nested
    @DisplayName("System Health Tests")
    inner class SystemHealthTests {

        @BeforeEach
        fun setupLogging() {
            unifiedLoggingSystem.initialize()
        }

        @Test
        fun `system health should start as HEALTHY`() {
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.HEALTHY,
                unifiedLoggingSystem.systemHealth.value
            )
        }

        @Test
        fun `FATAL log should set system health to CRITICAL`() = runTest {
            // Given
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.HEALTHY,
                unifiedLoggingSystem.systemHealth.value
            )

            // When
            unifiedLoggingSystem.fatal(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Fatal error"
            )
            delay(100) // Allow processing

            // Then
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.CRITICAL,
                unifiedLoggingSystem.systemHealth.value
            )
        }

        @Test
        fun `ERROR log should set system health to ERROR when currently HEALTHY`() = runTest {
            // Given
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.HEALTHY,
                unifiedLoggingSystem.systemHealth.value
            )

            // When
            unifiedLoggingSystem.error(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Error occurred"
            )
            delay(100) // Allow processing

            // Then
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.ERROR,
                unifiedLoggingSystem.systemHealth.value
            )
        }

        @Test
        fun `WARNING log should set system health to WARNING when currently HEALTHY`() = runTest {
            // Given
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.HEALTHY,
                unifiedLoggingSystem.systemHealth.value
            )

            // When
            unifiedLoggingSystem.warning(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Warning occurred"
            )
            delay(100) // Allow processing

            // Then
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.WARNING,
                unifiedLoggingSystem.systemHealth.value
            )
        }

        @Test
        fun `WARNING log should not override ERROR system health`() = runTest {
            // Given - Set to ERROR first
            unifiedLoggingSystem.error(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Error occurred"
            )
            delay(100)
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.ERROR,
                unifiedLoggingSystem.systemHealth.value
            )

            // When
            unifiedLoggingSystem.warning(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Warning occurred"
            )
            delay(100) // Allow processing

            // Then
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.ERROR,
                unifiedLoggingSystem.systemHealth.value
            )
        }

        @Test
        fun `INFO and DEBUG logs should not affect system health`() = runTest {
            // Given
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.HEALTHY,
                unifiedLoggingSystem.systemHealth.value
            )

            // When
            unifiedLoggingSystem.info(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Info message"
            )
            unifiedLoggingSystem.debug(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Debug message"
            )
            delay(100) // Allow processing

            // Then
            assertEquals(
                UnifiedLoggingSystem.SystemHealth.HEALTHY,
                unifiedLoggingSystem.systemHealth.value
            )
        }
    }

    @Nested
    @DisplayName("File Operations Tests")
    inner class FileOperationsTests {

        @BeforeEach
        fun setupLogging() {
            unifiedLoggingSystem.initialize()
        }

        @Test
        fun `log files should be created in correct directory with date format`() = runTest {
            // Given
            val logDir = File(tempDir, "aura_logs")

            // When
            unifiedLoggingSystem.info(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Test",
                "Test message"
            )
            delay(200) // Allow file writing

            // Then
            assertTrue(logDir.exists())
            val logFiles =
                logDir.listFiles { _, name -> name.startsWith("aura_log_") && name.endsWith(".log") }
            assertNotNull(logFiles)
            assertTrue(logFiles!!.isNotEmpty())
        }

        @Test
        fun `log file should contain formatted log entry`() = runTest {
            // Given
            val logDir = File(tempDir, "aura_logs")
            val testMessage = "Unique test message ${System.currentTimeMillis()}"

            // When
            unifiedLoggingSystem.info(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "TestTag",
                testMessage
            )
            delay(200) // Allow file writing

            // Then
            val logFiles =
                logDir.listFiles { _, name -> name.startsWith("aura_log_") && name.endsWith(".log") }
            assertNotNull(logFiles)
            assertTrue(logFiles!!.isNotEmpty())

            val content = logFiles[0].readText()
            assertTrue(content.contains(testMessage))
            assertTrue(content.contains("[INFO]"))
            assertTrue(content.contains("[SYSTEM]"))
            assertTrue(content.contains("[TestTag]"))
        }

        @Test
        fun `log entry with metadata should include metadata in file`() = runTest {
            // Given
            val logDir = File(tempDir, "aura_logs")
            val metadata = mapOf("userId" to 123, "action" to "login")

            // When
            unifiedLoggingSystem.info(
                UnifiedLoggingSystem.LogCategory.USER_ACTION,
                "Auth",
                "User logged in",
                metadata
            )
            delay(200) // Allow file writing

            // Then
            val logFiles =
                logDir.listFiles { _, name -> name.startsWith("aura_log_") && name.endsWith(".log") }
            val content = logFiles!![0].readText()
            assertTrue(content.contains("userId=123"))
            assertTrue(content.contains("action=login"))
        }

        @Test
        fun `log entry with throwable should include exception info`() = runTest {
            // Given
            val logDir = File(tempDir, "aura_logs")
            val exception = RuntimeException("Test exception")

            // When
            unifiedLoggingSystem.error(
                UnifiedLoggingSystem.LogCategory.SYSTEM,
                "Error",
                "Something failed",
                exception
            )
            delay(200) // Allow file writing

            // Then
            val logFiles =
                logDir.listFiles { _, name -> name.startsWith("aura_log_") && name.endsWith(".log") }
            val content = logFiles!![0].readText()
            assertTrue(content.contains("Exception: RuntimeException: Test exception"))
        }
    }

    @Nested
    @DisplayName("Session Management Tests")
    inner class SessionManagementTests {

        @Test
        fun `LogEntry should include session ID`() {
            // Given
            val expectedSessionIdPrefix = "session_"

            // When
            val logEntry = UnifiedLoggingSystem.LogEntry(
                timestamp = System.currentTimeMillis(),
                level = UnifiedLoggingSystem.LogLevel.INFO,
                category = UnifiedLoggingSystem.LogCategory.SYSTEM,
                tag = "Test",
                message = "Test message"
            )

            // Then
            assertTrue(logEntry.sessionId.startsWith(expectedSessionIdPrefix))
            assertTrue(logEntry.sessionId.length > expectedSessionIdPrefix.length)
        }

        @Test
        fun `session ID should be hour-based`() {
            // Given
            val currentHour = System.currentTimeMillis() / 1000 / 3600
            val expectedSessionId = "session_$currentHour"

            // When
            val logEntry = UnifiedLoggingSystem.LogEntry(
                timestamp = System.currentTimeMillis(),
                level = UnifiedLoggingSystem.LogLevel.INFO,
                category = UnifiedLoggingSystem.LogCategory.SYSTEM,
                tag = "Test",
                message = "Test message"
            )

            // Then
            assertEquals(expectedSessionId, logEntry.sessionId)
        }
    }

    @Nested
    @DisplayName("Shutdown Tests")
    inner class ShutdownTests {

        @Test
        fun `shutdown should be safe to call multiple times`() {
            // Given
            unifiedLoggingSystem.initialize()

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.shutdown()
                unifiedLoggingSystem.shutdown()
            }
        }

        @Test
        fun `shutdown should complete without throwing exceptions`() {
            // Given
            unifiedLoggingSystem.initialize()

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.shutdown()
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    inner class EdgeCasesTests {

        @BeforeEach
        fun setupLogging() {
            unifiedLoggingSystem.initialize()
        }

        @Test
        fun `logging should handle empty tag gracefully`() {
            assertDoesNotThrow {
                unifiedLoggingSystem.info(
                    UnifiedLoggingSystem.LogCategory.SYSTEM,
                    "",
                    "Message with empty tag"
                )
            }
        }

        @Test
        fun `logging should handle empty message`() {
            assertDoesNotThrow {
                unifiedLoggingSystem.info(UnifiedLoggingSystem.LogCategory.SYSTEM, "Tag", "")
            }
        }

        @Test
        fun `logging should handle very long messages`() {
            // Given
            val longMessage = "A".repeat(10000)

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.info(
                    UnifiedLoggingSystem.LogCategory.SYSTEM,
                    "Tag",
                    longMessage
                )
            }
        }

        @Test
        fun `logging should handle large metadata maps`() {
            // Given
            val largeMetadata = (1..100).associate { "key$it" to "value$it" }

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.info(
                    UnifiedLoggingSystem.LogCategory.SYSTEM,
                    "Tag",
                    "Message",
                    largeMetadata
                )
            }
        }

        @Test
        fun `logging should handle special characters in messages`() {
            // Given
            val specialMessage = "Message with special chars: ñáéíóú@#\$%^&*()[]{}|\\:;\"'<>,.?/~`"

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.info(
                    UnifiedLoggingSystem.LogCategory.SYSTEM,
                    "Tag",
                    specialMessage
                )
            }
        }

        @Test
        fun `logging should handle metadata with null values`() {
            // Given
            val metadataWithNull = mapOf("key1" to "value1", "key2" to null, "key3" to "value3")

            // When & Then
            assertDoesNotThrow {
                unifiedLoggingSystem.info(
                    UnifiedLoggingSystem.LogCategory.SYSTEM,
                    "Tag",
                    "Message",
                    metadataWithNull
                )
            }
        }
    }

    @Nested
    @DisplayName("Log Analytics Tests")
    inner class LogAnalyticsTests {

        @Test
        fun `LogAnalytics data class should have correct properties`() {
            // Given
            val analytics = UnifiedLoggingSystem.LogAnalytics(
                totalLogs = 1500L,
                errorCount = 10L,
                warningCount = 25L,
                performanceIssues = 3L,
                securityEvents = 1L,
                averageResponseTime = 200.5,
                systemHealthScore = 0.88f
            )

            // Then
            assertEquals(1500L, analytics.totalLogs)
            assertEquals(10L, analytics.errorCount)
            assertEquals(25L, analytics.warningCount)
            assertEquals(3L, analytics.performanceIssues)
            assertEquals(1L, analytics.securityEvents)
            assertEquals(200.5, analytics.averageResponseTime)
            assertEquals(0.88f, analytics.systemHealthScore)
        }

        @Test
        fun `LogAnalytics should support zero values`() {
            // Given
            val analytics = UnifiedLoggingSystem.LogAnalytics(
                totalLogs = 0L,
                errorCount = 0L,
                warningCount = 0L,
                performanceIssues = 0L,
                securityEvents = 0L,
                averageResponseTime = 0.0,
                systemHealthScore = 1.0f
            )

            // Then
            assertEquals(0L, analytics.totalLogs)
            assertEquals(0L, analytics.errorCount)
            assertEquals(1.0f, analytics.systemHealthScore)
        }
    }

    @Nested
    @DisplayName("AuraFxLoggerCompat Tests")
    inner class AuraFxLoggerCompatTests {

        @BeforeEach
        fun setupCompat() {
            unifiedLoggingSystem.initialize()
            AuraFxLoggerCompat.initialize(unifiedLoggingSystem)
        }

        @Test
        fun `AuraFxLoggerCompat d should delegate to debug`() {
            assertDoesNotThrow {
                AuraFxLoggerCompat.d("TestTag", "Debug message")
            }
        }

        @Test
        fun `AuraFxLoggerCompat i should delegate to info`() {
            assertDoesNotThrow {
                AuraFxLoggerCompat.i("TestTag", "Info message")
            }
        }

        @Test
        fun `AuraFxLoggerCompat w should delegate to warning`() {
            assertDoesNotThrow {
                AuraFxLoggerCompat.w("TestTag", "Warning message")
            }
        }

        @Test
        fun `AuraFxLoggerCompat e should delegate to error`() {
            // Given
            val exception = RuntimeException("Test error")

            // When & Then
            assertDoesNotThrow {
                AuraFxLoggerCompat.e("TestTag", "Error message", exception)
            }
        }

        @Test
        fun `AuraFxLoggerCompat should handle null tags`() {
            assertDoesNotThrow {
                AuraFxLoggerCompat.d(null, "Debug message")
            }
        }

        @Test
        fun `AuraFxLoggerCompat should handle uninitialized logger gracefully`() {
            // Given - Create new compat instance without initialization
            UnifiedLoggingSystem(mockContext)

            // When & Then - Should not throw
            assertDoesNotThrow {
                AuraFxLoggerCompat.d("Tag", "Message")
                AuraFxLoggerCompat.i("Tag", "Message")
                AuraFxLoggerCompat.w("Tag", "Message")
                AuraFxLoggerCompat.e("Tag", "Message")
            }
        }
    }

    @Nested
    @DisplayName("Enum Validation Tests")
    inner class EnumValidationTests {

        @Test
        fun `SystemHealth enum should have all expected values`() {
            val healthValues = UnifiedLoggingSystem.SystemHealth.values()
            assertEquals(4, healthValues.size)
            assertTrue(healthValues.contains(UnifiedLoggingSystem.SystemHealth.HEALTHY))
            assertTrue(healthValues.contains(UnifiedLoggingSystem.SystemHealth.WARNING))
            assertTrue(healthValues.contains(UnifiedLoggingSystem.SystemHealth.ERROR))
            assertTrue(healthValues.contains(UnifiedLoggingSystem.SystemHealth.CRITICAL))
        }

        @Test
        fun `LogLevel enum should have all expected values`() {
            val levelValues = UnifiedLoggingSystem.LogLevel.values()
            assertEquals(6, levelValues.size)
            assertTrue(levelValues.contains(UnifiedLoggingSystem.LogLevel.VERBOSE))
            assertTrue(levelValues.contains(UnifiedLoggingSystem.LogLevel.DEBUG))
            assertTrue(levelValues.contains(UnifiedLoggingSystem.LogLevel.INFO))
            assertTrue(levelValues.contains(UnifiedLoggingSystem.LogLevel.WARNING))
            assertTrue(levelValues.contains(UnifiedLoggingSystem.LogLevel.ERROR))
            assertTrue(levelValues.contains(UnifiedLoggingSystem.LogLevel.FATAL))
        }

        @Test
        fun `LogCategory enum should have all expected values`() {
            val categoryValues = UnifiedLoggingSystem.LogCategory.values()
            assertEquals(9, categoryValues.size)
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.SYSTEM))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.SECURITY))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.UI))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.AI))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.NETWORK))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.STORAGE))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.PERFORMANCE))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.USER_ACTION))
            assertTrue(categoryValues.contains(UnifiedLoggingSystem.LogCategory.GENESIS_PROTOCOL))
        }
    }

    @Nested
    @DisplayName("Concurrent Access Tests")
    inner class ConcurrentAccessTests {

        @BeforeEach
        fun setupLogging() {
            unifiedLoggingSystem.initialize()
        }

        @Test
        fun `multiple coroutines should be able to log simultaneously`() = runTest {
            // Given
            val messageCount = 100
            val jobs = mutableListOf<Job>()

            // When
            repeat(messageCount) { i ->
                val job = launch {
                    unifiedLoggingSystem.info(
                        UnifiedLoggingSystem.LogCategory.SYSTEM,
                        "Coroutine$i",
                        "Concurrent message $i"
                    )
                }
                jobs.add(job)
            }

            // Wait for all jobs to complete
            jobs.joinAll()
            delay(200) // Allow processing

            // Then - Should not throw any exceptions
            assertTrue(jobs.all { it.isCompleted })
        }

        @Test
        fun `system health updates should be thread-safe`() = runTest {
            // Given
            val jobs = mutableListOf<Job>()

            // When - Multiple coroutines updating health simultaneously
            repeat(50) { i ->
                val job = launch {
                    when (i % 3) {
                        0 -> unifiedLoggingSystem.warning(
                            UnifiedLoggingSystem.LogCategory.SYSTEM,
                            "Test",
                            "Warning $i"
                        )

                        1 -> unifiedLoggingSystem.error(
                            UnifiedLoggingSystem.LogCategory.SYSTEM,
                            "Test",
                            "Error $i"
                        )

                        2 -> unifiedLoggingSystem.fatal(
                            UnifiedLoggingSystem.LogCategory.SYSTEM,
                            "Test",
                            "Fatal $i"
                        )
                    }
                }
                jobs.add(job)
            }

            jobs.joinAll()
            delay(300) // Allow processing

            // Then - System health should be in a valid state
            val finalHealth = unifiedLoggingSystem.systemHealth.value
            assertTrue(
                finalHealth in listOf(
                    UnifiedLoggingSystem.SystemHealth.WARNING,
                    UnifiedLoggingSystem.SystemHealth.ERROR,
                    UnifiedLoggingSystem.SystemHealth.CRITICAL
                )
            )
        }

        @Test
        fun `logging with metadata should be thread-safe`() = runTest {
            // Given
            val jobs = mutableListOf<Job>()

            // When
            repeat(30) { i ->
                val job = launch {
                    val metadata = mapOf("thread" to i, "timestamp" to System.currentTimeMillis())
                    unifiedLoggingSystem.info(
                        UnifiedLoggingSystem.LogCategory.PERFORMANCE,
                        "ThreadTest",
                        "Metadata test $i",
                        metadata
                    )
                }
                jobs.add(job)
            }

            jobs.joinAll()
            delay(200) // Allow processing

            // Then - Should complete without exceptions
            assertTrue(jobs.all { it.isCompleted })
        }
    }

    @Nested
    @DisplayName("LogEntry Data Class Tests")
    inner class LogEntryTests {

        @Test
        fun `LogEntry should have correct default values`() {
            // Given
            val timestamp = System.currentTimeMillis()

            // When
            val logEntry = UnifiedLoggingSystem.LogEntry(
                timestamp = timestamp,
                level = UnifiedLoggingSystem.LogLevel.INFO,
                category = UnifiedLoggingSystem.LogCategory.SYSTEM,
                tag = "TestTag",
                message = "Test message"
            )

            // Then
            assertEquals(timestamp, logEntry.timestamp)
            assertEquals(UnifiedLoggingSystem.LogLevel.INFO, logEntry.level)
            assertEquals(UnifiedLoggingSystem.LogCategory.SYSTEM, logEntry.category)
            assertEquals("TestTag", logEntry.tag)
            assertEquals("Test message", logEntry.message)
            assertNull(logEntry.throwable)
            assertTrue(logEntry.metadata.isEmpty())
            assertNotNull(logEntry.threadName)
            assertNotNull(logEntry.sessionId)
        }

        @Test
        fun `LogEntry should handle all parameters`() {
            // Given
            val timestamp = System.currentTimeMillis()
            val exception = RuntimeException("Test exception")
            val metadata = mapOf("key" to "value")

            // When
            val logEntry = UnifiedLoggingSystem.LogEntry(
                timestamp = timestamp,
                level = UnifiedLoggingSystem.LogLevel.ERROR,
                category = UnifiedLoggingSystem.LogCategory.SECURITY,
                tag = "SecurityTag",
                message = "Security message",
                throwable = exception,
                metadata = metadata,
                threadName = "TestThread",
                sessionId = "test-session"
            )

            // Then
            assertEquals(timestamp, logEntry.timestamp)
            assertEquals(UnifiedLoggingSystem.LogLevel.ERROR, logEntry.level)
            assertEquals(UnifiedLoggingSystem.LogCategory.SECURITY, logEntry.category)
            assertEquals("SecurityTag", logEntry.tag)
            assertEquals("Security message", logEntry.message)
            assertEquals(exception, logEntry.throwable)
            assertEquals(metadata, logEntry.metadata)
            assertEquals("TestThread", logEntry.threadName)
            assertEquals("test-session", logEntry.sessionId)
        }
    }
}
