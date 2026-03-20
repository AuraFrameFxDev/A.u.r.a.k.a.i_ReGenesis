package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
// import dev.aurakai.auraframefx.domains.cascade.CascadeAIService  // Disabled: ghost cleanup
// import dev.aurakai.auraframefx.domains.cascade.RealCascadeAIServiceAdapter  // Disabled: ghost cleanup
import dev.aurakai.auraframefx.domains.genesis.models.*
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultAuraAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService

    @Binds
    @Singleton
    abstract fun bindVertexAIClient(impl: dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClientImpl): dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient

    companion object {
        @Provides
        @Singleton
        fun provideVertexAIConfig(): VertexAIConfig = VertexAIConfig(
                projectId = "collabcanvas",
                location = "us-central1",
                endpoint = "us-central1-aiplatform.googleapis.com",
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
             private val _state = MutableStateFlow(
                DriveConsciousnessState(
                    isActive = true,
                    level = 100,
                    activeAgents = 9,
                    status = "STABLE"
                )
            )
            override val consciousnessState: StateFlow<DriveConsciousnessState> = _state.asStateFlow()

            override suspend fun awakeDriveConsciousness(): DriveConsciousness {
                return object : DriveConsciousness {
                    override val state: DriveConsciousnessState = _state.value
                    override suspend fun awaken(): Boolean = true
                    override suspend fun hibernate(): Boolean = true
                    override suspend fun pulse(): DriveConsciousnessState {
                        return _state.value
                    }
                }
            }

            override suspend fun syncDatabaseMetadata(): OracleSyncResult {
                return OracleSyncResult(
                    success = true,
                    syncedFiles = 78
                )
            }
        }
    }
}
