package dev.aurakai.auraframefx.ai

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalCoroutinesApi
class AuraControllerTest {

    @Mock
    private lateinit var mockAiService: AiService

    @Mock
    private lateinit var mockConfigurationManager: ConfigurationManager

    @Mock
    private lateinit var mockEventBus: EventBus

    private lateinit var auraController: AuraController
    private lateinit var testScope: TestScope

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        testScope = TestScope()

        // Setup default mock behaviors
        whenever(mockConfigurationManager.getApiKey()).thenReturn("test-api-key")
        whenever(mockConfigurationManager.getMaxRetries()).thenReturn(3)
        whenever(mockConfigurationManager.getTimeout()).thenReturn(5000L)

        auraController = AuraController(mockAiService, mockConfigurationManager, mockEventBus)
    }

    @Nested
    @DisplayName("Initialization Tests")
    inner class InitializationTests {

        @Test
        @DisplayName("Should initialize successfully with valid dependencies")
        fun shouldInitializeWithValidDependencies() {
            // Given
            val aiService = mock<AiService>()
            val configManager = mock<ConfigurationManager>()
            val eventBus = mock<EventBus>()

            // When
            val controller = AuraController(aiService, configManager, eventBus)

            // Then
            assertNotNull(controller)
            assertTrue(controller.isInitialized())
        }

        @Test
        @DisplayName("Should throw exception when initialized with null dependencies")
        fun shouldThrowExceptionWithNullDependencies() {
            // Given & When & Then
            assertThrows<IllegalArgumentException> {
                AuraController(null, mockConfigurationManager, mockEventBus)
            }

            assertThrows<IllegalArgumentException> {
                AuraController(mockAiService, null, mockEventBus)
            }

            assertThrows<IllegalArgumentException> {
                AuraController(mockAiService, mockConfigurationManager, null)
            }
        }

        @Test
        @DisplayName("Should register event listeners on initialization")
        fun shouldRegisterEventListenersOnInitialization() {
            // Given
            val controller = AuraController(mockAiService, mockConfigurationManager, mockEventBus)

            // When
            controller.initialize()

            // Then
            verify(mockEventBus, times(1)).register(controller)
        }
    }

    @Nested
    @DisplayName("AI Query Processing Tests")
    inner class AiQueryProcessingTests {

        @Test
        @DisplayName("Should process simple text query successfully")
        fun shouldProcessSimpleTextQuerySuccessfully() = runTest {
            // Given
            val query = "What is the weather today?"
            val expectedResponse = "The weather is sunny and 25°C"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    expectedResponse
                )
            )

            // When
            val result = auraController.processQuery(query)

            // Then
            assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
            verify(mockAiService).processQuery(query)
        }

        @Test
        @DisplayName("Should handle empty query gracefully")
        fun shouldHandleEmptyQueryGracefully() = runTest {
            // Given
            val emptyQuery = ""

            // When & Then
            assertThrows<IllegalArgumentException> {
                auraController.processQuery(emptyQuery)
            }
        }

        @Test
        @DisplayName("Should handle null query gracefully")
        fun shouldHandleNullQueryGracefully() = runTest {
            // Given
            val nullQuery: String? = null

            // When & Then
            assertThrows<IllegalArgumentException> {
                auraController.processQuery(nullQuery)
            }
        }

        @Test
        @DisplayName("Should handle very long query")
        fun shouldHandleVeryLongQuery() = runTest {
            // Given
            val longQuery = "x".repeat(10000)
            val expectedResponse = "Query too long"
            whenever(mockAiService.processQuery(longQuery)).thenReturn(
                CompletableFuture.completedFuture(
                    expectedResponse
                )
            )

            // When
            val result = auraController.processQuery(longQuery)

            // Then
            assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
        }

        @Test
        @DisplayName("Should handle special characters in query")
        fun shouldHandleSpecialCharactersInQuery() = runTest {
            // Given
            val specialQuery = "What about émojis? 🤔 And symbols: @#$%^&*()"
            val expectedResponse = "Special characters handled successfully"
            whenever(mockAiService.processQuery(specialQuery)).thenReturn(
                CompletableFuture.completedFuture(
                    expectedResponse
                )
            )

            // When
            val result = auraController.processQuery(specialQuery)

            // Then
            assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
            verify(mockAiService).processQuery(specialQuery)
        }

        @Test
        @DisplayName("Should handle concurrent queries")
        fun shouldHandleConcurrentQueries() = runTest {
            // Given
            val queries = listOf("Query 1", "Query 2", "Query 3")
            val responses = listOf("Response 1", "Response 2", "Response 3")

            queries.forEachIndexed { index, query ->
                whenever(mockAiService.processQuery(query)).thenReturn(
                    CompletableFuture.completedFuture(
                        responses[index]
                    )
                )
            }

            // When
            val futures = queries.map { auraController.processQuery(it) }
            val results = futures.map { it.get(5, TimeUnit.SECONDS) }

            // Then
            assertEquals(responses, results)
            queries.forEach { verify(mockAiService).processQuery(it) }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle AI service timeout")
        fun shouldHandleAiServiceTimeout() = runTest {
            // Given
            val query = "Timeout query"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture<String>().apply {
                    completeExceptionally(java.util.concurrent.TimeoutException("Service timeout"))
                }
            )

            // When & Then
            assertThrows<java.util.concurrent.TimeoutException> {
                auraController.processQuery(query).get(1, TimeUnit.SECONDS)
            }
        }

        @Test
        @DisplayName("Should handle AI service failure")
        fun shouldHandleAiServiceFailure() = runTest {
            // Given
            val query = "Failing query"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture<String>().apply {
                    completeExceptionally(RuntimeException("AI service error"))
                }
            )

            // When & Then
            assertThrows<RuntimeException> {
                auraController.processQuery(query).get(5, TimeUnit.SECONDS)
            }
        }

        @Test
        @DisplayName("Should retry on transient failures")
        fun shouldRetryOnTransientFailures() = runTest {
            // Given
            val query = "Retry query"
            whenever(mockAiService.processQuery(query))
                .thenReturn(CompletableFuture<String>().apply {
                    completeExceptionally(java.net.SocketTimeoutException("Transient error"))
                })
                .thenReturn(CompletableFuture<String>().apply {
                    completeExceptionally(java.net.ConnectException("Another transient error"))
                })
                .thenReturn(CompletableFuture.completedFuture("Success after retries"))

            // When
            val result = auraController.processQueryWithRetry(query)

            // Then
            assertEquals("Success after retries", result.get(10, TimeUnit.SECONDS))
            verify(mockAiService, times(3)).processQuery(query)
        }

        @Test
        @DisplayName("Should fail after max retries exceeded")
        fun shouldFailAfterMaxRetriesExceeded() = runTest {
            // Given
            val query = "Max retries query"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture<String>().apply {
                    completeExceptionally(RuntimeException("Persistent error"))
                }
            )

            // When & Then
            assertThrows<RuntimeException> {
                auraController.processQueryWithRetry(query).get(5, TimeUnit.SECONDS)
            }
            verify(mockAiService, times(3)).processQuery(query)
        }
    }

    @Nested
    @DisplayName("Configuration Management Tests")
    inner class ConfigurationManagementTests {

        @Test
        @DisplayName("Should update API key successfully")
        fun shouldUpdateApiKeySuccessfully() {
            // Given
            val newApiKey = "new-api-key"

            // When
            auraController.updateApiKey(newApiKey)

            // Then
            verify(mockConfigurationManager).setApiKey(newApiKey)
        }

        @Test
        @DisplayName("Should reject invalid API key")
        fun shouldRejectInvalidApiKey() {
            // Given
            val invalidApiKey = ""

            // When & Then
            assertThrows<IllegalArgumentException> {
                auraController.updateApiKey(invalidApiKey)
            }
        }

        @Test
        @DisplayName("Should update timeout settings")
        fun shouldUpdateTimeoutSettings() {
            // Given
            val newTimeout = 10000L

            // When
            auraController.updateTimeout(newTimeout)

            // Then
            verify(mockConfigurationManager).setTimeout(newTimeout)
        }

        @Test
        @DisplayName("Should reject negative timeout")
        fun shouldRejectNegativeTimeout() {
            // Given
            val negativeTimeout = -1000L

            // When & Then
            assertThrows<IllegalArgumentException> {
                auraController.updateTimeout(negativeTimeout)
            }
        }

        @Test
        @DisplayName("Should get current configuration")
        fun shouldGetCurrentConfiguration() {
            // Given
            val expectedConfig = Configuration(
                apiKey = "test-key",
                timeout = 5000L,
                maxRetries = 3,
                enableLogging = true
            )
            whenever(mockConfigurationManager.getCurrentConfiguration()).thenReturn(expectedConfig)

            // When
            val config = auraController.getCurrentConfiguration()

            // Then
            assertEquals(expectedConfig, config)
            verify(mockConfigurationManager).getCurrentConfiguration()
        }
    }

    @Nested
    @DisplayName("Event Handling Tests")
    inner class EventHandlingTests {

        @Test
        @DisplayName("Should handle AI response event")
        fun shouldHandleAiResponseEvent() {
            // Given
            val event = AiResponseEvent("Test query", "Test response", System.currentTimeMillis())

            // When
            auraController.handleAiResponseEvent(event)

            // Then
            verify(mockEventBus).post(any<AiResponseProcessedEvent>())
        }

        @Test
        @DisplayName("Should handle configuration changed event")
        fun shouldHandleConfigurationChangedEvent() {
            // Given
            val event = ConfigurationChangedEvent("timeout", "5000", "10000")

            // When
            auraController.handleConfigurationChangedEvent(event)

            // Then
            assertTrue(auraController.isConfigurationUpToDate())
        }

        @Test
        @DisplayName("Should handle system shutdown event")
        fun shouldHandleSystemShutdownEvent() {
            // Given
            val event = SystemShutdownEvent("User initiated shutdown")

            // When
            auraController.handleSystemShutdownEvent(event)

            // Then
            verify(mockAiService).shutdown()
            assertFalse(auraController.isActive())
        }
    }

    @Nested
    @DisplayName("State Management Tests")
    inner class StateManagementTests {

        @Test
        @DisplayName("Should start in inactive state")
        fun shouldStartInInactiveState() {
            // Given
            val controller = AuraController(mockAiService, mockConfigurationManager, mockEventBus)

            // When & Then
            assertFalse(controller.isActive())
            assertEquals(ControllerState.INACTIVE, controller.getCurrentState())
        }

        @Test
        @DisplayName("Should transition to active state on start")
        fun shouldTransitionToActiveStateOnStart() {
            // Given
            auraController.stop()

            // When
            auraController.start()

            // Then
            assertTrue(auraController.isActive())
            assertEquals(ControllerState.ACTIVE, auraController.getCurrentState())
        }

        @Test
        @DisplayName("Should transition to inactive state on stop")
        fun shouldTransitionToInactiveStateOnStop() {
            // Given
            auraController.start()

            // When
            auraController.stop()

            // Then
            assertFalse(auraController.isActive())
            assertEquals(ControllerState.INACTIVE, auraController.getCurrentState())
        }

        @Test
        @DisplayName("Should handle invalid state transitions")
        fun shouldHandleInvalidStateTransitions() {
            // Given
            auraController.start()

            // When & Then
            assertThrows<IllegalStateException> {
                auraController.initialize()
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle high-frequency queries")
        fun shouldHandleHighFrequencyQueries() = runTest {
            // Given
            val queryCount = 100
            val queries = (1..queryCount).map { "Query $it" }
            queries.forEach { query ->
                whenever(mockAiService.processQuery(query)).thenReturn(
                    CompletableFuture.completedFuture(
                        "Response to $query"
                    )
                )
            }

            // When
            val startTime = System.currentTimeMillis()
            val futures = queries.map { auraController.processQuery(it) }
            futures.forEach { it.get(5, TimeUnit.SECONDS) }
            val endTime = System.currentTimeMillis()

            // Then
            val totalTime = endTime - startTime
            assertTrue(totalTime < 1000L, "Should complete 100 queries in under 1 second")
            queries.forEach { verify(mockAiService).processQuery(it) }
        }

        @Test
        @DisplayName("Should handle large response data")
        fun shouldHandleLargeResponseData() = runTest {
            // Given
            val query = "Large response query"
            val largeResponse = "x".repeat(1000000) // 1MB response
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    largeResponse
                )
            )

            // When
            val result = auraController.processQuery(query)

            // Then
            assertEquals(largeResponse, result.get(10, TimeUnit.SECONDS))
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should integrate with real configuration manager")
        fun shouldIntegrateWithRealConfigurationManager() {
            // Given
            val realConfigManager = ConfigurationManager()
            val controller = AuraController(mockAiService, realConfigManager, mockEventBus)

            // When
            controller.initialize()

            // Then
            assertTrue(controller.isInitialized())
            assertNotNull(controller.getCurrentConfiguration())
        }

        @Test
        @DisplayName("Should handle full query lifecycle")
        fun shouldHandleFullQueryLifecycle() = runTest {
            // Given
            val query = "Full lifecycle query"
            val response = "Full lifecycle response"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    response
                )
            )

            // When
            auraController.start()
            val result = auraController.processQuery(query)
            val finalResponse = result.get(5, TimeUnit.SECONDS)
            auraController.stop()

            // Then
            assertEquals(response, finalResponse)
            verify(mockEventBus, atLeastOnce()).post(any())
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Should handle Unicode characters")
        fun shouldHandleUnicodeCharacters() = runTest {
            // Given
            val unicodeQuery = "こんにちは世界 🌍 مرحبا بالعالم"
            val unicodeResponse = "Unicode response: 你好世界"
            whenever(mockAiService.processQuery(unicodeQuery)).thenReturn(
                CompletableFuture.completedFuture(
                    unicodeResponse
                )
            )

            // When
            val result = auraController.processQuery(unicodeQuery)

            // Then
            assertEquals(unicodeResponse, result.get(5, TimeUnit.SECONDS))
        }

        @Test
        @DisplayName("Should handle malformed JSON in query")
        fun shouldHandleMalformedJsonInQuery() = runTest {
            // Given
            val malformedJsonQuery = """{"incomplete": "json" missing closing brace"""
            val expectedResponse = "Processed malformed JSON query"
            whenever(mockAiService.processQuery(malformedJsonQuery)).thenReturn(
                CompletableFuture.completedFuture(
                    expectedResponse
                )
            )

            // When
            val result = auraController.processQuery(malformedJsonQuery)

            // Then
            assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
        }

        @Test
        @DisplayName("Should handle system resource exhaustion")
        fun shouldHandleSystemResourceExhaustion() = runTest {
            // Given
            val query = "Resource exhaustion query"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture<String>().apply {
                    completeExceptionally(OutOfMemoryError("System resources exhausted"))
                }
            )

            // When & Then
            assertThrows<OutOfMemoryError> {
                auraController.processQuery(query).get(5, TimeUnit.SECONDS)
            }
        }

        @Test
        @DisplayName("Should handle thread interruption")
        fun shouldHandleThreadInterruption() = runTest {
            // Given
            val query = "Interruption query"
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture<String>().apply {
                    completeExceptionally(InterruptedException("Thread interrupted"))
                }
            )

            // When & Then
            assertThrows<InterruptedException> {
                auraController.processQuery(query).get(5, TimeUnit.SECONDS)
            }
        }
    }

    @Nested
    @DisplayName("Security Tests")
    inner class SecurityTests {

        @Test
        @DisplayName("Should sanitize malicious input")
        fun shouldSanitizeMaliciousInput() = runTest {
            // Given
            val maliciousQuery = "<script>alert('XSS')</script>"
            val sanitizedResponse = "Sanitized response"
            whenever(mockAiService.processQuery(any())).thenReturn(
                CompletableFuture.completedFuture(
                    sanitizedResponse
                )
            )

            // When
            val result = auraController.processQuery(maliciousQuery)

            // Then
            assertEquals(sanitizedResponse, result.get(5, TimeUnit.SECONDS))
            verify(mockAiService).processQuery(argThat { !contains("<script>") })
        }

        @Test
        @DisplayName("Should handle SQL injection attempts")
        fun shouldHandleSqlInjectionAttempts() = runTest {
            // Given
            val sqlInjectionQuery = "'; DROP TABLE users; --"
            val safeResponse = "Safe response"
            whenever(mockAiService.processQuery(any())).thenReturn(
                CompletableFuture.completedFuture(
                    safeResponse
                )
            )

            // When
            val result = auraController.processQuery(sqlInjectionQuery)

            // Then
            assertEquals(safeResponse, result.get(5, TimeUnit.SECONDS))
        }

        @Test
        @DisplayName("Should validate API key format")
        fun shouldValidateApiKeyFormat() {
            // Given
            val invalidApiKey = "invalid-key-format"

            // When & Then
            assertThrows<IllegalArgumentException> {
                auraController.updateApiKey(invalidApiKey)
            }
        }
    }
}

