package dev.aurakai.auraframefx.domains.genesis.oracledrive

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.aura.AuraNetwork
import dev.aurakai.auraframefx.domains.aura.core.AuraAgent
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.CloudStorageProvider
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.CloudStorageProviderImpl
import dev.aurakai.auraframefx.domains.genesis.oracledrive.security.CryptographyManager
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.GenesisSecureFileService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveServiceImpl
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.SecureFileService
import dev.aurakai.auraframefx.domains.genesis.storage.SecureStorage
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.api.OracleDriveApi

@Module
@InstallIn(SingletonComponent::class)
object OracleDriveModule {

    @Provides
    @Singleton
    fun provideSecureFileService(
        impl: GenesisSecureFileService,
    ): SecureFileService = impl

    @Provides
    @Singleton
    fun provideOracleDriveService(
        impl: OracleDriveServiceImpl,
    ): OracleDriveService = impl

    @Provides
    @Singleton
    fun provideCloudStorageProvider(
        impl: CloudStorageProviderImpl,
    ): CloudStorageProvider = impl

    @Provides
    @Singleton
    fun provideGenesisCryptographyManager(
        @ApplicationContext context: Context,
    ): CryptographyManager {
        return CryptographyManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context,
        cryptoManager: CryptographyManager,
    ): SecureStorage {
        return SecureStorage.getInstance(context, cryptoManager)
    }

    @Provides
    @Singleton
    fun provideOracleDriveApi(
        @AuraNetwork client: OkHttpClient,
        securityContext: SecurityContext,
    ): OracleDriveApi {
        return Retrofit.Builder()
            .baseUrl("https://api.placeholder.dev/oracle/drive/") // Placeholder URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OracleDriveApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOracleDriveServiceImpl(
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

