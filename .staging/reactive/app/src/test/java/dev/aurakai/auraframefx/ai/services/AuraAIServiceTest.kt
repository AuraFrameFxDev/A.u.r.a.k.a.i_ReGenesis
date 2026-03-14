package dev.aurakai.auraframefx.ai.services

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuraAIServiceTest {

    private lateinit var auraAIService: AuraAIService
    private val mockHttpClient = mockk<HttpClient>()
    private val mockApiClient = mockk<ApiClient>()
    private val mockConfigService = mockk<ConfigService>()
    private val mockLogger = mockk<Logger>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        auraAIService = AuraAIService(mockHttpClient, mockApiClient, mockConfigService, mockLogger)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("Initialization Tests")
    inner class InitializationTests {

        @Test
        @DisplayName("Should initialize service with valid configuration")
        fun `should initialize service with valid configuration`() {
            // Given
            val validConfig = mapOf(
                "apiKey" to "test-key",
                "baseUrl" to "https://api.test.com",
                "timeout" to "30000"
            )
            every { mockConfigService.getConfig("ai") } returns validConfig

            // When
            val result = auraAIService.initialize()

            // Then
            assertTrue(result)
            assertTrue(auraAIService.isInitialized())
            verify { mockConfigService.getConfig("ai") }
        }

        @Test
        @DisplayName("Should fail initialization with invalid configuration")
        fun `should fail initialization with invalid configuration`() {
            // Given
            val invalidConfig = mapOf<String, String>()
            every { mockConfigService.getConfig("ai") } returns invalidConfig

            // When
            val result = auraAIService.initialize()

            // Then
            assertFalse(result)
            assertFalse(auraAIService.isInitialized())
        }

        @Test
        @DisplayName("Should handle null configuration gracefully")
        fun `should handle null configuration gracefully`() {
            // Given
            every { mockConfigService.getConfig("ai") } returns null

            // When
            val result = auraAIService.initialize()

            // Then
            assertFalse(result)
            assertFalse(auraAIService.isInitialized())
        }
    }

    @Nested
    @DisplayName("AI Query Tests")
    inner class AIQueryTests {

        @BeforeEach
        fun setUpInitializedService() {
            val validConfig = mapOf(
                "apiKey" to "test-key",
                "baseUrl" to "https://api.test.com",
                "timeout" to "30000"
            )
            every { mockConfigService.getConfig("ai") } returns validConfig
            auraAIService.initialize()
        }

        @Test
        @DisplayName("Should successfully process valid query")
        fun `should successfully process valid query`() = runTest {
            // Given
            val query = "What is the meaning of life?"
            val expectedResponse = AIResponse(
                content = "The meaning of life is 42",
                confidence = 0.95,
                tokensUsed = 15
            )
            coEvery { mockApiClient.sendQuery(any()) } returns expectedResponse

            // When
            val result = auraAIService.processQuery(query)

            // Then
            assertEquals(expectedResponse, result)
            coVerify { mockApiClient.sendQuery(query) }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "   ", "\t\n"])
        @DisplayName("Should handle empty or whitespace queries")
        fun `should handle empty or whitespace queries`(query: String) = runTest {
            // When & Then
            assertThrows<IllegalArgumentException> {
                auraAIService.processQuery(query)
            }
        }

        @Test
        @DisplayName("Should handle null query gracefully")
        fun `should handle null query gracefully`() = runTest {
            // When & Then
            assertThrows<IllegalArgumentException> {
                auraAIService.processQuery(null)
            }
        }

        @ParameterizedTest
        @CsvSource(
            "Simple query, 50",
            "Medium length query with more words, 100",
            "This is a very long query that contains many words and should test the token counting functionality properly, 200"
        )
        @DisplayName("Should handle queries of different lengths")
        fun `should handle queries of different lengths`(query: String, expectedTokens: Int) =
            runTest {
                // Given
                val response = AIResponse(
                    content = "Test response",
                    confidence = 0.8,
                    tokensUsed = expectedTokens
                )
                coEvery { mockApiClient.sendQuery(any()) } returns response

                // When
                val result = auraAIService.processQuery(query)

                // Then
                assertEquals(expectedTokens, result.tokensUsed)
            }

        @Test
        @DisplayName("Should handle network timeout gracefully")
        fun `should handle network timeout gracefully`() = runTest {
            // Given
            val query = "Test query"
            coEvery { mockApiClient.sendQuery(any()) } throws SocketTimeoutException("Request timeout")

            // When & Then
            assertThrows<ServiceException> {
                auraAIService.processQuery(query)
            }
        }

        @Test
        @DisplayName("Should handle API rate limiting")
        fun `should handle API rate limiting`() = runTest {
            // Given
            val query = "Test query"
            coEvery { mockApiClient.sendQuery(any()) } throws ApiRateLimitException("Rate limit exceeded")

            // When & Then
            assertThrows<ServiceException> {
                auraAIService.processQuery(query)
            }
        }

        @Test
        @DisplayName("Should retry on transient failures")
        fun `should retry on transient failures`() = runTest {
            // Given
            val query = "Test query"
            val expectedResponse = AIResponse("Success", 0.9, 10)
            coEvery { mockApiClient.sendQuery(any()) } throws IOException("Network error") andThen expectedResponse

            // When
            val result = auraAIService.processQuery(query)

            // Then
            assertEquals(expectedResponse, result)
            coVerify(exactly = 2) { mockApiClient.sendQuery(query) }
        }
    }

    @Nested
    @DisplayName("Context Management Tests")
    inner class ContextManagementTests {

        @Test
        @DisplayName("Should maintain conversation context")
        fun `should maintain conversation context`() = runTest {
            // Given
            val sessionId = "test-session-123"
            val firstQuery = "Hello"
            val secondQuery = "What did I just say?"

            val firstResponse = AIResponse("Hello there!", 0.9, 5)
            val secondResponse = AIResponse("You said 'Hello'", 0.95, 8)

            coEvery {
                mockApiClient.sendQueryWithContext(
                    firstQuery,
                    emptyList()
                )
            } returns firstResponse
            coEvery {
                mockApiClient.sendQueryWithContext(
                    secondQuery,
                    any()
                )
            } returns secondResponse

            // When
            val result1 = auraAIService.processQueryWithContext(firstQuery, sessionId)
            val result2 = auraAIService.processQueryWithContext(secondQuery, sessionId)

            // Then
            assertEquals(firstResponse, result1)
            assertEquals(secondResponse, result2)
            coVerify { mockApiClient.sendQueryWithContext(secondQuery, match { it.isNotEmpty() }) }
        }

        @Test
        @DisplayName("Should clear context when requested")
        fun `should clear context when requested`() {
            // Given
            val sessionId = "test-session-123"
            auraAIService.storeContext(sessionId, "Previous context")

            // When
            auraAIService.clearContext(sessionId)

            // Then
            assertTrue(auraAIService.getContext(sessionId).isEmpty())
        }

        @Test
        @DisplayName("Should handle multiple concurrent sessions")
        fun `should handle multiple concurrent sessions`() = runTest {
            // Given
            val session1 = "session-1"
            val session2 = "session-2"
            val query = "Test query"

            val response1 = AIResponse("Response 1", 0.8, 10)
            val response2 = AIResponse("Response 2", 0.9, 12)

            coEvery {
                mockApiClient.sendQueryWithContext(
                    query,
                    emptyList()
                )
            } returns response1 andThen response2

            // When
            val result1 = auraAIService.processQueryWithContext(query, session1)
            val result2 = auraAIService.processQueryWithContext(query, session2)

            // Then
            assertEquals(response1, result1)
            assertEquals(response2, result2)
            assertNotEquals(auraAIService.getContext(session1), auraAIService.getContext(session2))
        }
    }

    @Nested
    @DisplayName("Configuration Management Tests")
    inner class ConfigurationManagementTests {

        @Test
        @DisplayName("Should update configuration at runtime")
        fun `should update configuration at runtime`() {
            // Given
            val newConfig = mapOf(
                "apiKey" to "new-test-key",
                "baseUrl" to "https://new-api.test.com",
                "timeout" to "45000"
            )
            every { mockConfigService.updateConfig("ai", newConfig) } returns true

            // When
            val result = auraAIService.updateConfiguration(newConfig)

            // Then
            assertTrue(result)
            verify { mockConfigService.updateConfig("ai", newConfig) }
        }

        @Test
        @DisplayName("Should validate configuration before updating")
        fun `should validate configuration before updating`() {
            // Given
            val invalidConfig = mapOf("invalidKey" to "invalidValue")

            // When
            val result = auraAIService.updateConfiguration(invalidConfig)

            // Then
            assertFalse(result)
            verify(exactly = 0) { mockConfigService.updateConfig(any(), any()) }
        }

        @Test
        @DisplayName("Should get current configuration")
        fun `should get current configuration`() {
            // Given
            val expectedConfig = mapOf(
                "apiKey" to "current-key",
                "baseUrl" to "https://current-api.test.com",
                "timeout" to "30000"
            )
            every { mockConfigService.getConfig("ai") } returns expectedConfig

            // When
            val result = auraAIService.getCurrentConfiguration()

            // Then
            assertEquals(expectedConfig, result)
        }
    }

    @Nested
    @DisplayName("Service State Management Tests")
    inner class ServiceStateManagementTests {

        @Test
        @DisplayName("Should report correct service status")
        fun `should report correct service status`() {
            // Given
            val config = mapOf("apiKey" to "test", "baseUrl" to "https://test.com")
            every { mockConfigService.getConfig("ai") } returns config

            // When
            auraAIService.initialize()
            val status = auraAIService.getServiceStatus()

            // Then
            assertTrue(status.isHealthy)
            assertTrue(status.isInitialized)
            assertNotNull(status.lastHealthCheck)
        }

        @Test
        @DisplayName("Should perform health checks")
        fun `should perform health checks`() = runTest {
            // Given
            coEvery { mockApiClient.healthCheck() } returns true

            // When
            val result = auraAIService.performHealthCheck()

            // Then
            assertTrue(result)
            coVerify { mockApiClient.healthCheck() }
        }

        @Test
        @DisplayName("Should handle failed health checks")
        fun `should handle failed health checks`() = runTest {
            // Given
            coEvery { mockApiClient.healthCheck() } throws IOException("Service unavailable")

            // When
            val result = auraAIService.performHealthCheck()

            // Then
            assertFalse(result)
        }

        @Test
        @DisplayName("Should shutdown gracefully")
        fun `should shutdown gracefully`() = runTest {
            // Given
            val config = mapOf("apiKey" to "test", "baseUrl" to "https://test.com")
            every { mockConfigService.getConfig("ai") } returns config
            auraAIService.initialize()

            // When
            auraAIService.shutdown()

            // Then
            assertFalse(auraAIService.isInitialized())
            verify { mockLogger.info("AuraAI service shutting down") }
        }
    }

    @Nested
    @DisplayName("Error Handling and Recovery Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle service not initialized error")
        fun `should handle service not initialized error`() = runTest {
            // Given - service not initialized

            // When & Then
            assertThrows<IllegalStateException> {
                auraAIService.processQuery("Test query")
            }
        }

        @Test
        @DisplayName("Should handle authentication errors")
        fun `should handle authentication errors`() = runTest {
            // Given
            val config = mapOf("apiKey" to "invalid-key", "baseUrl" to "https://test.com")
            every { mockConfigService.getConfig("ai") } returns config
            auraAIService.initialize()

            coEvery { mockApiClient.sendQuery(any()) } throws AuthenticationException("Invalid API key")

            // When & Then
            assertThrows<ServiceException> {
                auraAIService.processQuery("Test query")
            }
        }

        @Test
        @DisplayName("Should handle quota exceeded errors")
        fun `should handle quota exceeded errors`() = runTest {
            // Given
            val config = mapOf("apiKey" to "test-key", "baseUrl" to "https://test.com")
            every { mockConfigService.getConfig("ai") } returns config
            auraAIService.initialize()

            coEvery { mockApiClient.sendQuery(any()) } throws QuotaExceededException("API quota exceeded")

            // When & Then
            assertThrows<ServiceException> {
                auraAIService.processQuery("Test query")
            }
        }

        @Test
        @DisplayName("Should recover from temporary network failures")
        fun `should recover from temporary network failures`() = runTest {
            // Given
            val config = mapOf("apiKey" to "test-key", "baseUrl" to "https://test.com")
            every { mockConfigService.getConfig("ai") } returns config
            auraAIService.initialize()

            val query = "Test query"
            val expectedResponse = AIResponse("Success after retry", 0.8, 15)

            coEvery { mockApiClient.sendQuery(any()) } throws
                    IOException("Network error") andThen
                    IOException("Network error") andThen
                    expectedResponse

            // When
            val result = auraAIService.processQuery(query)

            // Then
            assertEquals(expectedResponse, result)
            coVerify(exactly = 3) { mockApiClient.sendQuery(query) }
        }
    }

    @Nested
    @DisplayName("Performance and Resource Management Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle concurrent requests efficiently")
        fun `should handle concurrent requests efficiently`() = runTest {
            // Given
            val config = mapOf("apiKey" to "test-key", "baseUrl" to "https://test.com")
            every { mockConfigService.getConfig("ai") } returns config
            auraAIService.initialize()

            val queries = (1..10).map { "Query $it" }
            val responses = queries.map { AIResponse("Response for $it", 0.8, 10) }

            queries.zip(responses).forEach { (query, response) ->
                coEvery { mockApiClient.sendQuery(query) } returns response
            }

            // When
            val results = queries.map { query ->
                auraAIService.processQuery(query)
            }

            // Then
            assertEquals(10, results.size)
            results.zip(responses).forEach { (result, expected) ->
                assertEquals(expected, result)
            }
        }

        @Test
        @DisplayName("Should manage memory efficiently with large contexts")
        fun `should manage memory efficiently with large contexts`() = runTest {
            // Given
            val sessionId = "large-context-session"
            val largeContext = (1..1000).map { "Context item $it" }.joinToString(" ")

            // When
            auraAIService.storeContext(sessionId, largeContext)
            val retrievedContext = auraAIService.getContext(sessionId)

            // Then
            assertEquals(largeContext, retrievedContext.joinToString(" "))
        }

        @Test
        @DisplayName("Should implement proper timeout handling")
        fun `should implement proper timeout handling`() = runTest {
            // Given
            val config =
                mapOf("apiKey" to "test-key", "baseUrl" to "https://test.com", "timeout" to "1000")
            every { mockConfigService.getConfig("ai") } returns config
            auraAIService.initialize()

            coEvery { mockApiClient.sendQuery(any()) } throws TimeoutException("Request timeout")

            // When & Then
            assertThrows<ServiceException> {
                auraAIService.processQuery("Test query")
            }
        }
    }

    companion object {
        @JvmStatic
        fun provideTestQueries(): Stream<Arguments> = Stream.of(
            Arguments.of("Simple question", "Simple answer"),
            Arguments.of("Complex multi-part question with details", "Detailed response"),
            Arguments.of("Question with special characters !@#$%^&*()", "Response with handling"),
            Arguments.of("Unicode question with émojis 🤖", "Unicode response 🚀")
        )
    }
}

