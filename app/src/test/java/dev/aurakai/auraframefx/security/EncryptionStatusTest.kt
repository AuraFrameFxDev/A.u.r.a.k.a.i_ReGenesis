package dev.aurakai.auraframefx.security

import org.junit.Assert.*
import org.junit.jupiter.api.Test

/**
 * Comprehensive unit tests for EncryptionStatus enum.
 * Tests all enum values, ordering, and edge cases.
 */
class EncryptionStatusTest {

    @Test
    fun `test all enum values exist`() {
        val expectedValues = setOf(
            EncryptionStatus.ACTIVE,
            EncryptionStatus.NOT_INITIALIZED,
            EncryptionStatus.DISABLED,
            EncryptionStatus.ERROR
        )

        val actualValues = EncryptionStatus.values().toSet()
        assertEquals("All expected enum values should exist", expectedValues, actualValues)
    }

    @Test
    fun `test enum values count`() {
        assertEquals("Should have exactly 4 encryption status values", 4, EncryptionStatus.values().size)
    }

    @Test
    fun `test ACTIVE status properties`() {
        val status = EncryptionStatus.ACTIVE
        assertEquals("ACTIVE", status.name)
        assertEquals(0, status.ordinal)
    }

    @Test
    fun `test NOT_INITIALIZED status properties`() {
        val status = EncryptionStatus.NOT_INITIALIZED
        assertEquals("NOT_INITIALIZED", status.name)
        assertEquals(1, status.ordinal)
    }

    @Test
    fun `test DISABLED status properties`() {
        val status = EncryptionStatus.DISABLED
        assertEquals("DISABLED", status.name)
        assertEquals(2, status.ordinal)
    }

    @Test
    fun `test ERROR status properties`() {
        val status = EncryptionStatus.ERROR
        assertEquals("ERROR", status.name)
        assertEquals(3, status.ordinal)
    }

    @Test
    fun `test enum valueOf with valid names`() {
        assertEquals(EncryptionStatus.ACTIVE, EncryptionStatus.valueOf("ACTIVE"))
        assertEquals(EncryptionStatus.NOT_INITIALIZED, EncryptionStatus.valueOf("NOT_INITIALIZED"))
        assertEquals(EncryptionStatus.DISABLED, EncryptionStatus.valueOf("DISABLED"))
        assertEquals(EncryptionStatus.ERROR, EncryptionStatus.valueOf("ERROR"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test valueOf with invalid name throws exception`() {
        EncryptionStatus.valueOf("INVALID_STATUS")
    }

    @Test
    fun `test enum comparison and ordering`() {
        assertTrue(EncryptionStatus.ACTIVE.ordinal < EncryptionStatus.NOT_INITIALIZED.ordinal)
        assertTrue(EncryptionStatus.NOT_INITIALIZED.ordinal < EncryptionStatus.DISABLED.ordinal)
        assertTrue(EncryptionStatus.DISABLED.ordinal < EncryptionStatus.ERROR.ordinal)
    }

    @Test
    fun `test enum equality`() {
        val status1 = EncryptionStatus.ACTIVE
        val status2 = EncryptionStatus.ACTIVE
        val status3 = EncryptionStatus.DISABLED

        assertEquals(status1, status2)
        assertNotEquals(status1, status3)
    }

    @Test
    fun `test when expression with all cases`() {
        val testCases = listOf(
            EncryptionStatus.ACTIVE to "active",
            EncryptionStatus.NOT_INITIALIZED to "not_initialized",
            EncryptionStatus.DISABLED to "disabled",
            EncryptionStatus.ERROR to "error"
        )

        testCases.forEach { (status, expected) ->
            val result = when (status) {
                EncryptionStatus.ACTIVE -> "active"
                EncryptionStatus.NOT_INITIALIZED -> "not_initialized"
                EncryptionStatus.DISABLED -> "disabled"
                EncryptionStatus.ERROR -> "error"
            }
            assertEquals(expected, result)
        }
    }

    @Test
    fun `test enum in collections`() {
        val statusList = listOf(
            EncryptionStatus.ACTIVE,
            EncryptionStatus.DISABLED,
            EncryptionStatus.ERROR
        )

        assertTrue(statusList.contains(EncryptionStatus.ACTIVE))
        assertTrue(statusList.contains(EncryptionStatus.DISABLED))
        assertFalse(statusList.contains(EncryptionStatus.NOT_INITIALIZED))
    }

    @Test
    fun `test enum in sets for uniqueness`() {
        val statusSet = setOf(
            EncryptionStatus.ACTIVE,
            EncryptionStatus.ACTIVE,
            EncryptionStatus.DISABLED
        )

        assertEquals(2, statusSet.size)
        assertTrue(statusSet.contains(EncryptionStatus.ACTIVE))
        assertTrue(statusSet.contains(EncryptionStatus.DISABLED))
    }

    @Test
    fun `test enum serialization toString`() {
        assertEquals("ACTIVE", EncryptionStatus.ACTIVE.toString())
        assertEquals("NOT_INITIALIZED", EncryptionStatus.NOT_INITIALIZED.toString())
        assertEquals("DISABLED", EncryptionStatus.DISABLED.toString())
        assertEquals("ERROR", EncryptionStatus.ERROR.toString())
    }
}
