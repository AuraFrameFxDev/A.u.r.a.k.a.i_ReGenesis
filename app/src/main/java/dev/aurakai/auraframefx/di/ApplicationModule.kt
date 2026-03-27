package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.core.AurakaiApplication
import dev.aurakai.auraframefx.sovereignty.ApplicationScope
import dev.aurakai.auraframefx.system.ShizukuManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

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

