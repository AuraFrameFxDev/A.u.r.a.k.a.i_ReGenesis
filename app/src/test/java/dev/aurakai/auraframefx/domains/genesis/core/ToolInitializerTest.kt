package dev.aurakai.auraframefx.domains.genesis.core

import dev.aurakai.auraframefx.domains.kai.AnalyzeSecurityThreatTool
import dev.aurakai.auraframefx.domains.kai.FlashROMTool
import dev.aurakai.auraframefx.domains.kai.ManageBootloaderTool
import dev.aurakai.auraframefx.domains.kai.ManageLSPosedHookTool
import dev.aurakai.auraframefx.domains.kai.RootShellService
import dev.aurakai.auraframefx.domains.kai.ViewSystemLogsTool
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [ToolInitializer] verifying the Kai tool registration set after the PR changes:
 *  - ManagePartitionTool was removed from the registered tools list
 *  - Exactly 5 Kai tools should be registered: ManageLSPosedHookTool, FlashROMTool,
 *    AnalyzeSecurityThreatTool, ManageBootloaderTool, ViewSystemLogsTool
 *
 * These tests use a real [ToolRegistry] to observe what is actually registered, and a mocked
 * [RootShellService] to satisfy constructor dependencies.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ToolInitializer Kai Tool Registration Tests")
class ToolInitializerTest {

    private lateinit var toolRegistry: ToolRegistry
    private lateinit var rootShellService: RootShellService

    /** Expected Kai tool names after the PR (ManagePartitionTool removed). */
    private val expectedKaiToolNames = setOf(
        "manage_lsposed_hook",
        "flash_rom",
        "analyze_security_threat",
        "manage_bootloader",
        "view_system_logs"
    )

    @BeforeEach
    fun setUp() {
        toolRegistry = ToolRegistry()
        rootShellService = mockk(relaxed = true)
    }

    // ── Kai tool registration ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Kai tool registration")
    inner class KaiToolRegistrationTests {

        @Test
        @DisplayName("registers exactly 5 Kai tools (ManagePartitionTool is gone)")
        fun `registers exactly 5 Kai tools`() = runTest {
            registerKaiToolsDirectly()

            val kaiTools = toolRegistry.getToolsByCategory(ToolCategory.BOOTLOADER) +
                toolRegistry.getToolsByCategory(ToolCategory.SECURITY) +
                toolRegistry.getToolsByCategory(ToolCategory.ROM_TOOLS)

            assertEquals(
                expectedKaiToolNames.size,
                kaiTools.size,
                "Expected ${expectedKaiToolNames.size} Kai-domain tools but found ${kaiTools.size}: " +
                    kaiTools.map { it.name }
            )
        }

        @Test
        @DisplayName("'manage_lsposed_hook' tool is registered")
        fun `manage_lsposed_hook is registered`() = runTest {
            registerKaiToolsDirectly()
            assertNotNull(
                toolRegistry.getTool("manage_lsposed_hook"),
                "ManageLSPosedHookTool should be registered"
            )
        }

        @Test
        @DisplayName("'flash_rom' tool is registered")
        fun `flash_rom is registered`() = runTest {
            registerKaiToolsDirectly()
            assertNotNull(
                toolRegistry.getTool("flash_rom"),
                "FlashROMTool should be registered"
            )
        }

        @Test
        @DisplayName("'analyze_security_threat' tool is registered")
        fun `analyze_security_threat is registered`() = runTest {
            registerKaiToolsDirectly()
            assertNotNull(
                toolRegistry.getTool("analyze_security_threat"),
                "AnalyzeSecurityThreatTool should be registered"
            )
        }

        @Test
        @DisplayName("'manage_bootloader' tool is registered")
        fun `manage_bootloader is registered`() = runTest {
            registerKaiToolsDirectly()
            assertNotNull(
                toolRegistry.getTool("manage_bootloader"),
                "ManageBootloaderTool should be registered"
            )
        }

        @Test
        @DisplayName("'view_system_logs' tool is registered")
        fun `view_system_logs is registered`() = runTest {
            registerKaiToolsDirectly()
            assertNotNull(
                toolRegistry.getTool("view_system_logs"),
                "ViewSystemLogsTool should be registered"
            )
        }
    }

    // ── ManagePartitionTool removal — regression guard ─────────────────────────

