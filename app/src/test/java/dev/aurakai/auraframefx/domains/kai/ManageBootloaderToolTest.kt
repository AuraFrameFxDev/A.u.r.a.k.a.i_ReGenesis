package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.genesis.core.ToolCategory
import dev.aurakai.auraframefx.domains.genesis.core.ToolResult
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [ManageBootloaderTool] covering the changes in this PR:
 *  - "reboot" action removed from the supported enum
 *  - "check_status" and "get_info" now return static stub strings
 *  - "unlock" and "lock" now return [ToolResult.Pending] instead of executing shell commands
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ManageBootloaderTool Tests")
class ManageBootloaderToolTest {

    private lateinit var rootShellService: RootShellService
    private lateinit var tool: ManageBootloaderTool

    @BeforeEach
    fun setUp() {
        rootShellService = mockk(relaxed = true)
        tool = ManageBootloaderTool(rootShellService)
    }

    // ── Tool Metadata ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Tool Metadata")
    inner class ToolMetadataTests {

        @Test
        @DisplayName("tool name is 'manage_bootloader'")
        fun `tool name is manage_bootloader`() {
            assertEquals("manage_bootloader", tool.name)
        }

        @Test
        @DisplayName("tool category is BOOTLOADER")
        fun `tool category is BOOTLOADER`() {
            assertEquals(ToolCategory.BOOTLOADER, tool.category)
        }

        @Test
        @DisplayName("authorized agents include KAI and kai")
        fun `authorized agents include KAI and kai`() {
            assertTrue(tool.authorizedAgents.contains("KAI"))
            assertTrue(tool.authorizedAgents.contains("kai"))
        }

        @Test
        @DisplayName("input schema requires action parameter")
        fun `input schema requires action parameter`() {
            assertTrue(tool.inputSchema.required.contains("action"))
        }

        @Test
        @DisplayName("action enum does NOT contain 'reboot' — regression guard")
        fun `action enum does not contain reboot`() {
            val actionProp = tool.inputSchema.properties["action"]
            assertNotNull(actionProp, "action property must exist in input schema")
            val allowedValues = actionProp!!.enum ?: emptyList()
            assertTrue(
                "reboot" !in allowedValues,
                "Enum should not contain 'reboot' after PR removal. Found: $allowedValues"
            )
        }

        @Test
        @DisplayName("action enum contains the four valid actions")
        fun `action enum contains check_status unlock lock get_info`() {
            val actionProp = tool.inputSchema.properties["action"]
            assertNotNull(actionProp)
            val allowedValues = actionProp!!.enum ?: emptyList()
            val expected = listOf("check_status", "unlock", "lock", "get_info")
            expected.forEach { action ->
                assertTrue(
                    action in allowedValues,
                    "Expected '$action' in enum but found: $allowedValues"
                )
            }
        }
    }

    // ── check_status ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("check_status action")
    inner class CheckStatusTests {

        @Test
        @DisplayName("returns Success with static UNLOCKED message")
        fun `check_status returns Success with static UNLOCKED message`() = runTest {
            val params = buildParams("action" to "check_status")

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Success, "Expected Success but got $result")
            val success = result as ToolResult.Success
            assertEquals("Bootloader Status: UNLOCKED", success.output)
        }

        @Test
        @DisplayName("metadata contains action key set to check_status")
        fun `check_status metadata contains action key`() = runTest {
            val params = buildParams("action" to "check_status")

            val result = tool.execute(params, "KAI") as ToolResult.Success
            assertEquals("check_status", result.metadata["action"])
        }

