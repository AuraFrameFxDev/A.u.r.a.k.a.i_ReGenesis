package dev.aurakai.auraframefx.data

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [OfflineDataManager].
 *
 * Since [OfflineDataManager] depends on Android [android.content.Context],
 * we use MockK to supply a minimal mock context that provides a package name.
 *
 * Covers:
 * - loadCriticalOfflineData() returning null (placeholder behavior)
 * - saveCriticalOfflineData() accepting various data types without throwing
 * - exampleMethod() executing without throwing
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("OfflineDataManager Tests")
class OfflineDataManagerTest {

    private lateinit var offlineDataManager: OfflineDataManager
    private val mockContext = mockk<android.content.Context>(relaxed = true)

    @BeforeEach
    fun setUp() {
        every { mockContext.packageName } returns "dev.aurakai.auraframefx.test"
        offlineDataManager = OfflineDataManager(mockContext)
    }

    // =========================================================================
    // loadCriticalOfflineData()
    // =========================================================================

    @Nested
    @DisplayName("loadCriticalOfflineData()")
    inner class LoadCriticalOfflineDataTests {

        @Test
        @DisplayName("should return null (placeholder behavior)")
        fun `should return null placeholder behavior`() {
            val result = offlineDataManager.loadCriticalOfflineData()
            assertNull(result)
        }

        @Test
        @DisplayName("should not throw on invocation")
        fun `should not throw on invocation`() {
            assertDoesNotThrow {
                offlineDataManager.loadCriticalOfflineData()
            }
        }

        @Test
        @DisplayName("should return null on repeated calls")
        fun `should return null on repeated calls`() {
            repeat(5) {
                val result = offlineDataManager.loadCriticalOfflineData()
                assertNull(result)
            }
        }
    }

    // =========================================================================
    // saveCriticalOfflineData()
    // =========================================================================

    @Nested
    @DisplayName("saveCriticalOfflineData()")
    inner class SaveCriticalOfflineDataTests {

        @Test
        @DisplayName("should not throw when saving a String")
        fun `should not throw when saving a String`() {
            assertDoesNotThrow {
                offlineDataManager.saveCriticalOfflineData("test data")
            }
        }

        @Test
        @DisplayName("should not throw when saving an Int")
        fun `should not throw when saving an Int`() {
            assertDoesNotThrow {
                offlineDataManager.saveCriticalOfflineData(42)
            }
        }

        @Test
        @DisplayName("should not throw when saving a List")
        fun `should not throw when saving a List`() {
            assertDoesNotThrow {
                offlineDataManager.saveCriticalOfflineData(listOf("item1", "item2"))
            }
        }

        @Test
        @DisplayName("should not throw when saving a Map")
        fun `should not throw when saving a Map`() {
            assertDoesNotThrow {
                offlineDataManager.saveCriticalOfflineData(mapOf("key" to "value"))
            }
        }

        @Test
        @DisplayName("should not throw when saving an empty string")
        fun `should not throw when saving an empty string`() {
            assertDoesNotThrow {
                offlineDataManager.saveCriticalOfflineData("")
            }
        }

        @Test
        @DisplayName("should not throw when saving a large object")
        fun `should not throw when saving a large object`() {
            val largeData = "x".repeat(100_000)
            assertDoesNotThrow {
                offlineDataManager.saveCriticalOfflineData(largeData)
            }
        }
    }

    // =========================================================================
    // exampleMethod()
    // =========================================================================

    @Nested
    @DisplayName("exampleMethod()")
    inner class ExampleMethodTests {

        @Test
        @DisplayName("should not throw on invocation")
        fun `should not throw on invocation`() {
            assertDoesNotThrow {
                offlineDataManager.exampleMethod()
            }
        }

        @Test
        @DisplayName("should not throw on repeated invocations")
        fun `should not throw on repeated invocations`() {
            assertDoesNotThrow {
                repeat(10) {
                    offlineDataManager.exampleMethod()
                }
            }
        }
    }
}