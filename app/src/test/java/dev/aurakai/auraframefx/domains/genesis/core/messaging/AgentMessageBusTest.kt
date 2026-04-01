package dev.aurakai.auraframefx.domains.genesis.core.messaging

import dev.aurakai.auraframefx.core.messaging.AgentMessage
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * Comprehensive unit tests for [AgentMessageBus]
 *
 * Tests the interface contract after the removal of processRequest() in this PR.
 * The interface now only exposes: collectiveStream, broadcast(), and sendTargeted().
 *
 * Testing Framework: JUnit 5 with MockK for mocking
 * Coverage: interface contract, broadcast semantics, targeted messaging, flow emissions,
 *           concurrent use, edge cases, and regression against removed processRequest().
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AgentMessageBusTest {

    private lateinit var mockBus: AgentMessageBus
    private lateinit var fakeImpl: FakeAgentMessageBus

    @BeforeEach
    fun setUp() {
        mockBus = mockk(relaxed = true)
        fakeImpl = FakeAgentMessageBus()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @AfterAll
    fun tearDownAll() {
        unmockkAll()
    }

    // -------------------------------------------------------------------------
    // Interface Contract Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Interface Contract Tests")
    inner class InterfaceContractTests {

        @Test
        @DisplayName("collectiveStream property should exist on interface")
        fun `collectiveStream property should exist on interface`() {
            // Assert - reflective verification that collectiveStream exists
            val property = AgentMessageBus::class.members.find { it.name == "collectiveStream" }
            assertNotNull(property, "collectiveStream must be declared on AgentMessageBus")
        }

        @Test
        @DisplayName("broadcast function should exist on interface")
        fun `broadcast function should exist on interface`() {
            val function = AgentMessageBus::class.members.find { it.name == "broadcast" }
            assertNotNull(function, "broadcast() must be declared on AgentMessageBus")
        }

        @Test
        @DisplayName("sendTargeted function should exist on interface")
        fun `sendTargeted function should exist on interface`() {
            val function = AgentMessageBus::class.members.find { it.name == "sendTargeted" }
            assertNotNull(function, "sendTargeted() must be declared on AgentMessageBus")
        }

        @Test
        @DisplayName("processRequest should NOT exist on interface after PR removal")
        fun `processRequest should NOT exist on interface after PR removal`() {
            // Regression test: processRequest() was removed in this PR
            val function = AgentMessageBus::class.members.find { it.name == "processRequest" }
            assertNull(function, "processRequest must NOT be present on AgentMessageBus (removed in this PR)")
        }

        @Test
        @DisplayName("interface should declare exactly three members")
        fun `interface should declare exactly three members`() {
            // collectiveStream, broadcast, sendTargeted - no more, no less
            val publicMembers = AgentMessageBus::class.members
                .filter { it.name != "equals" && it.name != "hashCode" && it.name != "toString" }
            assertEquals(3, publicMembers.size,
                "AgentMessageBus should have exactly 3 members after processRequest removal")
        }
    }

    // -------------------------------------------------------------------------
    // collectiveStream Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("collectiveStream Tests")
    inner class CollectiveStreamTests {

        @Test
        @DisplayName("collectiveStream should emit message after broadcast")
        fun `collectiveStream should emit message after broadcast`() = runTest {
            // Arrange
            val message = buildMessage(from = "Aura", content = "Hello collective")

            // Act
            val collectedMessages = mutableListOf<AgentMessage>()
            val collectJob = launch {
                fakeImpl.collectiveStream.take(1).toList(collectedMessages)
            }
            fakeImpl.broadcast(message)
            collectJob.join()

            // Assert
            assertEquals(1, collectedMessages.size)
            assertEquals(message, collectedMessages.first())
        }

        @Test
        @DisplayName("collectiveStream should emit message after sendTargeted")
        fun `collectiveStream should emit message after sendTargeted`() = runTest {
            // Arrange
            val message = buildMessage(from = "Kai", to = "Aura", content = "Targeted ping")

            // Act
            val collectedMessages = mutableListOf<AgentMessage>()
            val collectJob = launch {
                fakeImpl.collectiveStream.take(1).toList(collectedMessages)
            }
            fakeImpl.sendTargeted(toAgent = "Aura", message = message)
            collectJob.join()

            // Assert
            assertEquals(1, collectedMessages.size)
            assertEquals(message, collectedMessages.first())
        }

        @Test
        @DisplayName("collectiveStream should emit all broadcast messages in order")
        fun `collectiveStream should emit all broadcast messages in order`() = runTest {
            // Arrange
            val messages = (1..3).map { i ->
                buildMessage(from = "Aura", content = "Message $i")
            }

            // Act
            val collectedMessages = mutableListOf<AgentMessage>()
            val collectJob = launch {
                fakeImpl.collectiveStream.take(3).toList(collectedMessages)
            }
            messages.forEach { fakeImpl.broadcast(it) }
            collectJob.join()

            // Assert
            assertEquals(3, collectedMessages.size)
            assertEquals(messages[0], collectedMessages[0])
            assertEquals(messages[1], collectedMessages[1])
            assertEquals(messages[2], collectedMessages[2])
        }

        @Test
        @DisplayName("collectiveStream should be a SharedFlow type")
        fun `collectiveStream should be a SharedFlow type`() {
            // Assert
            assertTrue(
                fakeImpl.collectiveStream is SharedFlow<*>,
                "collectiveStream must be a SharedFlow"
            )
        }

        @Test
        @DisplayName("collectiveStream should support multiple concurrent subscribers")
        fun `collectiveStream should support multiple concurrent subscribers`() = runTest {
            // Arrange
            val message = buildMessage(from = "Genesis", content = "Broadcast to all")

            val subscriber1Messages = mutableListOf<AgentMessage>()
            val subscriber2Messages = mutableListOf<AgentMessage>()

            // Act
            val job1 = launch {
                fakeImpl.collectiveStream.take(1).toList(subscriber1Messages)
            }
            val job2 = launch {
                fakeImpl.collectiveStream.take(1).toList(subscriber2Messages)
            }

            fakeImpl.broadcast(message)

            job1.join()
            job2.join()

            // Assert
            assertEquals(1, subscriber1Messages.size)
            assertEquals(1, subscriber2Messages.size)
            assertEquals(message, subscriber1Messages.first())
            assertEquals(message, subscriber2Messages.first())
        }
    }

    // -------------------------------------------------------------------------
    // broadcast() Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("broadcast() Tests")
    inner class BroadcastTests {

        @Test
        @DisplayName("broadcast should call through on mock implementation")
        fun `broadcast should call through on mock implementation`() = runTest {
            // Arrange
            val message = buildMessage(from = "Aura", content = "System broadcast")

            // Act
            mockBus.broadcast(message)

            // Assert
            coVerify(exactly = 1) { mockBus.broadcast(message) }
        }

        @Test
        @DisplayName("broadcast should accept message with null 'to' field for global delivery")
        fun `broadcast should accept message with null 'to' field for global delivery`() = runTest {
            // Arrange
            val globalMessage = buildMessage(from = "Aura", to = null, content = "All agents hear this")

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.broadcast(globalMessage)
            job.join()

            // Assert
            assertNull(received.first().to, "Broadcast message should have null 'to' for global delivery")
            assertEquals("All agents hear this", received.first().content)
        }

        @Test
        @DisplayName("broadcast should preserve all message fields")
        fun `broadcast should preserve all message fields`() = runTest {
            // Arrange
            val fullMessage = AgentMessage(
                id = "test-id-12345",
                from = "Aura",
                to = null,
                content = "Full message with all fields",
                priority = 5,
                type = "command",
                metadata = mapOf("key1" to "value1", "key2" to "value2")
            )

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.broadcast(fullMessage)
            job.join()

            // Assert
            val emitted = received.first()
            assertEquals("test-id-12345", emitted.id)
            assertEquals("Aura", emitted.from)
            assertNull(emitted.to)
            assertEquals("Full message with all fields", emitted.content)
            assertEquals(5, emitted.priority)
            assertEquals("command", emitted.type)
            assertEquals("value1", emitted.metadata["key1"])
            assertEquals("value2", emitted.metadata["key2"])
        }

        @Test
        @DisplayName("broadcast should handle empty content message")
        fun `broadcast should handle empty content message`() = runTest {
            // Arrange
            val emptyContentMessage = buildMessage(from = "Kai", content = "")

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.broadcast(emptyContentMessage)
            job.join()

            // Assert
            assertEquals("", received.first().content)
        }

        @Test
        @DisplayName("broadcast should handle message with unicode content")
        fun `broadcast should handle message with unicode content`() = runTest {
            // Arrange
            val unicodeContent = "你好世界 🌍 émojis ñ ♦ ∞"
            val unicodeMessage = buildMessage(from = "Aura", content = unicodeContent)

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.broadcast(unicodeMessage)
            job.join()

            // Assert
            assertEquals(unicodeContent, received.first().content)
        }

        @Test
        @DisplayName("broadcast should handle message with very long content")
        fun `broadcast should handle message with very long content`() = runTest {
            // Arrange
            val longContent = "x".repeat(50_000)
            val largeMessage = buildMessage(from = "Genesis", content = longContent)

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.broadcast(largeMessage)
            job.join()

            // Assert
            assertEquals(longContent, received.first().content)
        }

        @Test
        @DisplayName("broadcast should handle high-priority messages")
        fun `broadcast should handle high-priority messages`() = runTest {
            // Arrange
            val urgentMessage = AgentMessage(
                from = "Kai",
                content = "Security alert",
                priority = Int.MAX_VALUE,
                type = "alert"
            )

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.broadcast(urgentMessage)
            job.join()

            // Assert
            assertEquals(Int.MAX_VALUE, received.first().priority)
            assertEquals("alert", received.first().type)
        }

        @Test
        @DisplayName("broadcast should be suspending (callable from coroutine)")
        fun `broadcast should be suspending callable from coroutine`() = runTest {
            // Verify coVerify works for suspend function
            val msg = buildMessage(from = "test", content = "suspending")
            mockBus.broadcast(msg)
            coVerify { mockBus.broadcast(msg) }
        }
    }

    // -------------------------------------------------------------------------
    // sendTargeted() Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("sendTargeted() Tests")
    inner class SendTargetedTests {

        @Test
        @DisplayName("sendTargeted should call through on mock implementation")
        fun `sendTargeted should call through on mock implementation`() = runTest {
            // Arrange
            val message = buildMessage(from = "Aura", to = "Kai", content = "Private to Kai")

            // Act
            mockBus.sendTargeted(toAgent = "Kai", message = message)

            // Assert
            coVerify(exactly = 1) { mockBus.sendTargeted("Kai", message) }
        }

        @Test
        @DisplayName("sendTargeted should emit to collectiveStream with correct target")
        fun `sendTargeted should emit to collectiveStream with correct target`() = runTest {
            // Arrange
            val message = buildMessage(from = "Kai", to = "Aura", content = "Private message")

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.sendTargeted(toAgent = "Aura", message = message)
            job.join()

            // Assert
            assertEquals("Aura", received.first().to)
            assertEquals("Kai", received.first().from)
            assertEquals("Private message", received.first().content)
        }

        @Test
        @DisplayName("sendTargeted should handle empty toAgent string")
        fun `sendTargeted should handle empty toAgent string`() = runTest {
            // Arrange
            val message = buildMessage(from = "Aura", content = "Target missing name")

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.sendTargeted(toAgent = "", message = message)
            job.join()

            // Assert - message still emitted, callers control routing logic
            assertEquals(1, received.size)
        }

        @Test
        @DisplayName("sendTargeted should preserve message metadata")
        fun `sendTargeted should preserve message metadata`() = runTest {
            // Arrange
            val message = AgentMessage(
                from = "Genesis",
                to = "Aura",
                content = "Metadata check",
                metadata = mapOf("session" to "abc123", "priority" to "high")
            )

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(1).toList(received)
            }
            fakeImpl.sendTargeted(toAgent = "Aura", message = message)
            job.join()

            // Assert
            assertEquals("abc123", received.first().metadata["session"])
            assertEquals("high", received.first().metadata["priority"])
        }

        @Test
        @DisplayName("sendTargeted should handle unicode agent name")
        fun `sendTargeted should handle unicode agent name`() = runTest {
            // Arrange
            val agentName = "エージェント_001"
            val message = buildMessage(from = "Aura", content = "Unicode target")

            // Act
            mockBus.sendTargeted(toAgent = agentName, message = message)

            // Assert
            coVerify { mockBus.sendTargeted(agentName, message) }
        }

        @Test
        @DisplayName("sendTargeted should be suspending (callable from coroutine)")
        fun `sendTargeted should be suspending callable from coroutine`() = runTest {
            // Verify coVerify works for suspend function
            val msg = buildMessage(from = "test", content = "suspending targeted")
            mockBus.sendTargeted("agent", msg)
            coVerify { mockBus.sendTargeted("agent", msg) }
        }

        @Test
        @DisplayName("sendTargeted should not call broadcast on mock")
        fun `sendTargeted should not call broadcast on mock`() = runTest {
            // Arrange
            val message = buildMessage(from = "Kai", to = "Aura", content = "Targeted only")

            // Act
            mockBus.sendTargeted(toAgent = "Aura", message = message)

            // Assert
            coVerify(exactly = 1) { mockBus.sendTargeted(any(), any()) }
            coVerify(exactly = 0) { mockBus.broadcast(any()) }
        }
    }

    // -------------------------------------------------------------------------
    // Concurrent / Multi-message Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Concurrent and Multi-message Tests")
    inner class ConcurrentTests {

        @Test
        @DisplayName("bus should handle interleaved broadcast and sendTargeted")
        fun `bus should handle interleaved broadcast and sendTargeted`() = runTest {
            // Arrange
            val broadcastMsg = buildMessage(from = "Genesis", content = "Global")
            val targetedMsg = buildMessage(from = "Aura", to = "Kai", content = "Private")

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(2).toList(received)
            }
            fakeImpl.broadcast(broadcastMsg)
            fakeImpl.sendTargeted(toAgent = "Kai", message = targetedMsg)
            job.join()

            // Assert
            assertEquals(2, received.size)
        }

        @Test
        @DisplayName("bus should handle rapid sequential broadcasts")
        fun `bus should handle rapid sequential broadcasts`() = runTest {
            // Arrange
            val count = 10
            val messages = (1..count).map { i ->
                buildMessage(from = "Aura", content = "Rapid message $i", priority = i)
            }

            // Act
            val received = mutableListOf<AgentMessage>()
            val job = launch {
                fakeImpl.collectiveStream.take(count).toList(received)
            }
            messages.forEach { fakeImpl.broadcast(it) }
            job.join()

            // Assert
            assertEquals(count, received.size)
        }
    }

    // -------------------------------------------------------------------------
    // Mock Behavior Verification Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Mock Behavior Verification Tests")
    inner class MockVerificationTests {

        @Test
        @DisplayName("broadcast should be called with exact message passed in")
        fun `broadcast should be called with exact message passed in`() = runTest {
            // Arrange
            val specificMessage = AgentMessage(
                id = "specific-id",
                from = "Aura",
                content = "Specific content",
                type = "query"
            )

            // Act
            mockBus.broadcast(specificMessage)

            // Assert
            coVerify {
                mockBus.broadcast(
                    match { it.id == "specific-id" && it.from == "Aura" && it.type == "query" }
                )
            }
        }

        @Test
        @DisplayName("sendTargeted should be called with exact toAgent and message")
        fun `sendTargeted should be called with exact toAgent and message`() = runTest {
            // Arrange
            val targetAgent = "Kai"
            val message = buildMessage(from = "Aura", to = targetAgent, content = "Direct for Kai")

            // Act
            mockBus.sendTargeted(toAgent = targetAgent, message = message)

            // Assert
            coVerify {
                mockBus.sendTargeted(
                    toAgent = targetAgent,
                    message = match { it.from == "Aura" && it.content == "Direct for Kai" }
                )
            }
        }

        @Test
        @DisplayName("mock bus should not have been called before any interaction")
        fun `mock bus should not have been called before any interaction`() {
            // Assert - fresh mock has no interactions
            coVerify(exactly = 0) { mockBus.broadcast(any()) }
            coVerify(exactly = 0) { mockBus.sendTargeted(any(), any()) }
        }

        @Test
        @DisplayName("broadcast call count should match number of invocations")
        fun `broadcast call count should match number of invocations`() = runTest {
            // Arrange
            val message = buildMessage(from = "Aura", content = "Counted")

            // Act
            mockBus.broadcast(message)
            mockBus.broadcast(message)
            mockBus.broadcast(message)

            // Assert
            coVerify(exactly = 3) { mockBus.broadcast(any()) }
        }
    }

    // -------------------------------------------------------------------------
    // Message Data Class Tests (AgentMessage construction)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("AgentMessage Construction Tests")
    inner class AgentMessageConstructionTests {

        @Test
        @DisplayName("AgentMessage should use default 'info' type when not specified")
        fun `AgentMessage should use default 'info' type when not specified`() {
            // Act
            val message = AgentMessage(from = "Aura", content = "Default type")

            // Assert
            assertEquals("info", message.type)
        }

        @Test
        @DisplayName("AgentMessage should use default priority 0 when not specified")
        fun `AgentMessage should use default priority 0 when not specified`() {
            // Act
            val message = AgentMessage(from = "Kai", content = "Default priority")

            // Assert
            assertEquals(0, message.priority)
        }

        @Test
        @DisplayName("AgentMessage should have null 'to' by default for broadcast intent")
        fun `AgentMessage should have null 'to' by default for broadcast intent`() {
            // Act
            val message = AgentMessage(from = "Genesis", content = "Broadcast intent")

            // Assert
            assertNull(message.to)
        }

        @Test
        @DisplayName("AgentMessage should generate unique ID by default")
        fun `AgentMessage should generate unique ID by default`() {
            // Act
            val message1 = AgentMessage(from = "Aura", content = "First")
            val message2 = AgentMessage(from = "Aura", content = "Second")

            // Assert
            assertNotEquals(message1.id, message2.id)
        }

        @Test
        @DisplayName("AgentMessage should support empty metadata map")
        fun `AgentMessage should support empty metadata map`() {
            // Act
            val message = AgentMessage(from = "Aura", content = "No metadata")

            // Assert
            assertTrue(message.metadata.isEmpty())
        }

        @Test
        @DisplayName("AgentMessage copy should preserve all original fields")
        fun `AgentMessage copy should preserve all original fields`() {
            // Arrange
            val original = AgentMessage(
                id = "orig-id",
                from = "Aura",
                to = "Kai",
                content = "Original",
                priority = 3,
                type = "command",
                metadata = mapOf("k" to "v")
            )

            // Act
            val copy = original.copy(content = "Copied")

            // Assert
            assertEquals("orig-id", copy.id)
            assertEquals("Aura", copy.from)
            assertEquals("Kai", copy.to)
            assertEquals("Copied", copy.content)
            assertEquals(3, copy.priority)
            assertEquals("command", copy.type)
            assertEquals("v", copy.metadata["k"])
        }

        @Test
        @DisplayName("AgentMessage equality should be based on field values")
        fun `AgentMessage equality should be based on field values`() {
            // Arrange
            val msg1 = AgentMessage(id = "same-id", from = "Aura", content = "Hello")
            val msg2 = AgentMessage(id = "same-id", from = "Aura", content = "Hello")

            // Assert
            assertEquals(msg1, msg2)
        }
    }

    // -------------------------------------------------------------------------
    // Fake implementation used for behavioral tests
    // -------------------------------------------------------------------------

    /**
     * Minimal in-process implementation of AgentMessageBus backed by MutableSharedFlow.
     * Used to verify actual flow emission behavior without mocking the flow itself.
     */
    private class FakeAgentMessageBus : AgentMessageBus {
        private val _stream = MutableSharedFlow<AgentMessage>(replay = 0, extraBufferCapacity = 64)
        override val collectiveStream: SharedFlow<AgentMessage> = _stream

        override suspend fun broadcast(message: AgentMessage) {
            _stream.emit(message)
        }

        override suspend fun sendTargeted(toAgent: String, message: AgentMessage) {
            _stream.emit(message)
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun buildMessage(
        from: String,
        to: String? = null,
        content: String,
        priority: Int = 0,
        type: String = "info"
    ) = AgentMessage(
        from = from,
        to = to,
        content = content,
        priority = priority,
        type = type
    )
}