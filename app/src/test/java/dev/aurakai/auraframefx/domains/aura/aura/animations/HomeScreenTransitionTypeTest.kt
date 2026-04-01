package dev.aurakai.auraframefx.domains.aura.aura.animations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [HomeScreenTransitionType].
 *
 * This PR introduced the [HomeScreenTransitionType] enum with four values:
 * GLOBE_ROTATE, DIGITAL_DECONSTRUCT, HOLOGRAM, and PIXELATE.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("HomeScreenTransitionType")
class HomeScreenTransitionTypeTest {

    @Nested
    @DisplayName("Enum values")
    inner class EnumValuesTests {

        @Test
        @DisplayName("exactly four transition types are declared")
        fun `enum has exactly four values`() {
            assertEquals(4, HomeScreenTransitionType.entries.size)
        }

        @Test
        @DisplayName("GLOBE_ROTATE is declared")
        fun `GLOBE_ROTATE entry exists`() {
            assertNotNull(HomeScreenTransitionType.GLOBE_ROTATE)
        }

        @Test
        @DisplayName("DIGITAL_DECONSTRUCT is declared")
        fun `DIGITAL_DECONSTRUCT entry exists`() {
            assertNotNull(HomeScreenTransitionType.DIGITAL_DECONSTRUCT)
        }

        @Test
        @DisplayName("HOLOGRAM is declared")
        fun `HOLOGRAM entry exists`() {
            assertNotNull(HomeScreenTransitionType.HOLOGRAM)
        }

        @Test
        @DisplayName("PIXELATE is declared")
        fun `PIXELATE entry exists`() {
            assertNotNull(HomeScreenTransitionType.PIXELATE)
        }

        @Test
        @DisplayName("entries list contains all four expected values in order")
        fun `entries are in expected declaration order`() {
            val entries = HomeScreenTransitionType.entries
            assertEquals(HomeScreenTransitionType.GLOBE_ROTATE, entries[0])
            assertEquals(HomeScreenTransitionType.DIGITAL_DECONSTRUCT, entries[1])
            assertEquals(HomeScreenTransitionType.HOLOGRAM, entries[2])
            assertEquals(HomeScreenTransitionType.PIXELATE, entries[3])
        }
    }

    @Nested
    @DisplayName("valueOf() lookup")
    inner class ValueOfTests {

        @Test
        @DisplayName("valueOf returns GLOBE_ROTATE for its name string")
        fun `valueOf GLOBE_ROTATE`() {
            assertEquals(
                HomeScreenTransitionType.GLOBE_ROTATE,
                HomeScreenTransitionType.valueOf("GLOBE_ROTATE")
            )
        }

        @Test
        @DisplayName("valueOf returns DIGITAL_DECONSTRUCT for its name string")
        fun `valueOf DIGITAL_DECONSTRUCT`() {
            assertEquals(
                HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
                HomeScreenTransitionType.valueOf("DIGITAL_DECONSTRUCT")
            )
        }

        @Test
        @DisplayName("valueOf returns HOLOGRAM for its name string")
        fun `valueOf HOLOGRAM`() {
            assertEquals(
                HomeScreenTransitionType.HOLOGRAM,
                HomeScreenTransitionType.valueOf("HOLOGRAM")
            )
        }

        @Test
        @DisplayName("valueOf returns PIXELATE for its name string")
        fun `valueOf PIXELATE`() {
            assertEquals(
                HomeScreenTransitionType.PIXELATE,
                HomeScreenTransitionType.valueOf("PIXELATE")
            )
        }

        @Test
        @DisplayName("valueOf throws IllegalArgumentException for unknown name")
        fun `valueOf throws for unknown name`() {
            org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                HomeScreenTransitionType.valueOf("UNKNOWN_TYPE")
            }
        }
    }

    @Nested
    @DisplayName("name property")
    inner class NamePropertyTests {

        @Test
        @DisplayName("name property returns the declared identifier string")
        fun `name returns correct string for each entry`() {
            assertEquals("GLOBE_ROTATE", HomeScreenTransitionType.GLOBE_ROTATE.name)
            assertEquals("DIGITAL_DECONSTRUCT", HomeScreenTransitionType.DIGITAL_DECONSTRUCT.name)
            assertEquals("HOLOGRAM", HomeScreenTransitionType.HOLOGRAM.name)
            assertEquals("PIXELATE", HomeScreenTransitionType.PIXELATE.name)
        }
    }

    @Nested
    @DisplayName("ordinal property")
    inner class OrdinalTests {

        @Test
        @DisplayName("ordinals are zero-based and sequential")
        fun `ordinals are sequential starting at zero`() {
            assertEquals(0, HomeScreenTransitionType.GLOBE_ROTATE.ordinal)
            assertEquals(1, HomeScreenTransitionType.DIGITAL_DECONSTRUCT.ordinal)
            assertEquals(2, HomeScreenTransitionType.HOLOGRAM.ordinal)
            assertEquals(3, HomeScreenTransitionType.PIXELATE.ordinal)
        }
    }

    @Nested
    @DisplayName("Equality and identity")
    inner class EqualityTests {

        @Test
        @DisplayName("same enum constant references are equal")
        fun `same enum constant is equal to itself`() {
            assertEquals(HomeScreenTransitionType.GLOBE_ROTATE, HomeScreenTransitionType.GLOBE_ROTATE)
        }

        @Test
        @DisplayName("different enum constants are not equal")
        fun `different enum constants are not equal`() {
            assertTrue(HomeScreenTransitionType.GLOBE_ROTATE != HomeScreenTransitionType.HOLOGRAM)
            assertTrue(HomeScreenTransitionType.DIGITAL_DECONSTRUCT != HomeScreenTransitionType.PIXELATE)
        }

        @Test
        @DisplayName("entries can be used in when-expression exhaustively")
        fun `enum can be used in when expression`() {
            val result = when (HomeScreenTransitionType.DIGITAL_DECONSTRUCT) {
                HomeScreenTransitionType.GLOBE_ROTATE -> "globe"
                HomeScreenTransitionType.DIGITAL_DECONSTRUCT -> "deconstruct"
                HomeScreenTransitionType.HOLOGRAM -> "hologram"
                HomeScreenTransitionType.PIXELATE -> "pixelate"
            }
            assertEquals("deconstruct", result)
        }
    }
}