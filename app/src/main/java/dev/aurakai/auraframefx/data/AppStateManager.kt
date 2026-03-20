package dev.aurakai.auraframefx.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A thread-safe singleton object responsible for managing the global application state.
 *
 * It uses a [StateFlow] to hold the current state, allowing various parts of the application
 * to observe state changes reactively and safely.
 */
object AppStateManager {

    // Private mutable state flow to hold the current state.
    private val _state = MutableStateFlow("default")

    // Public immutable state flow that other classes can collect to observe state changes.
    val state: StateFlow<String> = _state.asStateFlow()

    /**
     * Updates the current application state.
     * This is the only way to modify the state from outside this manager.
     *
     * @param newState The new state to set.
     */
    fun updateState(newState: String) {
        _state.value = newState
    }

    /**
     * Retrieves the current state synchronously.
     *
     * @return The current state value.
     */
    fun getCurrentState(): String = state.value
}