@Nested
@DisplayName("Advanced Configuration Validation Tests")
inner class AdvancedConfigurationValidationTests {

    @ParameterizedTest
    @CsvSource(
        "0, false",
        "-1, false",
        "1, true",
        "30000, true",
        "60000, true",
        "120000, false"
    )
    @DisplayName("Should validate timeout values correctly")
    fun `should validate timeout values correctly`(timeout: String, expectedValid: Boolean) {
        // Given
        val config = mapOf(
            "apiKey" to "test-key",
            "baseUrl" to "https://api.test.com",
            "timeout" to timeout
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val result = auraAIService.initialize()

        // Then
        assertEquals(expectedValid, result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "invalid-url", "ftp://invalid", "http://", "https://"])
    @DisplayName("Should validate base URL format")
    fun `should validate base URL format`(baseUrl: String) {
        // Given
        val config = mapOf(
            "apiKey" to "test-key",
            "baseUrl" to baseUrl,
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val result = auraAIService.initialize()

        // Then
        if (baseUrl.matches(Regex("^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*"))) {
            assertTrue(result)
        } else {
            assertFalse(result)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "x", "ab", "very-short"])
    @DisplayName("Should validate API key length requirements")
    fun `should validate API key length requirements`(apiKey: String) {
        // Given
        val config = mapOf(
            "apiKey" to apiKey,
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val result = auraAIService.initialize()

        // Then
        if (apiKey.trim().length >= 8) {
            assertTrue(result)
        } else {
            assertFalse(result)
        }
    }

    @Test
    @DisplayName("Should handle configuration with extra unknown fields")
    fun `should handle configuration with extra unknown fields`() {
        // Given
        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000",
            "unknownField1" to "value1",
            "unknownField2" to "value2"
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val result = auraAIService.initialize()

        // Then
        assertTrue(result)
        verify { mockLogger.warn(match { it.contains("unknown") }) }
    }

    @Test
    @DisplayName("Should handle configuration updates with partial data")
    fun `should handle configuration updates with partial data`() {
        // Given
        val initialConfig = mapOf(
            "apiKey" to "initial-key",
            "baseUrl" to "https://initial.test.com",
            "timeout" to "30000"
        )
        val partialUpdate = mapOf("timeout" to "45000")

        every { mockConfigService.getConfig("ai") } returns initialConfig
        every { mockConfigService.updateConfig("ai", partialUpdate) } returns true
        auraAIService.initialize()

        // When
        val result = auraAIService.updateConfiguration(partialUpdate)

        // Then
        assertTrue(result)
        verify { mockConfigService.updateConfig("ai", partialUpdate) }
    }
}

@Nested
@DisplayName("Advanced Query Processing Tests")
inner class AdvancedQueryProcessingTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should handle extremely large query payloads")
    fun `should handle extremely large query payloads`() = runTest {
        // Given
        val largeQuery = "x".repeat(10000)
        val response = AIResponse("Large response", 0.8, 5000)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        val result = auraAIService.processQuery(largeQuery)

        // Then
        assertEquals(response, result)
        assertTrue(result.tokensUsed > 1000)
    }

    @Test
    @DisplayName("Should handle queries with special Unicode characters")
    fun `should handle queries with special Unicode characters`() = runTest {
        // Given
        val unicodeQuery = "测试 🚀 émojis ñoño עברית العربية"
        val response = AIResponse("Unicode response", 0.9, 25)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        val result = auraAIService.processQuery(unicodeQuery)

        // Then
        assertEquals(response, result)
        coVerify { mockApiClient.sendQuery(unicodeQuery) }
    }

    @Test
    @DisplayName("Should handle malformed or corrupted responses gracefully")
    fun `should handle malformed or corrupted responses gracefully`() = runTest {
        // Given
        val query = "Test query"
        coEvery { mockApiClient.sendQuery(any()) } throws JsonParseException("Malformed response")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should handle response with zero confidence")
    fun `should handle response with zero confidence`() = runTest {
        // Given
        val query = "Ambiguous query"
        val lowConfidenceResponse = AIResponse("Uncertain response", 0.0, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns lowConfidenceResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(lowConfidenceResponse, result)
        assertEquals(0.0, result.confidence)
    }

    @Test
    @DisplayName("Should handle response with negative token count")
    fun `should handle response with negative token count`() = runTest {
        // Given
        val query = "Test query"
        val invalidResponse = AIResponse("Response", 0.8, -5)
        coEvery { mockApiClient.sendQuery(any()) } returns invalidResponse

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should handle queries with code injection attempts")
    fun `should handle queries with code injection attempts`() = runTest {
        // Given
        val maliciousQuery = "'; DROP TABLE users; --"
        val response = AIResponse("Sanitized response", 0.9, 15)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        val result = auraAIService.processQuery(maliciousQuery)

        // Then
        assertEquals(response, result)
        coVerify { mockApiClient.sendQuery(maliciousQuery) }
    }
}

@Nested
@DisplayName("Advanced Context Management Tests")
inner class AdvancedContextManagementTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should handle context overflow scenarios")
    fun `should handle context overflow scenarios`() = runTest {
        // Given
        val sessionId = "overflow-session"
        val maxContextSize = 1000
        val largeContext = (1..1500).map { "Item $it" }.joinToString(" ")

        // When
        auraAIService.storeContext(sessionId, largeContext)
        val retrievedContext = auraAIService.getContext(sessionId)

        // Then
        assertTrue(retrievedContext.size <= maxContextSize)
    }

    @Test
    @DisplayName("Should handle concurrent context modifications")
    fun `should handle concurrent context modifications`() = runTest {
        // Given
        val sessionId = "concurrent-session"
        val contexts = (1..10).map { "Context $it" }

        // When
        contexts.forEach { context ->
            auraAIService.storeContext(sessionId, context)
        }
        val finalContext = auraAIService.getContext(sessionId)

        // Then
        assertFalse(finalContext.isEmpty())
        assertTrue(finalContext.any { it.contains("Context") })
    }

    @Test
    @DisplayName("Should handle context with null or empty values")
    fun `should handle context with null or empty values`() = runTest {
        // Given
        val sessionId = "null-context-session"

        // When & Then
        assertThrows<IllegalArgumentException> {
            auraAIService.storeContext(sessionId, null)
        }

        assertThrows<IllegalArgumentException> {
            auraAIService.storeContext(sessionId, "")
        }
    }

    @Test
    @DisplayName("Should handle context expiration")
    fun `should handle context expiration`() = runTest {
        // Given
        val sessionId = "expiring-session"
        val context = "Expiring context"
        auraAIService.storeContext(sessionId, context)

        // When
        Thread.sleep(100) // Simulate time passing
        auraAIService.expireOldContexts()

        // Then
        assertTrue(auraAIService.getContext(sessionId).isEmpty())
    }

    @Test
    @DisplayName("Should handle invalid session IDs")
    fun `should handle invalid session IDs`() = runTest {
        // Given
        val invalidSessionIds = listOf("", "   ", null, "session@#$%", "session\nwith\nnewlines")

        // When & Then
        invalidSessionIds.forEach { sessionId ->
            assertThrows<IllegalArgumentException> {
                auraAIService.storeContext(sessionId, "Test context")
            }
        }
    }

    @Test
    @DisplayName("Should handle context retrieval for non-existent sessions")
    fun `should handle context retrieval for non-existent sessions`() = runTest {
        // Given
        val nonExistentSessionId = "non-existent-session"

        // When
        val context = auraAIService.getContext(nonExistentSessionId)

        // Then
        assertTrue(context.isEmpty())
    }
}

@Nested
@DisplayName("Advanced Error Handling and Recovery Tests")
inner class AdvancedErrorHandlingTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should handle cascading failures")
    fun `should handle cascading failures`() = runTest {
        // Given
        val query = "Test query"
        coEvery { mockApiClient.sendQuery(any()) } throws IOException("Network error")
        coEvery { mockApiClient.healthCheck() } throws IOException("Health check failed")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }

        val healthResult = auraAIService.performHealthCheck()
        assertFalse(healthResult)
    }

    @Test
    @DisplayName("Should handle out of memory errors")
    fun `should handle out of memory errors`() = runTest {
        // Given
        val query = "Memory intensive query"
        coEvery { mockApiClient.sendQuery(any()) } throws OutOfMemoryError("Heap space")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should handle service degradation gracefully")
    fun `should handle service degradation gracefully`() = runTest {
        // Given
        val query = "Test query"
        val degradedResponse = AIResponse("Degraded response", 0.3, 5)
        coEvery { mockApiClient.sendQuery(any()) } returns degradedResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(degradedResponse, result)
        assertTrue(result.confidence < 0.5)
        verify { mockLogger.warn(match { it.contains("degraded") }) }
    }

    @Test
    @DisplayName("Should handle interrupted operations")
    fun `should handle interrupted operations`() = runTest {
        // Given
        val query = "Long running query"
        coEvery { mockApiClient.sendQuery(any()) } throws InterruptedException("Operation interrupted")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should handle circuit breaker activation")
    fun `should handle circuit breaker activation`() = runTest {
        // Given
        val query = "Test query"
        repeat(5) {
            coEvery { mockApiClient.sendQuery(any()) } throws IOException("Network error")
            assertThrows<ServiceException> {
                auraAIService.processQuery(query)
            }
        }

        // When
        val status = auraAIService.getServiceStatus()

        // Then
        assertTrue(status.isCircuitBreakerOpen)
    }
}

@Nested
@DisplayName("Performance and Load Testing")
inner class PerformanceAndLoadTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should handle high-frequency requests")
    fun `should handle high-frequency requests`() = runTest {
        // Given
        val numberOfRequests = 100
        val queries = (1..numberOfRequests).map { "Query $it" }
        val responses = queries.map { AIResponse("Response for $it", 0.8, 10) }

        queries.zip(responses).forEach { (query, response) ->
            coEvery { mockApiClient.sendQuery(query) } returns response
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = queries.map { auraAIService.processQuery(it) }
        val endTime = System.currentTimeMillis()

        // Then
        assertEquals(numberOfRequests, results.size)
        val totalTime = endTime - startTime
        assertTrue(totalTime < 5000) // Should complete within 5 seconds
    }

    @Test
    @DisplayName("Should handle memory-intensive operations")
    fun `should handle memory-intensive operations`() = runTest {
        // Given
        val sessionId = "memory-intensive-session"
        val largeContextItems = (1..1000).map { "Large context item $it with lots of text content" }

        // When
        largeContextItems.forEach { item ->
            auraAIService.storeContext(sessionId, item)
        }

        val retrievedContext = auraAIService.getContext(sessionId)

        // Then
        assertFalse(retrievedContext.isEmpty())
        assertTrue(retrievedContext.size <= 1000)
    }

    @Test
    @DisplayName("Should handle burst traffic patterns")
    fun `should handle burst traffic patterns`() = runTest {
        // Given
        val burstSize = 50
        val queries = (1..burstSize).map { "Burst query $it" }
        val response = AIResponse("Burst response", 0.8, 10)

        queries.forEach { query ->
            coEvery { mockApiClient.sendQuery(query) } returns response
        }

        // When
        val results = queries.map { auraAIService.processQuery(it) }

        // Then
        assertEquals(burstSize, results.size)
        results.forEach { result ->
            assertEquals(response, result)
        }
    }

    @Test
    @DisplayName("Should maintain performance under sustained load")
    fun `should maintain performance under sustained load`() = runTest {
        // Given
        val sustainedRequests = 200
        val query = "Sustained load query"
        val response = AIResponse("Sustained response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        val results = mutableListOf<AIResponse>()
        repeat(sustainedRequests) {
            results.add(auraAIService.processQuery(query))
        }

        // Then
        assertEquals(sustainedRequests, results.size)
        results.forEach { result ->
            assertEquals(response, result)
        }
    }
}

@Nested
@DisplayName("Integration and Boundary Tests")
inner class IntegrationAndBoundaryTests {

    @Test
    @DisplayName("Should handle service initialization race conditions")
    fun `should handle service initialization race conditions`() = runTest {
        // Given
        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val initResults = (1..10).map {
            auraAIService.initialize()
        }

        // Then
        assertTrue(initResults.all { it })
        assertTrue(auraAIService.isInitialized())
    }

    @Test
    @DisplayName("Should handle boundary values for token limits")
    fun `should handle boundary values for token limits`() = runTest {
        // Given
        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config
        auraAIService.initialize()

        val query = "Boundary test query"
        val maxTokenResponse = AIResponse("Max tokens response", 0.8, 4096)
        coEvery { mockApiClient.sendQuery(any()) } returns maxTokenResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(maxTokenResponse, result)
        assertTrue(result.tokensUsed <= 4096)
    }

    @Test
    @DisplayName("Should handle service shutdown during active operations")
    fun `should handle service shutdown during active operations`() = runTest {
        // Given
        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config
        auraAIService.initialize()

        val query = "Long running query"
        coEvery { mockApiClient.sendQuery(any()) } coAnswers {
            Thread.sleep(1000)
            AIResponse("Delayed response", 0.8, 10)
        }

        // When
        auraAIService.shutdown()

        // Then
        assertFalse(auraAIService.isInitialized())
        assertThrows<IllegalStateException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should handle configuration changes during operation")
    fun `should handle configuration changes during operation`() = runTest {
        // Given
        val initialConfig = mapOf(
            "apiKey" to "initial-key",
            "baseUrl" to "https://initial.test.com",
            "timeout" to "30000"
        )
        val newConfig = mapOf(
            "apiKey" to "new-key",
            "baseUrl" to "https://new.test.com",
            "timeout" to "45000"
        )

        every { mockConfigService.getConfig("ai") } returns initialConfig
        every { mockConfigService.updateConfig("ai", newConfig) } returns true
        auraAIService.initialize()

        val query = "Test query"
        val response = AIResponse("Test response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.updateConfiguration(newConfig)
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(response, result)
        verify { mockConfigService.updateConfig("ai", newConfig) }
    }
}

@Nested
@DisplayName("Logging and Monitoring Tests")
inner class LoggingAndMonitoringTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should log successful operations")
    fun `should log successful operations`() = runTest {
        // Given
        val query = "Test query"
        val response = AIResponse("Test response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.processQuery(query)

        // Then
        verify { mockLogger.info(match { it.contains("processed successfully") }) }
    }

    @Test
    @DisplayName("Should log error conditions appropriately")
    fun `should log error conditions appropriately`() = runTest {
        // Given
        val query = "Test query"
        coEvery { mockApiClient.sendQuery(any()) } throws IOException("Network error")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }

        verify { mockLogger.error(match { it.contains("error") }) }
    }

    @Test
    @DisplayName("Should log performance metrics")
    fun `should log performance metrics`() = runTest {
        // Given
        val query = "Performance test query"
        val response = AIResponse("Performance response", 0.8, 100)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.processQuery(query)

        // Then
        verify { mockLogger.debug(match { it.contains("tokens") && it.contains("100") }) }
    }

    @Test
    @DisplayName("Should log configuration changes")
    fun `should log configuration changes`() {
        // Given
        val newConfig = mapOf(
            "apiKey" to "new-test-key",
            "baseUrl" to "https://new.test.com",
            "timeout" to "45000"
        )
        every { mockConfigService.updateConfig("ai", newConfig) } returns true

        // When
        auraAIService.updateConfiguration(newConfig)

        // Then
        verify { mockLogger.info(match { it.contains("configuration updated") }) }
    }

    @Test
    @DisplayName("Should log health check results")
    fun `should log health check results`() = runTest {
        // Given
        coEvery { mockApiClient.healthCheck() } returns true

        // When
        auraAIService.performHealthCheck()

        // Then
        verify { mockLogger.debug(match { it.contains("health check") }) }
    }
}
}
@Nested
@DisplayName("Security and Input Validation Tests")
inner class SecurityAndInputValidationTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should sanitize potentially dangerous input characters")
    fun `should sanitize potentially dangerous input characters`() = runTest {
        // Given
        val dangerousChars = listOf("<script>", "javascript:", "data:", "vbscript:", "onload=")
        val response = AIResponse("Sanitized response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When & Then
        dangerousChars.forEach { dangerousChar ->
            val query = "Test query with $dangerousChar"
            val result = auraAIService.processQuery(query)
            assertEquals(response, result)
            coVerify { mockApiClient.sendQuery(query) }
        }
    }

    @Test
    @DisplayName("Should handle extremely long API keys")
    fun `should handle extremely long API keys`() {
        // Given
        val extremelyLongKey = "x".repeat(10000)
        val config = mapOf(
            "apiKey" to extremelyLongKey,
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val result = auraAIService.initialize()

        // Then
        assertFalse(result) // Should fail due to unreasonably long key
        verify { mockLogger.warn(match { it.contains("key length") }) }
    }

    @Test
    @DisplayName("Should handle configuration with potential injection attacks")
    fun `should handle configuration with potential injection attacks`() {
        // Given
        val maliciousConfig = mapOf(
            "apiKey" to "test-key'; DROP TABLE config; --",
            "baseUrl" to "https://api.test.com/'; DELETE FROM users; --",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns maliciousConfig

        // When
        val result = auraAIService.initialize()

        // Then
        if (result) {
            // If initialization succeeds, the service should have sanitized the input
            verify { mockLogger.warn(match { it.contains("suspicious") }) }
        } else {
            // If initialization fails, it should be due to validation
            verify { mockLogger.error(match { it.contains("validation") }) }
        }
    }

    @Test
    @DisplayName("Should handle queries with control characters")
    fun `should handle queries with control characters`() = runTest {
        // Given
        val controlChars = listOf("\u0000", "\u0001", "\u0002", "\u001F", "\u007F")
        val response = AIResponse("Control char response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When & Then
        controlChars.forEach { controlChar ->
            val query = "Test${controlChar}query"
            val result = auraAIService.processQuery(query)
            assertEquals(response, result)
        }
    }

    @Test
    @DisplayName("Should validate session ID format for security")
    fun `should validate session ID format for security`() = runTest {
        // Given
        val maliciousSessionIds = listOf(
            "../../../etc/passwd",
            "session<script>alert('xss')</script>",
            "session'; DROP TABLE sessions; --",
            "session\u0000hidden",
            "session\r\nHost: evil.com"
        )

        // When & Then
        maliciousSessionIds.forEach { sessionId ->
            assertThrows<IllegalArgumentException> {
                auraAIService.storeContext(sessionId, "Test context")
            }
        }
    }
}

@Nested
@DisplayName("Resource Management and Cleanup Tests")
inner class ResourceManagementTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should properly clean up resources on shutdown")
    fun `should properly clean up resources on shutdown`() = runTest {
        // Given
        val sessionIds = (1..10).map { "session-$it" }
        sessionIds.forEach { sessionId ->
            auraAIService.storeContext(sessionId, "Context for $sessionId")
        }

        // When
        auraAIService.shutdown()

        // Then
        assertFalse(auraAIService.isInitialized())
        sessionIds.forEach { sessionId ->
            assertTrue(auraAIService.getContext(sessionId).isEmpty())
        }
        verify { mockLogger.info("Resources cleaned up successfully") }
    }

    @Test
    @DisplayName("Should handle resource cleanup when already shut down")
    fun `should handle resource cleanup when already shut down`() = runTest {
        // Given
        auraAIService.shutdown()

        // When
        auraAIService.shutdown() // Second shutdown call

        // Then
        assertFalse(auraAIService.isInitialized())
        verify(atMost = 1) { mockLogger.info("AuraAI service shutting down") }
    }

    @Test
    @DisplayName("Should handle memory pressure scenarios")
    fun `should handle memory pressure scenarios`() = runTest {
        // Given
        val sessionId = "memory-pressure-session"
        val largeContextItems = (1..500).map { "Large context item $it ".repeat(100) }

        // When
        largeContextItems.forEach { item ->
            auraAIService.storeContext(sessionId, item)
        }

        // Then
        val context = auraAIService.getContext(sessionId)
        assertTrue(context.isNotEmpty())
        verify { mockLogger.warn(match { it.contains("memory") }) }
    }

    @Test
    @DisplayName("Should handle context cleanup on service restart")
    fun `should handle context cleanup on service restart`() = runTest {
        // Given
        val sessionId = "restart-session"
        auraAIService.storeContext(sessionId, "Pre-restart context")

        // When
        auraAIService.shutdown()
        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config
        auraAIService.initialize()

        // Then
        assertTrue(auraAIService.getContext(sessionId).isEmpty())
    }

    @Test
    @DisplayName("Should handle resource allocation failures gracefully")
    fun `should handle resource allocation failures gracefully`() = runTest {
        // Given
        val sessionId = "allocation-failure-session"
        every { mockLogger.error(any()) } throws OutOfMemoryError("Cannot allocate memory")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.storeContext(sessionId, "Context that triggers allocation failure")
        }
    }
}

@Nested
@DisplayName("State Consistency and Thread Safety Tests")
inner class StateConsistencyTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should maintain state consistency during concurrent initialization")
    fun `should maintain state consistency during concurrent initialization`() = runTest {
        // Given
        auraAIService.shutdown()
        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config

        // When
        val initResults = (1..20).map {
            auraAIService.initialize()
        }

        // Then
        assertTrue(initResults.all { it })
        assertTrue(auraAIService.isInitialized())
    }

    @Test
    @DisplayName("Should handle concurrent context access safely")
    fun `should handle concurrent context access safely`() = runTest {
        // Given
        val sessionId = "concurrent-access-session"
        val contexts = (1..50).map { "Context item $it" }

        // When
        contexts.forEach { context ->
            auraAIService.storeContext(sessionId, context)
        }

        val retrievedContexts = (1..10).map {
            auraAIService.getContext(sessionId)
        }

        // Then
        retrievedContexts.forEach { context ->
            assertFalse(context.isEmpty())
        }
    }

    @Test
    @DisplayName("Should handle state transitions correctly")
    fun `should handle state transitions correctly`() = runTest {
        // Given
        val query = "State transition test"
        val response = AIResponse("State response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        assertTrue(auraAIService.isInitialized())
        val result1 = auraAIService.processQuery(query)

        auraAIService.shutdown()
        assertFalse(auraAIService.isInitialized())

        val config = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns config
        auraAIService.initialize()

        val result2 = auraAIService.processQuery(query)

        // Then
        assertEquals(response, result1)
        assertEquals(response, result2)
    }

    @Test
    @DisplayName("Should handle configuration state changes atomically")
    fun `should handle configuration state changes atomically`() = runTest {
        // Given
        val newConfig = mapOf(
            "apiKey" to "new-atomic-key",
            "baseUrl" to "https://new-atomic.test.com",
            "timeout" to "45000"
        )
        every { mockConfigService.updateConfig("ai", newConfig) } returns true

        // When
        val updateResult = auraAIService.updateConfiguration(newConfig)
        val currentConfig = auraAIService.getCurrentConfiguration()

        // Then
        assertTrue(updateResult)
        assertEquals(newConfig, currentConfig)
    }
}

@Nested
@DisplayName("Edge Case and Boundary Condition Tests")
inner class EdgeCaseTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should handle response with maximum confidence value")
    fun `should handle response with maximum confidence value`() = runTest {
        // Given
        val query = "Maximum confidence test"
        val maxConfidenceResponse = AIResponse("Max confidence response", 1.0, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns maxConfidenceResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(maxConfidenceResponse, result)
        assertEquals(1.0, result.confidence)
    }

    @Test
    @DisplayName("Should handle response with confidence greater than 1.0")
    fun `should handle response with confidence greater than 1_0`() = runTest {
        // Given
        val query = "Invalid confidence test"
        val invalidConfidenceResponse = AIResponse("Invalid confidence", 1.5, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns invalidConfidenceResponse

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should handle empty response content")
    fun `should handle empty response content`() = runTest {
        // Given
        val query = "Empty response test"
        val emptyResponse = AIResponse("", 0.8, 5)
        coEvery { mockApiClient.sendQuery(any()) } returns emptyResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(emptyResponse, result)
        assertTrue(result.content.isEmpty())
    }

    @Test
    @DisplayName("Should handle response with zero tokens")
    fun `should handle response with zero tokens`() = runTest {
        // Given
        val query = "Zero tokens test"
        val zeroTokensResponse = AIResponse("Zero tokens", 0.8, 0)
        coEvery { mockApiClient.sendQuery(any()) } returns zeroTokensResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(zeroTokensResponse, result)
        assertEquals(0, result.tokensUsed)
    }

    @Test
    @DisplayName("Should handle maximum integer token count")
    fun `should handle maximum integer token count`() = runTest {
        // Given
        val query = "Maximum tokens test"
        val maxTokensResponse = AIResponse("Max tokens", 0.8, Int.MAX_VALUE)
        coEvery { mockApiClient.sendQuery(any()) } returns maxTokensResponse

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertEquals(maxTokensResponse, result)
        assertEquals(Int.MAX_VALUE, result.tokensUsed)
    }

    @Test
    @DisplayName("Should handle context with single character")
    fun `should handle context with single character`() = runTest {
        // Given
        val sessionId = "single-char-session"
        val singleCharContext = "a"

        // When
        auraAIService.storeContext(sessionId, singleCharContext)
        val retrievedContext = auraAIService.getContext(sessionId)

        // Then
        assertEquals(listOf(singleCharContext), retrievedContext)
    }

    @Test
    @DisplayName("Should handle context with maximum unicode characters")
    fun `should handle context with maximum unicode characters`() = runTest {
        // Given
        val sessionId = "unicode-max-session"
        val unicodeContext = "\uD83D\uDE00\uD83D\uDE01\uD83D\uDE02" // Emoji characters

        // When
        auraAIService.storeContext(sessionId, unicodeContext)
        val retrievedContext = auraAIService.getContext(sessionId)

        // Then
        assertEquals(listOf(unicodeContext), retrievedContext)
    }
}

@Nested
@DisplayName("Mock Interaction and Verification Tests")
inner class MockInteractionTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should verify exact parameter matching in API calls")
    fun `should verify exact parameter matching in API calls`() = runTest {
        // Given
        val exactQuery = "Exact parameter test query"
        val response = AIResponse("Exact response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(exactQuery) } returns response

        // When
        val result = auraAIService.processQuery(exactQuery)

        // Then
        assertEquals(response, result)
        coVerify(exactly = 1) { mockApiClient.sendQuery(exactQuery) }
        coVerify(exactly = 0) { mockApiClient.sendQuery(not(exactQuery)) }
    }

    @Test
    @DisplayName("Should verify correct order of mock calls")
    fun `should verify correct order of mock calls`() = runTest {
        // Given
        val query = "Order verification test"
        val response = AIResponse("Order response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.processQuery(query)

        // Then
        coVerifyOrder {
            mockApiClient.sendQuery(query)
            mockLogger.info(match { it.contains("processed successfully") })
        }
    }

    @Test
    @DisplayName("Should verify configuration service interactions")
    fun `should verify configuration service interactions`() {
        // Given
        val newConfig = mapOf(
            "apiKey" to "verification-key",
            "baseUrl" to "https://verification.test.com",
            "timeout" to "45000"
        )
        every { mockConfigService.updateConfig("ai", newConfig) } returns true

        // When
        auraAIService.updateConfiguration(newConfig)

        // Then
        verifySequence {
            mockConfigService.updateConfig("ai", newConfig)
            mockLogger.info(match { it.contains("configuration updated") })
        }
    }

    @Test
    @DisplayName("Should verify logger interactions for different log levels")
    fun `should verify logger interactions for different log levels`() = runTest {
        // Given
        val query = "Logger interaction test"
        val response = AIResponse("Logger response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.processQuery(query)

        // Then
        verify(exactly = 1) { mockLogger.info(match { it.contains("processed successfully") }) }
        verify(exactly = 1) { mockLogger.debug(match { it.contains("tokens") }) }
        verify(exactly = 0) { mockLogger.error(any()) }
    }

    @Test
    @DisplayName("Should verify no unexpected mock interactions")
    fun `should verify no unexpected mock interactions`() = runTest {
        // Given
        val query = "No unexpected interactions test"
        val response = AIResponse("Clean response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.processQuery(query)

        // Then
        confirmVerified(mockApiClient, mockConfigService, mockLogger)
    }
}

@Nested
@DisplayName("Data Integrity and Validation Tests")
inner class DataIntegrityTests {

    @BeforeEach
    fun setUpInitializedService() {
        val validConfig = mapOf(
            "apiKey" to "test-key-12345",
            "baseUrl" to "https://api.test.com",
            "timeout" to "30000"
        )
        every { mockConfigService.getConfig("ai") } returns validConfig
        auraAIService.initialize()
    }

    @Test
    @DisplayName("Should preserve data integrity across multiple operations")
    fun `should preserve data integrity across multiple operations`() = runTest {
        // Given
        val sessionId = "integrity-session"
        val originalContext = "Original context data"
        val query = "Data integrity test"
        val response = AIResponse("Integrity response", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        auraAIService.storeContext(sessionId, originalContext)
        val beforeQuery = auraAIService.getContext(sessionId)
        auraAIService.processQuery(query)
        val afterQuery = auraAIService.getContext(sessionId)

        // Then
        assertEquals(beforeQuery, afterQuery)
        assertEquals(listOf(originalContext), afterQuery)
    }

    @Test
    @DisplayName("Should validate response data structure")
    fun `should validate response data structure`() = runTest {
        // Given
        val query = "Structure validation test"
        val response = AIResponse("Valid structure", 0.8, 10)
        coEvery { mockApiClient.sendQuery(any()) } returns response

        // When
        val result = auraAIService.processQuery(query)

        // Then
        assertNotNull(result.content)
        assertTrue(result.confidence >= 0.0 && result.confidence <= 1.0)
        assertTrue(result.tokensUsed >= 0)
    }

    @Test
    @DisplayName("Should handle data corruption scenarios")
    fun `should handle data corruption scenarios`() = runTest {
        // Given
        val query = "Corruption test"
        coEvery { mockApiClient.sendQuery(any()) } throws DataCorruptionException("Data corrupted")

        // When & Then
        assertThrows<ServiceException> {
            auraAIService.processQuery(query)
        }
    }

    @Test
    @DisplayName("Should maintain context ordering")
    fun `should maintain context ordering`() = runTest {
        // Given
        val sessionId = "ordering-session"
        val contexts = listOf("First", "Second", "Third", "Fourth")

        // When
        contexts.forEach { context ->
            auraAIService.storeContext(sessionId, context)
        }
        val retrievedContexts = auraAIService.getContext(sessionId)

        // Then
        assertEquals(contexts, retrievedContexts)
    }
}
}

// Additional companion object method for more complex test data
@JvmStatic
fun provideComplexTestScenarios(): Stream<Arguments> = Stream.of(
    Arguments.of("Multi-line\nquery\nwith\nbreaks", "Multi-line response"),
    Arguments.of("Query with\ttabs\tand\tspaces", "Tab-handled response"),
    Arguments.of("Query with \"quotes\" and 'apostrophes'", "Quote-handled response"),
    Arguments.of(
        "Query with [brackets] and {braces} and (parentheses)",
        "Bracket-handled response"
    ),
    Arguments.of("Query with numbers 123 and symbols !@#$%^&*()", "Symbol-handled response")
)

// Additional exception classes for testing if they don't exist
data class DataCorruptionException(override val message: String) : Exception(message)
data class JsonParseException(override val message: String) : Exception(message)
data class ApiRateLimitException(override val message: String) : Exception(message)
data class AuthenticationException(override val message: String) : Exception(message)
data class QuotaExceededException(override val message: String) : Exception(message)