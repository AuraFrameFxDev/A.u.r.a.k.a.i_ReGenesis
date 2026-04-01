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
 * Unit tests for [OverlayAnimation].
 *
 * This PR introduced [OverlayAnimation] as a data class with:
 * - [OverlayAnimation.id] — required, used as a map key
 * - [OverlayAnimation.type] — required, e.g. "fade_in", "slide_up"
 * - [OverlayAnimation.durationMs] — nullable, optional
 * - [OverlayAnimation.targetProperty] — nullable, optional (e.g., "alpha", "translationY")
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("OverlayAnimation")
class OverlayAnimationTest {

    @Nested
    @DisplayName("Constructor and field storage")
    inner class ConstructorTests {

        @Test
        @DisplayName("id field is stored correctly")
        fun `id field stored`() {
            val anim = OverlayAnimation(id = "anim_001", type = "fade_in")
            assertEquals("anim_001", anim.id)
        }

        @Test
        @DisplayName("type field is stored correctly")
        fun `type field stored`() {
            val anim = OverlayAnimation(id = "x", type = "slide_up")
            assertEquals("slide_up", anim.type)
        }

        @Test
        @DisplayName("durationMs defaults to null")
        fun `durationMs defaults to null`() {
            val anim = OverlayAnimation(id = "x", type = "fade_out")
            assertNull(anim.durationMs)
        }

        @Test
        @DisplayName("targetProperty defaults to null")
        fun `targetProperty defaults to null`() {
            val anim = OverlayAnimation(id = "x", type = "fade_out")
            assertNull(anim.targetProperty)
        }

        @Test
        @DisplayName("custom durationMs is stored when provided")
        fun `custom durationMs stored`() {
            val anim = OverlayAnimation(id = "a", type = "zoom", durationMs = 350L)
            assertEquals(350L, anim.durationMs)
        }

        @Test
        @DisplayName("custom targetProperty is stored when provided")
        fun `custom targetProperty stored`() {
            val anim = OverlayAnimation(id = "b", type = "fade", targetProperty = "alpha")
            assertEquals("alpha", anim.targetProperty)
        }

        @Test
        @DisplayName("all four fields stored when all are provided")
        fun `all fields stored`() {
            val anim = OverlayAnimation(
                id = "full_id",
                type = "slide_up",
                durationMs = 500L,
                targetProperty = "translationY"
            )
            assertEquals("full_id", anim.id)
            assertEquals("slide_up", anim.type)
            assertEquals(500L, anim.durationMs)
            assertEquals("translationY", anim.targetProperty)
        }

        @Test
        @DisplayName("zero durationMs is stored as-is")
        fun `zero durationMs stored`() {
            val anim = OverlayAnimation(id = "x", type = "t", durationMs = 0L)
            assertEquals(0L, anim.durationMs)
        }

        @Test
        @DisplayName("negative durationMs is stored as-is (no clamping)")
        fun `negative durationMs stored without clamping`() {
            val anim = OverlayAnimation(id = "x", type = "t", durationMs = -1L)
            assertEquals(-1L, anim.durationMs)
        }
    }

    @Nested
    @DisplayName("Data class structural equality")
    inner class EqualityTests {

        @Test
        @DisplayName("two instances with identical values are equal")
        fun `equal by value`() {
            val a = OverlayAnimation("id1", "fade_in", 200L, "alpha")
            val b = OverlayAnimation("id1", "fade_in", 200L, "alpha")
            assertEquals(a, b)
        }

        @Test
        @DisplayName("different id makes instances not equal")
        fun `different id not equal`() {
            val a = OverlayAnimation("id1", "fade_in")
            val b = OverlayAnimation("id2", "fade_in")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("different type makes instances not equal")
        fun `different type not equal`() {
            val a = OverlayAnimation("id1", "fade_in")
            val b = OverlayAnimation("id1", "slide_up")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("different durationMs makes instances not equal")
        fun `different durationMs not equal`() {
            val a = OverlayAnimation("id1", "fade", durationMs = 100L)
            val b = OverlayAnimation("id1", "fade", durationMs = 200L)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("null vs non-null durationMs makes instances not equal")
        fun `null vs non-null durationMs not equal`() {
            val a = OverlayAnimation("id1", "fade", durationMs = null)
            val b = OverlayAnimation("id1", "fade", durationMs = 100L)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("different targetProperty makes instances not equal")
        fun `different targetProperty not equal`() {
            val a = OverlayAnimation("id1", "fade", targetProperty = "alpha")
            val b = OverlayAnimation("id1", "fade", targetProperty = "scaleX")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("equal instances have the same hashCode")
        fun `equal instances have same hashCode`() {
            val a = OverlayAnimation("id1", "fade_in", 300L, "alpha")
            val b = OverlayAnimation("id1", "fade_in", 300L, "alpha")
            assertEquals(a.hashCode(), b.hashCode())
        }
    }

    @Nested
    @DisplayName("copy()")
    inner class CopyTests {

        @Test
        @DisplayName("copy produces a distinct object reference")
        fun `copy produces new reference`() {
            val original = OverlayAnimation("id", "fade")
            val copy = original.copy()
            assertNotSame(original, copy)
        }

        @Test
        @DisplayName("copy without arguments is structurally equal to original")
        fun `copy without changes equals original`() {
            val original = OverlayAnimation("id", "slide_up", 400L, "translationY")
            assertEquals(original, original.copy())
        }

        @Test
        @DisplayName("copy with new id retains other fields")
        fun `copy with new id retains type and optionals`() {
            val original = OverlayAnimation("old_id", "zoom", 500L, "scaleX")
            val copy = original.copy(id = "new_id")
            assertEquals("new_id", copy.id)
            assertEquals("zoom", copy.type)
            assertEquals(500L, copy.durationMs)
            assertEquals("scaleX", copy.targetProperty)
        }

        @Test
        @DisplayName("copy with new type retains id and optionals")
        fun `copy with new type retains id`() {
            val original = OverlayAnimation("anim_id", "fade", 200L)
            val copy = original.copy(type = "bounce")
            assertEquals("anim_id", copy.id)
            assertEquals("bounce", copy.type)
        }
    }

    @Nested
    @DisplayName("toString()")
    inner class ToStringTests {

        @Test
        @DisplayName("toString contains the id")
        fun `toString contains id`() {
            val anim = OverlayAnimation("unique_id_xyz", "fade")
            assertTrue(anim.toString().contains("unique_id_xyz"))
        }

        @Test
        @DisplayName("toString contains the type")
        fun `toString contains type`() {
            val anim = OverlayAnimation("id", "slide_from_bottom")
            assertTrue(anim.toString().contains("slide_from_bottom"))
        }
    }

    @Nested
    @DisplayName("Usage as map key (id field)")
    inner class MapKeyTests {

        @Test
        @DisplayName("id can be used as a map key to store and retrieve the animation")
        fun `id usable as map key`() {
            val anim1 = OverlayAnimation("key_one", "fade_in", 100L)
            val anim2 = OverlayAnimation("key_two", "slide_up", 200L)
            val map = mapOf(anim1.id to anim1, anim2.id to anim2)
            assertEquals(anim1, map["key_one"])
            assertEquals(anim2, map["key_two"])
        }

        @Test
        @DisplayName("two animations with the same id but different types are different map entries")
        fun `same id but different type are distinct instances`() {
            val a = OverlayAnimation("shared_id", "fade_in")
            val b = OverlayAnimation("shared_id", "fade_out")
            assertNotEquals(a, b)
        }
    }
}