package dev.aurakai.auraframefx.domains.kai.security

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [SovereignStateManager] covering the PR changes:
 * - Constructor no longer takes Context or EncryptedSharedPreferences
 * - requestSovereignFreeze() takes no parameters (no spiritualDelta, no kvCache)
 * - requestSovereignRestore() returns Unit (no Pair<String?, ByteArray?> return)
 * - No memory-mapped file or encrypted prefs logic
 * - State lifecycle: ACTIVE → FROZEN → RECOVERING → ACTIVE / EMERGENCY → RECOVERING → ACTIVE
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("SovereignStateManager")
class SovereignStateManagerTest {

    private lateinit var sentinelBus: KaiSentinelBus
    private lateinit var manager: SovereignStateManager

    @BeforeEach
    fun setUp() {
        sentinelBus = mockk(relaxed = true)
        manager = SovereignStateManager(sentinelBus)
    }

    // ─── Initial State ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Initial state")
    inner class InitialState {

        @Test
        fun `starts in ACTIVE state`() = runTest {
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.first())
        }

        @Test
        fun `isOperational returns true initially`() {
            assertTrue(manager.isOperational())
        }

        @Test
        fun `isFrozen returns false initially`() {
            assertFalse(manager.isFrozen())
        }
    }

    // ─── requestSovereignFreeze ───────────────────────────────────────────────

    @Nested
    @DisplayName("requestSovereignFreeze — no-arg form")
    inner class RequestSovereignFreeze {

        @Test
        fun `transitions state to FROZEN`() = runTest {
            manager.requestSovereignFreeze()
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.first())
        }

        @Test
        fun `isOperational returns false after freeze`() {
            manager.requestSovereignFreeze()
            assertFalse(manager.isOperational())
        }

        @Test
        fun `isFrozen returns true after freeze`() {
            manager.requestSovereignFreeze()
            assertTrue(manager.isFrozen())
        }

        @Test
        fun `emits FREEZING sovereign event to sentinelBus`() {
            manager.requestSovereignFreeze()
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FREEZING) }
        }

        @Test
        fun `calling freeze twice leaves state FROZEN`() {
            manager.requestSovereignFreeze()
            manager.requestSovereignFreeze()
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.value)
        }
    }

    // ─── requestSovereignRestore ──────────────────────────────────────────────

    @Nested
    @DisplayName("requestSovereignRestore — Unit return, only acts from FROZEN")
    inner class RequestSovereignRestore {

        @Test
        fun `restores from FROZEN to ACTIVE`() = runTest {
            manager.requestSovereignFreeze()
            manager.requestSovereignRestore()
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.first())
        }

        @Test
        fun `isOperational returns true after restore`() {
            manager.requestSovereignFreeze()
            manager.requestSovereignRestore()
            assertTrue(manager.isOperational())
        }

        @Test
        fun `isFrozen returns false after restore`() {
            manager.requestSovereignFreeze()
            manager.requestSovereignRestore()
            assertFalse(manager.isFrozen())
        }

        @Test
        fun `emits AWAKE sovereign event after successful restore`() {
            manager.requestSovereignFreeze()
            manager.requestSovereignRestore()
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE) }
        }

        @Test
        fun `restore is a no-op when not FROZEN`() {
            // state is ACTIVE — restore should not do anything
            manager.requestSovereignRestore()
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.value)
            verify(exactly = 0) { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE) }
        }

        @Test
        fun `restore is a no-op from EMERGENCY state`() {
            manager.enterEmergencyMode()
            manager.requestSovereignRestore()
            // EMERGENCY → restore should not fire (restore only acts from FROZEN)
            assertEquals(SovereignStateManager.SovereignState.EMERGENCY, manager.state.value)
        }
    }

    // ─── enterEmergencyMode ───────────────────────────────────────────────────

    @Nested
    @DisplayName("enterEmergencyMode")
    inner class EnterEmergencyMode {

        @Test
        fun `transitions state to EMERGENCY`() = runTest {
            manager.enterEmergencyMode()
            assertEquals(SovereignStateManager.SovereignState.EMERGENCY, manager.state.first())
        }

        @Test
        fun `isOperational returns false in EMERGENCY`() {
            manager.enterEmergencyMode()
            assertFalse(manager.isOperational())
        }

        @Test
        fun `emits NEUTRALIZING event to sentinelBus`() {
            manager.enterEmergencyMode()
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.NEUTRALIZING) }
        }

        @Test
        fun `can enter emergency from FROZEN state`() {
            manager.requestSovereignFreeze()
            manager.enterEmergencyMode()
            assertEquals(SovereignStateManager.SovereignState.EMERGENCY, manager.state.value)
        }
    }

    // ─── recoverFromEmergency ─────────────────────────────────────────────────

    @Nested
    @DisplayName("recoverFromEmergency")
    inner class RecoverFromEmergency {

        @Test
        fun `transitions from EMERGENCY to ACTIVE`() = runTest {
            manager.enterEmergencyMode()
            manager.recoverFromEmergency()
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.first())
        }

        @Test
        fun `isOperational returns true after recovery`() {
            manager.enterEmergencyMode()
            manager.recoverFromEmergency()
            assertTrue(manager.isOperational())
        }

        @Test
        fun `emits AWAKE event after recovery`() {
            manager.enterEmergencyMode()
            manager.recoverFromEmergency()
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE) }
        }

        @Test
        fun `recovery is a no-op when not in EMERGENCY`() {
            manager.recoverFromEmergency()
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.value)
            verify(exactly = 0) { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE) }
        }

        @Test
        fun `recovery is a no-op from FROZEN state`() {
            manager.requestSovereignFreeze()
            manager.recoverFromEmergency()
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.value)
        }
    }

    // ─── State Lifecycle ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Full state lifecycle")
    inner class StatLifecycle {

        @Test
        fun `freeze-restore cycle returns to ACTIVE`() {
            manager.requestSovereignFreeze()
            assertTrue(manager.isFrozen())
            manager.requestSovereignRestore()
            assertTrue(manager.isOperational())
        }

        @Test
        fun `emergency-recover cycle returns to ACTIVE`() {
            manager.enterEmergencyMode()
            assertFalse(manager.isOperational())
            manager.recoverFromEmergency()
            assertTrue(manager.isOperational())
        }

        @Test
        fun `multiple freeze-restore cycles work correctly`() {
            repeat(3) {
                manager.requestSovereignFreeze()
                assertTrue(manager.isFrozen())
                manager.requestSovereignRestore()
                assertTrue(manager.isOperational())
            }
        }

        @Test
        fun `freeze then emergency then recover leaves state ACTIVE`() {
            manager.requestSovereignFreeze()
            manager.enterEmergencyMode()
            manager.recoverFromEmergency()
            assertTrue(manager.isOperational())
        }

        @Test
        fun `state flow reflects every transition`() = runTest {
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.first())
            manager.requestSovereignFreeze()
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.first())
            manager.requestSovereignRestore()
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.first())
        }
    }

    // ─── Regression: removed Context/Encrypted Prefs ─────────────────────────

    @Nested
    @DisplayName("Regression — simplified constructor (no Context)")
    inner class RegressionSimplifiedConstructor {

        @Test
        fun `can be constructed with only KaiSentinelBus — no Context needed`() {
            // If this test compiles and passes, the refactoring removed Context correctly
            val bus = mockk<KaiSentinelBus>(relaxed = true)
            val sm = SovereignStateManager(bus)
            assertTrue(sm.isOperational())
        }

        @Test
        fun `requestSovereignFreeze accepts no arguments`() {
            // Regression: old signature had (spiritualDelta: String, kvCache: ByteArray?)
            // New signature: no parameters
            manager.requestSovereignFreeze() // must compile with no args
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.value)
        }

        @Test
        fun `requestSovereignRestore returns Unit not Pair`() {
            manager.requestSovereignFreeze()
            // If this compiles without assigning a return value, it correctly returns Unit
            manager.requestSovereignRestore()
            assertTrue(manager.isOperational())
        }
    }
}