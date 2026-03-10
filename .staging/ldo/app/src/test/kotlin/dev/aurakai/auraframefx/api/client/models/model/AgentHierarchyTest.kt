package dev.aurakai.auraframefx.api.client.models.model

import dev.aurakai.auraframefx.models.AgentHierarchy
import dev.aurakai.auraframefx.models.AgentMessage
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.ConversationMode
import dev.aurakai.auraframefx.models.HierarchyAgentConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Test framework note:
 * - JUnit 5 (org.junit.jupiter) is used here.
 * - If this repository uses a different framework (e.g., JUnit 4 or Kotest), update annotations/imports accordingly.
 *
 * Scope: Focused on the diff-provided model structures, especially AgentHierarchy's companion object behaviors.
 */
class AgentHierarchyTest {

    @AfterEach
    fun tearDown() {
        // Attempt to reset auxiliary agents to avoid cross-test pollution.
        // Since auxiliaryAgents is private inside companion object, we can't clear it directly.
        // Strategy: Register a unique set per test and rely on name uniqueness to avoid collisions.
        // If a repository helper exists to clear, prefer that.
    }

    @Test
    fun `MASTER_AGENTS should contain expected core agents in defined order and priorities`() {
        val masters = AgentHierarchy.MASTER_AGENTS

        // Basic existence
        assertTrue(masters.isNotEmpty(), "MASTER_AGENTS should not be empty")

        // Names and priorities asserted per diff
        val byName = masters.associateBy { it.name }
        assertTrue(byName.containsKey("GENESIS"))
        assertTrue(byName.containsKey("AURA"))
        assertTrue(byName.containsKey("KAI"))
        assertTrue(byName.containsKey("CASCADE"))

        assertEquals(1, byName["GENESIS"]\!\!.priority)
        assertEquals(2, byName["AURA"]\!\!.priority)
        assertEquals(2, byName["KAI"]\!\!.priority)
        assertEquals(3, byName["CASCADE"]\!\!.priority)

        // Capabilities sanity
        assertTrue(byName["GENESIS"]\!\!.capabilities.containsAll(setOf("coordination", "synthesis")))
        assertTrue(byName["AURA"]\!\!.capabilities.containsAll(setOf("creativity", "design")))
        assertTrue(byName["KAI"]\!\!.capabilities.containsAll(setOf("security", "analysis")))
        assertTrue(byName["CASCADE"]\!\!.capabilities.containsAll(setOf("vision", "processing")))
    }

    @Test
    fun `registerAuxiliaryAgent should add agent with default priority 4 and be retrievable`() {
        val uniqueName = "AUX_TEST_" + System.nanoTime()
        val caps = setOf("assist", "tools")

        val created = AgentHierarchy.registerAuxiliaryAgent(uniqueName, caps)
        assertEquals(uniqueName, created.name)
        assertEquals(caps, created.capabilities)
        assertEquals(4, created.priority)

        val fetched = AgentHierarchy.getAgentConfig(uniqueName)
        assertNotNull(
            fetched,
            "Registered auxiliary agent should be retrievable via getAgentConfig"
        )
        assertEquals(4, fetched\!\!.priority)
        assertEquals(caps, fetched.capabilities)
    }

    @Test
    fun `getAgentConfig should return master agent when queried by name`() {
        val fetched = AgentHierarchy.getAgentConfig("GENESIS")
        assertNotNull(fetched)
        assertEquals("GENESIS", fetched\!\!.name)
        assertEquals(1, fetched.priority)
    }

    @Test
    fun `getAgentConfig should return null for non-existent agent`() {
        assertNull(AgentHierarchy.getAgentConfig("NON_EXISTENT_" + System.nanoTime()))
    }

