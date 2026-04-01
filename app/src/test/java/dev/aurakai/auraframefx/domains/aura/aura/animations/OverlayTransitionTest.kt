package dev.aurakai.auraframefx.domains.aura.aura.animations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [OverlayTransition].
 *
 * This PR introduced [OverlayTransition] as a data class with:
 * - [OverlayTransition.id] — required
 * - [OverlayTransition.type] — required
 * - [OverlayTransition.name] — optional, defaults to ""
 * - [OverlayTransition.durationMs] — nullable, optional
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("OverlayTransition")
class OverlayTransitionTest {

    @Nested
    @DisplayName("Constructor and field storage")
    inner class ConstructorTests {

        @Test
        @DisplayName("id field is stored correctly")
        fun `id field stored`() {
            val transition = OverlayTransition(id = "tr_001", type = "slide")
            assertEquals("tr_001", transition.id)
        }

        @Test
        @DisplayName("type field is stored correctly")
        fun `type field stored`() {
            val transition = OverlayTransition(id = "x", type = "fade_out")
            assertEquals("fade_out", transition.type)
        }

        @Test
        @DisplayName("name defaults to empty string")
        fun `name defaults to empty string`() {
            val transition = OverlayTransition(id = "x", type = "zoom")
            assertEquals("", transition.name)
        }

        @Test
        @DisplayName("durationMs defaults to null")
        fun `durationMs defaults to null`() {
            val transition = OverlayTransition(id = "x", type = "zoom")
            assertNull(transition.durationMs)
        }

        @Test
        @DisplayName("custom name is stored correctly")
        fun `custom name stored`() {
            val transition = OverlayTransition(id = "x", type = "t", name = "Enter Slide")
            assertEquals("Enter Slide", transition.name)
        }

        @Test
        @DisplayName("custom durationMs is stored correctly")
        fun `custom durationMs stored`() {
            val transition = OverlayTransition(id = "x", type = "t", durationMs = 250L)
            assertEquals(250L, transition.durationMs)
        }

        @Test
        @DisplayName("all four fields stored when all are provided")
        fun `all fields stored when all provided`() {
            val transition = OverlayTransition(
                id = "full_tr",
                type = "circular_reveal",
                name = "Circular Reveal",
                durationMs = 600L
            )
            assertEquals("full_tr", transition.id)
            assertEquals("circular_reveal", transition.type)
            assertEquals("Circular Reveal", transition.name)
            assertEquals(600L, transition.durationMs)
        }

        @Test
        @DisplayName("zero durationMs is stored as-is")
        fun `zero durationMs stored`() {
            val transition = OverlayTransition(id = "x", type = "t", durationMs = 0L)
            assertEquals(0L, transition.durationMs)
        }

        @Test
        @DisplayName("negative durationMs is stored as-is (no clamping)")
        fun `negative durationMs stored without clamping`() {
            val transition = OverlayTransition(id = "x", type = "t", durationMs = -50L)
            assertEquals(-50L, transition.durationMs)
        }

        @Test
        @DisplayName("empty string id is stored as-is")
        fun `empty string id stored`() {
            val transition = OverlayTransition(id = "", type = "fade")
            assertEquals("", transition.id)
        }

        @Test
        @DisplayName("empty string type is stored as-is")
        fun `empty string type stored`() {
            val transition = OverlayTransition(id = "id", type = "")
            assertEquals("", transition.type)
        }
    }

