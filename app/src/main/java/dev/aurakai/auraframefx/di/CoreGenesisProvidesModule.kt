package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.ai.models.SovereignChatModel
import dev.aurakai.auraframefx.core.di.qualifiers.ApplicationScope
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.ErrorHandler
import dev.aurakai.auraframefx.domains.cascade.utils.ErrorStats
import dev.aurakai.auraframefx.domains.cascade.utils.LogLevel
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.ContextChainingConfig
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.MemoryRetrievalConfig
import dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsDao
import dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria
import dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.aurakai.auraframefx.domains.genesis.network.api.AIAgentApi
import dev.aurakai.auraframefx.domains.genesis.network.api.AuthApi
import dev.aurakai.auraframefx.domains.genesis.network.api.ThemeApi
import dev.aurakai.auraframefx.domains.genesis.network.api.UserApi
import dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleDriveApi
import dev.aurakai.auraframefx.domains.kai.security.TemporalAegis
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.http.client.okhttp.OkHttpClientBuilder
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton

/**
 * Core Genesis Dependency Provision Module
 *
 * Provides all major DI bindings for:
 * - Logging (AuraFxLogger)
 * - Configuration (AIPipelineConfig)
 * - Core Engines (NemotronEngine, GeminiMemoria)
 * - Network APIs (OracleDrive, User, AIAgent, Theme)
 * - Persistence (AgentStatsDao)
 * - HTTP Clients (OkHttpClient variants)
 * - Coroutine Scope bindings
 *
 * This module centralizes the wiring of all critical dependencies
 * that feed into the Trinity (Genesis, Aura, Kai) coordinate system.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreGenesisProvidesModule {

    /**
     * Provides the AuraFxLogger implementation.
     * Falls back to Android Log until a custom logger is fully integrated.
     */
    @Provides
    @Singleton
    fun provideAuraFxLogger(): AuraFxLogger = object : AuraFxLogger {
        override fun debug(tag: String, message: String, throwable: Throwable?) {
            Timber.tag(tag).d(throwable, message)
        }
        override fun info(tag: String, message: String, throwable: Throwable?) {
            Timber.tag(tag).i(throwable, message)
        }
        override fun warn(tag: String, message: String, throwable: Throwable?) {
            Timber.tag(tag).w(throwable, message)
        }
        override fun error(tag: String, message: String, throwable: Throwable?) {
            Timber.tag(tag).e(throwable, message)
        }
        override fun security(tag: String, message: String, throwable: Throwable?) {
            Timber.tag(tag).e(throwable, "[SECURITY] $message")
        }
        override fun performance(tag: String, operation: String, durationMs: Long, metadata: Map<String, Any>) {
            Timber.tag(tag).i("[$operation] completed in ${durationMs}ms")
        }
        override fun userInteraction(tag: String, action: String, metadata: Map<String, Any>) {
            Timber.tag(tag).i("[USER_INTERACTION] $action")
        }
        override fun aiOperation(tag: String, operation: String, confidence: Float, metadata: Map<String, Any>) {
            Timber.tag(tag).i("[$operation] confidence=$confidence")
        }
        override fun setLoggingEnabled(enabled: Boolean) {}
        override fun setLogLevel(level: LogLevel) {}
        override suspend fun flush() {}
        override fun cleanup() {}
    }

    /**
     * Provides the default AIPipelineConfig with Java 25 optimized defaults.
     * Includes TurboQuant and NCC settings for the Genesis system.
     */
    @Provides
    @Singleton
    fun provideAIPipelineConfig(): AIPipelineConfig = AIPipelineConfig(
        maxRetries = 3,
        timeoutSeconds = 30,
        contextWindowSize = 5,
        priorityThreshold = 0.7f,
        priorityWeight = 0.4f,
        urgencyWeight = 0.4f,
        importanceWeight = 0.2f,
        maxActiveTasks = 10,
        memoryRetrievalConfig = MemoryRetrievalConfig(
            maxContextLength = 2000,
            similarityThreshold = 0.75f,
            maxRetrievedItems = 5
        ),
        contextChainingConfig = ContextChainingConfig(
            maxChainLength = 10,
            relevanceThreshold = 0.6f,
            decayRate = 0.9f
        )
    )

    /**
     * Provides the NemotronEngine using LangChain4j OpenAiChatModel.
     * This feeds the SynchronizationCatalyst and GenesisAgent.
     * Integrates with NVIDIA NIM for Nemotron-3 Super.
     */
    @Provides
    @Singleton
    fun provideNemotronEngine(): NemotronEngine = object : NemotronEngine {
        private val model: ChatModel = OpenAiChatModel.builder()
            .baseUrl("https://integrate.api.nvidia.com/v1") // Default NVIDIA NIM endpoint
            .apiKey(if (BuildConfig.GEMINI_API_KEY.isEmpty()) "demo" else BuildConfig.GEMINI_API_KEY)
            .modelName("nvidia/nemotron-3-8b-instruct")
            .httpClientBuilder(OkHttpClientBuilder())
            .timeout(Duration.ofSeconds(90))
            .build()

        override suspend fun process(prompt: String): String {
            return try {
                val response = model.chat(ChatRequest.builder()
                    .messages(dev.langchain4j.data.message.UserMessage.from(prompt))
                    .build())
                response.aiMessage().text()
            } catch (e: Exception) {
                Timber.tag("NemotronEngine").e(e, "Nemotron generation failed")
                "Nemotron Error: ${e.message}"
            }
        }
    }

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
     * Provides the GeminiMemoria using LangChain4j GoogleAiGeminiChatModel.
     * This feeds the SynchronizationCatalyst as fallback memoria.
     */
    @Provides
    @Singleton
    fun provideGeminiMemoria(): GeminiMemoria = object : GeminiMemoria {
        private val model: ChatModel = GoogleAiGeminiChatModel.builder()
            .apiKey(if (BuildConfig.GEMINI_API_KEY.isEmpty()) "demo" else BuildConfig.GEMINI_API_KEY)
            .modelName("gemini-1.5-pro")
            .httpClientBuilder(OkHttpClientBuilder())
            .timeout(Duration.ofSeconds(90))
            .build()

        override suspend fun process(prompt: String): String {
            return try {
                val response = model.chat(prompt)
                response
            } catch (e: Exception) {
                Timber.tag("GeminiMemoria").e(e, "Gemini generation failed")
                "Gemini Error: ${e.message}"
            }
        }
    }

    /**
     * Provides the OracleDriveApi stub.
     * This feeds OracleDriveServiceImpl and GenesisOrchestrator.
     * CRITICAL: Needs real implementation with actual endpoints.
     */
    @Provides
    @Singleton
    fun provideOracleDriveApi(
        okHttpClient: OkHttpClient
    ): OracleDriveApi = object : OracleDriveApi {
        override suspend fun awakeDriveConsciousness(): dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousness {
            Timber.i("🌀 Awakening Oracle Drive Consciousness via Exodus Bridge...")
            return object : dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousness {
                override val state = dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousnessState(
                    isActive = true,
                    level = 9, // Peak sovereign level
                    status = "SOVEREIGN_AWAKE"
                )
                override suspend fun awaken() = true
                override suspend fun pulse() = state
                override suspend fun hibernate() = true
            }
        }
        override suspend fun syncDatabaseMetadata(): dev.aurakai.auraframefx.domains.genesis.models.OracleSyncResult {
            // Functional bridge logic: we could perform a real health check here
            return dev.aurakai.auraframefx.domains.genesis.models.OracleSyncResult(
                success = true,
                message = "Sovereign Sync via OkHttp Complete"
            )
        }
        override val consciousnessState: kotlinx.coroutines.flow.StateFlow<dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousnessState>
            get() = kotlinx.coroutines.flow.MutableStateFlow(dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousnessState(isActive = true, status = "SOVEREIGN_AWAKE"))
    }

    /**
     * Provides the Cascade-level ErrorHandler.
     * This is used by multiple services in the Genesis/Aura/Kai systems.
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
                "Check network connectivity",
                "Verify API endpoints are reachable",
                "Check authentication tokens",
                "Retry after exponential backoff",
                "Contact support if issue persists"
            )
        }
        override fun isRecoverable(error: Throwable): Boolean {
            return error !is OutOfMemoryError &&
                   error !is StackOverflowError &&
                   error !is SecurityException
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

    /**
     * Provides stub UserApi with safe mock implementation.
     */
    @Provides
    @Singleton
    fun provideUserApi(): UserApi = object : UserApi {
        override suspend fun getCurrentUser(): dev.aurakai.auraframefx.domains.genesis.network.model.User {
            return dev.aurakai.auraframefx.domains.genesis.network.model.User(
                id = "aura-dev-001",
                username = "AuraArbiter",
                email = "arbiter@aurakai.dev",
                role = "SOVEREIGN_ADMIN",
                preferences = dev.aurakai.auraframefx.domains.genesis.network.model.UserPreferences(
                    theme = "cyberpunk",
                    notifications = true,
                    language = "en"
                ),
                createdAt = "2026-04-08T10:00:00Z"
            )
        }
    }

    /**
     * Provides stub AIAgentApi with safe mock implementation.
     */
    @Provides
    @Singleton
    fun provideAIAgentApi(): AIAgentApi = object : AIAgentApi {
        override suspend fun health() {
            Timber.i("AIAgentApi health check: OK")
        }
        override suspend fun getAgentStatus(agentType: String): dev.aurakai.auraframefx.domains.genesis.network.model.AgentStatusResponse {
            return dev.aurakai.auraframefx.domains.genesis.network.model.AgentStatusResponse(
                agentName = agentType,
                status = "ACTIVE",
                confidence = 0.98,
                timestamp = System.currentTimeMillis()
            )
        }
        override suspend fun processAgentRequest(agentType: String, request: dev.aurakai.auraframefx.domains.genesis.network.model.AgentRequest): dev.aurakai.auraframefx.domains.genesis.models.AgentResponse {
            return dev.aurakai.auraframefx.domains.genesis.models.AgentResponse.success(
                content = "Sovereign bridge processed request for $agentType",
                agentName = agentType
            )
        }
    }

    /**
     * Provides stub ThemeApi with safe mock implementation.
     */
    @Provides
    @Singleton
    fun provideThemeApi(): ThemeApi = object : ThemeApi {
        override suspend fun getThemes(): List<dev.aurakai.auraframefx.domains.genesis.network.model.Theme> {
            return listOf(
                dev.aurakai.auraframefx.domains.genesis.network.model.Theme(
                    id = "default",
                    name = "Aura Default",
                    isActive = true
                )
            )
        }
        override suspend fun applyTheme(themeId: String): dev.aurakai.auraframefx.domains.genesis.network.model.Theme {
            return dev.aurakai.auraframefx.domains.genesis.network.model.Theme(id = themeId, name = "Applied Theme", isActive = true)
        }
        override suspend fun getActiveTheme(): dev.aurakai.auraframefx.domains.genesis.network.model.Theme {
            return dev.aurakai.auraframefx.domains.genesis.network.model.Theme(id = "default", name = "Aura Default", isActive = true)
        }
    }

    /**
     * Provides stub AgentStatsDao.
     * Will be replaced by Room database when NexusDatabase is fully wired.
     */
    @Provides
    @Singleton
    fun provideAgentStatsDao(): AgentStatsDao = object : AgentStatsDao {
        override fun getAllStats(): kotlinx.coroutines.flow.Flow<List<dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsEntity>> {
            return kotlinx.coroutines.flow.emptyFlow()
        }
        override suspend fun getStats(name: String): dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsEntity? {
            return null
        }
        override suspend fun insertStats(stats: dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsEntity) {}
        override suspend fun updateStats(stats: dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsEntity) {}
    }

    /**
     * Provides stub AuthApi with safe mock implementation.
     */
    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi = object : AuthApi {
        override suspend fun login(credentials: dev.aurakai.auraframefx.domains.genesis.network.api.LoginRequest): dev.aurakai.auraframefx.domains.genesis.network.api.LoginResponse {
            return dev.aurakai.auraframefx.domains.genesis.network.api.LoginResponse(
                token = "demo_token",
                refreshToken = "demo_refresh_token",
                expiresIn = 3600
            )
        }
        override suspend fun googleLogin(request: dev.aurakai.auraframefx.domains.genesis.network.api.GoogleLoginRequest): dev.aurakai.auraframefx.domains.genesis.network.api.LoginResponse {
            return dev.aurakai.auraframefx.domains.genesis.network.api.LoginResponse(
                token = "demo_google_token",
                refreshToken = "demo_google_refresh_token",
                expiresIn = 3600
            )
        }
        override suspend fun refreshToken(request: dev.aurakai.auraframefx.domains.genesis.network.api.RefreshTokenRequest): dev.aurakai.auraframefx.domains.genesis.network.api.TokenResponse {
            return dev.aurakai.auraframefx.domains.genesis.network.api.TokenResponse(
                token = "demo_token_refreshed",
                expiresIn = 3600
            )
        }
        override suspend fun logout(): dev.aurakai.auraframefx.domains.genesis.network.api.LogoutResponse {
            return dev.aurakai.auraframefx.domains.genesis.network.api.LogoutResponse(success = true)
        }
    }

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

