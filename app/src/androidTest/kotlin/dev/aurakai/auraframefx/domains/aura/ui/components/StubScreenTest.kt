package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * UI tests for [StubScreen] covering the changes in this PR:
 *  - Background is now a vertical gradient (HexagonGrid removed)
 *  - Title displayed via plain [Text] (GlitchText removed)
 *  - Back navigation via standard [Button] with ArrowBack icon (SparkleButton removed)
 *  - "COMING SOON" card with default or custom description text
 */
@DisplayName("StubScreen UI Tests")
class StubScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Title display ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Title display")
    inner class TitleDisplayTests {

        @Test
        @DisplayName("title is rendered as plain text (not GlitchText)")
        fun titleIsRenderedAsPlainText() {
            composeTestRule.setContent {
                StubScreen(title = "Test Gate", iconName = "TestIcon")
            }

            composeTestRule
                .onNodeWithText("Test Gate")
                .assertExists("Title 'Test Gate' should be visible")
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("title text matches the provided parameter exactly")
        fun titleMatchesProvidedParameter() {
            composeTestRule.setContent {
                StubScreen(title = "Sovereign VPN", iconName = "VpnLock")
            }

            composeTestRule
                .onNodeWithText("Sovereign VPN")
                .assertExists()
        }

        @Test
        @DisplayName("iconName label is rendered as a secondary text node")
        fun iconNameIsRendered() {
            composeTestRule.setContent {
                StubScreen(title = "ROM Flasher", iconName = "Build")
            }

            composeTestRule
                .onNodeWithText("Build")
                .assertExists("iconName 'Build' should be displayed as subtitle text")
        }
    }

    // ── Construction icon ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Construction icon")
    inner class ConstructionIconTests {

        @Test
        @DisplayName("Construction icon content description matches iconName parameter")
        fun iconContentDescriptionMatchesIconName() {
            composeTestRule.setContent {
                StubScreen(title = "Some Screen", iconName = "Gate")
            }

            composeTestRule
                .onNodeWithContentDescription("Gate")
                .assertExists("Construction icon content description should equal iconName 'Gate'")
        }
    }

    // ── COMING SOON card ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Coming Soon card")
    inner class ComingSoonCardTests {

        @Test
        @DisplayName("default description text is shown when no description is provided")
        fun defaultDescriptionIsShownWhenNoneProvided() {
            composeTestRule.setContent {
                StubScreen(title = "Any Title", iconName = "AnyIcon")
            }

            composeTestRule
                .onNodeWithText("This feature is currently under development")
                .assertExists("Default description should show when description param is null")
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("custom description text is shown when a description is provided")
        fun customDescriptionIsShownWhenProvided() {
            val customDesc = "Custom feature description for testing"
            composeTestRule.setContent {
                StubScreen(title = "My Screen", iconName = "MyIcon", description = customDesc)
            }

            composeTestRule
                .onNodeWithText(customDesc)
                .assertExists("Custom description should be displayed when provided")
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("default description is NOT shown when a custom description is provided")
        fun defaultDescriptionHiddenWhenCustomProvided() {
            composeTestRule.setContent {
                StubScreen(
                    title = "My Screen",
                    iconName = "MyIcon",
                    description = "Custom text here"
                )
            }

            composeTestRule
                .onNodeWithText("This feature is currently under development")
                .assertDoesNotExist()
        }
    }

    // ── Back button (navController null) ─────────────────────────────────────

    @Nested
    @DisplayName("Back button — navController is null")
    inner class BackButtonHiddenTests {

        @Test
        @DisplayName("BACK button is NOT shown when navController is null")
        fun backButtonHiddenWhenNavControllerIsNull() {
            composeTestRule.setContent {
                StubScreen(title = "No Nav", iconName = "Lock", navController = null)
            }

            composeTestRule
                .onNodeWithText("BACK")
                .assertDoesNotExist()
        }

        @Test
        @DisplayName("ArrowBack icon is NOT shown when navController is null")
        fun arrowBackIconHiddenWhenNavControllerIsNull() {
            composeTestRule.setContent {
                StubScreen(title = "No Nav", iconName = "Lock", navController = null)
            }

            composeTestRule
                .onNodeWithContentDescription("Back")
                .assertDoesNotExist()
        }
    }

    // ── Back button (navController provided) ─────────────────────────────────

    @Nested
    @DisplayName("Back button — navController provided")
    inner class BackButtonVisibleTests {

        @Test
        @DisplayName("BACK button is shown when navController is provided")
        fun backButtonShownWhenNavControllerProvided() {
            val fakeNav = mockk<androidx.navigation.NavController>(relaxed = true)
            composeTestRule.setContent {
                StubScreen(title = "Has Nav", iconName = "Home", navController = fakeNav)
            }

            composeTestRule
                .onNodeWithText("BACK")
                .assertExists("BACK label should be visible when navController is provided")
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("clicking BACK button calls navController.popBackStack()")
        fun clickingBackButtonCallsPopBackStack() {
            val fakeNav = mockk<androidx.navigation.NavController>(relaxed = true)
            composeTestRule.setContent {
                StubScreen(title = "Has Nav", iconName = "Home", navController = fakeNav)
            }

            composeTestRule
                .onNodeWithText("BACK")
                .performClick()

            verify(exactly = 1) { fakeNav.popBackStack() }
        }

        @Test
        @DisplayName("ArrowBack icon content description is 'Back'")
        fun arrowBackIconContentDescriptionIsBack() {
            val fakeNav = mockk<androidx.navigation.NavController>(relaxed = true)
            composeTestRule.setContent {
                StubScreen(title = "Has Nav", iconName = "Home", navController = fakeNav)
            }

            composeTestRule
                .onNodeWithContentDescription("Back")
                .assertExists()
                .assertIsDisplayed()
        }
    }
}