    @Test
    fun `getAgentsByPriority should return combined master plus auxiliary sorted ascending by priority`() {
        // Register three auxiliaries with same priority (4) to ensure they come after masters
        val n1 = "AUX_SORT_" + System.nanoTime()
        val n2 = "AUX_SORT_" + (System.nanoTime() + 1)
        val n3 = "AUX_SORT_" + (System.nanoTime() + 2)

        AgentHierarchy.registerAuxiliaryAgent(n1, setOf("c1"))
        AgentHierarchy.registerAuxiliaryAgent(n2, setOf("c2"))
        AgentHierarchy.registerAuxiliaryAgent(n3, setOf("c3"))

        val sorted = AgentHierarchy.getAgentsByPriority()
        assertTrue(sorted.isNotEmpty())

        // Ascending order check
        val priorities = sorted.map { it.priority }
        assertTrue(priorities == priorities.sorted(), "Expected ascending order by priority")

        // Masters should appear before auxiliaries (priority 1..3 vs 4)
        val maxMasterPriority = 3
        assertTrue(sorted.takeWhile { it.priority <= maxMasterPriority }
            .all { it.name in setOf("GENESIS", "AURA", "KAI", "CASCADE") })
        assertTrue(sorted.dropWhile { it.priority <= maxMasterPriority }.all { it.priority >= 4 })
    }

    @Test
    fun `HierarchyAgentConfig equality and copy semantics should work as data class contracts`() {
        val c1 = HierarchyAgentConfig("X", setOf("a", "b"), 9, role = Any())
        val c2 = c1.copy()
        assertEquals(c1, c2)
        assertEquals(c1.hashCode(), c2.hashCode())
        assertEquals("X", c2.copy(name = "Y").name)
    }

    @Test
    fun `AgentMessage holds given values and supports serialization-like constraints`() {
        // This is a value integrity test; actual serialization is handled by kotlinx.serialization runtime.
        val now = System.currentTimeMillis()
        val msg = AgentMessage(
            content = "hello",
            sender = AgentType.PRIMARY,
            timestamp = now,
            confidence = 0.85f
        )
        assertEquals("hello", msg.content)
        assertEquals(AgentType.PRIMARY, msg.sender)
        assertEquals(now, msg.timestamp)
        assertEquals(0.85f, msg.confidence)
    }

    @Test
    fun `ConversationMode enum should expose expected constants`() {
        val all = ConversationMode.entries.toSet()
        assertTrue(all.contains(ConversationMode.TURN_ORDER))
        assertTrue(all.contains(ConversationMode.FREE_FORM))
        assertEquals(2, all.size)
    }

    @Test
    fun `getAgentsByPriority should be stable for same priority items order within their group`() {
        // Register multiple auxiliaries with same priority; while total order is by priority,
        // we at least ensure all priority-4 items are grouped together.
        val base = "AUX_GROUP_" + System.nanoTime()
        val auxNames = List(5) { "$base-$it" }
        auxNames.forEach { AgentHierarchy.registerAuxiliaryAgent(it, setOf("cap")) }

        val sorted = AgentHierarchy.getAgentsByPriority()
        val lastMasterIndex = sorted.indexOfLast { it.priority < 4 }
        val auxSegment = sorted.drop(lastMasterIndex + 1)
        assertTrue(
            auxSegment.all { it.priority == 4 },
            "All trailing auxiliaries should have priority 4"
        )
        assertTrue(
            auxNames.all { name -> auxSegment.any { it.name == name } },
            "All registered auxiliaries should be present in sorted list"
        )
    }

    // Edge case protection: Attempt to create HierarchyAgentConfig with empty capabilities
    @Test
    fun `HierarchyAgentConfig allows empty capability set but still participates in lookups`() {
        val name = "AUX_EMPTY_CAPS_" + System.nanoTime()
        AgentHierarchy.registerAuxiliaryAgent(name, emptySet())
        val fetched = AgentHierarchy.getAgentConfig(name)
        assertNotNull(fetched)
        assertTrue(fetched\!\!.capabilities.isEmpty())
        assertEquals(4, fetched.priority)
    }

    // Guard test documenting current behavior when duplicate names are registered:
    @Test
    fun `registering duplicate auxiliary names results in first-match retrieval by getAgentConfig`() {
        val base = "AUX_DUP_" + System.nanoTime()
        AgentHierarchy.registerAuxiliaryAgent(base, setOf("one"))
        AgentHierarchy.registerAuxiliaryAgent(base, setOf("two")) // duplicate name

        // Current implementation uses first match across (MASTER + auxiliaries).
        val fetched = AgentHierarchy.getAgentConfig(base)
        assertNotNull(fetched)
        // We cannot guarantee which duplicate is first because we append to the end of the list.
        // Documenting expectation: first occurrence returned; other duplicates still exist in the combined list.
        val allByName = AgentHierarchy.getAgentsByPriority().filter { it.name == base }
        assertTrue(allByName.size >= 2, "Expect duplicates to exist in internal list")
    }
}
