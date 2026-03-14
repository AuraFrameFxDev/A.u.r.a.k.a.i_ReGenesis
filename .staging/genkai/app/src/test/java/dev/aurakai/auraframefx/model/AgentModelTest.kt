package dev.aurakai.auraframefx.model

import dev.aurakai.auraframefx.models.AgentMessage
import dev.aurakai.auraframefx.models.AgentType
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.jupiter.api.Test
import java.util.*

class AgentModelTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `AgentType should serialize and deserialize correctly`() {
        // Test all enum values
        AgentType.values().forEach { agentType ->
            json.encodeToString(agentType)
            val deserialized = json.decodeFromString<AgentType>("\"${agentType.value}\"")
            assertEquals(agentType, deserialized)
        }
    }

    @Test
    fun `AgentType fromValue should return correct enum`() {
        AgentType.values().forEach { agentType ->
            val fromValue = AgentType.fromValue(agentType.value)
            assertEquals(agentType, fromValue)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AgentType fromValue should throw for invalid value`() {
        AgentType.fromValue("INVALID_AGENT_TYPE")
    }

    @Test
    fun `AgentMessage should serialize and deserialize correctly`() {
        val message = AgentMessage(
            content = "Test message",
            sender = AgentType.AURA,
            timestamp = System.currentTimeMillis(),
            confidence = 0.95f,
            messageType = MessageType.TEXT,
            metadata = mapOf("key1" to "value1", "key2" to "value2")
        )

        val serialized = json.encodeToString(message)
        val deserialized = json.decodeFromString<AgentMessage>(serialized)

        assertEquals(message.content, deserialized.content)
        assertEquals(message.sender, deserialized.sender)
        assertEquals(message.timestamp, deserialized.timestamp)
        assertEquals(message.confidence, deserialized.confidence)
        assertEquals(message.messageType, deserialized.messageType)
        assertEquals(message.metadata, deserialized.metadata)
    }

    @Test
    fun `AgentMessage should handle default values correctly`() {
        val message = AgentMessage(
            content = "Test message",
            sender = AgentType.AURA
        )

        assertTrue(message.timestamp > 0)
        assertEquals(1.0f, message.confidence)
        assertEquals(MessageType.TEXT, message.messageType)
        assertTrue(message.metadata.isEmpty())
    }
}