        @Test
        @DisplayName("does NOT call rootShellService — static response only")
        fun `check_status does not invoke rootShellService`() = runTest {
            val params = buildParams("action" to "check_status")

            tool.execute(params, "kai")

            io.mockk.verify(exactly = 0) { rootShellService.executeCommand(any()) }
        }
    }

    // ── get_info ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("get_info action")
    inner class GetInfoTests {

        @Test
        @DisplayName("returns Success with static bootloader info string")
        fun `get_info returns Success with static bootloader info string`() = runTest {
            val params = buildParams("action" to "get_info")

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Success, "Expected Success but got $result")
            val success = result as ToolResult.Success
            assertEquals(
                "Bootloader Version: 1.0, Status: UNLOCKED, Verified Boot: Disabled",
                success.output
            )
        }

        @Test
        @DisplayName("metadata contains action key set to get_info")
        fun `get_info metadata contains action key`() = runTest {
            val params = buildParams("action" to "get_info")

            val result = tool.execute(params, "kai") as ToolResult.Success
            assertEquals("get_info", result.metadata["action"])
        }

        @Test
        @DisplayName("does NOT call rootShellService — static response only")
        fun `get_info does not invoke rootShellService`() = runTest {
            val params = buildParams("action" to "get_info")

            tool.execute(params, "kai")

            io.mockk.verify(exactly = 0) { rootShellService.executeCommand(any()) }
        }
    }

    // ── unlock ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("unlock action")
    inner class UnlockTests {

        @Test
        @DisplayName("returns Pending result")
        fun `unlock returns Pending result`() = runTest {
            val params = buildParams("action" to "unlock")

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Pending, "Expected Pending but got $result")
        }

        @Test
        @DisplayName("Pending taskId starts with 'bootloader_unlock_'")
        fun `unlock Pending taskId starts with bootloader_unlock_`() = runTest {
            val params = buildParams("action" to "unlock")

            val result = tool.execute(params, "kai") as ToolResult.Pending

            assertTrue(
                result.taskId.startsWith("bootloader_unlock_"),
                "taskId should start with 'bootloader_unlock_' but was: ${result.taskId}"
            )
        }

        @Test
        @DisplayName("Pending estimatedDuration is 60000ms (1 minute)")
        fun `unlock Pending estimatedDuration is 60000`() = runTest {
            val params = buildParams("action" to "unlock")

            val result = tool.execute(params, "kai") as ToolResult.Pending

            assertEquals(60000L, result.estimatedDuration)
        }

        @Test
        @DisplayName("does NOT call rootShellService for unlock")
        fun `unlock does not invoke rootShellService`() = runTest {
            val params = buildParams("action" to "unlock")

            tool.execute(params, "kai")

            io.mockk.verify(exactly = 0) { rootShellService.executeCommand(any()) }
        }
    }

    // ── lock ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("lock action")
    inner class LockTests {

        @Test
        @DisplayName("returns Pending result")
        fun `lock returns Pending result`() = runTest {
            val params = buildParams("action" to "lock")

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Pending, "Expected Pending but got $result")
        }

        @Test
        @DisplayName("Pending taskId starts with 'bootloader_lock_'")
        fun `lock Pending taskId starts with bootloader_lock_`() = runTest {
            val params = buildParams("action" to "lock")

            val result = tool.execute(params, "kai") as ToolResult.Pending

            assertTrue(
                result.taskId.startsWith("bootloader_lock_"),
                "taskId should start with 'bootloader_lock_' but was: ${result.taskId}"
            )
        }

        @Test
        @DisplayName("Pending estimatedDuration is 60000ms (1 minute)")
        fun `lock Pending estimatedDuration is 60000`() = runTest {
            val params = buildParams("action" to "lock")

            val result = tool.execute(params, "kai") as ToolResult.Pending

            assertEquals(60000L, result.estimatedDuration)
        }

        @Test
        @DisplayName("does NOT call rootShellService for lock")
        fun `lock does not invoke rootShellService`() = runTest {
            val params = buildParams("action" to "lock")

            tool.execute(params, "kai")

            io.mockk.verify(exactly = 0) { rootShellService.executeCommand(any()) }
        }
    }

    // ── Removed action: reboot ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Removed 'reboot' action — regression tests")
    inner class RemovedRebootActionTests {

        @Test
        @DisplayName("'reboot' action returns Failure with invalid action message")
        fun `reboot action returns Failure`() = runTest {
            val params = buildParams("action" to "reboot")

            val result = tool.execute(params, "kai")

            assertTrue(
                result is ToolResult.Failure,
                "Expected Failure for removed 'reboot' action but got $result"
            )
            val failure = result as ToolResult.Failure
            assertTrue(
                failure.error.contains("reboot"),
                "Failure error message should mention 'reboot' but was: ${failure.error}"
            )
        }

        @Test
        @DisplayName("'reboot' action does NOT reboot the device — no shell command issued")
        fun `reboot action does not issue shell command`() = runTest {
            val params = buildParams("action" to "reboot")

            tool.execute(params, "kai")

            io.mockk.verify(exactly = 0) { rootShellService.executeCommand(any()) }
        }
    }

    // ── Missing / invalid parameters ──────────────────────────────────────────

    @Nested
    @DisplayName("Missing and invalid parameters")
    inner class InvalidParameterTests {

        @Test
        @DisplayName("missing action returns Failure with 'Missing action' message")
        fun `missing action param returns Failure`() = runTest {
            val params = JsonObject(emptyMap())

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Failure, "Expected Failure but got $result")
            val failure = result as ToolResult.Failure
            assertTrue(
                failure.error.contains("Missing action", ignoreCase = true),
                "Error message should mention missing action but was: ${failure.error}"
            )
        }

        @Test
        @DisplayName("completely unknown action returns Failure")
        fun `unknown action returns Failure`() = runTest {
            val params = buildParams("action" to "wipe_data")

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Failure, "Expected Failure but got $result")
            val failure = result as ToolResult.Failure
            assertTrue(
                failure.error.contains("Invalid action"),
                "Error message should contain 'Invalid action' but was: ${failure.error}"
            )
        }

        @Test
        @DisplayName("empty action string returns Failure")
        fun `empty action string returns Failure`() = runTest {
            val params = buildParams("action" to "")

            val result = tool.execute(params, "kai")

            assertTrue(result is ToolResult.Failure, "Expected Failure for empty action but got $result")
        }
    }

    // ── Authorization ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Authorization")
    inner class AuthorizationTests {

        @Test
        @DisplayName("KAI agent is authorized")
        fun `KAI agent is authorized`() {
            assertTrue(tool.isAuthorized("KAI"))
        }

        @Test
        @DisplayName("kai agent is authorized")
        fun `kai agent is authorized`() {
            assertTrue(tool.isAuthorized("kai"))
        }

        @Test
        @DisplayName("AURA agent is NOT authorized")
        fun `AURA agent is not authorized`() {
            assertTrue(!tool.isAuthorized("AURA"))
        }

        @Test
        @DisplayName("unknown agent is NOT authorized")
        fun `unknown agent is not authorized`() {
            assertTrue(!tool.isAuthorized("unknown_agent"))
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun buildParams(vararg pairs: Pair<String, String>): JsonObject {
        return JsonObject(pairs.associate { (k, v) -> k to JsonPrimitive(v) })
    }
}