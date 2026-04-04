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
import javax.inject.Singleton

/**
 * Application-level DI bindings for the Kai domain.
 *
 * Focuses on abstract @Binds methods that bind concrete implementations to interfaces.
 * All @Provides methods for configuration and singletons have been moved to
 * CoreGenesisProvidesModule to maintain clear separation of concerns.
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

    @Binds
    @Singleton
    abstract fun bindErrorHandler(
        impl: DefaultErrorHandler
    ): ErrorHandler

    companion object {
        // Legacy shim providers - kept here as they are specific to Kai domain
        @Provides
        @Singleton
        fun provideLegacyTaskScheduler(): Any = Any()

        @Provides
        @Singleton
        fun provideShizukuManager(): dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager =
            dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
    }
}

