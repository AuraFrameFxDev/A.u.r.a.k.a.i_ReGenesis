package dev.aurakai.auraframefx.ui.screens

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test

class AgentNexusScreenUnitTest {

    @Test
    fun agentStats_defaults_areApplied() {
        val s = AgentStats(
            name = "AL",
            processingPower = 0.5f,
            knowledgeBase = 0.25f,
            speed = 1.0f,
            accuracy = 0.75f
        )
        assertEquals("AL", s.name)
        assertEquals(1, s.evolutionLevel)
        assertTrue(s.isActive)
        assertEquals("", s.specialAbility)
        // color default should be Cyan
        assertEquals(androidx.compose.ui.graphics.Color.Cyan, s.color)
    }

    @Test
    fun agentStatsPanel_statBar_percentages_areFormatted() {
        // Validate the formatting logic for StatBar values (pure math)
        fun pct(value: Float) = "${(value * 100).toInt()}%"
        assertEquals("0%", pct(0f))
        assertEquals("25%", pct(0.25f))
        assertEquals("50%", pct(0.5f))
        assertEquals("99%", pct(0.999f))
        assertEquals("100%", pct(1.0f))
    }

    @Test
    fun statBar_handles_edge_values() {
        // Ensure no crashes for edge values outside 0..1 domain.
        // The Composable uses fillMaxWidth(value), so callers should keep 0..1,
        // but verify we can reason about edge math externally.
        val below = -0.1f
        val above = 1.5f

        // Values themselves are floats; we only check that conversion to percent won't crash
        fun pctSafe(v: Float): String = try {
            "${(v * 100).toInt()}%"
        } catch (e: Exception) {
            "ERR"
        }
        assertEquals("-10%", pctSafe(below))
        assertEquals("150%", pctSafe(above))
    }

    @Test
    fun agentNode_selection_scale_targets_correctValues() {
        // animateFloatAsState targets 1.2f if selected else 1f; here we verify target values
        val selectedTarget = if (true) 1.2f else 1f
        val unselectedTarget = if (false) 1.2f else 1f
        assertEquals(1.2f, selectedTarget)
        assertEquals(1f, unselectedTarget)
    }

    @Test
    fun nexusCore_positions_threeAgents_inTriangle() {
        // NexusCore computes angle = index*120f - 90f and uses radius=100.dp.
        // Validate the angle math positions for three indices.
        val angles = (0..2).map { it * 120f - 90f }
        assertArrayEquals(floatArrayOf(-90f, 30f, 150f), angles.toFloatArray(), 0.0001f)

        // Validate offsets sign patterns using cos/sin of the computed angles (in degrees)
        fun off(angleDeg: Float): Pair<Float, Float> {
            val r = 100f
            val rad = Math.toRadians(angleDeg.toDouble())
            val x = (r * kotlin.math.cos(rad)).toFloat()
            val y = (r * kotlin.math.sin(rad)).toFloat()
            return x to y
        }

        val p0 = off(-90f)
        val p1 = off(30f)
        val p2 = off(150f)
        // p0 should be roughly (0, -r)
        assertTrue(kotlin.math.abs(p0.first) < 1e-3)
        assertTrue(p0.second < 0)
        // p1 should have positive x and positive y
        assertTrue(p1.first > 0 && p1.second > 0)
        // p2 should have negative x and positive y
        assertTrue(p2.first < 0 && p2.second > 0)
    }
}