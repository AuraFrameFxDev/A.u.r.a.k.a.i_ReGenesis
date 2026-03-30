package dev.aurakai.auraframefx.domains.genesis

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.BridgeMemorySink
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.GenesisBridge
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.HttpGenesisBridge
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.NexusMemoryBridgeSink
import dev.aurakai.auraframefx.domains.genesis.network.api.GenesisBackendApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GenesisBridgeNetwork

@Module
@InstallIn(SingletonComponent::class)
object BridgeModule {

    @Provides
    @Singleton
    fun provideBridgeMemorySink(impl: NexusMemoryBridgeSink): BridgeMemorySink = impl

    @Provides
    @Singleton
    @GenesisBridgeNetwork
    fun provideGenesisBridgeOkHttp(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGenesisBackendApi(
        @GenesisBridgeNetwork client: OkHttpClient,
        gson: Gson
    ): GenesisBackendApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GENESIS_BACKEND_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GenesisBackendApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGenesisBridge(impl: HttpGenesisBridge): GenesisBridge = impl
}