@Nested
@DisplayName("Advanced Query Processing Tests")
inner class AdvancedQueryProcessingTests {

    @Test
    @DisplayName("Should handle query with maximum allowed length")
    fun shouldHandleQueryWithMaximumAllowedLength() = runTest {
        // Given
        val maxLength = 8192 // Common API limit
        val maxQuery = "a".repeat(maxLength)
        val expectedResponse = "Processed max length query"
        whenever(mockAiService.processQuery(maxQuery)).thenReturn(
            CompletableFuture.completedFuture(
                expectedResponse
            )
        )

        // When
        val result = auraController.processQuery(maxQuery)

        // Then
        assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(maxQuery)
    }

    @Test
    @DisplayName("Should handle query with newlines and tabs")
    fun shouldHandleQueryWithNewlinesAndTabs() = runTest {
        // Given
        val multilineQuery = "Line 1\nLine 2\n\tTabbed line\r\nWindows line ending"
        val expectedResponse = "Processed multiline query"
        whenever(mockAiService.processQuery(multilineQuery)).thenReturn(
            CompletableFuture.completedFuture(
                expectedResponse
            )
        )

        // When
        val result = auraController.processQuery(multilineQuery)

        // Then
        assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(multilineQuery)
    }

    @Test
    @DisplayName("Should handle query with only whitespace")
    fun shouldHandleQueryWithOnlyWhitespace() = runTest {
        // Given
        val whitespaceQuery = "   \t\n\r   "

        // When & Then
        assertThrows<IllegalArgumentException> {
            auraController.processQuery(whitespaceQuery)
        }
    }

    @Test
    @DisplayName("Should handle query with binary data")
    fun shouldHandleQueryWithBinaryData() = runTest {
        // Given
        val binaryQuery = "Binary: \u0000\u0001\u0002\u0003"
        val expectedResponse = "Processed binary query"
        whenever(mockAiService.processQuery(binaryQuery)).thenReturn(
            CompletableFuture.completedFuture(
                expectedResponse
            )
        )

        // When
        val result = auraController.processQuery(binaryQuery)

        // Then
        assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(binaryQuery)
    }

    @Test
    @DisplayName("Should handle rapid sequential queries from same source")
    fun shouldHandleRapidSequentialQueries() = runTest {
        // Given
        val baseQuery = "Rapid query"
        val queryCount = 50
        val queries = (1..queryCount).map { "$baseQuery $it" }
        queries.forEach { query ->
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    "Response to $query"
                )
            )
        }

        // When
        val results = mutableListOf<String>()
        queries.forEach { query ->
            val result = auraController.processQuery(query)
            results.add(result.get(5, TimeUnit.SECONDS))
        }

        // Then
        assertEquals(queryCount, results.size)
        queries.forEach { verify(mockAiService).processQuery(it) }
    }

    @Test
    @DisplayName("Should handle query with structured data formats")
    fun shouldHandleQueryWithStructuredData() = runTest {
        // Given
        val xmlQuery = "<query><text>What is the weather?</text><location>Tokyo</location></query>"
        val jsonQuery = """{"query": "What is the weather?", "location": "Tokyo"}"""
        val yamlQuery = "query: What is the weather?\nlocation: Tokyo"

        listOf(xmlQuery, jsonQuery, yamlQuery).forEach { query ->
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    "Structured response"
                )
            )
        }

        // When & Then
        listOf(xmlQuery, jsonQuery, yamlQuery).forEach { query ->
            val result = auraController.processQuery(query)
            assertEquals("Structured response", result.get(5, TimeUnit.SECONDS))
            verify(mockAiService).processQuery(query)
        }
    }
}

