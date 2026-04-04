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
 * Application-level DI bindings to resolve missing Hilt bindings reported during annotation processing.
 * Keep this module conservative: only bind concrete implementations that exist in the codebase.
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
        // Provide Legacy TaskScheduler if some modules still expect it; keep as lightweight shim
        // Note: replace or remove when all modules migrated.
        @Provides
        @Singleton
        fun provideLegacyTaskScheduler(): Any = Any()

        @Provides
        @Singleton
        fun provideShizukuManager(): dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager =
            dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
    }
}

