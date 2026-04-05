package dev.aurakai.auraframefx.core.security

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive unit tests for SecurityContext.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SecurityContextTest {

    private lateinit var securityContext: SecurityContext
    private val keystoreManager: KeystoreManager = mockk()
    private val context: Context = mockk()

    @Before
    fun setup() {
        securityContext = SecurityContext(keystoreManager, context)
    }

    @Test
    fun `test initial session state is unauthenticated`() = runTest {
        val state = securityContext.sessionState.first()
        assertTrue("Initial state should be Unauthenticated", state is SecurityContext.SessionState.Unauthenticated)
    }

    @Test
    fun `test authenticate success`() = runTest {
        val userId = "testUser"
        val token = "valid_token_32_chars_long_exactly_!!!"
        every { keystoreManager.validateToken(token) } returns true

        val result = securityContext.authenticate(userId, token)

        assertTrue("Authentication should succeed", result)
        val state = securityContext.sessionState.first()
        assertTrue("State should be Authenticated", state is SecurityContext.SessionState.Authenticated)
        assertEquals(userId, (state as SecurityContext.SessionState.Authenticated).userId)
    }

    @Test
    fun `test authenticate failure`() = runTest {
        val userId = "testUser"
        val token = "invalid_token"
        every { keystoreManager.validateToken(token) } returns false

        val result = securityContext.authenticate(userId, token)

        assertFalse("Authentication should fail", result)
        assertTrue("State should remain Unauthenticated", securityContext.sessionState.first() is SecurityContext.SessionState.Unauthenticated)
    }

    @Test
    fun `test bootstrapSovereignSession in dev mode`() = runTest {
        System.setProperty("aurakai.dev_mode", "true")
        
        securityContext.bootstrapSovereignSession("TEST_SOVEREIGN")

        val state = securityContext.sessionState.first()
        assertTrue(state is SecurityContext.SessionState.Authenticated)
        assertTrue(securityContext.hasPermission(SecurityContext.SecurityPermission.SOVEREIGN_ACCESS))
        
        System.clearProperty("aurakai.dev_mode")
    }

    @Test(expected = SecurityException::class)
    fun `test bootstrapSovereignSession unauthorized throws exception`() {
        System.setProperty("aurakai.dev_mode", "false")
        securityContext.bootstrapSovereignSession("NORMAL_USER")
    }

    @Test
    fun `test validateRequest blocks unauthenticated`() {
        assertThrows(SecurityException::class.java) {
            securityContext.validateRequest("root_op", "details")
        }
    }

    @Test
    fun `test revoke resets state`() = runTest {
        securityContext.bootstrapSovereignSession("SYSTEM_ADMIN")
        securityContext.revoke()

        assertTrue(securityContext.sessionState.first() is SecurityContext.SessionState.Unauthenticated)
        assertFalse(securityContext.isAuthenticated())
    }

    @Test
    fun `test lock sets state to Locked`() = runTest {
        securityContext.lock()
        assertTrue(securityContext.sessionState.first() is SecurityContext.SessionState.Locked)
    }
}
