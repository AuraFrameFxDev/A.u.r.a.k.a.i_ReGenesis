package dev.aurakai.auraframefx.common

/** Common error handler contract. */
interface ErrorHandler {
    /**
 * Processes or responds to the provided error.
 *
 * Implementations define how the error is handled (for example: logging, user feedback, recovery).
 *
 * @param error The Throwable representing the error or exception to be handled.
 */
fun handle(error: Throwable)
}