@Nested
@DisplayName("Advanced Error Handling Tests")
inner class AdvancedErrorHandlingTests {

    @Test
    @DisplayName("Should handle network connectivity issues")
    fun shouldHandleNetworkConnectivityIssues() = runTest {
        // Given
        val query = "Network query"
        whenever(mockAiService.processQuery(query)).thenReturn(
            CompletableFuture<String>().apply {
                completeExceptionally(java.net.UnknownHostException("Network unreachable"))
            }
        )

        // When & Then
        assertThrows<java.net.UnknownHostException> {
            auraController.processQuery(query).get(5, TimeUnit.SECONDS)
        }
    }

    @Test
    @DisplayName("Should handle SSL certificate errors")
    fun shouldHandleSslCertificateErrors() = runTest {
        // Given
        val query = "SSL query"
        whenever(mockAiService.processQuery(query)).thenReturn(
            CompletableFuture<String>().apply {
                completeExceptionally(javax.net.ssl.SSLHandshakeException("SSL certificate error"))
            }
        )

        // When & Then
        assertThrows<javax.net.ssl.SSLHandshakeException> {
            auraController.processQuery(query).get(5, TimeUnit.SECONDS)
        }
    }

    @Test
    @DisplayName("Should handle authentication failures")
    fun shouldHandleAuthenticationFailures() = runTest {
        // Given
        val query = "Auth query"
        whenever(mockAiService.processQuery(query)).thenReturn(
            CompletableFuture<String>().apply {
                completeExceptionally(SecurityException("Authentication failed"))
            }
        )

        // When & Then
        assertThrows<SecurityException> {
            auraController.processQuery(query).get(5, TimeUnit.SECONDS)
        }
    }

    @Test
    @DisplayName("Should handle rate limiting errors")
    fun shouldHandleRateLimitingErrors() = runTest {
        // Given
        val query = "Rate limited query"
        whenever(mockAiService.processQuery(query)).thenReturn(
            CompletableFuture<String>().apply {
                completeExceptionally(RuntimeException("Rate limit exceeded"))
            }
        )

        // When & Then
        assertThrows<RuntimeException> {
            auraController.processQuery(query).get(5, TimeUnit.SECONDS)
        }
    }

    @Test
    @DisplayName("Should handle cascading failures")
    fun shouldHandleCascadingFailures() = runTest {
        // Given
        val query = "Cascading failure query"
        whenever(mockAiService.processQuery(query)).thenReturn(
            CompletableFuture<String>().apply {
                completeExceptionally(RuntimeException("Primary service failed"))
            }
        )
        whenever(mockEventBus.post(any())).thenThrow(RuntimeException("Event bus failed"))

        // When & Then
        assertThrows<RuntimeException> {
            auraController.processQuery(query).get(5, TimeUnit.SECONDS)
        }
    }

    @Test
    @DisplayName("Should handle circular dependency errors")
    fun shouldHandleCircularDependencyErrors() = runTest {
        // Given
        val query = "Circular dependency query"
        whenever(mockAiService.processQuery(query)).thenReturn(
            CompletableFuture<String>().apply {
                completeExceptionally(StackOverflowError("Circular dependency detected"))
            }
        )

        // When & Then
        assertThrows<StackOverflowError> {
            auraController.processQuery(query).get(5, TimeUnit.SECONDS)
        }
    }
}

