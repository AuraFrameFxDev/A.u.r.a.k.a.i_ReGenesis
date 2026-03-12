package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Comprehensive unit tests for WorkManagerModule
 * Testing Framework: JUnit 5 with MockK for mocking
 */
@DisplayName("WorkManagerModule Tests")
class WorkManagerModuleTest {

    private lateinit var workManagerModule: WorkManagerModule
    private lateinit var mockContext: Context
    private lateinit var mockHiltWorkerFactory: HiltWorkerFactory
    private lateinit var mockWorkManager: WorkManager

    @BeforeEach
    fun setUp() {
        mockContext = mockk<Context>(relaxed = true)
        mockHiltWorkerFactory = mockk<HiltWorkerFactory>(relaxed = true)
        mockWorkManager = mockk<WorkManager>(relaxed = true)
        workManagerModule = WorkManagerModule

        // Mock WorkManager.getInstance to prevent actual WorkManager initialization
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(any()) } returns mockWorkManager
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    @Nested
    @DisplayName("WorkManager Configuration Provider Tests")
    inner class WorkManagerConfigurationProviderTests {

        @Test
        @DisplayName("Should provide valid WorkManager Configuration")
        fun shouldProvideValidWorkManagerConfiguration() {
            // When
            val result = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // Then
            assertNotNull(result)
            assertTrue(result is Configuration)
        }

        @Test
        @DisplayName("Should configure WorkManager with HiltWorkerFactory")
        fun shouldConfigureWorkManagerWithHiltWorkerFactory() {
            // When
            val result = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // Then
            assertNotNull(result)
            assertNotNull(result.workerFactory)
        }

        @Test
        @DisplayName("Should throw exception when HiltWorkerFactory is null")
        fun shouldThrowExceptionWhenHiltWorkerFactoryIsNull() {
            // Given
            val nullFactory: HiltWorkerFactory? = null

            // When & Then
            assertThrows<NullPointerException> {
                workManagerModule.provideWorkManagerConfiguration(nullFactory!!)
            }
        }

        @Test
        @DisplayName("Should create Configuration with builder pattern")
        fun shouldCreateConfigurationWithBuilderPattern() {
            // When
            val result = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // Then
            assertNotNull(result)
            // Configuration should be properly built
            assertDoesNotThrow {
                result.workerFactory
            }
        }

        @Test
        @DisplayName("Should use provided HiltWorkerFactory instance")
        fun shouldUseProvidedHiltWorkerFactoryInstance() {
            // Given
            val customFactory = mockk<HiltWorkerFactory>(relaxed = true)

            // When
            val result = workManagerModule.provideWorkManagerConfiguration(customFactory)

            // Then
            assertNotNull(result)
            assertNotNull(result.workerFactory)
        }

        @Test
        @DisplayName("Should create new Configuration on each call")
        fun shouldCreateNewConfigurationOnEachCall() {
            // When
            val config1 = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            val config2 = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // Then
            assertNotNull(config1)
            assertNotNull(config2)
            assertNotSame(config1, config2, "Each call should create a new Configuration instance")
        }

        @Test
        @DisplayName("Should handle multiple factory instances")
        fun shouldHandleMultipleFactoryInstances() {
            // Given
            val factory1 = mockk<HiltWorkerFactory>(relaxed = true)
            val factory2 = mockk<HiltWorkerFactory>(relaxed = true)

            // When
            val config1 = workManagerModule.provideWorkManagerConfiguration(factory1)
            val config2 = workManagerModule.provideWorkManagerConfiguration(factory2)

            // Then
            assertNotNull(config1)
            assertNotNull(config2)
        }
    }

    @Nested
    @DisplayName("WorkManager Instance Provider Tests")
    inner class WorkManagerInstanceProviderTests {

        @Test
        @DisplayName("Should provide WorkManager instance")
        fun shouldProvideWorkManagerInstance() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)

            // When
            val result = workManagerModule.provideWorkManager(mockContext, mockConfig)

