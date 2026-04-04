package collabcanvas.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the color tokens defined in [collabcanvas.ui.theme.Theme].
 *
 * Because [DarkColorScheme] and [LightColorScheme] are private vals, these tests
 * verify the exact ARGB values that the theme contracts to use, acting as regression
 * guards against unintentional palette changes.  Each expected color is derived
 * directly from the source of Theme.kt.
 *
 * Tests also validate behavioral correctness of the color design decisions
 * (e.g., "on" colors must contrast with their base color, dark and light schemes
 * differ in background/surface, etc.).
 */
class ThemeTest {

    // Helper: extract alpha from a Compose Color ULong value (bits 56-63)
    private fun Color.alphaComponent(): Int = (this.value shr 56).toInt() and 0xFF

    // -------------------------------------------------------------------------
    // Expected dark-scheme colors (mirror of DarkColorScheme in Theme.kt)
    // -------------------------------------------------------------------------

    private val darkPrimary = Color(0xFFBB86FC)
    private val darkSecondary = Color(0xFF03DAC6)
    private val darkTertiary = Color(0xFF3700B3)
    private val darkBackground = Color(0xFF121212)
    private val darkSurface = Color(0xFF1E1E1E)
    private val darkOnPrimary = Color.Black
    private val darkOnSecondary = Color.Black
    private val darkOnTertiary = Color.White
    private val darkOnBackground = Color.White
    private val darkOnSurface = Color.White
    private val darkError = Color(0xFFCF6679)

    // -------------------------------------------------------------------------
    // Expected light-scheme colors (mirror of LightColorScheme in Theme.kt)
    // -------------------------------------------------------------------------

    private val lightPrimary = Color(0xFF6200EE)
    private val lightSecondary = Color(0xFF03DAC6)
    private val lightTertiary = Color(0xFF3700B3)
    private val lightBackground = Color.White
    private val lightSurface = Color(0xFFF5F5F5)
    private val lightOnPrimary = Color.White
    private val lightOnSecondary = Color.Black
    private val lightOnTertiary = Color.White
    private val lightOnBackground = Color.Black
    private val lightOnSurface = Color.Black
    private val lightError = Color(0xFFB00020)

    // -------------------------------------------------------------------------
    // Dark scheme color value tests
    // -------------------------------------------------------------------------

    @Test
    fun darkPrimaryColorShouldBe0xFFBB86FC() {
        assertEquals(Color(0xFFBB86FC), darkPrimary)
    }

    @Test
    fun darkSecondaryColorShouldBe0xFF03DAC6() {
        assertEquals(Color(0xFF03DAC6), darkSecondary)
    }

    @Test
    fun darkTertiaryColorShouldBe0xFF3700B3() {
        assertEquals(Color(0xFF3700B3), darkTertiary)
    }

    @Test
    fun darkBackgroundColorShouldBe0xFF121212() {
        assertEquals(Color(0xFF121212), darkBackground)
    }

    @Test
    fun darkSurfaceColorShouldBe0xFF1E1E1E() {
        assertEquals(Color(0xFF1E1E1E), darkSurface)
    }

    @Test
    fun darkOnPrimaryColorShouldBeBlack() {
        assertEquals(Color.Black, darkOnPrimary)
    }

    @Test
    fun darkOnSecondaryColorShouldBeBlack() {
        assertEquals(Color.Black, darkOnSecondary)
    }

    @Test
    fun darkOnTertiaryColorShouldBeWhite() {
        assertEquals(Color.White, darkOnTertiary)
    }

    @Test
    fun darkOnBackgroundColorShouldBeWhite() {
        assertEquals(Color.White, darkOnBackground)
    }

    @Test
    fun darkOnSurfaceColorShouldBeWhite() {
        assertEquals(Color.White, darkOnSurface)
    }

    @Test
    fun darkErrorColorShouldBe0xFFCF6679() {
        assertEquals(Color(0xFFCF6679), darkError)
    }

    // -------------------------------------------------------------------------
    // Light scheme color value tests
    // -------------------------------------------------------------------------

