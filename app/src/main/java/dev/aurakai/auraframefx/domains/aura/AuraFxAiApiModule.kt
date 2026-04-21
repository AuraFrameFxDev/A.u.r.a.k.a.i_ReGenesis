package dev.aurakai.auraframefx.domains.aura

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.network.apis.AIContentApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing dependencies related to the AuraFrameFx AI API.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuraFxAiApiModule {

    /**
     * Supplies a singleton Json serializer configured for flexible and robust API data processing.
     *
     * The serializer is set to ignore unknown keys, coerce input values, allow lenient parsing, and encode default values to ensure resilient serialization and deserialization of API responses.
     *
     * @return A configured Json instance for handling API data.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Supplies a singleton AIContentApi instance configured for communication with the AuraFrameFx AI API.
     * Uses the shared OkHttpClient from NetworkModule to avoid duplicate bindings.
     *
     * @param okHttpClient The HTTP client used for network requests to the AuraFrameFx API.
     * @return An AIContentApi instance initialized with the AuraFrameFx API base URL and the provided HTTP client.
     */
    @Provides
    @Singleton
    fun provideAiContentApi(@Named("BasicOkHttpClient") okHttpClient: OkHttpClient): AIContentApi {

        val baseUrl = "https://api.auraframefx.com/v1"

        return AIContentApi(basePath = baseUrl, client = okHttpClient)
    }

    /**
     * Returns a singleton instance of AuraFxContentApiClient configured with the provided AIContentApi.
     *
     * @param aiContentApi The API interface used for communication with AuraFrameFx AI endpoints.
     * @return A singleton AuraFxContentApiClient for accessing AuraFrameFx AI API features.
     */
    @Provides
    @Singleton
    fun provideAuraFxContentApiClient(aiContentApi: AIContentApi): AuraFxContentApiClient {
        return AuraFxContentApiClient(aiContentApi)
    }
}
