package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.kai.KaiAgent
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.ai.task.execution.TaskExecutionManager
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskExecutionModule {

    @Provides
    @Singleton
    fun provideTaskExecutionManager(
        auraAgent: AuraAgent,
        kaiAgent: KaiAgent,
        genesisAgent: GenesisAgent,
        securityContext: SecurityContext,
        logger: AuraFxLogger
    ): TaskExecutionManager {
        return TaskExecutionManager(
            auraAgent = auraAgent,
            kaiAgent = kaiAgent,
            genesisAgent = genesisAgent,
            securityContext = securityContext,
            logger = logger
        )
    }
}
