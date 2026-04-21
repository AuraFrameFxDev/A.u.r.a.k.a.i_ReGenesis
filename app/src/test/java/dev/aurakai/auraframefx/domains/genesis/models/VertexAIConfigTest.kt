package dev.aurakai.auraframefx.domains.genesis.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [VertexAIConfig] helper methods and construction.
 *
 * These are pure-function tests; no mocking required.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("VertexAIConfig Tests")
class VertexAIConfigTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Default construction
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Default construction")
    inner class DefaultConstructionTests {

        @Test
        @DisplayName("should use us-central1 as default location")
        fun shouldUseDefaultLocation() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals("us-central1", config.location)
        }

        @Test
        @DisplayName("should use Gemini 1.5 Pro 002 as default model")
        fun shouldUseDefaultModel() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals("gemini-1.5-pro-002", config.modelName)
        }

        @Test
        @DisplayName("should default apiVersion to v1")
        fun shouldDefaultApiVersionToV1() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals("v1", config.apiVersion)
        }

        @Test
        @DisplayName("should default apiKey to null")
        fun shouldDefaultApiKeyToNull() {
            val config = VertexAIConfig(projectId = "my-project")
            assertNull(config.apiKey)
        }

        @Test
        @DisplayName("should enable safety filters by default")
        fun shouldEnableSafetyFiltersByDefault() {
            val config = VertexAIConfig(projectId = "my-project")
            assertTrue(config.enableSafetyFilters)
        }

        @Test
        @DisplayName("should default maxContentLength to 1_000_000 (1 MB)")
        fun shouldDefaultMaxContentLengthToOneMB() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(1_000_000, config.maxContentLength)
        }

        @Test
        @DisplayName("should default timeoutMs to 30 seconds")
        fun shouldDefaultTimeoutTo30Seconds() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(30_000L, config.timeoutMs)
        }

        @Test
        @DisplayName("should default maxRetries to 3")
        fun shouldDefaultMaxRetriesTo3() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(3, config.maxRetries)
        }

        @Test
        @DisplayName("should default retryDelayMs to 1 second")
        fun shouldDefaultRetryDelayTo1Second() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(1_000L, config.retryDelayMs)
        }

        @Test
        @DisplayName("should enable caching by default")
        fun shouldEnableCachingByDefault() {
            val config = VertexAIConfig(projectId = "my-project")
            assertTrue(config.enableCaching)
        }

        @Test
        @DisplayName("should default cacheExpiryMs to 1 hour")
        fun shouldDefaultCacheExpiryToOneHour() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(3_600_000L, config.cacheExpiryMs)
        }

        @Test
        @DisplayName("should default maxCacheSize to 100")
        fun shouldDefaultMaxCacheSizeTo100() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(100, config.maxCacheSize)
        }

        @Test
        @DisplayName("should default defaultTemperature to 0.7")
        fun shouldDefaultTemperatureTo07() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(0.7, config.defaultTemperature)
        }

        @Test
        @DisplayName("should default defaultMaxTokens to 1024")
        fun shouldDefaultMaxTokensTo1024() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals(1024, config.defaultMaxTokens)
        }

        @Test
        @DisplayName("should enable logging by default")
        fun shouldEnableLoggingByDefault() {
            val config = VertexAIConfig(projectId = "my-project")
            assertTrue(config.enableLogging)
        }

        @Test
        @DisplayName("should default logLevel to INFO")
        fun shouldDefaultLogLevelToInfo() {
            val config = VertexAIConfig(projectId = "my-project")
            assertEquals("INFO", config.logLevel)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // getFullEndpoint()
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getFullEndpoint()")
    inner class GetFullEndpointTests {

        @Test
        @DisplayName("should produce correct full endpoint URL")
        fun shouldProduceCorrectFullEndpointUrl() {
            val config = VertexAIConfig(
                projectId = "collabcanvas",
                location = "us-central1",
                endpoint = "us-central1-aiplatform.googleapis.com",
                apiVersion = "v1"
            )
            val expected = "https://us-central1-aiplatform.googleapis.com/v1/projects/collabcanvas/locations/us-central1"
            assertEquals(expected, config.getFullEndpoint())
        }

        @Test
        @DisplayName("should include HTTPS scheme")
        fun shouldIncludeHttpsScheme() {
            val config = VertexAIConfig(projectId = "p")
            assertTrue(config.getFullEndpoint().startsWith("https://"))
        }

        @Test
        @DisplayName("should embed projectId in URL")
        fun shouldEmbedProjectIdInUrl() {
            val config = VertexAIConfig(projectId = "unique-project-xyz")
            assertTrue(config.getFullEndpoint().contains("unique-project-xyz"))
        }

        @Test
        @DisplayName("should embed location in URL")
        fun shouldEmbedLocationInUrl() {
            val config = VertexAIConfig(projectId = "p", location = "europe-west4")
            assertTrue(config.getFullEndpoint().contains("europe-west4"))
        }

        @Test
        @DisplayName("should embed apiVersion in URL")
        fun shouldEmbedApiVersionInUrl() {
            val config = VertexAIConfig(projectId = "p", apiVersion = "v2beta")
            assertTrue(config.getFullEndpoint().contains("v2beta"))
        }

        @Test
        @DisplayName("should embed endpoint host in URL")
        fun shouldEmbedEndpointHostInUrl() {
            val config = VertexAIConfig(
                projectId = "p",
                endpoint = "asia-northeast1-aiplatform.googleapis.com"
            )
            assertTrue(config.getFullEndpoint().contains("asia-northeast1-aiplatform.googleapis.com"))
        }

        @Test
        @DisplayName("should not end with a trailing slash")
        fun shouldNotEndWithTrailingSlash() {
            val config = VertexAIConfig(projectId = "p")
            assertFalse(config.getFullEndpoint().endsWith("/"))
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // getModelEndpoint()
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getModelEndpoint()")
    inner class GetModelEndpointTests {

        @Test
        @DisplayName("should produce correct model endpoint URL")
        fun shouldProduceCorrectModelEndpointUrl() {
            val config = VertexAIConfig(
                projectId = "collabcanvas",
                location = "us-central1",
                endpoint = "us-central1-aiplatform.googleapis.com",
                apiVersion = "v1",
                modelName = "gemini-1.5-pro-002"
            )
            val expected =
                "https://us-central1-aiplatform.googleapis.com/v1/projects/collabcanvas/locations/us-central1" +
                    "/publishers/google/models/gemini-1.5-pro-002:generateContent"
            assertEquals(expected, config.getModelEndpoint())
        }

        @Test
        @DisplayName("should append publishers/google/models/<modelName>:generateContent suffix")
        fun shouldAppendCorrectSuffix() {
            val config = VertexAIConfig(projectId = "p", modelName = "gemini-flash")
            val endpoint = config.getModelEndpoint()
            assertTrue(endpoint.endsWith("publishers/google/models/gemini-flash:generateContent"))
        }

        @Test
        @DisplayName("should be a prefix-extension of getFullEndpoint()")
        fun shouldExtendFullEndpoint() {
            val config = VertexAIConfig(projectId = "p")
            assertTrue(config.getModelEndpoint().startsWith(config.getFullEndpoint()))
        }

        @Test
        @DisplayName("should embed modelName in the URL")
        fun shouldEmbedModelNameInUrl() {
            val config = VertexAIConfig(projectId = "p", modelName = "custom-model-v99")
            assertTrue(config.getModelEndpoint().contains("custom-model-v99"))
        }

        @Test
        @DisplayName("should end with :generateContent")
        fun shouldEndWithGenerateContent() {
            val config = VertexAIConfig(projectId = "p")
            assertTrue(config.getModelEndpoint().endsWith(":generateContent"))
        }

        @Test
        @DisplayName("should include HTTPS scheme")
        fun shouldIncludeHttpsScheme() {
            val config = VertexAIConfig(projectId = "p")
            assertTrue(config.getModelEndpoint().startsWith("https://"))
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // copy / equals / hashCode (data class contract)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("data class contract")
    inner class DataClassContractTests {

        @Test
        @DisplayName("copy() should produce equal configs with changed field")
        fun copyShouldProduceEqualConfigsWithChangedField() {
            val original = VertexAIConfig(projectId = "original")
            val copy = original.copy(projectId = "copy")
            assertNotEquals(original, copy)
            assertEquals("copy", copy.projectId)
        }

        @Test
        @DisplayName("identical configs should be equal")
        fun identicalConfigsShouldBeEqual() {
            val a = VertexAIConfig(projectId = "same", apiKey = "key")
            val b = VertexAIConfig(projectId = "same", apiKey = "key")
            assertEquals(a, b)
            assertEquals(a.hashCode(), b.hashCode())
        }

        @Test
        @DisplayName("different projectIds should not be equal")
        fun differentProjectIdsShouldNotBeEqual() {
            val a = VertexAIConfig(projectId = "a")
            val b = VertexAIConfig(projectId = "b")
            assertNotEquals(a, b)
        }
    }
}