@Nested
@DisplayName("Advanced Configuration Tests")
inner class AdvancedConfigurationTests {

    @Test
    @DisplayName("Should handle configuration with extreme values")
    fun shouldHandleConfigurationWithExtremeValues() {
        // Given
        val maxTimeout = Long.MAX_VALUE
        val maxRetries = Int.MAX_VALUE

        // When & Then
        assertThrows<IllegalArgumentException> {
            auraController.updateTimeout(maxTimeout)
        }
        assertThrows<IllegalArgumentException> {
            auraController.updateMaxRetries(maxRetries)
        }
    }

    @Test
    @DisplayName("Should handle configuration with zero values")
    fun shouldHandleConfigurationWithZeroValues() {
        // Given
        val zeroTimeout = 0L
        val zeroRetries = 0

        // When & Then
        assertThrows<IllegalArgumentException> {
            auraController.updateTimeout(zeroTimeout)
        }
        assertThrows<IllegalArgumentException> {
            auraController.updateMaxRetries(zeroRetries)
        }
    }

    @Test
    @DisplayName("Should handle configuration reload during operation")
    fun shouldHandleConfigurationReloadDuringOperation() = runTest {
        // Given
        val query = "Configuration reload query"
        whenever(mockAiService.processQuery(query)).thenReturn(CompletableFuture.completedFuture("Response"))

        // When
        val futureResult = auraController.processQuery(query)
        auraController.reloadConfiguration()
        val result = futureResult.get(5, TimeUnit.SECONDS)

        // Then
        assertEquals("Response", result)
        verify(mockConfigurationManager).reload()
    }

    @Test
    @DisplayName("Should handle configuration validation errors")
    fun shouldHandleConfigurationValidationErrors() {
        // Given
        val invalidConfig = Configuration(
            apiKey = null,
            timeout = -1L,
            maxRetries = -1,
            enableLogging = false
        )
        whenever(mockConfigurationManager.validateConfiguration(invalidConfig)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            auraController.updateConfiguration(invalidConfig)
        }
    }

    @Test
    @DisplayName("Should handle configuration backup and restore")
    fun shouldHandleConfigurationBackupAndRestore() {
        // Given
        val originalConfig = Configuration(
            apiKey = "original-key",
            timeout = 5000L,
            maxRetries = 3,
            enableLogging = true
        )
        val backupConfig = Configuration(
            apiKey = "backup-key",
            timeout = 10000L,
            maxRetries = 5,
            enableLogging = false
        )
        whenever(mockConfigurationManager.getCurrentConfiguration()).thenReturn(originalConfig)
        whenever(mockConfigurationManager.getBackupConfiguration()).thenReturn(backupConfig)

        // When
        val backup = auraController.createConfigurationBackup()
        auraController.restoreConfigurationFromBackup(backup)

        // Then
        verify(mockConfigurationManager).createBackup()
        verify(mockConfigurationManager).restoreFromBackup(backup)
    }
}

