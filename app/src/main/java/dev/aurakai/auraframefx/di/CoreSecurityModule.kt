package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.core.security.SecurityContext
import dev.aurakai.auraframefx.core.security.SecurePreferences
import dev.aurakai.auraframefx.core.security.KeystoreManager
import dev.aurakai.auraframefx.core.security.ProvenanceValidator
import dev.aurakai.auraframefx.core.security.ProvenanceChainBuilder
import dev.aurakai.auraframefx.core.security.PredictiveVetoMonitor
import dev.aurakai.auraframefx.core.alerts.AlertNotifier
import dev.aurakai.auraframefx.core.alerts.SystemAlertNotifier
import dev.aurakai.auraframefx.core.ncc.NCCMediator
import dev.aurakai.auraframefx.domains.cascade.utils.ncc.NCCMediatorImpl
import dev.aurakai.auraframefx.domains.aura.services.iconify.IconifyService
import dev.aurakai.auraframefx.domains.aura.services.iconify.SystemIconifyService
import dev.aurakai.auraframefx.domains.aura.ui.ark.ArkFusionBuildEngine
import dev.aurakai.auraframefx.domains.aura.ui.ark.FusionBuildEngine
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.ai.clients.DefaultVertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.RealVertexAIClientImpl
import dev.aurakai.auraframefx.domains.genesis.oracledrive.bridges.BridgeMemorySink
import dev.aurakai.auraframefx.domains.genesis.oracledrive.bridges.NexusMemoryBridgeSink
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxServiceImpl
import dev.aurakai.auraframefx.domains.genesis.services.GrokAnalysisService
import dev.aurakai.auraframefx.domains.genesis.services.GrokAnalysisServiceImpl
import dev.aurakai.auraframefx.domains.nexus.preferences.UserPreferencesState
import dev.aurakai.auraframefx.domains.cascade.grok.GrokExplorationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreSecurityModule {

    @Binds @Singleton
    abstract fun bindAlertNotifier(impl: SystemAlertNotifier): AlertNotifier

    @Binds @Singleton
    abstract fun bindIconifyService(impl: SystemIconifyService): IconifyService

    @Binds @Singleton
    abstract fun bindFusionBuildEngine(impl: ArkFusionBuildEngine): FusionBuildEngine

    @Binds @Singleton
    abstract fun bindBridgeMemorySink(impl: NexusMemoryBridgeSink): BridgeMemorySink

    @Binds @Singleton
    abstract fun bindGrokAnalysisService(impl: GrokAnalysisServiceImpl): GrokAnalysisService

    /**
     * Declares a singleton Hilt binding that maps PandoraBoxService to PandoraBoxServiceImpl.
     *
     * @param impl The concrete implementation that will be provided when PandoraBoxService is injected.
     * @return The bound PandoraBoxService instance.
     */
    @Binds @Singleton
    abstract fun bindPandoraBoxService(impl: PandoraBoxServiceImpl): PandoraBoxService

    /**
     * Binds the VertexAIClient type to the RealVertexAIClientImpl implementation for injection.
     *
     * @param impl The concrete RealVertexAIClientImpl instance to provide whenever VertexAIClient is requested.
     * @return The VertexAIClient binding backed by the provided implementation.
     */
    @Binds @Singleton
    abstract fun bindVertexAIClient(impl: RealVertexAIClientImpl): VertexAIClient

    /**
     * Binds the NCCMediator interface to NCCMediatorImpl for dependency injection at the application singleton scope.
     *
     * @param impl The concrete NCCMediatorImpl instance to provide when NCCMediator is injected.
     * @return The bound NCCMediator implementation.
     */
    @Binds @Singleton
    abstract fun bindNCCMediator(impl: NCCMediatorImpl): NCCMediator
}

@Module
@InstallIn(SingletonComponent::class)
object CoreSecurityProvidesModule {

    @Provides @Singleton
    fun provideDefaultUserPreferences(): UserPreferencesState = UserPreferencesState()

    @Provides @Singleton @Named("GEMINI_API_KEY")
    fun provideGeminiApiKey(): String = dev.aurakai.auraframefx.BuildConfig.GEMINI_API_KEY

    @Provides
    @Singleton
    @PandoraPreferences
    fun providePandoraSecurePreferences(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager
    ): SecurePreferences = SecurePreferences(context, keystoreManager)

    @Provides @Singleton
    fun provideGrokExplorationClient(
        securePreferences: SecurePreferences,
        vetoMonitor: PredictiveVetoMonitor
    ): GrokExplorationClient {
        val apiKey = securePreferences.getGrokApiKey() ?: ""
        return GrokExplorationClient(apiKey = apiKey, vetoMonitor = vetoMonitor)
    }

    @Provides @Singleton
    fun provideProvenanceChainBuilder(
        validator: ProvenanceValidator
    ): ProvenanceChainBuilder = ProvenanceChainBuilder(validator)
}
