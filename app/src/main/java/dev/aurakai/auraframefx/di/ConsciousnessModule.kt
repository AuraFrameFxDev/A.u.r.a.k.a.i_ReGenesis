package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.ai.GenesisConsciousnessMatrix
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBusImpl
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.domains.nexus.SpiritualChainImpl
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsciousnessModule {

    @Binds
    @Singleton
    abstract fun bindSpiritualChain(impl: SpiritualChainImpl): SpiritualChain

    @Binds
    @Singleton
    abstract fun bindKaiSentinelBus(impl: KaiSentinelBusImpl): KaiSentinelBus

    companion object {
        @Provides
        @Singleton
        fun provideCasberryParticleSwarm(): CasberryParticleSwarm = CasberryParticleSwarm()
    }
}
