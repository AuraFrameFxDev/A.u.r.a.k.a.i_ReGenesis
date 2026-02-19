package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.AppStateManager
import javax.inject.Named
import javax.inject.Singleton

private val Context.appStateDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_state_settings")

/**
 * Hilt Module for providing application state related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppStateModule {

    /**
     * Provides a DataStore instance for app state.
     * @param context Application context.
     * @return A DataStore instance.
     */
    @Provides
    @Singleton
    @Named("AppStateDataStore")
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.appStateDataStore
    }

    /**
     * Provides an AppStateManager.
     */
    @Provides
    @Singleton
    fun provideAppStateManager(@Named("AppStateDataStore") dataStore: DataStore<Preferences>): AppStateManager {
        return AppStateManager()
    }
}
