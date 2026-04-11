package dev.aurakai.auraframefx.domains.genesis.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * Unit tests for [VertexAIConfig] — the configuration data class used by the staging
 * [VertexAIClientImpl] (added in this PR). Verifies endpoint URL construction and
 * default / custom field values.
 */
@DisplayName("VertexAIConfig Tests")
class VertexAIConfigTest {

    // ── Fixtures ─────────────────────────────────────────────────────────────

    private val defaultConfig = VertexAIConfig(projectId = "my-project")

    private fun configWith(
        projectId: String = "proj",
        location: String = "us-central1",
        endpoint: String = "us-central1-aiplatform.googleapis.com",
        modelName: String = "gemini-1.5-pro-002",
        apiVersion: String = "v1",
    ) = VertexAIConfig(
        projectId = projectId,
        location = location,
        endpoint = endpoint,
        modelName = modelName,
        apiVersion = apiVersion,
    )

    // ── Default value tests ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Default Values")
    inner class DefaultValues {

        @Test
        @DisplayName("default location is us-central1")
        fun defaultLocation() {
            assertEquals("us-central1", defaultConfig.location)
        }

        @Test
        @DisplayName("default endpoint is us-central1-aiplatform.googleapis.com")
        fun defaultEndpoint() {
            assertEquals("us-central1-aiplatform.googleapis.com", defaultConfig.endpoint)
        }

        @Test
        @DisplayName("default model name is gemini-1.5-pro-002")
        fun defaultModelName() {
            assertEquals("gemini-1.5-pro-002", defaultConfig.modelName)
        }

        @Test
        @DisplayName("default apiVersion is v1")
        fun defaultApiVersion() {
            assertEquals("v1", defaultConfig.apiVersion)
        }

        @Test
        @DisplayName("apiKey is null by default")
        fun defaultApiKeyIsNull() {
            assertNull(defaultConfig.apiKey)
        }

        @Test
        @DisplayName("useApplicationDefaultCredentials is true by default")
        fun defaultADCEnabled() {
            assertTrue(defaultConfig.useApplicationDefaultCredentials)
        }

        @Test
        @DisplayName("safety filters are enabled by default")
        fun defaultSafetyFiltersEnabled() {
            assertTrue(defaultConfig.enableSafetyFilters)
        }

        @Test
        @DisplayName("default maxContentLength is 1 MB")
        fun defaultMaxContentLength() {
            assertEquals(1_000_000, defaultConfig.maxContentLength)
        }

        @Test
        @DisplayName("default timeout is 30 seconds")
        fun defaultTimeout() {
            assertEquals(30_000L, defaultConfig.timeoutMs)
        }

        @Test
        @DisplayName("default maxRetries is 3")
        fun defaultMaxRetries() {
            assertEquals(3, defaultConfig.maxRetries)
        }

        @Test
        @DisplayName("default retryDelay is 1 second")
        fun defaultRetryDelay() {
            assertEquals(1_000L, defaultConfig.retryDelayMs)
        }

        @Test
        @DisplayName("caching is enabled by default")
        fun defaultCachingEnabled() {
            assertTrue(defaultConfig.enableCaching)
        }

        @Test
        @DisplayName("default cache expiry is 1 hour")
        fun defaultCacheExpiry() {
            assertEquals(3_600_000L, defaultConfig.cacheExpiryMs)
        }

        @Test
        @DisplayName("default maxCacheSize is 100")
        fun defaultMaxCacheSize() {
            assertEquals(100, defaultConfig.maxCacheSize)
        }

        @Test
        @DisplayName("default temperature is 0.7")
        fun defaultTemperature() {
            assertEquals(0.7, defaultConfig.defaultTemperature)
        }

        @Test
        @DisplayName("default topP is 0.9")
        fun defaultTopP() {
            assertEquals(0.9, defaultConfig.defaultTopP)
        }

        @Test
        @DisplayName("default topK is 40")
        fun defaultTopK() {
            assertEquals(40, defaultConfig.defaultTopK)
        }

        @Test
        @DisplayName("default maxTokens is 1024")
        fun defaultMaxTokens() {
            assertEquals(1024, defaultConfig.defaultMaxTokens)
        }

        @Test
        @DisplayName("logging is enabled by default")
        fun defaultLoggingEnabled() {
            assertTrue(defaultConfig.enableLogging)
        }

        @Test
        @DisplayName("default logLevel is INFO")
        fun defaultLogLevel() {
            assertEquals("INFO", defaultConfig.logLevel)
        }
    }

