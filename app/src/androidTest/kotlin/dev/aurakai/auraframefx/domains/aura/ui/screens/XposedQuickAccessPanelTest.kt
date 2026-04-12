package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * UI tests for [XposedQuickAccessPanel] covering the changes in this PR:
 *  - The panel was simplified to a placeholder screen with no parameters.
 *  - The old [onNavigateBack] parameter, Scaffold, TopAppBar, HexagonGrid and
 *    SparkleButton were all removed.
 *  - The screen now shows a single centered "Xposed Quick Access - Coming Soon" message.
 */
@DisplayName("XposedQuickAccessPanel UI Tests")
class XposedQuickAccessPanelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Placeholder content ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Placeholder content")
    inner class PlaceholderContentTests {

        @Test
        @DisplayName("displays the 'Xposed Quick Access - Coming Soon' placeholder text")
        fun displaysPlaceholderText() {
            composeTestRule.setContent {
                XposedQuickAccessPanel()
            }

            composeTestRule
                .onNodeWithText("Xposed Quick Access - Coming Soon")
                .assertExists("Placeholder text should be visible")
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("composable renders without throwing any exception")
        fun composableRendersWithoutException() {
            composeTestRule.setContent {
                XposedQuickAccessPanel()
            }

            composeTestRule.waitForIdle()
            // If we reach this point the composable composed successfully
        }
    }

    // ── Removed content — regression guards ───────────────────────────────────

    @Nested
    @DisplayName("Removed content — regression guards")
    inner class RemovedContentTests {

        @Test
        @DisplayName("'LSPosed Quick Toggles' top-bar title is NOT shown (TopAppBar removed)")
        fun lsposedQuickTogglesTitleNotPresent() {
            composeTestRule.setContent {
                XposedQuickAccessPanel()
            }

            // The old TopAppBar had this title; it was removed in the PR.
            composeTestRule
                .onNodeWithText("LSPosed Quick Toggles")
                .assertDoesNotExist()
        }

        @Test
        @DisplayName("'NEURAL INJECTION MODULES' header text is NOT shown (old content removed)")
        fun neuralInjectionModulesNotPresent() {
            composeTestRule.setContent {
                XposedQuickAccessPanel()
            }

            composeTestRule
                .onNodeWithText("NEURAL INJECTION MODULES")
                .assertDoesNotExist()
        }

        @Test
        @DisplayName("'No active hooks detected.' text is NOT shown (old content removed)")
        fun noActiveHooksTextNotPresent() {
            composeTestRule.setContent {
                XposedQuickAccessPanel()
            }

            composeTestRule
                .onNodeWithText("No active hooks detected.")
                .assertDoesNotExist()
        }

        @Test
        @DisplayName("'Sync LSPosed' button text is NOT shown (SparkleButton removed)")
        fun syncLsposedButtonNotPresent() {
            composeTestRule.setContent {
                XposedQuickAccessPanel()
            }

            composeTestRule
                .onNodeWithText("Sync LSPosed")
                .assertDoesNotExist()
        }
    }
}