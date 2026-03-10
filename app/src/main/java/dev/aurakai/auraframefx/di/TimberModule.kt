package dev.aurakai.auraframefx.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.core.initialization.TimberInitializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimberModule {

    @Provides
    @Singleton
    fun provideTimberInitializer(app: Application): TimberInitializer {
        val initializer = TimberInitializer()
        init(app)
        return initializer
    }
}

private fun init(app: Application) {
    TODO("Not yet implemented")
}
