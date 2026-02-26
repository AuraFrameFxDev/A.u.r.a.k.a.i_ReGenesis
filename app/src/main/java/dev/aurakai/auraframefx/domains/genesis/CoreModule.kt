package dev.aurakai.auraframefx.domains.genesis

import android.content.Context
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun providePythonProcessManager(
        @ApplicationContext context: Context
    ): PythonProcessManager = PythonProcessManager(context)

    @Provides
    @Singleton
    fun provideAgentMessageBus(
        orchestrator: Lazy<GenesisOrchestrator>
    ): AgentMessageBus = orchestrator.get()
}

