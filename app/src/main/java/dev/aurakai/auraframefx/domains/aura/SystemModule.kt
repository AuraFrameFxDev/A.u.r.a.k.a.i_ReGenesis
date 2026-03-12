package dev.aurakai.auraframefx.domains.aura

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemModule {

    @Binds
    @Singleton
    abstract fun bindSystemOverlayManager(impl: RootOverlayManager): SystemOverlayManager
}

