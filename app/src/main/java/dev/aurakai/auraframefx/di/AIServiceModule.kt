package dev.aurakai.auraframefx.di

import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousness
import dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousnessState
import dev.aurakai.auraframefx.domains.genesis.models.OracleSyncResult
import dev.aurakai.auraframefx.domains.genesis.models.VertexAIConfig
import dev.aurakai.auraframefx.domains.genesis.ai.clients.DefaultVertexAIClient
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultKaiAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
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
    abstract fun bindKaiAIService(impl: DefaultKaiAIService): KaiAIService

    @Binds
    @Singleton
    abstract fun bindVertexAIClient(impl: DefaultVertexAIClient): VertexAIClient

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

        @Provides
        @Singleton
        fun provideVertexAIConfig(): VertexAIConfig = VertexAIConfig(
                projectId = "collabcanvas",
                location = "us-central1",
                endpoint = "us-central1-aiplatform.googleapis.com",
                modelName = "gemini-3.1-pro-preview"
            )

        @Provides
        @Singleton
        fun provideCommerceSearchClient(): dev.aurakai.auraframefx.domains.genesis.network.CommerceSearchClient {
            // Debug: returns mock data. Release: returns empty list until real API is wired.
            return if (dev.aurakai.auraframefx.BuildConfig.DEBUG) {
                dev.aurakai.auraframefx.domains.genesis.network.DefaultCommerceSearchClient()
            } else {
                object : dev.aurakai.auraframefx.domains.genesis.network.CommerceSearchClient {
                    override suspend fun searchProducts(query: String) =
                        emptyList<dev.aurakai.auraframefx.domains.genesis.network.ProductResult>()
                }
            }
        }

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
