package dev.aurakai.auraframefx.domains.aura.aura.animations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [HomeScreenTransitionConfig].
 *
 * This PR introduced [HomeScreenTransitionConfig], a data class with three fields:
 * - [HomeScreenTransitionConfig.type] defaulting to [HomeScreenTransitionType.GLOBE_ROTATE]
 * - [HomeScreenTransitionConfig.duration] defaulting to 500
 * - [HomeScreenTransitionConfig.properties] defaulting to an empty map
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("HomeScreenTransitionConfig")
class HomeScreenTransitionConfigTest {

    @Nested
    @DisplayName("Default values")
    inner class DefaultValueTests {

        @Test
        @DisplayName("default type is GLOBE_ROTATE")
        fun `default type is GLOBE_ROTATE`() {
            val config = HomeScreenTransitionConfig()
            assertEquals(HomeScreenTransitionType.GLOBE_ROTATE, config.type)
        }

        @Test
        @DisplayName("default duration is 500")
        fun `default duration is 500`() {
            val config = HomeScreenTransitionConfig()
            assertEquals(500, config.duration)
        }

        @Test
        @DisplayName("default properties is an empty map")
        fun `default properties is empty map`() {
            val config = HomeScreenTransitionConfig()
            assertTrue(config.properties.isEmpty())
        }
    }

    @Nested
    @DisplayName("Custom constructor values")
    inner class CustomConstructorTests {

        @Test
        @DisplayName("custom type is stored correctly")
        fun `custom type is stored`() {
            val config = HomeScreenTransitionConfig(type = HomeScreenTransitionType.HOLOGRAM)
            assertEquals(HomeScreenTransitionType.HOLOGRAM, config.type)
        }

        @Test
        @DisplayName("custom duration is stored correctly")
        fun `custom duration is stored`() {
            val config = HomeScreenTransitionConfig(duration = 1000)
            assertEquals(1000, config.duration)
        }

        @Test
        @DisplayName("custom properties map is stored correctly")
        fun `custom properties map is stored`() {
            val props = mapOf("speed" to 1.5f as Any, "easing" to "ease_in" as Any)
            val config = HomeScreenTransitionConfig(properties = props)
            assertEquals(props, config.properties)
        }

        @Test
        @DisplayName("all custom values are stored when all three are provided")
        fun `all fields stored when all provided`() {
            val config = HomeScreenTransitionConfig(
                type = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
                duration = 300,
                properties = mapOf("intensity" to 0.8 as Any)
            )
            assertEquals(HomeScreenTransitionType.DIGITAL_DECONSTRUCT, config.type)
            assertEquals(300, config.duration)
            assertEquals(1, config.properties.size)
        }

        @Test
        @DisplayName("zero duration is stored as-is")
        fun `zero duration is stored`() {
            val config = HomeScreenTransitionConfig(duration = 0)
            assertEquals(0, config.duration)
        }

        @Test
        @DisplayName("negative duration is stored as-is (no clamping)")
        fun `negative duration stored without clamping`() {
            val config = HomeScreenTransitionConfig(duration = -100)
            assertEquals(-100, config.duration)
        }
    }

    @Nested
    @DisplayName("Data class structural equality")
    inner class EqualityTests {

        @Test
        @DisplayName("two configs with identical values are equal")
        fun `configs with same values are equal`() {
            val a = HomeScreenTransitionConfig(
                type = HomeScreenTransitionType.PIXELATE,
                duration = 400,
                properties = emptyMap()
            )
            val b = HomeScreenTransitionConfig(
                type = HomeScreenTransitionType.PIXELATE,
                duration = 400,
                properties = emptyMap()
            )
            assertEquals(a, b)
        }

        @Test
        @DisplayName("two configs with different types are not equal")
        fun `different type makes configs not equal`() {
            val a = HomeScreenTransitionConfig(type = HomeScreenTransitionType.GLOBE_ROTATE)
            val b = HomeScreenTransitionConfig(type = HomeScreenTransitionType.HOLOGRAM)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("two configs with different durations are not equal")
        fun `different duration makes configs not equal`() {
            val a = HomeScreenTransitionConfig(duration = 100)
            val b = HomeScreenTransitionConfig(duration = 200)
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("two configs with different properties maps are not equal")
        fun `different properties makes configs not equal`() {
            val a = HomeScreenTransitionConfig(properties = mapOf("k" to "v" as Any))
            val b = HomeScreenTransitionConfig(properties = emptyMap())
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("hashCode is equal for structurally equal configs")
        fun `equal configs have same hashCode`() {
            val a = HomeScreenTransitionConfig()
            val b = HomeScreenTransitionConfig()
            assertEquals(a.hashCode(), b.hashCode())
        }
    }

    @Nested
    @DisplayName("copy()")
    inner class CopyTests {

        @Test
        @DisplayName("copy produces a distinct object reference")
        fun `copy produces new object reference`() {
            val original = HomeScreenTransitionConfig()
            val copy = original.copy()
            assertNotSame(original, copy)
        }

        @Test
        @DisplayName("copy with no arguments is structurally equal to original")
        fun `copy without changes is equal to original`() {
            val original = HomeScreenTransitionConfig(
                type = HomeScreenTransitionType.HOLOGRAM,
                duration = 250
            )
            assertEquals(original, original.copy())
        }

        @Test
        @DisplayName("copy with modified type retains other fields")
        fun `copy changes only specified field`() {
            val original = HomeScreenTransitionConfig(
                type = HomeScreenTransitionType.GLOBE_ROTATE,
                duration = 600,
                properties = mapOf("x" to 1 as Any)
            )
            val copy = original.copy(type = HomeScreenTransitionType.PIXELATE)
            assertEquals(HomeScreenTransitionType.PIXELATE, copy.type)
            assertEquals(600, copy.duration)
            assertEquals(original.properties, copy.properties)
        }

        @Test
        @DisplayName("copy with modified duration retains type and properties")
        fun `copy with new duration retains other fields`() {
            val original = HomeScreenTransitionConfig(
                type = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
                duration = 300
            )
            val copy = original.copy(duration = 750)
            assertEquals(HomeScreenTransitionType.DIGITAL_DECONSTRUCT, copy.type)
            assertEquals(750, copy.duration)
            assertTrue(copy.properties.isEmpty())
        }
    }

    @Nested
    @DisplayName("toString()")
    inner class ToStringTests {

        @Test
        @DisplayName("toString contains the type name")
        fun `toString contains type`() {
            val config = HomeScreenTransitionConfig(type = HomeScreenTransitionType.HOLOGRAM)
            assertTrue(config.toString().contains("HOLOGRAM"))
        }

        @Test
        @DisplayName("toString contains the duration value")
        fun `toString contains duration`() {
            val config = HomeScreenTransitionConfig(duration = 999)
            assertTrue(config.toString().contains("999"))
        }
    }

    @Nested
    @DisplayName("Regression: all four transition types can be set")
    inner class RegressionTests {

        @Test
        @DisplayName("all HomeScreenTransitionType values can be assigned to type field")
        fun `all transition types can be used as config type`() {
            HomeScreenTransitionType.entries.forEach { transitionType ->
                val config = HomeScreenTransitionConfig(type = transitionType)
                assertEquals(transitionType, config.type)
            }
        }
    }
}