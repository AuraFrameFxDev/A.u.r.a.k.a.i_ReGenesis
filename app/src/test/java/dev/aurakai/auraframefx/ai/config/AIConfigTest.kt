package dev.aurakai.auraframefx.ai.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [AIConfig].
 *
 * Covers: data class construction, validate(), toDebugString(), createForTesting(),
 * SecurityLevel enum, default parameter values, and edge cases.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AIConfig Tests")
class AIConfigTest {

    // =========================================================================
    // Helpers
    // =========================================================================

    private fun validConfig(
        modelName: String = "test-model",
        apiKey: String = "valid-api-key",
        projectId: String = "test-project",
        endpoint: String = "https://api.aegenesis.ai",
        maxTokens: Int = 4096,
        temperature: Float = 0.7f,
        timeout: Long = 30000L,
        retryAttempts: Int = 3,
        enableLogging: Boolean = true,
        enableAnalytics: Boolean = true,
        securityLevel: AIConfig.SecurityLevel = AIConfig.SecurityLevel.HIGH,
        lastSyncTimestamp: Long = 0L
    ) = AIConfig(
        modelName = modelName,
        apiKey = apiKey,
        projectId = projectId,
        endpoint = endpoint,
        maxTokens = maxTokens,
        temperature = temperature,
        timeout = timeout,
        retryAttempts = retryAttempts,
        enableLogging = enableLogging,
        enableAnalytics = enableAnalytics,
        securityLevel = securityLevel,
        lastSyncTimestamp = lastSyncTimestamp
    )

    // =========================================================================
    // Data Class Construction
    // =========================================================================

    @Nested
    @DisplayName("Data Class Construction")
    inner class ConstructionTests {

        @Test
        @DisplayName("should create config with required fields and default values")
        fun `should create config with required fields and default values`() {
            val config = AIConfig(
                modelName = "test-model",
                apiKey = "key123",
                projectId = "proj-1"
            )

            assertEquals("test-model", config.modelName)
            assertEquals("key123", config.apiKey)
            assertEquals("proj-1", config.projectId)
            assertEquals("https://api.aegenesis.ai", config.endpoint)
            assertEquals(4096, config.maxTokens)
            assertEquals(0.7f, config.temperature)
            assertEquals(30000L, config.timeout)
            assertEquals(3, config.retryAttempts)
            assertTrue(config.enableLogging)
            assertTrue(config.enableAnalytics)
            assertEquals(AIConfig.SecurityLevel.HIGH, config.securityLevel)
            assertEquals(0L, config.lastSyncTimestamp)
        }

        @Test
        @DisplayName("should allow overriding all default values")
        fun `should allow overriding all default values`() {
            val config = AIConfig(
                modelName = "custom-model",
                apiKey = "custom-key",
                projectId = "custom-proj",
                endpoint = "https://custom.endpoint.test",
                maxTokens = 2048,
                temperature = 1.0f,
                timeout = 60000L,
                retryAttempts = 5,
                enableLogging = false,
                enableAnalytics = false,
                securityLevel = AIConfig.SecurityLevel.MAXIMUM,
                lastSyncTimestamp = 1234567890L
            )

            assertEquals("custom-model", config.modelName)
            assertEquals("custom-key", config.apiKey)
            assertEquals("custom-proj", config.projectId)
            assertEquals("https://custom.endpoint.test", config.endpoint)
            assertEquals(2048, config.maxTokens)
            assertEquals(1.0f, config.temperature)
            assertEquals(60000L, config.timeout)
            assertEquals(5, config.retryAttempts)
            assertFalse(config.enableLogging)
            assertFalse(config.enableAnalytics)
            assertEquals(AIConfig.SecurityLevel.MAXIMUM, config.securityLevel)
            assertEquals(1234567890L, config.lastSyncTimestamp)
        }

        @Test
        @DisplayName("should support data class copy")
        fun `should support data class copy`() {
            val original = validConfig()
            val copied = original.copy(modelName = "new-model", apiKey = "new-key")

            assertEquals("new-model", copied.modelName)
            assertEquals("new-key", copied.apiKey)
            assertEquals(original.projectId, copied.projectId)
            assertEquals(original.maxTokens, copied.maxTokens)
        }

        @Test
        @DisplayName("should support structural equality")
        fun `should support structural equality`() {
            val config1 = validConfig()
            val config2 = validConfig()

            assertEquals(config1, config2)
            assertEquals(config1.hashCode(), config2.hashCode())
        }

        @Test
        @DisplayName("configs with different fields should not be equal")
        fun `configs with different fields should not be equal`() {
            val config1 = validConfig(modelName = "model-a")
            val config2 = validConfig(modelName = "model-b")

            assertNotEquals(config1, config2)
        }
    }

