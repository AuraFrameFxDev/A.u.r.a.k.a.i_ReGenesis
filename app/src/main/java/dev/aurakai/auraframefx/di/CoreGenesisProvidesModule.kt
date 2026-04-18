package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.models.SovereignChatModel
import dev.aurakai.auraframefx.core.di.qualifiers.ApplicationScope
import dev.aurakai.auraframefx.domains.cascade.utils.ErrorHandler
import dev.aurakai.auraframefx.domains.cascade.utils.ErrorStats
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.aurakai.auraframefx.domains.kai.security.TemporalAegis
import dev.langchain4j.model.chat.ChatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Singleton

/**
 * Core Genesis Dependency Provision Module
 *
 * Provides all major DI bindings for:
 * - Coroutine Scope bindings
 * - Sovereign Chat Model
 * - Cascade ErrorHandler
 *
 * This module centralizes the wiring of all critical dependencies
 * that feed into the Trinity (Genesis, Aura, Kai) coordinate system.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreGenesisProvidesModule {

    /**
     * Entry #16: Provide the Sovereign Chat Model (The Predator)
     * Using interface ChatLanguageModel as suggested to satisfy KSP.
     */
    @Provides
    @Singleton
    fun provideSovereignChatModel(
        turboQuant: TurboQuantCache,
        aegis: TemporalAegis,
        vertexAIClient: dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
    ): ChatModel = SovereignChatModel(turboQuant, aegis, vertexAIClient)

    /**
     * Provides the Application-level CoroutineScope.
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Provides the Cascade-level ErrorHandler.
     * This implementation is used by NemotronAIService and other core AI components.
     */
    @Provides
    @Singleton
    fun provideCascadeErrorHandler(): ErrorHandler = object : ErrorHandler {
        override fun handleError(error: Throwable, operation: String) {
            Timber.tag("CascadeErrorHandler").e(error, "[$operation] ${error.message}")
        }
        override fun reportCriticalError(error: Throwable, context: String) {
            Timber.tag("CascadeErrorHandler").e(error, "[CRITICAL] $context - ${error.message}")
        }
        override fun getRecoverySuggestions(error: Throwable): List<String> {
            return listOf(
                "Verify API endpoints are reachable",
                "Check authentication tokens",
                "Retry after exponential backoff"
            )
        }
        override fun isRecoverable(error: Throwable): Boolean {
            return error !is SecurityException && error !is OutOfMemoryError
        }
        override fun getErrorStats(): ErrorStats {
            return ErrorStats(
                totalErrors = 0,
                criticalErrors = 0,
                recoverableErrors = 0,
                errorsByType = emptyMap()
            )
        }
    }
}
