package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.models.SovereignChatModel
import dev.aurakai.auraframefx.core.di.qualifiers.ApplicationScope
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.aurakai.auraframefx.domains.kai.security.TemporalAegis
import dev.langchain4j.model.chat.ChatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Core Genesis Dependency Provision Module
 *
 * Provides all major DI bindings for:
 * - Coroutine Scope bindings
 * - Sovereign Chat Model
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
     * This is injected by PredictiveVetoMonitor and other long-lived services.
     * Uses SupervisorJob to isolate failures across child coroutines.
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
