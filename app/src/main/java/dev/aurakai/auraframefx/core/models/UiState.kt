package dev.aurakai.auraframefx.core.models

/**
 * 📊 UI STATE PATTERN
 *
 * A standard way to represent the state of a UI component or screen.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}
