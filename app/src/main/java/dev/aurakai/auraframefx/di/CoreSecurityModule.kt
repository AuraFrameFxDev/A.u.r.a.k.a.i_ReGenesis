package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.aura.services.iconify.IconifyService
import dev.aurakai.auraframefx.domains.aura.services.iconify.SystemIconifyService
import dev.aurakai.auraframefx.domains.aura.ui.ark.ArkFusionBuildEngine
import dev.aurakai.auraframefx.domains.aura.ui.ark.FusionBuildEngine
import dev.aurakai.auraframefx.domains.genesis.oracledrive.bridges.BridgeMemorySink
import dev.aurakai.auraframefx.domains.genesis.oracledrive.bridges.NexusMemoryBridgeSink
import dev.aurakai.auraframefx.domains.genesis.services.GrokAnalysisService
import dev.aurakai.auraframefx.domains.genesis.services.GrokAnalysisServiceImpl
import dev.aurakai.auraframefx.domains.nexus.preferences.UserPreferencesState
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreSecurityModule {

    @Binds @Singleton
    abstract fun bindIconifyService(impl: SystemIconifyService): IconifyService

    @Binds @Singleton
    abstract fun bindFusionBuildEngine(impl: ArkFusionBuildEngine): FusionBuildEngine

    @Binds @Singleton
    abstract fun bindBridgeMemorySink(impl: NexusMemoryBridgeSink): BridgeMemorySink

    @Binds @Singleton
    abstract fun bindGrokAnalysisService(impl: GrokAnalysisServiceImpl): GrokAnalysisService
}

@Module
@InstallIn(SingletonComponent::class)
object CoreSecurityProvidesModule {

    @Provides @Singleton
    fun provideDefaultUserPreferences(): UserPreferencesState = UserPreferencesState()
}
