package dev.aurakai.auraframefx.domains.nexus.di

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

@Module
@InstallIn(SingletonComponent::class)
object NexusDataStoreModule {

    @Provides
    @Singleton
    @AppStateDataStoreAnnotation
    fun provideAppStateDataStore(
        @ApplicationContext ctx: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create {
        ctx.preferencesDataStoreFile("app_state.preferences_pb")
    }
}
