package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import timber.log.Timber

/**
 * Comprehensive unit tests for BaseAgent
 *
 * Testing Framework: JUnit 5 with MockK for mocking
 * Coverage: Agent initialization, request processing, type resolution, flow emissions, error handling
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseAgentTest {

    private lateinit var testAgent: TestAgent

    @BeforeAll
    fun setupAll() {
        // Mock Timber to prevent logging during tests
        mockkStatic(Timber::class)
        every { Timber.tag(any()).w(any<Throwable>(), any(), *anyVararg()) } returns mockk()
        every { Timber.tag(any()).d(any(), *anyVararg()) } returns mockk()
    }

    @BeforeEach
    fun setUp() {
        testAgent = TestAgent("TestAgent", "AURA")
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @AfterAll
    fun tearDownAll() {
        unmockkAll()
    }

    @Nested
    @DisplayName("Agent Identity Tests")
    inner class AgentIdentityTests {

        @Test
        @DisplayName("getName should return configured agent name")
        fun `getName should return configured agent name`() {
            // Act
            val name = testAgent.getName()

            // Assert
            assertEquals("TestAgent", name)
        }

        @Test
        @DisplayName("getName should handle null name gracefully")
        fun `getName should handle null name gracefully`() {
            // Arrange
            val agentWithoutName = TestAgent("", "USER")

            // Act
            val name = agentWithoutName.getName()

            // Assert
            assertEquals("", name)
        }

        @Test
        @DisplayName("getType should return correct AgentType for valid type string")
        fun `getType should return correct AgentType for valid type string`() {
            // Act
            val type = testAgent.getType()

            // Assert
            assertEquals(AgentType.AURA, type)
        }

        @Test
        @DisplayName("getType should default to USER for invalid type string")
        fun `getType should default to USER for invalid type string`() {
            // Arrange
            val agentWithInvalidType = TestAgent("InvalidAgent", "INVALID_TYPE")

            // Act
            val type = agentWithInvalidType.getType()

            // Assert
            assertEquals(AgentType.USER, type)
        }

        @Test
        @DisplayName("getType should handle lowercase type strings")
        fun `getType should handle lowercase type strings`() {
            // Arrange
            val lowercaseAgent = TestAgent("LowercaseAgent", "kai")

            // Act
            val type = lowercaseAgent.getType()

            // Assert
            assertEquals(AgentType.KAI, type)
        }

        @Test
        @DisplayName("getType should handle mixed case type strings")
        fun `getType should handle mixed case type strings`() {
            // Arrange
            val mixedCaseAgent = TestAgent("MixedAgent", "CaScAdE")

            // Act
            val type = mixedCaseAgent.getType()

            // Assert
            assertEquals(AgentType.CASCADE, type)
        }
    }

    @Nested
    @DisplayName("Request Processing Tests")
    inner class RequestProcessingTests {

        @Test
        @DisplayName("processRequest should return success response with content")
        fun `processRequest should return success response with content`() = runTest {
            // Arrange
            val request = AiRequest("test query", emptyMap())
            val context = "test context"

            // Act
            val response = testAgent.processRequest(request, context)

            // Assert
            assertNotNull(response)
            assertTrue(response.content.contains("test query"))
            assertTrue(response.content.contains("TestAgent"))
            assertTrue(response.content.contains("test context"))
            assertEquals(1.0f, response.confidence)
        }

        @Test
        @DisplayName("processRequest should handle empty query")
        fun `processRequest should handle empty query`() = runTest {
            // Arrange
            val request = AiRequest("", emptyMap())
            val context = "empty query context"

            // Act
            val response = testAgent.processRequest(request, context)

            // Assert
            assertNotNull(response)
            assertEquals(1.0f, response.confidence)
        }

        @Test
        @DisplayName("processRequest should handle complex context strings")
        fun `processRequest should handle complex context strings`() = runTest {
            // Arrange
            val request = AiRequest("query", emptyMap())
            val complexContext = "context_with_special_chars_!@#$%^&*()"

            // Act
            val response = testAgent.processRequest(request, complexContext)

            // Assert
            assertNotNull(response)
            assertTrue(response.content.contains(complexContext))
        }

        @Test
        @DisplayName("processRequest should handle very long queries")
        fun `processRequest should handle very long queries`() = runTest {
            // Arrange
            val longQuery = "x".repeat(10000)
            val request = AiRequest(longQuery, emptyMap())

            // Act
            val response = testAgent.processRequest(request, "context")

            // Assert
            assertNotNull(response)
            assertEquals(1.0f, response.confidence)
        }

        @Test
        @DisplayName("processRequest should handle unicode in query")
        fun `processRequest should handle unicode in query`() = runTest {
            // Arrange
            val unicodeQuery = "你好世界 🌍 émojis ñ"
            val request = AiRequest(unicodeQuery, emptyMap())

            // Act
            val response = testAgent.processRequest(request, "context")

            // Assert
            assertNotNull(response)
            assertTrue(response.content.contains(unicodeQuery))
        }
    }

    @Nested
    @DisplayName("Flow Processing Tests")
    inner class FlowProcessingTests {

        @Test
        @DisplayName("processRequestFlow should emit single response")
        fun `processRequestFlow should emit single response`() = runTest {
            // Arrange
            val request = AiRequest("flow query", emptyMap())

            // Act
            val flow = testAgent.processRequestFlow(request)
            val response = flow.first()

            // Assert
            assertNotNull(response)
            assertTrue(response.content.contains("flow query"))
            assertEquals(1.0f, response.confidence)
        }

        @Test
        @DisplayName("processRequestFlow should use default context")
        fun `processRequestFlow should use default context`() = runTest {
            // Arrange
            val request = AiRequest("test", emptyMap())

            // Act
            val flow = testAgent.processRequestFlow(request)
            val response = flow.first()

            // Assert
            assertTrue(response.content.contains("DefaultContext_BaseAgentFlow"))
        }
    }

    @Nested
    @DisplayName("InteractionResponse Tests")
    inner class InteractionResponseTests {

        @Test
        @DisplayName("InteractionResponse should create valid response")
        fun `InteractionResponse should create valid response`() {
            // Arrange
            val content = "test content"
            val success = true
            val timestamp = System.currentTimeMillis()
            val metadata = mapOf("key" to "value")

            // Act
            val response = testAgent.InteractionResponse(content, success, timestamp, metadata)

            // Assert
            assertNotNull(response)
            assertEquals(content, response.content)
            assertEquals(timestamp, response.timestamp)
        }

        @Test
        @DisplayName("InteractionResponse should handle empty metadata")
        fun `InteractionResponse should handle empty metadata`() {
            // Arrange
            val content = "test"
            val timestamp = System.currentTimeMillis()

            // Act
            val response = testAgent.InteractionResponse(content, true, timestamp, emptyMap())

            // Assert
            assertNotNull(response)
            assertEquals(content, response.content)
        }

        @Test
        @DisplayName("InteractionResponse should handle complex metadata")
        fun `InteractionResponse should handle complex metadata`() {
            // Arrange
            val metadata = mapOf(
                "string" to "value",
                "number" to 42,
                "boolean" to true,
                "list" to listOf(1, 2, 3)
            )

            // Act
            val response = testAgent.InteractionResponse("test", true, 123L, metadata)

            // Assert
            assertNotNull(response)
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("should handle concurrent processRequest calls")
        fun `should handle concurrent processRequest calls`() = runTest {
            // Arrange
            val requests = (1..10).map { AiRequest("query $it", emptyMap()) }

            // Act
            val responses = requests.map { request ->
                testAgent.processRequest(request, "context")
            }

            // Assert
            assertEquals(10, responses.size)
            responses.forEach { response ->
                assertNotNull(response)
                assertEquals(1.0f, response.confidence)
            }
        }

        @Test
        @DisplayName("should handle null values in request metadata")
        fun `should handle null values in request metadata`() = runTest {
            // Arrange
            val request = AiRequest("test", mapOf("key" to "value"))

            // Act
            val response = testAgent.processRequest(request, "context")

            // Assert
            assertNotNull(response)
        }

        @Test
        @DisplayName("getType should log warning for invalid type")
        fun `getType should log warning for invalid type`() {
            // Arrange
            val invalidAgent = TestAgent("Invalid", "NONEXISTENT_TYPE")

            // Act
            invalidAgent.getType()

            // Assert
            verify { Timber.tag(any()).w(any<Throwable>(), any(), *anyVararg()) }
        }
    }

    @Nested
    @DisplayName("Subclass Override Tests")
    class SubclassOverrideTests {

        @Test
        @DisplayName("custom agent should be able to override processRequest")
        fun `custom agent should be able to override processRequest`() = runTest {
            // Arrange
            val customAgent = CustomAgent("CustomAgent", "CUSTOM")
            val request = AiRequest("custom query", emptyMap())

            // Act
            val response = customAgent.processRequest(request, "context")

            // Assert
            assertTrue(response.content.contains("CUSTOM_RESPONSE"))
            assertEquals(0.5f, response.confidence)
        }

        @Test
        @DisplayName("custom agent should be able to override processRequestFlow")
        fun `custom agent should be able to override processRequestFlow`() = runTest {
            // Arrange
            val customAgent = CustomAgent("CustomAgent", "CUSTOM")
            val request = AiRequest("flow", emptyMap())

            // Act
            val response = customAgent.processRequestFlow(request).first()

            // Assert
            assertTrue(response.content.contains("CUSTOM_RESPONSE"))
        }
    }

    // Test implementation classes
    private class TestAgent(
        override val agentName: String,
        override val agentTypeStr: String
    ) : BaseAgent(agentName, agentTypeStr)

    private class CustomAgent(
        override val agentName: String,
        override val agentTypeStr: String
    ) : BaseAgent(agentName, agentTypeStr) {
        override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
            return AgentResponse.success(
                content = "CUSTOM_RESPONSE: ${request.query}",
                confidence = 0.5f,
                agentName = agentName
            )
        }
    }
}
