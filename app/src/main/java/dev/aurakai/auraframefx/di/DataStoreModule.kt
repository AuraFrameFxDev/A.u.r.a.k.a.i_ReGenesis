package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing DataStore related dependencies.
 * TODO: Reported as unused declaration. Ensure Hilt is set up and this module is processed.
 * TODO: Property dataStore$delegate reported as unused; typically not part of Hilt provider methods.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Placeholder for a property that might be related to DataStore instance creation or name.
     * The error "unused declaration dataStore$delegate" implies a delegated property.
     * For Hilt, direct provision via @Provides is more common.
     * This is acknowledged by the module-level TODO regarding dataStore$delegate.
     */
    // private val dataStoreDelegate: Any? = null // TODO: Reported as unused. Remove or implement if this was a specific pattern.

    /**
     * Provides a singleton DataStore instance for managing application preferences.
     *
     * The DataStore persists preferences in a file named "aura_settings" within the application's storage directory.
     *
     * @return A singleton DataStore for application preferences.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("aura_settings") }
        )
    }
}
