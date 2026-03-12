package dev.aurakai.auraframefx.domains.kai

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-level DI bindings to resolve missing Hilt bindings reported during annotation processing.
 * Keep this module conservative: only bind concrete implementations that exist in the codebase.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    companion object {
        // Provide Legacy TaskScheduler if some modules still expect it; keep as lightweight shim
        // Note: replace or remove when all modules migrated.
        @Provides
        @Singleton
        fun provideLegacyTaskScheduler(): Any = Any()
    }
}