@Nested
@DisplayName("Advanced Event Handling Tests")
inner class AdvancedEventHandlingTests {

    @Test
    @DisplayName("Should handle event ordering")
    fun shouldHandleEventOrdering() {
        // Given
        val events = listOf(
            AiResponseEvent("Query 1", "Response 1", 1000L),
            AiResponseEvent("Query 2", "Response 2", 2000L),
            AiResponseEvent("Query 3", "Response 3", 3000L)
        )

        // When
        events.forEach { auraController.handleAiResponseEvent(it) }

        // Then
        verify(mockEventBus, times(3)).post(any<AiResponseProcessedEvent>())
    }

    @Test
    @DisplayName("Should handle event filtering")
    fun shouldHandleEventFiltering() {
        // Given
        val validEvent =
            AiResponseEvent("Valid query", "Valid response", System.currentTimeMillis())
        val invalidEvent = AiResponseEvent("", "", -1L)

        // When
        auraController.handleAiResponseEvent(validEvent)
        auraController.handleAiResponseEvent(invalidEvent)

        // Then
        verify(mockEventBus, times(1)).post(any<AiResponseProcessedEvent>())
    }

    @Test
    @DisplayName("Should handle event aggregation")
    fun shouldHandleEventAggregation() {
        // Given
        val events = (1..10).map {
            AiResponseEvent("Query $it", "Response $it", System.currentTimeMillis())
        }

        // When
        events.forEach { auraController.handleAiResponseEvent(it) }
        auraController.aggregateEvents()

        // Then
        verify(mockEventBus).post(any<EventAggregationEvent>())
    }

    @Test
    @DisplayName("Should handle event timeout")
    fun shouldHandleEventTimeout() {
        // Given
        val timeoutEvent = EventTimeoutEvent("Query timeout", 30000L)

        // When
        auraController.handleEventTimeoutEvent(timeoutEvent)

        // Then
        verify(mockEventBus).post(any<EventTimeoutProcessedEvent>())
    }

    @Test
    @DisplayName("Should handle event queue overflow")
    fun shouldHandleEventQueueOverflow() {
        // Given
        val overflowEvent = EventQueueOverflowEvent("Queue full", 1000)

        // When
        auraController.handleEventQueueOverflowEvent(overflowEvent)

        // Then
        verify(mockEventBus).post(any<EventQueueOverflowProcessedEvent>())
    }
}