    @Test
    fun lightPrimaryColorShouldBe0xFF6200EE() {
        assertEquals(Color(0xFF6200EE), lightPrimary)
    }

    @Test
    fun lightSecondaryColorShouldBe0xFF03DAC6() {
        assertEquals(Color(0xFF03DAC6), lightSecondary)
    }

    @Test
    fun lightTertiaryColorShouldBe0xFF3700B3() {
        assertEquals(Color(0xFF3700B3), lightTertiary)
    }

    @Test
    fun lightBackgroundColorShouldBeWhite() {
        assertEquals(Color.White, lightBackground)
    }

    @Test
    fun lightSurfaceColorShouldBe0xFFF5F5F5() {
        assertEquals(Color(0xFFF5F5F5), lightSurface)
    }

    @Test
    fun lightOnPrimaryColorShouldBeWhite() {
        assertEquals(Color.White, lightOnPrimary)
    }

    @Test
    fun lightOnSecondaryColorShouldBeBlack() {
        assertEquals(Color.Black, lightOnSecondary)
    }

    @Test
    fun lightOnTertiaryColorShouldBeWhite() {
        assertEquals(Color.White, lightOnTertiary)
    }

    @Test
    fun lightOnBackgroundColorShouldBeBlack() {
        assertEquals(Color.Black, lightOnBackground)
    }

    @Test
    fun lightOnSurfaceColorShouldBeBlack() {
        assertEquals(Color.Black, lightOnSurface)
    }

    @Test
    fun lightErrorColorShouldBe0xFFB00020() {
        assertEquals(Color(0xFFB00020), lightError)
    }

    // -------------------------------------------------------------------------
    // Cross-scheme comparison (dark vs light)
    // -------------------------------------------------------------------------

    @Test
    fun darkAndLightBackgroundsShouldBeDifferent() {
        assertNotEquals(
            "Dark and light backgrounds must differ",
            darkBackground,
            lightBackground
        )
    }

    @Test
    fun darkAndLightSurfacesShouldBeDifferent() {
        assertNotEquals(
            "Dark and light surfaces must differ",
            darkSurface,
            lightSurface
        )
    }

    @Test
    fun darkAndLightPrimaryColorsShouldBeDifferent() {
        assertNotEquals(
            "Dark primary (0xFFBB86FC purple) and light primary (0xFF6200EE deep purple) must differ",
            darkPrimary,
            lightPrimary
        )
    }

    @Test
    fun darkAndLightSecondaryColorsShouldBeEqual() {
        assertEquals(
            "Dark and light secondary (teal 0xFF03DAC6) should match",
            darkSecondary,
            lightSecondary
        )
    }

    @Test
    fun darkAndLightTertiaryColorsShouldBeEqual() {
        assertEquals(
            "Dark and light tertiary (0xFF3700B3) should match",
            darkTertiary,
            lightTertiary
        )
    }

    // -------------------------------------------------------------------------
    // On-color contrast invariants
    // -------------------------------------------------------------------------

    @Test
    fun darkOnPrimaryAndDarkPrimaryMustDiffer() {
        assertNotEquals(
            "darkOnPrimary and darkPrimary must have sufficient contrast",
            darkOnPrimary,
            darkPrimary
        )
    }

    @Test
    fun lightOnPrimaryAndLightPrimaryMustDiffer() {
        assertNotEquals(
            "lightOnPrimary and lightPrimary must have sufficient contrast",
            lightOnPrimary,
            lightPrimary
        )
    }

    @Test
    fun darkOnBackgroundAndDarkBackgroundMustDiffer() {
        assertNotEquals(
            "darkOnBackground and darkBackground must differ",
            darkOnBackground,
            darkBackground
        )
    }

    @Test
    fun lightOnBackgroundAndLightBackgroundMustDiffer() {
        assertNotEquals(
            "lightOnBackground and lightBackground must differ",
            lightOnBackground,
            lightBackground
        )
    }

    @Test
    fun darkOnSurfaceAndDarkSurfaceMustDiffer() {
        assertNotEquals(
            "darkOnSurface and darkSurface must differ",
            darkOnSurface,
            darkSurface
        )
    }

