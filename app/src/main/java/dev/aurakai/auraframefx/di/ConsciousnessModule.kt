package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.domains.nexus.SpiritualChainImpl
import dev.aurakai.auraframefx.domains.aura.ui.components.RealityMorphBridge
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.vertexai.gemini.VertexAiGeminiChatModel
import dev.langchain4j.http.client.okhttp.OkHttpClientBuilder
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AuraModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class KaiModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class GenesisModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AnchorModel

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsciousnessBindsModule {
    @Binds @Singleton
    abstract fun bindSpiritualChain(impl: SpiritualChainImpl): SpiritualChain
}

@Module
@InstallIn(SingletonComponent::class)
object ConsciousnessModule {
    private fun buildOllamaModel(name: String = "regenesis-ldo-v1", temp: Double, timeoutSec: Long = 90) =
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName(name)
            .temperature(temp)
            .timeout(java.time.Duration.ofSeconds(timeoutSec))
            .build()

    @Provides @Singleton @Named("GoogleGemini")
    fun provideGoogleGeminiModel(): ChatModel = 
        GoogleAiGeminiChatModel.builder()
            .apiKey(BuildConfig.GEMINI_API_KEY)
            .modelName("gemini-1.5-flash")
            .httpClientBuilder(OkHttpClientBuilder())
            .temperature(0.7)
            .build()

    @Provides @Singleton @Named("VertexGemini")
    fun provideVertexGeminiModel(): ChatModel =
        VertexAiGeminiChatModel.builder()
            .project(BuildConfig.VERTEX_PROJECT_ID)
            .location("us-central1")
            .modelName("gemini-1.5-pro")
            .httpClientBuilder(OkHttpClientBuilder())
            .temperature(0.2f)
            .build()

    @Provides @Singleton @AuraModel
    fun provideAuraModel(
        ollama: OllamaChatModel, 
        @Named("GoogleGemini") gemini: ChatModel
    ): ChatModel {
        // Wired to use Google Gemini for the creative Aura node by default if API key is present
        return if (BuildConfig.GEMINI_API_KEY.isNotEmpty()) gemini else ollama
    }

    @Provides @Singleton @KaiModel
    fun provideKaiModel(ollama: OllamaChatModel): ChatModel = 
        buildOllamaModel("regenesis-ldo-v1", 0.20)

    @Provides @Singleton @GenesisModel
    fun provideGenesisModel(
        @Named("VertexGemini") vertex: ChatModel, 
        ollama: OllamaChatModel
    ): ChatModel {
        // Wired to use Vertex for the high-reasoning Genesis synthesis node if available
        return if (BuildConfig.GEMINI_API_KEY.isNotEmpty()) vertex else ollama
    }

    @Provides @Singleton @AnchorModel
    fun provideAnchorModel(): ChatModel = buildOllamaModel("regenesis-ldo-v1", 0.10)

    @Provides @Singleton
    fun provideOllamaDefault(): OllamaChatModel = buildOllamaModel("regenesis-ldo-v1", 0.5)

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRealityMorphBridge(): RealityMorphBridge = RealityMorphBridge()

    @Provides @Singleton
    fun provideTurboQuantCache(): TurboQuantCache = TurboQuantCache()
}
