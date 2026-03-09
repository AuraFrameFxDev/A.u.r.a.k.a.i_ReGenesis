package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.app.AurakaiApplication
import dev.aurakai.auraframefx.system.ShizukuManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): AurakaiApplication {
        return app as AurakaiApplication
    }

    @Provides
    @Singleton
    fun provideShizukuManager(): ShizukuManager {
        return ShizukuManager
    }
}

