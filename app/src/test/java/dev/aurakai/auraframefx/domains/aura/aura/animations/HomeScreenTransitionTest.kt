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
 * Unit tests for the data classes introduced in HomeScreenTransition.kt by this PR:
 * - [TransitionProperties]
 * - [HomeScreenTransitionEffect]
 * - [HomeScreenTransitionEffectsConfig]
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("HomeScreenTransition data classes")
class HomeScreenTransitionTest {

    // ─── TransitionProperties ─────────────────────────────────────────────────

    @Nested
    @DisplayName("TransitionProperties")
    inner class TransitionPropertiesTests {

        @Test
        @DisplayName("default duration is 300ms")
        fun `default duration is 300`() {
            val props = TransitionProperties()
            assertEquals(300L, props.duration)
        }

        @Test
        @DisplayName("default direction is null")
        fun `default direction is null`() {
            val props = TransitionProperties()
            assertNull(props.direction)
        }

        @Test
        @DisplayName("default interpolator is 'linear'")
        fun `default interpolator is linear`() {
            val props = TransitionProperties()
            assertEquals("linear", props.interpolator)
        }

        @Test
        @DisplayName("custom duration is stored correctly")
        fun `custom duration stored`() {
            val props = TransitionProperties(duration = 600L)
            assertEquals(600L, props.duration)
        }

        @Test
        @DisplayName("custom direction is stored correctly")
        fun `custom direction stored`() {
            val props = TransitionProperties(direction = "left_to_right")
            assertEquals("left_to_right", props.direction)
        }

        @Test
        @DisplayName("custom interpolator is stored correctly")
        fun `custom interpolator stored`() {
            val props = TransitionProperties(interpolator = "ease_in_out")
            assertEquals("ease_in_out", props.interpolator)
        }

        @Test
        @DisplayName("all custom fields are stored when all are provided")
        fun `all fields stored when all provided`() {
            val props = TransitionProperties(
                duration = 450L,
                direction = "top_to_bottom",
                interpolator = "accelerate"
            )
            assertEquals(450L, props.duration)
            assertEquals("top_to_bottom", props.direction)
            assertEquals("accelerate", props.interpolator)
        }

        @Test
        @DisplayName("two TransitionProperties with identical values are equal")
        fun `equal by value`() {
            val a = TransitionProperties(duration = 200L, direction = "up", interpolator = "linear")
            val b = TransitionProperties(duration = 200L, direction = "up", interpolator = "linear")
            assertEquals(a, b)
        }

        @Test
        @DisplayName("different durations make properties not equal")
        fun `different duration not equal`() {
            val a = TransitionProperties(duration = 100L)
            val b = TransitionProperties(duration = 200L)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("copy with new duration retains other defaults")
        fun `copy changes only duration`() {
            val original = TransitionProperties()
            val copy = original.copy(duration = 1000L)
            assertEquals(1000L, copy.duration)
            assertNull(copy.direction)
            assertEquals("linear", copy.interpolator)
        }

        @Test
        @DisplayName("zero duration is stored as-is")
        fun `zero duration stored`() {
            val props = TransitionProperties(duration = 0L)
            assertEquals(0L, props.duration)
        }

        @Test
        @DisplayName("toString contains the duration value")
        fun `toString contains duration`() {
            val props = TransitionProperties(duration = 777L)
            assertTrue(props.toString().contains("777"))
        }
    }

    // ─── HomeScreenTransitionEffect ───────────────────────────────────────────

    @Nested
    @DisplayName("HomeScreenTransitionEffect")
    inner class HomeScreenTransitionEffectTests {

        @Test
        @DisplayName("type field is stored correctly")
        fun `type field stored`() {
            val effect = HomeScreenTransitionEffect(type = "slide")
            assertEquals("slide", effect.type)
        }

        @Test
        @DisplayName("properties defaults to null")
        fun `properties defaults to null`() {
            val effect = HomeScreenTransitionEffect(type = "fade")
            assertNull(effect.properties)
        }

        @Test
        @DisplayName("custom properties are stored when provided")
        fun `custom properties stored`() {
            val props = TransitionProperties(duration = 250L, direction = "right")
            val effect = HomeScreenTransitionEffect(type = "slide", properties = props)
            assertEquals(props, effect.properties)
            assertEquals(250L, effect.properties!!.duration)
        }

        @Test
        @DisplayName("two effects with same type and null properties are equal")
        fun `equal by value with null properties`() {
            val a = HomeScreenTransitionEffect(type = "zoom")
            val b = HomeScreenTransitionEffect(type = "zoom")
            assertEquals(a, b)
        }

        @Test
        @DisplayName("effects with different types are not equal")
        fun `different types not equal`() {
            val a = HomeScreenTransitionEffect(type = "slide")
            val b = HomeScreenTransitionEffect(type = "fade")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("copy produces a distinct reference but equal value")
        fun `copy is structurally equal but distinct reference`() {
            val original = HomeScreenTransitionEffect(type = "hologram")
            val copy = original.copy()
            assertEquals(original, copy)
            assertNotSame(original, copy)
        }

        @Test
        @DisplayName("copy with new type retains properties")
        fun `copy with new type retains properties`() {
            val props = TransitionProperties(duration = 100L)
            val original = HomeScreenTransitionEffect(type = "fade", properties = props)
            val copy = original.copy(type = "slide")
            assertEquals("slide", copy.type)
            assertEquals(props, copy.properties)
        }

        @Test
        @DisplayName("toString contains the type value")
        fun `toString contains type`() {
            val effect = HomeScreenTransitionEffect(type = "pixelate_dissolve")
            assertTrue(effect.toString().contains("pixelate_dissolve"))
        }

        @Test
        @DisplayName("empty string type is stored as-is")
        fun `empty string type stored`() {
            val effect = HomeScreenTransitionEffect(type = "")
            assertEquals("", effect.type)
        }
    }

    // ─── HomeScreenTransitionEffectsConfig ────────────────────────────────────

    @Nested
    @DisplayName("HomeScreenTransitionEffectsConfig")
    inner class HomeScreenTransitionEffectsConfigTests {

        @Test
        @DisplayName("defaultOutgoingEffect defaults to null")
        fun `defaultOutgoingEffect defaults to null`() {
            val config = HomeScreenTransitionEffectsConfig()
            assertNull(config.defaultOutgoingEffect)
        }

        @Test
        @DisplayName("defaultIncomingEffect defaults to null")
        fun `defaultIncomingEffect defaults to null`() {
            val config = HomeScreenTransitionEffectsConfig()
            assertNull(config.defaultIncomingEffect)
        }

        @Test
        @DisplayName("custom outgoing effect is stored correctly")
        fun `custom outgoing effect stored`() {
            val effect = HomeScreenTransitionEffect(type = "slide")
            val config = HomeScreenTransitionEffectsConfig(defaultOutgoingEffect = effect)
            assertEquals(effect, config.defaultOutgoingEffect)
        }

        @Test
        @DisplayName("custom incoming effect is stored correctly")
        fun `custom incoming effect stored`() {
            val effect = HomeScreenTransitionEffect(type = "fade")
            val config = HomeScreenTransitionEffectsConfig(defaultIncomingEffect = effect)
            assertEquals(effect, config.defaultIncomingEffect)
        }

        @Test
        @DisplayName("both effects can be set independently")
        fun `outgoing and incoming effects set independently`() {
            val outgoing = HomeScreenTransitionEffect(type = "zoom_out")
            val incoming = HomeScreenTransitionEffect(type = "zoom_in")
            val config = HomeScreenTransitionEffectsConfig(
                defaultOutgoingEffect = outgoing,
                defaultIncomingEffect = incoming
            )
            assertEquals(outgoing, config.defaultOutgoingEffect)
            assertEquals(incoming, config.defaultIncomingEffect)
        }

        @Test
        @DisplayName("two configs with both effects null are equal")
        fun `configs with null effects are equal`() {
            val a = HomeScreenTransitionEffectsConfig()
            val b = HomeScreenTransitionEffectsConfig()
            assertEquals(a, b)
        }

        @Test
        @DisplayName("configs with different outgoing effects are not equal")
        fun `different outgoing effects not equal`() {
            val a = HomeScreenTransitionEffectsConfig(
                defaultOutgoingEffect = HomeScreenTransitionEffect("fade")
            )
            val b = HomeScreenTransitionEffectsConfig(
                defaultOutgoingEffect = HomeScreenTransitionEffect("slide")
            )
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("copy with new outgoing effect retains incoming effect")
        fun `copy with new outgoing retains incoming`() {
            val incoming = HomeScreenTransitionEffect(type = "reveal")
            val original = HomeScreenTransitionEffectsConfig(defaultIncomingEffect = incoming)
            val outgoing = HomeScreenTransitionEffect(type = "dissolve")
            val copy = original.copy(defaultOutgoingEffect = outgoing)
            assertEquals(outgoing, copy.defaultOutgoingEffect)
            assertEquals(incoming, copy.defaultIncomingEffect)
        }

        @Test
        @DisplayName("hashCode is consistent for equal configs")
        fun `equal configs have same hashCode`() {
            val a = HomeScreenTransitionEffectsConfig()
            val b = HomeScreenTransitionEffectsConfig()
            assertEquals(a.hashCode(), b.hashCode())
        }

        @Test
        @DisplayName("config with nested TransitionProperties stores them correctly")
        fun `nested properties are preserved`() {
            val props = TransitionProperties(duration = 800L, direction = "bottom_to_top")
            val effect = HomeScreenTransitionEffect(type = "swipe", properties = props)
            val config = HomeScreenTransitionEffectsConfig(defaultIncomingEffect = effect)

            assertEquals("swipe", config.defaultIncomingEffect!!.type)
            assertEquals(800L, config.defaultIncomingEffect!!.properties!!.duration)
            assertEquals("bottom_to_top", config.defaultIncomingEffect!!.properties!!.direction)
        }
    }
}