    // ── getFullEndpoint() ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("getFullEndpoint()")
    inner class GetFullEndpoint {

        @Test
        @DisplayName("builds URL with https scheme")
        fun buildsHttpsUrl() {
            val url = defaultConfig.getFullEndpoint()
            assertTrue(url.startsWith("https://"), "Expected HTTPS scheme; got: $url")
        }

        @Test
        @DisplayName("embeds endpoint host correctly")
        fun embedsEndpointHost() {
            val url = defaultConfig.getFullEndpoint()
            assertTrue(url.contains("us-central1-aiplatform.googleapis.com"), "URL: $url")
        }

        @Test
        @DisplayName("embeds apiVersion correctly")
        fun embedsApiVersion() {
            val url = defaultConfig.getFullEndpoint()
            assertTrue(url.contains("/v1/"), "URL: $url")
        }

        @Test
        @DisplayName("embeds projectId correctly")
        fun embedsProjectId() {
            val url = configWith(projectId = "sentinel-project").getFullEndpoint()
            assertTrue(url.contains("sentinel-project"), "URL: $url")
        }

        @Test
        @DisplayName("embeds location correctly")
        fun embedsLocation() {
            val url = configWith(location = "europe-west4").getFullEndpoint()
            assertTrue(url.contains("europe-west4"), "URL: $url")
        }

        @ParameterizedTest(name = "projectId={0}, location={1}")
        @CsvSource(
            "proj-alpha,  us-central1",
            "proj-beta,   europe-west4",
            "proj-gamma,  asia-east1",
        )
        @DisplayName("returns correct full endpoint for various projects and locations")
        fun variousProjectsAndLocations(projectId: String, location: String) {
            val cfg = configWith(projectId = projectId.trim(), location = location.trim())
            val url = cfg.getFullEndpoint()
            assertEquals(
                "https://${cfg.endpoint}/${cfg.apiVersion}/projects/${projectId.trim()}/locations/${location.trim()}",
                url
            )
        }

        @Test
        @DisplayName("custom apiVersion is respected")
        fun customApiVersion() {
            val cfg = configWith(apiVersion = "v2beta")
            val url = cfg.getFullEndpoint()
            assertTrue(url.contains("/v2beta/"), "URL: $url")
        }
    }

    // ── getModelEndpoint() ────────────────────────────────────────────────────

    @Nested
    @DisplayName("getModelEndpoint()")
    inner class GetModelEndpoint {

        @Test
        @DisplayName("starts with getFullEndpoint()")
        fun startsWithFullEndpoint() {
            val cfg = configWith(projectId = "p1")
            val modelUrl = cfg.getModelEndpoint()
            assertTrue(modelUrl.startsWith(cfg.getFullEndpoint()), "Model URL: $modelUrl")
        }

        @Test
        @DisplayName("contains publisher path segment")
        fun containsPublisherPathSegment() {
            val url = defaultConfig.getModelEndpoint()
            assertTrue(url.contains("/publishers/google/models/"), "URL: $url")
        }

        @Test
        @DisplayName("contains generateContent action")
        fun containsGenerateContentAction() {
            val url = defaultConfig.getModelEndpoint()
            assertTrue(url.endsWith(":generateContent"), "URL: $url")
        }

        @Test
        @DisplayName("embeds modelName correctly")
        fun embedsModelName() {
            val cfg = configWith(modelName = "gemini-ultra-001")
            val url = cfg.getModelEndpoint()
            assertTrue(url.contains("gemini-ultra-001"), "URL: $url")
        }

        @Test
        @DisplayName("produces fully qualified URL for default config")
        fun fullyQualifiedUrlForDefaultConfig() {
            val cfg = configWith(projectId = "test-project")
            val expected = "https://us-central1-aiplatform.googleapis.com/v1/projects/test-project" +
                    "/locations/us-central1/publishers/google/models/gemini-1.5-pro-002:generateContent"
            assertEquals(expected, cfg.getModelEndpoint())
        }
    }

    // ── Data-class equality & copy ────────────────────────────────────────────

    @Nested
    @DisplayName("Data Class Semantics")
    inner class DataClassSemantics {

        @Test
        @DisplayName("two configs with same values are equal")
        fun equalWhenSameValues() {
            val c1 = VertexAIConfig(projectId = "x")
            val c2 = VertexAIConfig(projectId = "x")
            assertEquals(c1, c2)
        }

        @Test
        @DisplayName("copy() produces independent instance with modified field")
        fun copyProducesIndependentInstance() {
            val original = VertexAIConfig(projectId = "orig", apiKey = null)
            val copy = original.copy(apiKey = "secret-key")
            assertNull(original.apiKey, "Original should not be modified")
            assertEquals("secret-key", copy.apiKey)
        }

        @Test
        @DisplayName("disabling safety filters is reflected in copy")
        fun disableSafetyFiltersViaCopy() {
            val cfg = VertexAIConfig(projectId = "p").copy(enableSafetyFilters = false)
            assertFalse(cfg.enableSafetyFilters)
        }
    }
}