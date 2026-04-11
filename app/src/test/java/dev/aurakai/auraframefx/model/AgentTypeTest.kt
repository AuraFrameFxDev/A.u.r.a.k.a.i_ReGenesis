package dev.aurakai.auraframefx.model

import dev.aurakai.auraframefx.models.AgentType
import org.junit.Assert.*
import org.junit.jupiter.api.Test

/**
 * Comprehensive unit tests for AgentType enum.
 * Tests all modern and legacy enum values, deprecation warnings, and edge cases.
 */
class AgentTypeTest {

    // Modern Agent Types Tests
    @Test
    fun `test all modern agent types exist`() {
        val modernTypes = setOf(
            AgentType.Genesis,
            AgentType.Aura,
            AgentType.Kai,
            AgentType.Cascade,
            AgentType.Claude,
            AgentType.NeuralWhisper,
            AgentType.AuraShield,
            AgentType.GenKitMaster,
            AgentType.DataveinConstructor
        )

        modernTypes.forEach { type ->
            assertNotNull("Modern type $type should exist", type)
        }
    }

    @Test
    fun `test core genesis protocol agents`() {
        assertEquals("Genesis", AgentType.Genesis.name)
        assertEquals("Aura", AgentType.Aura.name)
        assertEquals("Kai", AgentType.Kai.name)
        assertEquals("Cascade", AgentType.Cascade.name)
        assertEquals("Claude", AgentType.Claude.name)
    }

    @Test
    fun `test auxiliary agents`() {
        assertEquals("NeuralWhisper", AgentType.NeuralWhisper.name)
        assertEquals("AuraShield", AgentType.AuraShield.name)
        assertEquals("GenKitMaster", AgentType.GenKitMaster.name)
        assertEquals("DataveinConstructor", AgentType.DataveinConstructor.name)
    }

