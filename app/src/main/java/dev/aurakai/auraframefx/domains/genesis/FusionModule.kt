package dev.aurakai.auraframefx.domains.genesis

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FusionModule {
    // InterfaceForge and ChronoSculptor auto-injectable via @Inject constructors
}
