package dev.aurakai.auraframefx.domains.aura.ui.theme.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.ThemeService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {

    @Provides
    @Singleton
    fun provideThemeService(): ThemeService {
        return ThemeService()
    }
}

