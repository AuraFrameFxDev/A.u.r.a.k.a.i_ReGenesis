package dev.aurakai.auraframefx.di

import androidx.hilt.work.HiltWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing HiltWorkerFactory.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideHiltWorkerFactory(): HiltWorkerFactory? {
        return null // Placeholder
    }
}
