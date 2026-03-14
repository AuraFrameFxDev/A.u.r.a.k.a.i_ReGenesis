package dev.aurakai.auraframefx.ai

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuraAIServiceImplTest {

    @Mock
    private lateinit var mockHttpClient: HttpClient

    @Mock
    private lateinit var mockConfiguration: AuraAIConfiguration

    @Mock
    private lateinit var mockTokenManager: TokenManager

    @Mock
    private lateinit var mockRateLimiter: RateLimiter

    private lateinit var auraAIService: AuraAIServiceImpl
    private lateinit var closeable: AutoCloseable

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)

        // Setup default mock behaviors
        whenever(mockConfiguration.apiKey).thenReturn("test-api-key")
        whenever(mockConfiguration.baseUrl).thenReturn("https://api.aurai.test")
        whenever(mockConfiguration.timeout).thenReturn(30.seconds)
        whenever(mockConfiguration.maxRetries).thenReturn(3)
        whenever(mockRateLimiter.tryAcquire()).thenReturn(true)
        whenever(mockTokenManager.getValidToken()).thenReturn("valid-token")

        auraAIService = AuraAIServiceImpl(
            httpClient = mockHttpClient,
            configuration = mockConfiguration,
            tokenManager = mockTokenManager,
            rateLimiter = mockRateLimiter
        )
    }

    @AfterEach
    fun tearDown() {
        closeable.close()
    }

    @Nested
    @DisplayName("Initialization Tests")
    inner class InitializationTests {

        @Test
        @DisplayName("Should initialize with valid configuration")
        fun shouldInitializeWithValidConfiguration() {
            assertNotNull(auraAIService)
            verify(mockConfiguration).apiKey
            verify(mockConfiguration).baseUrl
        }

        @Test
        @DisplayName("Should throw exception with null configuration")
        fun shouldThrowExceptionWithNullConfiguration() {
            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = null,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }

        @Test
        @DisplayName("Should throw exception with invalid API key")
        fun shouldThrowExceptionWithInvalidApiKey() {
            whenever(mockConfiguration.apiKey).thenReturn("")

            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = mockConfiguration,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }
    }

    @Nested
    @DisplayName("Generate Text Tests")
    inner class GenerateTextTests {

        @Test
        @DisplayName("Should generate text successfully with valid input")
        fun shouldGenerateTextSuccessfully() = runTest {
            val prompt = "Write a hello world program"
            val expectedResponse = "println(\"Hello, World!\")"
            val mockResponse = AIResponse(
                text = expectedResponse,
                usage = TokenUsage(promptTokens = 10, completionTokens = 15, totalTokens = 25),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(prompt)

            assertEquals(expectedResponse, result.text)
            verify(mockHttpClient).post(any(), any())
            verify(mockRateLimiter).tryAcquire()
        }

        @Test
        @DisplayName("Should handle empty prompt")
        fun shouldHandleEmptyPrompt() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("")
            }
        }

        @Test
        @DisplayName("Should handle null prompt")
        fun shouldHandleNullPrompt() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText(null)
            }
        }

        @Test
        @DisplayName("Should handle very long prompt")
        fun shouldHandleVeryLongPrompt() = runTest {
            val longPrompt = "A".repeat(100000)

            whenever(mockHttpClient.post(any(), any())).thenThrow(
                AIException("Prompt too long", AIErrorCode.PROMPT_TOO_LONG)
            )

            assertThrows<AIException> {
                auraAIService.generateText(longPrompt)
            }
        }

        @Test
        @DisplayName("Should handle rate limiting")
        fun shouldHandleRateLimiting() = runTest {
            whenever(mockRateLimiter.tryAcquire()).thenReturn(false)

            assertThrows<RateLimitExceededException> {
                auraAIService.generateText("test prompt")
            }
        }

        @Test
        @DisplayName("Should retry on transient failures")
        fun shouldRetryOnTransientFailures() = runTest {
            val prompt = "test prompt"
            val mockResponse = AIResponse(
                text = "response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(TransientException("Network error"))
                .thenThrow(TransientException("Server error"))
                .thenReturn(mockResponse)

            val result = auraAIService.generateText(prompt)

            assertEquals("response", result.text)
            verify(mockHttpClient, times(3)).post(any(), any())
        }

        @Test
        @DisplayName("Should fail after max retries")
        fun shouldFailAfterMaxRetries() = runTest {
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(TransientException("Persistent error"))

            assertThrows<AIException> {
                auraAIService.generateText("test prompt")
            }

            verify(mockHttpClient, times(4)).post(any(), any()) // initial + 3 retries
        }
    }

    @Nested
    @DisplayName("Generate Text with Parameters Tests")
    inner class GenerateTextWithParametersTests {

        @Test
        @DisplayName("Should generate text with custom parameters")
        fun shouldGenerateTextWithCustomParameters() = runTest {
            val prompt = "Generate code"
            val parameters = AIParameters(
                temperature = 0.7f,
                maxTokens = 1000,
                topP = 0.9f,
                presencePenalty = 0.1f,
                frequencyPenalty = 0.2f
            )

            val mockResponse = AIResponse(
                text = "Generated code here",
                usage = TokenUsage(20, 30, 50),
                model = "gpt-4"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(prompt, parameters)

            assertEquals("Generated code here", result.text)
            verify(mockHttpClient).post(any(), any())
        }

        @Test
        @DisplayName("Should validate temperature parameter")
        fun shouldValidateTemperatureParameter() = runTest {
            val parameters = AIParameters(temperature = 2.5f) // Invalid temperature

            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", parameters)
            }
        }

        @Test
        @DisplayName("Should validate max tokens parameter")
        fun shouldValidateMaxTokensParameter() = runTest {
            val parameters = AIParameters(maxTokens = -1) // Invalid max tokens

            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", parameters)
            }
        }

        @Test
        @DisplayName("Should validate top-p parameter")
        fun shouldValidateTopPParameter() = runTest {
            val parameters = AIParameters(topP = 1.5f) // Invalid top-p

            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", parameters)
            }
        }
    }

    @Nested
    @DisplayName("Async Operations Tests")
    inner class AsyncOperationsTests {

        @Test
        @DisplayName("Should handle async text generation")
        fun shouldHandleAsyncTextGeneration() = runTest {
            val prompt = "Async test"
            val mockResponse = AIResponse(
                text = "Async response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val future = auraAIService.generateTextAsync(prompt)
            val result = future.get(5, TimeUnit.SECONDS)

            assertEquals("Async response", result.text)
            assertTrue(future.isDone)
            assertFalse(future.isCancelled)
        }

        @Test
        @DisplayName("Should handle async operation timeout")
        fun shouldHandleAsyncOperationTimeout() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenAnswer {
                Thread.sleep(10000) // Simulate slow response
                AIResponse("", TokenUsage(0, 0, 0), "")
            }

            val future = auraAIService.generateTextAsync("test")

            assertThrows<TimeoutException> {
                future.get(1, TimeUnit.SECONDS)
            }
        }

        @Test
        @DisplayName("Should handle async operation cancellation")
        fun shouldHandleAsyncOperationCancellation() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenAnswer {
                Thread.sleep(5000) // Simulate slow response
                AIResponse("", TokenUsage(0, 0, 0), "")
            }

            val future = auraAIService.generateTextAsync("test")
            future.cancel(true)

            assertTrue(future.isCancelled)
        }
    }

    @Nested
    @DisplayName("Token Management Tests")
    inner class TokenManagementTests {

        @Test
        @DisplayName("Should refresh token when expired")
        fun shouldRefreshTokenWhenExpired() = runTest {
            whenever(mockTokenManager.getValidToken())
                .thenReturn("expired-token")
                .thenReturn("new-token")

            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(UnauthorizedException("Token expired"))
                .thenReturn(AIResponse("success", TokenUsage(5, 10, 15), "gpt-3.5-turbo"))

            val result = auraAIService.generateText("test")

            assertEquals("success", result.text)
            verify(mockTokenManager, times(2)).getValidToken()
        }

        @Test
        @DisplayName("Should handle token refresh failure")
        fun shouldHandleTokenRefreshFailure() = runTest {
            whenever(mockTokenManager.getValidToken())
                .thenThrow(TokenRefreshException("Cannot refresh token"))

            assertThrows<AuthenticationException> {
                auraAIService.generateText("test")
            }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle API quota exceeded")
        fun shouldHandleApiQuotaExceeded() = runTest {
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(QuotaExceededException("API quota exceeded"))

            assertThrows<QuotaExceededException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle server errors")
        fun shouldHandleServerErrors() = runTest {
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(ServerException("Internal server error", 500))

            assertThrows<AIException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle network connectivity issues")
        fun shouldHandleNetworkConnectivityIssues() = runTest {
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(NetworkException("Connection timeout"))

            assertThrows<AIException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle malformed responses")
        fun shouldHandleMalformedResponses() = runTest {
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(JsonParseException("Invalid JSON response"))

            assertThrows<AIException> {
                auraAIService.generateText("test")
            }
        }
    }

    @Nested
    @DisplayName("Configuration Tests")
    inner class ConfigurationTests {

        @Test
        @DisplayName("Should respect timeout configuration")
        fun shouldRespectTimeoutConfiguration() = runTest {
            whenever(mockConfiguration.timeout).thenReturn(1.seconds)

            whenever(mockHttpClient.post(any(), any())).thenAnswer {
                Thread.sleep(2000) // Simulate slow response
                AIResponse("", TokenUsage(0, 0, 0), "")
            }

            assertThrows<TimeoutException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should use configured base URL")
        fun shouldUseConfiguredBaseUrl() = runTest {
            val customUrl = "https://custom.api.url"
            whenever(mockConfiguration.baseUrl).thenReturn(customUrl)

            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse("response", TokenUsage(5, 10, 15), "gpt-3.5-turbo")
            )

            auraAIService.generateText("test")

            verify(mockHttpClient).post(contains(customUrl), any())
        }

        @Test
        @DisplayName("Should handle configuration updates")
        fun shouldHandleConfigurationUpdates() = runTest {
            val newConfig = mockConfiguration.copy(
                apiKey = "new-api-key",
                baseUrl = "https://new.api.url"
            )

            auraAIService.updateConfiguration(newConfig)

            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse("response", TokenUsage(5, 10, 15), "gpt-3.5-turbo")
            )

            auraAIService.generateText("test")

            verify(mockHttpClient).post(contains("https://new.api.url"), any())
        }
    }

    @Nested
    @DisplayName("Resource Management Tests")
    inner class ResourceManagementTests {

        @Test
        @DisplayName("Should cleanup resources on shutdown")
        fun shouldCleanupResourcesOnShutdown() = runTest {
            auraAIService.shutdown()

            verify(mockHttpClient).close()
            verify(mockTokenManager).cleanup()
            verify(mockRateLimiter).shutdown()
        }

        @Test
        @DisplayName("Should handle concurrent requests")
        fun shouldHandleConcurrentRequests() = runTest {
            val mockResponse = AIResponse(
                text = "concurrent response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val futures = (1..10).map { i ->
                auraAIService.generateTextAsync("test $i")
            }

            val results = futures.map { it.get(10, TimeUnit.SECONDS) }

            assertEquals(10, results.size)
            results.forEach { result ->
                assertEquals("concurrent response", result.text)
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Should handle unicode characters in prompt")
        fun shouldHandleUnicodeCharactersInPrompt() = runTest {
            val unicodePrompt = "Generate code with emojis 🚀🎯💻"
            val mockResponse = AIResponse(
                text = "// Code with emojis ✨",
                usage = TokenUsage(10, 15, 25),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(unicodePrompt)

            assertEquals("// Code with emojis ✨", result.text)
        }

        @Test
        @DisplayName("Should handle special characters in prompt")
        fun shouldHandleSpecialCharactersInPrompt() = runTest {
            val specialPrompt = "Generate code with special chars: \n\t\r\"'\\/"
            val mockResponse = AIResponse(
                text = "Code with special handling",
                usage = TokenUsage(15, 20, 35),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(specialPrompt)

            assertEquals("Code with special handling", result.text)
        }

        @Test
        @DisplayName("Should handle very large response")
        fun shouldHandleVeryLargeResponse() = runTest {
            val largeResponse = "A".repeat(50000)
            val mockResponse = AIResponse(
                text = largeResponse,
                usage = TokenUsage(100, 12500, 12600),
                model = "gpt-4"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("Generate large text")

            assertEquals(largeResponse, result.text)
            assertEquals(50000, result.text.length)
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should complete request within reasonable time")
        fun shouldCompleteRequestWithinReasonableTime() = runTest {
            val mockResponse = AIResponse(
                text = "Fast response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val startTime = System.currentTimeMillis()
            val result = auraAIService.generateText("Quick test")
            val endTime = System.currentTimeMillis()

            assertEquals("Fast response", result.text)
            assertTrue(endTime - startTime < 5000) // Should complete within 5 seconds
        }

        @Test
        @DisplayName("Should handle multiple sequential requests efficiently")
        fun shouldHandleMultipleSequentialRequestsEfficiency() = runTest {
            val mockResponse = AIResponse(
                text = "Sequential response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val startTime = System.currentTimeMillis()

            repeat(5) { i ->
                val result = auraAIService.generateText("Sequential test $i")
                assertEquals("Sequential response", result.text)
            }

            val endTime = System.currentTimeMillis()
            assertTrue(endTime - startTime < 10000) // Should complete within 10 seconds
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    inner class InputValidationTests {

        @Test
        @DisplayName("Should handle whitespace-only prompt")
        fun shouldHandleWhitespaceOnlyPrompt() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("   \n\t\r   ")
            }
        }

        @Test
        @DisplayName("Should handle prompt with only special characters")
        fun shouldHandlePromptWithOnlySpecialCharacters() = runTest {
            val specialPrompt = "!@#$%^&*()_+-=[]{}|;:,.<>?"
            val mockResponse = AIResponse(
                text = "Special character response",
                usage = TokenUsage(10, 15, 25),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(specialPrompt)

            assertEquals("Special character response", result.text)
        }

        @Test
        @DisplayName("Should validate presence penalty bounds")
        fun shouldValidatePresencePenaltyBounds() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(presencePenalty = 2.5f))
            }

            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(presencePenalty = -2.5f))
            }
        }

        @Test
        @DisplayName("Should validate frequency penalty bounds")
        fun shouldValidateFrequencyPenaltyBounds() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(frequencyPenalty = 3.0f))
            }

            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(frequencyPenalty = -3.0f))
            }
        }

        @Test
        @DisplayName("Should validate zero temperature parameter")
        fun shouldValidateZeroTemperatureParameter() = runTest {
            val parameters = AIParameters(temperature = 0.0f)
            val mockResponse = AIResponse(
                text = "Zero temperature response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("Zero temperature response", result.text)
        }

        @Test
        @DisplayName("Should validate zero max tokens parameter")
        fun shouldValidateZeroMaxTokensParameter() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(maxTokens = 0))
            }
        }

        @Test
        @DisplayName("Should validate negative top-p parameter")
        fun shouldValidateNegativeTopPParameter() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(topP = -0.1f))
            }
        }

        @Test
        @DisplayName("Should validate zero top-p parameter")
        fun shouldValidateZeroTopPParameter() = runTest {
            assertThrows<IllegalArgumentException> {
                auraAIService.generateText("test", AIParameters(topP = 0.0f))
            }
        }
    }

    @Nested
    @DisplayName("Threading and Concurrency Tests")
    inner class ThreadingAndConcurrencyTests {

        @Test
        @DisplayName("Should handle thread interruption gracefully")
        fun shouldHandleThreadInterruptionGracefully() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenAnswer {
                Thread.currentThread().interrupt()
                throw InterruptedException("Thread interrupted")
            }

            assertThrows<InterruptedException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle concurrent configuration updates")
        fun shouldHandleConcurrentConfigurationUpdates() = runTest {
            val mockResponse = AIResponse(
                text = "response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val futures = (1..5).map { i ->
                CompletableFuture.runAsync {
                    val newConfig = mockConfiguration.copy(
                        apiKey = "config-$i",
                        baseUrl = "https://api$i.test"
                    )
                    auraAIService.updateConfiguration(newConfig)
                }
            }

            futures.forEach { it.get(5, TimeUnit.SECONDS) }

            // Should not throw any concurrency exceptions
            val result = auraAIService.generateText("test")
            assertEquals("response", result.text)
        }

        @Test
        @DisplayName("Should handle concurrent token refresh requests")
        fun shouldHandleConcurrentTokenRefreshRequests() = runTest {
            whenever(mockTokenManager.getValidToken())
                .thenReturn("expired-token")
                .thenReturn("new-token")

            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(UnauthorizedException("Token expired"))
                .thenReturn(AIResponse("success", TokenUsage(5, 10, 15), "gpt-3.5-turbo"))

            val futures = (1..3).map {
                auraAIService.generateTextAsync("test $it")
            }

            val results = futures.map { it.get(10, TimeUnit.SECONDS) }

            assertEquals(3, results.size)
            results.forEach { result ->
                assertEquals("success", result.text)
            }
        }
    }

    @Nested
    @DisplayName("Memory and Resource Tests")
    inner class MemoryAndResourceTests {

        @Test
        @DisplayName("Should handle memory pressure gracefully")
        fun shouldHandleMemoryPressureGracefully() = runTest {
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(OutOfMemoryError("Java heap space"))

            assertThrows<OutOfMemoryError> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should not leak resources on repeated operations")
        fun shouldNotLeakResourcesOnRepeatedOperations() = runTest {
            val mockResponse = AIResponse(
                text = "response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            repeat(100) { i ->
                val result = auraAIService.generateText("test $i")
                assertEquals("response", result.text)
            }

            // Verify no resource leaks by checking mock invocations
            verify(mockHttpClient, times(100)).post(any(), any())
        }

        @Test
        @DisplayName("Should handle resource cleanup on service shutdown")
        fun shouldHandleResourceCleanupOnServiceShutdown() = runTest {
            // Start some async operations
            val futures = (1..5).map { i ->
                auraAIService.generateTextAsync("test $i")
            }

            // Shutdown service before operations complete
            auraAIService.shutdown()

            // Verify cleanup was called
            verify(mockHttpClient).close()
            verify(mockTokenManager).cleanup()
            verify(mockRateLimiter).shutdown()

            // Operations should be cancelled or completed
            futures.forEach { future ->
                assertTrue(future.isDone || future.isCancelled)
            }
        }
    }

    @Nested
    @DisplayName("Configuration Edge Cases Tests")
    inner class ConfigurationEdgeCasesTests {

        @Test
        @DisplayName("Should handle configuration with empty base URL")
        fun shouldHandleConfigurationWithEmptyBaseUrl() = runTest {
            whenever(mockConfiguration.baseUrl).thenReturn("")

            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = mockConfiguration,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }

        @Test
        @DisplayName("Should handle configuration with null API key")
        fun shouldHandleConfigurationWithNullApiKey() = runTest {
            whenever(mockConfiguration.apiKey).thenReturn(null)

            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = mockConfiguration,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }

        @Test
        @DisplayName("Should handle configuration with zero timeout")
        fun shouldHandleConfigurationWithZeroTimeout() = runTest {
            whenever(mockConfiguration.timeout).thenReturn(0.seconds)

            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = mockConfiguration,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }

        @Test
        @DisplayName("Should handle configuration with negative max retries")
        fun shouldHandleConfigurationWithNegativeMaxRetries() = runTest {
            whenever(mockConfiguration.maxRetries).thenReturn(-1)

            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = mockConfiguration,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }

        @Test
        @DisplayName("Should handle configuration with malformed base URL")
        fun shouldHandleConfigurationWithMalformedBaseUrl() = runTest {
            whenever(mockConfiguration.baseUrl).thenReturn("not-a-valid-url")

            assertThrows<IllegalArgumentException> {
                AuraAIServiceImpl(
                    httpClient = mockHttpClient,
                    configuration = mockConfiguration,
                    tokenManager = mockTokenManager,
                    rateLimiter = mockRateLimiter
                )
            }
        }
    }

    @Nested
    @DisplayName("Response Validation Tests")
    inner class ResponseValidationTests {

        @Test
        @DisplayName("Should handle response with null text")
        fun shouldHandleResponseWithNullText() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse(
                    text = null,
                    usage = TokenUsage(5, 10, 15),
                    model = "gpt-3.5-turbo"
                )
            )

            assertThrows<InvalidResponseException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle response with empty text")
        fun shouldHandleResponseWithEmptyText() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse(
                    text = "",
                    usage = TokenUsage(5, 10, 15),
                    model = "gpt-3.5-turbo"
                )
            )

            val result = auraAIService.generateText("test")

            assertEquals("", result.text)
        }

        @Test
        @DisplayName("Should handle response with null usage")
        fun shouldHandleResponseWithNullUsage() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse(
                    text = "response",
                    usage = null,
                    model = "gpt-3.5-turbo"
                )
            )

            assertThrows<InvalidResponseException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle response with negative token usage")
        fun shouldHandleResponseWithNegativeTokenUsage() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse(
                    text = "response",
                    usage = TokenUsage(-5, 10, 15),
                    model = "gpt-3.5-turbo"
                )
            )

            assertThrows<InvalidResponseException> {
                auraAIService.generateText("test")
            }
        }

        @Test
        @DisplayName("Should handle response with inconsistent token usage")
        fun shouldHandleResponseWithInconsistentTokenUsage() = runTest {
            whenever(mockHttpClient.post(any(), any())).thenReturn(
                AIResponse(
                    text = "response",
                    usage = TokenUsage(5, 10, 20), // total should be 15
                    model = "gpt-3.5-turbo"
                )
            )

            assertThrows<InvalidResponseException> {
                auraAIService.generateText("test")
            }
        }
    }

    @Nested
    @DisplayName("Stress Tests")
    inner class StressTests {

        @Test
        @DisplayName("Should handle rapid sequential requests")
        fun shouldHandleRapidSequentialRequests() = runTest {
            val mockResponse = AIResponse(
                text = "rapid response",
                usage = TokenUsage(1, 2, 3),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val results = mutableListOf<AIResponse>()
            val startTime = System.currentTimeMillis()

            repeat(50) { i ->
                val result = auraAIService.generateText("rapid test $i")
                results.add(result)
            }

            val endTime = System.currentTimeMillis()

            assertEquals(50, results.size)
            results.forEach { result ->
                assertEquals("rapid response", result.text)
            }

            // Should complete within reasonable time
            assertTrue(endTime - startTime < 30000)
        }

        @Test
        @DisplayName("Should handle mixed sync and async requests")
        fun shouldHandleMixedSyncAndAsyncRequests() = runTest {
            val mockResponse = AIResponse(
                text = "mixed response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val asyncFutures = (1..5).map { i ->
                auraAIService.generateTextAsync("async test $i")
            }

            val syncResults = (1..5).map { i ->
                auraAIService.generateText("sync test $i")
            }

            val asyncResults = asyncFutures.map { it.get(10, TimeUnit.SECONDS) }

            assertEquals(5, syncResults.size)
            assertEquals(5, asyncResults.size)

            (syncResults + asyncResults).forEach { result ->
                assertEquals("mixed response", result.text)
            }
        }
    }

    @Nested
    @DisplayName("Boundary Value Tests")
    inner class BoundaryValueTests {

        @Test
        @DisplayName("Should handle minimum valid temperature")
        fun shouldHandleMinimumValidTemperature() = runTest {
            val parameters = AIParameters(temperature = 0.01f)
            val mockResponse = AIResponse(
                text = "min temp response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("min temp response", result.text)
        }

        @Test
        @DisplayName("Should handle maximum valid temperature")
        fun shouldHandleMaximumValidTemperature() = runTest {
            val parameters = AIParameters(temperature = 2.0f)
            val mockResponse = AIResponse(
                text = "max temp response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("max temp response", result.text)
        }

        @Test
        @DisplayName("Should handle minimum valid max tokens")
        fun shouldHandleMinimumValidMaxTokens() = runTest {
            val parameters = AIParameters(maxTokens = 1)
            val mockResponse = AIResponse(
                text = "1",
                usage = TokenUsage(5, 1, 6),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("1", result.text)
        }

        @Test
        @DisplayName("Should handle maximum valid max tokens")
        fun shouldHandleMaximumValidMaxTokens() = runTest {
            val parameters = AIParameters(maxTokens = 4096)
            val mockResponse = AIResponse(
                text = "Large response within token limit",
                usage = TokenUsage(10, 4096, 4106),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("Large response within token limit", result.text)
        }

        @Test
        @DisplayName("Should handle minimum valid top-p")
        fun shouldHandleMinimumValidTopP() = runTest {
            val parameters = AIParameters(topP = 0.01f)
            val mockResponse = AIResponse(
                text = "min top-p response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("min top-p response", result.text)
        }

        @Test
        @DisplayName("Should handle maximum valid top-p")
        fun shouldHandleMaximumValidTopP() = runTest {
            val parameters = AIParameters(topP = 1.0f)
            val mockResponse = AIResponse(
                text = "max top-p response",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText("test", parameters)

            assertEquals("max top-p response", result.text)
        }
    }

    @Nested
    @DisplayName("Security Tests")
    inner class SecurityTests {

        @Test
        @DisplayName("Should handle malicious input injection attempts")
        fun shouldHandleMaliciousInputInjectionAttempts() = runTest {
            val maliciousPrompt = "'; DROP TABLE users; --"
            val mockResponse = AIResponse(
                text = "Safe response",
                usage = TokenUsage(10, 15, 25),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(maliciousPrompt)

            assertEquals("Safe response", result.text)
        }

        @Test
        @DisplayName("Should handle prompt with script injection attempts")
        fun shouldHandlePromptWithScriptInjectionAttempts() = runTest {
            val scriptPrompt = "<script>alert('xss')</script>"
            val mockResponse = AIResponse(
                text = "Sanitized response",
                usage = TokenUsage(10, 15, 25),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(scriptPrompt)

            assertEquals("Sanitized response", result.text)
        }

        @Test
        @DisplayName("Should handle API key exposure in logs")
        fun shouldHandleApiKeyExposureInLogs() = runTest {
            // This test ensures API keys are not exposed in error messages or logs
            whenever(mockHttpClient.post(any(), any()))
                .thenThrow(RuntimeException("Connection failed to https://api.aurai.test"))

            val exception = assertThrows<AIException> {
                auraAIService.generateText("test")
            }

            // Verify exception message doesn't contain API key
            assertFalse(exception.message?.contains("test-api-key") ?: false)
        }
    }

    @Nested
    @DisplayName("Integration-like Tests")
    inner class IntegrationLikeTests {

        @Test
        @DisplayName("Should handle full request-response cycle with all parameters")
        fun shouldHandleFullRequestResponseCycleWithAllParameters() = runTest {
            val prompt = "Generate a comprehensive code example"
            val parameters = AIParameters(
                temperature = 0.7f,
                maxTokens = 500,
                topP = 0.95f,
                presencePenalty = 0.1f,
                frequencyPenalty = 0.1f
            )

            val mockResponse = AIResponse(
                text = "Here's a comprehensive code example...",
                usage = TokenUsage(25, 500, 525),
                model = "gpt-4"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            val result = auraAIService.generateText(prompt, parameters)

            assertEquals("Here's a comprehensive code example...", result.text)
            assertEquals(25, result.usage.promptTokens)
            assertEquals(500, result.usage.completionTokens)
            assertEquals(525, result.usage.totalTokens)
            assertEquals("gpt-4", result.model)

            verify(mockHttpClient).post(any(), any())
            verify(mockRateLimiter).tryAcquire()
            verify(mockTokenManager).getValidToken()
        }

        @Test
        @DisplayName("Should handle service lifecycle from initialization to shutdown")
        fun shouldHandleServiceLifecycleFromInitializationToShutdown() = runTest {
            // Service is already initialized in setUp()

            // Perform some operations
            val mockResponse = AIResponse(
                text = "lifecycle test",
                usage = TokenUsage(5, 10, 15),
                model = "gpt-3.5-turbo"
            )

            whenever(mockHttpClient.post(any(), any())).thenReturn(mockResponse)

            // Generate text
            val result = auraAIService.generateText("test")
            assertEquals("lifecycle test", result.text)

            // Update configuration
            val newConfig = mockConfiguration.copy(apiKey = "new-key")
            auraAIService.updateConfiguration(newConfig)

            // Generate text again
            val result2 = auraAIService.generateText("test2")
            assertEquals("lifecycle test", result2.text)

            // Shutdown
            auraAIService.shutdown()

            // Verify all lifecycle methods were called
            verify(mockHttpClient, times(2)).post(any(), any())
            verify(mockHttpClient).close()
            verify(mockTokenManager).cleanup()
            verify(mockRateLimiter).shutdown()
        }
    }
}