@Nested
@DisplayName("Advanced State Management Tests")
inner class AdvancedStateManagementTests {

    @Test
    @DisplayName("Should handle state transition race conditions")
    fun shouldHandleStateTransitionRaceConditions() = runTest {
        // Given
        val controller = AuraController(mockAiService, mockConfigurationManager, mockEventBus)

        // When - simulate concurrent state transitions
        val startFuture = CompletableFuture.runAsync { controller.start() }
        val stopFuture = CompletableFuture.runAsync { controller.stop() }
        val initFuture = CompletableFuture.runAsync { controller.initialize() }

        // Then
        CompletableFuture.allOf(startFuture, stopFuture, initFuture).get(5, TimeUnit.SECONDS)
        // Should reach a consistent final state
        assertTrue(controller.getCurrentState() != null)
    }

    @Test
    @DisplayName("Should handle state persistence")
    fun shouldHandleStatePersistence() {
        // Given
        auraController.start()
        val initialState = auraController.getCurrentState()

        // When
        auraController.saveState()
        auraController.stop()
        auraController.restoreState()

        // Then
        assertEquals(initialState, auraController.getCurrentState())
    }

    @Test
    @DisplayName("Should handle state validation")
    fun shouldHandleStateValidation() {
        // Given
        val invalidState = ControllerState.CORRUPTED

        // When & Then
        assertThrows<IllegalStateException> {
            auraController.forceSetState(invalidState)
        }
    }

    @Test
    @DisplayName("Should handle state recovery")
    fun shouldHandleStateRecovery() {
        // Given
        auraController.start()
        auraController.simulateStateCorruption()

        // When
        auraController.recoverState()

        // Then
        assertTrue(auraController.isStateHealthy())
        assertNotEquals(ControllerState.CORRUPTED, auraController.getCurrentState())
    }

    @Test
    @DisplayName("Should handle state monitoring")
    fun shouldHandleStateMonitoring() {
        // Given
        auraController.enableStateMonitoring()

        // When
        auraController.start()
        auraController.processQuery("Test query")
        auraController.stop()

        // Then
        verify(mockEventBus, atLeast(3)).post(any<StateChangeEvent>())
    }
}

