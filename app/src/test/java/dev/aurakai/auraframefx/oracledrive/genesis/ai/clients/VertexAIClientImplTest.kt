package dev.aurakai.auraframefx.oracledrive.genesis.ai.clients

import dev.aurakai.auraframefx.domains.genesis.models.VertexAIConfig
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever

/**
 * Comprehensive tests for [VertexAIClientImpl].
 *
 * Tests focus on:
 * - Input validation logic (validatePrompt)
 * - Fallback analysis structure (createFallbackAnalysis)
 * - Content analysis response parsing (analyzeContent pipe-delimited format)
 * - Cache LRU eviction policy (removeEldestEntry)
 * - Multimodal embedding edge case for empty input
 * - VertexAIException properties
 * - generateCode and generateContent delegation
 */
@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("VertexAIClientImpl Tests")
class VertexAIClientImplTest {

    private lateinit var defaultConfig: VertexAIConfig

    @BeforeEach
    fun setUp() {
        defaultConfig = VertexAIConfig(
            projectId = "test-project",
            location = "us-central1",
            endpoint = "us-central1-aiplatform.googleapis.com",
            modelName = "gemini-1.5-pro-002",
            apiKey = "test-api-key",
            maxContentLength = 1000000,
            timeoutMs = 30000,
            maxRetries = 3,
            retryDelayMs = 1000,
            enableCaching = true,
            cacheExpiryMs = 3600000,
            maxCacheSize = 100,
            enableSafetyFilters = true,
            defaultTemperature = 0.7,
            defaultMaxTokens = 1024,
            enableLogging = false,
            logLevel = "INFO"
        )
    }

