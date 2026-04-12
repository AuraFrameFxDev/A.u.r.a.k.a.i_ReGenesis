package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.aurakai.auraframefx.core.models.HomeScreenTransitionType
import org.junit.Rule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * UI tests for [EcosystemMenuScreen] covering the changes in this PR:
 *  - The [onNavigateBack] parameter was removed; the screen no longer hosts a top bar.
 *  - The screen is composable with just [transitionType] and [showHologram] params.
 *  - "Ecosystem Command Center" heading is shown inside a [HologramTransition].
 */
@DisplayName("EcosystemMenuScreen UI Tests")
class EcosystemMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Content rendering ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Content rendering")
    inner class ContentRenderingTests {

        @Test
        @DisplayName("shows 'Ecosystem Command Center' heading with default parameters")
        fun showsHeadingWithDefaultParameters() {
            composeTestRule.setContent {
                EcosystemMenuScreen()
            }

            composeTestRule
                .onNodeWithText("Ecosystem Command Center")
                .assertExists("Heading should be visible with default params (showHologram=true)")
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("shows heading when showHologram is true")
        fun showsHeadingWhenHologramVisible() {
            composeTestRule.setContent {
                EcosystemMenuScreen(showHologram = true)
            }

            composeTestRule
                .onNodeWithText("Ecosystem Command Center")
                .assertExists()
                .assertIsDisplayed()
        }

        @Test
        @DisplayName("'Ecosystem Command Center' heading is absent when showHologram is false")
        fun headingAbsentWhenHologramHidden() {
            // HologramTransition hides content when visible=false
            composeTestRule.setContent {
                EcosystemMenuScreen(showHologram = false)
            }

            composeTestRule
                .onNodeWithText("Ecosystem Command Center")
                .assertDoesNotExist()
        }
    }

    // ── No navigation top bar ─────────────────────────────────────────────────

    @Nested
    @DisplayName("No navigation top bar (removed in PR)")
    inner class NoTopBarTests {

        @Test
        @DisplayName("'Ecosystem Hub' top-bar title is NOT present (Scaffold removed)")
        fun ecosystemHubTitleNotPresent() {
            composeTestRule.setContent {
                EcosystemMenuScreen()
            }

            // The old implementation had a TopAppBar with this title; it was removed in the PR.
            composeTestRule
                .onNodeWithText("Ecosystem Hub")
                .assertDoesNotExist()
        }
    }

    // ── Transition type parameter ─────────────────────────────────────────────

    @Nested
    @DisplayName("Transition type parameter")
    inner class TransitionTypeTests {

        @Test
        @DisplayName("screen renders with DIGITAL_DECONSTRUCT transition type")
        fun rendersWithDigitalDeconstructType() {
            composeTestRule.setContent {
                EcosystemMenuScreen(
                    transitionType = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
                    showHologram = true
                )
            }

            composeTestRule
                .onNodeWithText("Ecosystem Command Center")
                .assertExists()
        }

        @Test
        @DisplayName("screen renders without crashing for all transition types")
        fun rendersForAllTransitionTypes() {
            HomeScreenTransitionType.entries.forEach { type ->
                composeTestRule.setContent {
                    EcosystemMenuScreen(
                        transitionType = type,
                        showHologram = true
                    )
                }

                // At minimum the screen must compose without throwing
                composeTestRule.waitForIdle()
            }
        }
    }
}