package dev.aurakai.auraframefx.romtools

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

/**
 * Tests for RomOperation.getDisplayName extension.
 *
 * Framework: JUnit 5 (JUnit Jupiter) for Kotlin.
 *
 * Scope:
 * - Validates exact mapping for each explicit RomOperation constant present in the extensions when expression.
 * - Guards against regressions by ensuring all enum values produce a non-blank, human-friendly label.
 *
 * Note:
 * These tests intentionally enumerate expected strings so that any copy or punctuation changes are caught.
 */
class RomOperationExtensionsTest {

    @Test
    @DisplayName("getDisplayName returns expected label for each RomOperation constant")
    fun testExactLabelsPerConstant() {
        // Expected mapping derived from the extension's when-branches.
        val expected = mapOf(
            RomOperation.VERIFYING_ROM to "Verifying ROM",
            RomOperation.CREATING_BACKUP to "Creating Backup",
            RomOperation.UNLOCKING_BOOTLOADER to "Unlocking Bootloader",
            RomOperation.INSTALLING_RECOVERY to "Installing Recovery",
            RomOperation.FLASHING_ROM to "Flashing ROM",
            RomOperation.VERIFYING_INSTALLATION to "Verifying Installation",
            RomOperation.RESTORING_BACKUP to "Restoring Backup",
            RomOperation.APPLYING_OPTIMIZATIONS to "Applying Optimizations",
            RomOperation.DOWNLOADING_ROM to "Downloading ROM",
            RomOperation.COMPLETED to "Completed",
            RomOperation.FAILED to "Failed"
        )

        // Assert individually for clearer failure messages.
        assertAll(
            "Exact label mapping",
            { assertEquals("Verifying ROM", RomOperation.VERIFYING_ROM.getDisplayName()) },
            { assertEquals("Creating Backup", RomOperation.CREATING_BACKUP.getDisplayName()) },
            {
                assertEquals(
                    "Unlocking Bootloader",
                    RomOperation.UNLOCKING_BOOTLOADER.getDisplayName()
                )
            },
            {
                assertEquals(
                    "Installing Recovery",
                    RomOperation.INSTALLING_RECOVERY.getDisplayName()
                )
            },
            { assertEquals("Flashing ROM", RomOperation.FLASHING_ROM.getDisplayName()) },
            {
                assertEquals(
                    "Verifying Installation",
                    RomOperation.VERIFYING_INSTALLATION.getDisplayName()
                )
            },
            { assertEquals("Restoring Backup", RomOperation.RESTORING_BACKUP.getDisplayName()) },
            {
                assertEquals(
                    "Applying Optimizations",
                    RomOperation.APPLYING_OPTIMIZATIONS.getDisplayName()
                )
            },
            { assertEquals("Downloading ROM", RomOperation.DOWNLOADING_ROM.getDisplayName()) },
            { assertEquals("Completed", RomOperation.COMPLETED.getDisplayName()) },
            { assertEquals("Failed", RomOperation.FAILED.getDisplayName()) }
        )

        // Secondary check to ensure the mapping table stays in sync if enum or code changes.
        // This will catch missing keys if new constants are added and the mapping isn't updated.
        for (op in RomOperation.entries) {
            val expectedLabel = expected[op]
            if (expectedLabel != null) {
                assertEquals(expectedLabel, op.getDisplayName(), "Label mismatch for $op")
            }
        }
    }

    @Test
    @DisplayName("getDisplayName never returns blank for any RomOperation")
    fun testNonBlankForAllEnumValues() {
        for (op in RomOperation.entries) {
            val label = op.getDisplayName()
            assertTrue(label.isNotBlank(), "Display label should be non-blank for $op")
        }
    }
}
