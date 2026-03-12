package dev.aurakai.auraframefx.di

/** Minimal ErrorHandler interface placeholder to satisfy KSP bindings during incremental build. */
interface ErrorHandler {
    /**
 * Handles the given error represented by [throwable].
 *
 * Implementations determine how the error is processed (for example: logging, reporting, or ignoring).
 *
 * @param throwable The error to handle.
 */
fun handleError(throwable: Throwable)
}
