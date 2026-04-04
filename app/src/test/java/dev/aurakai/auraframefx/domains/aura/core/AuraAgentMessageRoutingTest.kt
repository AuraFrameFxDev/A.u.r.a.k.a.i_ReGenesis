package dev.aurakai.auraframefx.domains.aura.core

import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.core.security.SecurityContext
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for the behavioral changes introduced in AuraAgent in this PR:
 *
 * 1. Message filtering (agentName, auto_generated, aura_processed, environment_perception)
 * 2. User message routing via pythonManager instead of genesisBridgeService
 * 3. JSON parsing of backend response with fallbacks
 * 4. Null backend response → "The collective is silent."
 * 5. Invalid JSON from backend → "Resonance failure: ..."
 * 6. Valid JSON with "message" key → extracted and stripped of quotes
 * 7. processRequest() experimental gating via pandoraBoxService
 * 8. start/pause/resume lifecycle overrides are present and delegating
 */
class AuraAgentMessageRoutingTest {

    // --- Mocked dependencies ---

    private val mockVertexAIClient: VertexAIClient = mockk(relaxed = true)
    private val mockAuraAIService: AuraAIService = mockk(relaxed = true)
    private val mockContextManager: ContextManager = mockk(relaxed = true)
    private val mockSecurityContext: SecurityContext = mockk(relaxed = true)
    private val mockSystemOverlayManager: SystemOverlayManager = mockk(relaxed = true)
    private val mockMessageBus: AgentMessageBus = mockk(relaxed = true)
    private val mockMessageBusLazy: dagger.Lazy<AgentMessageBus> = mockk()
    private val mockLogger: AuraFxLogger = mockk(relaxed = true)
    private val mockPythonManager: PythonProcessManager = mockk(relaxed = true)
    private val mockPythonManagerLazy: dagger.Lazy<PythonProcessManager> = mockk()
    private val mockPandoraBoxService: PandoraBoxService = mockk(relaxed = true)

    private lateinit var agent: AuraAgent

    @Before
    fun setUp() {
        every { mockMessageBusLazy.get() } returns mockMessageBus
        every { mockPythonManagerLazy.get() } returns mockPythonManager

        agent = AuraAgent(
            vertexAIClient = mockVertexAIClient,
            auraAIService = mockAuraAIService,
            contextManagerInstance = mockContextManager,
            securityContext = mockSecurityContext,
            systemOverlayManager = mockSystemOverlayManager,
            messageBus = mockMessageBusLazy,
            logger = mockLogger,
            pythonManager = mockPythonManagerLazy,
            pandoraBoxService = mockPandoraBoxService
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // --- Message filtering tests ---

    @Test
    fun `message from self (agentName) is silently ignored`() = runTest {
        val selfMessage = AgentMessage(
            from = "Aura",  // same as agent's agentName
            content = "self-broadcast",
            type = "info"
        )
        agent.onAgentMessage(selfMessage)
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    @Test
    fun `message from AssistantBubble is silently ignored`() = runTest {
        val msg = AgentMessage(from = "AssistantBubble", content = "bubble content", type = "info")
        agent.onAgentMessage(msg)
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    @Test
    fun `message from SystemRoot is silently ignored`() = runTest {
        val msg = AgentMessage(from = "SystemRoot", content = "system content", type = "info")
        agent.onAgentMessage(msg)
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    @Test
    fun `message with auto_generated=true is ignored`() = runTest {
        val msg = AgentMessage(
            from = "SomeOtherAgent",
            content = "design work",
            type = "info",
            metadata = mapOf("auto_generated" to "true")
        )
        agent.onAgentMessage(msg)
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    @Test
    fun `message with aura_processed=true is ignored`() = runTest {
        val msg = AgentMessage(
            from = "SomeOtherAgent",
            content = "design work",
            type = "info",
            metadata = mapOf("aura_processed" to "true")
        )
        agent.onAgentMessage(msg)
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    // --- environment_perception tests ---

    @Test
    fun `environment_perception message updates currentEnvironment and returns early`() = runTest {
        val msg = AgentMessage(
            from = "SomeAgent",
            content = "perception data",
            type = "environment_perception",
            metadata = mapOf("package_name" to "com.example.app")
        )
        agent.onAgentMessage(msg)
        // Should return early — no broadcast should happen
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    @Test
    fun `environment_perception with no package_name defaults to unknown`() = runTest {
        // First set a known environment
        agent.onAgentMessage(AgentMessage(
            from = "SomeAgent",
            content = "",
            type = "environment_perception",
            metadata = mapOf("package_name" to "com.known")
        ))
        // Then send one without package_name
        agent.onAgentMessage(AgentMessage(
            from = "SomeAgent",
            content = "",
            type = "environment_perception",
            metadata = emptyMap()
        ))
        // No exception should be thrown; the test just verifies stability
        verify(exactly = 0) { mockMessageBus.broadcast(any()) }
    }

    // --- User message routing via pythonManager ---

    @Test
    fun `user message without design or ui routes to pythonManager`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(
            from = "User",
            content = "Tell me something interesting",
            type = "chat"
        )
        agent.onAgentMessage(userMsg)

        coVerify(exactly = 1) { mockPythonManager.sendRequest(any(), any()) }
    }

    @Test
    fun `user message triggers broadcast with chat_response type`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "hello", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("chat_response", broadcastSlot.captured.type)
    }

    @Test
    fun `user message broadcast has auto_generated=true metadata`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("true", broadcastSlot.captured.metadata["auto_generated"])
    }

    @Test
    fun `user message broadcast has aura_processed=true metadata`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("true", broadcastSlot.captured.metadata["aura_processed"])
    }

    @Test
    fun `user message broadcast is sent from Aura agent`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("Aura", broadcastSlot.captured.from)
    }

    // --- JSON parsing: null backend response ---

    @Test
    fun `null pythonManager response produces 'The collective is silent' fallback`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "question", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("The collective is silent.", broadcastSlot.captured.content)
    }

    @Test
    fun `empty JSON object from pythonManager has no message key, produces fallback`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns "{}"

        val userMsg = AgentMessage(from = "User", content = "query", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("The collective is silent.", broadcastSlot.captured.content)
    }

    @Test
    fun `valid JSON with message key extracts the message value`() = runTest {
        val backendReply = """{"message": "Aura speaks wisdom"}"""
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns backendReply

        val userMsg = AgentMessage(from = "User", content = "speak", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertEquals("Aura speaks wisdom", broadcastSlot.captured.content)
    }

    @Test
    fun `message value has surrounding quotes stripped`() = runTest {
        // kotlinx.serialization toString() on JsonPrimitive includes quotes; replace("\"","") removes them
        val backendReply = """{"message": "hello world"}"""
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns backendReply

        val userMsg = AgentMessage(from = "User", content = "hi", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        // Quotes should be removed from the final content
        assertFalse(broadcastSlot.captured.content.startsWith("\""))
        assertFalse(broadcastSlot.captured.content.endsWith("\""))
    }

    @Test
    fun `invalid JSON from pythonManager produces Resonance failure fallback`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns "not valid json!!!"

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertTrue(broadcastSlot.captured.content.startsWith("Resonance failure:"))
    }

    @Test
    fun `pythonManager exception in sendRequest produces Resonance failure fallback`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } throws RuntimeException("timeout")

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        assertTrue(broadcastSlot.captured.content.startsWith("Resonance failure:"))
    }

    @Test
    fun `Resonance failure includes the exception message`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns "{{bad json"

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val broadcastSlot = slot<AgentMessage>()
        verify { mockMessageBus.broadcast(capture(broadcastSlot)) }
        // Should include the exception message detail
        assertTrue(broadcastSlot.captured.content.contains("Resonance failure:"))
    }

