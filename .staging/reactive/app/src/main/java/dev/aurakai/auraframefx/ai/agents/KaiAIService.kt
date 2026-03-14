package dev.aurakai.auraframefx.ai.agents

/**
 * Minimal KaiAIService stub for DI. The real implementation provides security/monitoring services.
 */
interface KaiAIService {
    /**
 * Start the AI service.
 *
 * Initiates the service so it is ready for use.
 *
 * @return `true` if the service started successfully, `false` otherwise.
 */
suspend fun start(): Boolean
}

class NoOpKaiAIService : KaiAIService {
    /**
 * Starts the no-op AI service used for dependency injection; always reports success.
 *
 * @return `true` if the service started successfully (always `true` for this no-op implementation).
 */
override suspend fun start(): Boolean = true
}
