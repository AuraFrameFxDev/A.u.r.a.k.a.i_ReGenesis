package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.preferences.UserPreferences
import javax.inject.Singleton

/**
 * Hilt Module for providing UserPreferences.
 * TODO: Reported as unused declaration. Ensure Hilt is set up and this module is processed.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    /**
     * Provides UserPreferences.
     * @param _context Application context. Parameter reported as unused.
     * @return A UserPreferences instance.
     * TODO: Reported as unused. Implement if UserPreferences is used.
     */
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext _context: Context): UserPreferences {
        // Minimal working placeholder
        return UserPreferences(_context)
    }
}
