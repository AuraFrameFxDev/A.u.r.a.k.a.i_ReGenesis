package dev.aurakai.auraframefx.kai

/** Minimal Kai AI service interface expected by DI. */
interface KaiAIService {
    /**
 * Activate the AI service and perform any initialization required for operation.
 *
 * @return `true` if activation succeeded, `false` otherwise.
 */
suspend fun activate(): Boolean
}

/** No-op implementation used for DI during development. */
class NoopKaiAIService : KaiAIService {
    /**
 * Activate the AI service.
 *
 * No-op implementation used for dependency injection during development; always succeeds.
 *
 * @return `true` if activation succeeded (always `true` for this implementation).
 */
override suspend fun activate(): Boolean = true
}