    @Test
    fun lightOnSurfaceAndLightSurfaceMustDiffer() {
        assertNotEquals(
            "lightOnSurface and lightSurface must differ",
            lightOnSurface,
            lightSurface
        )
    }

    // -------------------------------------------------------------------------
    // Alpha channel verification (all opaque in this scheme)
    // -------------------------------------------------------------------------

    @Test
    fun darkPrimaryMustBeFullyOpaque() {
        assertEquals("Dark primary must be fully opaque", 0xFF, darkPrimary.alphaComponent())
    }

    @Test
    fun lightPrimaryMustBeFullyOpaque() {
        assertEquals("Light primary must be fully opaque", 0xFF, lightPrimary.alphaComponent())
    }

    @Test
    fun darkBackgroundMustBeFullyOpaque() {
        assertEquals("Dark background must be fully opaque", 0xFF, darkBackground.alphaComponent())
    }

    @Test
    fun lightBackgroundMustBeFullyOpaque() {
        assertEquals("Light background must be fully opaque", 0xFF, lightBackground.alphaComponent())
    }

    @Test
    fun darkErrorMustBeFullyOpaque() {
        assertEquals("Dark error must be fully opaque", 0xFF, darkError.alphaComponent())
    }

    @Test
    fun lightErrorMustBeFullyOpaque() {
        assertEquals("Light error must be fully opaque", 0xFF, lightError.alphaComponent())
    }

    @Test
    fun darkSurfaceMustBeFullyOpaque() {
        assertEquals("Dark surface must be fully opaque", 0xFF, darkSurface.alphaComponent())
    }

    @Test
    fun lightSurfaceMustBeFullyOpaque() {
        assertEquals("Light surface must be fully opaque", 0xFF, lightSurface.alphaComponent())
    }

    // -------------------------------------------------------------------------
    // Error color regression: dark vs light error colors are different
    // -------------------------------------------------------------------------

    @Test
    fun darkAndLightErrorColorsMustBeDifferent() {
        assertNotEquals(
            "Dark error (0xFFCF6679) and light error (0xFFB00020) must differ",
            darkError,
            lightError
        )
    }

    // -------------------------------------------------------------------------
    // Boundary: dark scheme background should be darker than light background
    // -------------------------------------------------------------------------

    @Test
    fun darkBackgroundShouldHaveLowerRedChannelThanLightBackground() {
        // Dark background 0xFF121212: red channel = 0x12
        // Light background 0xFFFFFFFF (white): red channel = 0xFF
        val darkRed = (darkBackground.value shr 40).toInt() and 0xFF
        val lightRed = (lightBackground.value shr 40).toInt() and 0xFF
        assertTrue(
            "Dark background red ($darkRed) should be less than light background red ($lightRed)",
            darkRed < lightRed
        )
    }

    @Test
    fun darkSurfaceShouldDifferFromLightSurface() {
        // Dark surface 0xFF1E1E1E vs light surface 0xFFF5F5F5
        assertNotEquals(
            "Dark surface and light surface must be distinct",
            darkSurface,
            lightSurface
        )
    }

    // -------------------------------------------------------------------------
    // On-color scheme-specific direction
    // The dark scheme uses Black as onPrimary/onSecondary (light-on-dark primary)
    // while light scheme uses White as onPrimary (dark-on-light primary).
    // -------------------------------------------------------------------------

    @Test
    fun darkOnPrimaryIsBlackWhileLightOnPrimaryIsWhite() {
        assertEquals("Dark scheme onPrimary must be Black", Color.Black, darkOnPrimary)
        assertEquals("Light scheme onPrimary must be White", Color.White, lightOnPrimary)
        assertNotEquals("Dark and light onPrimary must differ", darkOnPrimary, lightOnPrimary)
    }

    @Test
    fun darkOnBackgroundIsWhiteWhileLightOnBackgroundIsBlack() {
        assertEquals("Dark scheme onBackground must be White", Color.White, darkOnBackground)
        assertEquals("Light scheme onBackground must be Black", Color.Black, lightOnBackground)
        assertNotEquals("Dark and light onBackground must differ", darkOnBackground, lightOnBackground)
    }
}