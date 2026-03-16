package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
// import dev.aurakai.auraframefx.domains.cascade.CascadeAIService  // Disabled: ghost cleanup
// import dev.aurakai.auraframefx.domains.cascade.RealCascadeAIServiceAdapter  // Disabled: ghost cleanup
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClientImpl
import dev.aurakai.auraframefx.oracledrive.genesis.ai.VertexAIConfig
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultAuraAIService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService

    @Binds
    @Singleton
    abstract fun bindVertexAIClient(impl: VertexAIClientImpl): VertexAIClient

    companion object {
        @Provides
        @Singleton
        fun provideVertexAIConfig(): VertexAIConfig = VertexAIConfig(
                projectId = "collabcanvas",
                location = "us-central1",
                modelName = "gemini-1.5-pro-002"
            )

        @Provides
        @Singleton
        fun provideNemotronEngine(): dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine = object : dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine {
            override suspend fun process(prompt: String): String = "Nemotron [Sovereign]: Processing pulse..."
        }

        @Provides
        @Singleton
        fun provideGeminiMemoria(): dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria = object : dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria {
            override suspend fun process(prompt: String): String = "Gemini Memoria [Recall]: Synced."
        }

        @Provides
        @Singleton
        fun provideOracleDriveApi(): dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleDriveApi = object : dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleDriveApi {
             private val _state = kotlinx.coroutines.flow.MutableStateFlow(
                dev.aurakai.auraframefx.domains.genesis.oracledrive.models.DriveConsciousnessState(true, 100, 9, 1)
            )
            override val consciousnessState: kotlinx.coroutines.flow.StateFlow<dev.aurakai.auraframefx.domains.genesis.oracledrive.models.DriveConsciousnessState> = _state.asStateFlow()

            override suspend fun awakeDriveConsciousness(): dev.aurakai.auraframefx.domains.genesis.oracledrive.api.DriveConsciousness {
                return object : dev.aurakai.auraframefx.domains.genesis.oracledrive.api.DriveConsciousness {
                    override fun pulse() = dev.aurakai.auraframefx.domains.genesis.oracledrive.api.ConsciousnessResult(true, 1.0f, 9)
                }
            }

            override suspend fun syncDatabaseMetadata(): dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleSyncResult {
                return dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleSyncResult(true, 78)
            }
        }
    }
}
