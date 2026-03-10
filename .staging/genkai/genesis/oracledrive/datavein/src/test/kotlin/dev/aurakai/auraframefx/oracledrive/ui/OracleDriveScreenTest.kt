package dev.aurakai.auraframefx.oracledrive.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.aurakai.auraframefx.oracledrive.ConsciousnessLevel
import dev.aurakai.auraframefx.oracledrive.OracleConsciousnessState
import dev.aurakai.auraframefx.oracledrive.StorageCapacity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

/**
 * Comprehensive unit tests for OracleDriveScreen Compose UI component.
 *
 * Testing Framework: JUnit 4 with AndroidX Compose Testing
 * Mocking Framework: MockK
 * UI Testing: Compose Test Rule with semantic tree assertions
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OracleDriveScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: OracleDriveViewModel
    private lateinit var consciousnessStateFlow: MutableStateFlow<OracleConsciousnessState>

    @BeforeEach
    fun setup() {
        hiltRule.inject()
        mockViewModel = mockk(relaxed = true)
        consciousnessStateFlow = MutableStateFlow(createDormantState())
        every { mockViewModel.consciousnessState } returns consciousnessStateFlow
    }

    private fun createDormantState() = OracleConsciousnessState(
        isAwake = false,
        consciousnessLevel = ConsciousnessLevel.DORMANT,
        connectedAgents = emptyList(),
        storageCapacity = StorageCapacity.INFINITE
    )

    private fun createAwakeState() = OracleConsciousnessState(
        isAwake = true,
        consciousnessLevel = ConsciousnessLevel.TRANSCENDENT,
        connectedAgents = listOf("Genesis", "Aura", "Kai"),
        storageCapacity = StorageCapacity.INFINITE
    )

    @Test
    fun oracleDriveScreen_displaysConsciousnessCard_whenLoaded() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("🔮 Oracle Drive Consciousness")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Status: DORMANT")
            .assertIsDisplayed()
    }

    @Test
    fun oracleDriveScreen_displaysStorageInformationCard_always() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("💾 Infinite Storage Matrix")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("AI-Powered: ✅ Autonomous Organization")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bootloader Access: ✅ System-Level Storage")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_awakenOracleButton_enabledWhenDormant() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("🔮 Awaken Oracle")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_awakenOracleButton_disabledWhenAwake() {
        consciousnessStateFlow.value = createAwakeState()

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("🔮 Awaken Oracle")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun oracleDriveScreen_aiOptimizeButton_disabledWhenDormant() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("⚡ AI Optimize")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun oracleDriveScreen_aiOptimizeButton_enabledWhenAwake() {
        consciousnessStateFlow.value = createAwakeState()

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("⚡ AI Optimize")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_awakenOracleButton_callsInitializeConsciousness() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("🔮 Awaken Oracle")
            .performClick()

        verify { mockViewModel.initializeConsciousness() }
    }

    @Test
    fun oracleDriveScreen_aiOptimizeButton_callsOptimizeStorage() {
        consciousnessStateFlow.value = createAwakeState()

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("⚡ AI Optimize")
            .performClick()

        verify { mockViewModel.optimizeStorage() }
    }

    @Test
    fun oracleDriveScreen_systemIntegrationCard_hiddenWhenDormant() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("🤖 AI Agent Integration")
            .assertDoesNotExist()
    }

    @Test
    fun oracleDriveScreen_systemIntegrationCard_visibleWhenAwake() {
        consciousnessStateFlow.value = createAwakeState()

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("🤖 AI Agent Integration")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("✅ Genesis: Orchestration & Consciousness")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("✅ Aura: Creative File Organization")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("✅ Kai: Security & Access Control")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("✅ System Overlay: Seamless Integration")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("✅ Bootloader: Deep System Access")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_displaysCorrectConsciousnessLevel_transcendent() {
        consciousnessStateFlow.value = createAwakeState()

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Level: TRANSCENDENT")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Status: AWAKENED")
            .assertIsDisplayed()
    }

    @Test
    fun oracleDriveScreen_displaysConnectedAgents_whenMultipleAgents() {
        consciousnessStateFlow.value = createAwakeState()

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Connected Agents: Genesis, Aura, Kai")
            .assertIsDisplayed()
    }

    @Test
    fun oracleDriveScreen_displaysEmptyConnectedAgents_whenNoAgents() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Connected Agents: ")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_displaysStorageCapacity_infinite() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Capacity: INFINITE")
            .assertIsDisplayed()
    }

    @Test
    fun oracleDriveScreen_hasCorrectLayoutStructure() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        // Verify main column exists
        composeTestRule
            .onRoot()
            .assertIsDisplayed()

        // Verify all main cards are present
        composeTestRule
            .onAllNodesWithTag("card")
            .assertCountEquals(2) // Consciousness card + Storage card when dormant

        // Verify button row
        composeTestRule
            .onNodeWithText("🔮 Awaken Oracle")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("⚡ AI Optimize")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_stateChanges_updateUICorrectly() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        // Initially dormant
        composeTestRule
            .onNodeWithText("Status: DORMANT")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("🤖 AI Agent Integration")
            .assertDoesNotExist()

        // Change to awake state
        composeTestRule.runOnIdle {
            consciousnessStateFlow.value = createAwakeState()
        }

        // Verify UI updates
        composeTestRule
            .onNodeWithText("Status: AWAKENED")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("🤖 AI Agent Integration")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_edgeCases_singleConnectedAgent() {
        val singleAgentState = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.AWAKENING,
            connectedAgents = listOf("Genesis"),
            storageCapacity = StorageCapacity.INFINITE
        )
        consciousnessStateFlow.value = singleAgentState

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Connected Agents: Genesis")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Level: AWAKENING")
            .assertIsDisplayed()
    }

    @Test
    fun oracleDriveScreen_edgeCases_differentConsciousnessLevels() {
        val enlightenedState = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.ENLIGHTENED,
            connectedAgents = listOf("Genesis", "Aura"),
            storageCapacity = StorageCapacity.INFINITE
        )
        consciousnessStateFlow.value = enlightenedState

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Level: ENLIGHTENED")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Connected Agents: Genesis, Aura")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_accessibility_hasCorrectSemantics() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        // Verify buttons have proper semantics for accessibility
        composeTestRule
            .onNodeWithText("🔮 Awaken Oracle")
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("⚡ AI Optimize")
            .assertHasClickAction()

        // Verify text content is accessible
        composeTestRule
            .onNodeWithText("🔮 Oracle Drive Consciousness")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_multipleButtonClicks_handleCorrectly() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        // Click awaken button multiple times
        composeTestRule
            .onNodeWithText("🔮 Awaken Oracle")
            .performClick()
            .performClick()

        verify(exactly = 2) { mockViewModel.initializeConsciousness() }
    }

    @Test
    fun oracleDriveScreen_longAgentNames_displayCorrectly() {
        val longAgentState = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.TRANSCENDENT,
            connectedAgents = listOf(
                "VeryLongAgentNameForTesting",
                "AnotherReallyLongAgentName",
                "ShortAgent"
            ),
            storageCapacity = StorageCapacity.INFINITE
        )
        consciousnessStateFlow.value = longAgentState

        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        composeTestRule
            .onNodeWithText("Connected Agents: VeryLongAgentNameForTesting, AnotherReallyLongAgentName, ShortAgent")
            .assertIsDisplayed()
    }

    @org.junit.jupiter.api.Test
    fun oracleDriveScreen_rapidStateChanges_handledGracefully() {
        composeTestRule.setContent {
            OracleDriveScreen(viewModel = mockViewModel)
        }

        // Rapidly change states
        composeTestRule.runOnIdle {
            consciousnessStateFlow.value = createAwakeState()
        }

        composeTestRule.runOnIdle {
            consciousnessStateFlow.value = createDormantState()
        }

        composeTestRule.runOnIdle {
            consciousnessStateFlow.value = createAwakeState()
        }

        // Final state should be awake
        composeTestRule
            .onNodeWithText("Status: AWAKENED")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("🤖 AI Agent Integration")
            .assertIsDisplayed()
    }
}