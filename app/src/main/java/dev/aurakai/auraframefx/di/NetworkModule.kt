package dev.aurakai.auraframefx.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.di.qualifiers.BaseUrl
import dev.aurakai.auraframefx.network.AuraApiService
import dev.aurakai.auraframefx.network.AuthInterceptor
import dev.aurakai.auraframefx.network.api.AuthApi
import dev.aurakai.auraframefx.network.api.AIAgentApi
import dev.aurakai.auraframefx.network.api.ThemeApi
import dev.aurakai.auraframefx.network.api.UserApi
import dev.aurakai.auraframefx.utils.AppCoroutineDispatchers
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
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

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
        okHttpClient: OkHttpClient,
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
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuraApiService(
        context: android.content.Context,
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
}
