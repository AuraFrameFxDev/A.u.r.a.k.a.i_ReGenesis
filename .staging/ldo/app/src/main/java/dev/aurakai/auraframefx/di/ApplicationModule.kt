package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.aurakaiapplication.AurakaiApplication
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    
    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): AurakaiApplication {
        return app as AurakaiApplication
    }
}