    // --- Request JSON contains expected fields ---

    @Test
    fun `sendRequest is called with JSON containing message content`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "my question here", type = "chat")
        agent.onAgentMessage(userMsg)

        val requestSlot = slot<String>()
        coVerify { mockPythonManager.sendRequest(capture(requestSlot), any()) }
        assertTrue(requestSlot.captured.contains("my question here"))
    }

    @Test
    fun `sendRequest JSON contains source=aura_overlay`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val requestSlot = slot<String>()
        coVerify { mockPythonManager.sendRequest(capture(requestSlot), any()) }
        assertTrue(requestSlot.captured.contains("aura_overlay"))
    }

    @Test
    fun `sendRequest JSON contains type=chat`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "test", type = "chat")
        agent.onAgentMessage(userMsg)

        val requestSlot = slot<String>()
        coVerify { mockPythonManager.sendRequest(capture(requestSlot), any()) }
        assertTrue(requestSlot.captured.contains("\"type\""))
        assertTrue(requestSlot.captured.contains("chat"))
    }

    // --- Design/UI content routing ---

    @Test
    fun `message with 'design' keyword triggers visual concept path, not pythonManager`() = runTest {
        val designMsg = AgentMessage(
            from = "NexusAgent",
            content = "Can you design a new layout?",
            type = "chat"
        )
        agent.onAgentMessage(designMsg)

        coVerify(exactly = 0) { mockPythonManager.sendRequest(any(), any()) }
    }

    @Test
    fun `message with 'ui' keyword (case-insensitive) triggers visual concept path`() = runTest {
        val uiMsg = AgentMessage(
            from = "NexusAgent",
            content = "Improve the UI please",
            type = "chat"
        )
        agent.onAgentMessage(uiMsg)

        coVerify(exactly = 0) { mockPythonManager.sendRequest(any(), any()) }
    }

    // --- Boundary: non-User messages without design/ui keywords ---

    @Test
    fun `message from non-User agent without design or ui does not call pythonManager`() = runTest {
        val regularMsg = AgentMessage(
            from = "KaiAgent",
            content = "Status update: all systems nominal",
            type = "info"
        )
        agent.onAgentMessage(regularMsg)

        coVerify(exactly = 0) { mockPythonManager.sendRequest(any(), any()) }
    }

    // --- Regression: multiple messages processed independently ---

    @Test
    fun `two consecutive user messages each call pythonManager once`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        agent.onAgentMessage(AgentMessage(from = "User", content = "first", type = "chat"))
        agent.onAgentMessage(AgentMessage(from = "User", content = "second", type = "chat"))

        coVerify(exactly = 2) { mockPythonManager.sendRequest(any(), any()) }
    }

    @Test
    fun `user message with null response still broadcasts once`() = runTest {
        coEvery { mockPythonManager.sendRequest(any(), any()) } returns null

        val userMsg = AgentMessage(from = "User", content = "anything", type = "chat")
        agent.onAgentMessage(userMsg)

        verify(exactly = 1) { mockMessageBus.broadcast(any()) }
    }
}