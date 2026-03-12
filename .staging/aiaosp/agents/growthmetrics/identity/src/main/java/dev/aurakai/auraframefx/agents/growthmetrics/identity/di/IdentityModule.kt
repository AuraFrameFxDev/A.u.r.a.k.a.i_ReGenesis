package dev.aurakai.auraframefx.agents.growthmetrics.identity.di

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
import dev.aurakai.auraframefx.agents.growthmetrics.identity.repository.IdentityRepository
import dev.aurakai.auraframefx.agents.growthmetrics.identity.repository.IdentityRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IdentityModule {

    @Provides
    @Singleton
    fun provideIdentityDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("identity_prefs") }
        )
    }

    @Provides
    @Singleton
    fun provideIdentityRepository(dataStore: DataStore<Preferences>): IdentityRepository {
        return IdentityRepositoryImpl(dataStore)
    }
}