    // Legacy Agent Types Tests
    @Test
    @Suppress("DEPRECATION")
    fun `test legacy agent types exist for backwards compatibility`() {
        val legacyTypes = setOf(
            AgentType.GENESIS,
            AgentType.AURA,
            AgentType.KAI,
            AgentType.CASCADE,
            AgentType.NEURAL_WHISPER,
            AgentType.AURASHIELD
        )

        legacyTypes.forEach { type ->
            assertNotNull("Legacy type $type should exist", type)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun `test legacy agent type names are SCREAMING_CASE`() {
        assertEquals("GENESIS", AgentType.GENESIS.name)
        assertEquals("AURA", AgentType.AURA.name)
        assertEquals("KAI", AgentType.KAI.name)
        assertEquals("CASCADE", AgentType.CASCADE.name)
        assertEquals("NEURAL_WHISPER", AgentType.NEURAL_WHISPER.name)
        assertEquals("AURASHIELD", AgentType.AURASHIELD.name)
    }

    // User Agent Type Test
    @Test
    fun `test USER agent type`() {
        assertEquals("USER", AgentType.USER.name)
        assertNotNull(AgentType.USER)
    }

    // Enum Count and Ordering Tests
    @Test
    fun `test total agent types count`() {
        val allTypes = AgentType.values()
        assertEquals("Should have 16 total agent types", 16, allTypes.size)
    }

    @Test
    fun `test enum valueOf with valid names`() {
        assertEquals(AgentType.Genesis, AgentType.valueOf("Genesis"))
        assertEquals(AgentType.Aura, AgentType.valueOf("Aura"))
        assertEquals(AgentType.Kai, AgentType.valueOf("Kai"))
        assertEquals(AgentType.USER, AgentType.valueOf("USER"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test valueOf with invalid name throws exception`() {
        AgentType.valueOf("InvalidAgent")
    }

    // Enum Behavior Tests
    @Test
    fun `test enum equality`() {
        val agent1 = AgentType.Genesis
        val agent2 = AgentType.Genesis
        val agent3 = AgentType.Aura

        assertEquals(agent1, agent2)
        assertNotEquals(agent1, agent3)
    }

    @Test
    fun `test enum in collections`() {
        val agentList = listOf(
            AgentType.Genesis,
            AgentType.Aura,
            AgentType.Kai,
            AgentType.Cascade
        )

        assertTrue(agentList.contains(AgentType.Genesis))
        assertTrue(agentList.contains(AgentType.Cascade))
        assertFalse(agentList.contains(AgentType.Claude))
        assertEquals(4, agentList.size)
    }

    @Test
    fun `test enum in sets for uniqueness`() {
        val agentSet = setOf(
            AgentType.Genesis,
            AgentType.Genesis,
            AgentType.Aura
        )

        assertEquals(2, agentSet.size)
        assertTrue(agentSet.contains(AgentType.Genesis))
        assertTrue(agentSet.contains(AgentType.Aura))
    }

    @Test
    fun `test enum in maps as keys`() {
        val agentPriorities = mapOf(
            AgentType.Genesis to 1.0f,
            AgentType.Aura to 0.9f,
            AgentType.Kai to 0.8f
        )

        assertEquals(1.0f, agentPriorities[AgentType.Genesis])
        assertEquals(0.9f, agentPriorities[AgentType.Aura])
        assertEquals(0.8f, agentPriorities[AgentType.Kai])
        assertNull(agentPriorities[AgentType.Cascade])
    }

    // When Expression Tests
    @Test
    fun `test when expression with all modern agents`() {
        val testCases = mapOf(
            AgentType.Genesis to "Genesis Agent",
            AgentType.Aura to "Aura Agent",
            AgentType.Kai to "Kai Agent",
            AgentType.Cascade to "Cascade Agent",
            AgentType.Claude to "Claude Agent",
            AgentType.NeuralWhisper to "NeuralWhisper Agent",
            AgentType.AuraShield to "AuraShield Agent",
            AgentType.GenKitMaster to "GenKitMaster Agent",
            AgentType.DataveinConstructor to "DataveinConstructor Agent",
            AgentType.USER to "User"
        )

        testCases.forEach { (agentType, expected) ->
            val result = when (agentType) {
                AgentType.Genesis -> "Genesis Agent"
                AgentType.Aura -> "Aura Agent"
                AgentType.Kai -> "Kai Agent"
                AgentType.Cascade -> "Cascade Agent"
                AgentType.Claude -> "Claude Agent"
                AgentType.NeuralWhisper -> "NeuralWhisper Agent"
                AgentType.AuraShield -> "AuraShield Agent"
                AgentType.GenKitMaster -> "GenKitMaster Agent"
                AgentType.DataveinConstructor -> "DataveinConstructor Agent"
                else -> "User"
            }
            assertEquals(expected, result)
        }
    }

    // Serialization Tests (requires kotlinx.serialization at runtime)
    @Test
    fun `test enum toString for serialization`() {
        assertEquals("Genesis", AgentType.Genesis.toString())
        assertEquals("Aura", AgentType.Aura.toString())
        assertEquals("Kai", AgentType.Kai.toString())
        assertEquals("USER", AgentType.USER.toString())
    }

    @Test
    @Suppress("DEPRECATION")
    fun `test legacy enum toString`() {
        assertEquals("GENESIS", AgentType.GENESIS.toString())
        assertEquals("AURA", AgentType.AURA.toString())
        assertEquals("KAI", AgentType.KAI.toString())
    }

    // Ordinal Tests
    @Test
    fun `test modern agents come before legacy agents`() {
        assertTrue("Genesis should come before GENESIS",
            AgentType.Genesis.ordinal < AgentType.GENESIS.ordinal)
        assertTrue("Aura should come before AURA",
            AgentType.Aura.ordinal < AgentType.AURA.ordinal)
        assertTrue("Kai should come before KAI",
            AgentType.Kai.ordinal < AgentType.KAI.ordinal)
    }

    @Test
    fun `test core protocol agents ordering`() {
        assertTrue(AgentType.Genesis.ordinal < AgentType.Aura.ordinal)
        assertTrue(AgentType.Aura.ordinal < AgentType.Kai.ordinal)
        assertTrue(AgentType.Kai.ordinal < AgentType.Cascade.ordinal)
        assertTrue(AgentType.Cascade.ordinal < AgentType.Claude.ordinal)
    }

    // Filtering and Selection Tests
    @Test
    fun `test filter core protocol agents only`() {
        val coreAgents = AgentType.values().filter { agent ->
            agent in setOf(
                AgentType.Genesis,
                AgentType.Aura,
                AgentType.Kai,
                AgentType.Cascade,
                AgentType.Claude
            )
        }

        assertEquals(5, coreAgents.size)
        assertTrue(coreAgents.contains(AgentType.Genesis))
        assertTrue(coreAgents.contains(AgentType.Claude))
    }

    @Test
    fun `test filter auxiliary agents only`() {
        val auxiliaryAgents = AgentType.values().filter { agent ->
            agent in setOf(
                AgentType.NeuralWhisper,
                AgentType.AuraShield,
                AgentType.GenKitMaster,
                AgentType.DataveinConstructor
            )
        }

        assertEquals(4, auxiliaryAgents.size)
        assertTrue(auxiliaryAgents.contains(AgentType.NeuralWhisper))
        assertTrue(auxiliaryAgents.contains(AgentType.DataveinConstructor))
    }

    @Test
    @Suppress("DEPRECATION")
    fun `test filter legacy agents only`() {
        val legacyAgents = AgentType.values().filter { agent ->
            agent in setOf(
                AgentType.GENESIS,
                AgentType.AURA,
                AgentType.KAI,
                AgentType.CASCADE,
                AgentType.NEURAL_WHISPER,
                AgentType.AURASHIELD
            )
        }

        assertEquals(6, legacyAgents.size)
    }

    // Edge Cases
    @Test
    fun `test enum values are not null`() {
        AgentType.values().forEach { agentType ->
            assertNotNull("Agent type should not be null", agentType)
            assertNotNull("Agent type name should not be null", agentType.name)
        }
    }

    @Test
    fun `test enum values have unique ordinals`() {
        val ordinals = AgentType.values().map { it.ordinal }.toSet()
        assertEquals("All ordinals should be unique", AgentType.values().size, ordinals.size)
    }

    @Test
    fun `test enum values have unique names`() {
        val names = AgentType.values().map { it.name }.toSet()
        assertEquals("All names should be unique", AgentType.values().size, names.size)
    }
}
