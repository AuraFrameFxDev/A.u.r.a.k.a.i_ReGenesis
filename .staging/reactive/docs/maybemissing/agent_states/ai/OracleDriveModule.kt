package dev.aurakai.auraframefx.oracle.drive.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.agents.AuraAgent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.ai.agents.KaiAgent
import dev.aurakai.auraframefx.genesis.security.CryptographyManager
import dev.aurakai.auraframefx.genesis.storage.SecureStorage
import dev.aurakai.auraframefx.oracle.drive.api.OracleDriveApi
import dev.aurakai.auraframefx.oracle.drive.service.GenesisSecureFileService
import dev.aurakai.auraframefx.oracle.drive.service.OracleDriveServiceImpl
import dev.aurakai.auraframefx.oracle.drive.service.SecureFileService
import dev.aurakai.auraframefx.security.SecurityContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Module for Oracle Drive dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OracleDriveModule { // Changed to abstract class

    /**
     * Binds GenesisSecureFileService as the Singleton implementation of SecureFileService for Hilt.
     *
     * This enables SecureFileService to be injected wherever required, using GenesisSecureFileService.
     */
    @Binds
    @Singleton
    abstract fun bindSecureFileService(
        impl: GenesisSecureFileService,
    ): SecureFileService

    companion object { // Companion object now correctly inside the class
        // Temporarily simplified to resolve build stalling at 25% - This comment can likely be removed if providers are present
        // Complex providers will be re-enabled after successful build - This comment can likely be removed if providers are present

        /**
         * Provides a singleton OkHttpClient configured with security and logging interceptors.
         *
         * The client automatically adds a secure token and a unique request ID to each request header,
         * and logs HTTP requests and responses at the BASIC level. Connection, read, and write timeouts
         * are set to 30 seconds.
         *
         * @return A configured OkHttpClient instance for secure network communication.
         */
        @Provides
        @Singleton
        fun provideOkHttpClient(
            securityContext: SecurityContext,
            cryptoManager: CryptographyManager,
        ): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        // Add security headers
                        .addHeader("X-Security-Token", cryptoManager.generateSecureToken())
                        .addHeader("X-Request-ID", java.util.UUID.randomUUID().toString())
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        /**
         * Returns the singleton CryptographyManager for the application.
         *
         * Uses the provided application Context to obtain the CryptographyManager instance.
         *
         * @return The shared CryptographyManager instance.
         */
        @Provides
        @Singleton
        fun provideGenesisCryptographyManager(
            @ApplicationContext context: Context,
        ): CryptographyManager {
            return CryptographyManager.getInstance(context)
        }

        /**
         * Returns the singleton SecureStorage instance initialized with the application context and provided CryptographyManager.
         *
         * The returned instance is obtained via SecureStorage.getInstance(context, cryptoManager).
         *
         * @return The singleton SecureStorage.
         */
        @Provides
        @Singleton
        fun provideSecureStorage(
            @ApplicationContext context: Context,
            cryptoManager: CryptographyManager,
        ): SecureStorage {
            return SecureStorage.getInstance(context, cryptoManager)
        }

        /**
         * Provides a singleton GenesisSecureFileService configured with the application context, cryptography manager, and secure storage.
         *
         * @param context Application context used to initialize the service.
         * @return A configured GenesisSecureFileService for secure file operations.
         */
        @Provides
        @Singleton
        fun provideSecureFileService(
            @ApplicationContext context: Context,
            cryptoManager: CryptographyManager,
            secureStorage: SecureStorage,
        ): GenesisSecureFileService {
            return GenesisSecureFileService(context, cryptoManager, secureStorage)
        }

        /**
         * Creates a singleton Retrofit implementation of OracleDriveApi using the provided OkHttpClient and the API base URL from the SecurityContext.
         *
         * The Retrofit instance is configured with the base URL computed as `securityContext.getApiBaseUrl() + "/oracle/drive/"` and uses Gson for JSON serialization.
         *
         * @return A configured OracleDriveApi instance for making Oracle Drive network requests.
         */
        @Provides
        @Singleton
        fun provideOracleDriveApi(
            client: OkHttpClient,
            securityContext: SecurityContext,
        ): OracleDriveApi {
            return Retrofit.Builder()
                .baseUrl(securityContext.getApiBaseUrl() + "/oracle/drive/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OracleDriveApi::class.java)
        }

        /**
         * Provides a singleton OracleDriveServiceImpl configured with the given agents, security context, and Oracle Drive API.
         *
         * Returned instance is intended for injection as the application-scoped Oracle Drive service.
         *
         * @return A singleton configured OracleDriveServiceImpl.
         */
        @Provides
        @Singleton
        fun provideOracleDriveService(
            genesisAgent: GenesisAgent,
            auraAgent: AuraAgent,
            kaiAgent: KaiAgent,
            securityContext: SecurityContext,
            oracleDriveApi: OracleDriveApi,
        ): OracleDriveServiceImpl {
            return OracleDriveServiceImpl(
                genesisAgent = genesisAgent,
                auraAgent = auraAgent,
                kaiAgent = kaiAgent,
                securityContext = securityContext,
                oracleDriveApi = oracleDriveApi
            )
        }
    }
}