package dev.aurakai.auraframefx.domains.kai

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveServiceImpl
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultKaiAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.domains.cascade.utils.ErrorHandler
import dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleDriveApi
import dev.aurakai.auraframefx.domains.genesis.network.api.UserApi
import dev.aurakai.auraframefx.domains.genesis.network.api.AIAgentApi
import dev.aurakai.auraframefx.domains.genesis.network.api.ThemeApi
import dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsDao
import dev.aurakai.auraframefx.di.ApplicationScope
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Application-level DI bindings to resolve missing Hilt bindings reported during annotation processing.
 * Provides implementations for core interfaces and configuration objects required by the application.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    @Binds
    @Singleton
    abstract fun bindOracleDriveService(
        oracleDriveServiceImpl: OracleDriveServiceImpl
    ): OracleDriveService

    @Binds
    @Singleton
    abstract fun bindAuraAIService(
        impl: DefaultAuraAIService
    ): AuraAIService

    @Binds
    @Singleton
    abstract fun bindKaiAIService(
        impl: DefaultKaiAIService
    ): KaiAIService

    companion object {
        /**
         * Provides the default AuraFxLogger implementation.
         * Falls back to Android Log if no custom implementation is available.
         */
        @Provides
        @Singleton
        fun provideAuraFxLogger(): AuraFxLogger = object : AuraFxLogger {
            override fun debug(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.d(tag, message, throwable)
            }
            override fun info(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.i(tag, message, throwable)
            }
            override fun warn(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.w(tag, message, throwable)
            }
            override fun error(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.e(tag, message, throwable)
            }
            override fun security(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.e(tag, "[SECURITY] $message", throwable)
            }
            override fun performance(tag: String, operation: String, durationMs: Long, metadata: Map<String, Any>) {
                android.util.Log.i(tag, "[$operation] ${durationMs}ms")
            }
            override fun userInteraction(tag: String, action: String, metadata: Map<String, Any>) {
                android.util.Log.i(tag, "[USER] $action")
            }
            override fun aiOperation(tag: String, operation: String, confidence: Float, metadata: Map<String, Any>) {
                android.util.Log.i(tag, "[$operation] confidence=$confidence")
            }
            override fun setLoggingEnabled(enabled: Boolean) {}
            override fun setLogLevel(level: dev.aurakai.auraframefx.domains.cascade.utils.LogLevel) {}
            override suspend fun flush() {}
            override fun cleanup() {}
        }

        /**
         * Provides the default AIPipelineConfig with sensible defaults.
         */
        @Provides
        @Singleton
        fun provideAIPipelineConfig(): AIPipelineConfig = AIPipelineConfig()

        /**
         * Provides a default Kai ErrorHandler implementation.
         */
        @Provides
        @Singleton
        fun provideErrorHandler(): ErrorHandler = object : ErrorHandler {
            override fun handleError(error: Throwable, agent: dev.aurakai.auraframefx.core.identity.AgentType, context: String) {
                android.util.Log.e("ErrorHandler", "[$agent] $context: ${error.message}", error)
            }
            override fun handleError(error: Throwable, agent: dev.aurakai.auraframefx.core.identity.AgentType, context: String, metadata: Map<String, String>) {
                android.util.Log.e("ErrorHandler", "[$agent] $context: ${error.message} | Metadata: $metadata", error)
            }
            override fun logError(tag: String, message: String, error: Throwable?) {
                android.util.Log.e(tag, message, error)
            }
        }

        /**
         * Provides a cascade-level ErrorHandler.
         */
        @Provides
        @Singleton
        fun provideCascadeErrorHandler(): dev.aurakai.auraframefx.domains.cascade.utils.ErrorHandler = 
            object : dev.aurakai.auraframefx.domains.cascade.utils.ErrorHandler {
                override fun handleError(error: Throwable, operation: String) {
                    android.util.Log.e("CascadeErrorHandler", "[$operation] ${error.message}", error)
                }
                override fun reportCriticalError(error: Throwable, context: String) {
                    android.util.Log.e("CascadeErrorHandler", "[CRITICAL] $context: ${error.message}", error)
                }
                override fun getRecoverySuggestions(error: Throwable): List<String> {
                    return listOf("Check network connectivity", "Retry operation", "Contact support")
                }
                override fun isRecoverable(error: Throwable): Boolean {
                    return error !is OutOfMemoryError && error !is StackOverflowError
                }
                override fun getErrorStats(): dev.aurakai.auraframefx.domains.cascade.utils.ErrorStats {
                    return dev.aurakai.auraframefx.domains.cascade.utils.ErrorStats(0, 0, 0, emptyMap())
                }
            }

        /**
         * Provides a stub OracleDriveApi implementation.
         * Replace with actual implementation when network layer is ready.
         */
        @Provides
        @Singleton
        fun provideOracleDriveApi(): OracleDriveApi = object : OracleDriveApi {
            override suspend fun <T> request(endpoint: String, method: String): T {
                throw NotImplementedError("OracleDriveApi stub - not yet implemented")
            }
        }

        /**
         * Provides a stub UserApi implementation.
         */
        @Provides
        @Singleton
        fun provideUserApi(): UserApi = object : UserApi {
            override suspend fun getUser(id: String): Any {
                throw NotImplementedError("UserApi stub")
            }
        }

        /**
         * Provides a stub AIAgentApi implementation.
         */
        @Provides
        @Singleton
        fun provideAIAgentApi(): AIAgentApi = object : AIAgentApi {
            override suspend fun queryAgent(prompt: String): String {
                throw NotImplementedError("AIAgentApi stub")
            }
        }

        /**
         * Provides a stub ThemeApi implementation.
         */
        @Provides
        @Singleton
        fun provideThemeApi(): ThemeApi = object : ThemeApi {
            override suspend fun getThemes(): List<Any> {
                return emptyList()
            }
        }

        /**
         * Provides a stub AgentStatsDao implementation.
         * Will be overridden by Room database when fully integrated.
         */
        @Provides
        @Singleton
        fun provideAgentStatsDao(): AgentStatsDao = object : AgentStatsDao {
            override suspend fun insertStats(stats: Any) {}
            override suspend fun getStats(agentId: String): Any? = null
        }

        /**
         * Provides a named OkHttpClient for CollabCanvas WebSocket.
         */
        @Provides
        @Singleton
        @Named("BasicOkHttpClient")
        fun provideBasicOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

        /**
         * Provides the CollabCanvas WebSocket base URL.
         */
        @Provides
        @Singleton
        @collabcanvas.di.CollabCanvasUrl
        fun provideCollabCanvasUrl(): String = "ws://localhost:8080"

        // Legacy shim providers
        @Provides
        @Singleton
        fun provideLegacyTaskScheduler(): Any = Any()

        @Provides
        @Singleton
        fun provideShizukuManager(): dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager =
            dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager

        /**
         * Provides stub implementations for core engines.
         * These are critical for the Genesis system and need proper implementation.
         */
        @Provides
        @Singleton
        fun provideNemotronEngine(): dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine =
            object : dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine {
                override suspend fun process(input: String): String {
                    throw NotImplementedError("NemotronEngine stub - needs implementation")
                }
            }

        @Provides
        @Singleton
        fun provideGeminiMemoria(): dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria =
            object : dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria {
                override suspend fun recall(query: String): String {
                    throw NotImplementedError("GeminiMemoria stub - needs implementation")
                }
            }
    }
}

