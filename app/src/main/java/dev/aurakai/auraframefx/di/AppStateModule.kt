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
 * TODO: Reported as unused declaration. Ensure Hilt is set up and this module is processed.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppStateModule {

    /**
     * Provides a DataStore instance, potentially for app state.
     * Note: DataStoreModule also provides a DataStore. If these are for different DataStores,
     * consider using @Named qualifiers or distinct return types.
     * For now, assuming this might be a duplicate or for a specific named DataStore.
     * @param _context Application context. Parameter reported as unused.
     * @return A DataStore instance (using Any as placeholder).
     * TODO: Reported as unused. Implement to provide an actual DataStore for app state.
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
