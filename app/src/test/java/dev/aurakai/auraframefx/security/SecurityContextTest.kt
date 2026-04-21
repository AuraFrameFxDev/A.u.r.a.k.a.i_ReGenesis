package dev.aurakai.auraframefx.security

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Comprehensive unit tests for DefaultSecurityContext.
 * Tests state management, security operations, and edge cases.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SecurityContextTest {

    private lateinit var securityContext: DefaultSecurityContext

    @BeforeEach
    fun setup() {
        securityContext = DefaultSecurityContext()
    }

    // Security State Tests
    @Test
    fun `test initial security state is not in error`() = runTest {
        val state = securityContext.securityState.first()
        assertFalse("Initial state should not be in error", state.errorState)
        assertNull("Initial state should have no error message", state.errorMessage)
    }

    @Test
    fun `test setSecurityError updates state correctly`() = runTest {
        val errorMessage = "Test security error"
        securityContext.setSecurityError(errorMessage)

        val state = securityContext.securityState.first()
        assertTrue("State should be in error", state.errorState)
        assertEquals("Error message should match", errorMessage, state.errorMessage)
    }

    @Test
    fun `test clearSecurityError resets state`() = runTest {
        securityContext.setSecurityError("Error occurred")
        securityContext.clearSecurityError()

        val state = securityContext.securityState.first()
        assertFalse("State should not be in error after clearing", state.errorState)
        assertNull("Error message should be null after clearing", state.errorMessage)
    }

    @Test
    fun `test multiple security error updates`() = runTest {
        securityContext.setSecurityError("First error")
        securityContext.setSecurityError("Second error")

        val state = securityContext.securityState.first()
        assertEquals("Should have latest error message", "Second error", state.errorMessage)
    }

    // Encryption Status Tests
    @Test
    fun `test initial encryption status is NOT_INITIALIZED`() = runTest {
        val status = securityContext.encryptionStatus.first()
        assertEquals("Initial encryption status should be NOT_INITIALIZED",
            EncryptionStatus.NOT_INITIALIZED, status)
    }

    @Test
    fun `test setEncryptionStatus updates correctly`() = runTest {
        securityContext.setEncryptionStatus(EncryptionStatus.ACTIVE)

        val status = securityContext.encryptionStatus.first()
        assertEquals("Encryption status should be ACTIVE", EncryptionStatus.ACTIVE, status)
    }

    @Test
    fun `test encryption status transitions`() = runTest {
        // Test typical lifecycle transitions
        securityContext.setEncryptionStatus(EncryptionStatus.ACTIVE)
        assertEquals(EncryptionStatus.ACTIVE, securityContext.encryptionStatus.first())

        securityContext.setEncryptionStatus(EncryptionStatus.DISABLED)
        assertEquals(EncryptionStatus.DISABLED, securityContext.encryptionStatus.first())

        securityContext.setEncryptionStatus(EncryptionStatus.ERROR)
        assertEquals(EncryptionStatus.ERROR, securityContext.encryptionStatus.first())
    }

    // Permissions Tests
    @Test
    fun `test initial permissions state is empty`() = runTest {
        val permissions = securityContext.permissionsState.first()
        assertTrue("Initial permissions should be empty", permissions.isEmpty())
    }

    @Test
    fun `test updatePermissions with single permission`() = runTest {
        val permissions = mapOf("READ" to true)
        securityContext.updatePermissions(permissions)

        val state = securityContext.permissionsState.first()
        assertEquals(1, state.size)
        assertTrue("READ permission should be granted", state["READ"] == true)
    }

    @Test
    fun `test updatePermissions with multiple permissions`() = runTest {
        val permissions = mapOf(
            "READ" to true,
            "WRITE" to false,
            "EXECUTE" to true,
            "DELETE" to false
        )
        securityContext.updatePermissions(permissions)

        val state = securityContext.permissionsState.first()
        assertEquals(4, state.size)
        assertTrue(state["READ"] == true)
        assertFalse(state["WRITE"] == true)
        assertTrue(state["EXECUTE"] == true)
        assertFalse(state["DELETE"] == true)
    }

    @Test
    fun `test updatePermissions replaces previous permissions`() = runTest {
        securityContext.updatePermissions(mapOf("OLD" to true))
        securityContext.updatePermissions(mapOf("NEW" to true))

        val state = securityContext.permissionsState.first()
        assertEquals(1, state.size)
        assertFalse("Old permission should not exist", state.containsKey("OLD"))
        assertTrue("New permission should exist", state.containsKey("NEW"))
    }

    @Test
    fun `test hasPermission always returns true in development mode`() {
        assertTrue(securityContext.hasPermission("ANY_PERMISSION"))
        assertTrue(securityContext.hasPermission("READ"))
        assertTrue(securityContext.hasPermission("WRITE"))
        assertTrue(securityContext.hasPermission(""))
    }

    // Threat Detection Tests
    @Test
    fun `test initial threat detection is inactive`() = runTest {
        val isActive = securityContext.threatDetectionActive.first()
        assertFalse("Threat detection should be inactive initially", isActive)
    }

    @Test
    fun `test startThreatDetection activates monitoring`() = runTest {
        securityContext.startThreatDetection()

        val isActive = securityContext.threatDetectionActive.first()
        assertTrue("Threat detection should be active", isActive)
    }

    @Test
    fun `test startThreatDetection sets encryption to ACTIVE`() = runTest {
        securityContext.startThreatDetection()

        val encryptionStatus = securityContext.encryptionStatus.first()
        assertEquals("Encryption should be ACTIVE when threat detection starts",
            EncryptionStatus.ACTIVE, encryptionStatus)
    }

    @Test
    fun `test stopThreatDetection deactivates monitoring`() = runTest {
        securityContext.startThreatDetection()
        securityContext.stopThreatDetection()

        val isActive = securityContext.threatDetectionActive.first()
        assertFalse("Threat detection should be inactive after stopping", isActive)
    }

    @Test
    fun `test threat detection lifecycle`() = runTest {
        // Initial state
        assertFalse(securityContext.threatDetectionActive.first())

        // Start
        securityContext.startThreatDetection()
        assertTrue(securityContext.threatDetectionActive.first())

        // Stop
        securityContext.stopThreatDetection()
        assertFalse(securityContext.threatDetectionActive.first())

        // Restart
        securityContext.startThreatDetection()
        assertTrue(securityContext.threatDetectionActive.first())
    }

    // User and Mode Tests
    @Test
    fun `test getCurrentUser returns default user`() {
        assertEquals("genesis_user", securityContext.getCurrentUser())
    }

    @Test
    fun `test isSecureMode returns false in development`() {
        assertFalse("Should not be in secure mode by default", securityContext.isSecureMode())
    }

    @Test
    fun `test validateAccess always allows in development`() {
        assertTrue(securityContext.validateAccess("/secure/resource"))
        assertTrue(securityContext.validateAccess("/public/resource"))
        assertTrue(securityContext.validateAccess(""))
    }

    // Application Integrity Tests
    @Test
    fun `test verifyApplicationIntegrity returns valid by default`() {
        val integrity = securityContext.verifyApplicationIntegrity()

        assertTrue("Application should be valid", integrity.isValid)
        assertEquals("default_signature_hash", integrity.signatureHash)
    }

    @Test
    fun `test verifyApplicationIntegrity is consistent`() {
        val integrity1 = securityContext.verifyApplicationIntegrity()
        val integrity2 = securityContext.verifyApplicationIntegrity()

        assertEquals(integrity1.signatureHash, integrity2.signatureHash)
        assertEquals(integrity1.isValid, integrity2.isValid)
    }

    // Security Event Logging Tests
    @Test
    fun `test logSecurityEvent does not throw exception`() {
        val event = SecurityEvent(
            type = SecurityEventType.INTEGRITY_CHECK,
            details = "Test event",
            severity = EventSeverity.LOW
        )

        // Should not throw
        securityContext.logSecurityEvent(event, error.message ?: "Unknown security error")
    }

    @Test
    fun `test logSecurityEvent with all event types`() {
        val events = listOf(
            SecurityEvent(SecurityEventType.INTEGRITY_CHECK, "Integrity check", EventSeverity.LOW),
            SecurityEvent(SecurityEventType.PERMISSION_VIOLATION, "Permission denied", EventSeverity.MEDIUM),
            SecurityEvent(SecurityEventType.ACCESS_DENIED, "Access denied", EventSeverity.HIGH)
        )

        events.forEach { event ->
            // Should not throw
            securityContext.logSecurityEvent(event, error.message ?: "Unknown security error")
        }
    }

    @Test
    fun `test logSecurityEvent with all severities`() {
        EventSeverity.values().forEach { severity ->
            val event = SecurityEvent(
                type = SecurityEventType.INTEGRITY_CHECK,
                details = "Test with $severity",
                severity = severity
            )
            // Should not throw
            securityContext.logSecurityEvent(event, error.message ?: "Unknown security error")
        }
    }

    // Data Class Tests
    @Test
    fun `test SecurityState data class equality`() {
        val state1 = SecurityState(errorState = true, errorMessage = "Error")
        val state2 = SecurityState(errorState = true, errorMessage = "Error")
        val state3 = SecurityState(errorState = false, errorMessage = null)

        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
    }

    @Test
    fun `test SecurityState copy functionality`() {
        val original = SecurityState(errorState = true, errorMessage = "Original")
        val copied = original.copy(errorMessage = "Modified")

        assertTrue(copied.errorState)
        assertEquals("Modified", copied.errorMessage)
        assertEquals("Original", original.errorMessage)
    }

    @Test
    fun `test ApplicationIntegrity data class`() {
        val integrity = ApplicationIntegrity(signatureHash = "abc123", isValid = true)

        assertEquals("abc123", integrity.signatureHash)
        assertTrue(integrity.isValid)
    }

    @Test
    fun `test SecurityEvent data class`() {
        val event = SecurityEvent(
            type = SecurityEventType.ACCESS_DENIED,
            details = "Unauthorized access attempt",
            severity = EventSeverity.CRITICAL
        )

        assertEquals(SecurityEventType.ACCESS_DENIED, event.type)
        assertEquals("Unauthorized access attempt", event.details)
        assertEquals(EventSeverity.CRITICAL, event.severity)
    }

    // Enum Tests
    @Test
    fun `test SecurityEventType enum values`() {
        val types = SecurityEventType.values()
        assertEquals(3, types.size)
        assertTrue(types.contains(SecurityEventType.INTEGRITY_CHECK))
        assertTrue(types.contains(SecurityEventType.PERMISSION_VIOLATION))
        assertTrue(types.contains(SecurityEventType.ACCESS_DENIED))
    }

    @Test
    fun `test EventSeverity enum values`() {
        val severities = EventSeverity.values()
        assertEquals(4, severities.size)
        assertTrue(severities.contains(EventSeverity.LOW))
        assertTrue(severities.contains(EventSeverity.MEDIUM))
        assertTrue(severities.contains(EventSeverity.HIGH))
        assertTrue(severities.contains(EventSeverity.CRITICAL))
    }

    @Test
    fun `test EventSeverity ordering`() {
        assertTrue(EventSeverity.LOW.ordinal < EventSeverity.MEDIUM.ordinal)
        assertTrue(EventSeverity.MEDIUM.ordinal < EventSeverity.HIGH.ordinal)
        assertTrue(EventSeverity.HIGH.ordinal < EventSeverity.CRITICAL.ordinal)
    }
}
