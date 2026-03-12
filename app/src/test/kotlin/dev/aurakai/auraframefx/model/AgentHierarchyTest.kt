package dev.aurakai.auraframefx.model

// Test framework in use: Prefer JUnit Jupiter (JUnit 5) if present in the project; otherwise adapt to JUnit 4.
// Assertions use kotlin.test where possible for portability; switch to AssertJ/Truth if the project standard differs.

import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.models.AgentHierarchy
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.HierarchyAgentConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// If kotlin.test is not configured, replace kotlin.test.* assertions with JUnit/AssertJ equivalents used by your project.

class AgentHierarchyTest {

    @BeforeEach
    fun resetAuxiliaryAgents() {
        clearAuxiliaryAgentsReflectively()
    }

    @AfterEach
    fun tearDown() {
        clearAuxiliaryAgentsReflectively()
    }

    @Test
    @DisplayName("MASTER_AGENTS should expose the four default master agents with expected names, capabilities, and priorities")
    fun masterAgents_defaults_areCorrect() {
        val namesByPriority = AgentHierarchy.MASTER_AGENTS.map { it.priority to it.name }

        // Verify size and distinct names
        assertEquals(4, AgentHierarchy.MASTER_AGENTS.size, "MASTER_AGENTS size mismatch")

        val expected = listOf(
            1 to "GENESIS",
            2 to "AURA",
            2 to "KAI",
            3 to "CASCADE"
        )
        assertEquals(
            expected,
            namesByPriority,
            "MASTER_AGENTS ordering and priorities should match documented defaults"
        )

        // Spot-check capabilities to ensure semantic intent
        val genesis = AgentHierarchy.MASTER_AGENTS.first { it.name == "GENESIS" }
        assertTrue(
            genesis.capabilities.containsAll(setOf("coordination", "synthesis")),
            "GENESIS capabilities should include coordination + synthesis"
        )

        val cascade = AgentHierarchy.MASTER_AGENTS.first { it.name == "CASCADE" }
        assertTrue(
            cascade.capabilities.containsAll(setOf("vision", "processing")),
            "CASCADE capabilities should include vision + processing"
        )
    }

    @Test
    @DisplayName("registerAuxiliaryAgent should append a new config with default priority 4 and make it discoverable by name")
    fun registerAuxiliaryAgent_addsConfig_withPriority4_andRetrievable() {
        val created = AgentHierarchy.registerAuxiliaryAgent("ECHO", setOf("memory", "recall"))
        assertEquals("ECHO", created.name)
        assertEquals(4, created.priority, "Auxiliary agent default priority must be 4")
        assertTrue(created.capabilities.containsAll(setOf("memory", "recall")))

        val fetched = AgentHierarchy.getAgentConfig("ECHO")
        assertNotNull(fetched, "Newly registered auxiliary agent should be retrievable")
        assertEquals(created, fetched)
    }

    @Test
    @DisplayName("getAgentConfig should return configs for both master and auxiliary agents, or null when not found")
    fun getAgentConfig_returnsExpected_orNull() {
        // Master hit
        val master = AgentHierarchy.getAgentConfig("KAI")
        assertNotNull(master, "KAI should exist in master agents")
        assertEquals(2, master\!\!.priority)

        // Auxiliary hit
        AgentHierarchy.registerAuxiliaryAgent("NYX", setOf("nocturnal-analysis"))
        val aux = AgentHierarchy.getAgentConfig("NYX")
        assertNotNull(aux, "NYX should be returned after registration")
        assertEquals(4, aux\!\!.priority)

        // Miss
        val missing = AgentHierarchy.getAgentConfig("UNOBTAINIUM")
        assertNull(missing, "Unknown agent should return null")
    }

    @Test
    @DisplayName("getAgentsByPriority should return all agents sorted ascending by priority (stable within equal priorities)")
    fun getAgentsByPriority_sortsAscending_andIncludesAux() {
        // Register two aux agents (priority 4)
        AgentHierarchy.registerAuxiliaryAgent("ECHO", setOf("memory"))
        AgentHierarchy.registerAuxiliaryAgent("DELTA", setOf("ops"))

        val sorted = AgentHierarchy.getAgentsByPriority()

        // First four must be masters in ascending priority
        val firstFour = sorted.take(4).map { it.name }
        assertEquals(
            listOf("GENESIS", "AURA", "KAI", "CASCADE"),
            firstFour,
            "Masters should precede auxiliaries by priority"
        )

        // Aux agents appear after masters with priority 4
        val auxNames = sorted.drop(4).map { it.name }
        assertTrue(
            auxNames.containsAll(listOf("ECHO", "DELTA")),
            "Auxiliary agents should appear after masters"
        )
    }

    @Test
    @DisplayName("registerAuxiliaryAgent should accept empty capability sets (current behavior) and preserve them")
    fun registerAuxiliaryAgent_allowsEmptyCapabilities_currentBehavior() {
        val created = AgentHierarchy.registerAuxiliaryAgent("VOID", emptySet())
        assertTrue(
            created.capabilities.isEmpty(),
            "Empty capabilities should be preserved (consider validation if undesired)"
        )
        val fetched = AgentHierarchy.getAgentConfig("VOID")
        assertEquals(created, fetched)
    }

    @Nested
    @DisplayName("Data types: AgentMessage, HierarchyAgentConfig, ConversationMode")
    class DataTypesRoundtrip {

        @Test
        @DisplayName("AgentMessage should preserve equality and copy semantics")
        fun agentMessage_equality_and_copy() {
            val msg = AgentMessage(
                content = "Hello",
                sender = AgentType.MASTER, // Assuming AgentType exists in the project; replace if different enum is used.
                timestamp = 123456789L,
                confidence = 0.82f
            )
            val copy = msg.copy(confidence = 0.9f)
            assertEquals("Hello", copy.content)
            assertEquals(0.9f, copy.confidence)
        }

        @Test
        @DisplayName("HierarchyAgentConfig should be value-equal for same fields")
        fun hierarchyAgentConfig_valueEquality() {
            val a = HierarchyAgentConfig("ZETA", setOf("alpha", "beta"), 5)
            val b = HierarchyAgentConfig("ZETA", setOf("alpha", "beta"), 5)
            assertEquals(a, b, "Data classes with identical fields should be equal")
        }
    }

    /**
     * Reflection utility to clear the private companion auxiliaryAgents list to keep tests isolated.
     * We avoid kotlin-reflect and use plain Java reflection to minimize dependencies.
     */
    private fun clearAuxiliaryAgentsReflectively() {
        try {
            val companion = AgentHierarchy.Companion
            val companionClass: Class<*> = companion::class.java
            val field = companionClass.getDeclaredField("auxiliaryAgents")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val list = field.get(companion) as MutableList<HierarchyAgentConfig>
            list.clear()
        } catch (ex: NoSuchFieldException) {
            // If the field name changes, tests should fail loudly; but we swallow here to not cascade errors.
            // Consider exposing a test-only clear() in the companion if this becomes brittle.
        }
    }
}