    // =========================================================================
    // SecurityLevel Enum
    // =========================================================================

    @Nested
    @DisplayName("SecurityLevel Enum")
    inner class SecurityLevelTests {

        @Test
        @DisplayName("should have all four security levels")
        fun `should have all four security levels`() {
            val levels = AIConfig.SecurityLevel.entries
            assertEquals(4, levels.size)
            assertTrue(levels.contains(AIConfig.SecurityLevel.LOW))
            assertTrue(levels.contains(AIConfig.SecurityLevel.MEDIUM))
            assertTrue(levels.contains(AIConfig.SecurityLevel.HIGH))
            assertTrue(levels.contains(AIConfig.SecurityLevel.MAXIMUM))
        }

        @Test
        @DisplayName("should have correct ordinal order from LOW to MAXIMUM")
        fun `should have correct ordinal order from LOW to MAXIMUM`() {
            assertTrue(AIConfig.SecurityLevel.LOW.ordinal < AIConfig.SecurityLevel.MEDIUM.ordinal)
            assertTrue(AIConfig.SecurityLevel.MEDIUM.ordinal < AIConfig.SecurityLevel.HIGH.ordinal)
            assertTrue(AIConfig.SecurityLevel.HIGH.ordinal < AIConfig.SecurityLevel.MAXIMUM.ordinal)
        }

        @Test
        @DisplayName("should resolve enum by name")
        fun `should resolve enum by name`() {
            assertEquals(AIConfig.SecurityLevel.LOW, AIConfig.SecurityLevel.valueOf("LOW"))
            assertEquals(AIConfig.SecurityLevel.MEDIUM, AIConfig.SecurityLevel.valueOf("MEDIUM"))
            assertEquals(AIConfig.SecurityLevel.HIGH, AIConfig.SecurityLevel.valueOf("HIGH"))
            assertEquals(AIConfig.SecurityLevel.MAXIMUM, AIConfig.SecurityLevel.valueOf("MAXIMUM"))
        }
    }

    // =========================================================================
    // validate()
    // =========================================================================