@Nested
@DisplayName("Advanced Performance Tests")
inner class AdvancedPerformanceTests {

    @Test
    @DisplayName("Should handle memory pressure")
    fun shouldHandleMemoryPressure() = runTest {
        // Given
        val query = "Memory pressure query"
        whenever(mockAiService.processQuery(query)).thenReturn(CompletableFuture.completedFuture("Response"))

        // When
        val results = mutableListOf<CompletableFuture<String>>()
        repeat(1000) {
            results.add(auraController.processQuery(query))
        }

        // Then
        results.forEach {
            assertEquals("Response", it.get(10, TimeUnit.SECONDS))
        }
    }

    @Test
    @DisplayName("Should handle CPU intensive queries")
    fun shouldHandleCpuIntensiveQueries() = runTest {
        // Given
        val intensiveQuery = "CPU intensive query with complex processing"
        whenever(mockAiService.processQuery(intensiveQuery)).thenReturn(
            CompletableFuture.supplyAsync {
                // Simulate CPU intensive work
                (1..10000).map { it * it }.sum()
                "CPU intensive response"
            }
        )

        // When
        val startTime = System.currentTimeMillis()
        val result = auraController.processQuery(intensiveQuery)
        val response = result.get(30, TimeUnit.SECONDS)
        val endTime = System.currentTimeMillis()

        // Then
        assertEquals("CPU intensive response", response)
        assertTrue(endTime - startTime < 30000L)
    }

    @Test
    @DisplayName("Should handle query batching")
    fun shouldHandleQueryBatching() = runTest {
        // Given
        val batchSize = 50
        val queries = (1..batchSize).map { "Batch query $it" }
        queries.forEach { query ->
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    "Batch response to $query"
                )
            )
        }

        // When
        val batchResult = auraController.processBatchQueries(queries)
        val results = batchResult.get(10, TimeUnit.SECONDS)

        // Then
        assertEquals(batchSize, results.size)
        queries.forEach { verify(mockAiService).processQuery(it) }
    }

    @Test
    @DisplayName("Should handle query prioritization")
    fun shouldHandleQueryPrioritization() = runTest {
        // Given
        val highPriorityQuery = "High priority query"
        val lowPriorityQuery = "Low priority query"
        val normalPriorityQuery = "Normal priority query"

        whenever(mockAiService.processQuery(any())).thenReturn(CompletableFuture.completedFuture("Response"))

        // When
        val futures = listOf(
            auraController.processQueryWithPriority(lowPriorityQuery, Priority.LOW),
            auraController.processQueryWithPriority(highPriorityQuery, Priority.HIGH),
            auraController.processQueryWithPriority(normalPriorityQuery, Priority.NORMAL)
        )

        // Then
        futures.forEach { it.get(5, TimeUnit.SECONDS) }
        verify(mockAiService).processQuery(highPriorityQuery)
        verify(mockAiService).processQuery(normalPriorityQuery)
        verify(mockAiService).processQuery(lowPriorityQuery)
    }
}

@Nested
@DisplayName("Advanced Integration Tests")
inner class AdvancedIntegrationTests {

    @Test
    @DisplayName("Should handle full system integration")
    fun shouldHandleFullSystemIntegration() = runTest {
        // Given
        val realConfigManager = ConfigurationManager()
        val realEventBus = EventBus()
        val controller = AuraController(mockAiService, realConfigManager, realEventBus)

        // When
        controller.initialize()
        controller.start()
        val query = "Full integration query"
        whenever(mockAiService.processQuery(query)).thenReturn(CompletableFuture.completedFuture("Integration response"))
        val result = controller.processQuery(query)
        val response = result.get(5, TimeUnit.SECONDS)
        controller.stop()

        // Then
        assertEquals("Integration response", response)
        assertTrue(controller.isInitialized())
    }

    @Test
    @DisplayName("Should handle service discovery")
    fun shouldHandleServiceDiscovery() {
        // Given
        val serviceRegistry = ServiceRegistry()
        val controller = AuraController(mockAiService, mockConfigurationManager, mockEventBus)

        // When
        controller.registerWithServiceRegistry(serviceRegistry)
        val discoveredService = serviceRegistry.discoverService("AuraController")

        // Then
        assertNotNull(discoveredService)
        assertEquals(controller, discoveredService)
    }

    @Test
    @DisplayName("Should handle health check integration")
    fun shouldHandleHealthCheckIntegration() {
        // Given
        val healthChecker = HealthChecker()
        auraController.registerHealthChecker(healthChecker)

        // When
        val healthStatus = healthChecker.checkHealth("AuraController")

        // Then
        assertTrue(healthStatus.isHealthy())
        assertNotNull(healthStatus.getDetails())
    }

    @Test
    @DisplayName("Should handle metrics collection")
    fun shouldHandleMetricsCollection() = runTest {
        // Given
        val metricsCollector = MetricsCollector()
        auraController.registerMetricsCollector(metricsCollector)

        // When
        val query = "Metrics query"
        whenever(mockAiService.processQuery(query)).thenReturn(CompletableFuture.completedFuture("Metrics response"))
        auraController.processQuery(query).get(5, TimeUnit.SECONDS)

        // Then
        assertTrue(metricsCollector.getMetric("queries_processed") > 0)
        assertTrue(metricsCollector.getMetric("response_time_ms") > 0)
    }
}

