package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ui.KaiController
import javax.inject.Singleton

/**
 * Hilt Module for providing KaiController.
 */
@Module
@InstallIn(SingletonComponent::class)
object KaiModule {

    /**
     * Provides KaiController.
     * @return A KaiController instance.
     */
    @Provides
    @Singleton
    fun provideKaiController(): KaiController? {
        return null // Placeholder
    }
}