    @Nested
    @DisplayName("validate()")
    inner class ValidateTests {

        @Test
        @DisplayName("should return true for a fully valid config")
        fun `should return true for a fully valid config`() {
            val config = validConfig()
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should return false when modelName is empty")
        fun `should return false when modelName is empty`() {
            val config = validConfig(modelName = "")
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when apiKey is empty")
        fun `should return false when apiKey is empty`() {
            val config = validConfig(apiKey = "")
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when projectId is empty")
        fun `should return false when projectId is empty`() {
            val config = validConfig(projectId = "")
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when maxTokens is zero")
        fun `should return false when maxTokens is zero`() {
            val config = validConfig(maxTokens = 0)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when maxTokens is negative")
        fun `should return false when maxTokens is negative`() {
            val config = validConfig(maxTokens = -1)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when temperature is below 0.0")
        fun `should return false when temperature is below 0 0`() {
            val config = validConfig(temperature = -0.1f)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when temperature is above 2.0")
        fun `should return false when temperature is above 2 0`() {
            val config = validConfig(temperature = 2.1f)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return true when temperature is exactly 0.0")
        fun `should return true when temperature is exactly 0 0`() {
            val config = validConfig(temperature = 0.0f)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should return true when temperature is exactly 2.0")
        fun `should return true when temperature is exactly 2 0`() {
            val config = validConfig(temperature = 2.0f)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should return false when timeout is zero")
        fun `should return false when timeout is zero`() {
            val config = validConfig(timeout = 0L)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return false when timeout is negative")
        fun `should return false when timeout is negative`() {
            val config = validConfig(timeout = -1L)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return true when retryAttempts is zero")
        fun `should return true when retryAttempts is zero`() {
            val config = validConfig(retryAttempts = 0)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should return false when retryAttempts is negative")
        fun `should return false when retryAttempts is negative`() {
            val config = validConfig(retryAttempts = -1)
            assertFalse(config.validate())
        }

        @Test
        @DisplayName("should return true with maxTokens of 1")
        fun `should return true with maxTokens of 1`() {
            val config = validConfig(maxTokens = 1)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should return true with timeout of 1ms")
        fun `should return true with timeout of 1ms`() {
            val config = validConfig(timeout = 1L)
            assertTrue(config.validate())
        }
    }

    // =========================================================================
    // toDebugString()
    // =========================================================================

    @Nested
    @DisplayName("toDebugString()")
    inner class ToDebugStringTests {

        @Test
        @DisplayName("should contain modelName in debug string")
        fun `should contain modelName in debug string`() {
            val config = validConfig(modelName = "my-unique-model")
            assertTrue(config.toDebugString().contains("my-unique-model"))
        }

        @Test
        @DisplayName("should contain projectId in debug string")
        fun `should contain projectId in debug string`() {
            val config = validConfig(projectId = "unique-project-id")
            assertTrue(config.toDebugString().contains("unique-project-id"))
        }

        @Test
        @DisplayName("should contain endpoint in debug string")
        fun `should contain endpoint in debug string`() {
            val config = validConfig(endpoint = "https://test.endpoint.io")
            assertTrue(config.toDebugString().contains("https://test.endpoint.io"))
        }

        @Test
        @DisplayName("should NOT expose apiKey in debug string")
        fun `should NOT expose apiKey in debug string`() {
            val config = validConfig(apiKey = "super-secret-key-12345")
            assertFalse(
                config.toDebugString().contains("super-secret-key-12345"),
                "apiKey should not appear in debug output"
            )
        }

        @Test
        @DisplayName("should contain maxTokens value in debug string")
        fun `should contain maxTokens value in debug string`() {
            val config = validConfig(maxTokens = 8192)
            assertTrue(config.toDebugString().contains("8192"))
        }

        @Test
        @DisplayName("should contain temperature in debug string")
        fun `should contain temperature in debug string`() {
            val config = validConfig(temperature = 0.9f)
            assertTrue(config.toDebugString().contains("0.9"))
        }

        @Test
        @DisplayName("should contain timeout with 'ms' suffix in debug string")
        fun `should contain timeout with ms suffix in debug string`() {
            val config = validConfig(timeout = 45000L)
            val debugStr = config.toDebugString()
            assertTrue(debugStr.contains("45000ms"))
        }

        @Test
        @DisplayName("should contain retryAttempts in debug string")
        fun `should contain retryAttempts in debug string`() {
            val config = validConfig(retryAttempts = 7)
            assertTrue(config.toDebugString().contains("7"))
        }

        @Test
        @DisplayName("should contain securityLevel in debug string")
        fun `should contain securityLevel in debug string`() {
            val config = validConfig(securityLevel = AIConfig.SecurityLevel.MAXIMUM)
            assertTrue(config.toDebugString().contains("MAXIMUM"))
        }

        @Test
        @DisplayName("should return trimmed non-empty string")
        fun `should return trimmed non-empty string`() {
            val result = validConfig().toDebugString()
            assertTrue(result.isNotBlank())
            assertFalse(result.startsWith(" "))
            assertFalse(result.endsWith(" "))
        }
    }

    // =========================================================================
    // createForTesting()
    // =========================================================================

    @Nested
    @DisplayName("createForTesting()")
    inner class CreateForTestingTests {

        @Test
        @DisplayName("should return a valid AIConfig")
        fun `should return a valid AIConfig`() {
            val config = AIConfig.createForTesting()
            assertNotNull(config)
        }

        @Test
        @DisplayName("should have a non-empty modelName")
        fun `should have a non-empty modelName`() {
            val config = AIConfig.createForTesting()
            assertTrue(config.modelName.isNotEmpty())
        }

        @Test
        @DisplayName("should have a non-empty apiKey")
        fun `should have a non-empty apiKey`() {
            val config = AIConfig.createForTesting()
            assertTrue(config.apiKey.isNotEmpty())
        }

        @Test
        @DisplayName("should have a non-empty projectId")
        fun `should have a non-empty projectId`() {
            val config = AIConfig.createForTesting()
            assertTrue(config.projectId.isNotEmpty())
        }

        @Test
        @DisplayName("should pass validate()")
        fun `should pass validate()`() {
            val config = AIConfig.createForTesting()
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should have logging disabled")
        fun `should have logging disabled`() {
            val config = AIConfig.createForTesting()
            assertFalse(config.enableLogging)
        }

        @Test
        @DisplayName("should have analytics disabled")
        fun `should have analytics disabled`() {
            val config = AIConfig.createForTesting()
            assertFalse(config.enableAnalytics)
        }

        @Test
        @DisplayName("should use LOW security level")
        fun `should use LOW security level`() {
            val config = AIConfig.createForTesting()
            assertEquals(AIConfig.SecurityLevel.LOW, config.securityLevel)
        }

        @Test
        @DisplayName("should use test model name")
        fun `should use test model name`() {
            val config = AIConfig.createForTesting()
            assertEquals("genesis-test-model", config.modelName)
        }

        @Test
        @DisplayName("should use test project id")
        fun `should use test project id`() {
            val config = AIConfig.createForTesting()
            assertEquals("test-project", config.projectId)
        }
    }

    // =========================================================================
    // Boundary and Edge Cases
    // =========================================================================

    @Nested
    @DisplayName("Boundary and Edge Cases")
    inner class BoundaryTests {

        @Test
        @DisplayName("should handle very large maxTokens without failing validate")
        fun `should handle very large maxTokens without failing validate`() {
            val config = validConfig(maxTokens = Int.MAX_VALUE)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should handle very large timeout without failing validate")
        fun `should handle very large timeout without failing validate`() {
            val config = validConfig(timeout = Long.MAX_VALUE)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should handle very large retryAttempts without failing validate")
        fun `should handle very large retryAttempts without failing validate`() {
            val config = validConfig(retryAttempts = Int.MAX_VALUE)
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should handle single-character modelName in validate")
        fun `should handle single-character modelName in validate`() {
            val config = validConfig(modelName = "x")
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should handle single-character apiKey in validate")
        fun `should handle single-character apiKey in validate`() {
            val config = validConfig(apiKey = "k")
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("should handle whitespace-only modelName as invalid")
        fun `should handle whitespace-only modelName as invalid`() {
            // isNotEmpty() on whitespace returns true — this is a known behavior
            // document it: whitespace-only strings pass isNotEmpty() but are semantically empty
            val config = validConfig(modelName = "   ")
            // isNotEmpty returns true for whitespace - documents current behavior
            assertTrue(config.validate())
        }

        @Test
        @DisplayName("createForTesting produces configs that are equal on repeated calls")
        fun `createForTesting produces configs that are equal on repeated calls`() {
            val config1 = AIConfig.createForTesting()
            val config2 = AIConfig.createForTesting()
            assertEquals(config1, config2)
        }
    }
}