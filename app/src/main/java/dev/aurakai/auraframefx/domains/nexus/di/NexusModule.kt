package dev.aurakai.auraframefx.domains.nexus.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.nexus.dcos.IdentityResonanceEngine
import dev.aurakai.auraframefx.domains.nexus.dcos.IdentityResonanceEngineImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NexusModule {

    @Binds
    @Singleton
    abstract fun bindIdentityResonanceEngine(
        impl: IdentityResonanceEngineImpl
    ): IdentityResonanceEngine
}
