// Tests use JUnit4 + AndroidX Compose UI Test (createComposeRule). Adjust if your project differs.
package dev.aurakai.auraframefx.romtools.ui

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.aurakai.auraframefx.romtools.BackupManager
import dev.aurakai.auraframefx.romtools.FlashManager
import dev.aurakai.auraframefx.romtools.OperationProgress
import dev.aurakai.auraframefx.romtools.RecoveryManager
import dev.aurakai.auraframefx.romtools.RomToolsState
import dev.aurakai.auraframefx.romtools.RomVerificationManager
import dev.aurakai.auraframefx.romtools.SystemModificationManager
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

// NOTE: Test stack used: JUnit4 + AndroidX Compose UI Test (createComposeRule).

/**
 * Minimal fake of the manager used by RomToolsScreen. It exposes the two StateFlows that the
 * composable collects: romToolsState and operationProgress.
 * This avoids setting up Hilt in instrumentation tests.
 */
private class FakeRomToolsManager(
    initialState: RomToolsState,
    initialProgress: OperationProgress?,
    bootloaderManager: BootloaderManager,
    recoveryManager: RecoveryManager,
    systemModificationManager: SystemModificationManager,
    flashManager: FlashManager,
    verificationManager: RomVerificationManager,
    backupManager: BackupManager,
    retentionManager: AurakaiRetentionManager
) : dev.aurakai.auraframefx.romtools.RomToolsManager(
    bootloaderManager,
    recoveryManager,
    systemModificationManager,
    flashManager,
    verificationManager,
    backupManager,
    retentionManager
) {
    override val _romToolsState = MutableStateFlow(initialState)
    override val _operationProgress = MutableStateFlow(initialProgress)

    override val romToolsState: StateFlow<RomToolsState> =
        _romToolsState
    override val operationProgress: StateFlow<OperationProgress?> =
        _operationProgress

    fun updateState(state: RomToolsState) {
        _romToolsState.value = state
    }

    fun updateProgress(progress: OperationProgress?) {
        _operationProgress.value = progress
    }
}

@RunWith(AndroidJUnit4::class)
class RomToolsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun defaultCaps(
        root: Boolean = false,
        bootloader: Boolean = false,
        recovery: Boolean = false,
        system: Boolean = false
    ) = dev.aurakai.auraframefx.romtools.RomCapabilities(
        hasRootAccess = root,
        hasBootloaderAccess = bootloader,
        hasRecoveryAccess = recovery,
        hasSystemWriteAccess = system,
        deviceModel = "Pixel X",
        androidVersion = "14",
        securityPatchLevel = "2025-08-05"
    )

    private fun defaultState(
        isInitialized: Boolean,
        capabilities: dev.aurakai.auraframefx.romtools.RomCapabilities? = null,
        availableRoms: List<dev.aurakai.auraframefx.romtools.AvailableRom> = emptyList(),
        backups: List<dev.aurakai.auraframefx.romtools.BackupInfo> = emptyList()
    ) = RomToolsState(
        isInitialized = isInitialized,
        capabilities = capabilities,
        availableRoms = availableRoms,
        backups = backups
    )

    @Test
    fun showsLoadingState_whenNotInitialized() {
        val fake = FakeRomToolsManager(
            initialState = defaultState(isInitialized = false),
            initialProgress = null
        )
        composeRule.setContent {
            RomToolsScreen(romToolsManager = fake)
        }

        // Verifies the loading text from the diff context.
        composeRule.onNodeWithText("Initializing ROM Tools...").assertIsDisplayed()
    }

    @Test
    fun showsCapabilitiesAndSections_whenInitialized() {
        val fake = FakeRomToolsManager(
            initialState = defaultState(isInitialized = true, capabilities = caps),
            initialProgress = null
        )
        composeRule.setContent {
            RomToolsScreen(romToolsManager = fake)
        }

        // Header and ROM Operations section visible
        composeRule.onNodeWithText("ROM Tools").assertIsDisplayed()
        composeRule.onNodeWithText("ROM Operations").assertIsDisplayed()

        // Capability labels should be visible
        composeRule.onNodeWithText("Root Access").assertIsDisplayed()
        composeRule.onNodeWithText("Bootloader Access").assertIsDisplayed()
        composeRule.onNodeWithText("Recovery Access").assertIsDisplayed()
        composeRule.onNodeWithText("System Write Access").assertIsDisplayed()

        // Info rows labels with trailing colon
        composeRule.onNodeWithText("Device:").assertIsDisplayed()
        composeRule.onNodeWithText("Android:").assertIsDisplayed()
        composeRule.onNodeWithText("Security Patch:").assertIsDisplayed()

        // Values shown
        composeRule.onNodeWithText("Pixel X").assertIsDisplayed()
        composeRule.onNodeWithText("14").assertIsDisplayed()
        composeRule.onNodeWithText("2025-08-05").assertIsDisplayed()
    }

    @Test
    fun operationProgressCard_usesProgressFraction_fromPercent() {
        // Focused on the diff: LinearProgressIndicator now uses progress lambda { percent / 100f }
        val op = OperationProgress(
            operation = dev.aurakai.auraframefx.romtools.RomOperation.FLASH,
            progress = 42f
        )
        val fake = FakeRomToolsManager(
            initialState = defaultState(isInitialized = true, capabilities = defaultCaps()),
            initialProgress = op
        )

        composeRule.setContent {
            RomToolsScreen(romToolsManager = fake)
        }

        // The label should display the operation display name and "42%"
        composeRule.onNodeWithText("42%").assertIsDisplayed()
        composeRule.onNode(
            hasProgressBarRangeInfo(
                ProgressBarRangeInfo(
                    current = 0.42f,
                    range = 0f..1f,
                    steps = 0
                )
            )
        ).assertIsDisplayed()
    }

    @Test
    fun romToolCards_renderLockedAffordance_whenCapabilitiesInsufficient() {
        // Provide no capabilities; many actions require root/recovery/bootloader/system.
        val fake = FakeRomToolsManager(
            initialState = defaultState(isInitialized = true, capabilities = caps),
            initialProgress = null
        )

        composeRule.setContent {
            RomToolsScreen(romToolsManager = fake)
        }

        // There should be at least one card that shows the "Locked" icon (contentDescription = "Locked").
        // Count locked icons > 0
        composeRule.onAllNodes(hasAnyChild(hasAnyChild(hasAnyChild(hasAnyChild(hasAnyChild { false })))))
            .fetchSemanticsNodes() // no-op to force tree init
        composeRule.onAllNodes(hasAnyChild(hasAnyChild { false })) // fallback matcher if custom keys missing
        // Simpler: just look for contentDescription "Locked"
        composeRule.onAllNodes(androidx.compose.ui.test.hasContentDescription("Locked"))
            .assertCountGreaterThan(0)
    }
}
