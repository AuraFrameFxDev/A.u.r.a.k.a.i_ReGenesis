package dev.aurakai.auraframefx.domains.kai

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.aura.ui.KaiController
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
    fun provideKaiController(): KaiController {
        return KaiController()
    }
}