    @Nested
    @DisplayName("Data class structural equality")
    inner class EqualityTests {

        @Test
        @DisplayName("two instances with identical values are equal")
        fun `equal by value`() {
            val a = OverlayTransition("id1", "slide", "Slide In", 300L)
            val b = OverlayTransition("id1", "slide", "Slide In", 300L)
            assertEquals(a, b)
        }

        @Test
        @DisplayName("different id makes instances not equal")
        fun `different id not equal`() {
            val a = OverlayTransition("id1", "fade")
            val b = OverlayTransition("id2", "fade")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("different type makes instances not equal")
        fun `different type not equal`() {
            val a = OverlayTransition("id1", "fade")
            val b = OverlayTransition("id1", "slide")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("different name makes instances not equal")
        fun `different name not equal`() {
            val a = OverlayTransition("id1", "fade", name = "Alpha Fade")
            val b = OverlayTransition("id1", "fade", name = "Beta Fade")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("empty vs non-empty name makes instances not equal")
        fun `empty vs non-empty name not equal`() {
            val a = OverlayTransition("id1", "fade")
            val b = OverlayTransition("id1", "fade", name = "Named")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("different durationMs makes instances not equal")
        fun `different durationMs not equal`() {
            val a = OverlayTransition("id1", "fade", durationMs = 100L)
            val b = OverlayTransition("id1", "fade", durationMs = 200L)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("null vs non-null durationMs makes instances not equal")
        fun `null vs non-null durationMs not equal`() {
            val a = OverlayTransition("id1", "fade", durationMs = null)
            val b = OverlayTransition("id1", "fade", durationMs = 0L)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("equal instances have the same hashCode")
        fun `equal instances have same hashCode`() {
            val a = OverlayTransition("id1", "slide", "Slide", 400L)
            val b = OverlayTransition("id1", "slide", "Slide", 400L)
            assertEquals(a.hashCode(), b.hashCode())
        }

        @Test
        @DisplayName("two default-constructed instances (identical) are equal")
        fun `two default name and null duration instances are equal`() {
            val a = OverlayTransition("same_id", "same_type")
            val b = OverlayTransition("same_id", "same_type")
            assertEquals(a, b)
        }
    }

    @Nested
    @DisplayName("copy()")
    inner class CopyTests {

        @Test
        @DisplayName("copy produces a distinct object reference")
        fun `copy produces new reference`() {
            val original = OverlayTransition("id", "fade")
            val copy = original.copy()
            assertNotSame(original, copy)
        }

        @Test
        @DisplayName("copy without arguments is structurally equal to original")
        fun `copy without changes equals original`() {
            val original = OverlayTransition("id", "slide", "Slide In", 500L)
            assertEquals(original, original.copy())
        }

        @Test
        @DisplayName("copy with new id retains type, name, and durationMs")
        fun `copy with new id retains other fields`() {
            val original = OverlayTransition("old_id", "zoom", "Zoom Out", 300L)
            val copy = original.copy(id = "new_id")
            assertEquals("new_id", copy.id)
            assertEquals("zoom", copy.type)
            assertEquals("Zoom Out", copy.name)
            assertEquals(300L, copy.durationMs)
        }

        @Test
        @DisplayName("copy with new durationMs retains id, type, and name")
        fun `copy with new durationMs retains other fields`() {
            val original = OverlayTransition("t_id", "fade", "Fade")
            val copy = original.copy(durationMs = 750L)
            assertEquals("t_id", copy.id)
            assertEquals("fade", copy.type)
            assertEquals("Fade", copy.name)
            assertEquals(750L, copy.durationMs)
        }

        @Test
        @DisplayName("copy can set durationMs to null even if previously set")
        fun `copy can null out durationMs`() {
            val original = OverlayTransition("id", "fade", durationMs = 200L)
            val copy = original.copy(durationMs = null)
            assertNull(copy.durationMs)
        }
    }

    @Nested
    @DisplayName("toString()")
    inner class ToStringTests {

        @Test
        @DisplayName("toString contains the id")
        fun `toString contains id`() {
            val transition = OverlayTransition("unique_transition_xyz", "fade")
            assertTrue(transition.toString().contains("unique_transition_xyz"))
        }

        @Test
        @DisplayName("toString contains the type")
        fun `toString contains type`() {
            val transition = OverlayTransition("id", "circular_dissolve")
            assertTrue(transition.toString().contains("circular_dissolve"))
        }
    }

    @Nested
    @DisplayName("Regression and boundary")
    inner class RegressionTests {

        @Test
        @DisplayName("Long.MAX_VALUE durationMs is stored correctly")
        fun `max long durationMs stored`() {
            val transition = OverlayTransition("id", "t", durationMs = Long.MAX_VALUE)
            assertEquals(Long.MAX_VALUE, transition.durationMs)
        }

        @Test
        @DisplayName("unicode characters in name are preserved")
        fun `unicode name preserved`() {
            val transition = OverlayTransition("id", "t", name = "トランジション")
            assertEquals("トランジション", transition.name)
        }

        @Test
        @DisplayName("OverlayTransition and OverlayAnimation are distinct types with no relation")
        fun `OverlayTransition is not an OverlayAnimation`() {
            val transition = OverlayTransition("id", "fade")
            assertTrue(transition !is OverlayAnimation)
        }
    }
}