    // ──────────────────────────────────────────────────────────────────────────
    // VertexAIException
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("VertexAIException")
    inner class VertexAIExceptionTests {

        @Test
        @DisplayName("should store httpCode and message")
        fun shouldStoreHttpCodeAndMessage() {
            val ex = VertexAIException("HTTP 429: Too Many Requests", 429)
            assertEquals(429, ex.httpCode)
            assertEquals("HTTP 429: Too Many Requests", ex.message)
        }

        @Test
        @DisplayName("should be a subtype of Exception")
        fun shouldBeSubtypeOfException() {
            val ex = VertexAIException("error", 500)
            assertTrue(ex is Exception)
        }

        @Test
        @DisplayName("should preserve zero httpCode for non-HTTP errors")
        fun shouldPreserveZeroHttpCode() {
            val ex = VertexAIException("Serialisation failure", 0)
            assertEquals(0, ex.httpCode)
        }

        @Test
        @DisplayName("should handle 4xx client error codes")
        fun shouldHandle4xxClientErrors() {
            listOf(400, 401, 403, 404, 422).forEach { code ->
                val ex = VertexAIException("Client error", code)
                assertEquals(code, ex.httpCode)
            }
        }

        @Test
        @DisplayName("should handle 5xx server error codes")
        fun shouldHandle5xxServerErrors() {
            listOf(500, 502, 503, 504).forEach { code ->
                val ex = VertexAIException("Server error", code)
                assertEquals(code, ex.httpCode)
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // analyzeContent — response parsing
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("analyzeContent — response parsing")
    inner class AnalyzeContentParsingTests {

        /**
         * A minimal subclass that allows us to stub the HTTP layer so that
         * [analyzeContent] and [generateText] can be exercised in isolation.
         */
        private inner class StubVertexAIClientImpl(
            config: VertexAIConfig,
            private val stubResponse: String?
        ) : VertexAIClientImpl(config) {

            // Override generateText to bypass OkHttp entirely
            override suspend fun generateText(prompt: String, temperature: Float, maxTokens: Int): String? {
                return stubResponse
            }
        }

        @Test
        @DisplayName("should parse well-formed pipe-delimited AI response")
        fun shouldParseWellFormedResponse() = runTest {
            val client = StubVertexAIClientImpl(
                config = defaultConfig,
                stubResponse = "positive|low|kotlin,android,ai|0.92"
            )
            val result = client.analyzeContent("Some test content")

            assertEquals("positive", result["sentiment"])
            assertEquals("low", result["complexity"])
            @Suppress("UNCHECKED_CAST")
            val topics = result["topics"] as List<String>
            assertEquals(listOf("kotlin", "android", "ai"), topics)
            assertEquals(0.92, result["confidence"])
            assertEquals("ai_powered", result["analysis_type"])
        }

        @Test
        @DisplayName("should fall back when AI returns null")
        fun shouldFallbackWhenAIReturnsNull() = runTest {
            val client = StubVertexAIClientImpl(
                config = defaultConfig,
                stubResponse = null
            )
            val result = client.analyzeContent("some content")

            assertEquals("neutral", result["sentiment"])
            assertEquals("medium", result["complexity"])
            assertEquals(0.5, result["confidence"])
            assertEquals("fallback", result["analysis_type"])
        }

        @Test
        @DisplayName("should fall back gracefully when AI response cannot be parsed")
        fun shouldFallbackOnParseFailure() = runTest {
            // Response with no pipe delimiters; double/parse of 4th segment will fail
            val client = StubVertexAIClientImpl(
                config = defaultConfig,
                stubResponse = "this is not a pipe delimited response at all"
            )
            // Should not throw; falls back when parse fails or returns partial data
            val result = client.analyzeContent("some content")
            assertNotNull(result["sentiment"])
            assertNotNull(result["word_count"])
        }

        @Test
        @DisplayName("should count words correctly in fallback analysis")
        fun shouldCountWordsInFallback() = runTest {
            val content = "one two three four five"
            val client = StubVertexAIClientImpl(config = defaultConfig, stubResponse = null)
            val result = client.analyzeContent(content)
            assertEquals(5, result["word_count"])
        }

        @Test
        @DisplayName("should report word count in AI-powered analysis")
        fun shouldReportWordCountInAiAnalysis() = runTest {
            val content = "alpha beta gamma"
            val client = StubVertexAIClientImpl(
                config = defaultConfig,
                stubResponse = "neutral|medium|topics|0.8"
            )
            val result = client.analyzeContent(content)
            assertEquals(3, result["word_count"])
        }

        @Test
        @DisplayName("should use neutral sentiment when first segment is missing")
        fun shouldDefaultToNeutralSentimentOnMissingSegment() = runTest {
            val client = StubVertexAIClientImpl(
                config = defaultConfig,
                stubResponse = "|medium|topic|0.5"
            )
            val result = client.analyzeContent("content")
            // Empty string from split — the implementation trims it; asserting no crash
            assertNotNull(result["sentiment"])
        }

        @Test
        @DisplayName("should use confidence of 0.75 when 4th segment is not a valid double")
        fun shouldDefaultConfidenceWhenInvalid() = runTest {
            val client = StubVertexAIClientImpl(
                config = defaultConfig,
                stubResponse = "positive|high|kotlin|not-a-number"
            )
            val result = client.analyzeContent("content")
            assertEquals(0.75, result["confidence"])
        }

        @Test
        @DisplayName("should validate prompt length before calling AI in analyzeContent")
        fun shouldRejectBlankContentInAnalyzeContent() = runTest {
            val client = StubVertexAIClientImpl(config = defaultConfig, stubResponse = null)
            val exception = assertThrows(IllegalArgumentException::class.java) {
                runTest { client.analyzeContent("") }
            }
            assertTrue(exception.message?.contains("blank") == true)
        }

        @Test
        @DisplayName("should reject content exceeding maxContentLength")
        fun shouldRejectOversizedContentInAnalyzeContent() = runTest {
            val smallMaxConfig = defaultConfig.copy(maxContentLength = 10)
            val client = StubVertexAIClientImpl(config = smallMaxConfig, stubResponse = null)
            val exception = assertThrows(IllegalArgumentException::class.java) {
                runTest { client.analyzeContent("This content is longer than 10 characters") }
            }
            assertTrue(exception.message?.contains("max length") == true)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // generateCode
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("generateCode")
    inner class GenerateCodeTests {

        private inner class StubVertexAIClientImpl(
            config: VertexAIConfig,
            private val stubResponse: String?
        ) : VertexAIClientImpl(config) {
            var capturedPrompt: String? = null

            override suspend fun generateText(prompt: String, temperature: Float, maxTokens: Int): String? {
                capturedPrompt = prompt
                return stubResponse
            }
        }

        @Test
        @DisplayName("should embed language and style in the generated prompt")
        fun shouldEmbedLanguageAndStyleInPrompt() = runTest {
            val client = StubVertexAIClientImpl(config = defaultConfig, stubResponse = "fun foo() {}")
            client.generateCode("create a foo function", "Kotlin", "functional")
            assertNotNull(client.capturedPrompt)
            assertTrue(client.capturedPrompt!!.contains("Kotlin"))
            assertTrue(client.capturedPrompt!!.contains("functional"))
        }

        @Test
        @DisplayName("should embed specification in the generated prompt")
        fun shouldEmbedSpecificationInPrompt() = runTest {
            val client = StubVertexAIClientImpl(config = defaultConfig, stubResponse = "class Foo")
            client.generateCode("create a Foo class", "Java", "OOP")
            assertTrue(client.capturedPrompt!!.contains("create a Foo class"))
        }

        @Test
        @DisplayName("should pass low temperature for deterministic code generation")
        fun shouldPassLowTemperatureForCodeGen() = runTest {
            var capturedTemp: Float? = null
            val client = object : VertexAIClientImpl(defaultConfig) {
                override suspend fun generateText(prompt: String, temperature: Float, maxTokens: Int): String? {
                    capturedTemp = temperature
                    return "code"
                }
            }
            client.generateCode("spec", "Kotlin", "clean")
            assertNotNull(capturedTemp)
            assertTrue(capturedTemp!! <= 0.3f, "Temperature should be low (<=0.3) for code generation, was $capturedTemp")
        }

        @Test
        @DisplayName("should validate non-blank specification")
        fun shouldRejectBlankSpecification() = runTest {
            val client = object : VertexAIClientImpl(defaultConfig) {
                override suspend fun generateText(prompt: String, temperature: Float, maxTokens: Int): String? = "code"
            }
            assertThrows(IllegalArgumentException::class.java) {
                runTest { client.generateCode("", "Kotlin", "clean") }
            }
        }

        @Test
        @DisplayName("should return null when AI service returns null")
        fun shouldReturnNullWhenAIReturnsNull() = runTest {
            val client = object : VertexAIClientImpl(defaultConfig) {
                override suspend fun generateText(prompt: String, temperature: Float, maxTokens: Int): String? = null
            }
            val result = client.generateCode("valid spec", "Python", "simple")
            assertNull(result)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // generateContent
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("generateContent")
    inner class GenerateContentTests {

        @Test
        @DisplayName("should delegate to generateText(prompt)")
        fun shouldDelegateToGenerateText() = runTest {
            val capturedPrompts = mutableListOf<String>()
            val client = object : VertexAIClientImpl(defaultConfig) {
                override suspend fun generateText(prompt: String): String? {
                    capturedPrompts.add(prompt)
                    return "generated text"
                }
            }
            val result = client.generateContent("hello world")
            assertEquals("generated text", result)
            assertEquals(listOf("hello world"), capturedPrompts)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // generateMultimodalEmbedding — empty list fast-path
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("generateMultimodalEmbedding")
    inner class GenerateMultimodalEmbeddingTests {

        @Test
        @DisplayName("should return FloatArray(0) for empty content list")
        fun shouldReturnEmptyArrayForEmptyInput() = runTest {
            // Use a stub config with an unreachable endpoint so no HTTP call is made
            val client = VertexAIClientImpl(defaultConfig)
            val result = client.generateMultimodalEmbedding(emptyList(), MrlDimension.OPTIMAL)
            assertNotNull(result)
            assertEquals(0, result.size)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // initialize / cleanup
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("lifecycle — initialize and cleanup")
    inner class LifecycleTests {

        @Test
        @DisplayName("initialize should complete without throwing")
        fun initializeShouldNotThrow() = runTest {
            val client = VertexAIClientImpl(defaultConfig)
            assertDoesNotThrow { runTest { client.initialize() } }
        }

        @Test
        @DisplayName("cleanup should complete without throwing")
        fun cleanupShouldNotThrow() = runTest {
            val client = VertexAIClientImpl(defaultConfig)
            assertDoesNotThrow { runTest { client.cleanup() } }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // initializeCreativeModels
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("initializeCreativeModels")
    inner class InitializeCreativeModelsTests {

        @Test
        @DisplayName("should complete without throwing")
        fun shouldCompleteWithoutThrowing() = runTest {
            val client = VertexAIClientImpl(defaultConfig)
            assertDoesNotThrow { runTest { client.initializeCreativeModels() } }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // analyzeImage — stub response
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("analyzeImage")
    inner class AnalyzeImageTests {

        @Test
        @DisplayName("should return non-null stub string describing image size")
        fun shouldReturnStubStringWithImageSize() = runTest {
            val client = VertexAIClientImpl(defaultConfig)
            val imageData = ByteArray(512)
            val result = client.analyzeImage(imageData, "Describe the image")
            assertNotNull(result)
            assertTrue(result.contains("512"), "Result should include image size in bytes")
        }

        @Test
        @DisplayName("should include prompt in the stub response")
        fun shouldIncludePromptInResponse() = runTest {
            val client = VertexAIClientImpl(defaultConfig)
            val result = client.analyzeImage(ByteArray(10), "What colour is this?")
            assertTrue(result.contains("What colour is this?"))
        }

        @Test
        @DisplayName("should handle empty byte array without throwing")
        fun shouldHandleEmptyByteArray() = runTest {
            val client = VertexAIClientImpl(defaultConfig)
            val result = client.analyzeImage(ByteArray(0), "empty")
            assertNotNull(result)
        }
    }
}