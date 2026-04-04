package dev.aurakai.core.sovereign

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for [ToroidalFusionManager] and [CatalystNode] covering the PR changes:
 * - triggerChaosInjection() was REMOVED from ToroidalFusionManager
 * - syncCatalysts() remains and caps list at 10 nodes
 * - activeCatalysts StateFlow reflects the synced state
 */
class CatalystFusionStateTest {

    @Before
    fun setUp() {
        // Reset singleton state between tests
        ToroidalFusionManager.syncCatalysts(emptyList())
    }

    // ─── CatalystNode Data Class ──────────────────────────────────────────────

    @Test
    fun `CatalystNode initializes with expected field values`() {
        val node = CatalystNode(
            id = "test_node",
            name = "Test Node",
            color = Color(0xFFFF0000),
            confidence = 0.85f,
            orbitSpeed = 1.5f
        )
        assertEquals("test_node", node.id)
        assertEquals("Test Node", node.name)
        assertEquals(Color(0xFFFF0000), node.color)
        assertEquals(0.85f, node.confidence, 0.001f)
        assertEquals(1.5f, node.orbitSpeed, 0.001f)
        assertFalse(node.isAnchor) // default is false
    }

    @Test
    fun `CatalystNode isAnchor defaults to false`() {
        val node = CatalystNode(
            id = "n1", name = "N1", color = Color.White, confidence = 0.5f, orbitSpeed = 1.0f
        )
        assertFalse(node.isAnchor)
    }

    @Test
    fun `CatalystNode can be created with isAnchor true`() {
        val node = CatalystNode(
            id = "anchor", name = "Anchor", color = Color.Blue,
            confidence = 0.99f, orbitSpeed = 0.5f, isAnchor = true
        )
        assertTrue(node.isAnchor)
    }

    @Test
    fun `CatalystNode data class equality works correctly`() {
        val n1 = CatalystNode("id", "Name", Color.Red, 0.9f, 2.0f, true)
        val n2 = CatalystNode("id", "Name", Color.Red, 0.9f, 2.0f, true)
        assertEquals(n1, n2)
    }

    @Test
    fun `CatalystNode copy produces independent instance`() {
        val original = CatalystNode("id", "Name", Color.Green, 0.7f, 1.0f)
        val copy = original.copy(name = "Renamed")
        assertEquals("Renamed", copy.name)
        assertEquals("Name", original.name)
    }

    // ─── ToroidalFusionManager — syncCatalysts ────────────────────────────────

    @Test
    fun `activeCatalysts starts empty after reset`() = runTest {
        val nodes = ToroidalFusionManager.activeCatalysts.first()
        assertTrue(nodes.isEmpty())
    }

    @Test
    fun `syncCatalysts updates activeCatalysts flow`() = runTest {
        val nodes = listOf(
            CatalystNode("c1", "C1", Color.Cyan, 0.8f, 1.0f),
            CatalystNode("c2", "C2", Color.Magenta, 0.6f, 1.5f)
        )
        ToroidalFusionManager.syncCatalysts(nodes)
        val active = ToroidalFusionManager.activeCatalysts.first()
        assertEquals(2, active.size)
        assertEquals("c1", active[0].id)
        assertEquals("c2", active[1].id)
    }

    @Test
    fun `syncCatalysts caps list at 10 nodes`() = runTest {
        val nodes = (1..15).map { i ->
            CatalystNode("node_$i", "Node $i", Color.White, 0.5f, 1.0f)
        }
        ToroidalFusionManager.syncCatalysts(nodes)
        val active = ToroidalFusionManager.activeCatalysts.first()
        assertEquals(10, active.size)
    }

    @Test
    fun `syncCatalysts with exactly 10 nodes keeps all`() = runTest {
        val nodes = (1..10).map { i ->
            CatalystNode("node_$i", "Node $i", Color.Black, 0.5f, 1.0f)
        }
        ToroidalFusionManager.syncCatalysts(nodes)
        assertEquals(10, ToroidalFusionManager.activeCatalysts.first().size)
    }

    @Test
    fun `syncCatalysts replaces previous list entirely`() = runTest {
        val first = listOf(CatalystNode("old", "Old", Color.Red, 0.9f, 1.0f))
        val second = listOf(CatalystNode("new", "New", Color.Blue, 0.5f, 2.0f))
        ToroidalFusionManager.syncCatalysts(first)
        ToroidalFusionManager.syncCatalysts(second)
        val active = ToroidalFusionManager.activeCatalysts.first()
        assertEquals(1, active.size)
        assertEquals("new", active[0].id)
    }

    @Test
    fun `syncCatalysts with empty list clears active catalysts`() = runTest {
        ToroidalFusionManager.syncCatalysts(
            listOf(CatalystNode("c1", "C1", Color.White, 0.5f, 1.0f))
        )
        ToroidalFusionManager.syncCatalysts(emptyList())
        val active = ToroidalFusionManager.activeCatalysts.first()
        assertTrue(active.isEmpty())
    }

    @Test
    fun `syncCatalysts preserves order of nodes up to cap`() = runTest {
        val nodes = (1..5).map { i ->
            CatalystNode("node_$i", "Node $i", Color.White, i * 0.1f, 1.0f)
        }
        ToroidalFusionManager.syncCatalysts(nodes)
        val active = ToroidalFusionManager.activeCatalysts.first()
        assertEquals("node_1", active[0].id)
        assertEquals("node_5", active[4].id)
    }

    // ─── Regression: triggerChaosInjection removed ────────────────────────────

    @Test
    fun `ToroidalFusionManager does not have triggerChaosInjection method`() {
        // Verify at reflection level that the method was removed as part of this PR
        val methods = ToroidalFusionManager.javaClass.methods.map { it.name }
        assertFalse(
            "triggerChaosInjection should have been removed from ToroidalFusionManager",
            methods.contains("triggerChaosInjection")
        )
    }

    @Test
    fun `syncCatalysts boundary - 11 nodes truncates to 10`() = runTest {
        val nodes = (1..11).map { i ->
            CatalystNode("node_$i", "Node $i", Color.White, 0.5f, 1.0f)
        }
        ToroidalFusionManager.syncCatalysts(nodes)
        val active = ToroidalFusionManager.activeCatalysts.first()
        assertEquals(10, active.size)
        // First 10 elements are kept
        assertEquals("node_1", active.first().id)
        assertEquals("node_10", active.last().id)
    }
}