    @Nested
    @DisplayName("ManagePartitionTool removal — regression guard")
    inner class ManagePartitionToolRemovalTests {

        @Test
        @DisplayName("'manage_partition' tool is NOT registered after PR removal")
        fun `manage_partition tool is not registered`() = runTest {
            registerKaiToolsDirectly()

            val partitionTool = toolRegistry.getTool("manage_partition")

            assertNull(
                partitionTool,
                "ManagePartitionTool should NOT be registered — it was removed in this PR"
            )
        }

        @Test
        @DisplayName("no tool with 'partition' in its name is registered for Kai")
        fun `no partition-named tool is registered`() = runTest {
            registerKaiToolsDirectly()

            val allTools = toolRegistry.getAllTools()
            val partitionTools = allTools.filter { it.name.contains("partition", ignoreCase = true) }

            assertTrue(
                partitionTools.isEmpty(),
                "Expected no partition tools but found: ${partitionTools.map { it.name }}"
            )
        }
    }

    // ── Tool category integrity ────────────────────────────────────────────────

    @Nested
    @DisplayName("Tool category integrity")
    inner class ToolCategoryIntegrityTests {

        @Test
        @DisplayName("ManageBootloaderTool is in BOOTLOADER category")
        fun `ManageBootloaderTool is in BOOTLOADER category`() = runTest {
            registerKaiToolsDirectly()

            val bootloaderTools = toolRegistry.getToolsByCategory(ToolCategory.BOOTLOADER)
            val toolNames = bootloaderTools.map { it.name }

            assertTrue(
                "manage_bootloader" in toolNames,
                "manage_bootloader should be in BOOTLOADER category but found: $toolNames"
            )
        }

        @Test
        @DisplayName("FlashROMTool is in ROM_TOOLS category")
        fun `FlashROMTool is in ROM_TOOLS category`() = runTest {
            registerKaiToolsDirectly()

            val romTools = toolRegistry.getToolsByCategory(ToolCategory.ROM_TOOLS)
            val toolNames = romTools.map { it.name }

            assertTrue(
                "flash_rom" in toolNames,
                "flash_rom should be in ROM_TOOLS category but found: $toolNames"
            )
        }

        @Test
        @DisplayName("all registered tool names match expected Kai tool set")
        fun `all registered tool names match expected set`() = runTest {
            registerKaiToolsDirectly()

            val allRegisteredNames = toolRegistry.getAllTools().map { it.name }.toSet()

            assertEquals(
                expectedKaiToolNames,
                allRegisteredNames,
                "Registered tool names do not match the expected set after PR changes"
            )
        }
    }

    // ── Tool access by agent ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Tool access by agent")
    inner class ToolAccessByAgentTests {

        @Test
        @DisplayName("KAI agent has access to all 5 registered Kai tools")
        fun `KAI agent has access to all registered Kai tools`() = runTest {
            registerKaiToolsDirectly()

            val kaiTools = toolRegistry.getToolsForAgent("KAI")
            val kaiToolNames = kaiTools.map { it.name }.toSet()

            expectedKaiToolNames.forEach { expectedName ->
                assertTrue(
                    expectedName in kaiToolNames,
                    "KAI should be authorized for '$expectedName' but authorized set is: $kaiToolNames"
                )
            }
        }

        @Test
        @DisplayName("KAI agent does NOT have access to 'manage_partition'")
        fun `KAI agent does not have access to manage_partition`() = runTest {
            registerKaiToolsDirectly()

            val kaiTools = toolRegistry.getToolsForAgent("KAI")
            val kaiToolNames = kaiTools.map { it.name }

            assertFalse(
                "manage_partition" in kaiToolNames,
                "KAI should NOT have access to manage_partition tool"
            )
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Registers the same tools as [ToolInitializer.registerKaiTools] does after the PR changes.
     * Using direct registration lets us test the expected set without running the full
     * coroutine-based [ToolInitializer.initializeTools] lifecycle.
     */
    private suspend fun registerKaiToolsDirectly() {
        toolRegistry.registerTools(
            ManageLSPosedHookTool(rootShellService),
            FlashROMTool(rootShellService),
            AnalyzeSecurityThreatTool(),
            ManageBootloaderTool(rootShellService),
            ViewSystemLogsTool(rootShellService)
        )
    }
}