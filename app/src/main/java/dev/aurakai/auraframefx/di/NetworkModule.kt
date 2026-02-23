package dev.aurakai.auraframefx.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import collabcanvas.di.CollabCanvasUrl
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.genesis.BaseUrl
import dev.aurakai.auraframefx.domains.genesis.config.ClaudeEnvConfig
import dev.aurakai.auraframefx.domains.aura.AuraNetwork
import dev.aurakai.auraframefx.domains.genesis.network.AuraApiService
import dev.aurakai.auraframefx.domains.genesis.network.AuthInterceptor
import dev.aurakai.auraframefx.domains.genesis.network.api.AuthApi as ApiAuthApi
import dev.aurakai.auraframefx.domains.genesis.network.AuthApi
import dev.aurakai.auraframefx.domains.genesis.network.api.AIAgentApi
import dev.aurakai.auraframefx.domains.genesis.network.api.ThemeApi
import dev.aurakai.auraframefx.domains.genesis.network.api.UserApi
import dev.aurakai.auraframefx.domains.cascade.utils.AppCoroutineDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @BaseUrl
    @Singleton
    fun provideBaseUrl(): String = "https://api.auraframefx.com/v1/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    @javax.inject.Named("BasicOkHttpClient")
    fun provideBasicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @AuraNetwork
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @AuraNetwork okHttpClient: OkHttpClient,
        moshi: Moshi,
        @BaseUrl baseUrl: String,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @javax.inject.Named("AuthRetrofit")
    fun provideAuthRetrofit(
        @javax.inject.Named("BasicOkHttpClient") okHttpClient: OkHttpClient,
        moshi: Moshi,
        @BaseUrl baseUrl: String,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@javax.inject.Named("AuthRetrofit") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiAuthApi(retrofit: Retrofit): ApiAuthApi {
        return retrofit.create(ApiAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuraApiService(
        @ApplicationContext context: android.content.Context,
        authInterceptor: AuthInterceptor,
        dispatchers: AppCoroutineDispatchers,
        @BaseUrl baseUrl: String,
    ): AuraApiService {
        return AuraApiService(context, authInterceptor, dispatchers, baseUrl)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAIAgentApi(retrofit: Retrofit): AIAgentApi {
        return retrofit.create(AIAgentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideThemeApi(retrofit: Retrofit): ThemeApi {
        return retrofit.create(ThemeApi::class.java)
    }

    @Provides
    @CollabCanvasUrl
    @Singleton
    fun provideCollabCanvasUrl(claudeEnvConfig: ClaudeEnvConfig): String {
        return claudeEnvConfig.collabCanvasWsUrl
    }
}