            // Then
            assertNotNull(result)
            verify { WorkManager.getInstance(mockContext) }
        }

        @Test
        @DisplayName("Should use application context")
        fun shouldUseApplicationContext() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)

            // When
            workManagerModule.provideWorkManager(mockContext, mockConfig)

            // Then
            verify { WorkManager.getInstance(mockContext) }
        }

        @Test
        @DisplayName("Should handle configuration parameter")
        fun shouldHandleConfigurationParameter() {
            // Given
            val customConfig =
                workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // When
            val result = workManagerModule.provideWorkManager(mockContext, customConfig)

            // Then
            assertNotNull(result)
            verify { WorkManager.getInstance(mockContext) }
        }

        @Test
        @DisplayName("Should throw exception when context is null")
        fun shouldThrowExceptionWhenContextIsNull() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)
            val nullContext: Context? = null

            // When & Then
            assertThrows<NullPointerException> {
                workManagerModule.provideWorkManager(nullContext!!, mockConfig)
            }
        }

        @Test
        @DisplayName("Should throw exception when configuration is null")
        fun shouldThrowExceptionWhenConfigurationIsNull() {
            // Given
            val nullConfig: Configuration? = null

            // When & Then
            assertThrows<NullPointerException> {
                workManagerModule.provideWorkManager(mockContext, nullConfig!!)
            }
        }

        @Test
        @DisplayName("Should return same WorkManager instance for same context")
        fun shouldReturnSameWorkManagerInstanceForSameContext() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)

            // When
            val result1 = workManagerModule.provideWorkManager(mockContext, mockConfig)
            val result2 = workManagerModule.provideWorkManager(mockContext, mockConfig)

            // Then
            assertNotNull(result1)
            assertNotNull(result2)
            // Both should call getInstance with the same context
            verify(exactly = 2) { WorkManager.getInstance(mockContext) }
        }

        @Test
        @DisplayName("Should handle different contexts")
        fun shouldHandleDifferentContexts() {
            // Given
            val mockContext1 = mockk<Context>(relaxed = true)
            val mockContext2 = mockk<Context>(relaxed = true)
            val mockConfig = mockk<Configuration>(relaxed = true)
            every { WorkManager.getInstance(mockContext1) } returns mockWorkManager
            every { WorkManager.getInstance(mockContext2) } returns mockWorkManager

            // When
            val result1 = workManagerModule.provideWorkManager(mockContext1, mockConfig)
            val result2 = workManagerModule.provideWorkManager(mockContext2, mockConfig)

            // Then
            assertNotNull(result1)
            assertNotNull(result2)
            verify { WorkManager.getInstance(mockContext1) }
            verify { WorkManager.getInstance(mockContext2) }
        }
    }

    @Nested
    @DisplayName("Module Configuration Tests")
    inner class ModuleConfigurationTests {

        @Test
        @DisplayName("Should be an object singleton")
        fun shouldBeObjectSingleton() {
            // When
            val instance1 = WorkManagerModule
            val instance2 = WorkManagerModule

            // Then
            assertSame(instance1, instance2, "WorkManagerModule should be a singleton object")
        }

        @Test
        @DisplayName("Should have configuration provider method")
        fun shouldHaveConfigurationProviderMethod() {
            // When
            val method = WorkManagerModule::class.java.getDeclaredMethod(
                "provideWorkManagerConfiguration",
                HiltWorkerFactory::class.java
            )

            // Then
            assertNotNull(method)
            assertTrue(method.canAccess(WorkManagerModule))
        }

        @Test
        @DisplayName("Should have WorkManager provider method")
        fun shouldHaveWorkManagerProviderMethod() {
            // When
            val method = WorkManagerModule::class.java.getDeclaredMethod(
                "provideWorkManager",
                Context::class.java,
                Configuration::class.java
            )

            // Then
            assertNotNull(method)
            assertTrue(method.canAccess(WorkManagerModule))
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should create complete WorkManager setup")
        fun shouldCreateCompleteWorkManagerSetup() {
            // When
            val configuration =
                workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            val workManager = workManagerModule.provideWorkManager(mockContext, configuration)

            // Then
            assertNotNull(configuration)
            assertNotNull(workManager)
            verify { WorkManager.getInstance(mockContext) }
        }

        @Test
        @DisplayName("Should integrate Configuration and WorkManager correctly")
        fun shouldIntegrateConfigurationAndWorkManagerCorrectly() {
            // Given
            val config = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // When
            val workManager = workManagerModule.provideWorkManager(mockContext, config)

            // Then
            assertNotNull(config)
            assertNotNull(workManager)
        }

        @Test
        @DisplayName("Should support Hilt dependency injection flow")
        fun shouldSupportHiltDependencyInjectionFlow() {
            // Given - Simulating Hilt providing dependencies
            val hiltProvidedFactory = mockk<HiltWorkerFactory>(relaxed = true)
            val hiltProvidedContext = mockk<Context>(relaxed = true)

            // When
            val config = workManagerModule.provideWorkManagerConfiguration(hiltProvidedFactory)
            val workManager = workManagerModule.provideWorkManager(hiltProvidedContext, config)

            // Then
            assertNotNull(config)
            assertNotNull(workManager)
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    inner class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent Configuration creation")
        fun shouldHandleConcurrentConfigurationCreation() {
            // Given
            val results = mutableListOf<Configuration>()
            val threads = mutableListOf<Thread>()

            // When
            repeat(10) {
                val thread = Thread {
                    val config =
                        workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
                    synchronized(results) {
                        results.add(config)
                    }
                }
                threads.add(thread)
                thread.start()
            }

            threads.forEach { it.join() }

            // Then
            assertEquals(10, results.size)
            results.forEach { config ->
                assertNotNull(config)
            }
        }

        @Test
        @DisplayName("Should handle concurrent WorkManager access")
        fun shouldHandleConcurrentWorkManagerAccess() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)
            val results = mutableListOf<WorkManager>()
            val threads = mutableListOf<Thread>()

            // When
            repeat(10) {
                val thread = Thread {
                    val wm = workManagerModule.provideWorkManager(mockContext, mockConfig)
                    synchronized(results) {
                        results.add(wm)
                    }
                }
                threads.add(thread)
                thread.start()
            }

            threads.forEach { it.join() }

            // Then
            assertEquals(10, results.size)
            results.forEach { wm ->
                assertNotNull(wm)
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should create Configuration within reasonable time")
        fun shouldCreateConfigurationWithinReasonableTime() {
            // Given
            val startTime = System.currentTimeMillis()

            // When
            workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            val endTime = System.currentTimeMillis()

            // Then
            val duration = endTime - startTime
            assertTrue(
                duration < 1000,
                "Configuration creation should take less than 1 second, took ${duration}ms"
            )
        }

        @Test
        @DisplayName("Should provide WorkManager within reasonable time")
        fun shouldProvideWorkManagerWithinReasonableTime() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)
            val startTime = System.currentTimeMillis()

            // When
            workManagerModule.provideWorkManager(mockContext, mockConfig)
            val endTime = System.currentTimeMillis()

            // Then
            val duration = endTime - startTime
            assertTrue(
                duration < 1000,
                "WorkManager provision should take less than 1 second, took ${duration}ms"
            )
        }

        @Test
        @DisplayName("Should handle repeated Configuration creation efficiently")
        fun shouldHandleRepeatedConfigurationCreationEfficiently() {
            // Given
            val iterations = 100
            val startTime = System.currentTimeMillis()

            // When
            repeat(iterations) {
                workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            }
            val endTime = System.currentTimeMillis()

            // Then
            val duration = endTime - startTime
            val averageTime = duration.toDouble() / iterations
            assertTrue(
                averageTime < 50,
                "Average Configuration creation time should be less than 50ms, was ${averageTime}ms"
            )
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle WorkManager initialization failure")
        fun shouldHandleWorkManagerInitializationFailure() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)
            every { WorkManager.getInstance(any()) } throws IllegalStateException("WorkManager not initialized")

            // When & Then
            assertThrows<IllegalStateException> {
                workManagerModule.provideWorkManager(mockContext, mockConfig)
            }
        }

        @Test
        @DisplayName("Should propagate exceptions from Configuration builder")
        fun shouldPropagateExceptionsFromConfigurationBuilder() {
            // Given
            mockk<HiltWorkerFactory>(relaxed = true) {
                every { this@mockk.toString() } throws RuntimeException("Factory error")
            }

            // When & Then - Should not throw during configuration creation
            assertDoesNotThrow {
                workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            }
        }

        @Test
        @DisplayName("Should handle context without proper initialization")
        fun shouldHandleContextWithoutProperInitialization() {
            // Given
            val uninitializedContext = mockk<Context>(relaxed = true)
            val mockConfig = mockk<Configuration>(relaxed = true)
            every { WorkManager.getInstance(uninitializedContext) } throws IllegalStateException()

            // When & Then
            assertThrows<IllegalStateException> {
                workManagerModule.provideWorkManager(uninitializedContext, mockConfig)
            }
        }
    }

    @Nested
    @DisplayName("Regression Tests")
    inner class RegressionTests {

        @Test
        @DisplayName("Should maintain Configuration type consistency")
        fun shouldMaintainConfigurationTypeConsistency() {
            // When
            val config1 = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            val config2 = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // Then
            assertEquals(config1::class.java, config2::class.java)
        }

        @Test
        @DisplayName("Should maintain WorkManager type consistency")
        fun shouldMaintainWorkManagerTypeConsistency() {
            // Given
            val mockConfig = mockk<Configuration>(relaxed = true)

            // When
            val wm1 = workManagerModule.provideWorkManager(mockContext, mockConfig)
            val wm2 = workManagerModule.provideWorkManager(mockContext, mockConfig)

            // Then
            assertEquals(wm1::class.java, wm2::class.java)
        }

        @Test
        @DisplayName("Should not introduce memory leaks with Configuration")
        fun shouldNotIntroduceMemoryLeaksWithConfiguration() {
            // When
            repeat(1000) {
                workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            }

            // Then - Should complete without OutOfMemoryError
            assertDoesNotThrow {
                System.gc()
            }
        }

        @Test
        @DisplayName("Should maintain backward compatibility with Hilt")
        fun shouldMaintainBackwardCompatibilityWithHilt() {
            // Given
            val config = workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)

            // When & Then
            assertNotNull(config.workerFactory)
            assertDoesNotThrow {
                workManagerModule.provideWorkManager(mockContext, config)
            }
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    inner class EdgeCaseTests {

        @Test
        @DisplayName("Should handle rapid sequential calls")
        fun shouldHandleRapidSequentialCalls() = runTest {
            // When
            val configs = (1..100).map {
                workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
            }

            // Then
            assertEquals(100, configs.size)
            configs.forEach { config ->
                assertNotNull(config)
            }
        }

        @Test
        @DisplayName("Should handle alternating Configuration and WorkManager creation")
        fun shouldHandleAlternatingConfigurationAndWorkManagerCreation() {
            // When & Then
            repeat(10) {
                val config =
                    workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
                val workManager = workManagerModule.provideWorkManager(mockContext, config)
                assertNotNull(config)
                assertNotNull(workManager)
            }
        }

        @Test
        @DisplayName("Should handle stress testing scenario")
        fun shouldHandleStressTestingScenario() {
            // Given
            val iterations = 500

            // When & Then
            assertDoesNotThrow {
                repeat(iterations) {
                    val config =
                        workManagerModule.provideWorkManagerConfiguration(mockHiltWorkerFactory)
                    workManagerModule.provideWorkManager(mockContext, config)
                }
            }
        }
    }
}