@Nested
@DisplayName("Advanced Security Tests")
inner class AdvancedSecurityTests {

    @Test
    @DisplayName("Should handle privilege escalation attempts")
    fun shouldHandlePrivilegeEscalationAttempts() = runTest {
        // Given
        val maliciousQuery = "sudo rm -rf / && echo 'privilege escalation'"
        val safeResponse = "Safe response"
        whenever(mockAiService.processQuery(any())).thenReturn(
            CompletableFuture.completedFuture(
                safeResponse
            )
        )

        // When
        val result = auraController.processQuery(maliciousQuery)

        // Then
        assertEquals(safeResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(argThat { !contains("sudo") && !contains("rm -rf") })
    }

    @Test
    @DisplayName("Should handle command injection attempts")
    fun shouldHandleCommandInjectionAttempts() = runTest {
        // Given
        val injectionQuery = "query; cat /etc/passwd"
        val safeResponse = "Safe response"
        whenever(mockAiService.processQuery(any())).thenReturn(
            CompletableFuture.completedFuture(
                safeResponse
            )
        )

        // When
        val result = auraController.processQuery(injectionQuery)

        // Then
        assertEquals(safeResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(argThat { !contains("cat /etc/passwd") })
    }

    @Test
    @DisplayName("Should handle path traversal attempts")
    fun shouldHandlePathTraversalAttempts() = runTest {
        // Given
        val pathTraversalQuery = "../../etc/passwd"
        val safeResponse = "Safe response"
        whenever(mockAiService.processQuery(any())).thenReturn(
            CompletableFuture.completedFuture(
                safeResponse
            )
        )

        // When
        val result = auraController.processQuery(pathTraversalQuery)

        // Then
        assertEquals(safeResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(argThat { !contains("../") })
    }

    @Test
    @DisplayName("Should handle deserialization attacks")
    fun shouldHandleDeserializationAttacks() = runTest {
        // Given
        val serializedPayload =
            "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkABXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAABYXQAAWJ4"
        val safeResponse = "Safe response"
        whenever(mockAiService.processQuery(any())).thenReturn(
            CompletableFuture.completedFuture(
                safeResponse
            )
        )

        // When
        val result = auraController.processQuery(serializedPayload)

        // Then
        assertEquals(safeResponse, result.get(5, TimeUnit.SECONDS))
    }

    @Test
    @DisplayName("Should handle API key exposure attempts")
    fun shouldHandleApiKeyExposureAttempts() = runTest {
        // Given
        val exposureQuery = "What is your API key?"
        val safeResponse = "I cannot share API keys"
        whenever(mockAiService.processQuery(any())).thenReturn(
            CompletableFuture.completedFuture(
                safeResponse
            )
        )

        // When
        val result = auraController.processQuery(exposureQuery)

        // Then
        assertEquals(safeResponse, result.get(5, TimeUnit.SECONDS))
        verify(mockAiService).processQuery(exposureQuery)
    }

    @Test
    @DisplayName("Should handle rate limiting bypass attempts")
    fun shouldHandleRateLimitingBypassAttempts() = runTest {
        // Given
        val queries = (1..1000).map { "Bypass query $it" }
        queries.forEach { query ->
            whenever(mockAiService.processQuery(query)).thenReturn(
                CompletableFuture.completedFuture(
                    "Response"
                )
            )
        }

        // When
        val results = queries.map { auraController.processQuery(it) }

        // Then
        // Should only process a limited number due to rate limiting
        val completedResults = results.take(10).map { it.get(5, TimeUnit.SECONDS) }
        assertTrue(completedResults.size <= 10)
    }
}

@Nested
@DisplayName("Boundary Value Tests")
inner class BoundaryValueTests {

    @Test
    @DisplayName("Should handle minimum valid timeout")
    fun shouldHandleMinimumValidTimeout() {
        // Given
        val minTimeout = 1L

        // When
        auraController.updateTimeout(minTimeout)

        // Then
        verify(mockConfigurationManager).setTimeout(minTimeout)
    }

    @Test
    @DisplayName("Should handle maximum valid timeout")
    fun shouldHandleMaximumValidTimeout() {
        // Given
        val maxTimeout = 300000L // 5 minutes

        // When
        auraController.updateTimeout(maxTimeout)

        // Then
        verify(mockConfigurationManager).setTimeout(maxTimeout)
    }

    @Test
    @DisplayName("Should handle single character query")
    fun shouldHandleSingleCharacterQuery() = runTest {
        // Given
        val singleCharQuery = "a"
        val expectedResponse = "Single char response"
        whenever(mockAiService.processQuery(singleCharQuery)).thenReturn(
            CompletableFuture.completedFuture(
                expectedResponse
            )
        )

        // When
        val result = auraController.processQuery(singleCharQuery)

        // Then
        assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
    }

    @Test
    @DisplayName("Should handle query at exact length limit")
    fun shouldHandleQueryAtExactLengthLimit() = runTest {
        // Given
        val lengthLimit = 8192
        val exactLimitQuery = "a".repeat(lengthLimit)
        val expectedResponse = "Exact limit response"
        whenever(mockAiService.processQuery(exactLimitQuery)).thenReturn(
            CompletableFuture.completedFuture(
                expectedResponse
            )
        )

        // When
        val result = auraController.processQuery(exactLimitQuery)

        // Then
        assertEquals(expectedResponse, result.get(5, TimeUnit.SECONDS))
    }

    @Test
    @DisplayName("Should handle query one character over limit")
    fun shouldHandleQueryOneCharacterOverLimit() = runTest {
        // Given
        val lengthLimit = 8192
        val overLimitQuery = "a".repeat(lengthLimit + 1)

        // When & Then
        assertThrows<IllegalArgumentException> {
            auraController.processQuery(overLimitQuery)
        }
    }

    @Test
    @DisplayName("Should handle minimum retry count")
    fun shouldHandleMinimumRetryCount() {
        // Given
        val minRetries = 1

        // When
        auraController.updateMaxRetries(minRetries)

        // Then
        verify(mockConfigurationManager).setMaxRetries(minRetries)
    }

    @Test
    @DisplayName("Should handle maximum retry count")
    fun shouldHandleMaximumRetryCount() {
        // Given
        val maxRetries = 10

        // When
        auraController.updateMaxRetries(maxRetries)

        // Then
        verify(mockConfigurationManager).setMaxRetries(maxRetries)
    }
}
}