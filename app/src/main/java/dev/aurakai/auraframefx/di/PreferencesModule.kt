package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.nexus.config.UserPreferences
import javax.inject.Singleton

/**
 * Hilt Module for providing config.UserPreferences (manual constructor version).
 * The preferences.UserPreferences uses @Inject constructor and doesn't need a provider.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    /**
     * Provides config.UserPreferences for legacy code that depends on it.
     */
    @Provides
    @Singleton
    fun provideConfigUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
