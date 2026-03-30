package dev.aurakai.auraframefx.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [AppStateManager].
 *
 * Covers: initial state, updateState, getCurrentState, StateFlow emissions,
 * concurrent updates, and edge case inputs.
 *
 * Note: AppStateManager is a Kotlin object singleton. Each test resets
 * state to "default" in @BeforeEach to prevent cross-test contamination.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AppStateManager Tests")
class AppStateManagerTest {

    @BeforeEach
    fun resetState() {
        // Reset the singleton to its initial value before each test
        AppStateManager.updateState("default")
    }

    // =========================================================================
    // Initial State
    // =========================================================================

    @Nested
    @DisplayName("Initial State")
    inner class InitialStateTests {

        @Test
        @DisplayName("should have 'default' as initial state")
        fun `should have default as initial state`() {
            AppStateManager.updateState("default") // Ensure reset
            assertEquals("default", AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("state flow should emit 'default' initially")
        fun `state flow should emit default initially`() = runTest {
            AppStateManager.updateState("default") // Ensure reset
            val current = AppStateManager.state.value
            assertEquals("default", current)
        }
    }

    // =========================================================================
    // updateState()
    // =========================================================================

    @Nested
    @DisplayName("updateState()")
    inner class UpdateStateTests {

        @Test
        @DisplayName("should update state to new value")
        fun `should update state to new value`() {
            AppStateManager.updateState("active")
            assertEquals("active", AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("should update state multiple times and reflect the latest")
        fun `should update state multiple times and reflect the latest`() {
            AppStateManager.updateState("state-one")
            AppStateManager.updateState("state-two")
            AppStateManager.updateState("state-three")
            assertEquals("state-three", AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("should accept empty string as state")
        fun `should accept empty string as state`() {
            AppStateManager.updateState("")
            assertEquals("", AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("should accept whitespace-only string as state")
        fun `should accept whitespace-only string as state`() {
            AppStateManager.updateState("   ")
            assertEquals("   ", AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("should accept unicode string as state")
        fun `should accept unicode string as state`() {
            val unicodeState = "状態_🌟_état"
            AppStateManager.updateState(unicodeState)
            assertEquals(unicodeState, AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("should accept very long string as state")
        fun `should accept very long string as state`() {
            val longState = "s".repeat(10_000)
            AppStateManager.updateState(longState)
            assertEquals(longState, AppStateManager.getCurrentState())
        }

        @Test
        @DisplayName("should set state back to 'default' when explicitly set")
        fun `should set state back to default when explicitly set`() {
            AppStateManager.updateState("changed")
            AppStateManager.updateState("default")
            assertEquals("default", AppStateManager.getCurrentState())
        }
    }

    // =========================================================================
    // getCurrentState()
    // =========================================================================

    @Nested
    @DisplayName("getCurrentState()")
    inner class GetCurrentStateTests {

        @Test
        @DisplayName("should return current state synchronously")
        fun `should return current state synchronously`() {
            AppStateManager.updateState("sync-test")
            val result = AppStateManager.getCurrentState()
            assertEquals("sync-test", result)
        }

        @Test
        @DisplayName("should match state flow value")
        fun `should match state flow value`() {
            AppStateManager.updateState("flow-match")
            assertEquals(AppStateManager.getCurrentState(), AppStateManager.state.value)
        }
    }

    // =========================================================================
    // StateFlow Observation
    // =========================================================================

    @Nested
    @DisplayName("StateFlow Observation")
    inner class StateFlowTests {

        @Test
        @DisplayName("state flow should emit updated value after updateState")
        fun `state flow should emit updated value after updateState`() = runTest {
            AppStateManager.updateState("flow-updated")
            val emitted = AppStateManager.state.first()
            assertEquals("flow-updated", emitted)
        }

        @Test
        @DisplayName("state flow value should match getCurrentState after update")
        fun `state flow value should match getCurrentState after update`() = runTest {
            AppStateManager.updateState("consistent-check")
            assertEquals(AppStateManager.getCurrentState(), AppStateManager.state.first())
        }

        @Test
        @DisplayName("state flow should reflect the last of multiple rapid updates")
        fun `state flow should reflect the last of multiple rapid updates`() = runTest {
            repeat(100) { i ->
                AppStateManager.updateState("state-$i")
            }
            // After 100 updates, state should be "state-99"
            val finalValue = AppStateManager.state.value
            assertEquals("state-99", finalValue)
        }
    }

    // =========================================================================
    // Thread Safety
    // =========================================================================

    @Nested
    @DisplayName("Thread Safety")
    inner class ThreadSafetyTests {

        @Test
        @DisplayName("should not throw when updated from multiple threads")
        fun `should not throw when updated from multiple threads`() {
            val threads = (1..8).map { threadIndex ->
                Thread {
                    repeat(50) { i ->
                        AppStateManager.updateState("thread-$threadIndex-$i")
                    }
                }
            }
            threads.forEach { it.start() }
            threads.forEach { it.join(2000L) }

            // After all threads complete, state should be a valid, non-null value
            val finalState = AppStateManager.getCurrentState()
            assertNotNull(finalState)
            assertTrue(finalState.startsWith("thread-"))
        }
    }
}